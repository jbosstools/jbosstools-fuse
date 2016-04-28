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
package org.fusesource.ide.camel.editor.properties.creators.modifylisteners.number;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Text;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;

/**
 * @author Aurelien Pupier
 *
 */
public abstract class AbstractNumberModifyListener implements ModifyListener {

	protected AbstractCamelModelElement camelModelElement;
	protected String parameterName;

	/**
	 * @param numberParameterPropertyUICreator
	 */
	public AbstractNumberModifyListener(AbstractCamelModelElement camelModelElement, String parameterName) {
		this.camelModelElement = camelModelElement;
		this.parameterName = parameterName;
	}

	@Override
	public void modifyText(ModifyEvent e) {
	    Text txt = (Text)e.getSource();
	    String val = txt.getText();
	    try {
	    	Double.parseDouble(val);
	    	txt.setBackground(ColorConstants.white);
			updateModel(txt.getText());
	    } catch (NumberFormatException ex) {
	    	// invalid character found
	        txt.setBackground(ColorConstants.red);
	        return;
	    }
	}

	protected abstract void updateModel(String newValue);
}