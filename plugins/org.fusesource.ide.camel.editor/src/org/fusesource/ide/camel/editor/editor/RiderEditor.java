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

package org.fusesource.ide.camel.editor.editor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.part.MultiPageEditorSite;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.wizards.newresource.BasicNewFileResourceWizard;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.fusesource.ide.camel.editor.Activator;
import org.fusesource.ide.camel.editor.IPrefersPerspective;
import org.fusesource.ide.camel.editor.Messages;
import org.fusesource.ide.camel.editor.utils.DiagramUtils;
import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.RouteContainer;
import org.fusesource.ide.camel.model.RouteSupport;
import org.fusesource.ide.camel.model.io.ICamelEditorInput;
import org.fusesource.ide.camel.model.util.Objects;
import org.fusesource.ide.commons.ui.UIConstants;
import org.fusesource.ide.commons.ui.Workbenches;
import org.fusesource.ide.preferences.PreferenceManager;
import org.fusesource.ide.preferences.PreferencesConstants;


/**
 * @author lhein
 */
public class RiderEditor extends MultiPageEditorPart implements IResourceChangeListener,
ITabbedPropertySheetPageContributor, IPrefersPerspective, IPropertyChangeListener {

	public static final String RIDER_PERSPECTIVE_ID = "org.fusesource.ide.branding.perspective";
	
	public static final int DESIGN_PAGE_INDEX = 0;
	public static final int SOURCE_PAGE_INDEX = 1;
	
	/** The text editor used in source page */
	private StructuredTextEditor sourceEditor;

	/** The graphical editor used in design page */
	private RiderDesignEditor designEditor;
	

	/** stores the last selection before saving and restores it after saving **/
	private ISelection savedSelection;

	private RiderDesignEditorData designEditorData = new RiderDesignEditorData();
	@SuppressWarnings("unused")
	private int designPageIndex;

	/**
	 * creates a new editor instance
	 */
	public RiderEditor() {
		super();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
		PreferenceManager.getInstance().getUnderlyingStorage().addPropertyChangeListener(this);
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
				/*
				 * (non-Javadoc)
				 * 
				 * @see java.lang.Runnable#run()
				 */
				@Override
				public void run() {
					IWorkbenchPage[] pages = getSite().getWorkbenchWindow()
							.getPages();
					for (int i = 0; i < pages.length; i++) {
						IEditorInput editorInput = sourceEditor.getEditorInput();
						if (editorInput instanceof FileEditorInput && ((FileEditorInput) editorInput)
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
		default:
			// do nothing
		}
	}

	protected void closeEditorsWithoutValidInput() {
		Display.getDefault().asyncExec(new Runnable() {
			/*
			 * (non-Javadoc)
			 * 
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
						if (editorInput instanceof FileEditorInput
								&& !((FileEditorInput) editorInput).getFile().exists()) {
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
	 * 
	 * @see org.eclipse.ui.part.MultiPageEditorPart#createPages()
	 */
	@Override
	protected void createPages() {
		createDesignPage(DESIGN_PAGE_INDEX);
		createSourcePage(SOURCE_PAGE_INDEX);

		IDocument document = getDocument();
		if (document == null) {
			throw new IllegalStateException("No Document available!");
		} else {
			document.addDocumentListener(new IDocumentListener() {

				/*
				 * (non-Javadoc)
				 * 
				 * @see
				 * org.eclipse.jface.text.IDocumentListener#documentChanged(
				 * org.eclipse.jface.text.DocumentEvent)
				 */
				@Override
				public void documentChanged(DocumentEvent event) {
					designEditor.onTextEditorPropertyChange();
				}

				/*
				 * (non-Javadoc)
				 * 
				 * @see
				 * org.eclipse.jface.text.IDocumentListener#documentAboutToBeChanged
				 * (org.eclipse.jface.text.DocumentEvent)
				 */
				@Override
				public void documentAboutToBeChanged(DocumentEvent event) {
				}
			});
		}
	}

	/*
	 * (non-Javadoc)
	 * 
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
			IEditorInput editorInput = getEditorInput();
			if (editorInput instanceof ICamelEditorInput) {
				ICamelEditorInput camelEditorInput = (ICamelEditorInput) editorInput;
				editorInput = camelEditorInput.getFileEditorInput();
			}
			addPage(index, sourceEditor, editorInput);
			setPageText(index, Messages.editorSourcePageTitle);
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
			designEditor = new RiderDesignEditor(this);
			this.designPageIndex = index;
			IEditorInput editorInput = getEditorInput();
			addPage(index, designEditor, editorInput);
			setPageText(index, Messages.editorDesignPageTitle);
		} catch (PartInitException e) {
			ErrorDialog.openError(getSite().getShell(),
					"Error creating nested design editor", null, e.getStatus());
		}
	}


	private Object getField(Object owner, String name, Class<?> aClass) {
		try {
			return Objects.getField(owner, name, aClass);
		} catch (Exception e) {
			Activator.getLogger().warning("Failed to access field: " + name + ". Reason: " + e, e);
			return null;
		}
	}

	private void disposePart(final IWorkbenchPart part) {
		SafeRunner.run(new ISafeRunnable() {
			@Override
			public void run() {
				IWorkbenchPartSite partSite = part.getSite();
				part.dispose();
				if (partSite instanceof MultiPageEditorSite) {
					((MultiPageEditorSite) partSite).dispose();
				}
			}

			@Override
			public void handleException(Throwable e) {
				// Exception has already being logged by Core. Do nothing.
			}
		});
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.
	 * IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		IEditorPart activeEditor = getActiveEditor();
		if (activeEditor != null) {
			if (activeEditor != designEditor) {
				monitor = designEditor.createSaveProgressMonitorWrapper(monitor);
			}
			activeEditor.doSave(monitor);
		} else {
			saveSelection();

			IEditorPart editor1 = getEditor(DESIGN_PAGE_INDEX);
			editor1.doSave(monitor);
			IEditorPart editor2 = getEditor(SOURCE_PAGE_INDEX);
			editor2.doSave(monitor);

			restoreSelection();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	@Override
	public void doSaveAs() {
		IEditorPart activeEditor = getActiveEditor();
		if (activeEditor != null) {
			IEditorInput oldInput = getEditorInput();
			if (oldInput instanceof ICamelEditorInput) {
				final ICamelEditorInput camelInput = (ICamelEditorInput) oldInput;
				Shell shell = getSite().getShell();
				BasicNewFileResourceWizard wizard = new BasicNewFileResourceWizard() {
					@Override
					public void addPages() {
						super.addPages();
						IWizardPage page = getPage("newFilePage1");
						if (page instanceof WizardNewFileCreationPage) {
							final WizardNewFileCreationPage fileCreationPage = (WizardNewFileCreationPage) page;
							fileCreationPage.setTitle("Save as new file");
							fileCreationPage.setDescription("Choose the folder and file name to save the routes as a file");
							IFile lastSaveAsFile = camelInput.getLastSaveAsFile();
							if (lastSaveAsFile != null) {
								fileCreationPage.setContainerFullPath(lastSaveAsFile.getFullPath());
								fileCreationPage.setFileName(lastSaveAsFile.getName());

								Display.getDefault().asyncExec(new Runnable() {
									@Override
									public void run() {
										// lets force a validation
										Event dummyEvent = new Event();
										fileCreationPage.handleEvent(dummyEvent);
									}
								});
							} else {
								fileCreationPage.setFileName("routes.xml");
							}

						}
					}

					@Override
					protected void selectAndReveal(IResource newResource) {
						// lets now write to the file!!!
						if (newResource instanceof IFile) {
							IFile file = (IFile) newResource;
							camelInput.setLastSaveAsFile(file);
//							camelInput.setLastSaveAsFile(file);
							saveAsFile(file);
						}
						super.selectAndReveal(newResource);
					}

				};
				IWorkbench workbench = Workbenches.getActiveWorkbench();
				IStructuredSelection selection = new StructuredSelection();
				wizard.setForcePreviousAndNextButtons(false);
				wizard.setWindowTitle("Save as new file");
				wizard.init(workbench, selection);
				WizardDialog dialog = new WizardDialog(shell, wizard);
				dialog.setTitle("Save as new file");
				dialog.open();
				return;
			} else {
				activeEditor.doSaveAs();
			}
		} else {
			saveSelection();
			doSaveAs(getEditor(DESIGN_PAGE_INDEX));
			doSaveAs(getEditor(SOURCE_PAGE_INDEX));
			restoreSelection();
		}
	}

	protected void saveAsFile(IFile file) {
		InputStream source = new ByteArrayInputStream(getDocument().get().getBytes());

		// lets write the document to the new output file
		try {
			if (file.exists()) {
				file.setContents(source, true, true, new NullProgressMonitor());
			} else {
				file.create(source, true, new NullProgressMonitor());
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			Activator.showUserError("Failed to save file", "Failed to write to " + file, e);
		}
	}

	protected void doSaveAs(IEditorPart editor) {
		editor.doSaveAs();
		setPageText(SOURCE_PAGE_INDEX, editor.getTitle());
		setInput(editor.getEditorInput());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return true;
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

	/*
	 * (non-Javadoc)
	 * 
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
	 * 
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
								if (page.findView(UIConstants.PROPERTIES_VIEW_ID) == null) {
									page.showView(UIConstants.PROPERTIES_VIEW_ID);
								}
							} catch (PartInitException ex) {
								Activator.getLogger().error(ex);
							}
						}
					}
				}
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.MultiPageEditorPart#pageChange(int)
	 */
	@Override
	protected void pageChange(int newPageIndex) {
		super.pageChange(newPageIndex);
		if (newPageIndex == SOURCE_PAGE_INDEX) {
			updatedDesignPage();
		} else {
			updatedTextPage();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor
	 * #getContributorId()
	 */
	@Override
	public String getContributorId() {
		return getSite().getId();
	}

	/**
	 * this method is responsible for updating the design editor before
	 * displaying it
	 */
	void updatedDesignPage() {
		updatedDesignPage(true);
	}

	void updatedDesignPage(boolean async) {
		// we are switching to the source page so lets update the text editor's
		// model with the latest diagram...
		designEditor.runIfDiagramModified(new Runnable() {

			@Override
			public void run() {
				IDocument document = getDocument();
				if (document != null) {
					String text = document.get();

					// lets update the text with the latest from the model..
					String newText = designEditor.updateEditorText(text);
					if (!Objects.equal(newText, text)) {
						// only update the document if its actually different
						// to avoid setting the dirty flag unnecessarily
						document.set(newText);
					}
				}
			}
		}, async);
// TODO: why do we update the design editor if we switched to source editor?
// 		 commented out the below line as it seems to make no sense here
//		this.designEditor.update();
	}


	/**
	 * We are switching to the design page so lets update the design model if
	 * the text has been changed
	 */
	private void updatedTextPage() {
		designEditor.runIfTextModified(new Runnable() {

			@Override
			public void run() {
				IDocument document = getDocument();
				if (document != null) {
					designEditor.clearCache();
					String text = document.get();
					Activator.getLogger().debug(
							"Updating the design model from the updated text");
					designEditor.loadEditorText(text);
					designEditor.refreshDiagramContents();
					designEditor.update();
					designEditor.setFocus();
					designEditor.fireModelChanged(); // will update the outline view
				}

			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.MultiPageEditorPart#getAdapter(java.lang.Class)
	 */
	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		if (adapter == RiderDesignEditor.class) {
			return this.designEditor;
		} else if (adapter == TextEditor.class) {
			return this.sourceEditor;
		} else if (adapter == IPropertySheetPage.class) {
			return new TabbedPropertySheetPage(this);
		} else if (adapter == ActionRegistry.class) {
			// this is needed otherwise switching between source
			// and design tab sometimes throws NPE
			return designEditor.getActionRegistry();
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

	public StructuredTextEditor getSourceEditor() {
		return sourceEditor;
	}

	public RiderDesignEditor getDesignEditor() {
		return designEditor;
	}

	public AbstractNode getSelectedNode() {
		return designEditor.getSelectedNode();
	}

	public void setSelectedNode(AbstractNode newSelection) {
		designEditor.setSelectedNode(newSelection);
	}

	public RouteContainer getModel() {
		return designEditor.getModel();
	}

	public RouteSupport getSelectedRoute() {
		return designEditor.getSelectedRoute();
	}

	public void setSelectedRoute(RouteSupport selectedRoute) {
		designEditor.setSelectedRoute(selectedRoute);
	}

	public RiderDesignEditorData getDesignEditorData() {
		return designEditorData;
	}

	public IDocument getDocument() {
		Object element = sourceEditor.getEditorInput();
		IDocumentProvider documentProvider = sourceEditor.getDocumentProvider();
		if (documentProvider != null) {
			IDocument document = documentProvider.getDocument(element);
			return document;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fusesource.ide.camel.editor.IPrefersPerspective#getPreferredPerspectiveId
	 * ()
	 */
	@Override
	public String getPreferredPerspectiveId() {
		return null; // RIDER_PERSPECTIVE_ID;
	}

	public void onFileLoading(IFile file) {
		setPartName(file.getName());
	}


	public void onInputLoading(IEditorInput input) {
		setPartName(input.getName());
	}

	public void switchToSourceEditor() {
		// lets switch async just in case we've not created the page yet
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				updatedDesignPage();
				setActiveEditor(getSourceEditor());
				setActivePage(SOURCE_PAGE_INDEX);
				getSourceEditor().setFocus();
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
			designEditor.update();
		} else if (event.getProperty().equals(PreferencesConstants.EDITOR_LAYOUT_ORIENTATION)) {
			// user switched direction of diagram layout - relayout the diagram
			designEditor.autoLayoutRoute();
		} else if (event.getProperty().equals(PreferencesConstants.EDITOR_GRID_VISIBILITY)) {
			// user switched grid visibility
			DiagramUtils.setGridVisible((Boolean)event.getNewValue());
		} else if (event.getProperty().equals(PreferencesConstants.EDITOR_TEXT_COLOR) ||
				   event.getProperty().equals(PreferencesConstants.EDITOR_CONNECTION_COLOR) ||
				   event.getProperty().equals(PreferencesConstants.EDITOR_FIGURE_BG_COLOR) ||
				   event.getProperty().equals(PreferencesConstants.EDITOR_FIGURE_FG_COLOR)) {
			designEditor.getDiagramBehavior().refresh();
		} else if (event.getProperty().equals(PreferencesConstants.EDITOR_GRID_COLOR)) {
			designEditor.setupGridVisibility();
		} 	
	}
}
