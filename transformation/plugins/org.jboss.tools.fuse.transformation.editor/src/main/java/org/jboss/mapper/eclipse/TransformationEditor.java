/******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: JBoss by Red Hat - Initial implementation.
 *****************************************************************************/
package org.jboss.mapper.eclipse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLClassLoader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;
import org.jboss.mapper.CustomMapping;
import org.jboss.mapper.FieldMapping;
import org.jboss.mapper.Literal;
import org.jboss.mapper.MapperConfiguration;
import org.jboss.mapper.MappingOperation;
import org.jboss.mapper.camel.CamelConfigBuilder;
import org.jboss.mapper.camel.CamelEndpoint;
import org.jboss.mapper.camel.EndpointHelper;
import org.jboss.mapper.dozer.DozerMapperConfiguration;
import org.jboss.mapper.eclipse.internal.editor.CamelEndpointSelectionDialog;
import org.jboss.mapper.eclipse.internal.editor.LiteralsViewer;
import org.jboss.mapper.eclipse.internal.editor.MappingViewer;
import org.jboss.mapper.eclipse.internal.editor.MappingsViewer;
import org.jboss.mapper.eclipse.internal.editor.ModelTabFolder;
import org.jboss.mapper.eclipse.internal.editor.ModelTabFolder.DropListener;
import org.jboss.mapper.eclipse.internal.util.JavaUtil;
import org.jboss.mapper.eclipse.internal.util.Util;
import org.jboss.mapper.model.Model;
import org.jboss.mapper.model.ModelBuilder;

/**
 *
 */
// TODO save preferences for toggle buttons
// TODO content assist in text
// TODO search fields in model viewers
// TODO sort in mappings viewer
// TODO search in mappings viewer
// TODO DnD from model viewer to add new mapping
// TODO add button in mappings viewer
public class TransformationEditor extends EditorPart {

    private static final int SASH_COLOR = SWT.COLOR_DARK_GRAY;
    private static final int SASH_WIDTH = 3;

    IFile configFile;
    MapperConfiguration config;
    URLClassLoader loader;
    File camelConfigFile;
    CamelConfigBuilder camelConfig;
    CamelEndpoint camelEndpoint;

    MappingsViewer mappingsViewer;
    Text helpText;
    ModelTabFolder sourceModelTabFolder, targetModelTabFolder;
    MappingViewer mappingViewer;

    /**
     * @param existingModel
     * @param name
     * @return the new Model
     * @throws Exception
     */
    public Model changeModel(final Model existingModel,
            final String name) throws Exception {
        final Model model = ModelBuilder.fromJavaClass(loader.loadClass(name));
        if (existingModel.equals(model))
            return model;
        final boolean sourceChanged = existingModel.equals(config.getTargetModel());
        if (sourceChanged) {
            final String targetType = config.getTargetModel().getType();
            config.removeAllMappings();
            config.addClassMapping(model.getType(), targetType);
        } else {
            final String sourceType = config.getSourceModel().getType();
            config.removeAllMappings();
            config.addClassMapping(sourceType, model.getType());
        }
        if (camelEndpoint == null) {
            final CamelEndpointSelectionDialog dlg =
                    new CamelEndpointSelectionDialog(Display.getCurrent().getActiveShell(),
                            configFile.getProject(),
                            null);
            if (dlg.open() != Window.OK)
                throw new Exception("Unable to update Camel endpoint");
            setCamelEndpoint(dlg.getEndpointID(), dlg.getCamelFilePath());
        } else {
            if (sourceChanged)
                EndpointHelper.setSourceModel(camelEndpoint, model.getType());
            else
                EndpointHelper.setTargetModel(camelEndpoint, model.getType());
            saveCamelConfig();
        }
        save();
        mappingsViewer.refresh(this, config);
        updateHelpText(helpText);
        return model;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createPartControl(final Composite parent) {
        final SashForm verticalSplitter = new SashForm(parent, SWT.VERTICAL);
        verticalSplitter.setBackground(parent.getDisplay().getSystemColor(SASH_COLOR));
        verticalSplitter.setSashWidth(SASH_WIDTH);
        final Composite pane = new Composite(verticalSplitter, SWT.NONE);
        pane.setLayout(GridLayoutFactory.fillDefaults().numColumns(3).create());
        pane.setBackground(parent.getBackground());
        // Create source model toggle button
        ToolBar toolBar = new ToolBar(pane, SWT.NONE);
        toolBar.setLayoutData(GridDataFactory.swtDefaults()
                .align(SWT.BEGINNING, SWT.BOTTOM)
                .create());
        final ToolItem sourceViewerButton = new ToolItem(toolBar, SWT.CHECK);
        sourceViewerButton.setImage(Util.Images.TREE);
        sourceViewerButton.setSelection(true);
        // Create help text
        helpText = new Text(pane, SWT.MULTI | SWT.WRAP);
        helpText.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
        helpText.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_BLUE));
        helpText.setEditable(false);
        updateHelpText(helpText);
        // Create target model toggle button
        toolBar = new ToolBar(pane, SWT.NONE);
        toolBar.setLayoutData(GridDataFactory.swtDefaults()
                .align(SWT.END, SWT.BOTTOM)
                .create());
        final ToolItem targetViewerButton = new ToolItem(toolBar, SWT.CHECK);
        targetViewerButton.setImage(Util.Images.TREE);
        targetViewerButton.setSelection(true);
        // Create splitter between mappings viewer and model viewers
        final SashForm horizontalSplitter = new SashForm(pane, SWT.HORIZONTAL);
        horizontalSplitter.setLayoutData(GridDataFactory.fillDefaults()
                .span(3, 1)
                .grab(true, true)
                .create());
        horizontalSplitter.setBackground(parent.getDisplay().getSystemColor(SASH_COLOR));
        horizontalSplitter.setSashWidth(SASH_WIDTH);
        // Create source model tab folder
        sourceModelTabFolder =
                new ModelTabFolder(this, horizontalSplitter, "Source", config.getSourceModel());
        // Create literals tab
        final CTabItem literalsTab = new CTabItem(sourceModelTabFolder, SWT.NONE);
        literalsTab.setText("Literals");
        final LiteralsViewer literalsViewer =
                new LiteralsViewer(sourceModelTabFolder, config.getLiterals());
        literalsTab.setControl(literalsViewer);
        literalsTab.setImage(Util.Images.LITERAL);
        // Create transformation viewer
        mappingsViewer = new MappingsViewer(this, horizontalSplitter, config);
        // Create target model tab folder
        targetModelTabFolder =
                new ModelTabFolder(this, horizontalSplitter, "Target", config.getTargetModel());
        // Create detail area
        mappingViewer = new MappingViewer(this, verticalSplitter, parent.getBackground());
        // Configure size of components in vertical splitter
        verticalSplitter.setWeights(new int[] {75, 25});

