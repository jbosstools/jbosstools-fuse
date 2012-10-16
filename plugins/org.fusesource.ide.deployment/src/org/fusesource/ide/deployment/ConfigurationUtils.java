package org.fusesource.ide.deployment;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.fusesource.ide.deployment.config.HotfolderDeploymentConfiguration;
import org.fusesource.ide.preferences.PreferenceManager;
import org.fusesource.ide.preferences.PreferencesConstants;


/**
 * @author lhein
 */
public class ConfigurationUtils {

	/**
	 * saves all configurations 
	 * 
	 * @param deploymentConfigurations
	 */
	public static void savePreferences(HotfolderDeploymentConfiguration[] deploymentConfigurations) {
		if (deploymentConfigurations == null) {
			return;
		}
		
		boolean first = true;
		StringBuffer sb = new StringBuffer();
		for (HotfolderDeploymentConfiguration cfg : deploymentConfigurations) {
			if (first) { 
				first = false;
			} else {
				sb.append(";");
			}			
			sb.append(String.format("%s,%s,%s,%s", 
					  				Boolean.toString(cfg.isDefaultConfig()),
					  				cfg.getName(),
					  				cfg.getDescription(),
					  				cfg.getHotDeployPath()));
		}
		PreferenceManager.getInstance().savePreference(PreferencesConstants.DEPLOYMENT_STORAGE_KEY, sb.toString());
	}
	
	/**
	 * loads all configurations
	 * 
	 * @return an array of configs (can be empty)
	 */
	public static HotfolderDeploymentConfiguration[] loadPreferences() {
		HotfolderDeploymentConfiguration[] deploymentConfigurations = new HotfolderDeploymentConfiguration[0];
		String values = PreferenceManager.getInstance().loadPreferenceAsString(PreferencesConstants.DEPLOYMENT_STORAGE_KEY);
		if (values == null || values.trim().length()<1) {
			// ignore
		} else {
			ArrayList<HotfolderDeploymentConfiguration> configs = new ArrayList<HotfolderDeploymentConfiguration>();
			StringTokenizer stok = new StringTokenizer(values, ";");
			while (stok.hasMoreTokens()) {
				int i = 0;
				String token = stok.nextToken();
				String[] parts = token.split(",");
				HotfolderDeploymentConfiguration cfg = new HotfolderDeploymentConfiguration();
				for (String part : parts) {
					switch (i) {
						case 0:		cfg.setDefaultConfig(Boolean.parseBoolean(part));
									break;
						case 1:		cfg.setName(part);
									break;
						case 2:		cfg.setDescription(part);
									break;
						case 3:		cfg.setHotDeployPath(part);
									break;
						default:	
					}
					i++;
				}
				configs.add(cfg);
			}
			deploymentConfigurations = configs.toArray(new HotfolderDeploymentConfiguration[configs.size()]);
		}
		return deploymentConfigurations;
	}
	
	public static HotfolderDeploymentConfiguration loadDefaultConfiguration() {
		HotfolderDeploymentConfiguration[] configs = loadPreferences();
		for (HotfolderDeploymentConfiguration config : configs) {
			if (config.isDefaultConfig()) {
				return config;
			}
		}
		return null;
	}
}
