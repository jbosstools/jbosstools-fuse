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

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.ValidationStatusProvider;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.internal.databinding.BindingStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.IViewerObservableList;
import org.eclipse.jface.databinding.viewers.ViewerProperties;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IMessageManager;

public class Forms {

	public static ISWTObservableValue observe(Text text) {
		return WidgetProperties.text(SWT.Modify).observe(text);
	}

	public static ISWTObservableValue observe(Button text) {
		return WidgetProperties.selection().observe(text);
	}

	public static void bindPojoProperty(DataBindingContext dataBindingContext, final IMessageManager mmng, Object bean,
			final String propertyName, boolean mandatory, final String labelText, IObservableValue value,
			final Control control) {
		IObservableValue modelValue = PojoProperties.value(propertyName).observe(bean);

		UpdateValueStrategy targetToModel = new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE);
		UpdateValueStrategy modelToTarget = new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE);

		if (mandatory) {
			targetToModel.setBeforeSetValidator(new MandatoryValidator(labelText));
			modelToTarget.setBeforeSetValidator(new MandatoryValidator(labelText));
		}

		Binding bindValue = dataBindingContext.bindValue(value, modelValue, targetToModel, modelToTarget);

		final IObservableValue validationStatus = bindValue.getValidationStatus();
		validationStatus.addChangeListener(new MessageChangeListener(validationStatus, control,
				propertyName, mmng));

		// we need to call validation here otherwise the fresh initiated fields
		// would
		// have no error decoration if field is invalid on init phase
		bindValue.validateTargetToModel();
		// bindValue.validateModelToTarget();
	}

	public static void bindBeanProperty(DataBindingContext dataBindingContext, final IMessageManager mmng, Object bean,
			final String propertyName, boolean mandatory, final String labelText, IObservableValue value,
			final Control control) {
		IValidator validator = null;
		if (mandatory) {
			validator = new MandatoryValidator(labelText);
		}
		bindBeanProperty(dataBindingContext, mmng, bean, propertyName, validator, value, control);
	}

	public static void bindBeanProperty(DataBindingContext dataBindingContext, final IMessageManager mmng,
			Object bean, final String propertyName, IValidator validator, IObservableValue value, final Control control) {
		IObservableValue modelValue = BeanProperties.value(propertyName).observe(bean);

		UpdateValueStrategy targetToModel = new TrimmingUpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE);
		//UpdateValueStrategy modelToTarget = new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE);
		UpdateValueStrategy modelToTarget = new TrimmingUpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE);

		if (validator != null) {
			targetToModel.setBeforeSetValidator(validator);
			modelToTarget.setBeforeSetValidator(validator);
		}

		Binding bindValue = dataBindingContext.bindValue(value, modelValue, targetToModel, modelToTarget);

		final IObservableValue validationStatus = bindValue.getValidationStatus();
		validationStatus.addChangeListener(new MessageChangeListener(validationStatus, control,
				propertyName, mmng));

		// we need to call validation here otherwise the fresh initiated fields
		// would
		// have no error decoration if field is invalid on init phase
		bindValue.validateTargetToModel();
		// bindValue.validateModelToTarget();
	}

	public static boolean isValid(DataBindingContext context) {
		IObservableList validationStatusProviders = context.getValidationStatusProviders();
		for (Object object : validationStatusProviders) {
			if (object instanceof ValidationStatusProvider) {
				ValidationStatusProvider provider = (ValidationStatusProvider) object;
				IObservableValue validationStatus = provider.getValidationStatus();
				if (validationStatus != null) {
					Object value = validationStatus.getValue();
					if (value instanceof BindingStatus) {
						BindingStatus status = (BindingStatus) value;
						if (!status.isOK()) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	public static void updateMessageManager(IMessageManager messageManager, Object source, Control propertyControl,
			IStatus status, String propertyId) {
		if (status.getSeverity() == IStatus.OK) {
			messageManager.removeMessage(propertyId, propertyControl);
		} else {
			String message = status.getMessage();
			messageManager.addMessage(propertyId, message, source, IMessageProvider.ERROR, propertyControl);
		}
	}

	public static void bindMultipleSelection(DataBindingContext dataBindingContext, IMessageManager mmgr, IObservableList modelList, StructuredViewer profilesViewer,
			String propertyName, String labelText) {
		IViewerObservableList observe = ViewerProperties.multipleSelection().observe(profilesViewer);

		UpdateValueStrategy targetToModel = new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE);
		UpdateValueStrategy modelToTarget = new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE);

		targetToModel.setBeforeSetValidator(new MandatoryValidator(labelText));
		modelToTarget.setBeforeSetValidator(new MandatoryValidator(labelText));

		Binding bindValue = dataBindingContext.bindList(observe, modelList);

		final IObservableValue validationStatus = bindValue.getValidationStatus();
		validationStatus.addChangeListener(new MessageChangeListener(validationStatus, profilesViewer.getControl(),
				propertyName, mmgr));
	}
}