        targetModelTabFolder.configureDropSupport(new DropListener() {

            @Override
            public void drop(final Object object,
                    final Model targetModel) throws Exception {
                dropOnTarget(object, targetModel);
            }
        });
        // Set weights so mappings view is at preferred with
        horizontalSplitter.addControlListener(new ControlAdapter() {

            @Override
            public void controlResized(final ControlEvent event) {
                final double middle = mappingsViewer.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
                final double total = horizontalSplitter.getSize().x;
                final int middleWeight = (int) (middle / total * 100.0);
                final int sideWeight = (100 - middleWeight) / 2;
                horizontalSplitter.setWeights(new int[] {sideWeight, middleWeight, sideWeight});
            }
        });
        // Wire tree buttons to toggle model viewers between visible and hidden
        sourceViewerButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
                sourceModelTabFolder.setVisible(sourceViewerButton.getSelection());
                horizontalSplitter.layout();
            }
        });
        targetViewerButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
                targetModelTabFolder.setVisible(targetViewerButton.getSelection());
                horizontalSplitter.layout();
            }
        });
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.swt.widgets.Widget#dispose()
     */
    @Override
    public void dispose() {
        super.dispose();
        if (loader != null)
            try {
                loader.close();
            } catch (final IOException e) {
                Activator.error(e);
            }
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void doSave(final IProgressMonitor monitor) {}

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.ui.part.EditorPart#doSaveAs()
     */
    @Override
    public void doSaveAs() {}

    void dropOnTarget(final Object object,
            final Model targetModel) throws Exception {
        mappingsViewer.setFocus(mappingsViewer.createMapping(this, map(object, targetModel)));
        refreshSourceModelViewer();
        refreshTargetModelViewer();
    }

    /**
     * {@inheritDoc}
     *
     * @see EditorPart#init(IEditorSite, IEditorInput)
     */
    @Override
    public void init(final IEditorSite site,
            final IEditorInput input) throws PartInitException {
        final IContentType contentType =
                Platform.getContentTypeManager().getContentType(DozerConfigContentTypeDescriber.ID);
        if (!contentType.isAssociatedWith(input.getName()))
            throw new PartInitException("The Fuse Transformation editor can only be opened with a"
                    + " Dozer configuration file.");
        setSite(site);
        setInput(input);
        setPartName(input.getName());

        configFile = ((FileEditorInput) getEditorInput()).getFile();
        final IJavaProject javaProject = JavaCore.create(configFile.getProject());
        try {
            loader =
                    (URLClassLoader) JavaUtil.getProjectClassLoader(javaProject,
                            getClass().getClassLoader());
            config = DozerMapperConfiguration.loadConfig(new File(configFile.getLocationURI()),
                    loader);
        } catch (final Exception e) {
            throw new PartInitException("Unable to load transformation configuration file", e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.ui.part.EditorPart#isDirty()
     */
    @Override
    public boolean isDirty() {
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
     */
    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

    /**
     * @param fieldMapping
     * @param customOperationType
     * @param customOperationMethod
     * @return A newly-created custom mapping
     * @throws Exception
     */
    public CustomMapping map(final FieldMapping fieldMapping,
            final String customOperationType,
            final String customOperationMethod) throws Exception {
        final CustomMapping mapping =
                config.customizeMapping(fieldMapping, customOperationType, customOperationMethod);
        save();
        return mapping;
    }

    /**
     * @param source
     * @param targetModel
     * @return A newly-created mapping
     * @throws Exception
     */
    public MappingOperation<?, ?> map(final Object source,
            final Model targetModel) throws Exception {
        final MappingOperation<?, ?> mapping = source instanceof Model
                ? config.map((Model) source, targetModel)
                : config.map(new Literal(source.toString()), targetModel);
        save();
        return mapping;
    }

    /**
     * @param model
     * @param rootModel
     * @return <code>true</code> if the supplied model has been mapped at least
     *         once
     */
    public boolean mapped(final Model model,
            final Model rootModel) {
        return rootModel.equals(config.getSourceModel())
                ? !config.getMappingsForSource(model).isEmpty()
                : !config.getMappingsForTarget(model).isEmpty();
    }

    /**
     * @return The project containing the transformation configuration being
     *         edited
     */
    public IProject project() {
        return configFile.getProject();
    }

    /**
     *
     */
    public void refreshSourceModelViewer() {
        sourceModelTabFolder.refresh();
    }

    /**
     *
     */
    public void refreshTargetModelViewer() {
        targetModelTabFolder.refresh();
    }

    /**
     * @throws Exception
     */
    public void save() throws Exception {
        try (FileOutputStream stream =
                new FileOutputStream(new File(configFile.getLocationURI()))) {
            config.saveConfig(stream);
            configFile.getProject().refreshLocal(IResource.DEPTH_INFINITE, null);
        }
    }

    void saveCamelConfig() throws Exception {
        try (FileOutputStream stream = new FileOutputStream(camelConfigFile)) {
            camelConfig.saveConfig(stream);
            configFile.getProject().refreshLocal(IResource.DEPTH_INFINITE, null);
        }
    }

    /**
     * @param mapping
     */
    public void selectMapping(final MappingOperation<?, ?> mapping) {
        if (mapping != null) {
            sourceModelTabFolder.select(mapping.getSource());
            targetModelTabFolder.select(mapping.getTarget());
        }
        mappingViewer.update(config.getSourceModel(), config.getTargetModel(), mapping);
    }

    /**
     * @param endPointId
     * @param camelFilePath
     * @throws Exception
     */
    public void setCamelEndpoint(final String endPointId,
            final String camelFilePath) throws Exception {
        camelConfigFile =
                new File(configFile.getProject().getFile(camelFilePath).getLocationURI());
        camelConfig = CamelConfigBuilder.loadConfig(camelConfigFile);
        camelEndpoint = camelConfig.getEndpoint(endPointId);
        EndpointHelper.setSourceModel(camelEndpoint, config.getSourceModel().getType());
        EndpointHelper.setTargetModel(camelEndpoint, config.getTargetModel().getType());
        saveCamelConfig();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     */
    @Override
    public void setFocus() {}

    /**
     * @return the source model
     */
    public Model sourceModel() {
        return config.getSourceModel();

    }

    /**
     * @return the target model
     */
    public Model targetModel() {
        return config.getTargetModel();
    }

    /**
     * Note, this method does not call {@link #save()}
     *
     * @param mapping
     */
    public void unmap(final MappingOperation<?, ?> mapping) {
        config.removeMapping(mapping);
        refreshSourceModelViewer();
        refreshTargetModelViewer();
    }

    void updateHelpText(final Text helpText) {
        helpText.setText("Create a new mapping below by dragging a field from source "
                + config.getSourceModel().getName() + " on the left to target "
                + config.getTargetModel().getName() + " on the right.");
    }

    /**
     * @param mapping
     */
    public void updateMapping(final MappingOperation<?, ?> mapping) {
        mappingsViewer.updateMapping(mapping);
    }
}
