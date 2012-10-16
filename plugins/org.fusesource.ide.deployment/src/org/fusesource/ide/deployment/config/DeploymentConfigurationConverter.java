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
