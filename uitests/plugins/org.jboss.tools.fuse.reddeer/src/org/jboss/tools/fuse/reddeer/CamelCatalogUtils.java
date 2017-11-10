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
package org.jboss.tools.fuse.reddeer;

import java.io.IOException;
import java.util.regex.Pattern;

import org.eclipse.reddeer.common.logging.Logger;

/**
 * Structure for using different versions of Camel catalog - implemented only for 'components'
 * 
 * @author djelinek
 */
public class CamelCatalogUtils {

	private static Logger log = Logger.getLogger(CamelCatalogUtils.class);

	private static final String ROOT_PATH = "/org/apache/camel/catalog/";

	private static String COMPONENTS = ROOT_PATH + "components";

	private static String COMPONENTS_LIST = ROOT_PATH + "components.properties";

	public enum CatalogType {

		COMPONENT("component", COMPONENTS, COMPONENTS_LIST);

		private String type;
		private String path;
		private String listPath;

		private CatalogType(String type, String path, String list) {
			this.type = type;
			this.path = path;
			this.listPath = list;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

		public String getListPath() {
			return listPath;
		}

		public void setListPath(String list) {
			this.listPath = list;
		}

	}

	public CamelCatalogUtils(String path) {
		COMPONENTS = path + COMPONENTS;
		COMPONENTS_LIST = path + COMPONENTS_LIST;
	}

	/**
	 * Returns all available components
	 * 
	 * @return String
	 */
	public String Components() {
		return getCatalogComponentsList(CatalogType.COMPONENT);
	}

	/**
	 * Checks if component is in CamelCatalog
	 * 
	 * @param name
	 *            (name of Component)
	 * @return true/false
	 */
	public boolean isExistComponent(String name) {
		return catalogNamePattern(name).matcher(Components()).find();
	}

	private Pattern catalogNamePattern(String name) {
		return Pattern.compile(name + "|" + name.split("\\s+", 2)[0] + "|" + name.replaceAll("\\s+", "") + "|"
				+ name.replaceAll("\\s+", "-") + "|" + name.replaceAll("-", ""), Pattern.CASE_INSENSITIVE);
	}

	private String getCatalogComponentsList(CatalogType type) {
		try {
			return FileUtils.getFileContent(type.listPath);
		} catch (IOException e) {
			log.error("Resource missing: can't find a failing test case to copy (" + type.listPath + ")!");
		}
		return "";
	}

}
