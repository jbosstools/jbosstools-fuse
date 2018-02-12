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
package org.fusesource.ide.syndesis.extensions.core.util;

import java.util.Map;

import org.fusesource.ide.syndesis.extensions.core.internal.SyndesisExtensionsCoreActivator;

/**
 * @author lheinema
 */
public class SyndesisExtensionsUtil {
	
	static final String KEY_SPRING_BOOT_VERSION = "spring.boot.version";
	static final String KEY_CAMEL_VERSION = "camel.version";
	static final String KEY_SYNDESIS_VERSION = "syndesis.version";
	
	private SyndesisExtensionsUtil() {
		// util class
	}
	
	/**
	 * checks if the syndesis version given is a valid version
	 * 
	 * @param version
	 * @return
	 */
	public static boolean isValidSyndesisExtensionVersion(String version) {
		boolean valid = true;
		String[] parts = version.split("\\.");
		if (parts.length<2 || version.trim().endsWith(".") || version.trim().startsWith(".")) {
			valid = false;
		} else {
			for (String part : parts) {
				try {
					Integer.parseInt(part);
				} catch (NumberFormatException ex) {
					valid = false;
					break;
				}
			}			
		}
		return valid;
	}
	
	/**
	 * retrieves important version information from an online version mapping file
	 * 
	 * @return
	 */
	public static IgniteVersionInfoModel getIgniteVersionModel() {
		IgniteVersionInfoModel model = new IgniteVersionInfoModel();

		Map<String, String> mapping = new IgniteVersionMapper().getMapping();
		model.setCamelVersion(mapping.get(KEY_CAMEL_VERSION));
		model.setSpringBootVersion(mapping.get(KEY_SPRING_BOOT_VERSION));
		model.setSyndesisVersion(mapping.get(KEY_SYNDESIS_VERSION));

		return model;
	}
	
	public static class IgniteVersionInfoModel {
		private String springBootVersion;
		private String camelVersion;
		private String syndesisVersion;
		
		/**
		 * @return the camelVersion
		 */
		public String getCamelVersion() {
			return this.camelVersion;
		}
		
		/**
		 * @return the springBootVersion
		 */
		public String getSpringBootVersion() {
			return this.springBootVersion;
		}
		
		/**
		 * @return the syndesisVersion
		 */
		public String getSyndesisVersion() {
			return this.syndesisVersion;
		}
		
		/**
		 * @param camelVersion the camelVersion to set
		 */
		public void setCamelVersion(String camelVersion) {
			this.camelVersion = camelVersion;
		}
		
		/**
		 * @param springBootVersion the springBootVersion to set
		 */
		public void setSpringBootVersion(String springBootVersion) {
			this.springBootVersion = springBootVersion;
		}
		
		/**
		 * @param syndesisVersion the syndesisVersion to set
		 */
		public void setSyndesisVersion(String syndesisVersion) {
			this.syndesisVersion = syndesisVersion;
		}
	}
}
