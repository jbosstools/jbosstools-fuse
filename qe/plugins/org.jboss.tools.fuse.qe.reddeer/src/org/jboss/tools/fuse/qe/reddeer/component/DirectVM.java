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

public class DirectVM implements CamelComponent {

	@Override
	public String getPaletteEntry() {
		return "Direct VM";
	}

	@Override
	public String getLabel() {
		return "direct-vm:name";
	}

	@Override
	public String getTooltip() {
		return "The Direct VM Component manages DirectVmEndpoint and holds the list of named direct-vm endpoints";
	}

}
