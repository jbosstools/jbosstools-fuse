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

import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.IMessageManager;

/**
 * Updates the {@link IMessageManager} whenever the validation changes status
 */
public class MessageChangeListener implements IChangeListener {
	private final IObservableValue validationStatus;
	private final Control control;
	private final String propertyName;
	private final IMessageManager mmng;

	public MessageChangeListener(IObservableValue validationStatus, Control control, String propertyName,
			IMessageManager mmng) {
		this.validationStatus = validationStatus;
		this.control = control;
		this.propertyName = propertyName;
		this.mmng = mmng;
	}

	@Override
	public void handleChange(ChangeEvent event) {
		Object value = validationStatus.getValue();
		if (value instanceof IStatus) {
			IStatus status = (IStatus) value;
			Forms.updateMessageManager(mmng, event.getSource(), control, status, propertyName);
		}
	}
}