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
import org.fusesource.ide.camel.editor.dialogs.GlobalConfigElementItem;
import org.fusesource.ide.camel.editor.dialogs.GlobalConfigElementsSelectionDialog;
import org.fusesource.ide.camel.editor.dialogs.provider.GlobalConfigElementsDialogContentProvider;
import org.fusesource.ide.camel.editor.dialogs.provider.GlobalConfigElementsDialogLabelProvider;
import org.fusesource.ide.camel.editor.internal.CamelEditorUIActivator;
import org.fusesource.ide.camel.editor.internal.UIMessages;
import org.fusesource.ide.camel.editor.provider.ext.ICustomGlobalConfigElementContribution;
import org.fusesource.ide.camel.editor.utils.MavenUtils;
import org.fusesource.ide.camel.model.service.core.catalog.Dependency;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.camel.model.service.core.model.CamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.ICamelModelListener;
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
	public static final String GLOBAL_ELEMENTS_ICON_ATTR = "icon";
	public static final String GLOBAL_ELEMENTS_ID_ATTR = "id";
	public static final String GLOBAL_ELEMENTS_NAME_ATTR = "name";
	
	private CamelEditor parentEditor;

	private Composite parent;
	private TreeViewer treeViewer;
	private Button btnAdd;
	private Button btnModify;
	private Button btnDelete;
	private List<GlobalConfigElementItem> elementContributions = new ArrayList<GlobalConfigElementItem>();

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

		this.treeViewer.setInput(this.parentEditor.getDesignEditor().getModel());
		parentEditor.getDesignEditor().getModel().addModelListener(this);
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
		IConfigurationElement[] extensions = Platform.getExtensionRegistry().getConfigurationElementsFor(GLOBAL_ELEMENTS_PROVIDER_EXT_POINT_ID);
		for (IConfigurationElement e : extensions) {
			try {
				final Object o = e.createExecutableExtension("class");
				
				if (o instanceof ICustomGlobalConfigElementContribution) {
					ICustomGlobalConfigElementContribution globalElementHandler = (ICustomGlobalConfigElementContribution) o;
					String icon = e.getAttribute(GLOBAL_ELEMENTS_ICON_ATTR);
					String id = e.getAttribute(GLOBAL_ELEMENTS_ID_ATTR);
					String name = e.getAttribute(GLOBAL_ELEMENTS_NAME_ATTR);
					
					GlobalConfigElementItem item = new GlobalConfigElementItem();
					item.setContributor(globalElementHandler);
					item.setId(id);
					item.setName(name);
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
		}
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
		
		return handler;
	}
	
	/**
	 * reloads the list of global elements
	 */
	public void reload() {
		treeViewer.refresh(true);
	}

	/**
	 * creates a new entry in the treeviewer
	 */
	private void createNewEntry() {
		GlobalConfigElementsSelectionDialog dlg = new GlobalConfigElementsSelectionDialog(Display.getDefault().getActiveShell(),
														  elementContributions,
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
					Node newXMLNode = item.getContributor().createGlobalElement(cf.getDocument());
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
				boolean changed = extHandler.modifyGlobalElement(parentEditor.getDesignEditor().getModel().getDocument(), modElem);
				if (changed) {
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
			ArrayList<Object> childElements = new ArrayList<Object>();
			if (parent instanceof CamelFile) {
				CamelFile cf = (CamelFile)parent;
				
				// we add all global beans etc outside context
				childElements.addAll(cf.getGlobalDefinitions().values());
				
				// we add all context wide endpoint elements
				if (cf.getCamelContext() != null && cf.getCamelContext().getEndpointDefinitions() != null) {
					Iterator<String> epIdIt = cf.getCamelContext().getEndpointDefinitions().keySet().iterator();
					while (epIdIt.hasNext()) {
						String epId = epIdIt.next();
						CamelModelElement ep = cf.getCamelContext().getEndpointDefinitions().get(epId);
						childElements.add(ep);
					}
				}
				
				// we add all context wide data formats
				if (cf.getCamelContext() != null && cf.getCamelContext().getDataformats() != null) {
					Iterator<String> dfIdIt = cf.getCamelContext().getDataformats().keySet().iterator();
					while (dfIdIt.hasNext()) {
						String dfId = dfIdIt.next();
						CamelModelElement df = cf.getCamelContext().getDataformats().get(dfId);
						childElements.add(df);
					}
				}
			}
			return childElements.toArray();
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
			return false;
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

			if (element instanceof Element) {
				Element node = (Element)element;
				Image img = getIconForElement(node);
				String type = Strings.capitalize(node.getNodeName());
				for (GlobalConfigElementItem item : elementContributions) {
					if (item.getContributor().canHandle(node)) {
						type = item.getName();
						img = item.getIcon();
						break;
					}
				}
				text.append(!Strings.isEmpty(node.getAttribute("id")) ? node.getAttribute("id") : node.getNodeName());
				cell.setImage(img);
				if (!Strings.isEmpty(node.getAttribute("id"))) text.append(" (" + type + ") ", StyledString.COUNTER_STYLER);
				cell.setText(text.toString());
				cell.setStyleRanges(text.getStyleRanges());
			} else if (element instanceof CamelModelElement) {
				CamelModelElement cme = (CamelModelElement)element;
				Image img = getIconForElement(cme);
				String type = Strings.capitalize(cme.getXmlNode().getNodeName());
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
				if (cme.getXmlNode().getNodeName().equalsIgnoreCase("endpoint")) {
					return CamelEditorUIActivator.getDefault().getImage("endpointdef.png");	
				} else if (cme.getXmlNode().getParentNode().getNodeName().equalsIgnoreCase("dataFormats")) {
					return CamelEditorUIActivator.getDefault().getImage("dataformat.gif");	
				} else {
					// unhandled
				}
			}
			return null;
		}
	}
}
