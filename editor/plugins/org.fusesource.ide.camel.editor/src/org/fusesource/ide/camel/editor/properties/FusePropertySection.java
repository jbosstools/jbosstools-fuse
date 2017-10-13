/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.fusesource.ide.camel.editor.properties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.map.WritableMap;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.internal.forms.widgets.FormsResources;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.fusesource.ide.camel.editor.internal.CamelEditorUIActivator;
import org.fusesource.ide.camel.editor.utils.MavenUtils;
import org.fusesource.ide.camel.editor.utils.NodeUtils;
import org.fusesource.ide.camel.model.service.core.catalog.Dependency;
import org.fusesource.ide.camel.model.service.core.catalog.IParameterContainer;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelCatalogCacheManager;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelModel;
import org.fusesource.ide.camel.model.service.core.catalog.components.Component;
import org.fusesource.ide.camel.model.service.core.catalog.dataformats.DataFormat;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.service.core.catalog.languages.Language;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelBasicModelElement;
import org.fusesource.ide.camel.model.service.core.util.CamelComponentUtils;
import org.fusesource.ide.camel.model.service.core.util.PropertiesUtils;
import org.fusesource.ide.foundation.core.util.Strings;
import org.w3c.dom.Node;

/**
 * @author Aurelien Pupier
 */
public abstract class FusePropertySection extends AbstractPropertySection {

	public static final String DEFAULT_GROUP = "General";
	public static final String GROUP_PATH = "Path";
	public static final String GROUP_COMMON = "Common";
	public static final String GROUP_CONSUMER = "Consumer";
	public static final String GROUP_PRODUCER = "Producer";

	protected FormToolkit toolkit;
	protected Form form;
	protected CTabFolder tabFolder;
	protected List<CTabItem> tabs = new ArrayList<>();
	protected AbstractCamelModelElement selectedEP;
	protected AbstractCamelModelElement lastSelectedEP;
	protected DataBindingContext dbc;
	protected IObservableMap modelMap = new WritableMap<>();
	protected Composite parent;
	protected TabbedPropertySheetPage aTabbedPropertySheetPage;

	protected Component component; // used for connectors
	protected Eip eip; // used for eips

	@Override
	public void dispose() {
		if (this.form != null)
			this.form.dispose();
		disposeTabs();
		if (this.tabFolder != null)
			this.tabFolder.dispose();
		if (toolkit != null) {
			toolkit.dispose();
			toolkit = null;
		}
		this.aTabbedPropertySheetPage = null;
		this.component = null;
		this.eip = null;
		super.dispose();
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		
		AbstractCamelModelElement n = NodeUtils.getSelectedNode(selection);
		
		if (!Objects.equals(lastSelectedEP, n)) {
			if (n.getUnderlyingMetaModelObject() != null) {
				selectedEP = n;
				eip = PropertiesUtils.getEipFor(selectedEP);
				if (selectedEP.isEndpointElement()) {
					component = PropertiesUtils.getComponentFor(selectedEP);
				}
			} else {
				selectedEP = null;
			}

			initSectionFor(selectedEP);
		}
	}

	private void initSectionFor(AbstractCamelModelElement n) {
		dbc = new DataBindingContext();
		createTabFolder();

		if (n != null && n.getUnderlyingMetaModelObject() != null) {
			String headerText = selectedEP.getDisplayText();
			form.setText(headerText);
		} else {
			form.setText("");
		}
		lastSelectedEP = n;
		
		int idx = Math.max(tabFolder.getSelectionIndex(), 0);

		if (!tabs.isEmpty()) {
			for (CTabItem tab : this.tabs) {
				if (!tab.isDisposed())
					tab.dispose();
			}
			tabs.clear();
		}

		// now generate the tab contents
		createContentTabs(tabFolder);

		tabFolder.setSingle(tabFolder.getItemCount() == 1);
		tabFolder.setSelection(idx >= tabFolder.getItemCount() ? 0 : idx);

		form.redraw();
		form.layout();
		form.update();
	}

