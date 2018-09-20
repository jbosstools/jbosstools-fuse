/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.editor.restconfiguration;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.progress.IProgressService;
import org.fusesource.ide.camel.editor.CamelEditor;
import org.fusesource.ide.camel.editor.internal.CamelEditorUIActivator;
import org.fusesource.ide.camel.editor.internal.UIMessages;
import org.fusesource.ide.camel.editor.restconfiguration.actions.AddRestConfigurationAction;
import org.fusesource.ide.camel.editor.restconfiguration.actions.AddRestElementAction;
import org.fusesource.ide.camel.editor.restconfiguration.actions.AddRestOperationAction;
import org.fusesource.ide.camel.editor.restconfiguration.actions.DeleteRestConfigurationAction;
import org.fusesource.ide.camel.editor.restconfiguration.actions.DeleteRestElementAction;
import org.fusesource.ide.camel.editor.restconfiguration.actions.DeleteRestOperationAction;
import org.fusesource.ide.camel.editor.restconfiguration.wizards.AddRestOperationWizard;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.AbstractRestCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelBasicModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelContextElement;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.camel.model.service.core.model.ICamelModelListener;
import org.fusesource.ide.camel.model.service.core.model.RestConfigurationElement;
import org.fusesource.ide.camel.model.service.core.model.RestElement;
import org.fusesource.ide.camel.model.service.core.model.RestVerbElement;
import org.fusesource.ide.camel.model.service.core.model.eips.RestConfigurationElementEIP;
import org.fusesource.ide.camel.model.service.core.model.eips.RestVerbElementEIP;
import org.fusesource.ide.foundation.core.util.Strings;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author bfitzpat
 */
public class RestConfigEditor extends EditorPart implements ICamelModelListener, ISelectionProvider {

	private static final String OFF = "off"; //$NON-NLS-1$
	
	CamelEditor parentEditor;
	private Composite parent;
	private ScrolledForm form;
	private FormToolkit toolkit;
	private ImageRegistry mImageRegistry;	
	private ListenerList<ISelectionChangedListener> listeners = new ListenerList<>();
	private Object selection;
	private Control selectedControl;
	private Combo componentCombo;
	private Text contextPathText;
	private Text portText;
	private Combo bindingModeCombo;
	private Text hostText;
	private Composite restOpsComposite;
	private ListViewer restList;
	private ScrolledComposite restOpsScrollable;

	private RestEditorColorManager colorManager = new RestEditorColorManager();
	private Map<String, Eip> restModel;
	private CamelContextElement ctx;
	private RestConfigurationElement rce = null;
	private RestConfigUtil util = new RestConfigUtil();

	private AddRestConfigurationAction addRestConfigAction;
	private DeleteRestConfigurationAction deleteRestConfigAction;
	private AddRestElementAction addRestElementAction;
	private DeleteRestElementAction deleteRestElementAction;
	private AddRestOperationAction addRestOperationAction;
	private DeleteRestOperationAction deleteRestOperationAction;

	
	/**
	 *
	 * @param parentEditor
	 */
	public RestConfigEditor(CamelEditor parentEditor) {
		this.parentEditor = parentEditor;
		restModel = RestModelBuilder.getRestModelFromCatalog(parentEditor.getDesignEditor().getWorkspaceProject(), new NullProgressMonitor());
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
		getSite().setSelectionProvider(this);
		setSelection(StructuredSelection.EMPTY);
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
		getImages();

		this.parent = new Composite(p, SWT.FLAT);

		GridLayout gl = new GridLayout(1, false);
		gl.horizontalSpacing = 10;

		this.parent.setLayout(gl);

		CamelFile designEditorModel = parentEditor.getDesignEditor().getModel();
		ctx = getCamelContext(designEditorModel);

		createContents();

		if (designEditorModel != null) {
			designEditorModel.addModelListener(this);
		}
	}
	
	// made public for testing only
	public CamelContextElement getCtx() {
		return ctx;
	}

	protected CamelContextElement getCamelContext(CamelFile designEditorModel) {
		if (designEditorModel != null) {
			designEditorModel.addModelListener(this);
			
			if (designEditorModel.getRouteContainer() instanceof CamelContextElement) {
				return  (CamelContextElement)designEditorModel.getRouteContainer();
			}		
		}
		return null;
	}

