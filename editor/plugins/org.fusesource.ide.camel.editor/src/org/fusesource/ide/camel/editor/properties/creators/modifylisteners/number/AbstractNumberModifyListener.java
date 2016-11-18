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
import org.fusesource.ide.camel.model.service.core.util.PropertiesUtils;

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
	    	PropertiesUtils.validateDuration(val);
	    	txt.setBackground(ColorConstants.white);
			updateModel(txt.getText());
	    } catch (IllegalArgumentException ex) {
	    	txt.setBackground(ColorConstants.red);
	    }
	}
	
	protected abstract void updateModel(String newValue);
}