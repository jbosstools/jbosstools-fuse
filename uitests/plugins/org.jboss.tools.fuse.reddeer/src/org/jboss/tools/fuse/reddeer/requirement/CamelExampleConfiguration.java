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
package org.jboss.tools.fuse.reddeer.requirement;

import java.io.File;

import org.eclipse.reddeer.junit.requirement.configuration.RequirementConfiguration;

/**
 * Requirement configuration for Camel example.
 * 
 * @author Andrej Podhradsky (apodhrad@redhat.com)
 *
 */
public class CamelExampleConfiguration implements RequirementConfiguration {

	private String name;
	private String version;
	private File jarFile;
	private JolokiaConfiguration jolokiaConfiguration;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public File getJarFile() {
		return jarFile;
	}

	public void setJarFile(File jarFile) {
		this.jarFile = jarFile;
	}

	public JolokiaConfiguration getJolokiaConfiguration() {
		return jolokiaConfiguration;
	}

	public void setJolokiaConfiguration(JolokiaConfiguration jolokiaConfiguration) {
		this.jolokiaConfiguration = jolokiaConfiguration;
	}

	@Override
	public String getId() {
		return getName() + "_" + getVersion();
	}

}
