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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.graphiti.ui.editor.DiagramEditorInput;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.fusesource.ide.camel.editor.internal.CamelEditorUIActivator;
import org.fusesource.ide.camel.editor.internal.UIMessages;
import org.fusesource.ide.camel.model.service.core.model.ICamelModelListener;
import org.fusesource.ide.camel.model.util.Objects;
import org.fusesource.ide.commons.ui.UIHelper;
import org.fusesource.ide.foundation.ui.io.CamelXMLEditorInput;
import org.fusesource.ide.preferences.PreferenceManager;
import org.fusesource.ide.preferences.PreferencesConstants;


/**
 * @author lhein
 */
public class CamelEditor extends MultiPageEditorPart implements IResourceChangeListener,
																ITabbedPropertySheetPageContributor, 
																IPropertyChangeListener,
																ICamelModelListener {

	public static final String INTEGRATION_PERSPECTIVE_ID = "org.fusesource.ide.branding.perspective";
	
	public static final int DESIGN_PAGE_INDEX = 0;
	public static final int SOURCE_PAGE_INDEX = 1;
	public static final int GLOBAL_CONF_INDEX = 2;
	
	/** The text editor used in source page */
	private StructuredTextEditor sourceEditor;

	/** The graphical editor used in design page */
	private CamelDesignEditor designEditor;	
	
	/** The global configuration elements editor */
	private CamelGlobalConfigEditor globalConfigEditor;

	/** stores the last selection before saving and restores it after saving **/
	private ISelection savedSelection;
	private int lastActivePageIdx = DESIGN_PAGE_INDEX;
	private CamelXMLEditorInput editorInput;

	
	/**
	 * creates a new editor instance
	 */
	public CamelEditor() {
		super();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
		PreferenceManager.getInstance().getUnderlyingStorage().addPropertyChangeListener(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
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
				/*
				 * (non-Javadoc)
				 * 
				 * @see java.lang.Runnable#run()
				 */
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
				IEditorReference[] eds = getSite().getPage().getEditorReferences();
				for (IEditorReference er : eds) {
					IEditorPart editor = er.getEditor(false);
					if (editor != null) {
						IEditorInput editorInput = editor.getEditorInput();
						if (editorInput instanceof FileEditorInput && !((FileEditorInput) editorInput).getFile().exists()) {
							getSite().getPage().closeEditor(er.getEditor(false), false);
							if (er != null && er.getEditor(false) != null) {
								er.getEditor(false).dispose();
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
		createDesignPage(DESIGN_PAGE_INDEX);
		createSourcePage(SOURCE_PAGE_INDEX);
		createGlobalConfPage(GLOBAL_CONF_INDEX);

		IDocument document = getDocument();
		if (document == null) {
			throw new IllegalStateException("No Document available!");
		} else {
			document.addDocumentListener(new IDocumentListener() {

				/*
				 * (non-Javadoc)
				 * @see org.eclipse.jface.text.IDocumentListener#documentChanged(org.eclipse.jface.text.DocumentEvent)
				 */
				@Override
				public void documentChanged(DocumentEvent event) {
					//designEditor.onTextEditorPropertyChange();
				}

				/*
				 * (non-Javadoc)
				 * @see org.eclipse.jface.text.IDocumentListener#documentAboutToBeChanged(org.eclipse.jface.text.DocumentEvent)
				 */
				@Override
				public void documentAboutToBeChanged(DocumentEvent event) {
				}
			});
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
		try {
			globalConfigEditor = new CamelGlobalConfigEditor(this);
			IEditorInput editorInput = getEditorInput();
			addPage(index, globalConfigEditor, editorInput);
			setPageText(index, UIMessages.editorGlobalConfigurationPageTitle);
		} catch (PartInitException e) {
			ErrorDialog.openError(getSite().getShell(),
					"Error creating nested global configuration page", null, e.getStatus());
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		if (getActivePage() == DESIGN_PAGE_INDEX) {
			this.designEditor.getModel().saveChanges();
			getDesignEditor().doSave(monitor);
		} else if (getActivePage() == SOURCE_PAGE_INDEX) {
			this.sourceEditor.doSave(monitor);
		} else if (getActivePage() == GLOBAL_CONF_INDEX) {
			this.globalConfigEditor.doSave(monitor);
		} else {
			// unknown tab -> ignore
		}
		if (getEditorInput() instanceof CamelXMLEditorInput) {
			((CamelXMLEditorInput)getEditorInput()).onEditorInputSave();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	@Override
	public void doSaveAs() {
		if (getActivePage() == DESIGN_PAGE_INDEX) {
			this.designEditor.getModel().saveChanges();
			getDesignEditor().doSaveAs();
		} else if (getActivePage() == SOURCE_PAGE_INDEX) {
			this.sourceEditor.doSaveAs();			
		} else if (getActivePage() == GLOBAL_CONF_INDEX) {
			this.globalConfigEditor.doSaveAs();
		} else {
			// unknown tab -> ignore
		}
		
		// TODO: activate this for saving remote camel contexts via JMX
		// but check what that means for the stored temp file in CamelContextNode and its EditorInput
//		if (getEditorInput() instanceof CamelXMLEditorInput) {
//			((CamelXMLEditorInput)getEditorInput()).onEditorInputSave();
//		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#getTitle()
	 */
	@Override
	public String getTitle() {
		return this.designEditor != null && this.designEditor.getModel() != null ? this.designEditor.getModel().getResource().getName() : "noname";
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
		setPartName(fileName);
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
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.MultiPageEditorPart#setFocus()
	 */
	@Override
	public void setFocus() {
		super.setFocus();
		// open the properties view if not already open
		openPropertiesView();
	}

	/**
	 * opens the properties view if not already open
	 */
	private void openPropertiesView() {
		Display.getDefault().asyncExec(new Runnable() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see java.lang.Runnable#run()
			 */
			@Override
			public void run() {
				IWorkbench wb = PlatformUI.getWorkbench();
				if (wb != null) {
					IWorkbenchWindow wbw = wb.getActiveWorkbenchWindow();
					if (wbw != null) {
						IWorkbenchPage page = wbw.getActivePage();
						if (page != null) {
							try {
								if (page.findView(UIHelper.ID_PROPERTIES_VIEW) == null) {
									page.showView(UIHelper.ID_PROPERTIES_VIEW);
								}
							} catch (PartInitException ex) {
								CamelEditorUIActivator.pluginLog().logError(ex);
							}
						}
					}
				}
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.MultiPageEditorPart#pageChange(int)
	 */
	@Override
	protected void pageChange(int newPageIndex) {
//		try {
//			if (newPageIndex == SOURCE_PAGE_INDEX) {
//				boolean doPageChange = continueWithUnconnectedFigures();
//				if (doPageChange) {
//					if (sourceEditor == null) sourceEditor = new StructuredTextEditor();
//					if (lastActivePageIdx == DESIGN_PAGE_INDEX) updatedDesignPage();
//					if (lastActivePageIdx == GLOBAL_CONF_INDEX) updatedConfigPage();
//				} else {
//					setActivePage(DESIGN_PAGE_INDEX);
//					newPageIndex = DESIGN_PAGE_INDEX;
//				}
//			} else if (newPageIndex == DESIGN_PAGE_INDEX){
//				if (lastActivePageIdx == SOURCE_PAGE_INDEX) updatedTextPage();
//				if (lastActivePageIdx == GLOBAL_CONF_INDEX) updatedConfigPage();
//			} else {
//				// must be global config page
//				if (lastActivePageIdx == DESIGN_PAGE_INDEX) updatedDesignPage();
//				if (lastActivePageIdx == SOURCE_PAGE_INDEX) updatedTextPage();
//				globalConfigEditor.reload();
//			}
//		} finally {
//			this.lastActivePageIdx = newPageIndex;
//			super.pageChange(newPageIndex);
//		}
	}
	
	/**
	 * checks if there are unconnected figures and shows a warning in that case.
	 * if users ignore the warning the unconnected endpoints are lost
	 *  
	 * @return	true if user wants to preserve unconnected figures, otherwise (or if no unconnected figures) returns false
	 */
//	private boolean continueWithUnconnectedFigures() {
		// search for figures which have no connections - those would be lost when
		// saving or switching the tabs
//		boolean unconnectedNodeFound = false;
//
//		unconnectedNodeFound = findUnconnectedNode(designEditor.getModel().getChildren());
//		
//		if (!unconnectedNodeFound) return true;
//		
//		return MessageDialog.openQuestion(Display.getDefault().getActiveShell(), EditorMessages.unconnectedNodeFoundTitle, EditorMessages.unconnectedNodeFoundText);
//	}
	
//	/**
//	 * searches for unconnected nodes
//	 * 
//	 * @param nodes
//	 * @return
//	 */
//	private boolean findUnconnectedNode(List<AbstractNode> nodes) {
//		boolean unconnected = false;
//		for (AbstractNode node : nodes) {
//			if (node instanceof Route == false && node.getAllConnections().isEmpty()) return true;
//			if (!node.getChildren().isEmpty()) {
//				unconnected = findUnconnectedNode(node.getChildren());
//				if (unconnected) return true;
//			}
//		}
//		return false;
//	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor#getContributorId()
	 */
	@Override
	public String getContributorId() {
		return getSite().getId();
	}

//	/**
//	 * this method is responsible for updating the design editor before
//	 * displaying it
//	 */
//	void updatedDesignPage() {
//		updatedDesignPage(true);
//	}
//
//	/**
//	 * 
//	 * @param async
//	 */
//	void updatedDesignPage(boolean async) {
//		// we are switching to the source page so lets update the text editor's
//		// model with the latest diagram...
//		designEditor.runIfDiagramModified(new Runnable() {
//
//			@Override
//			public void run() {
//				IDocument document = getDocument();
//				if (document != null) {
//					String text = document.get();
//
//					// lets update the text with the latest from the model..
//					String newText = designEditor.updateEditorText(text);
//					if (!Objects.equal(newText, text)) {
//						// only update the document if its actually different
//						// to avoid setting the dirty flag unnecessarily
//						document.set(newText);
//					}
//				}
//			}
//		}, async);
//// TODO: why do we update the design editor if we switched to source editor?
//// 		 commented out the below line as it seems to make no sense here
////		this.designEditor.update();
//	}
//
//
//	/**
//	 * We are switching to the design page so lets update the design model if
//	 * the text has been changed
//	 */
//	private void updatedTextPage() {
//		designEditor.runIfTextModified(new Runnable() {
//
//			@Override
//			public void run() {
//				IDocument document = getDocument();
//				if (document != null) {
//					designEditor.clearCache();
//					String text = document.get();
//					Activator.getLogger().debug(
//							"Updating the design model from the updated text");
//					designEditor.loadEditorText(text);
//					designEditor.refreshDiagramContents();
//					designEditor.update();
//					designEditor.setFocus();
//					designEditor.fireModelChanged(); // will update the outline view
//				}
//
//			}
//		});
//	}
//	
//	private void updatedConfigPage() {
//		// we are switching from the config page so lets update the model for the other editors
//		// TODO: make changes in Config tab visible on other tabs
//	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.MultiPageEditorPart#getAdapter(java.lang.Class)
	 */
	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		if (adapter == TextEditor.class) {
			return this.sourceEditor;
		} else if (adapter == CamelDesignEditor.class) {
			return this.designEditor;
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

	public CamelDesignEditor getDesignEditor() {
		return designEditor;
	}

	public CamelGlobalConfigEditor getGlobalConfigEditor() {
		return globalConfigEditor;
	}
	
//	public CamelModelElement getSelectedNode() {
//		return designEditor.getSelectedNode();
//	}

//	public void setSelectedNode(AbstractNode newSelection) {
//		designEditor.setSelectedNode(newSelection);
//	}

//	public RouteContainer getModel() {
//		return designEditor.getModel();
//	}
//
//	public RouteSupport getSelectedRoute() {
//		return designEditor.getSelectedRoute();
//	}
//
//	public void setSelectedRoute(RouteSupport selectedRoute) {
//		designEditor.setSelectedRoute(selectedRoute);
//	}

	/**
	 * returns the document
	 * 
	 * @return
	 */
	public IDocument getDocument() {
		Object element = sourceEditor.getEditorInput();
		IDocumentProvider documentProvider = sourceEditor.getDocumentProvider();
		if (documentProvider != null) {
			IDocument document = documentProvider.getDocument(element);
			return document;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.MultiPageEditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
System.err.println("CamelEditor.init(" + input.getClass().getName() + ")");
		CamelXMLEditorInput camelInput = null;
		
		if (input instanceof IFileEditorInput) {
			camelInput = new CamelXMLEditorInput(((IFileEditorInput)input).getFile());
		} else if (input instanceof DiagramEditorInput) {
			IFile f = (IFile)((DiagramEditorInput)input).getAdapter(IFile.class);
			camelInput = new CamelXMLEditorInput(f);
		} else if (input instanceof CamelXMLEditorInput) {
			camelInput = (CamelXMLEditorInput)input;
		} else {
			throw new PartInitException("Unknown input type: " + input.getClass().getName());
		}
		super.init(site, camelInput);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#setInput(org.eclipse.ui.IEditorInput)
	 */
	@Override
	protected void setInput(IEditorInput input) {
		System.err.println("CamelEditor.setInput(" + input.getClass().getName() + ")");
		super.setInput(input);
		setPartName(input.getName());
	}
	
	/**
	 * 
	 */
	public void switchToDesignEditor() {
		// lets switch async just in case we've not created the page yet
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
//				updatedDesignPage();
				setActiveEditor(getDesignEditor());
				setActivePage(DESIGN_PAGE_INDEX);
				getDesignEditor().setFocus();
			}
		});
	}

	/**
	 * 
	 */
	public void switchToSourceEditor() {
		// lets switch async just in case we've not created the page yet
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
//				updatedDesignPage();
				setActiveEditor(getSourceEditor());
				setActivePage(SOURCE_PAGE_INDEX);
				getSourceEditor().setFocus();
			}
		});
	}

	/**
	 * 
	 */
	public void switchToGlobalConfigEditor() {
		// lets switch async just in case we've not created the page yet
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				setActiveEditor(getGlobalConfigEditor());
				setActivePage(GLOBAL_CONF_INDEX);
				getGlobalConfigEditor().setFocus();
			}
		});
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(PreferencesConstants.EDITOR_PREFER_ID_AS_LABEL)) {
			// user switched the displaytext logic flag - refresh diagram and outline
//			designEditor.update();
		} else if (event.getProperty().equals(PreferencesConstants.EDITOR_LAYOUT_ORIENTATION)) {
			// user switched direction of diagram layout - relayout the diagram
//			designEditor.autoLayoutRoute();
		} else if (event.getProperty().equals(PreferencesConstants.EDITOR_GRID_VISIBILITY)) {
			// user switched grid visibility
//			DiagramUtils.setGridVisible((Boolean)event.getNewValue(), designEditor);
		} else if (event.getProperty().equals(PreferencesConstants.EDITOR_TEXT_COLOR) ||
				   event.getProperty().equals(PreferencesConstants.EDITOR_CONNECTION_COLOR) ||
				   event.getProperty().equals(PreferencesConstants.EDITOR_FIGURE_BG_COLOR) ||
				   event.getProperty().equals(PreferencesConstants.EDITOR_FIGURE_FG_COLOR)) {
//			designEditor.getDiagramBehavior().refresh();
		} else if (event.getProperty().equals(PreferencesConstants.EDITOR_GRID_COLOR)) {
//			designEditor.setupGridVisibilityAsync();
		} 	
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.model.ICamelModelListener#modelChanged()
	 */
	@Override
	public void modelChanged() {
		// we only update if the correct editor tab is selected
		if (getActivePage() != SOURCE_PAGE_INDEX) return;
		Display.getDefault().asyncExec(new Runnable() {
			/*
			 * (non-Javadoc)
			 * @see java.lang.Runnable#run()
			 */
			@Override
			public void run() {
				IDocument document = getDocument();
				if (document != null) {
					String text = document.get();

					// lets update the text with the latest from the model..
					String newText = designEditor.getModel().getDocumentAsXML();
					if (!Objects.equal(newText, text)) {
						// only update the document if its actually different
						// to avoid setting the dirty flag unnecessarily
						document.set(newText);
					}
				}
			}
		});
	}
}
