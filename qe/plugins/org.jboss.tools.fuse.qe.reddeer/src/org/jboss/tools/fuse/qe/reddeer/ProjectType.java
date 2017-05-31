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
package org.jboss.tools.fuse.qe.reddeer;

public enum ProjectType {

	JAVA("Java DSL"),
	SPRING("Spring DSL", "camel-context.xml", "beans"),
	BLUEPRINT("Blueprint DSL", "blueprint.xml", "blueprint");

	private String description;
	private String camelContext;
	private String rootElement;

	private ProjectType(String description) {
		this(description, null);
	}

	private ProjectType(String description, String camelContext) {
		this(description, camelContext, null);
	}

	private ProjectType(String description, String camelContext, String rootElement) {
		this.description = description;
		this.camelContext = camelContext;
		this.rootElement = rootElement;
	}

	public String getDescription() {
		return description;
	}

	public String getCamelContext() {
		return camelContext;
	}

	public String getRootElement() {
		return rootElement;
	}
}