	private void disposeTabs() {
		if (!tabs.isEmpty()) {
			for (CTabItem tab : this.tabs) {
				if (!tab.isDisposed())
					tab.dispose();
			}
			tabs.clear();
		}
	}
	
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);

		this.toolkit = new FormToolkit(parent.getDisplay());
		this.parent = parent;
		this.aTabbedPropertySheetPage = aTabbedPropertySheetPage;

		// now setup the file binding properties page
		parent.setLayout(new GridLayout());
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
	}
	
	protected void createStandardTabLayout(String sectionTitle) {
		parent.setLayout(new GridLayout());
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));

		form = toolkit.createForm(parent);
		form.setLayoutData(new GridData(GridData.FILL_BOTH));
		form.getBody().setLayout(new GridLayout(1, false));

		Composite sbody = form.getBody();

		tabFolder = new CTabFolder(sbody, SWT.TOP | SWT.FLAT);
		toolkit.adapt(tabFolder, true, true);
		tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));

		Color selectedColor = toolkit.getColors().getColor(IFormColors.SEPARATOR);
		tabFolder.setSelectionBackground(new Color[] { selectedColor, toolkit.getColors().getBackground() },
				new int[] { 20 }, true);
		tabFolder.setCursor(FormsResources.getHandCursor());
		toolkit.paintBordersFor(tabFolder);

		form.setText(sectionTitle);
		toolkit.decorateFormHeading(form);

		form.layout();
		tabFolder.setSelection(0);
	}

	/**
	 * creates the tab folder to hold all tabs
	 */
	private void createTabFolder() {

		if (this.form != null && !this.form.isDisposed()) {
			form.dispose();
		}

		this.form = this.toolkit.createForm(this.parent);
		this.form.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.form.getBody().setLayout(new GridLayout(1, false));

		if (tabFolder != null && !tabFolder.isDisposed()) {
			tabFolder.dispose();
		}

		tabFolder = new CTabFolder(form.getBody(), SWT.TOP | SWT.FLAT);
		toolkit.adapt(tabFolder, true, true);
		tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));

		Color selectedColor = toolkit.getColors().getColor(IFormColors.SEPARATOR);
		tabFolder.setSelectionBackground(new Color[] { selectedColor, toolkit.getColors().getBackground() },
				new int[] { 20 }, true);
		tabFolder.setCursor(FormsResources.getHandCursor());
		toolkit.paintBordersFor(tabFolder);

		toolkit.decorateFormHeading(form);

		form.layout();
		parent.layout(true);
		tabFolder.setSelection(0);
	}

	/**
	 * creates the tabs needed to be displayed to users
	 * 
	 * @param tabFolder
	 */
	protected abstract void createContentTabs(CTabFolder tabFolder);

	/**
	 * /!\ public for test purpose only
	 * 
	 * @param toolkit
	 * @param page
	 *            The page on which it will be created
	 * @param p
	 *            The property for which the label is generated
	 */
	public void createPropertyLabel(FormToolkit toolkit, Composite page, Parameter p) {
		String s = computePropertyDisplayName(p);
		Label l = toolkit.createLabel(page, s);
		l.setLayoutData(new GridData());
		addDescriptionAsTooltip(p, l);
	}

	protected Display getDisplay() {
		return Display.getDefault();
	}

	protected String computePropertyDisplayName(Parameter parameter) {
		String s = Strings.humanize(parameter.getName());
		// if the parameter is of kind expression we want to display "Language"
		// as the parameter label instead of Expression as this is misleading
		if (CamelComponentUtils.isExpressionProperty(parameter)) {
			s = "Language";
		}
		if (PropertiesUtils.isRequired(parameter)) {
			s += " *";
		}
		if (PropertiesUtils.isDeprecated(parameter)) {
			s += " (deprecated)";
		}
		return s;
	}

	private void addDescriptionAsTooltip(Parameter parameter, Label label) {
		String description = parameter.getDescription();
		if (description != null) {
			label.setToolTipText(description.replaceAll("&", "&&"));
		}
	}

	/**
	 * retrieves the camel model
	 * 
	 * @param modelElement
	 * @return
	 */
	protected CamelModel getCamelModel(AbstractCamelModelElement modelElement) {
		CamelModel model = null;
		if (modelElement != null) {
			model = modelElement.getCamelFile().getCamelModel();
		}
		if (model == null && lastSelectedEP != null) {
			model = lastSelectedEP.getCamelFile().getCamelModel();
		}
		return model;
	}

	/**
	 * called when user switches the expression language
	 * 
	 * @param language
	 *            the new language for the expression
	 * @param eform
	 *            the expandable form to use
	 * @param expressionElement
	 *            the expression element if simple expression, otherwise it will
	 *            be the container element which contains the expression element
	 *            as parameter "expression"
	 * @param page
	 *            the page
	 * @param prop
	 *            the property which is currently used
	 */
	protected void languageChanged(String language, Composite eform, AbstractCamelModelElement expressionElement, Composite page, Parameter prop) {
		IProject project = selectedEP.getCamelFile().getResource().getProject();
		for (Control co : eform.getChildren())
			if (co.getData("fuseExpressionClient") != null)
				co.dispose();
		Composite client = getWidgetFactory().createComposite(eform);
		client.setData("fuseExpressionClient", true);
		client.setLayoutData(new GridData(GridData.FILL_BOTH));
		client.setLayout(new GridLayout(4, false));

		AbstractCamelModelElement uiExpressionElement = null;

		Language lang = getCamelModel(expressionElement).getLanguage(language);

		if (AbstractCamelModelElement.NODE_KIND_EXPRESSION.equalsIgnoreCase(prop.getName())) {
			// normal expression subnode - no cascading -> when.<expression>
			// the content of expressionElement is the language node itself
			if (expressionElement != null && expressionElement.getTagNameWithoutPrefix().equals(language) == false) {
				Node oldExpNode = null;
				for (int i = 0; i < selectedEP.getXmlNode().getChildNodes().getLength(); i++) {
					if (org.fusesource.ide.foundation.core.util.CamelUtils
							.getTagNameWithoutPrefix(selectedEP.getXmlNode().getChildNodes().item(i))
							.equals(expressionElement.getTagNameWithoutPrefix())) {
						oldExpNode = selectedEP.getXmlNode().getChildNodes().item(i);
						break;
					}
				}
				if (language.trim().length() > 0) {
					Node expNode = selectedEP.createElement(language,
							selectedEP != null && selectedEP.getXmlNode() != null ? selectedEP.getXmlNode().getPrefix()
									: null);
					expressionElement = new CamelBasicModelElement(this.selectedEP, expNode);
					selectedEP.setParameter(prop.getName(), expressionElement);
					selectedEP.getXmlNode().replaceChild(expNode, oldExpNode);
					
					if (lang != null) { // some languages are not defined in catalog like "method"
						updateDependencies(lang.getDependencies(), project);
					}
				} else {
					// user wants to delete the expression
					selectedEP.getXmlNode().removeChild(oldExpNode);
					selectedEP.removeParameter(prop.getName());
				}
			} else if (expressionElement == null && language.trim().length() > 0) {
				// no expression set, but now we set one
				Node expNode = selectedEP.createElement(language, selectedEP != null && selectedEP.getXmlNode() != null
						? selectedEP.getXmlNode().getPrefix() : null);
				expressionElement = new CamelBasicModelElement(this.selectedEP, expNode);
				selectedEP.getXmlNode().insertBefore(expNode, selectedEP.getXmlNode().getFirstChild());
				this.selectedEP.setParameter(prop.getName(), expressionElement);

				if (lang != null) { // some languages are not defined in catalog like "method"
					updateDependencies(lang.getDependencies(), project);
				}
			}
			uiExpressionElement = expressionElement;

		} else {

			// cascaded expression subnode -> onException.handled.<expression>
			// the content of expressionElement is the container element which
			// holds the expression as parameter "expression"
			if (expressionElement != null && expressionElement.getParameter(AbstractCamelModelElement.NODE_KIND_EXPRESSION) != null) {
				// 1. container element exists and expression element exists
				Node oldExpNode = null;
				List<String> langs = Arrays.asList(CamelComponentUtils.getOneOfList(prop));
				for (int i = 0; i < expressionElement.getXmlNode().getChildNodes().getLength(); i++) {
					Node n = expressionElement.getXmlNode().getChildNodes().item(i);
					if (langs.contains(org.fusesource.ide.foundation.core.util.CamelUtils.getTagNameWithoutPrefix(n))) {
						oldExpNode = n;
						break;
					}
				}
				AbstractCamelModelElement expElement = (AbstractCamelModelElement) expressionElement
						.getParameter(AbstractCamelModelElement.NODE_KIND_EXPRESSION);
				if (expElement.getTagNameWithoutPrefix().equals(language) == false) {
					if (language.trim().length() > 0) {
						Node expNode = selectedEP.createElement(language,
								selectedEP != null && selectedEP.getXmlNode() != null
										? selectedEP.getXmlNode().getPrefix() : null);
						uiExpressionElement = new CamelBasicModelElement(expressionElement, expNode);
						expressionElement.getXmlNode().replaceChild(expNode, oldExpNode);
						expressionElement.setParameter(AbstractCamelModelElement.NODE_KIND_EXPRESSION, uiExpressionElement);
						
						if (lang != null) { // some languages are not defined in catalog like "method"
							updateDependencies(lang.getDependencies(), project);
						}
					} else {
						// user deletes the expression
						selectedEP.getXmlNode().removeChild(expressionElement.getXmlNode());
						selectedEP.removeParameter(prop.getName());
					}
				} else {
					uiExpressionElement = expElement;
				}

			} else if (expressionElement != null && expressionElement.getParameter(AbstractCamelModelElement.NODE_KIND_EXPRESSION) == null) {
				// 2. container element exists but no expression element exists
				Node expNode = selectedEP.createElement(language, selectedEP != null && selectedEP.getXmlNode() != null
						? selectedEP.getXmlNode().getPrefix() : null);
				uiExpressionElement = new CamelBasicModelElement(expressionElement, expNode);
				expressionElement.getXmlNode().appendChild(expNode);
				expressionElement.setParameter(AbstractCamelModelElement.NODE_KIND_EXPRESSION, uiExpressionElement);

			} else if (expressionElement == null && language.trim().length() > 0) {
				// 3. No container but language set
				Node expContainerNode = selectedEP.createElement(prop.getName(),
						selectedEP != null && selectedEP.getXmlNode() != null ? selectedEP.getXmlNode().getPrefix()
								: null);
				Node expNode = selectedEP.createElement(language, selectedEP != null && selectedEP.getXmlNode() != null
						? selectedEP.getXmlNode().getPrefix() : null);
				AbstractCamelModelElement expContainerElement = new CamelBasicModelElement(selectedEP,
						expContainerNode);
				expressionElement = new CamelBasicModelElement(expContainerElement, expNode);
				expContainerElement.getXmlNode().appendChild(expNode);
				selectedEP.getXmlNode().insertBefore(expContainerNode, selectedEP.getXmlNode().getFirstChild());
				expContainerElement.setParameter(AbstractCamelModelElement.NODE_KIND_EXPRESSION, expressionElement);
				this.selectedEP.setParameter(prop.getName(), expContainerElement);
				uiExpressionElement = expressionElement;

				if (lang != null) { // some languages are not defined in catalog like "method"
					updateDependencies(lang.getDependencies(), project);
				}
			}
		}

		prepareExpressionUIForLanguage(language, uiExpressionElement, client);
		page.layout(true);
		refresh();
		eform.layout(true);
		aTabbedPropertySheetPage.resizeScrolledComposite();
	}

	/**
	 * prepares the ui for expression elements
	 * 
	 * @param language
	 * @param expressionElement
	 * @param parent
	 */
	protected void prepareExpressionUIForLanguage(String language, AbstractCamelModelElement expressionElement,
			Composite parent) {
		CamelModel model = getCamelModel(expressionElement);
		// now create the new fields
		Language lang = model.getLanguage(language);
		if (lang != null) {
			List<Parameter> props = lang.getParameters();
			props.sort(new ParameterPriorityComparator());

			for (Parameter p : props) {
				createPropertyLabel(toolkit, parent, p);

				// Field
				Control field = getControlForParameter(p, parent, expressionElement, lang);
				field.setToolTipText(p.getDescription());
			}
		} else {
			// seems to be not in language catalog - use eip catalog
			Eip eip = model.getEip(language);
			if (eip != null) {
				List<Parameter> props = eip.getParameters();
				props.sort(new ParameterPriorityComparator());

				for (Parameter p : props) {
					createPropertyLabel(toolkit, parent, p);

					// Field
					Control field = getControlForParameter(p, parent, expressionElement, lang);
					field.setToolTipText(p.getDescription());
				}
			}
		}
	}

	/**
	 * called when user switches the expression language
	 * 
	 * @param language
	 *            the new language for the expression
	 * @param eform
	 *            the expandable form to use
	 * @param dataFormatElement
	 *            the expression element if simple expression, otherwise it will
	 *            be the container element which contains the expression element
	 *            as parameter "expression"
	 * @param page
	 *            the page
	 * @param prop
	 *            the property which is currently used
	 */
	protected void dataFormatChanged(String dataformat, Composite eform, AbstractCamelModelElement dataFormatElement, Composite page, Parameter prop) {
		IProject project = selectedEP.getCamelFile().getResource().getProject();
		for (Control co : eform.getChildren())
			if (co.getData("fuseDataFormatClient") != null)
				co.dispose();
		Composite client = getWidgetFactory().createComposite(eform);
		client.setData("fuseDataFormatClient", true);
		client.setLayoutData(new GridData(GridData.FILL_BOTH));
		client.setLayout(new GridLayout(4, false));

		DataFormat df = getCamelModel(dataFormatElement).getDataFormat(dataformat);
		if (df == null) {
			Collection<DataFormat> dfs = getCamelModel(dataFormatElement).getDataFormatsByTag(dataformat);
			if (dfs != null && !dfs.isEmpty() && dfs.size() == 1) {
				df = dfs.iterator().next(); // take first element
			}
		}
		if (dataFormatElement != null && df != null && dataFormatElement.getTagNameWithoutPrefix().equals(dataformat) == false) {
			Node oldExpNode = null;
			for (int i = 0; i < selectedEP.getXmlNode().getChildNodes().getLength(); i++) {
				final Node childNode = selectedEP.getXmlNode().getChildNodes().item(i);
				if (org.fusesource.ide.foundation.core.util.CamelUtils.getTagNameWithoutPrefix(childNode)
						.equalsIgnoreCase(dataFormatElement.getTagNameWithoutPrefix())) {
					oldExpNode = childNode;
					break;
				}
			}
			if (dataformat.trim().length() > 0) {
				Node expNode = selectedEP.createElement(dataformat, selectedEP.getXmlNode() != null ? selectedEP.getXmlNode().getPrefix() : null);
				dataFormatElement = new CamelBasicModelElement(this.selectedEP, expNode);
				selectedEP.setParameter(prop.getName(), dataFormatElement);
				selectedEP.getXmlNode().replaceChild(expNode, oldExpNode);

				updateDependencies(df.getDependencies(), project);
			} else {
				// user wants to delete the expression
				selectedEP.getXmlNode().removeChild(oldExpNode);
				selectedEP.removeParameter(prop.getName());
			}
		} else if (dataFormatElement == null && dataformat.trim().length() > 0 && df != null) {
			// no expression set, but now we set one
			Node expNode = selectedEP.createElement(dataformat, selectedEP.getXmlNode() != null ? selectedEP.getXmlNode().getPrefix() : null);
			dataFormatElement = new CamelBasicModelElement(this.selectedEP, expNode);
			selectedEP.getXmlNode().insertBefore(expNode, selectedEP.getXmlNode().getFirstChild());
			this.selectedEP.setParameter(prop.getName(), dataFormatElement);
			updateDependencies(df.getDependencies(), project);
		}

		prepareDataFormatUIForDataFormat(dataformat, dataFormatElement, client);
		page.layout(true);
		refresh();
		eform.layout(true);
		aTabbedPropertySheetPage.resizeScrolledComposite();
	}
	
	private void updateDependencies(List<Dependency> dependencies, IProject project) {
		MavenUtils utils = new MavenUtils();
		try {
			utils.updateMavenDependencies(dependencies, project);
		} catch (CoreException e) {
			CamelEditorUIActivator.pluginLog().logError(e);
		}
	}

	protected void updateDependenciesForDataFormat(AbstractCamelModelElement selectedEP, String newValue) {
		if (newValue != null) {
			IProject project = selectedEP.getCamelFile().getResource().getProject();
			CamelModel m = CamelCatalogCacheManager.getInstance().getCamelModelForProject(project);
			if (m != null) {
				DataFormat df = m.getDataFormat(newValue);
				if (df != null) {
					updateDependencies(df.getDependencies(), project);
				}
			}
		}
	}
	
	protected void updateDependenciesForLanguage(AbstractCamelModelElement selectedEP, String newValue) {
		if (newValue != null) {
			IProject project = selectedEP.getCamelFile().getResource().getProject();
			CamelModel m = CamelCatalogCacheManager.getInstance().getCamelModelForProject(project);
			if (m != null) {
				Language l = m.getLanguage(newValue);
				if (l != null) {
					updateDependencies(l.getDependencies(), project);
				}
			}
		}
	}
	
	/**
	 * prepares the ui for the data format element
	 * 
	 * @param dataformat
	 * @param dataFormatElement
	 * @param parent
	 */
	protected void prepareDataFormatUIForDataFormat(String dataformat, AbstractCamelModelElement dataFormatElement,
			Composite parent) {
		CamelModel model = getCamelModel(dataFormatElement);

		// now create the new fields
		Eip df = model.getEip(dataformat);
		if (df != null) {
			List<Parameter> props = df.getParameters();
			props.sort(new ParameterPriorityComparator());

			for (Parameter p : props) {
				createPropertyLabel(toolkit, parent, p);

				// Field
				Control field = getControlForParameter(p, parent, dataFormatElement, df);
				field.setToolTipText(p.getDescription());
			}
		}
	}

	/**
	 * returns the control for the given parameter
	 * 
	 * @param p
	 * @param parent
	 * @param camelModelElement
	 * @param parameterContainer
	 * @return
	 */
	protected Control getControlForParameter(final Parameter p, Composite parent,
			final AbstractCamelModelElement camelModelElement, IParameterContainer parameterContainer) {
		Control c;

		// CHOICE
		if (CamelComponentUtils.isChoiceProperty(p)) {
			String initialTextValue;
			if (camelModelElement != null && camelModelElement.getParameter(p.getName()) != null) {
				initialTextValue = (String) camelModelElement.getParameter(p.getName());
			} else if (parameterContainer != null) {
				initialTextValue = parameterContainer.getParameter(p.getName()).getDefaultValue();
			} else {
				initialTextValue = p.getDefaultValue();
			}
			CCombo txtField = getWidgetFactory().createCCombo(parent, SWT.DROP_DOWN | SWT.LEFT | SWT.READ_ONLY);
			txtField.setItems(CamelComponentUtils.getChoicesWithExtraEmptyEntry(p));
			txtField.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					CCombo txt = (CCombo) e.getSource();
					camelModelElement.setParameter(p.getName(), txt.getText());
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			});
			txtField.setLayoutData(createPropertyFieldLayoutData());
			for (int idx = 0; idx < txtField.getItemCount(); idx++) {
				if (txtField.getItem(idx).equalsIgnoreCase(initialTextValue)) {
					txtField.select(idx);
					break;
				}
			}
			c = txtField;

		// BOOLEAN PROPERTIES
		} else if (CamelComponentUtils.isBooleanProperty(p)) {
			final Button checkBox = getWidgetFactory().createButton(parent, "", SWT.CHECK);
			Boolean b;
			if (camelModelElement != null && camelModelElement.getParameter(p.getName()) != null) {
				Object paramValue = camelModelElement.getParameter(p.getName());
				if (paramValue instanceof String) {
					b = Boolean.valueOf((String) paramValue);
				} else {
					b = (Boolean) paramValue;
				}
			} else if (parameterContainer != null) {
				b = Boolean.parseBoolean(parameterContainer.getParameter(p.getName()).getDefaultValue());
			} else {
				b = Boolean.parseBoolean(p.getDefaultValue());
			}
			checkBox.setSelection(b);
			checkBox.addSelectionListener(new SelectionAdapter() {
				/*
				 * (non-Javadoc)
				 * 
				 * @see
				 * org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.
				 * eclipse.swt.events.SelectionEvent)
				 */
				@Override
				public void widgetSelected(SelectionEvent e) {
					camelModelElement.setParameter(p.getName(), checkBox.getSelection());
				}
			});
			checkBox.setLayoutData(createPropertyFieldLayoutData());
			c = checkBox;

			// TEXT PROPERTIES
		} else if (CamelComponentUtils.isTextProperty(p)) {
			String initialTextValue;
			if (camelModelElement != null && camelModelElement.getParameter(p.getName()) != null) {
				initialTextValue = (String) camelModelElement.getParameter(p.getName());
			} else if (parameterContainer != null) {
				initialTextValue = parameterContainer.getParameter(p.getName()).getDefaultValue();
			} else {
				initialTextValue = p.getDefaultValue();
			}
			Text txtField = getWidgetFactory().createText(parent, initialTextValue, SWT.SINGLE | SWT.LEFT);
			txtField.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					Text txt = (Text) e.getSource();
					camelModelElement.setParameter(p.getName(), txt.getText());
				}
			});
			txtField.setLayoutData(createPropertyFieldLayoutData());
			c = txtField;

			// NUMBER PROPERTIES
		} else if (CamelComponentUtils.isNumberProperty(p)) {
			String initialValue;
			if (camelModelElement != null && camelModelElement.getParameter(p.getName()) != null) {
				initialValue = (String) camelModelElement.getParameter(p.getName());
			} else if (parameterContainer != null) {
				initialValue = parameterContainer.getParameter(p.getName()).getDefaultValue();
			} else {
				initialValue = p.getDefaultValue();
			}
			Text txtField = getWidgetFactory().createText(parent, initialValue, SWT.SINGLE | SWT.RIGHT);
			txtField.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					Text txt = (Text) e.getSource();
					String val = txt.getText();
					try {
						Double.parseDouble(val);
						txt.setBackground(ColorConstants.white);
						camelModelElement.setParameter(p.getName(), txt.getText());
					} catch (NumberFormatException ex) {
						// invalid character found
						txt.setBackground(ColorConstants.red);
						return;
					}
				}
			});
			txtField.setLayoutData(createPropertyFieldLayoutData());
			c = txtField;

			// grand Children inside a list of nodes
		} else if(camelModelElement != null && camelModelElement.getParameter(p.getName()) instanceof List){
			//TODO: use something better than a text for a list of elements as we have the obvious limitation of not supporting the ","
			List<String> initialValue = (List<String>)camelModelElement.getParameter(p.getName());
			String delimiter = ",";
			String collect = initialValue.stream().collect(Collectors.joining(delimiter));
			Text txtField = getWidgetFactory().createText(parent, collect, SWT.SINGLE | SWT.LEFT);
			txtField.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					Text txt = (Text) e.getSource();
					camelModelElement.setParameter(p.getName(), Arrays.asList(txt.getText().split(delimiter)));
				}
			});
			txtField.setLayoutData(createPropertyFieldLayoutData());
			c = txtField;
			
			// OTHER
		} else {
			String initialValue;
			if (camelModelElement != null && camelModelElement.getParameter(p.getName()) != null) {
				initialValue = (String) camelModelElement.getParameter(p.getName());
			} else if (parameterContainer != null) {
				initialValue = parameterContainer.getParameter(p.getName()).getDefaultValue();
			} else {
				initialValue = p.getDefaultValue();
			}
			Text txtField = getWidgetFactory().createText(parent, initialValue, SWT.SINGLE | SWT.LEFT);
			txtField.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					Text txt = (Text) e.getSource();
					camelModelElement.setParameter(p.getName(), txt.getText());
				}
			});
			txtField.setLayoutData(createPropertyFieldLayoutData());
			c = txtField;
		}

		return c;
	}

	protected void createHelpDecoration(Parameter parameter, Control control) {
		String description = parameter.getDescription();
		if (description != null) {
			ControlDecoration helpDecoration = new ControlDecoration(control, SWT.BOTTOM | SWT.LEFT);
			helpDecoration.setShowOnlyOnFocus(true);
			FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault()
					.getFieldDecoration(FieldDecorationRegistry.DEC_INFORMATION);
			helpDecoration.setImage(fieldDecoration.getImage());
			helpDecoration.setDescriptionText(description);
			control.setToolTipText(description);
		}
	}

	protected GridData createPropertyFieldLayoutData() {
		return GridDataFactory.fillDefaults().indent(5, 0).span(3, 1).grab(true, false).create();
	}
	
	@Override
	public void aboutToBeShown() {
		super.aboutToBeShown();
		initSectionFor(selectedEP);
	}
}
