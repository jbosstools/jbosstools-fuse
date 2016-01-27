/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.camel.editor;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.fusesource.ide.camel.editor.dialogs.GlobalConfigCategoryItem;
import org.fusesource.ide.camel.editor.dialogs.GlobalConfigElementItem;
import org.fusesource.ide.camel.editor.dialogs.GlobalConfigElementsSelectionDialog;
import org.fusesource.ide.camel.editor.dialogs.provider.GlobalConfigElementsDialogContentProvider;
import org.fusesource.ide.camel.editor.dialogs.provider.GlobalConfigElementsDialogLabelProvider;
import org.fusesource.ide.camel.editor.internal.CamelEditorUIActivator;
import org.fusesource.ide.camel.editor.internal.UIMessages;
import org.fusesource.ide.camel.editor.provider.ext.GlobalConfigurationTypeWizard;
import org.fusesource.ide.camel.editor.provider.ext.ICustomGlobalConfigElementContribution;
import org.fusesource.ide.camel.editor.utils.MavenUtils;
import org.fusesource.ide.camel.model.service.core.catalog.Dependency;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.camel.model.service.core.model.CamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.ICamelModelListener;
import org.fusesource.ide.foundation.core.util.CamelUtils;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.foundation.ui.util.Selections;
import org.osgi.framework.Bundle;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author lhein
 */
public class CamelGlobalConfigEditor extends EditorPart implements ICamelModelListener {

	public static final String GLOBAL_ELEMENTS_PROVIDER_EXT_POINT_ID = "org.fusesource.ide.editor.globalConfigContributor";
	public static final String CATEGORY_ELEMENT = "GlobalConfigCategory";
	public static final String TYPE_ELEMENT = "GlobalConfigElement";
	public static final String FUSE_CAT_ID = "org.fusesource.ide.camel.editor.globalconfig.FUSE_CATEGORY";
	public static final String DEFAULT_CAT_ID = "org.fusesource.ide.camel.editor.globalconfig.DEFAULT_CATEGORY";
	
	public static final String GLOBAL_ELEMENTS_ICON_ATTR = "icon";
	public static final String GLOBAL_ELEMENTS_ID_ATTR = "id";
	public static final String GLOBAL_ELEMENTS_NAME_ATTR = "name";
	public static final String GLOBAL_ELEMENTS_CATEGORY_ATTR = "category";
	
	private CamelEditor parentEditor;

	private Composite parent;
	private TreeViewer treeViewer;
	private Button btnAdd;
	private Button btnModify;
	private Button btnDelete;
	
	private List<GlobalConfigElementItem> elementContributions = new ArrayList<GlobalConfigElementItem>();
	private List<GlobalConfigCategoryItem> categoryContributions = new ArrayList<GlobalConfigCategoryItem>();
	private HashMap<String, ArrayList> model;
	
