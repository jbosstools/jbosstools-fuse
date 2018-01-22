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

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

/**
 * @author lheinema
 */
public class SyndesisExtensionsUtil {
	
	private static final String URL_IGNITE_VERSIONS_FILE = "https://raw.githubusercontent.com/jbosstools/jbosstools-fuse/master/configuration/ignite.properties";
	
	private static final String KEY_SPRING_BOOT_VERSION = "spring.boot.version";
	private static final String KEY_CAMEL_VERSION = "camel.version";
	private static final String KEY_SYNDESIS_VERSION = "syndesis.version";
	
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
	 * @param urlString
	 * @return
	 * @throws IOException
	 */
	public static IgniteVersionInfoModel getIgniteVersionModel(String urlString) {
		IgniteVersionInfoModel model = new IgniteVersionInfoModel();
		Properties vMapping = new Properties();
		
		try {
			URL url = new URL(urlString);
			vMapping.load(url.openStream());
		} catch (IOException ex) {
			// we ignore load errors
		}
				
		model.setCamelVersion(vMapping.getProperty(KEY_CAMEL_VERSION, "2.20.1"));
		model.setSpringBootVersion(vMapping.getProperty(KEY_SPRING_BOOT_VERSION, "1.5.8.RELEASE"));
		model.setSyndesisVersion(vMapping.getProperty(KEY_SYNDESIS_VERSION, "1.2.3"));
		
		return model;
	}
	
	/**
	 * retrieves important version information from an online version mapping file
	 * 
	 * @return
	 * @throws IOException
	 */
	public static IgniteVersionInfoModel getIgniteVersionModel() {
		return getIgniteVersionModel(URL_IGNITE_VERSIONS_FILE);
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
