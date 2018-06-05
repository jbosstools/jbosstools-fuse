/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.editor;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.graphiti.ui.editor.DiagramEditorInput;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.fusesource.ide.camel.editor.commands.DiagramOperations;
import org.fusesource.ide.camel.editor.commands.ImportCamelContextElementsCommand;
import org.fusesource.ide.camel.editor.globalconfiguration.CamelGlobalConfigEditor;
import org.fusesource.ide.camel.editor.internal.CamelEditorUIActivator;
import org.fusesource.ide.camel.editor.internal.UIMessages;
import org.fusesource.ide.camel.editor.restconfiguration.RestConfigEditor;
import org.fusesource.ide.camel.editor.utils.DiagramUtils;
import org.fusesource.ide.camel.model.service.core.io.CamelIOHandler;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelContextElement;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.foundation.ui.io.CamelXMLEditorInput;
import org.fusesource.ide.foundation.ui.util.Selections;
import org.fusesource.ide.preferences.PreferenceManager;
import org.fusesource.ide.preferences.PreferencesConstants;


/**
 * @author lhein
 */
public class CamelEditor extends MultiPageEditorPart implements IResourceChangeListener,
																ITabbedPropertySheetPageContributor, 
																IDocumentListener,
																IPropertyChangeListener {

	public static final String INTEGRATION_PERSPECTIVE_ID = "org.fusesource.ide.branding.perspective";
	
	public static final int DESIGN_PAGE_INDEX = 0;
	public static final int SOURCE_PAGE_INDEX = 1;
	public static final int GLOBAL_CONF_INDEX = 2;
	public static final int REST_CONF_INDEX = 3;
	
	/** The text editor used in source page */
	private StructuredTextEditor sourceEditor;

	/** The graphical editor used in design page */
	private CamelDesignEditor designEditor;	
	
	/** The global configuration elements editor */
	private CamelGlobalConfigEditor globalConfigEditor;

	/** stores the last selection before saving and restores it after saving **/
	private ISelection savedSelection;
	private int lastActivePageIdx = DESIGN_PAGE_INDEX;
	
	/** the editor input **/
	private CamelXMLEditorInput editorInput;

	/** contains the last xml validation error or an empty string if no error **/
	private String lastError = "";
	
	/** the editor dirty flag **/
	private boolean dirtyFlag = false;
	
	private boolean disableDirtyFlag = false;
	
	/** 
	 * this flag is used when invalid xml is detected in source and then 
	 * a tab switch is performed. if the user ignores the warning all changes 
	 * in the source editor are lost, otherwise he will be set back into the 
	 * source editor and the changes are still there.
	 */
	private boolean rollBackActive = false;
	
	private RestConfigEditor restConfigEditor;
	
	/**
	 * creates a new editor instance
	 */
	public CamelEditor() {
		super();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
		PreferenceManager.getInstance().getUnderlyingStorage().addPropertyChangeListener(this);
	}
	
	@Override
	public void resourceChanged(final IResourceChangeEvent event) {
		switch (event.getType()) {
		case IResourceChangeEvent.POST_CHANGE:
		case IResourceChangeEvent.PRE_DELETE:
			// file has been deleted...
			closeEditorsWithoutValidInput();
			break;
		case IResourceChangeEvent.PRE_CLOSE:
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					IWorkbenchPage[] pages = getSite().getWorkbenchWindow().getPages();
					for (int i = 0; i < pages.length; i++) {
						IEditorInput editorInput = sourceEditor.getEditorInput();
						if (editorInput instanceof FileEditorInput && ((FileEditorInput) editorInput).getFile().getProject().equals(event.getResource())) {
							IWorkbenchPage page = pages[i];
							IEditorPart editorPart = page.findEditor(editorInput);
							page.closeEditor(editorPart, true);
						}
					}
				}
			});
			break;
		default:
			// do nothing
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.MultiPageEditorPart#setFocus()
	 */
	@Override
	public void setFocus() {
		super.setFocus();
		// this will assure the diagram is alway layed out correctly which
		// wasn't always the case (for instance when adding a new data 
		// transformation endpoint which opened another editor underneath
		if (designEditor != null && designEditor.getModel() != null){
			DiagramOperations.layoutDiagram(designEditor);
		}
	}
	
	/**
	 * closes all editors with no valid inputs
	 */
	protected void closeEditorsWithoutValidInput() {
		Display.getDefault().asyncExec(new Runnable() {
			/*
			 * (non-Javadoc)
			 * @see java.lang.Runnable#run()
			 */
			@Override
			public void run() {
				// close all editors without valid input
				final IWorkbenchPartSite site = getSite();
				if (site != null) {
					final IWorkbenchPage page = site.getPage();
					if (page != null) {
						IEditorReference[] eds = page.getEditorReferences();
						for (IEditorReference er : eds) {
							IEditorPart editor = er.getEditor(false);
							if (editor != null) {
								IEditorInput editorInput = editor.getEditorInput();
								if (editorInput instanceof CamelXMLEditorInput && !((CamelXMLEditorInput) editorInput).getCamelContextFile().exists()) {
									page.closeEditor(er.getEditor(false), false);
									if (er.getEditor(false) != null) {
										er.getEditor(false).dispose();
									}
								}
							}
						}
					}
				}
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.MultiPageEditorPart#createPages()
	 */
	@Override
	protected void createPages() {
		stopDirtyListener();

		createDesignPage(DESIGN_PAGE_INDEX);
		createSourcePage(SOURCE_PAGE_INDEX);
		createGlobalConfPage(GLOBAL_CONF_INDEX);
		createRestConfPage(REST_CONF_INDEX);

		IDocument document = getDocument();
		if (document == null) {
			throw new IllegalStateException("No Document available!");
		} else {
			document.addDocumentListener(this);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.MultiPageEditorPart#getActiveEditor()
	 */
	@Override
	public IEditorPart getActiveEditor() {
		return super.getActiveEditor();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.IDocumentListener#documentAboutToBeChanged(org.eclipse.jface.text.DocumentEvent)
	 */
	@Override
	public void documentAboutToBeChanged(DocumentEvent event) {
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.IDocumentListener#documentChanged(org.eclipse.jface.text.DocumentEvent)
	 */
	@Override
	public void documentChanged(DocumentEvent event) {
		if (getActivePage() == SOURCE_PAGE_INDEX) {
			setDirtyFlag(true);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.MultiPageEditorPart#isDirty()
	 */
	@Override
	public boolean isDirty() {
		return this.dirtyFlag;
	}
	
	/**
	 * @param dirtyFlag the dirtyFlag to set
	 */
	public void setDirtyFlag(boolean dirtyFlag) {
		if (!disableDirtyFlag) {
			this.dirtyFlag = dirtyFlag;
			firePropertyChange(IEditorPart.PROP_DIRTY);
		}
	}
	
	public void stopDirtyListener() {
		this.disableDirtyFlag = true;
	}
	
	public void startDirtyListener() {
		this.disableDirtyFlag = false;
	}
	
	/**
	 * creates the source page
	 * 
	 * @param index
	 *            the page index
	 */
	private void createSourcePage(int index) {
		try {
			sourceEditor = new StructuredTextEditor();
			IEditorInput editorInput = designEditor.asFileEditorInput(getEditorInput());
			addPage(index, sourceEditor, editorInput);
			setPageText(index, UIMessages.editorSourcePageTitle);
		} catch (PartInitException e) {
			ErrorDialog.openError(getSite().getShell(),
					"Error creating nested text editor", null, e.getStatus());
		}
	}

	/**
	 * creates the design page
	 * 
	 * @param index
	 *            the page index
	 */
	private void createDesignPage(int index) {
		try {
			designEditor = new CamelDesignEditor(this);
			IEditorInput editorInput = getEditorInput();
			addPage(index, designEditor, editorInput);
			setPageText(index, UIMessages.editorDesignPageTitle);
		} catch (PartInitException e) {
			ErrorDialog.openError(getSite().getShell(),
					"Error creating nested design editor", null, e.getStatus());
		}
	}
	
	/**
	 * creates the global configuration page
	 * 
	 * @param index
	 * 			  the page index
	 */
	private void createGlobalConfPage(int index) {
		CamelFile model = designEditor.getModel();
		if (model != null && model.getRouteContainer() instanceof CamelContextElement) {
			try {
				globalConfigEditor = new CamelGlobalConfigEditor(this);
				addPage(index, globalConfigEditor, editorInput);
				setPageText(index, UIMessages.editorGlobalConfigurationPageTitle);
			} catch (PartInitException e) {
				ErrorDialog.openError(getSite().getShell(),
						"Error creating nested global configuration page", null, e.getStatus());
			}
		}
	}
	
	private void createRestConfPage(int index) {
		CamelFile model = designEditor.getModel();
		if (model != null && model.getRouteContainer() instanceof CamelContextElement) {
			try {
				restConfigEditor = new RestConfigEditor(this);
				addPage(index, restConfigEditor, editorInput);
				setPageText(index, "REST");
			} catch (PartInitException e) {
				ErrorDialog.openError(getSite().getShell(),
						"Error creating nested REST configuration page", null, e.getStatus());
			}
		}
	}

	
	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		try {
			if (getActivePage() == DESIGN_PAGE_INDEX || 
				getActivePage() == GLOBAL_CONF_INDEX) {
				// we need to generate the source from our model
				updateSourceFromModel(false);
				this.sourceEditor.doSave(monitor);
			} else if (getActivePage() == SOURCE_PAGE_INDEX) {
				this.sourceEditor.doSave(monitor);
			} else {
				// unknown tab -> ignore
			}
			if (getEditorInput() instanceof CamelXMLEditorInput) {
				((CamelXMLEditorInput)getEditorInput()).onEditorInputSave();
			}
			setDirtyFlag(false);
		} finally {
			refreshProject(monitor);
		}
	}
	
	@Override
	public void doSaveAs() {
		try {
			if (getActivePage() == DESIGN_PAGE_INDEX || 
				getActivePage() == GLOBAL_CONF_INDEX) {
				// we need to generate the source from our model
				updateSourceFromModel(false); 
				this.sourceEditor.doSaveAs();
			} else if (getActivePage() == SOURCE_PAGE_INDEX) {
				this.sourceEditor.doSaveAs();			
			} else {
				// unknown tab -> ignore
			}
			
			// TODO: activate this for saving remote camel contexts via JMX
			// but check what that means for the stored temp file in CamelContextNode and its EditorInput
//			if (getEditorInput() instanceof CamelXMLEditorInput) {
//				((CamelXMLEditorInput)getEditorInput()).onEditorInputSave();
//			}		
			setDirtyFlag(false);
		} finally {
			refreshProject(new NullProgressMonitor());
		}
	}
	
	@Override
	public String getTitle() {
		return this.editorInput != null ? this.editorInput.getName() : "";
	}
	
	private void refreshProject(IProgressMonitor monitor) {
		IProject prj = this.editorInput.getCamelContextFile().getProject();
		try {
			prj.refreshLocal(IProject.DEPTH_INFINITE, monitor);
		} catch (CoreException ex) {
			CamelEditorUIActivator.pluginLog().logError("Unable to refresh project after saving...", ex);
		}
	}
	
	/**
	 * saves the current selection in the active editor
	 */
	protected void saveSelection() {
		this.savedSelection = getActiveEditor().getEditorSite()
				.getSelectionProvider().getSelection();
	}

	/**
	 * restores the selection of the active editor
	 */
	protected void restoreSelection() {
		getActiveEditor().getEditorSite().getSelectionProvider()
		.setSelection(this.savedSelection);
	}

	public void onFileLoading(String fileName) {
		setPartName(this.editorInput != null ? this.editorInput.getName() : "");
	}
		
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.MultiPageEditorPart#dispose()
	 */
	@Override
	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		PreferenceManager.getInstance().getUnderlyingStorage().removePropertyChangeListener(this);
		super.dispose();
		editorInput.dispose();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.MultiPageEditorPart#pageChange(int)
	 */
	@Override
	protected void pageChange(int newPageIndex) {
		if (newPageIndex == SOURCE_PAGE_INDEX) {
			if (sourceEditor == null) {
				sourceEditor = new StructuredTextEditor();
			}
			if (!rollBackActive) {
				updateSourceFromModel();
			} else {
				rollBackActive = false;
			}
			this.lastActivePageIdx = newPageIndex;
			super.pageChange(newPageIndex);
		} else if (newPageIndex == DESIGN_PAGE_INDEX || newPageIndex == GLOBAL_CONF_INDEX || newPageIndex == REST_CONF_INDEX) {
			if (lastActivePageIdx == SOURCE_PAGE_INDEX) {
				IDocument document = getDocument();
				if (document != null) {
					String text = document.get();
					if (!isValidXML(text)) {
						// invalid XML -> could result in data loss...
						MessageDialog.openError(getSite().getShell(), UIMessages.failedXMLValidationTitle, NLS.bind(UIMessages.failedXMLValidationText, lastError));
						rollBackActive = true;
						newPageIndex = SOURCE_PAGE_INDEX;
						setActivePage(SOURCE_PAGE_INDEX);
						super.pageChange(newPageIndex);
						getDocument().set(text);
					} else {
						lastError = "";
						if (newPageIndex == GLOBAL_CONF_INDEX) {
							updateModelFromSource();
							globalConfigEditor.reload();
						} else if (newPageIndex == REST_CONF_INDEX) {
							updateModelFromSource(false);
							restConfigEditor.reload();
						} else if (newPageIndex == DESIGN_PAGE_INDEX) {
							updateModelFromSource();
							designEditor.switchContainer(); // needed to fix sync issue between props view and selected context element
							designEditor.refreshOutlineView();
						}
						this.lastActivePageIdx = newPageIndex;
						super.pageChange(newPageIndex);
					}
				}
			} else {
				if (newPageIndex == GLOBAL_CONF_INDEX) {
					globalConfigEditor.reload();
				} else if (newPageIndex == REST_CONF_INDEX) {
					restConfigEditor.reload();
				} else {
					designEditor.update();
					designEditor.refreshOutlineView();
				}
			}
		}
	}
	
	@Override
	public String getContributorId() {
		return getSite().getId();
	}

	/**
	 * this method is responsible for updating the design editor before
	 * displaying it
	 */
	void updateSourceFromModel() {
		updateSourceFromModel(true);
	}

	/**
	 * 
	 * @param async
	 */
	void updateSourceFromModel(boolean async) {
		// we are switching to the source page so lets update the text editor's
		// model with the latest diagram...
		Runnable r = new Runnable() {
			@Override
			public void run() {
				try {
					stopDirtyListener();
					IDocument document = getDocument();
					if (document != null) {
						String text = document.get();
						CamelFile model = designEditor.getModel();
						if (model != null) {
							String newText = model.getDocumentAsXML();
							if (!text.equals(newText)) {
								// only update the document if its actually
								// different
								// to avoid setting the dirty flag unnecessarily
								boolean designEditorWasDirty = designEditor.isDirty();
								document.set(newText);
								if (!designEditorWasDirty) {
									// stopping dirty listener wasn't enough
									doSave(new NullProgressMonitor());
								}
							}
						}
					}
				} finally {
					startDirtyListener();
				}
			}
		};
		
		if (async) {
			Display.getDefault().asyncExec(r);
		} else {
			Display.getDefault().syncExec(r);
		}
	}

	/**
	 * this method is responsible for updating the model from the XML source
	 */
	void updateModelFromSource() {
		updateModelFromSource(true);
	}

	/**
	 * We are switching from the text page so lets update the model if
	 * the text has been changed
	 */
	private void updateModelFromSource(boolean async) {
		Runnable r = new Runnable() {
			@Override
			public void run() {
				stopDirtyListener();
				// reload model
				String text = getDocument().get();
				if (designEditor != null) {
					designEditor.clearCache();
					if (designEditor.getModel() == null) {
						// in this case we lost our model due to invalid xml and
						// we need to regenerate it
						CamelIOHandler ioHandler = new CamelIOHandler();
						designEditor.setModel(ioHandler.reloadCamelModel(text, new NullProgressMonitor(), getCamelXMLInput().getCamelContextFile()));	
					} else {
						designEditor.setModel(designEditor.getModel().reloadModelFromXML(text));
					}
					// add the diagram contents
					if (designEditor.getModel() != null) {
						ImportCamelContextElementsCommand importCommand = new ImportCamelContextElementsCommand(designEditor, designEditor.getEditingDomain(),
								(AbstractCamelModelElement) (getDesignEditor().getSelectedContainer() != null ? getDesignEditor().getSelectedContainer() : designEditor.getModel()), null);
						designEditor.getEditingDomain().getCommandStack().execute(importCommand);
						designEditor.initializeDiagram(importCommand.getDiagram());
						designEditor.refreshDiagramContents(null);
					}
				}
			}
		};
		
		if (async) {
			Display.getDefault().asyncExec(r);
		} else {
			Display.getDefault().syncExec(r);
		}
	}
	
	/**
	 * checks if the text is xml compliant
	 * 
	 * @param text
	 * @return	true if valid, otherwise false
	 */
	private boolean isValidXML(String text) {
		try {
			lastError = "";
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.parse(new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8)));
		} catch (Exception ex) {
			lastError = ex.getMessage();				
			return false;
		}
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.MultiPageEditorPart#getAdapter(java.lang.Class)
	 */
	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		if (adapter == TextEditor.class) {
			return this.sourceEditor;
		} else if (adapter == CamelFile.class) {
			return this.designEditor.getModel();
		} else if (adapter == CamelDesignEditor.class) {
			return this.designEditor;
		} else if (adapter == CamelGlobalConfigEditor.class) {
			return this.globalConfigEditor;
		} else if (adapter == RestConfigEditor.class) {
			return this.restConfigEditor;
		} else if (adapter == IPropertySheetPage.class) {
			return new TabbedPropertySheetPage(this);
//		} else if (adapter == ActionRegistry.class) {
//			// this is needed otherwise switching between source
//			// and design tab sometimes throws NPE
//			return designEditor.getActionRegistry();
		} else if (adapter == IDocumentProvider.class) {
			IEditorInput editorInput = getEditorInput();
			if (editorInput != null) {
				Object answer = editorInput.getAdapter(adapter);
				if (answer != null) {
					return answer;
				}
			}
		} else if (adapter == IGotoMarker.class) {
			return new GoToMarkerForCamelEditor(this);
		}
		return super.getAdapter(adapter);
	}

	/**
	 * returns the source xml editor
	 * 
	 * @return
	 */
	public StructuredTextEditor getSourceEditor() {
		return sourceEditor;
	}

	public RestConfigEditor getRestEditor() {
		return restConfigEditor;
	}

	public CamelDesignEditor getDesignEditor() {
		return designEditor;
	}

	public CamelGlobalConfigEditor getGlobalConfigEditor() {
		return globalConfigEditor;
	}
	
	/**
	 * returns the document
	 * 
	 * @return
	 */
	public IDocument getDocument() {
		Object element = sourceEditor.getEditorInput();
		IDocumentProvider documentProvider = sourceEditor.getDocumentProvider();
		if (documentProvider != null) {
			return documentProvider.getDocument(element);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.MultiPageEditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		this.editorInput = null;
		
		if (input instanceof IFileEditorInput) {
			this.editorInput = new CamelXMLEditorInput(((IFileEditorInput)input).getFile(), null);
		} else if (input instanceof DiagramEditorInput) {
			IFile f = (IFile)((DiagramEditorInput)input).getAdapter(IFile.class);
			this.editorInput = new CamelXMLEditorInput(f, null);
		} else if (input instanceof CamelXMLEditorInput) {
			this.editorInput = (CamelXMLEditorInput)input;
		} else {
			throw new PartInitException("Unknown input type: " + input.getClass().getName());
		}
		super.init(site, this.editorInput);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.MultiPageEditorPart#handlePropertyChange(int)
	 */
	@Override
	protected void handlePropertyChange(int propertyId) {
		super.handlePropertyChange(propertyId);
		
		// the following is needed otherwise we can't get back displaying the full context when clicking the context file once we showed a route
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IViewPart viewPart = page.findView("org.eclipse.ui.navigator.ProjectExplorer");
		if (viewPart != null) {
			ISelectionProvider selProvider = viewPart.getSite().getSelectionProvider();
			Object o = Selections.getFirstSelection(selProvider.getSelection());
			if (o != null && o instanceof IResource) {
				IResource res = (IResource)o;
				if (res.getLocationURI().getPath().equals(this.editorInput.getCamelContextFile().getLocationURI().getPath()) && editorInput.getSelectedContainerId() != null && editorInput.getSelectedContainerId().equals(getDesignEditor().getModel().getRouteContainer().getId()) == false) {
					editorInput.setSelectedContainerId(null);
					getDesignEditor().setSelectedContainer(null);
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#setInput(org.eclipse.ui.IEditorInput)
	 */
	@Override
	protected void setInput(IEditorInput input) {
		super.setInput(input);
		if (input instanceof CamelXMLEditorInput && !input.equals(this.editorInput)) {
			this.editorInput = (CamelXMLEditorInput)input;
		}
		setPartName(input.getName());
	}
    
	/* (non-Javadoc)
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		String eventProperty = event.getProperty();
		if (eventProperty.equals(PreferencesConstants.EDITOR_PREFER_ID_AS_LABEL)) {
			// user switched the display text logic flag - refresh diagram and outline
			designEditor.update();
		} else if (eventProperty.equals(PreferencesConstants.EDITOR_PREFERRED_LABEL)) {
			// user defined own labels
			designEditor.update();
		} else if (eventProperty.equals(PreferencesConstants.EDITOR_LAYOUT_ORIENTATION)) {
			// user switched direction of diagram layout - relayout the diagram
			designEditor.autoLayoutRoute();
		} else if (eventProperty.equals(PreferencesConstants.EDITOR_GRID_VISIBILITY)) {
			// user switched grid visibility
			DiagramUtils.setGridVisible((Boolean)event.getNewValue(), designEditor);
		} else if (eventProperty.equals(PreferencesConstants.EDITOR_TEXT_COLOR) ||
				   eventProperty.equals(PreferencesConstants.EDITOR_CONNECTION_COLOR) ||
				   eventProperty.equals(PreferencesConstants.EDITOR_FIGURE_BG_COLOR) ||
				   eventProperty.equals(PreferencesConstants.EDITOR_FIGURE_FG_COLOR)) {
			designEditor.getDiagramBehavior().refresh();
		} else if (eventProperty.equals(PreferencesConstants.EDITOR_GRID_COLOR)) {
			designEditor.setupGridVisibilityAsync();
		}
	}
	
	/**
	 * switches to the design editor tab
	 */
	public void switchToDesignEditor() {
		// lets switch async just in case we've not created the page yet
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				setActiveEditor(getDesignEditor());
				setActivePage(DESIGN_PAGE_INDEX);
				getDesignEditor().setFocus();
			}
		});
	}
	
	/**
	 * switches to the source editor tab
	 */
	public void switchToSourceEditor() {
		// lets switch async just in case we've not created the page yet
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				setActiveEditor(getSourceEditor());
				setActivePage(SOURCE_PAGE_INDEX);
				getSourceEditor().setFocus();
			}
		});
	}
	
	/**
	 * returns the editor input
	 * 
	 * @return	the editor input
	 */
	public CamelXMLEditorInput getCamelXMLInput() {
		return this.editorInput;
	}
	
	/**
	 * updates the currently selected container id in the editor input
	 * 
	 * @param containerId
	 */
	public void updateSelectedContainer(String containerId) {
		this.editorInput.setSelectedContainerId(containerId);
	}

	public void updatePartName() {
		setPartName(getTitle());
	}
	
	public CTabFolder getTabbedFolder() {
		return (CTabFolder)super.getContainer();
	}
}
