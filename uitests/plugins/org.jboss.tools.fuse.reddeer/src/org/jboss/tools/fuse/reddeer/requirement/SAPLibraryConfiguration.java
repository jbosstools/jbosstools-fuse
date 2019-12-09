/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.reddeer.requirement;

import org.eclipse.reddeer.junit.requirement.configuration.RequirementConfiguration;

/**
 * 
 * @author apodhrad
 * 
 */
public class SAPLibraryConfiguration implements RequirementConfiguration {

	private String jco3;
	private String jidoc;

	public String getJco3() {
		return jco3;
	}

	public void setJco3(String jco3) {
		this.jco3 = jco3;
	}

	public String getJidoc() {
		return jidoc;
	}

	public void setJidoc(String jidoc) {
		this.jidoc = jidoc;
	}

	@Override
	public String getId() {
		return "SAP";
	}

}
