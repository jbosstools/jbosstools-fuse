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

public class Language implements CamelComponent {

	@Override
	public String getPaletteEntry() {
		return "Language";
	}

	@Override
	public String getLabel() {
		return "language:languageName";
	}

	@Override
	public String getTooltip() {
		return "The Language component enables sending org.apache.camel.Exchanges to a given language in order to have a script executed";
	}

}
