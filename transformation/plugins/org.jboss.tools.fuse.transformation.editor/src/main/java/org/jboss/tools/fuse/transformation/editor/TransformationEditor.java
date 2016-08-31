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
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.swt.SWT;
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
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISaveablePart2;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;
import org.fusesource.ide.camel.editor.utils.CamelUtils;
import org.fusesource.ide.camel.editor.utils.MavenUtils;
import org.fusesource.ide.camel.model.service.core.catalog.CamelModelFactory;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.jboss.tools.fuse.transformation.core.MappingOperation;
import org.jboss.tools.fuse.transformation.editor.internal.MappingDetailViewer;
import org.jboss.tools.fuse.transformation.editor.internal.MappingsViewer;
import org.jboss.tools.fuse.transformation.editor.internal.PotentialDropTarget;
import org.jboss.tools.fuse.transformation.editor.internal.SourceTabFolder;
import org.jboss.tools.fuse.transformation.editor.internal.TargetTabFolder;
import org.jboss.tools.fuse.transformation.editor.internal.l10n.Messages;
import org.jboss.tools.fuse.transformation.editor.internal.util.JavaUtil;
import org.jboss.tools.fuse.transformation.editor.internal.util.TransformationManager;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util.Colors;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util.Images;
import org.jboss.tools.fuse.transformation.editor.transformations.Function;
import org.jboss.tools.fuse.transformation.extensions.DozerConfigContentTypeDescriber;

public class TransformationEditor extends EditorPart implements ISaveablePart2, IResourceChangeListener {

	private static final int SASH_WIDTH = 3;

    private static final String PREFERENCE_PREFIX = TransformationEditor.class.getName() + "."; //$NON-NLS-1$

    private static final String SOURCE_VIEWER_PREFERENCE = PREFERENCE_PREFIX + "sourceViewer"; //$NON-NLS-1$
    private static final String TARGET_VIEWER_PREFERENCE = PREFERENCE_PREFIX + "targetViewer"; //$NON-NLS-1$

    private static final String HORIZONTAL_SPLITTER_PREFIX = PREFERENCE_PREFIX + "horizontalSplitterWeight."; //$NON-NLS-1$
    private static final String VERTICAL_SPLITTER_PREFIX = PREFERENCE_PREFIX + "verticalSplitterWeight."; //$NON-NLS-1$
    private static final String HORIZONTAL_SPLITTER_WEIGHT_LEFT_PREFERENCE = HORIZONTAL_SPLITTER_PREFIX + "left"; //$NON-NLS-1$
    private static final String HORIZONTAL_SPLITTER_WEIGHT_CENTER_PREFERENCE = HORIZONTAL_SPLITTER_PREFIX + "center"; //$NON-NLS-1$
    private static final String HORIZONTAL_SPLITTER_WEIGHT_RIGHT_PREFERENCE = HORIZONTAL_SPLITTER_PREFIX + "right"; //$NON-NLS-1$
    private static final String VERTICAL_SPLITTER_WEIGHT_TOP_PREFERENCE = VERTICAL_SPLITTER_PREFIX + "top"; //$NON-NLS-1$
    private static final String VERTICAL_SPLITTER_WEIGHT_BOTTOM_PREFERENCE = VERTICAL_SPLITTER_PREFIX + "bottom"; //$NON-NLS-1$

    private static final String VERSION_PREFERENCE = PREFERENCE_PREFIX + "version"; //$NON-NLS-1$

    TransformationManager manager;
    URLClassLoader loader;
    File camelConfigFile;
    AbstractCamelModelElement camelEndpoint;

    MappingsViewer mappingsViewer;
    Text helpText;
    SourceTabFolder sourceTabFolder;
    TargetTabFolder targetTabFolder;
    MappingDetailViewer mappingDetailViewer;
    ToolItem sourceViewerButton;
    ToolItem targetViewerButton;

    final List<PotentialDropTarget> potentialDropTargets = new ArrayList<>();

    /**
     * creates a new editor instance
     */
    public TransformationEditor() {
        super();
        ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
    }

