/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.foundation.ui.form;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.viewers.IViewerObservableValue;
import org.eclipse.jface.databinding.viewers.typed.ViewerProperties;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.fusesource.ide.foundation.ui.util.ICanValidate;


public abstract class FormSupport implements PropertyChangeListener  {
	private ICanValidate validator;

	/**
	 * Font metrics to use for determining pixel sizes.
	 */
	private FontMetrics fontMetrics;
	private  FormToolkit toolkit;
	private IMessageManager messageManager;
	private ScrolledForm form;
	private DataBindingContext dataBindingContext = new DataBindingContext();
	private Set<String> mandatoryPropertyNames = new HashSet<String>();
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	public FormSupport() {
	}

	public FormSupport(ICanValidate validator) {
		this.validator = validator;
	}

	public abstract void setFocus();

	public void dispose() {
		if (toolkit != null) {
			toolkit.dispose();
			toolkit = null;
		}
	}

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		propertyChangeSupport.firePropertyChange(e);
	}

	protected void firePropertyChange(String name, Object oldValue, Object newValue) {
		propertyChangeSupport.firePropertyChange(name, oldValue, newValue);
	}


	public DataBindingContext getDataBindingContext() {
		return dataBindingContext;
	}

	public IMessageManager getMessageManager() {
		return messageManager;
	}

	public ScrolledForm getForm() {
		return form;
	}

	public FormToolkit getToolkit() {
		return toolkit;
	}

	protected void addMandatoryPropertyNames(String... names) {
		for (String n : names) {
			mandatoryPropertyNames.add(n);
		}
	}

	protected void initializeFontMetrics(Control control) {
		// Compute and store a font metric
		GC gc = new GC(control);
		gc.setFont(JFaceResources.getDialogFont());
		fontMetrics = gc.getFontMetrics();
		gc.dispose();
	}


	public Control getControl() {
		return form.getContent();
	}

	public void createForm(Composite parent) {
		initializeFontMetrics(parent);

		toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createScrolledForm(parent);
		messageManager = form.getMessageManager();

		Form formChild = form.getForm();
		String header = getFormHeader();
		if (header != null) {
			formChild.setText(header);
		}
		toolkit.decorateFormHeading(formChild);
		form.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		//form.getBody().setLayout(new GridLayout(1, false));
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		form.getBody().setLayout(layout);
	}

	protected String getFormHeader() {
		return null;
	}

	public Composite createSectionComposite(String text, GridData gridData) {
		int sectionStyle = Section.DESCRIPTION | Section.TITLE_BAR;
		if (text != null) {
			sectionStyle |= Section.TWISTIE | Section.EXPANDED;
		}
		Section section = toolkit.createSection(form.getBody(), sectionStyle);
		if (text != null) {
			section.setText(text);
		}
		section.setLayoutData(gridData);
		Composite inner = toolkit.createComposite(section);
		section.setClient(inner);
		inner.setLayoutData(gridData);
		return inner;
	}

	protected Label createLabel(Composite inner, String text) {
		return toolkit.createLabel(inner, text);
	}

	protected Label createLabel(Composite inner, String text, int flags) {
		return toolkit.createLabel(inner, text, flags);
	}

	protected Text createText(Composite inner) {
		return createText(inner, SWT.NONE | SWT.BORDER);
	}

	protected Text createText(Composite inner, int flags) {
		Text answer = toolkit.createText(inner,  "", flags);
		GridData gdata = new GridData(GridData.FILL_HORIZONTAL);
		answer.setLayoutData(gdata);
		answer.addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(KeyEvent e) {
				validate();
			}

			@Override
			public void keyPressed(KeyEvent e) {
				validate();
			}
		});
		return answer;
	}

	protected Text createTextArea(Composite inner) {
		return createTextArea(inner, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL);
	}

	protected Text createTextArea(Composite inner, int flags) {
		Text answer = toolkit.createText(inner,  "", flags);
		GridData gdata = new GridData(SWT.FILL, SWT.FILL, true, true);
		answer.setLayoutData(gdata);
		answer.addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(KeyEvent e) {
				validate();
			}

			@Override
			public void keyPressed(KeyEvent e) {
				validate();
			}
		});
		return answer;
	}

	protected Text createBeanPropertyPasswordField(Composite parent, Object bean, String propertyName, String labelText, String tooltip) {
		return createBeanPropertyTextField(parent, bean, propertyName, labelText, tooltip, SWT.NONE | SWT.BORDER | SWT.PASSWORD);
	}
	
	protected Text createBeanPropertyTextField(Composite parent, Object bean, String propertyName, String labelText, String tooltip) {
		return createBeanPropertyTextField(parent, bean, propertyName, labelText, tooltip, SWT.NONE | SWT.BORDER);
	}

	protected Text createBeanPropertyTextField(Composite parent, Object bean, String propertyName, String labelText, String tooltip,
			int flags) {
		createLabel(parent, labelText);
		Text text = createText(parent, flags);
		text.setToolTipText(tooltip);
		ISWTObservableValue textValue = Forms.observe(text);
		Forms.bindBeanProperty(getDataBindingContext(), getMessageManager(), bean, propertyName, isMandatory(bean, propertyName), labelText, textValue, text);
		return text;
	}

	protected ComboViewer createBeanPropertyCombo(Composite parent, Object bean, String propertyName, String labelText, String tooltip,
			int flags) {
		createLabel(parent, labelText);
		ComboViewer combo = new ComboViewer(parent, flags);
		combo.setContentProvider(ArrayContentProvider.getInstance());

		IViewerObservableValue comboValue = ViewerProperties.singleSelection().observe(combo);
		Control control = combo.getControl();
		GridData gdata = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gdata.widthHint = 400;
		control.setLayoutData(gdata);

		IValidator validator = null;
		if (isMandatory(bean, propertyName)) {
			if (control instanceof Combo) {
				validator = new MandatoryComboValidator(labelText, (Combo) control);
			} else {
				validator = new MandatoryValidator(labelText);
			}
		}

		Forms.bindBeanProperty(getDataBindingContext(), getMessageManager(), bean, propertyName, validator, comboValue, control);

		toolkit.adapt(control, true, true);
		return combo;
	}

	protected ListViewer createBeanPropertyList(Composite parent, Object bean, String propertyName, String labelText, String tooltip,
			int flags) {
		createLabel(parent, labelText);
		ListViewer combo = new ListViewer(parent, flags);
		combo.setContentProvider(ArrayContentProvider.getInstance());

		IViewerObservableValue comboValue = ViewerProperties.singleSelection().observe(combo);
		Control control = combo.getControl();
		GridData gdata = new GridData(GridData.FILL_BOTH);
		control.setLayoutData(gdata);

		Forms.bindBeanProperty(getDataBindingContext(), getMessageManager(), bean, propertyName, isMandatory(bean, propertyName), labelText, comboValue, control);

		toolkit.adapt(control, true, true);
		combo.refresh();
		return combo;
	}

	public boolean isValid() {
		return Forms.isValid(getDataBindingContext());
	}

	protected boolean isMandatory(Object bean, String propertyName) {
		return mandatoryPropertyNames.contains(propertyName);
	}

	protected void validate() {
		if (validator != null) {
			validator.validate();
		}
	}

	protected void setButtonLayoutData(Button button) {
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		int widthHint = convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
		Point minSize = button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		data.widthHint = Math.max(widthHint, minSize.x);
		button.setLayoutData(data);
	}

	protected int convertHorizontalDLUsToPixels(int dlus) {
		// test for failure to initialize for backward compatibility
		if (fontMetrics == null) {
			return 0;
		}
		return Dialog.convertHorizontalDLUsToPixels(fontMetrics, dlus);
	}

	public Control createDialogArea(Composite parent) {
		createForm(parent);

		createTextFields(parent);

		return parent;
	}

	protected abstract void createTextFields(Composite parent);

	public abstract void okPressed();

}