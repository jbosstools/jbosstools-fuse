/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.ui.bot.tests.utils;

import org.jboss.tools.fuse.reddeer.ProjectType;

/**
 * Represents a Fuse Integration project
 * 
 * @author tsedmik
 */
public class FuseProjectDefinition {

	private String template;
	private ProjectType dsl;
	private String camelVersion;

	public FuseProjectDefinition(String template, ProjectType dsl, String camelVersion) {
		super();
		this.template = template;
		this.dsl = dsl;
		this.camelVersion = camelVersion;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public ProjectType getDsl() {
		return dsl;
	}

	public void setDsl(ProjectType dsl) {
		this.dsl = dsl;
	}

	public String getCamelVersion() {
		return camelVersion;
	}

	public void setCamelVersion(String camelVersion) {
		this.camelVersion = camelVersion;
	}
}
