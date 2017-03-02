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
package org.fusesource.ide.camel.editor.globalconfiguration;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.DecoratingStyledCellLabelProvider;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
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
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;
import org.fusesource.ide.camel.editor.CamelEditor;
import org.fusesource.ide.camel.editor.commands.ShowPropertiesViewHandler;
import org.fusesource.ide.camel.editor.dialogs.GlobalConfigCategoryItem;
import org.fusesource.ide.camel.editor.dialogs.GlobalConfigElementItem;
import org.fusesource.ide.camel.editor.dialogs.GlobalConfigElementsSelectionDialog;
import org.fusesource.ide.camel.editor.dialogs.GlobalConfigSupport;
import org.fusesource.ide.camel.editor.dialogs.provider.GlobalConfigElementsDialogContentProvider;
import org.fusesource.ide.camel.editor.dialogs.provider.GlobalConfigElementsDialogLabelProvider;
import org.fusesource.ide.camel.editor.internal.CamelEditorUIActivator;
import org.fusesource.ide.camel.editor.internal.UIMessages;
import org.fusesource.ide.camel.editor.provider.ext.GlobalConfigurationTypeWizard;
import org.fusesource.ide.camel.editor.provider.ext.ICustomGlobalConfigElementContribution;
import org.fusesource.ide.camel.editor.utils.MavenUtils;
import org.fusesource.ide.camel.model.service.core.catalog.CamelModel;
import org.fusesource.ide.camel.model.service.core.catalog.CamelModelFactory;
import org.fusesource.ide.camel.model.service.core.catalog.Dependency;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelBasicModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelContextElement;
import org.fusesource.ide.camel.model.service.core.model.CamelEndpoint;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.camel.model.service.core.model.GlobalDefinitionCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.ICamelModelListener;
import org.fusesource.ide.camel.validation.diagram.BasicNodeValidator;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.foundation.ui.util.Selections;
import org.fusesource.ide.foundation.ui.util.Widgets;
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

	private List<GlobalConfigElementItem> elementContributions = new ArrayList<>();
	private List<GlobalConfigCategoryItem> categoryContributions = new ArrayList<>();
	private HashMap<String, ArrayList<Object>> model;
	private Set<Image> extensionPointIcons = new HashSet<>();

	/**
	 *
	 * @param parentEditor
	 */
	public CamelGlobalConfigEditor(CamelEditor parentEditor) {
		this.parentEditor = parentEditor;
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		this.parentEditor.doSave(monitor);
	}

	@Override
	public void doSaveAs() {
		this.parentEditor.doSaveAs();
	}

	@Override
	public void init(IEditorSite editorSite, IEditorInput input)
			throws PartInitException {
		setSite(editorSite);
		setInput(input);
		determineExtensions();
	}

	@Override
	public boolean isDirty() {
		return parentEditor.isDirty();
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void createPartControl(Composite p) {
		this.parent = new Composite(p, SWT.FLAT);

		GridLayout gl = new GridLayout(2, false);
		gl.horizontalSpacing = 10;

		this.parent.setLayout(gl);

		createTreeViewer();
		createRightButtons();
		this.categoryContributions.sort(new Comparator<GlobalConfigCategoryItem>() {
			@Override
			public int compare(GlobalConfigCategoryItem o1, GlobalConfigCategoryItem o2) {
				if (DEFAULT_CAT_ID.equals(o1.getId())) {
					return 1;
				}
				if (DEFAULT_CAT_ID.equals(o2.getId())) {
					return -1;
				}
				return o1.getName().compareTo(o2.getName());
			}
		});
		reload();
		this.treeViewer.setInput(this.getModel());
		CamelFile designEditorModel = parentEditor.getDesignEditor().getModel();
		if (designEditorModel != null) {
			designEditorModel.addModelListener(this);
		}
		this.treeViewer.expandAll();
	}

	private void createTreeViewer() {
		treeViewer = new TreeViewer(parent, SWT.BORDER | SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
		treeViewer.setUseHashlookup(true);
		treeViewer.setContentProvider(new GlobalConfigContentProvider(this));
		final ILabelDecorator labelDecorator = PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator();
		final IBaseLabelProvider labelProvider = new DecoratingStyledCellLabelProvider(new GlobalConfigLabelProvider(this), labelDecorator, null);
		treeViewer.setLabelProvider(labelProvider);
		treeViewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 10));
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				Object selObj = Selections.getFirstSelection(event.getSelection());
				if (selObj != null) {
					btnModify.setEnabled(supportsEdit(selObj));
					btnDelete.setEnabled(supportsDelete(selObj));
				} else {
					btnModify.setEnabled(false);
					btnDelete.setEnabled(false);
				}
			}

		});
		treeViewer.addFilter(new ViewerFilter() {
			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				if (element instanceof String) {
					return !getModel().get(element).isEmpty();
				}
				return true;
			}
		});
		getSite().setSelectionProvider(treeViewer);
	}

	private void createRightButtons() {
		createAddButton();
		createEditButton();
		createDeleteButton();
	}

	private void createDeleteButton() {
		btnDelete = new Button(parent, SWT.FLAT | SWT.PUSH);
		btnDelete.setText(UIMessages.globalElementsTabDeleteButtonLabel);
		btnDelete.setToolTipText(UIMessages.globalElementsTabDeleteButtonTooltip);
		btnDelete.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false, 1, 1));
		btnDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				deleteEntries();
			}
		});
	}

	private void createEditButton() {
		btnModify = new Button(parent, SWT.FLAT | SWT.PUSH);
		btnModify.setText(UIMessages.globalElementsTabEditButtonLabel);
		btnModify.setToolTipText(UIMessages.globalElementsTabEditButtonTooltip);
		btnModify.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false, 1, 1));
		btnModify.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				modifyEntry();
			}
		});
	}

	private void createAddButton() {
		btnAdd = new Button(parent, SWT.FLAT | SWT.PUSH);
		btnAdd.setText(UIMessages.globalElementsTabAddButtonLabel);
		btnAdd.setToolTipText(UIMessages.globalElementsTabAddButtonTooltip);
		GridData gd = new GridData(GridData.FILL, GridData.BEGINNING, false, false, 1, 1);
		gd.minimumWidth = 120;
		gd.widthHint = 120;
		btnAdd.setLayoutData(gd);
		btnAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				createNewEntry();
			}
		});
		btnAdd.setEnabled(getElementContributions().isEmpty() == false);
	}

	@Override
	public void dispose() {
		if (parentEditor != null && parentEditor.getDesignEditor() != null && parentEditor.getDesignEditor().getModel() != null) {
			parentEditor.getDesignEditor().getModel().removeModelListener(this);
		}
		for (Image image : extensionPointIcons) {
			image.dispose();
		}
		super.dispose();
	}

	@Override
	public void setFocus() {
		Display.getDefault().asyncExec(() -> reload());
		this.treeViewer.getTree().setFocus();
	}

	@Override
	public void modelChanged() {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				if (CamelGlobalConfigEditor.this.parentEditor != null
						&& CamelGlobalConfigEditor.this.equals(CamelGlobalConfigEditor.this.parentEditor.getActiveEditor())) {
					reload();
					parentEditor.setDirtyFlag(true);
				}
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
					determineCategoryExtension(e);
				} else if (e.getName().equals(TYPE_ELEMENT)) {
					determineGlobalConfigExtension(e);
				} else {
					// undefined
				}
			}
		} finally {
			// now shuffle children into categories
			for (GlobalConfigCategoryItem cat : categoryContributions) {
				for (GlobalConfigElementItem elem : getElementContributions()) {
					final String elementCategoryId = elem.getCategoryId();
					final String categoryId = cat.getId();
					if ( (elementCategoryId.trim().length()<1 && categoryId.equals(FUSE_CAT_ID)) ||
 (elementCategoryId.equals(categoryId) && !cat.getChildren().contains(elem))) {
						cat.getChildren().add(elem);
					}
				}
			}
		}
	}

	/**
	 * @param e
	 */
	private void determineGlobalConfigExtension(IConfigurationElement e) {
		try {
			final Object o = e.createExecutableExtension("class");

			if (o instanceof ICustomGlobalConfigElementContribution) {
				ICustomGlobalConfigElementContribution globalElementHandler = (ICustomGlobalConfigElementContribution) o;
				String id = e.getAttribute(GLOBAL_ELEMENTS_ID_ATTR);
				String name = e.getAttribute(GLOBAL_ELEMENTS_NAME_ATTR);
				String catId = e.getAttribute(GLOBAL_ELEMENTS_CATEGORY_ATTR);

				GlobalConfigElementItem item = new GlobalConfigElementItem();
				item.setContributor(globalElementHandler);
				item.setId(id);
				item.setName(name);
				item.setCategoryId(catId);
				setIconIfProvided(e, item);
				getElementContributions().add(item);
			}
		} catch (Exception ex) {
			CamelEditorUIActivator.pluginLog().logError(ex);
		}
	}

	/**
	 * @param e
	 */
	private void determineCategoryExtension(IConfigurationElement e) {
		try {
			String id = e.getAttribute(GLOBAL_ELEMENTS_ID_ATTR);
			String name = e.getAttribute(GLOBAL_ELEMENTS_NAME_ATTR);

			GlobalConfigCategoryItem item = new GlobalConfigCategoryItem();
			item.setId(id);
			item.setName(name);
			setIconIfProvided(e, item);
			categoryContributions.add(item);
		} catch (Exception ex) {
			CamelEditorUIActivator.pluginLog().logError(ex);
		}
	}

	/**
	 * @param e
	 * @param item
	 * @throws IOException
	 */
	private void setIconIfProvided(IConfigurationElement e, GlobalConfigSupport item) throws IOException {
		String icon = e.getAttribute(GLOBAL_ELEMENTS_ICON_ATTR);
		if (!Strings.isBlank(icon)) {
			String implementorBundle = e.getDeclaringExtension().getContributor().getName();
			Bundle implBundle = Platform.getBundle(implementorBundle);
			URL iconUrl = implBundle.getResource(icon);
			final Image image = new Image(Display.getCurrent(), iconUrl.openConnection().getInputStream());
			item.setIcon(image);
			extensionPointIcons.add(image);
		}
	}

	GlobalConfigCategoryItem getCategoryForId(String catId) {
		for (GlobalConfigCategoryItem cat : categoryContributions) {
			if (cat.getId().equals(catId)) {
				return cat;
			}
		}
		return null;
	}

	/**
	 * finds a handler for the given camel model element
	 *
	 * @param elem
	 * @return
	 */
	private ICustomGlobalConfigElementContribution getExtensionForElement(AbstractCamelModelElement cme) {
		ICustomGlobalConfigElementContribution handler = null;

		IConfigurationElement[] extensions = Platform.getExtensionRegistry().getConfigurationElementsFor(GLOBAL_ELEMENTS_PROVIDER_EXT_POINT_ID);
		for (IConfigurationElement e : extensions) {
			if (TYPE_ELEMENT.equals(e.getName())) {
				try {
					final Object o = e.createExecutableExtension("class");

					if (o instanceof ICustomGlobalConfigElementContribution) {
						ICustomGlobalConfigElementContribution globalElementHandler = (ICustomGlobalConfigElementContribution) o;
						if (globalElementHandler.canHandle(cme)) {
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
		if (!Widgets.isDisposed(treeViewer)) {
			IStructuredSelection selection = treeViewer.getStructuredSelection();
			Object firstElement = selection.getFirstElement();
			String selectedId = null;
			if (firstElement instanceof AbstractCamelModelElement) {
				selectedId = ((AbstractCamelModelElement) firstElement).getId();
			}
			treeViewer.setInput(this.getModel());
			treeViewer.refresh(true);
			treeViewer.expandAll();
			restoreSelection(selection, selectedId);
		}
		if (treeViewer != null) {
			final Object selection = treeViewer.getStructuredSelection().getFirstElement();
			if (selection instanceof AbstractCamelModelElement) {
				org.fusesource.ide.camel.validation.ValidationFactory.getInstance().validate((AbstractCamelModelElement) selection);
			}
		}
	}

	/**
	 * @param selection
	 * @param selectedId
	 */
	private void restoreSelection(IStructuredSelection selection, String selectedId) {
		if (selectedId != null) {
			for (List<Object> models : getModel().values()) {
				for (Object object : models) {
					if (object instanceof AbstractCamelModelElement) {
						if (selectedId.equals(((AbstractCamelModelElement) object).getId())) {
							selection = new StructuredSelection(object);
							break;
						}
					}
				}
			}
		}
		treeViewer.setSelection(selection);
	}

	private void buildModel() {
		model = new HashMap<>();

		for (GlobalConfigCategoryItem cat : categoryContributions) {
			getModel().put(cat.getId(), new ArrayList<Object>());
		}

		CamelFile cf = parentEditor.getDesignEditor().getModel();

		if(cf != null){
			// we add all global beans etc outside context
			for (GlobalDefinitionCamelModelElement cme : cf.getGlobalDefinitions().values()) {
				boolean foundMatch = false;
				for (GlobalConfigElementItem item : getElementContributions()) {
					String catId = item.getCategoryId() != null && item.getCategoryId().trim().length()>0 ? item.getCategoryId() : DEFAULT_CAT_ID;
					if (item.getContributor().canHandle(cme) && getModel().containsKey(catId)) {
						getModel().get(catId).add(cme);
						foundMatch = true;
						break;
					}
				}
				if (!foundMatch) {
					getModel().get(DEFAULT_CAT_ID).add(cme);
				}
			}

			// we add all context wide endpoint elements
			CamelContextElement ctx = null;
			if (cf.getRouteContainer() instanceof CamelContextElement) {
				ctx = (CamelContextElement)cf.getRouteContainer();
			}
			if (ctx != null && ctx.getEndpointDefinitions() != null) {
				for (AbstractCamelModelElement cme : ctx.getEndpointDefinitions().values()) {
					boolean foundMatch = false;
					for (GlobalConfigElementItem item : getElementContributions()) {
						String catId = item.getCategoryId() != null && item.getCategoryId().trim().length()>0 ? item.getCategoryId() : DEFAULT_CAT_ID;
						if (item.getContributor().canHandle(cme) && getModel().containsKey(catId)) {
							getModel().get(catId).add(cme);
							foundMatch = true;
							break;
						}
					}
					if (!foundMatch) {
						getModel().get(DEFAULT_CAT_ID).add(cme);
					}
				}
			}

			// we add all context wide data formats
			if (ctx != null && ctx.getDataformats() != null) {
				for (AbstractCamelModelElement cme : ctx.getDataformats().values()) {
					boolean foundMatch = false;
					for (GlobalConfigElementItem item : getElementContributions()) {
						String catId = item.getCategoryId() != null && item.getCategoryId().trim().length()>0 ? item.getCategoryId() : DEFAULT_CAT_ID;
						if (item.getContributor().canHandle(cme) && getModel().containsKey(catId)) {
							getModel().get(catId).add(cme);
							foundMatch = true;
							break;
						}
					}
					if (!foundMatch) {
						getModel().get(DEFAULT_CAT_ID).add(cme);
					}
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
					GlobalConfigurationTypeWizard wizard = item.getContributor().createGlobalElement(cf);
					if (wizard == null) {
						return;
					}
					WizardDialog wizdlg = new WizardDialog(Display.getCurrent().getActiveShell(), wizard);
					wizdlg.setBlockOnOpen(true);
					wizdlg.setTitle(UIMessages.newGlobalConfigurationTypeWizardDialogTitle);
					if (Window.OK == wizdlg.open()) {
						Node newXMLNode = wizard.getGlobalConfigurationElementNode();
						if (newXMLNode != null) {
							switch (item.getContributor().getGlobalConfigElementType()) {
							case GLOBAL_ELEMENT:
								createNewGlobalElement(cf, newXMLNode);
								break;
							case CONTEXT_DATAFORMAT:
								createNewDataFormat(cf, newXMLNode);
								break;
							case CONTEXT_ENDPOINT:
								createNewEndpoint(cf, newXMLNode);
								break;
							default: // ignore
								break;
							}
						}
						List<Dependency> deps = item.getContributor().getElementDependencies();
						if (deps != null && !deps.isEmpty()) {
							try {
								new MavenUtils().updateMavenDependencies(deps);
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
	 * @param cf
	 * @param newXMLNode
	 */
	private void createNewEndpoint(CamelFile cf, Node newXMLNode) {
		final CamelEndpoint newEndpoint = addEndpointToGlobalContext(cf, (Element) newXMLNode);
		new BasicNodeValidator().validate(newEndpoint);
		reload();
		treeViewer.setSelection(new StructuredSelection(newEndpoint), true);
	}

	/**
	 * @param cf
	 * @param newXMLNode
	 */
	private void createNewDataFormat(CamelFile cf, Node newXMLNode) {
		final CamelBasicModelElement newDataFormat = addDataFormat(cf, (Element) newXMLNode);
		new BasicNodeValidator().validate(newDataFormat);
		reload();
		treeViewer.setSelection(new StructuredSelection(newDataFormat), true);
	}

	/**
	 * @param cf
	 * @param newXMLNode
	 */
	private void createNewGlobalElement(CamelFile cf, Node newXMLNode) {
		addNewGlobalElement(cf, newXMLNode);
		reload();
		treeViewer.setSelection(new StructuredSelection(newXMLNode), true);
	}
	
	/**
	 * /!\ Public for test purpose
	 *
	 * @param cf
	 * @param newXMLNode
	 */
	public GlobalDefinitionCamelModelElement addNewGlobalElement(CamelFile cf, Node newXMLNode) {
		if (newXMLNode != null) {
			String id = ((Element) newXMLNode).getAttribute("id");
			GlobalDefinitionCamelModelElement newGlobalDef = new GlobalDefinitionCamelModelElement(cf, newXMLNode);
			final String settedId = Strings.isBlank(id) ? UUID.randomUUID().toString() : id;
			newGlobalDef.setId(settedId);
			newGlobalDef.initialize();
			if (cf.getGlobalDefinitions().containsKey(id)) {
				cf.updateGlobalDefinition(settedId, newGlobalDef);
			} else {
				cf.addGlobalDefinition(settedId, newGlobalDef);
			}
			return newGlobalDef;
		}
		return null;
	}

	/**
	 * /!\ Public for test purpose
	 *
	 * @param cf
	 * @param newXMLNode
	 */
	public CamelBasicModelElement addDataFormat(CamelFile cf, Element newXMLNode) {
		if (cf.getRouteContainer() instanceof CamelContextElement) {
			CamelBasicModelElement elemDF = new CamelBasicModelElement(cf.getRouteContainer(), newXMLNode);
			final String eipName = org.fusesource.ide.foundation.core.util.CamelUtils.getTranslatedNodeName(newXMLNode);
			configureCamelModelElement(cf, newXMLNode, elemDF, eipName);
			((CamelContextElement)cf.getRouteContainer()).addDataFormat(elemDF);
			return elemDF;
		}
		return null;
	}

	/**
	 * @param cf
	 * @param newXMLNode
	 */
	private CamelEndpoint addEndpointToGlobalContext(CamelFile cf, Element newXMLNode) {
		if (cf.getRouteContainer() instanceof CamelContextElement) {
			CamelEndpoint elemEP = new CamelEndpoint(newXMLNode.getAttribute("uri"));
			elemEP.setParent(cf.getRouteContainer());
			configureCamelModelElement(cf, newXMLNode, elemEP, "to");
			((CamelContextElement)cf.getRouteContainer()).addEndpointDefinition(elemEP);
			return elemEP;
		}
		return null;
	}

	/**
	 * @param cf
	 * @param newXMLNode
	 * @param cme
	 * @param eipName
	 */
	private void configureCamelModelElement(CamelFile cf, Element newXMLNode, AbstractCamelModelElement cme, final String eipName) {
		cme.setXmlNode(newXMLNode);
		IProject project = cf.getResource().getProject();
		final CamelModel camelModel = CamelModelFactory.getModelForProject(project);
		cme.setUnderlyingMetaModelObject(camelModel.getEipModel().getEIPByName(eipName));
		cme.setId(newXMLNode.getAttribute("id"));
		cme.initialize();
	}

	/**
	 * modifies the selected entry
	 */
	private void modifyEntry() {
		if (!treeViewer.getSelection().isEmpty()) {
			IStructuredSelection sel = (IStructuredSelection)this.treeViewer.getSelection();
			Object o = Selections.getFirstSelection(sel);
			AbstractCamelModelElement cme = o instanceof AbstractCamelModelElement ? (AbstractCamelModelElement) o : null;
			ICustomGlobalConfigElementContribution extHandler = getExtensionForElement(cme);
			if (extHandler != null) {
				GlobalConfigurationTypeWizard wizard = extHandler.modifyGlobalElement(parentEditor.getDesignEditor().getModel());
				if (wizard == null) {
					try {
						new ShowPropertiesViewHandler().execute(null);
					} catch (ExecutionException e) {
						CamelEditorUIActivator.pluginLog().logError(e);
					}
				} else {
					wizard.setGlobalConfigurationElementNode((Element) cme.getXmlNode());
					WizardDialog wizdlg = new WizardDialog(Display.getCurrent().getActiveShell(), wizard);
					wizdlg.setBlockOnOpen(true);
					wizdlg.setTitle(UIMessages.newGlobalConfigurationTypeWizardDialogTitle);
					wizdlg.setTitleImage(null); // TODO get a general icon or
												// retrieve from contributor <-
					if (Window.OK == wizdlg.open()) {
						Node newXMLNode = wizard.getGlobalConfigurationElementNode();
						if (newXMLNode == null) {
							return;
						}
						switch (extHandler.getGlobalConfigElementType()) {
						case CONTEXT_DATAFORMAT:
							throw new UnsupportedOperationException();
							// here we need to reinit the model element so it
							// copies all information from the node
						case CONTEXT_ENDPOINT:		throw new UnsupportedOperationException();
						case GLOBAL_ELEMENT:
							modifyGlobalElement(newXMLNode);
							break;
						default:					// nothing to do - handled via node events
							break;
						}
						treeViewer.refresh(o, true);
					}
				}
			}
		}
	}

	/**
	 * @param newXMLNode
	 */
	private void modifyGlobalElement(Node newXmlNode) {
		String id = ((Element) newXmlNode).getAttribute("id");
		CamelFile cf = parentEditor.getDesignEditor().getModel();
		GlobalDefinitionCamelModelElement cme = new GlobalDefinitionCamelModelElement(cf, newXmlNode);
		cme.setId(id);
		cme.initialize();
		cf.updateGlobalDefinition(Strings.isBlank(id) ? UUID.randomUUID().toString() : id, cme);
		reload();
		treeViewer.setSelection(new StructuredSelection(cme), true);
	}

	/**
	 * deletes the selected entries
	 */
	private void deleteEntries() {
		if (!treeViewer.getSelection().isEmpty()) {
			IStructuredSelection sel = (IStructuredSelection)this.treeViewer.getSelection();
			for (Object selObj : sel.toList()) {
				ICustomGlobalConfigElementContribution extHandler = null;
				if (selObj instanceof AbstractCamelModelElement) {
					AbstractCamelModelElement cme = (AbstractCamelModelElement) selObj;
					try {
						// either an endpoint or a data format definition
						extHandler = getExtensionForElement(cme);
						if (cme.isEndpointElement()) {
							if (cme.getRouteContainer() instanceof CamelContextElement) {
								((CamelContextElement)cme.getRouteContainer()).removeEndpointDefinition(cme);
							}
						} else if (cme instanceof GlobalDefinitionCamelModelElement) {
							cme.getCamelFile().removeGlobalDefinition(cme.getId());
						} else {
							if (cme.getRouteContainer() instanceof CamelContextElement) {
								((CamelContextElement)cme.getRouteContainer()).removeDataFormat(cme);
							}
						}
					} finally {
						if (extHandler != null) {
							extHandler.onGlobalElementDeleted(cme);
						}
						treeViewer.remove(selObj);
					}
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
		if (element instanceof AbstractCamelModelElement) {
			ICustomGlobalConfigElementContribution handler = getExtensionForElement((AbstractCamelModelElement) element);
			return handler != null;
		}
		return false;
	}

	private boolean supportsDelete(Object selObj) {
		return selObj instanceof AbstractCamelModelElement && ((AbstractCamelModelElement) selObj).getId() != null || selObj instanceof Element;
	}

	/**
	 * @return the model
	 */
	public Map<String, ArrayList<Object>> getModel() {
		return model;
	}

	/**
	 * @return the elementContributions
	 */
	public List<GlobalConfigElementItem> getElementContributions() {
		return elementContributions;
	}

	/**
	 * @param camelModelElement
	 */
	public void setSelection(AbstractCamelModelElement camelModelElement) {
		if (camelModelElement != null) {
			treeViewer.setSelection(new StructuredSelection(camelModelElement), true);
		}
	}

}
