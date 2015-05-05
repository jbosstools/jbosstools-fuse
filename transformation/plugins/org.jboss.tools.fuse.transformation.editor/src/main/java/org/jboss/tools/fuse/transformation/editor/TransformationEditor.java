/******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: JBoss by Red Hat - Initial implementation.
 *****************************************************************************/
package org.jboss.tools.fuse.transformation.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISaveablePart2;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;
import org.fusesource.ide.camel.model.catalog.CamelModelFactory;
import org.jboss.tools.fuse.transformation.MappingOperation;
import org.jboss.tools.fuse.transformation.camel.CamelEndpoint;
import org.jboss.tools.fuse.transformation.editor.internal.MappingDetailViewer;
import org.jboss.tools.fuse.transformation.editor.internal.MappingsViewer;
import org.jboss.tools.fuse.transformation.editor.internal.PotentialDropTarget;
import org.jboss.tools.fuse.transformation.editor.internal.SourceTabFolder;
import org.jboss.tools.fuse.transformation.editor.internal.TargetTabFolder;
import org.jboss.tools.fuse.transformation.editor.internal.util.JavaUtil;
import org.jboss.tools.fuse.transformation.editor.internal.util.TransformationConfig;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util.Colors;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util.Images;

/**
 *
 */
public class TransformationEditor extends EditorPart implements ISaveablePart2 {

    private static final int SASH_COLOR = SWT.COLOR_DARK_GRAY;
    private static final int SASH_WIDTH = 3;

    TransformationConfig config;
    URLClassLoader loader;
    File camelConfigFile;
    CamelEndpoint camelEndpoint;

    MappingsViewer mappingsViewer;
    Text helpText;
    SourceTabFolder sourceTabFolder;
    TargetTabFolder targetTabFolder;
    MappingDetailViewer mappingDetailViewer;
    ToolItem sourceViewerButton;
    ToolItem targetViewerButton;

    final List<PotentialDropTarget> potentialDropTargets = new ArrayList<>();

    void configEvent() {
        firePropertyChange(IEditorPart.PROP_DIRTY);
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
        pane.setBackground(Colors.BACKGROUND);
        // Create source model toggle button
        ToolBar toolBar = new ToolBar(pane, SWT.NONE);
        toolBar.setLayoutData(GridDataFactory.swtDefaults()
                                             .align(SWT.BEGINNING, SWT.BOTTOM)
                                             .create());
        sourceViewerButton = new ToolItem(toolBar, SWT.CHECK);
        sourceViewerButton.setImage(Images.TREE);
        // Create help text
        helpText = new Text(pane, SWT.MULTI | SWT.WRAP);
        helpText.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
        helpText.setEditable(false);
        helpText.setBackground(pane.getBackground());
        // Create target model toggle button
        toolBar = new ToolBar(pane, SWT.NONE);
        toolBar.setLayoutData(GridDataFactory.swtDefaults()
                                             .align(SWT.END, SWT.BOTTOM)
                                             .create());
        targetViewerButton = new ToolItem(toolBar, SWT.CHECK);
        targetViewerButton.setImage(Images.TREE);
        // Create splitter between mappings viewer and model viewers
        final SashForm horizontalSplitter = new SashForm(pane, SWT.HORIZONTAL);
        horizontalSplitter.setLayoutData(GridDataFactory.fillDefaults()
                                                        .span(3, 1)
                                                        .grab(true, true)
                                                        .create());
        horizontalSplitter.setBackground(parent.getDisplay().getSystemColor(SASH_COLOR));
        horizontalSplitter.setSashWidth(SASH_WIDTH);
        // Create source tab folder
        sourceTabFolder = new SourceTabFolder(config, horizontalSplitter, potentialDropTargets);
        sourceTabFolder.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
                updateHelpText();
            }
        });
        // Create transformation viewer
        mappingsViewer = new MappingsViewer(config, this, horizontalSplitter, potentialDropTargets);
        // Create target tab folder
        targetTabFolder = new TargetTabFolder(config, horizontalSplitter, potentialDropTargets);
        // Create detail area
        mappingDetailViewer =
            new MappingDetailViewer(config, verticalSplitter, potentialDropTargets);
        // Configure size of components in splitters
        verticalSplitter.setWeights(new int[] {75, 25});
        horizontalSplitter.setWeights(new int[] {33, 34, 33});
        // Wire tree buttons to toggle model viewers between visible and hidden
        sourceViewerButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
                sourceTabFolder.setVisible(sourceViewerButton.getSelection());
                horizontalSplitter.layout();
                sourceViewerButton.setToolTipText(sourceViewerButton.getSelection()
                                                  ? "Hide the source/variables viewers"
                                                  : "Show the source/variables viewers");
                updateHelpText();
            }
        });
        sourceViewerButton.setSelection(true);
        targetViewerButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
                targetTabFolder.setVisible(targetViewerButton.getSelection());
                horizontalSplitter.layout();
                targetViewerButton.setToolTipText(targetViewerButton.getSelection()
                                                  ? "Hide the target viewer"
                                                  : "Show the target viewer");
                updateHelpText();
            }
        });
        targetViewerButton.setSelection(true);
        config.addListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(final PropertyChangeEvent event) {
                configEvent();
            }
        });
        updateHelpText();
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
        if (!contentType.isAssociatedWith(input.getName())) {
            throw new PartInitException("The Fuse Transformation editor can only be opened with a"
                                        + " Dozer configuration file.");
        }
        setSite(site);
        setInput(input);
        setPartName(input.getName());

        final IFile configFile = ((FileEditorInput) getEditorInput()).getFile();
        final IJavaProject javaProject = JavaCore.create(configFile.getProject());
        try {
            loader = (URLClassLoader) JavaUtil.getProjectClassLoader(javaProject,
                                                                     getClass().getClassLoader());
            config = new TransformationConfig(configFile, loader);
        } catch (final Exception e) {
            throw new PartInitException("Unable to load transformation configuration file", e);
        }
        CamelModelFactory.initializeModels();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.ui.part.EditorPart#isDirty()
     */
    @Override
    public boolean isDirty() {
        return config.hasMappingPlaceholders();
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
     * {@inheritDoc}
     *
     * @see org.eclipse.ui.ISaveablePart2#promptToSaveOnClose()
     */
    @Override
    public int promptToSaveOnClose() {
        return config.hasMappingPlaceholders()
               && !MessageDialog.openConfirm(mappingsViewer.getShell(), "Confirm",
                                             "Are you sure?\n\n"
                                             + "All incomplete mappings will be lost when the "
                                             + "editor is closed.")
        ? CANCEL : NO;
    }

    /**
     * @param mapping
     */
    public void selected(final MappingOperation<?, ?> mapping) {
        sourceTabFolder.select(mapping.getSource());
        targetTabFolder.select(mapping.getTarget());
        mappingDetailViewer.update(mapping);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     */
    @Override
    public void setFocus() {
        mappingsViewer.setFocus();
    }

    void updateHelpText() {
        if (sourceViewerButton.getSelection() && targetViewerButton.getSelection()) {
            if (sourceTabFolder.getSelectionIndex() == 0) {
                helpText.setText("Create a new mapping below by dragging a field in source "
                                 + config.getSourceModel().getName()
                                 + " on the left to a field in target "
                                 + config.getTargetModel().getName() + " on the right.");
            } else {
                helpText.setText("Create a new mapping below by dragging a variable from the list"
                                  + " of variables on the left to a field in target "
                                  + config.getTargetModel().getName() + " on the right.");
            }
        } else {
            helpText.setText("");
        }
    }
}