	@Override
	public void dispose() {
		if (parentEditor != null && parentEditor.getDesignEditor() != null && parentEditor.getDesignEditor().getModel() != null) {
			parentEditor.getDesignEditor().getModel().removeModelListener(this);
		}
		mImageRegistry.dispose();
		super.dispose();
	}

	@Override
	public void modelChanged() {
		reload();
	}

	@Override
	public void setFocus() {
		// empty
	}

	private void createContents() {
		toolkit = new FormToolkit(Display.getDefault());

		form = toolkit.createScrolledForm(parent);
		form.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 10));
		form.getBody().setLayout(new GridLayout(2, false));
		
		createRestConfigurationTabSection();
		createRestTabSection();
		restOpsComposite = createRestOperationTabSection();

		form.layout(true);
		toolkit.decorateFormHeading(form.getForm());
		getSite().setSelectionProvider(this);

	}
	
	private String getTextForImage(String text) {
		if (restModel.containsKey(text)) {
			return restModel.get(text).getName();
		}
		return text;
	}
	
	private Composite createVerbComposite(Composite parent, String labelText, String content) {
		Composite client=toolkit.createComposite(parent,SWT.BORDER);
		client.setBackground(colorManager.get(RestConfigConstants.REST_COLOR_LIGHT_BLUE));
		client.setLayout(new GridLayout(2, false));
		GridData gd = GridDataFactory.fillDefaults().grab(true, false).span(2, 1).create();
		client.setLayoutData(gd);

		String graphicLabel = getTextForImage(labelText);
		Color imageColor = colorManager.getImageColorForType(graphicLabel);
		Color backgroundColor = colorManager.getBackgroundColorForType(graphicLabel);
		Color foregroundColor = colorManager.getForegroundColorForType(graphicLabel);
		client.setBackground(backgroundColor);
		client.addListener(SWT.MouseDown, new RestVerbSelectionListener());

		Label image=new Label(client,SWT.WRAP | SWT.BOLD | SWT.CENTER);
		image.setText(graphicLabel);
		image.setBackground(imageColor);
		image.setForeground(foregroundColor);
		image.setLayoutData(GridDataFactory.fillDefaults().hint(50, SWT.DEFAULT).create());
		image.addListener(SWT.MouseDown, new RestVerbSelectionListener());

		Label label=new Label(client,SWT.WRAP);
		label.setText(content);
		label.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false));
		label.setBackground(client.getBackground());
		label.addListener(SWT.MouseDown, new RestVerbSelectionListener());

		return client;
	}

	private Object getDataFromSelectedUIElement(Control control) {
		Node data = null;
		if (!control.isDisposed()) {
			Object oData = control.getData(RestConfigConstants.REST_VERB_FLAG);
			if (oData instanceof RestVerbElement) {
				return oData;
			}
			if (oData instanceof Node) {
				data = (Node) oData;
			}
			if (data != null) {
				return new CamelBasicModelElement(null, data);
			}
			if (control.getParent() != null) {
				return getDataFromSelectedUIElement(control.getParent());
			}
		}
		return null;
	}

	class PushAction extends Action {
		public PushAction ( ) {
			super(null, IAction.AS_PUSH_BUTTON);
		}
	}

	private void getImages() {
		mImageRegistry = new ImageRegistry();
		mImageRegistry.put(RestConfigConstants.IMG_DESC_ADD, ImageDescriptor
				.createFromURL(CamelEditorUIActivator.getDefault().getBundle()
						.getEntry(RestConfigConstants.IMG_DESC_ADD)));	
		mImageRegistry.put(RestConfigConstants.IMG_DESC_DELETE, ImageDescriptor
				.createFromURL(CamelEditorUIActivator.getDefault().getBundle()
						.getEntry(RestConfigConstants.IMG_DESC_DELETE)));	
	}

	private ToolBar createRestConfigurationToolbar(Composite parent) {
		ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);
		ToolBar toolbar = toolBarManager.createControl(parent);

		addRestConfigAction = new AddRestConfigurationAction(this, mImageRegistry);
		toolBarManager.add(addRestConfigAction);

		deleteRestConfigAction = new DeleteRestConfigurationAction(this, mImageRegistry);
		toolBarManager.add(deleteRestConfigAction);

		toolBarManager.update(true);
		return toolbar;
	}

	private ToolBar createRestTabToolbar(Composite parent) {
		ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);
		ToolBar toolbar = toolBarManager.createControl(parent);

		addRestElementAction = new AddRestElementAction(this, mImageRegistry);
		toolBarManager.add(addRestElementAction);

		deleteRestElementAction = new DeleteRestElementAction(this, mImageRegistry);
		toolBarManager.add(deleteRestElementAction);

		toolBarManager.update(true);
		return toolbar;
	}

	private ToolBar createRestOperationTabToolbar(Composite parent) {
		ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);
		ToolBar toolbar = toolBarManager.createControl(parent);

		addRestOperationAction = new AddRestOperationAction(this, mImageRegistry);
		toolBarManager.add(addRestOperationAction);

		deleteRestOperationAction = new DeleteRestOperationAction(this, mImageRegistry);
		toolBarManager.add(deleteRestOperationAction);

		toolBarManager.update(true);
		return toolbar;
	}

	private Composite createRestTabSection() {
		Section section = toolkit.createSection(form.getBody(), Section.EXPANDED | Section.TWISTIE | Section.TITLE_BAR | Section.DESCRIPTION);
		section.setText(UIMessages.restConfigEditorRestSectionLabelText);
		section.setDescription(UIMessages.restConfigEditorRestTabDescription);
		section.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).span(1, 5).create());
		section.setLayout(new FillLayout(SWT.VERTICAL));

		ToolBar toolbar = createRestTabToolbar(section);
		section.setTextClient(toolbar);
		
		Composite client=toolkit.createComposite(section,SWT.NONE);
		client.setLayout(new FillLayout(SWT.VERTICAL));
		client.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		
		restList = new ListViewer(client, SWT.V_SCROLL | SWT.SINGLE | SWT.BORDER);
		restList.setContentProvider(ArrayContentProvider.getInstance());
		restList.setComparator(new ViewerComparator());
		restList.setLabelProvider(new RestLabelProvider());
		restList.addSelectionChangedListener(event -> {
			addRestOperationAction.setEnabled(false);
			if (event.getStructuredSelection().getFirstElement() instanceof RestElement) {
				clearUI();
				
				RestElement acme = (RestElement) event.getStructuredSelection().getFirstElement();
				selection = acme;
				setSelection(new StructuredSelection(acme));
				addRestOperationAction.setEnabled(true);
				deleteRestElementAction.setEnabled(
						!ctx.getRestElements().isEmpty() && !restList.getStructuredSelection().isEmpty());

				Comparator<AbstractCamelModelElement> acmeCompare = (m1, m2) -> {
			    	String m1uri = (String) m1.getParameter(AbstractCamelModelElement.URI_PARAMETER_KEY);
			    	String m2uri = (String) m2.getParameter(AbstractCamelModelElement.URI_PARAMETER_KEY);
			    	return m1uri.compareTo(m2uri);
					
				};
				Collection<AbstractCamelModelElement> operations = acme.getRestOperations().values();
				operations.stream().sorted(acmeCompare).forEach(e -> {
					RestVerbElement rve = (RestVerbElement) e;
					Element elChild = (Element) rve.getXmlNode();
					String verbUri = elChild.getAttribute(RestVerbElementEIP.PROP_URI);
					Composite operation = createVerbComposite(restOpsComposite, elChild.getTagName(), verbUri);
					operation.setData(RestConfigConstants.REST_VERB_FLAG, rve);
					operation.requestLayout();
				});
			}
			restOpsComposite.layout();
			restOpsScrollable.notifyListeners(SWT.Resize, new Event());
		});

		section.setClient(client);
		
		return client;
	}
	
	private Composite createRestOperationTabSection() {
		Section section = toolkit.createSection(form.getBody(), Section.EXPANDED | Section.TWISTIE | Section.TITLE_BAR | Section.DESCRIPTION);
		section.setText(UIMessages.restConfigEditorRestSectionLabel);
		section.setDescription(UIMessages.restConfigEditorRestOperationTabDescription);
		section.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).span(1, 5).create());
		section.setLayout(new FillLayout(SWT.VERTICAL));

		ToolBar toolbar = createRestOperationTabToolbar(section);
		section.setTextClient(toolbar);

		restOpsScrollable = new ScrolledComposite(section, SWT.BORDER | SWT.V_SCROLL);
		restOpsScrollable.setLayout(new FillLayout(SWT.VERTICAL));
		Composite client=toolkit.createComposite(restOpsScrollable, SWT.NONE);
		client.setLayout(new GridLayout(1, false));
		client.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		restOpsScrollable.setContent(client);
		restOpsScrollable.setExpandHorizontal(true);
		restOpsScrollable.setExpandVertical(true);
		restOpsScrollable.addListener(SWT.Resize, event -> computeMinSize(client) );
		
		section.setClient(restOpsScrollable);
		
		return client;
	}

	private void computeMinSize( Composite content ) {
		int width = restOpsScrollable.getClientArea().width;
		restOpsScrollable.setMinSize( content.computeSize( width, SWT.DEFAULT ) );
	}
	
	class RestLabelProvider extends LabelProvider {

		@Override
		public String getText(Object element) {
			if (element instanceof AbstractCamelModelElement) {
				AbstractCamelModelElement acme = (AbstractCamelModelElement) element;
				Element restElement = (Element) acme.getXmlNode();
				if (getAttrValue(restElement, "path") != null) { //$NON-NLS-1$
					return getAttrValue(restElement, "path"); //$NON-NLS-1$
				}
			}
			return super.getText(element);
		}
		
	}
	
	private void enableRestConfigurationTabSection(boolean enabledFlag) {
		componentCombo.setEnabled(enabledFlag);
		contextPathText.setEnabled(enabledFlag);
		portText.setEnabled(enabledFlag);
		bindingModeCombo.setEnabled(enabledFlag);
		hostText.setEnabled(enabledFlag);
	}

	private Composite createRestConfigurationTabSection() {
		Section section = toolkit.createSection(form.getBody(), Section.EXPANDED | Section.TWISTIE | Section.TITLE_BAR | Section.DESCRIPTION);
		section.setText(UIMessages.restConfigEditorRestConfigSectionLabelText);
		section.setDescription(UIMessages.restConfigEditorRestConfigurationTabDescription);
		GridData gd = GridDataFactory.fillDefaults().grab(true, false).span(2, 1).create();
		section.setLayoutData(gd);
		section.setLayout(new GridLayout(2, false));

		ToolBar toolbar = createRestConfigurationToolbar(section);
		section.setTextClient(toolbar);

		Composite client=toolkit.createComposite(section,SWT.NONE);
		client.setLayout(new GridLayout(2, false));
		client.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		
		toolkit.createLabel(client, UIMessages.restConfigEditorComponentLabel);
		componentCombo = new Combo(client, SWT.DROP_DOWN | SWT.READ_ONLY);
		componentCombo.add(""); //$NON-NLS-1$
		componentCombo.add("netty-http"); //$NON-NLS-1$
		componentCombo.add("netty4-http"); //$NON-NLS-1$
		componentCombo.add("jetty"); //$NON-NLS-1$
		componentCombo.add("restlet"); //$NON-NLS-1$
		componentCombo.add("servlet"); //$NON-NLS-1$
		componentCombo.add("spark-rest"); //$NON-NLS-1$
		componentCombo.add("undertow"); //$NON-NLS-1$
		componentCombo.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		componentCombo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (rce != null) {
					rce.setComponent(componentCombo.getText());
					addComponentDependency(componentCombo.getText());
				}
			}
		});
		
		toolkit.createLabel(client, UIMessages.restConfigEditorContextPathLabel);
		contextPathText = toolkit.createText(client, "", SWT.BORDER); //$NON-NLS-1$
		contextPathText.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		contextPathText.addModifyListener(input -> {
			if (rce != null) {
				rce.setContextPath(contextPathText.getText());
			}
		});
		
		toolkit.createLabel(client, UIMessages.restConfigEditorPortLabel);
		portText = toolkit.createText(client, "", SWT.BORDER); //$NON-NLS-1$
		portText.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		portText.addModifyListener(input -> {
			if (rce != null) {
				rce.setPort(portText.getText());
			}
		});

		toolkit.createLabel(client, UIMessages.restConfigEditorBindingModeLabel);
		bindingModeCombo = new Combo(client, SWT.DROP_DOWN | SWT.READ_ONLY);
		bindingModeCombo.add("auto"); //$NON-NLS-1$
		bindingModeCombo.add("json"); //$NON-NLS-1$
		bindingModeCombo.add("json_xml"); //$NON-NLS-1$
		bindingModeCombo.add(OFF); //$NON-NLS-1$
		bindingModeCombo.add("xml"); //$NON-NLS-1$
		bindingModeCombo.setText(OFF); //$NON-NLS-1$
		bindingModeCombo.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		bindingModeCombo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (rce != null) {
					rce.setBindingMode(bindingModeCombo.getText());
				}
			}
		});

		toolkit.createLabel(client, UIMessages.restConfigEditorHostLabel);
		hostText = toolkit.createText(client, "", SWT.BORDER); //$NON-NLS-1$
		hostText.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		hostText.addModifyListener(input -> {
			if (rce != null) {
				rce.setHost(hostText.getText());
			}
		});
		
		enableRestConfigurationTabSection(false);

		refreshRestConfigurationSection();
		
		section.setClient(client);
		
		return client;
	}
	
	private void addComponentDependency(String componentName) {
		IWorkbench wb = PlatformUI.getWorkbench();
		IProgressService ps = wb.getProgressService();
		try {
			ps.busyCursorWhile(new AddComponentDependencyRunnable(this, componentName));
		} catch (InvocationTargetException e) {
			CamelEditorUIActivator.pluginLog().logError(e);
		} catch (InterruptedException e) {
			CamelEditorUIActivator.pluginLog().logError(e);
			Thread.currentThread().interrupt();
		}
	}

	private void clearUI() {
		if (restOpsComposite != null && !restOpsComposite.isDisposed()) {
			Control[] children = restOpsComposite.getChildren();
			for (int i = 0; i < children.length; i++) {
				Control child = children[i];
				child.dispose();
			}
			restOpsComposite.layout();
		}
	}

	private String getAttrValue(Element element, String attrName) {
		if (element.getAttribute(attrName) != null) {
			String tempValue = element.getAttribute(attrName);
			if (!Strings.isEmpty(tempValue)) {
				return tempValue;
			}
		}
		return null;
	}
	
	private void setControlValue(Control control, String attrName, RestConfigurationElement element) {
		String value = null;
		if (element != null && element.getXmlNode() instanceof Element) {
			value = getAttrValue((Element)element.getXmlNode(), attrName);
		}
		if (value == null) {
			value = ""; //$NON-NLS-1$
		}
		if (RestConfigurationElementEIP.PROP_BINDINGMODE.equals(attrName) && Strings.isEmpty(value)) {
			value = OFF; // set default 
		}
		if (control instanceof Text) {
			((Text)control).setText(value);
		} else if (control instanceof Combo) {
			((Combo)control).setText(value);
		}
	}
	
	private void updateRestConfigurationControls() {
		setControlValue(contextPathText, RestConfigurationElementEIP.PROP_CONTEXTPATH, rce);
		setControlValue(hostText, RestConfigurationElementEIP.PROP_HOST, rce);
		setControlValue(portText, RestConfigurationElementEIP.PROP_PORT, rce);
		setControlValue(componentCombo, RestConfigurationElementEIP.PROP_COMPONENT, rce);
		setControlValue(bindingModeCombo, RestConfigurationElementEIP.PROP_BINDINGMODE, rce);
	}
	
	private void refreshRestConfigurationSection() {
		if (!ctx.getRestConfigurations().isEmpty()) {
			rce = (RestConfigurationElement) ctx.getRestConfigurations().values().iterator().next();
		} else {
			rce = null;
		}

		if (rce != null) {
			enableRestConfigurationTabSection(true);
			addRestConfigAction.setEnabled(false);
			deleteRestConfigAction.setEnabled(true);
		} else {
			enableRestConfigurationTabSection(false);
			addRestConfigAction.setEnabled(true);
			deleteRestConfigAction.setEnabled(false);
		}
		updateRestConfigurationControls();
	}
	
	private void refreshRestSection() {
		restList.setInput(null);
		if (!ctx.getRestElements().isEmpty()) {
			restList.setInput(ctx.getRestElements().values());
			if (selection == null) {
				restList.setSelection(new StructuredSelection(restList.getElementAt(0)), true);
			} else if (selection instanceof RestElement) {
				RestElement relement = (RestElement) selection;
				selectRestElement(relement);
			} else if (selection instanceof RestVerbElement) {
				RestVerbElement verbElement = (RestVerbElement) selection;
				selectRestVerbElement(verbElement);
			}
		} else {
			clearUI();
		}
		addRestElementAction.setEnabled(!ctx.getRestConfigurations().isEmpty());
		deleteRestElementAction.setEnabled(
				!ctx.getRestElements().isEmpty() && !restList.getStructuredSelection().isEmpty());
		
		addRestOperationAction.setEnabled(
				!ctx.getRestElements().isEmpty() && !restList.getStructuredSelection().isEmpty());
		deleteRestOperationAction.setEnabled(selection instanceof RestVerbElement);
	}	
	
	// made public for testing purposes
	public void selectRestElement(RestElement relement) {
		String id = relement.getId();
		Iterator<AbstractCamelModelElement> restElementIter = ctx.getRestElements().values().iterator();
		AbstractCamelModelElement found = null;
		while (found == null && restElementIter.hasNext()) {
			AbstractCamelModelElement element = restElementIter.next();
			if (element.getId().equals(id)) {
				found = element;
			}
		}
		if (found != null) {
			selection = found;
		} else {
			if (restList.getList().getItemCount() > 0) {
				selection = restList.getElementAt(0);
			} else {
				selection = null;
			}
		}
		if (selection != null) {
			IStructuredSelection ssel = new StructuredSelection(selection);
			restList.setSelection(ssel, true);
			setSelection(ssel);
		} else {
			restList.setSelection(StructuredSelection.EMPTY);
			setSelection(StructuredSelection.EMPTY);
		}
	}

	// made public for testing purposes
	public void selectRestVerbElement(RestVerbElement rve) {
		RestElement re = (RestElement) rve.getParent();
		String restId = re.getId();
		String restVerbId = rve.getId();
		Iterator<AbstractCamelModelElement> restElementIter = ctx.getRestElements().values().iterator();
		AbstractCamelModelElement found = null;
		while (found == null && restElementIter.hasNext()) {
			AbstractCamelModelElement element = restElementIter.next();
			if (element.getId().equals(restId)) {
				found = element;
			}
		}
		if (found != null) {
			RestElement relement = (RestElement) found;
			restList.setSelection(new StructuredSelection(relement), true);
			RestVerbElement selRve = (RestVerbElement) relement.getRestOperations().get(restVerbId);
			if (selRve != null) {
				updateRestVerbDisplayForSelection(selRve);
				selection = selRve;
				setSelection(new StructuredSelection(selRve));
			}
		}
	}
	
	private void clearSelection() {
		selection = null;
		setSelection(StructuredSelection.EMPTY);
	}
	
	private void reselect(Object toSelect) {
		if (toSelect != null) {
			if (toSelect instanceof RestVerbElement) {
				RestVerbElement rve = (RestVerbElement) toSelect;
				selectRestVerbElement(rve);
			} else if (toSelect instanceof RestElement) {
				RestElement relement = (RestElement) toSelect;
				selectRestElement(relement);
			} else {
				clearSelection();
			}
		} else {
			clearSelection();
		}
		
	}
	
	public void reload() {
		ctx = getCamelContext(parentEditor.getDesignEditor().getModel());
		final Object oldSelection = selection;
		refreshRestConfigurationSection();
		refreshRestSection();
		form.layout(true);
		toolkit.decorateFormHeading(form.getForm());
		
		// only try to update selection if we have one to go back to
		if (oldSelection != null) {
			reselect(oldSelection);
		}
	}

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		listeners.add(listener);
	}

	@Override
	public ISelection getSelection() {
		if (selection != null) {
			return new StructuredSelection(selection);
		}
		return StructuredSelection.EMPTY;
	}

	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		listeners.remove(listener);		
	}

	@Override
	public void setSelection(ISelection selection) {
		Object[] list = listeners.getListeners();
		for (int i = 0; i < list.length; i++) {
			// limit to just the selection changed events on THIS page
			if (parentEditor.getActivePage() == CamelEditor.REST_CONF_INDEX) {
				((ISelectionChangedListener) list[i]).selectionChanged(new SelectionChangedEvent(this, selection));
			}
		}
	}

	private void updateSelectionDisplay(Control oldControl, Control newControl) {
		if (oldControl != null && getDataFromSelectedUIElement(oldControl) != null) {
			AbstractRestCamelModelElement node = (AbstractRestCamelModelElement) getDataFromSelectedUIElement(oldControl);
			Color background = colorManager.getBackgroundColorForType(""); //$NON-NLS-1$
			if (node != null && node.getXmlNode() != null) {
				background = colorManager.getBackgroundColorForType(node.getXmlNode().getNodeName());
			}
			updateBorder(oldControl, background);
		}
		updateBorder(newControl, newControl.getDisplay().getSystemColor(SWT.COLOR_LIST_SELECTION));
	}
	
	private void updateBorder(Control control, Color color) {
		if (control instanceof Composite) {
			Composite composite = (Composite) control;
			composite.setBackground(color);
		} else if (control.getParent() != null) {
			updateBorder(control.getParent(), color);
		}
	}

	private void updateRestVerbDisplayForSelection(RestVerbElement rve) {
		Control[] verbComposites = restOpsComposite.getChildren();
		for (int i=0; i < verbComposites.length; i++) {
			Composite operation = (Composite) verbComposites[i];
			Object data = getDataFromSelectedUIElement(operation);
			if (data != null && data.equals(rve)) {
				selectedControl = operation;
				updateSelectionDisplay(selectedControl, operation);
				break;
			}
		}
	}
	
	class RestVerbSelectionListener implements Listener {
		@Override
		public void handleEvent(Event event) {
			Control newControl = (Control) event.widget;
			updateSelectionDisplay(selectedControl, newControl);
			selectedControl = newControl;
			selection = getDataFromSelectedUIElement(newControl);
			addRestOperationAction.setEnabled(!restList.getStructuredSelection().isEmpty());
			deleteRestOperationAction.setEnabled(selection instanceof RestVerbElement);
			if (selection != null) {
				setSelection(new StructuredSelection(selection));
			}
		}
	}
	
	/**
	 * Public for tests and wizard.
	 */
	public void addRestOperation() {
		Display.getDefault().syncExec(() -> {
			if (!restList.getStructuredSelection().isEmpty()) {
				AddRestOperationWizard wizard = new AddRestOperationWizard(this);
				WizardDialog wizdlg = new WizardDialog(Display.getCurrent().getActiveShell(), wizard);
				wizdlg.setBlockOnOpen(true);
				wizdlg.open();
			}
		});
	}
	
	public void createRestOperation(String verbType, String uri, String id, String toUri) {
		RestElement re = (RestElement) restList.getStructuredSelection().getFirstElement();
		
		// must select which type of verb to create - defaulting to GET for now
		RestVerbElement newrve = 
				util.createRestVerbElementNode(ctx, verbType);
		newrve.initialize();
		newrve.setId(id);
		newrve.setParameter(RestVerbElementEIP.PROP_URI, uri);
		if (!Strings.isBlank(toUri)) {
			newrve.setParameter(RestVerbElementEIP.PROP_TO_URI, toUri);
		}
		re.addRestOperation(newrve);
		selection = re;
		reload();
	}

	/**
	 * Public for tests.
	 */
	public void addRestElement() {
		Display.getDefault().syncExec(() -> {
			RestElement newre = util.createRestElementNode(ctx);
			newre.initialize();
			ctx.addRestElement(newre);
			reload();
			selectRestElement(newre);
		});
	}

	/**
	 * Public for tests.
	 */
	public void removeRestElement() {
		Display.getDefault().syncExec(() -> {
			// delete selected rest element
			if (!restList.getStructuredSelection().isEmpty()) {
				RestElement re = (RestElement) restList.getStructuredSelection().getFirstElement();
				ctx.removeRestElement(re);
				selection = null;
				setSelection(StructuredSelection.EMPTY);
			}
			reload();
		});
	}

	/**
	 * Public for tests.
	 */
	public void removeRestOperation() {
		Display.getDefault().syncExec(() -> {
			// delete selected rest operation
			if (selection instanceof RestVerbElement) {
				RestVerbElement rve = (RestVerbElement)selection;
				RestElement re = (RestElement) rve.getParent();
				re.removeRestOperation(rve);
				selection = null;
				reload();
				selectRestElement(re);
			}
		});
	}
}