    protected void closeEditorsWithoutValidInput() {
        Display.getDefault().asyncExec(new Runnable() {

            @Override
            public void run() {
                if (getSite() == null || getSite().getPage() == null) return;
                // close all editors without valid input
                IEditorReference[] refs = getSite().getPage().getEditorReferences();
                if (refs != null) {
                    for (IEditorReference ref : refs) {
                        IEditorPart editor = ref.getEditor(false);
                        if (editor != null) {
                            IEditorInput editorInput = editor.getEditorInput();
                            if (editorInput instanceof FileEditorInput
                                && !((FileEditorInput)editorInput).getFile().exists()) {
                                getSite().getPage().closeEditor(editor, false);
                                editor.dispose();
                            }
                        }
                    }
                }
            }
        });
    }

    void copySourceToProject(Class<?> sourceClass,
                             boolean latestVersion) throws IOException {
        IPath pkgPath = new Path(sourceClass.getPackage().getName().replace('.', '/'));
        IPath xformsFolderPath = manager.project().getLocation().append(Util.TRANSFORMATIONS_FOLDER);
        File file = xformsFolderPath.append(pkgPath).toFile();
        if (!file.exists()) file.mkdirs();
        IPath resourcePath = pkgPath.append(sourceClass.getSimpleName()).addFileExtension("java"); //$NON-NLS-1$
        file = xformsFolderPath.append(resourcePath).toFile();
        if (file.exists() && latestVersion) return;
        byte[] buf = null;
        try (InputStream in = sourceClass.getResourceAsStream(resourcePath.makeAbsolute().toString())) {
            if (in != null) {
                buf = new byte[4096];
                try (OutputStream out = new FileOutputStream(file)) {
                    for (int len = in.read(buf); len > 0; len = in.read(buf)) {
                        out.write(buf, 0, len);
                    }
                }
            }
        }
        // Below is necessary when running from within development Eclipse
        if (buf == null) {
            try (InputStream in =
                    sourceClass.getResourceAsStream(new Path(MavenUtils.RESOURCES_PATH).append(resourcePath).makeAbsolute().toString())) {
                buf = new byte[4096];
                try (OutputStream out = new FileOutputStream(file)) {
                    for (int len = in.read(buf); len > 0; len = in.read(buf)) {
                        out.write(buf, 0, len);
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createPartControl(final Composite parent) {
        final IPreferenceStore prefs = Activator.plugin().getPreferenceStore();
        prefs.setDefault(SOURCE_VIEWER_PREFERENCE, true);
        prefs.setDefault(TARGET_VIEWER_PREFERENCE, true);
        prefs.setDefault(HORIZONTAL_SPLITTER_WEIGHT_LEFT_PREFERENCE, 33);
        prefs.setDefault(HORIZONTAL_SPLITTER_WEIGHT_CENTER_PREFERENCE, 34);
        prefs.setDefault(HORIZONTAL_SPLITTER_WEIGHT_RIGHT_PREFERENCE, 33);
        prefs.setDefault(VERTICAL_SPLITTER_WEIGHT_TOP_PREFERENCE, 75);
        prefs.setDefault(VERTICAL_SPLITTER_WEIGHT_BOTTOM_PREFERENCE, 25);

        final SashForm verticalSplitter = new SashForm(parent, SWT.VERTICAL);
        verticalSplitter.setBackground(Colors.SASH);
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
        helpText = new Text(pane, SWT.MULTI | SWT.WRAP | SWT.READ_ONLY);
        helpText.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
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
        horizontalSplitter.setBackground(Colors.SASH);
        horizontalSplitter.setSashWidth(SASH_WIDTH);
        // Create source tab folder
        sourceTabFolder = new SourceTabFolder(manager, horizontalSplitter, potentialDropTargets);
        sourceTabFolder.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
                updateHelpText();
            }
        });
        // Create mappings viewer
        mappingsViewer = new MappingsViewer(manager, this, horizontalSplitter, potentialDropTargets);
        // Create target tab folder
        targetTabFolder = new TargetTabFolder(manager, horizontalSplitter, potentialDropTargets);
        // Create detail area
        mappingDetailViewer =
            new MappingDetailViewer(manager, verticalSplitter, potentialDropTargets);
        // Configure size of components in splitters
        verticalSplitter.setWeights(new int[] {prefs.getInt(VERTICAL_SPLITTER_WEIGHT_TOP_PREFERENCE),
            prefs.getInt(VERTICAL_SPLITTER_WEIGHT_BOTTOM_PREFERENCE)});
        horizontalSplitter.setWeights(new int[] {prefs.getInt(HORIZONTAL_SPLITTER_WEIGHT_LEFT_PREFERENCE),
            prefs.getInt(HORIZONTAL_SPLITTER_WEIGHT_CENTER_PREFERENCE),
            prefs.getInt(HORIZONTAL_SPLITTER_WEIGHT_RIGHT_PREFERENCE)});
        mappingsViewer.addControlListener(new ControlAdapter() {

            @Override
            public void controlResized(ControlEvent event) {
                int[] weights = horizontalSplitter.getWeights();
                prefs.setValue(HORIZONTAL_SPLITTER_WEIGHT_LEFT_PREFERENCE, weights[0]);
                prefs.setValue(HORIZONTAL_SPLITTER_WEIGHT_CENTER_PREFERENCE, weights[1]);
                prefs.setValue(HORIZONTAL_SPLITTER_WEIGHT_RIGHT_PREFERENCE, weights[2]);
                weights = verticalSplitter.getWeights();
                prefs.setValue(VERTICAL_SPLITTER_WEIGHT_TOP_PREFERENCE, weights[0]);
                prefs.setValue(VERTICAL_SPLITTER_WEIGHT_BOTTOM_PREFERENCE, weights[1]);
            }
        });
        // Wire tree buttons to toggle model viewers between visible and hidden
        sourceViewerButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
                toggleSourceViewer(horizontalSplitter);
                prefs.setValue(SOURCE_VIEWER_PREFERENCE, sourceViewerButton.getSelection());
            }
        });
        sourceViewerButton.setSelection(prefs.getBoolean(SOURCE_VIEWER_PREFERENCE));
        if (!sourceViewerButton.getSelection()) toggleSourceViewer(horizontalSplitter);
        targetViewerButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
                toggleTargetViewer(horizontalSplitter);
                prefs.setValue(TARGET_VIEWER_PREFERENCE, targetViewerButton.getSelection());
            }
        });
        targetViewerButton.setSelection(prefs.getBoolean(TARGET_VIEWER_PREFERENCE));
        if (!targetViewerButton.getSelection()) toggleTargetViewer(horizontalSplitter);
        manager.addListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(final PropertyChangeEvent event) {
                managerEvent();
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
    public void init(IEditorSite site,
                     IEditorInput input) throws PartInitException {
        IContentType contentType = Platform.getContentTypeManager().getContentType(DozerConfigContentTypeDescriber.ID);
        if (!contentType.isAssociatedWith(input.getName()))
            throw new PartInitException(Messages.TransformationEditor_invalidTransformationFile);
        if (CamelUtils.getDiagramEditor() == null)
            throw new PartInitException(Messages.TransformationEditor_mustBeOpenedViaCamelEditor);
        setSite(site);
        setInput(input);
        setPartName(input.getName());

        IFile configFile = ((FileEditorInput)getEditorInput()).getFile();
        IJavaProject javaProject = JavaCore.create(configFile.getProject());
        try {
            loader = (URLClassLoader)JavaUtil.getProjectClassLoader(javaProject, getClass().getClassLoader());
            manager = new TransformationManager(configFile, loader);
            CamelModelFactory.initializeModels();
            // Add contributed transformations if missing or a different version
            String version = Activator.plugin().getBundle().getVersion().toString();
            IPreferenceStore prefs = Activator.plugin().getPreferenceStore();
            boolean latestVersion = version.equals(prefs.getString(VERSION_PREFERENCE));
            copySourceToProject(Function.class, latestVersion);
            for (IConfigurationElement element : Platform.getExtensionRegistry()
                                                         .getConfigurationElementsFor(Activator.TRANSFORMATION_EXTENSION_POINT)) {
                copySourceToProject(element.createExecutableExtension("class").getClass(), latestVersion); //$NON-NLS-1$
            }
            if (!latestVersion) prefs.setValue(VERSION_PREFERENCE, version);

            // Ensure Maven will compile transformations folder
            IProject project = manager.project();
			File pomFile = project.getLocation().append("pom.xml").toFile(); //$NON-NLS-1$
			MavenUtils mavenUtils = new MavenUtils();
			mavenUtils.addResourceFolder(project, pomFile, Util.TRANSFORMATIONS_FOLDER);
			mavenUtils.addResourceFolder(project, pomFile, MavenUtils.RESOURCES_PATH);

			IProgressMonitor monitor = new NullProgressMonitor();
			
			// Ensure import of package com.sun.el (FUSETOOLS-2039)
			updateManifestPackageImports(project, pomFile, monitor);


            // Ensure Java project source classpath entry exists for main Java source & transformations folder
            Util.ensureSourceFolderExists(javaProject, Util.TRANSFORMATIONS_FOLDER, monitor);
            Util.ensureSourceFolderExists(javaProject, new MavenUtils().javaSourceFolder(), monitor);
            project.refreshLocal(IResource.DEPTH_INFINITE, monitor);

            // Ensure build of Java classes has completed
            waitJavaBuild(monitor);
        } catch (final Exception e) {
            throw new PartInitException("Error initializing editor", e); //$NON-NLS-1$
        }
    }

	private void waitJavaBuild(IProgressMonitor monitor) {
		try{
			Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, monitor);
		} catch(InterruptedException ie){
			//try to wait a second time
			//ugly workaround to bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=335251
			try{
				Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, monitor);
			} catch(InterruptedException ie2){
				Activator.error(ie2);
			}
		}
	}

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.ui.part.EditorPart#isDirty()
     */
    @Override
    public boolean isDirty() {
        return manager.hasMappingPlaceholders();
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

    void managerEvent() {
        firePropertyChange(IEditorPart.PROP_DIRTY);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.ui.ISaveablePart2#promptToSaveOnClose()
     */
    @Override
    public int promptToSaveOnClose() {
        return manager.hasMappingPlaceholders()
               && !MessageDialog.openConfirm(mappingsViewer.getShell(), Messages.TransformationEditor_ConfirmDialogTtile,
				Messages.TransformationEditor_messageDialogConfirmation)
            ? CANCEL : NO;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org
     * .eclipse.core.resources.IResourceChangeEvent)
     */
    @Override
    public void resourceChanged(final IResourceChangeEvent event) {
        switch (event.getType()) {
            case IResourceChangeEvent.POST_CHANGE:
                // file has been deleted...
                closeEditorsWithoutValidInput();
                break;
            case IResourceChangeEvent.PRE_CLOSE:
                Display.getDefault().asyncExec(new Runnable() {

                    @Override
                    public void run() {
                        IWorkbenchPage[] pages = getSite().getWorkbenchWindow()
                                                          .getPages();
                        for (int i = 0; i < pages.length; i++) {
                            IEditorInput editorInput = getEditorInput();
                            if (editorInput instanceof FileEditorInput && ((FileEditorInput)editorInput)
                                                                                                        .getFile().getProject()
                                                                                                        .equals(event.getResource())) {
                                IWorkbenchPage page = pages[i];
                                IEditorPart editorPart = page.findEditor(editorInput);
                                page.closeEditor(editorPart, true);
                            }
                        }
                    }
                });
                break;
        }
    }

    public void selected(final MappingOperation<?, ?> mapping) {
        sourceTabFolder.select(mapping.getSource());
        targetTabFolder.select(mapping.getTarget());
        mappingDetailViewer.update(mapping);
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     */
    @Override
    public void setFocus() {

    }

    void toggleSourceViewer(SashForm horizontalSplitter) {
        sourceTabFolder.setVisible(sourceViewerButton.getSelection());
        horizontalSplitter.layout();
        sourceViewerButton.setToolTipText(sourceViewerButton.getSelection()
            ? Messages.TransformationEditor_tooltipHideSourceVariableViewers
            : Messages.TransformationEditor_tooltipShowSourceVariableViewers);
        updateHelpText();
    }

    void toggleTargetViewer(SashForm horizontalSplitter) {
        targetTabFolder.setVisible(targetViewerButton.getSelection());
        horizontalSplitter.layout();
        targetViewerButton.setToolTipText(targetViewerButton.getSelection()
            ? Messages.TransformationEditor_tooltipHideTargetViewers
            : Messages.TransformationEditor_tooltipShowTargetViewers);
        updateHelpText();
    }

    void updateHelpText() {
        if (sourceViewerButton.getSelection() && targetViewerButton.getSelection()) {
            if (sourceTabFolder.getSelectionIndex() == 0) {
				helpText.setText(Messages.bind(Messages.TransformationEditor_helptextSource,
						manager.rootSourceModel().getName(), manager.rootTargetModel().getName()));
            } else {
				helpText.setText(Messages.bind(Messages.TransformationEditor_helpTextTarget,
								manager.rootTargetModel().getName()));
            }
        } else {
            helpText.setText(""); //$NON-NLS-1$
        }
        helpText.getParent().layout();
    }

    public void updateManifestPackageImports(IProject project, File pomFile, IProgressMonitor monitor) throws Exception {
        IPath pomPath = project.getRawLocation() != null
                        ? project.getRawLocation()
                        : ResourcesPlugin.getWorkspace().getRoot().getLocation().append(project.getFullPath());
        Model pomModel = MavenPlugin.getMaven().readModel(new File(pomPath.append("pom.xml").toOSString()));  //$NON-NLS-1$
        if ("war".equals(pomModel.getPackaging())){ //$NON-NLS-1$
        	return; 
        }
        Build build = pomModel.getBuild();
        Map<String, Plugin> pluginsByName = build.getPluginsAsMap();
        Plugin plugin = pluginsByName.get("org.apache.felix:maven-bundle-plugin"); //$NON-NLS-1$
        if (plugin == null) {
            plugin = new Plugin();
            plugin.setGroupId("org.apache.felix"); //$NON-NLS-1$
            plugin.setArtifactId("maven-bundle-plugin"); //$NON-NLS-1$
            plugin.setVersion("3.2.0"); //$NON-NLS-1$
            plugin.setExtensions(true);
            build.addPlugin(plugin);
        }
        Xpp3Dom config = (Xpp3Dom)plugin.getConfiguration();
        if (config == null) {
            config = Xpp3DomBuilder.build(new ByteArrayInputStream(("<configuration>" + //$NON-NLS-1$
                                                                    "    <excludeDependencies>false</excludeDependencies>" + //$NON-NLS-1$
                                                                    "    <archive>" + //$NON-NLS-1$
                                                                    "        <manifestEntries>" + //$NON-NLS-1$
                                                                    "            <Project-Group-Id>${project.groupId}</Project-Group-Id>" + //$NON-NLS-1$
                                                                    "            <Project-Artifact-Id>${project.artifactId}</Project-Artifact-Id>" + //$NON-NLS-1$
                                                                    "            <Project-Version>${project.version}</Project-Version>" + //$NON-NLS-1$
                                                                    "        </manifestEntries>" + //$NON-NLS-1$
                                                                    "    </archive>" + //$NON-NLS-1$
                                                                    "</configuration>").getBytes()), //$NON-NLS-1$
                                          StandardCharsets.UTF_8.name());
            plugin.setConfiguration(config);
        } else {
        	
        }
        Xpp3Dom instructions = config.getChild("instructions"); //$NON-NLS-1$
        if (instructions == null) {
            instructions = Xpp3DomBuilder.build(new ByteArrayInputStream(("<instructions>" + //$NON-NLS-1$
                                                                          "</instructions>").getBytes()), //$NON-NLS-1$
                                                StandardCharsets.UTF_8.name());
            config.addChild(instructions);
        }
        Xpp3Dom importPkg = instructions.getChild("Import-Package"); //$NON-NLS-1$
        if (importPkg == null) {
            importPkg = Xpp3DomBuilder.build(new ByteArrayInputStream(("<Import-Package>" + //$NON-NLS-1$
                                                                       "</Import-Package>").getBytes()), //$NON-NLS-1$
                                             StandardCharsets.UTF_8.name());
            instructions.addChild(importPkg);
        }
        String importPkgs = importPkg.getValue().trim();
        if (!importPkgs.contains("com.sun.el;version=")) { //$NON-NLS-1$
            if (!importPkgs.isEmpty()){
            	importPkgs += ",\n"; //$NON-NLS-1$
            }
            importPkgs += "*,com.sun.el;version=\"[2,3)\";resolution:=optional"; //$NON-NLS-1$
            importPkg.setValue(importPkgs);
            try (OutputStream out = new BufferedOutputStream(new FileOutputStream(pomFile))) {
                MavenPlugin.getMaven().writeModel(pomModel, out);
                project.getFile("pom.xml").refreshLocal(IResource.DEPTH_ZERO, monitor);
            }
        }
    }
}
