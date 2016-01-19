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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.foundation.core.util.Strings;

/**
 * @author Aurelien Pupier
 *
 */
public class FusePropertySection extends AbstractPropertySection {

	/**
	 * @param toolkit 
	 * @param page The page on which it will be created
	 * @param p The property for which the label is generated
	 */
	protected void createPropertyLabel(FormToolkit toolkit, Composite page, Parameter p) {
	    String s = computePropertyDisplayName(p);
	    Label l = toolkit.createLabel(page, s);         
	    l.setLayoutData(new GridData());
	    addDescriptionAsTooltip(p, l);
		if (isRequired(p)) {
	    	l.setForeground(getDisplay().getSystemColor(SWT.COLOR_RED));
	    }
	}

	/**
	 * @return
	 */
	private Display getDisplay() {
		return Display.getDefault();
	}

	protected String computePropertyDisplayName(Parameter parameter) {
		String s = Strings.humanize(parameter.getName());
		if(isRequired(parameter)){
			s += " *";
		}
	    if (isDeprecated(parameter)){
	    	s += " (deprecated)"; 
	    }
		return s;
	}
	
	private void addDescriptionAsTooltip(Parameter parameter, Label label) {
		String description = parameter.getDescription();
		if (description != null) {
	    	label.setToolTipText(description);
	    }
	}

	protected boolean isRequired(Parameter parameter) {
		return isParameterValueTrue(parameter.getRequired());
	}

	private boolean isDeprecated(Parameter parameter) {
		return isParameterValueTrue(parameter.getDeprecated());
	}
	
	private boolean isParameterValueTrue(String parameterValue) {
		return parameterValue != null && parameterValue.equalsIgnoreCase("true");
	}

}
