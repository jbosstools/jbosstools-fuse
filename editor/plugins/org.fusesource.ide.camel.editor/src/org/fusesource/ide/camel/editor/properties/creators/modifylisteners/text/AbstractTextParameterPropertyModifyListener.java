/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.camel.editor.properties.creators.modifylisteners.text;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;

/**
 * @author Aurelien Pupier
 *
 */
public abstract class AbstractTextParameterPropertyModifyListener implements ModifyListener {

	protected AbstractCamelModelElement camelModelElement;
	protected String parameterName;

	public AbstractTextParameterPropertyModifyListener(AbstractCamelModelElement camelModelElement, String parameterName) {
		this.camelModelElement = camelModelElement;
		this.parameterName = parameterName;
	}

	@Override
	public void modifyText(ModifyEvent e) {
		if (e.getSource() instanceof Text) {
			Text txt = (Text) e.getSource();
			updateModel(txt.getText());
		} else if (e.getSource() instanceof Combo) {
			Combo combo = (Combo) e.getSource();
			updateModel(combo.getText());
		}
	}

	protected abstract void updateModel(String newValue);
}