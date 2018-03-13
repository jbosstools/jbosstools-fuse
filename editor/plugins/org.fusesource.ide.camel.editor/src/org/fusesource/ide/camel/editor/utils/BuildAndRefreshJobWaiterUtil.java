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
package org.fusesource.ide.camel.editor.utils;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.ResourcesPlugin;

public class BuildAndRefreshJobWaiterUtil extends JobWaiterUtil {
	
	private static final List<Object> buildAnRefreshJobFamilies = Arrays.asList(
			ResourcesPlugin.FAMILY_AUTO_BUILD,
			ResourcesPlugin.FAMILY_MANUAL_REFRESH,
			ResourcesPlugin.FAMILY_AUTO_REFRESH,
			ResourcesPlugin.FAMILY_MANUAL_BUILD
	);

	public BuildAndRefreshJobWaiterUtil() {
		super(buildAnRefreshJobFamilies);
	}

}
