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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
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
public class TransformationEditor extends EditorPart {

    IFile configFile;
    MapperConfiguration config;
    URLClassLoader loader;
    File camelConfigFile;
    CamelConfigBuilder camelConfig;
    CamelEndpoint camelEndpoint;

    MappingsViewer mappingsViewer;
    Text helpText;
    ModelTabFolder sourceModelTabFolder;
    ModelTabFolder targetModelTabFolder;

    /**
     * @param existingModel
     * @param name
     * @return the new Model
     * @throws Exception
     */
    public Model changeModel(final Model existingModel,
            final String name) throws Exception {
        final Model model = ModelBuilder.fromJavaClass(loader.loadClass(name));
        if (existingModel.equals(model)) {
            return model;
        }
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
                            configFile.getProject(), null);
            if (dlg.open() != Window.OK) {
                throw new Exception("Unable to update Camel endpoint");
            }
            setCamelEndpoint(dlg.getEndpointID(), dlg.getCamelFilePath());
        } else {
            if (sourceChanged) {
                EndpointHelper.setSourceModel(camelEndpoint, model.getType());
            } else {
                EndpointHelper.setTargetModel(camelEndpoint, model.getType());
            }
            saveCamelConfig();
        }
        save();
        mappingsViewer.refresh(this, config.getMappings());
        updateHelpText(helpText);
        return model;
    }

    private void createModelsPane(final SashForm splitter) {
        // Create pane for model viewers
        final Composite modelsPane = new Composite(splitter, SWT.NONE);
        modelsPane.setBackground(splitter.getBackground());
        modelsPane.setLayout(GridLayoutFactory.swtDefaults().margins(0, 5).numColumns(3).create());

        // Create help text
        helpText = new Text(modelsPane, SWT.MULTI | SWT.WRAP);
        helpText.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(3, 1).create());
        helpText.setForeground(splitter.getDisplay().getSystemColor(SWT.COLOR_BLUE));
        helpText.setEditable(false);

        // Create source model tab folder
        sourceModelTabFolder =
                new ModelTabFolder(this, modelsPane, "Source", config.getSourceModel());
        sourceModelTabFolder
                .setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

        // Create literals tab
        final CTabItem literalsTab = new CTabItem(sourceModelTabFolder, SWT.NONE);
        literalsTab.setText("Literals");
        final LiteralsViewer literalsViewer =
                new LiteralsViewer(sourceModelTabFolder, config.getLiterals());
        literalsTab.setControl(literalsViewer);
        literalsTab.setImage(Util.LITERAL_IMAGE);

        new Label(modelsPane, SWT.NONE).setImage(Util.RIGHT_ARROW_IMAGE);

        // Create target model tab folder
        targetModelTabFolder =
                new ModelTabFolder(this, modelsPane, "Target", config.getTargetModel());
        targetModelTabFolder
                .setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
        targetModelTabFolder.configureDropSupport(new DropListener() {

            @Override
            public void drop(final Object object,
                    final Model targetModel) throws Exception {
                dropOnTarget(object, targetModel);
            }
        });

        updateHelpText(helpText);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createPartControl(final Composite parent) {
        // Create splitter between mappings viewer and model viewers
        final SashForm splitter = new SashForm(parent, SWT.VERTICAL);
        // Create transformation viewer
        mappingsViewer = new MappingsViewer(this, splitter, config.getMappings());
        // Create models pane containing source and target model viewers
        createModelsPane(splitter);
        // Configure splitter
        splitter.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
        splitter.SASH_WIDTH = 5;
        splitter.setWeights(new int[] {25, 75});
        // Add selection listener to highlight associated model elements in
        // model viewers when a mapping is selected
        mappingsViewer.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
                final MappingOperation<?, ?> mapping = (MappingOperation<?, ?>) event.data;
                sourceModelTabFolder.select(mapping.getSource());
                targetModelTabFolder.select(mapping.getTarget());
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
        if (loader != null) {
            try {
                loader.close();
            } catch (final IOException e) {
                Activator.error(e);
            }
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
        mappingsViewer.createMapping(this, map(object, targetModel));
        refreshSourceModelViewer();
        refreshTargetModelViewer();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite,
     *      org.eclipse.ui.IEditorInput)
     */
    @Override
    public void init(final IEditorSite site,
            final IEditorInput input) throws PartInitException {
        final IContentType contentType =
                Platform.getContentTypeManager().getContentType(DozerConfigContentTypeDescriber.ID);
        if (!contentType.isAssociatedWith(input.getName())) {
            throw new PartInitException(
                    "The Fuse Transformation editor can only be opened with a Dozer configuration file.");
        }
        setSite(site);
        setInput(input);
        setPartName(input.getName());

        configFile = ((FileEditorInput) getEditorInput()).getFile();
        final IJavaProject javaProject = JavaCore.create(configFile.getProject());
        try {
            loader = (URLClassLoader) JavaUtil.getProjectClassLoader(
                    javaProject, getClass().getClassLoader());
            config = DozerMapperConfiguration.loadConfig(
                    new File(configFile.getLocationURI()), loader);
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
        final MappingOperation<?, ?> mapping =
                source instanceof Model ? config.map((Model) source, targetModel)
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
    public boolean mapped(final Model model, final Model rootModel) {
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
        try (FileOutputStream stream = new FileOutputStream(new File(configFile.getLocationURI()))) {
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
     * @param endPointId
     * @param camelFilePath
     * @throws Exception
     */
    public void setCamelEndpoint(final String endPointId,
            final String camelFilePath) throws Exception {
        camelConfigFile = new File(configFile.getProject().getFile(camelFilePath).getLocationURI());
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
        helpText.setText("Create a new mapping in the list of operations above "
                + "by dragging an item below from source "
                + config.getSourceModel().getName()
                + " to target "
                + config.getTargetModel().getName());
    }
}
