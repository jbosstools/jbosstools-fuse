/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.project;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.InputStream;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.junit.Test;
import org.osgi.framework.Constants;

public class DependencyCheckIT {
	
	@Test
	public void testSonatypeDependencyAvailable() throws Exception {
		InputStream manifestStream = Activator.class.getResourceAsStream("/META-INF/MANIFEST.MF");
		Manifest manifest = new Manifest(manifestStream);
		Attributes mainAttributes = manifest.getMainAttributes();
		String requireBundles = mainAttributes.getValue(Constants.REQUIRE_BUNDLE);
		assertThat(requireBundles).contains("org.sonatype.tycho.m2e");
	}

}
