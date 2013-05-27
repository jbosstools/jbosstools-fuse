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

package org.fusesource.ide.camel.editor.propertysheet;

import static org.fusesource.camel.tooling.util.Strings.splitCamelCase;
import static org.fusesource.ide.commons.util.Strings.capitalize;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.apache.camel.model.ExpressionNode;
import org.apache.camel.model.SetHeaderDefinition;
import org.apache.camel.model.language.ExpressionDefinition;
import org.apache.camel.spi.Required;
import org.apache.camel.util.ObjectHelper;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.IViewerObservableValue;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.fusesource.camel.tooling.util.Languages;
import org.fusesource.ide.camel.editor.Activator;
import org.fusesource.ide.camel.editor.EditorMessages;
import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.CamelModelHelper;
import org.fusesource.ide.camel.model.ExpressionPropertyDescriptor;
import org.fusesource.ide.camel.model.LanguageExpressionBean;
import org.fusesource.ide.camel.model.RouteContainer;
import org.fusesource.ide.camel.model.generated.Aggregate;
import org.fusesource.ide.camel.model.generated.ConvertBody;
import org.fusesource.ide.camel.model.generated.Log;
import org.fusesource.ide.camel.model.generated.Process;
import org.fusesource.ide.camel.model.generated.RemoveHeader;
import org.fusesource.ide.camel.model.generated.RemoveProperty;
import org.fusesource.ide.camel.model.generated.Resequence;
import org.fusesource.ide.camel.model.generated.SetHeader;
import org.fusesource.ide.camel.model.generated.SetOutHeader;
import org.fusesource.ide.camel.model.generated.SetProperty;
import org.fusesource.ide.camel.model.generated.Tooltips;
import org.fusesource.ide.camel.model.util.JaxbHelper;
import org.fusesource.ide.commons.Viewers;
import org.fusesource.ide.commons.properties.BooleanPropertyDescriptor;
import org.fusesource.ide.commons.properties.ComplexPropertyDescriptor;
import org.fusesource.ide.commons.properties.ComplexUnionPropertyDescriptor;
import org.fusesource.ide.commons.properties.EnumPropertyDescriptor;
import org.fusesource.ide.commons.properties.ListPropertyDescriptor;
import org.fusesource.ide.commons.properties.UnionTypeValue;
import org.fusesource.ide.commons.tree.Refreshable;
import org.fusesource.ide.commons.ui.actions.RunnableAction;
import org.fusesource.ide.commons.ui.form.Forms;
import org.fusesource.ide.commons.util.Sorts;
import org.fusesource.ide.commons.util.Strings;


/**
 * Shows the property details for the currently selected node
 */
public class DetailsSection extends NodeSectionSupport {

	private static final String[] EMPTY_STRING_ARRAY = new String[0];

	public static final String NODE_DESCRIPTION = "AbstractNode.Description";

	private Composite parent;
	private FormToolkit toolkit;
	private Form form;
	private DataBindingContext bindingContext;

	/**
	 * 
	 */
	public DetailsSection() {
		bindingContext = new DataBindingContext();
	}

	@Override
	public void dispose() {
		if (toolkit != null) {
			toolkit.dispose();
			toolkit = null;
		}
		super.dispose();
	}

