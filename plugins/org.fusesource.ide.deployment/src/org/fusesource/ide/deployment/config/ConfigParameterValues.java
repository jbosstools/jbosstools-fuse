/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.deployment.config;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.IParameterValues;
import org.fusesource.ide.deployment.ConfigurationUtils;


/**
 * @author lhein
 */
public class ConfigParameterValues implements IParameterValues {

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IParameterValues#getParameterValues()
	 */
	@Override
	public Map getParameterValues() {

		HotfolderDeploymentConfiguration[] configs = ConfigurationUtils.loadPreferences();
	
		HashMap<String, HotfolderDeploymentConfiguration> map = new HashMap<String, HotfolderDeploymentConfiguration>();
		
		for (HotfolderDeploymentConfiguration cfg : configs) {
			map.put(cfg.getName(), cfg);	
		}
		
		return map;
	}
}
