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

import org.eclipse.core.commands.AbstractParameterValueConverter;
import org.eclipse.core.commands.ParameterValueConversionException;
import org.fusesource.ide.deployment.ConfigurationUtils;


/**
 * @author lhein
 */
public class DeploymentConfigurationConverter extends
		AbstractParameterValueConverter {

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.AbstractParameterValueConverter#convertToObject(java.lang.String)
	 */
	@Override
	public Object convertToObject(String parameterValue)
			throws ParameterValueConversionException {
		
		HotfolderDeploymentConfiguration[] configs = ConfigurationUtils.loadPreferences();
		for (HotfolderDeploymentConfiguration cfg : configs) {
			if (cfg.getName().equals(parameterValue)) {
				return cfg;
			}
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.AbstractParameterValueConverter#convertToString(java.lang.Object)
	 */
	@Override
	public String convertToString(Object parameterValue)
			throws ParameterValueConversionException {
		
		if (parameterValue instanceof AbstractDeploymentConfiguration) {
			return ((AbstractDeploymentConfiguration)parameterValue).getName();
		}
		
		return null;
	}
}