	@Override
	protected synchronized void onNodeChanged(AbstractNode node) {
		if (form != null && !form.isDisposed()) {
			try {
				form.dispose();
			} catch (Exception e) {
				// ignore any expose exceptions
			}
		}
		form = null;

		if (parent.isDisposed()) return;

		parent.setLayout(new GridLayout());
		//parent.setLayout(new GridLayout(1, false));
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));

		form = toolkit.createForm(parent);
		form.setLayoutData(new GridData(GridData.FILL_BOTH));
		form.setText(EditorMessages.propertiesDetailsTitle);
		toolkit.decorateFormHeading(form);

		form.getBody().setLayout(new GridLayout(2, false));

		Composite sbody = form.getBody();

		if (node != null) {
			final IMessageManager mmng = form.getMessageManager();

			form.setText(node.getPatternName());

			IPropertyDescriptor idDescriptor = null;
			IPropertyDescriptor descriptionDescriptor = null;

			IPropertyDescriptor[] propertyDescriptors = node.getPropertyDescriptors();

			for (int i = 0; i < 2; i++) {
				for (IPropertyDescriptor descriptor : propertyDescriptors) {
					final Object id = descriptor.getId();
					if ("AbstractNode.Id".equals(id)) {
						idDescriptor = descriptor;
					} else if (NODE_DESCRIPTION.equals(id)) {
						descriptionDescriptor = descriptor;
					} else {
						String propertyName = getPropertyName(id);
						boolean mandatory = descriptor instanceof ExpressionPropertyDescriptor
								|| isMandatory(node, propertyName);
						if ((mandatory && i == 0) || (!mandatory && i == 1)) {
							createDecoratedTextField(descriptor, toolkit, sbody, mmng);
						}
					}
				}
			}

			if (idDescriptor != null || descriptionDescriptor != null) {
				if (idDescriptor != null) {
					createDecoratedTextField(idDescriptor, toolkit, sbody, mmng);
				}
				if (descriptionDescriptor != null) {
					createDecoratedTextField(descriptionDescriptor, toolkit, sbody, mmng);
				}
			}

			mmng.update();
		} else {
			form.setText(EditorMessages.propertiesDetailsTitle);
		}

		layoutForm();
	}

	protected void layoutForm() {
		// section.pack();
		// form.pack();
		if (form != null && !form.isDisposed()) {
			form.layout(true, true);
		}
		if (parent != null && !parent.isDisposed()) {
			parent.layout(true, true);
		}

		// in case of timing issues, lets do another layout just in case...
		Display.getCurrent().asyncExec(new Runnable() {
			@Override
			public void run() {
				if (form != null && !form.isDisposed()) {
					form.layout(true, true);
				}
				if (parent != null && !parent.isDisposed()) {
					parent.layout(true, true);
				}
			}
		});
	}

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		this.parent = parent;
		this.toolkit = new FormToolkit(parent.getDisplay());
		super.createControls(parent, aTabbedPropertySheetPage);
	}

	private void createDecoratedTextField(final IPropertyDescriptor descriptor, FormToolkit toolkit, Composite parent,
			final IMessageManager mmng) {

		final Object id = descriptor.getId();
		String labelText = descriptor.getDisplayName();
		String tooltip = Tooltips.tooltip(id.toString());
		GridData gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		boolean isDescription = NODE_DESCRIPTION.equals(id);
		Control widget;

		boolean isComplexProperty = descriptor instanceof ComplexPropertyDescriptor;
		boolean isUnionProperty = descriptor instanceof ComplexUnionPropertyDescriptor;
		if (isComplexProperty) {
			widget = bindNestedComplexProperties(toolkit, parent, mmng, (ComplexPropertyDescriptor) descriptor, id, labelText, tooltip);
		} else {
			Label label = toolkit.createLabel(parent, labelText);
			label.setToolTipText(tooltip);

			if (isDescription) {
				label.setLayoutData(gd);
			}
			if (isUnionProperty) {
				widget = bindNestedComplexUnionProperties(toolkit, parent, mmng, id, labelText, tooltip,
						(ComplexUnionPropertyDescriptor) descriptor);
			} else if (descriptor instanceof ExpressionPropertyDescriptor) {
				// lets create a composite and add a text are and a combo box
				// ExpandableComposite composite =
				// toolkit.createExpandableComposite(parent, SWT.HORIZONTAL);
				Composite composite = toolkit.createComposite(parent);
				GridLayout layout = new GridLayout(3, false);
				zeroMargins(layout);
				composite.setLayout(layout);
				widget = composite;

				Text text = toolkit.createText(composite, "");
				text.setToolTipText(tooltip);

				gd = new GridData(GridData.FILL_HORIZONTAL);
				// gd.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
				// gd.horizontalAlignment = SWT.LEFT;
				// gd.verticalIndent = 0;
				// gd.verticalAlignment = GridData.FILL;
				text.setLayoutData(gd);
				ISWTObservableValue textValue = Forms.observe(text);

				// NOTE this code only works if the LanguageExpressionBean is
				// not
				// replaced under our feet!
				final LanguageExpressionBean expression = LanguageExpressionBean.bindToNodeProperty(node, id);
				final String expressionPropertyName = "expression";
				Forms.bindBeanProperty(bindingContext, mmng, expression, expressionPropertyName, isMandatory(expression, expressionPropertyName), expressionPropertyName, textValue, text);

				String languageLabel = EditorMessages.propertiesLanguageTitle;
				toolkit.createLabel(composite, languageLabel);
				// toolkit.createSeparator(composite, SWT.SEPARATOR);

				Combo combo = new Combo(composite, SWT.NONE);
				combo.setItems(new Languages().languageArray());
				toolkit.adapt(combo, true, true);

				ISWTObservableValue comboValue = WidgetProperties.selection().observe(combo);
				Forms.bindBeanProperty(bindingContext, mmng, expression, "language", isMandatory(expression, "language"), languageLabel, comboValue, combo);

				String language = expression.getLanguage();
				if (language == null) {
					language = CamelModelHelper.getDefaultLanguageName();
					expression.setLanguage(language);
				}

				// now lets forward property events to the node
				expression.addPropertyChangeListener(new PropertyChangeListener() {
					/*
					 * (non-Javadoc)
					 * 
					 * @see
					 * java.beans.PropertyChangeListener#propertyChange(java
					 * .beans .PropertyChangeEvent)
					 */
					@Override
					public void propertyChange(PropertyChangeEvent event) {
						node.firePropertyChange(id.toString(), null, expression);
					}
				});
			} else {
				String propertyName = getPropertyName(id);
				Class refType = isBeanRef(node, propertyName);
				if (refType != null) {
					Combo combo = new Combo(parent, SWT.NONE);
					String[] beanRefs = getBeanRefs(refType);
					combo.setItems(beanRefs);
					toolkit.adapt(combo, true, true);
					widget = combo;

					ISWTObservableValue comboValue = WidgetProperties.selection().observe(combo);
					Forms.bindBeanProperty(bindingContext, mmng, node, propertyName, isMandatory(node, propertyName), labelText, comboValue, combo);
				} else if (isEndpointUri(node, propertyName)) {
					Combo combo = new Combo(parent, SWT.NONE);
					combo.setItems(getEndpointUris());
					toolkit.adapt(combo, true, true);
					widget = combo;

					ISWTObservableValue comboValue = WidgetProperties.selection().observe(combo);
					Forms.bindBeanProperty(bindingContext, mmng, node, propertyName, isMandatory(node, propertyName), labelText, comboValue, combo);
				} else if (descriptor instanceof BooleanPropertyDescriptor) {
					Button checkbox = new Button(parent, SWT.CHECK);
					widget = checkbox;
					ISWTObservableValue textValue = Forms.observe(checkbox);
					Forms.bindBeanProperty(bindingContext, mmng, node, propertyName, isMandatory(node, propertyName), labelText, textValue, checkbox);
				} else if (descriptor instanceof ListPropertyDescriptor) {
					if (CamelModelHelper.isPropertyListOFSetHeaders(id)) {
						Control control = bindSetHeaderTable(toolkit, parent, id);
						widget = control;
					} else {
						Control control = bindListOfValues(toolkit, parent, id);
						widget = control;
					}
				} else if (descriptor instanceof EnumPropertyDescriptor) {
					EnumPropertyDescriptor enumProperty = (EnumPropertyDescriptor) descriptor;
					ComboViewer combo = new ComboViewer(parent, SWT.READ_ONLY);
					combo.setContentProvider(ArrayContentProvider.getInstance());
					combo.setInput(getEnumValues(enumProperty.getEnumType()));

					IViewerObservableValue comboValue = ViewersObservables.observeSingleSelection(combo);
					Control control = combo.getControl();
					Forms.bindBeanProperty(bindingContext, mmng, node, propertyName, isMandatory(node, propertyName), labelText, comboValue, control);

					toolkit.adapt(control, true, true);
					widget = control;
				} else {
					Text text;
					if (isDescription) {
						text = toolkit.createText(parent, "", SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL);
					} else {
						text = toolkit.createText(parent, "");
					}
					text.setToolTipText(tooltip);
					widget = text;
					ISWTObservableValue textValue = Forms.observe(text);
					Forms.bindBeanProperty(bindingContext, mmng, node, propertyName, isMandatory(node, propertyName), labelText, textValue, text);
				}
			}
		}
		boolean isComplexOrUnion = isComplexProperty || isUnionProperty;
		if (isDescription || isComplexOrUnion) {
			gd = new GridData(GridData.FILL_BOTH);
			gd.heightHint = 90;
			gd.grabExcessVerticalSpace = true;
			gd.grabExcessHorizontalSpace = true;
			if (isComplexOrUnion) {
				gd.heightHint = -1;
			}
			if (isComplexProperty) {
				gd.horizontalSpan = 2;
			}

		} else {
			gd = new GridData(GridData.FILL_HORIZONTAL);
		}
		gd.widthHint = 250;
		widget.setLayoutData(gd);
	}

	protected Section bindNestedComplexProperties(FormToolkit toolkit, Composite parent, final IMessageManager mmng, ComplexPropertyDescriptor complexProperty,
			final Object id, String labelText, String tooltip) {
		Class<?> complexType = complexProperty.getPropertyType();
		Section section = toolkit.createSection(parent, Section.DESCRIPTION | Section.TITLE_BAR | Section.TWISTIE
				| Section.EXPANDED);
		section.setText(labelText);
		section.setToolTipText(tooltip);
		Composite sectionClient = toolkit.createComposite(section);
		sectionClient.setLayout(new GridLayout(2, false));
		section.setClient(sectionClient);

		// now lets introspect the composite...
		Object value = node.getPropertyValue(id);
		if (value == null) {
			value = ObjectHelper.newInstance(complexType);
			node.setPropertyValue(id, value);
		}
		bindNestedComplexProperties(toolkit, mmng, complexProperty, complexType, sectionClient, value);
		sectionClient.layout(true, true);
		return section;
	}

	protected CTabFolder bindNestedComplexUnionProperties(final FormToolkit toolkit, Composite parent,
			final IMessageManager mmng, final Object id, String labelText, String tooltip,
			ComplexUnionPropertyDescriptor complexProperty) {

		Class<?> basePropertyType = complexProperty.getPropertyType();

		//final CTabFolder bar = new CTabFolder(parent, SWT.TITLE | SWT.BORDER);
		final CTabFolder bar = new CTabFolder(parent, SWT.BORDER);
		// section.setTitle(labelText);
		bar.setToolTipText(tooltip);
		final UnionTypeValue[] valueTypes = complexProperty.getValueTypes();
		final Object[] values = new Object[valueTypes.length];

		bar.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int idx = bar.getSelectionIndex();
				if (idx >= 0 && idx < valueTypes.length) {
					// lets update the model with the value for this tab
					node.setPropertyValue(id, values[idx]);
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		Object value = node.getPropertyValue(id);
		int selectIdx = -1;
		int idx = 0;
		for (UnionTypeValue valueType : valueTypes) {
			Composite composite = new Composite(bar, SWT.NONE);
			composite.setLayout(new GridLayout(2, false));

			CTabItem item = new CTabItem(bar, SWT.BORDER);
			item.setText(valueType.getId());
			item.setControl(composite);

			Class<?> complexType = valueType.getValueType();
			Object tabValue;
			if (complexType.isInstance(value)) {
				selectIdx = idx;
				tabValue = value;
			}
			else {
				tabValue = ObjectHelper.newInstance(complexType);
			}
			values[idx++] = tabValue;
			bindNestedComplexProperties(toolkit, mmng, complexProperty, complexType, composite, tabValue);
			composite.layout(true, true);
		}
		if (selectIdx >= 0) {
			bar.setSelection(selectIdx);
		}
		bar.layout(true, true);
		return bar;
	}

	public void bindNestedComplexProperties(FormToolkit toolkit, final IMessageManager mmng, IPropertyDescriptor complexProperty, Class<?> complexType,
			Composite sectionClient, Object value) {
		try {
			boolean useMethods = JaxbHelper.useMethodReflection(complexType);
			BeanInfo beanInfo = Introspector.getBeanInfo(complexType);
			PropertyDescriptor[] properties = beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor property : properties) {
				Method getter = property.getReadMethod();
				Method setter = property.getWriteMethod();
				if (getter != null && setter != null) {
					String propertyName = property.getName();
					if (useMethods) {
						if (!JaxbHelper.hasXmlAnnotation(getter) && !JaxbHelper.hasXmlAnnotation(setter)) {
							continue;
						}
					} else {
						// lets look for the field
						try {
							Field field = complexType.getDeclaredField(propertyName);
							if (!JaxbHelper.hasXmlAnnotation(field)) {
								continue;
							}
						} catch (Exception e) {
							Activator.getLogger().debug(
									"Failed to find field: " + propertyName + " on " + complexType.getName());
							continue;
						}
					}
					bindNestedPojoProperty(toolkit, mmng, complexProperty, sectionClient, value, property, complexType);
				}
			}
		} catch (IntrospectionException e) {
			Activator.getLogger().warning("Failed to introspect " + complexType.getName() + ". Reason: " + e, e);
		}
	}

	protected void bindNestedPojoProperty(FormToolkit toolkit, final IMessageManager mmng, final IPropertyDescriptor complexProperty, Composite parent,
			Object value, PropertyDescriptor property, Class<?> complexType) {
		String labelText;
		String propertyName = property.getName();
		Class<?> propertyType = property.getPropertyType();
		labelText = capitalize(splitCamelCase(propertyName));
		if (complexProperty instanceof ComplexUnionPropertyDescriptor) {
			// lets fix up the background colour!
			Label label = new Label(parent, SWT.NONE);
			label.setText(labelText);
		} else {
			Label label = toolkit.createLabel(parent, labelText);
		}

		Control widget;
		Class refType = isBeanRef(value, propertyName);
		IObservable observable;
		if (boolean.class.isAssignableFrom(propertyType) || Boolean.class.isAssignableFrom(propertyType)) {
			Button checkbox = new Button(parent, SWT.CHECK);
			widget = checkbox;
			ISWTObservableValue textValue = Forms.observe(checkbox);
			observable = textValue;
			Forms.bindPojoProperty(bindingContext, mmng, value, propertyName, isMandatory(value, propertyName), labelText, textValue, checkbox);
		} else if (refType != null) {
			Combo combo = new Combo(parent, SWT.NONE);
			String[] beanRefs = getBeanRefs(refType);
			combo.setItems(beanRefs);
			toolkit.adapt(combo, true, true);
			widget = combo;

			ISWTObservableValue comboValue = WidgetProperties.selection().observe(combo);
			observable = comboValue;
			Forms.bindPojoProperty(bindingContext, mmng, value, propertyName, isMandatory(value, propertyName), labelText, comboValue, combo);
		} else if (isEndpointUri(value, propertyName)) {
			Combo combo = new Combo(parent, SWT.NONE);
			combo.setItems(getEndpointUris());
			toolkit.adapt(combo, true, true);
			widget = combo;

			ISWTObservableValue comboValue = WidgetProperties.selection().observe(combo);
			observable = comboValue;
			Forms.bindPojoProperty(bindingContext, mmng, value, propertyName, isMandatory(value, propertyName), labelText, comboValue, combo);
		} else if (Enum.class.isAssignableFrom(propertyType)) {
			ComboViewer combo = new ComboViewer(parent, SWT.READ_ONLY);
			combo.setContentProvider(ArrayContentProvider.getInstance());
			combo.setInput(getEnumValues((Class<? extends Enum>) propertyType));

			IViewerObservableValue comboValue = ViewersObservables.observeSingleSelection(combo);
			observable = comboValue;
			Control control = combo.getControl();
			Forms.bindPojoProperty(bindingContext, mmng, value, propertyName, isMandatory(value, propertyName), labelText, comboValue, control);

			toolkit.adapt(control, true, true);
			widget = control;

		} else {
			Text text = toolkit.createText(parent, "");
			widget = text;
			// text.setToolTipText(tooltip);
			ISWTObservableValue textValue = Forms.observe(text);
			observable = textValue;
			Forms.bindPojoProperty(bindingContext, mmng, value, propertyName, isMandatory(value, propertyName), labelText, textValue, text);
		}
		widget.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		if (observable != null && node != null) {
			observable.addChangeListener(new IChangeListener() {

				@Override
				public void handleChange(ChangeEvent event) {
					// lets notify the node that its changed
					String id = complexProperty.getId().toString();
					fireNodePropertyChangedEvent(id);
				}
			});
		}
	}

	protected Control bindListOfValues(FormToolkit toolkit, Composite parent, final Object id) {
		final Composite panel = toolkit.createComposite(parent);
		panel.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout(3, false);
		zeroMargins(layout);
		panel.setLayout(layout);

		final Text name = toolkit.createText(panel, "");
		name.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final Button add = new Button(panel, SWT.PUSH);
		add.setText("Add");
		add.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));

		final Button delete = new Button(panel, SWT.PUSH);
		delete.setText("Delete");
		// delete.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));

		final ListViewer viewer = new ListViewer(panel);
		viewer.setContentProvider(new ObservableListContentProvider());

		final Control control = viewer.getControl();
		control.setLayoutData(new GridData(GridData.FILL_BOTH));
		if (control instanceof org.eclipse.swt.widgets.List) {
			org.eclipse.swt.widgets.List list = (org.eclipse.swt.widgets.List) control;
			list.setSize(400, 300);
		}

		List<String> listData;
		Object value = node.getPropertyValue(id);
		if (value instanceof List) {
			listData = (List<String>) value;
		} else {
			listData = new ArrayList<String>();
		}
		final WritableList input = new WritableList(listData, String.class);
		node.setPropertyValue(id, input);
		viewer.setInput(input);

		final Runnable addAction = new Runnable() {
			@Override
			public void run() {
				String p = name.getText();
				name.setText("");
				if (!input.contains(p)) {
					input.add(p);
					fireNodePropertyChangedEvent(id);

					// lets layout to make things a bit bigger if need be
					if (control instanceof org.eclipse.swt.widgets.List) {
						org.eclipse.swt.widgets.List list = (org.eclipse.swt.widgets.List) control;
						list.pack(true);
					}
					panel.layout(true, true);
					layoutForm();
				}
			}
		};
		final Runnable deleteAction = new Runnable() {
			@Override
			public void run() {
				if (!viewer.getSelection().isEmpty()) {
					IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
					Iterator iter = selection.iterator();
					while (iter.hasNext()) {
						String p = (String) iter.next();
						input.remove(p);
					}
					fireNodePropertyChangedEvent(id);
				}
			}
		};

		// return on entry field adds
		name.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.stateMask == 0 && e.keyCode == '\r') {
					addAction.run();
				}
			}
		});
		// backspace on list to delete
		control.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if ((e.stateMask == 0 && e.keyCode == SWT.BS) || (e.stateMask == 0 && e.keyCode == SWT.DEL)) {
					deleteAction.run();
				}
			}
		});

		// enable / disable buttons
		add.setEnabled(false);
		delete.setEnabled(false);
		name.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				add.setEnabled(name.getText().length() > 0);
			}
		});
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				delete.setEnabled(!event.getSelection().isEmpty());
			}
		});

		delete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				deleteAction.run();
			}
		});

		add.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addAction.run();
			}
		});

		toolkit.adapt(control, true, true);
		return control;
	}


	private TableViewerColumn createTableViewerColumn(TableViewer viewer, final ICellModifier modifier, final String property, String title, String tooltip) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.FILL);
		viewerColumn.setLabelProvider(new CellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {
				Object value = modifier.getValue(cell.getElement(), property);
				cell.setText(Strings.getOrElse(value));
			}
		});
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(100);
		column.setResizable(true);
		column.setMoveable(false);
		column.setToolTipText(tooltip);
		return viewerColumn;

	}

	protected Control bindSetHeaderTable(FormToolkit toolkit, Composite parent, final Object id) {
		final Composite panel = toolkit.createComposite(parent);
		panel.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout(2, false);
		zeroMargins(layout);
		panel.setLayout(layout);

		List<SetHeaderDefinition> listData;
		Object value = node.getPropertyValue(id);
		if (value instanceof List) {
			listData = (List<SetHeaderDefinition>) value;
		} else {
			listData = new ArrayList<SetHeaderDefinition>();
		}
		final SetHeaderTableView tableView = new SetHeaderTableView(listData);
		final WritableList input = tableView.getInput();

		final Runnable addAction = new Runnable() {
			@Override
			public void run() {
				final SetHeaderDefinition sh = new SetHeaderDefinition("myHeaderName", new LanguageExpressionBean(CamelModelHelper.getDefaultLanguageName(), ""));
				final TableViewer viewer = tableView.getViewer();
				Refreshable refreshable = new Refreshable() {

					@Override
					public void refresh() {
						input.add(sh);
						viewer.setSelection(new StructuredSelection(sh));
						Viewers.refresh(viewer);
						layoutForm();
						panel.layout(true, true);
						fireNodePropertyChangedEvent(id);
					}
				};
				SetHeaderDialog.showDialog(sh, refreshable);
			}
		};
		final Runnable editAction = new Runnable() {
			@Override
			public void run() {
				System.out.println("Starting the edit...");

				final TableViewer viewer = tableView.getViewer();
				if (!viewer.getSelection().isEmpty()) {
					IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
					Iterator iter = selection.iterator();
					while (iter.hasNext()) {
						SetHeaderDefinition sh = toSetHeaderDefinition(iter.next());
						if (sh != null) {
							Refreshable refreshable = new Refreshable() {

								@Override
								public void refresh() {
									Viewers.refresh(viewer);
									fireNodePropertyChangedEvent(id);
								}
							};
							SetHeaderDialog.showDialog(sh, refreshable);
							break;
						}
					}
				}
			}
		};
		final Runnable deleteAction = new Runnable() {
			@Override
			public void run() {
				TableViewer viewer = tableView.getViewer();
				if (!viewer.getSelection().isEmpty()) {
					IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
					Iterator iter = selection.iterator();
					while (iter.hasNext()) {
						SetHeaderDefinition sh = toSetHeaderDefinition(iter.next());
						if (sh != null) {
							input.remove(sh);
						}
					}
					fireNodePropertyChangedEvent(id);
				}
			}
		};
		tableView.setDoubleClickAction(new RunnableAction(getClass().getName() + ".editSetHeaderDef", "Edit", editAction));

		tableView.createPartControl(panel);
		final TableViewer viewer = tableView.getViewer();
		final Control control = tableView.getControl();
		control.setLayoutData(new GridData(GridData.FILL_BOTH));

		Composite buttonPanel = toolkit.createComposite(panel);
		buttonPanel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
		buttonPanel.setLayout(new GridLayout(1, false));

		final Button add = new Button(buttonPanel, SWT.PUSH);
		add.setText("Add");
		add.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));

		final Button edit = new Button(buttonPanel, SWT.PUSH);
		edit.setText("Edit");
		edit.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));

		final Button delete = new Button(buttonPanel, SWT.PUSH);
		delete.setText("Delete");
		delete.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));


		// backspace on list to delete
		control.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if ((e.stateMask == 0 && e.keyCode == SWT.BS) || (e.stateMask == 0 && e.keyCode == SWT.DEL)) {
					deleteAction.run();
				}
			}
		});

		// enable / disable buttons
		edit.setEnabled(false);
		delete.setEnabled(false);
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				boolean enabled = !event.getSelection().isEmpty();
				edit.setEnabled(enabled);
				delete.setEnabled(enabled);
			}
		});

		edit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				editAction.run();
			}
		});

		delete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				deleteAction.run();
			}
		});

		add.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addAction.run();
			}
		});

		toolkit.adapt(control, true, true);
		return control;
	}

	public static SetHeaderDefinition toSetHeaderDefinition(Object element) {
		if (element instanceof SetHeaderDefinition) {
			return (SetHeaderDefinition) element;
		}
		if (element instanceof TableItem) {
			TableItem ti = (TableItem) element;
			return toSetHeaderDefinition(ti.getData());
		}
		return null;
	}

	protected void zeroMargins(GridLayout layout) {
		layout.marginLeft = 0;
		layout.marginTop = 0;
		layout.marginBottom = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
	}

	protected Object[] getEnumValues(Class<? extends Enum> enumType) {
		try {
			Method method = enumType.getMethod("values");
			Object result = method.invoke(null);
			if (result instanceof Object[]) {
				return (Object[]) result;
			}
		} catch (Exception e) {
			Activator.getLogger().warning("Failed to get enum values from " + enumType, e);
		}
		return EMPTY_STRING_ARRAY;
	}

	protected String[] getBeanRefs(Class refType) {
		RouteContainer routeContainer = getNodeContainer();
		if (routeContainer != null) {
			return Sorts.toSortedStringArray(routeContainer.getBeans().keySet());
		} else {
			return EMPTY_STRING_ARRAY;
		}
	}

	protected String[] getEndpointUris() {
		RouteContainer routeContainer = getNodeContainer();
		if (routeContainer != null) {
			return routeContainer.getEndpointUris();
		} else {
			return EMPTY_STRING_ARRAY;
		}
	}

	protected String getPropertyName(final Object id) {
		String propertyName = id.toString();
		int idx = propertyName.indexOf('.');
		if (idx > 0) {
			propertyName = propertyName.substring(idx + 1);
		}
		propertyName = propertyName.substring(0, 1).toLowerCase() + propertyName.substring(1);
		return propertyName;
	}

	protected boolean isMandatory(Object bean, String propertyName) {
		// lets look at the setter method and see if its got a @Required
		// annotation
		if (bean instanceof AbstractNode) {
			AbstractNode node = (AbstractNode) bean;
			Class<?> camelClass = node.getCamelDefinitionClass();
			if (camelClass != null) {
				XmlAccessorType accessorType = camelClass.getAnnotation(XmlAccessorType.class);
				boolean useMethods = true;
				if (accessorType != null) {
					if (accessorType.value().equals(XmlAccessType.FIELD)) {
						useMethods = false;
					}
				}
				try {
					BeanInfo beanInfo = Introspector.getBeanInfo(camelClass);
					PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
					if (propertyDescriptors != null) {
						for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
							if (propertyName.equals(propertyDescriptor.getName())) {
								Method writeMethod = propertyDescriptor.getWriteMethod();
								if (writeMethod != null) {
									Required annotation = writeMethod.getAnnotation(Required.class);
									if (annotation != null) {
										return true;
									}
									if (useMethods) {
										XmlElement element = writeMethod.getAnnotation(XmlElement.class);
										if (element != null && element.required()) {
											return true;
										}
										XmlAttribute attribute = writeMethod.getAnnotation(XmlAttribute.class);
										if (attribute != null && attribute.required()) {
											return true;
										}
									}
								}
								break;
							}
						}
					}
					if (!useMethods) {
						Field[] fields = camelClass.getDeclaredFields();
						for (Field field : fields) {
							if (propertyName.equals(field.getName())) {
								Required annotation = field.getAnnotation(Required.class);
								if (annotation != null) {
									return true;
								}
								XmlElement element = field.getAnnotation(XmlElement.class);
								if (element != null && element.required()) {
									return true;
								}
								XmlAttribute attribute = field.getAnnotation(XmlAttribute.class);
								if (attribute != null && attribute.required()) {
									return true;
								}
							}
						}
					}
				} catch (IntrospectionException e) {
					// ignore
				}
			}
		}

		// expression is mandatory on resequence
		if (node instanceof Resequence && "expression".equals(propertyName)) {
			return true;
		}

		// lets make all URI properties mandatory by default to avoid complex
		// validation with ref v uri
		boolean answer = ("uri".equals(propertyName) || propertyName.endsWith("Uri"))
				|| (bean instanceof Aggregate && "strategyRef".equals(propertyName))
				|| (bean instanceof ConvertBody && "type".equals(propertyName))
				|| (bean instanceof ExpressionDefinition && isMandatoryExpression())
				|| (bean instanceof Log && "message".equals(propertyName))
				|| (bean instanceof Process && "ref".equals(propertyName))
				|| (bean instanceof RemoveHeader && "headerName".equals(propertyName))
				|| (bean instanceof RemoveProperty && "propertyName".equals(propertyName))
				|| (bean instanceof SetHeader && "headerName".equals(propertyName))
				|| (bean instanceof SetOutHeader && "headerName".equals(propertyName))
				|| (bean instanceof SetProperty && "propertyName".equals(propertyName));
		return answer;
	}

	protected boolean isMandatoryExpression() {
		// is this expression mandatory?
		Class<?> camelClass = node.getCamelDefinitionClass();
		return ExpressionNode.class.isAssignableFrom(camelClass);
	}

	/**
	 * Returns a base class if a property is a bean reference which we can one
	 * day use to filter out all the available bean refs or returns null if its
	 * not a bean ref.
	 */
	protected Class isBeanRef(Object bean, String propertyName) {
		if (propertyName.matches("(ref)|(.+Ref)")) {
			return Object.class;
		} else {
			return null;
		}
	}

	/**
	 * Returns true if the given property is a URI
	 */
	protected boolean isEndpointUri(Object bean, String propertyName) {
		return propertyName.matches("(uri)|(.+Uri)");
	}

	/**
	 * Fires an event that a property has changed.
	 */
	protected void fireNodePropertyChangedEvent(Object id) {
		if (id != null) {
			Object newValue = node.getPropertyValue(id);
			node.firePropertyChange(id.toString(), null, newValue);
		}
	}
}