	/**
	 * 
	 * @param parentEditor
	 */
	public CamelGlobalConfigEditor(CamelEditor parentEditor) {
		this.parentEditor = parentEditor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.
	 * IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		this.parentEditor.getDesignEditor().doSave(monitor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	@Override
	public void doSaveAs() {
		this.parentEditor.getDesignEditor().doSaveAs();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite,
	 * org.eclipse.ui.IEditorInput)
	 */
	@Override
	public void init(IEditorSite editorSite, IEditorInput input)
			throws PartInitException {
		setSite(editorSite);
		setInput(input);
		determineExtensions();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#isDirty()
	 */
	@Override
	public boolean isDirty() {
		return parentEditor.isDirty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	@Override
	public void createPartControl(Composite p) {
		this.parent = new Composite(p, SWT.FLAT);

		GridLayout gl = new GridLayout(2, false);
		gl.horizontalSpacing = 10;

		this.parent.setLayout(gl);

		// now create the controls

		this.treeViewer = new TreeViewer(parent, SWT.BORDER | SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
		this.treeViewer.setUseHashlookup(true);
		this.treeViewer.setContentProvider(new GlobalConfigContentProvider());
		this.treeViewer.setLabelProvider(new GlobalConfigLabelProvider());
		this.treeViewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 10));
		this.treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				Object selObj = Selections.getFirstSelection(event.getSelection());
				if (selObj != null) {
					btnModify.setEnabled(supportsEdit(selObj));
				} else {
					btnModify.setEnabled(false);
				}
			}
		});
		
		this.btnAdd = new Button(parent, SWT.BORDER | SWT.PUSH);
		this.btnAdd.setText(UIMessages.globalElementsTabAddButtonLabel);
		this.btnAdd.setToolTipText(UIMessages.globalElementsTabAddButtonTooltip);
		GridData gd = new GridData(GridData.FILL, GridData.BEGINNING, false,
				false, 1, 1);
		gd.minimumWidth = 120;
		gd.widthHint = 120;
		this.btnAdd.setLayoutData(gd);
		this.btnAdd.addSelectionListener(new SelectionAdapter() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse
			 * .swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				createNewEntry();
			}
		});
		this.btnAdd.setEnabled(elementContributions.isEmpty() == false);

		this.btnModify = new Button(parent, SWT.BORDER | SWT.PUSH);
		this.btnModify.setText(UIMessages.globalElementsTabEditButtonLabel);
		this.btnModify.setToolTipText(UIMessages.globalElementsTabEditButtonTooltip);
		this.btnModify.setLayoutData(new GridData(GridData.FILL,
				GridData.BEGINNING, false, false, 1, 1));
		this.btnModify.addSelectionListener(new SelectionAdapter() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse
			 * .swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				modifyEntry();
			}
		});

		this.btnDelete = new Button(parent, SWT.BORDER | SWT.PUSH);
		this.btnDelete.setText(UIMessages.globalElementsTabDeleteButtonLabel);
		this.btnDelete.setToolTipText(UIMessages.globalElementsTabDeleteButtonTooltip);
		this.btnDelete.setLayoutData(new GridData(GridData.FILL,
				GridData.BEGINNING, false, false, 1, 1));
		this.btnDelete.addSelectionListener(new SelectionAdapter() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse
			 * .swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				deleteEntries();
			}
		});

		this.categoryContributions.sort(new Comparator<GlobalConfigCategoryItem>() {
			/* (non-Javadoc)
			 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
			 */
			@Override
			public int compare(GlobalConfigCategoryItem o1, GlobalConfigCategoryItem o2) {
				if (o1.getId().equals(DEFAULT_CAT_ID)) return 1;
				if (o2.getId().equals(DEFAULT_CAT_ID)) return -1;
				return o1.getName().compareTo(o2.getName());
			}
		});
		reload();
		this.treeViewer.setInput(this.model);
		CamelFile designEditorModel = parentEditor.getDesignEditor().getModel();
		if(designEditorModel != null){
			designEditorModel.addModelListener(this);
		}
		this.treeViewer.expandAll();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		parentEditor.getDesignEditor().getModel().removeModelListener(this);
		super.dispose();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		modelChanged();
		this.treeViewer.getTree().setFocus();
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.model.ICamelModelListener#modelChanged()
	 */
	@Override
	public void modelChanged() {
		Display.getDefault().asyncExec(new Runnable() {
			/*
			 * (non-Javadoc)
			 * @see java.lang.Runnable#run()
			 */
			@Override
			public void run() {
				reload();
				parentEditor.setDirtyFlag(true);
			}
		});
	}
	
	/**
	 * loop the extensions of our global config elements extension point and
	 * collect them in a map
	 */
	private void determineExtensions() {
		try {
			IConfigurationElement[] extensions = Platform.getExtensionRegistry().getConfigurationElementsFor(GLOBAL_ELEMENTS_PROVIDER_EXT_POINT_ID);
			for (IConfigurationElement e : extensions) {
				if (e.getName().equals(CATEGORY_ELEMENT)) {
					try {
						String icon = e.getAttribute(GLOBAL_ELEMENTS_ICON_ATTR);
						String id = e.getAttribute(GLOBAL_ELEMENTS_ID_ATTR);
						String name = e.getAttribute(GLOBAL_ELEMENTS_NAME_ATTR);
		
						GlobalConfigCategoryItem item = new GlobalConfigCategoryItem();
						item.setId(id);
						item.setName(name);
						if (Strings.isBlank(icon) == false) {
							String implementorBundle = e.getDeclaringExtension().getContributor().getName();
							Bundle implBundle = Platform.getBundle(implementorBundle);
							URL iconUrl = implBundle.getResource(icon);
							item.setIcon(new Image(Display.getCurrent(), iconUrl.openConnection().getInputStream()));
						}					
						categoryContributions.add(item);
					} catch (Exception ex) {
						CamelEditorUIActivator.pluginLog().logError(ex);
						continue;
					}
				} else if (e.getName().equals(TYPE_ELEMENT)) {
					try {
						final Object o = e.createExecutableExtension("class");
						
						if (o instanceof ICustomGlobalConfigElementContribution) {
							ICustomGlobalConfigElementContribution globalElementHandler = (ICustomGlobalConfigElementContribution) o;
							String icon = e.getAttribute(GLOBAL_ELEMENTS_ICON_ATTR);
							String id = e.getAttribute(GLOBAL_ELEMENTS_ID_ATTR);
							String name = e.getAttribute(GLOBAL_ELEMENTS_NAME_ATTR);
							String catId = e.getAttribute(GLOBAL_ELEMENTS_CATEGORY_ATTR);
							
							GlobalConfigElementItem item = new GlobalConfigElementItem();
							item.setContributor(globalElementHandler);
							item.setId(id);
							item.setName(name);
							item.setCategoryId(catId);
							if (Strings.isBlank(icon) == false) {
								String implementorBundle = e.getDeclaringExtension().getContributor().getName();
								Bundle implBundle = Platform.getBundle(implementorBundle);
								URL iconUrl = implBundle.getResource(icon);
								item.setIcon(new Image(Display.getCurrent(), iconUrl.openConnection().getInputStream()));
							}					
							elementContributions.add(item);
						}
					} catch (Exception ex) {
						CamelEditorUIActivator.pluginLog().logError(ex);
						continue;
					}
				} else {
					// undefined
				}
			} 
		} finally {
			// now shuffle children into categories
			for (GlobalConfigCategoryItem cat : categoryContributions) {
				for (GlobalConfigElementItem elem : elementContributions) {
					if ( (elem.getCategoryId().trim().length()<1 && cat.getId().equals(FUSE_CAT_ID)) || 
						 (elem.getCategoryId().equals(cat.getId()) && cat.getChildren().contains(elem) == false)) {
						cat.getChildren().add(elem);
					}
				}
			}
		}
	}
	
	private GlobalConfigCategoryItem getCategoryForId(String catId) {
		for (GlobalConfigCategoryItem cat : categoryContributions) {
			if (cat.getId().equals(catId)) return cat;
		}
		return null;
	}
	
	/**
	 * finds a handler for the given camel model element
	 * 
	 * @param elem
	 * @return
	 */
	private ICustomGlobalConfigElementContribution getExtensionForElement(Element elem) {
		ICustomGlobalConfigElementContribution handler = null;
		
		IConfigurationElement[] extensions = Platform.getExtensionRegistry().getConfigurationElementsFor(GLOBAL_ELEMENTS_PROVIDER_EXT_POINT_ID);
		for (IConfigurationElement e : extensions) {
			if (e.getName().equals(TYPE_ELEMENT)) {
				try {
					final Object o = e.createExecutableExtension("class");
					
					if (o instanceof ICustomGlobalConfigElementContribution) {
						ICustomGlobalConfigElementContribution globalElementHandler = (ICustomGlobalConfigElementContribution) o;
						if (globalElementHandler.canHandle(elem)) {
							handler = globalElementHandler;
							break;
						}
					}
				} catch (Exception ex) {
					CamelEditorUIActivator.pluginLog().logError(ex);
					continue;
				}
			}
		}
		
		return handler;
	}
	
	/**
	 * reloads the list of global elements
	 */
	public void reload() {
		buildModel();
		if (treeViewer != null && treeViewer.getTree().isDisposed() == false) {
			treeViewer.setInput(this.model);
			treeViewer.refresh(true);
			treeViewer.expandAll();
		}
	}

	private void buildModel() {
		this.model = new HashMap<String, ArrayList>();
		
		for (GlobalConfigCategoryItem cat : categoryContributions) {
			model.put(cat.getId(), new ArrayList());
		}
		
		CamelFile cf = parentEditor.getDesignEditor().getModel();
		
		if(cf != null){
			// we add all global beans etc outside context
			for (Node n : cf.getGlobalDefinitions().values()) {
				boolean foundMatch = false;
				for (GlobalConfigElementItem item : elementContributions) {
					String catId = item.getCategoryId() != null && item.getCategoryId().trim().length()>0 ? item.getCategoryId() : DEFAULT_CAT_ID;	
					if (item.getContributor().canHandle(n) && model.containsKey(catId)) {
						model.get(catId).add(n);
						foundMatch = true;
						break;
					}
				}
				if (!foundMatch) model.get(DEFAULT_CAT_ID).add(n);
			}

			// we add all context wide endpoint elements
			if (cf.getCamelContext() != null && cf.getCamelContext().getEndpointDefinitions() != null) {
				for (CamelModelElement cme : cf.getCamelContext().getEndpointDefinitions().values()) {
					boolean foundMatch = false;
					for (GlobalConfigElementItem item : elementContributions) {
						String catId = item.getCategoryId() != null && item.getCategoryId().trim().length()>0 ? item.getCategoryId() : DEFAULT_CAT_ID;
						if (item.getContributor().canHandle(cme.getXmlNode()) && model.containsKey(catId)) {
							model.get(catId).add(cme);
							foundMatch = true;
							break;
						}
					}
					if (!foundMatch) model.get(DEFAULT_CAT_ID).add(cme);
				}
			}

			// we add all context wide data formats
			if (cf.getCamelContext() != null && cf.getCamelContext().getDataformats() != null) {
				for (CamelModelElement cme : cf.getCamelContext().getDataformats().values()) {
					boolean foundMatch = false;
					for (GlobalConfigElementItem item : elementContributions) {
						String catId = item.getCategoryId() != null && item.getCategoryId().trim().length()>0 ? item.getCategoryId() : DEFAULT_CAT_ID;
						if (item.getContributor().canHandle(cme.getXmlNode()) && model.containsKey(catId)) {
							model.get(catId).add(cme);
							foundMatch = true;
							break;
						}
					}
					if (!foundMatch) model.get(DEFAULT_CAT_ID).add(cme);
				}
			}
		}
	}
	
	/**
	 * creates a new entry in the treeviewer
	 */
	private void createNewEntry() {
		GlobalConfigElementsSelectionDialog dlg = new GlobalConfigElementsSelectionDialog(Display.getDefault().getActiveShell(),
														  this,
														  categoryContributions,
														  new GlobalConfigElementsDialogContentProvider(),
													  	  new GlobalConfigElementsDialogLabelProvider(),
													  	  UIMessages.createGlobalElementDialogTitle,
													  	  UIMessages.createGlobalElementDiaglogText);
		if (dlg.open() == Window.OK) {
			Object[] selection = dlg.getResult();
			if (selection != null && selection.length>0) {
				Object selObj = selection[0];
				if (selObj instanceof GlobalConfigElementItem) {
					GlobalConfigElementItem item = (GlobalConfigElementItem)selObj;
					CamelFile cf = parentEditor.getDesignEditor().getModel();
					GlobalConfigurationTypeWizard wizard = item.getContributor().createGlobalElement(cf.getDocument());
					if (wizard == null) return;
					WizardDialog wizdlg = new WizardDialog(Display.getCurrent().getActiveShell(), wizard);
					wizdlg.setBlockOnOpen(true);
					wizdlg.setTitle(UIMessages.newGlobalConfigurationTypeWizardDialogTitle);
					wizdlg.setTitleImage(null); // get a general icon or retrieve from contributor <- TODO!
					if (Window.OK == wizdlg.open()) {
						Node newXMLNode = wizard.getGlobalConfigrationElementNode();
						if (newXMLNode != null) {
							switch (item.getContributor().getGlobalConfigElementType()) {
								case GLOBAL_ELEMENT:		String id = ((Element)newXMLNode).getAttribute("id");
															cf.addGlobalDefinition(Strings.isBlank(id) ? UUID.randomUUID().toString() : id, newXMLNode);	
															break;
								case CONTEXT_DATAFORMAT:	CamelModelElement elemDF = new CamelModelElement(cf.getCamelContext(), newXMLNode);
															cf.getCamelContext().addDataFormat(elemDF);
															break;
								case CONTEXT_ENDPOINT:		CamelModelElement elemEP = new CamelModelElement(cf.getCamelContext(), newXMLNode);
															cf.getCamelContext().addEndpointDefinition(elemEP);
															break;
								default:					// ignore
															break;
							}
						}
						List<Dependency> deps = item.getContributor().getElementDependencies();
						if (deps != null && deps.isEmpty() == false) {
							try {
								MavenUtils.updateMavenDependencies(deps);
							} catch (CoreException ex) {
								CamelEditorUIActivator.pluginLog().logError("Unable to update pom dependencies for element " + item.getName(), ex);
							}
						}						
					}
				}
			}
		}
	}

	/**
	 * modifies the selected entry
	 */
	private void modifyEntry() {
		if (this.treeViewer.getSelection().isEmpty() == false) {
			IStructuredSelection sel = (IStructuredSelection)this.treeViewer.getSelection();
			Object o = Selections.getFirstSelection(sel);
			Element modElem = o instanceof Element ? (Element)o : o instanceof CamelModelElement ? (Element)((CamelModelElement)o).getXmlNode() : null; 
			ICustomGlobalConfigElementContribution extHandler = getExtensionForElement(modElem);
			if (extHandler != null) {
				GlobalConfigurationTypeWizard wizard = extHandler.modifyGlobalElement(parentEditor.getDesignEditor().getModel().getDocument());
				if (wizard == null) return;
				wizard.setGlobalConfigrationElementNode(modElem);
				WizardDialog wizdlg = new WizardDialog(Display.getCurrent().getActiveShell(), wizard);
				wizdlg.setBlockOnOpen(true);
				wizdlg.setTitle(UIMessages.newGlobalConfigurationTypeWizardDialogTitle);
				wizdlg.setTitleImage(null); // get a general icon or retrieve from contributor <- TODO!
				if (Window.OK == wizdlg.open()) {
					Node newXMLNode = wizard.getGlobalConfigrationElementNode();
					if (newXMLNode == null) return;
					switch (extHandler.getGlobalConfigElementType()) {
						case CONTEXT_DATAFORMAT:	// here we need to reinit the model element so it copies all information from the node
						case CONTEXT_ENDPOINT:		CamelModelElement cme = (CamelModelElement)o;
													cme.initialize();
													break;
						case GLOBAL_ELEMENT:		
						default:					// nothing to do - handled via node events
													break;
					}
					treeViewer.refresh(o, true);
				}
			}
		}
	}

	/**
	 * deletes the selected entries
	 */
	private void deleteEntries() {
		if (this.treeViewer.getSelection().isEmpty() == false) {
			IStructuredSelection sel = (IStructuredSelection)this.treeViewer.getSelection();
			for (Object selObj : sel.toList()) {
				ICustomGlobalConfigElementContribution extHandler = null;
				Element deletedNode = null;
				try {
					if (selObj instanceof CamelModelElement) {
						// either an endpoint or a data format definition
						CamelModelElement cme = (CamelModelElement)selObj;
						deletedNode = (Element)cme.getXmlNode();
						extHandler = getExtensionForElement(deletedNode);
						if (cme.isEndpointElement()) {
							cme.getCamelContext().removeEndpointDefinition(cme);
						} else {
							cme.getCamelContext().removeDataFormat(cme);
						}					
					} else if (selObj instanceof Element) {
						// a global bean or alike
						deletedNode = (Element)selObj;
						String id = deletedNode.getAttribute("id");
						if (id == null && id.trim().length()<1) {
							Iterator<String> keyIt = parentEditor.getDesignEditor().getModel().getGlobalDefinitions().keySet().iterator();
							while (keyIt.hasNext()) {
								String key = keyIt.next();
								Node n = parentEditor.getDesignEditor().getModel().getGlobalDefinitions().get(key);
								if (n.isEqualNode(deletedNode)) {
									id = key;
									break;
								}
							}
						}
						if (id != null) parentEditor.getDesignEditor().getModel().removeGlobalDefinition(id);
					}
				} finally {
					if (extHandler != null) extHandler.onGlobalElementDeleted(deletedNode);
					treeViewer.remove(selObj);
				}				
			}
		}
	}
	
	/**
	 * checks if we have a registered handler for the object type and enables
	 * the edit function if true
	 * 
	 * @param element
	 * @return
	 */
	private boolean supportsEdit(Object element) {
		Element e = null;
		if (element instanceof Element) {
			e = (Element)element;
		} else if (element instanceof CamelModelElement) {
			e = (Element)((CamelModelElement)element).getXmlNode();
		}
		if (e != null) {
			ICustomGlobalConfigElementContribution handler = getExtensionForElement(e);
			return handler != null;
		}
		return false;
	}

	
	class GlobalConfigContentProvider implements ITreeContentProvider {
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
		 */
		@Override
		public void dispose() {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang
		 * .Object)
		 */
		@Override
		public Object[] getChildren(Object parent) {
			return getElements(parent);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.ITreeContentProvider#getElements(java.lang
		 * .Object)
		 */
		@Override
		public Object[] getElements(Object parent) {
			if (parent instanceof HashMap) {
				Object[] catIds = ((HashMap)parent).keySet().toArray();
				Arrays.sort(catIds, new Comparator<Object>() {
					/* (non-Javadoc)
					 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
					 */
					@Override
					public int compare(Object o1, Object o2) {
						if (o1.toString().equals(DEFAULT_CAT_ID)) return 1;
						if (o2.toString().equals(DEFAULT_CAT_ID)) return -1;
						return o1.toString().compareTo(o2.toString());
					}
				});
				return catIds;
			} else if (parent instanceof String) {
				return model.get((String)parent).toArray();
			}
			
			return new Object[0];
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang
		 * .Object)
		 */
		@Override
		public Object getParent(Object element) {
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang
		 * .Object)
		 */
		@Override
		public boolean hasChildren(Object element) {
			return element instanceof String;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse
		 * .jface.viewers.Viewer, java.lang.Object, java.lang.Object)
		 */
		@Override
		public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
		}
	}

	class GlobalConfigLabelProvider extends StyledCellLabelProvider {
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.StyledCellLabelProvider#update(org.eclipse
		 * .jface.viewers.ViewerCell)
		 */
		@Override
		public void update(ViewerCell cell) {
			Object element = cell.getElement();
			StyledString text = new StyledString();
			
			if (element instanceof String) {
				GlobalConfigCategoryItem cat = getCategoryForId((String)element);
				Image img = cat.getIcon();
				text.append(cat.getName());
				cell.setImage(img);
				cell.setText(text.toString());
				cell.setStyleRanges(text.getStyleRanges());
			} else if (element instanceof Element) {
				Element node = (Element)element;
				Image img = getIconForElement(node);
				String type = Strings.capitalize(CamelUtils.getTranslatedNodeName(node));
				String catId = FUSE_CAT_ID;
				for (GlobalConfigElementItem item : elementContributions) {
					if (item.getContributor().canHandle(node)) {
						type = item.getName();
						img = item.getIcon();
						break;
					}
				}
				text.append(!Strings.isEmpty(node.getAttribute("id")) ? node.getAttribute("id") : CamelUtils.getTranslatedNodeName(node));
				cell.setImage(img);
				if (!Strings.isEmpty(node.getAttribute("id"))) text.append(" (" + type + ") ", StyledString.COUNTER_STYLER);
				cell.setText(text.toString());
				cell.setStyleRanges(text.getStyleRanges());
			} else if (element instanceof CamelModelElement) {
				CamelModelElement cme = (CamelModelElement)element;
				Image img = getIconForElement(cme);
				String type = Strings.capitalize(cme.getTranslatedNodeName());
				for (GlobalConfigElementItem item : elementContributions) {
					if (item.getContributor().canHandle(cme.getXmlNode())) {
						type = item.getName();
						img = item.getIcon();
						break;
					}
				}
				text.append(cme.getId());
				cell.setImage(img);
				text.append(" (" + type + ")", StyledString.COUNTER_STYLER);
				cell.setText(text.toString());
				cell.setStyleRanges(text.getStyleRanges());
			} else {
				// unhandled
			}
			super.update(cell);
		}
		
		private Image getIconForElement(Object element) {
			if (element instanceof Node) {
				return CamelEditorUIActivator.getDefault().getImage("beandef.gif");
			} else if (element instanceof CamelModelElement) {
				CamelModelElement cme = (CamelModelElement)element;
				if (cme.getTranslatedNodeName().equalsIgnoreCase("endpoint")) {
					return CamelEditorUIActivator.getDefault().getImage("endpointdef.png");	
				} else if (CamelUtils.getTranslatedNodeName(cme.getXmlNode().getParentNode()).equalsIgnoreCase("dataFormats")) {
					return CamelEditorUIActivator.getDefault().getImage("dataformat.gif");	
				} else {
					// unhandled
				}
			}
			return null;
		}
	}
}
