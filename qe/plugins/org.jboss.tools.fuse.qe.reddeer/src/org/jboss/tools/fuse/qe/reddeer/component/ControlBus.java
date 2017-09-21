/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.qe.reddeer.component;

public class ControlBus implements CamelComponent {

	@Override
	public String getPaletteEntry() {
		return "Control Bus";
	}

	@Override
	public String getLabel() {
		return "controlbus:command:language";
	}

	@Override
	public String getTooltip() {
		return "The Control Bus component allows sending messages to a control-bus endpoint to control the lifecycle of routes";
	}

}
