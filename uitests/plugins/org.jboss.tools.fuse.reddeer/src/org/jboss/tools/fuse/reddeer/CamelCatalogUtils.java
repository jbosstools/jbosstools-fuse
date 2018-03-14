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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.reddeer.common.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Structure for using different versions of Camel catalog
 * 
 * @author djelinek
 */
public class CamelCatalogUtils {

	private static Logger log = Logger.getLogger(CamelCatalogUtils.class);

	private static final String ROOT_PATH = "/org/apache/camel/catalog/";

	private static String COMPONENTS_PATH = ROOT_PATH + "components";

	private static String COMPONENTS_LIST_PATH = ROOT_PATH + "components.properties";

	private static String DATAFORMATS_PATH = ROOT_PATH + "dataformats";

	private static String DATAFORMATS_LIST_PATH = ROOT_PATH + "dataformats.properties";

	private static String LANGUAGES_PATH = ROOT_PATH + "languages";

	private static String LANGUAGES_LIST_PATH = ROOT_PATH + "languages.properties";

	private static String MODELS_PATH = ROOT_PATH + "models";

	private static String MODELS_LIST_PATH = ROOT_PATH + "models.properties";

	private static final String JSON_SUFFIX = ".json";

	public enum CatalogType {

		COMPONENT("component", COMPONENTS_PATH, COMPONENTS_LIST_PATH),
		DATAFORMAT("dataformat", DATAFORMATS_PATH, DATAFORMATS_LIST_PATH),
		LANGUAGE("language", LANGUAGES_PATH, LANGUAGES_LIST_PATH),
		MODEL("model", MODELS_PATH, MODELS_LIST_PATH);

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
		COMPONENTS_PATH = path + COMPONENTS_PATH;
		COMPONENTS_LIST_PATH = path + COMPONENTS_LIST_PATH;
		DATAFORMATS_PATH = path + DATAFORMATS_PATH;
		DATAFORMATS_LIST_PATH = path + DATAFORMATS_LIST_PATH;
		LANGUAGES_PATH = path + LANGUAGES_PATH;
		LANGUAGES_LIST_PATH = path + LANGUAGES_LIST_PATH;
		MODELS_PATH = path + MODELS_PATH;
		MODELS_LIST_PATH = path + MODELS_LIST_PATH;
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
	 * Returns all available dataformats
	 *
	 * @return String
	 */
	public String DataFormats() {
		return getCatalogComponentsList(CatalogType.DATAFORMAT);
	}

	/**
	 * Returns all available languages
	 *
	 * @return String
	 */
	public String Languages() {
		return getCatalogComponentsList(CatalogType.LANGUAGE);
	}

	/**
	 * Returns all available models
	 *
	 * @return String
	 */
	public String Models() {
		return getCatalogComponentsList(CatalogType.MODEL);
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

	/**
	 * Checks if dataformat is in CamelCatalog
	 *
	 * @param name
	 *            (name of Component)
	 * @return true/false
	 */
	public boolean isExistDataFormat(String name) {
		return catalogNamePattern(name).matcher(DataFormats()).find();
	}

	/**
	 * Checks if language is in CamelCatalog
	 *
	 * @param name
	 *            (name of Component)
	 * @return true/false
	 */
	public boolean isExistLanguage(String name) {
		return catalogNamePattern(name).matcher(Languages()).find();
	}

	/**
	 * Checks if model is in CamelCatalog
	 *
	 * @param name
	 *            (name of Component)
	 * @return true/false
	 */
	public boolean isExistModel(String name) {
		return catalogNamePattern(name).matcher(Models()).find();
	}

	/**
	 * Returns property value for given component name
	 * 
	 * @return String
	 */
	public String getPropertytValue(CatalogType type, String name, String property) {
		try {
			return new JSONObject(getJsonFileContent(type, name)).getJSONObject(type.type).getString(property);
		} catch (Exception e) {
			log.error("Trying to get missing property '" + property + "'");
		}
		return "";
	}

	/**
	 * Returns list of available component properties names
	 * 
	 * @param type
	 *            CatalogType (COMPONENT, DATAFORMAT, LANGUAGE, MODEL)
	 * @param name
	 *            Name of component
	 * @return List<String>
	 */
	public List<String> getPropertiesNamesList(CatalogType type, String name) {
		JSONObject obj = null;
		JSONArray arr = null;
		List<String> properties = new ArrayList<>();
		try {
			obj = new JSONObject(getJsonFileContent(type, name)).getJSONObject("properties");
			arr = obj.names();
			for (int i = 0; i < arr.length(); i++) {
				properties.add(arr.get(i).toString());
			}
		} catch (Exception e) {
			log.error("Trying to get non-existing 'properties' JSON object for '" + type + "' - '" + name + "'");
		}
		if (type == CatalogType.COMPONENT) {
			try {
				obj = new JSONObject(getJsonFileContent(type, name)).getJSONObject("componentProperties");
				arr = obj.names();
				if (arr != null) {
					for (int i = 0; i < arr.length(); i++) {
						properties.add(arr.get(i).toString());
					}
				}
			} catch (Exception e) {
				log.error("Trying to get non-existing 'componentProperties' JSON object for '" + type + "' - '" + name + "'");
			}
		}
		return properties;
	}

	/**
	 * @return JSON object
	 */
	public JSONObject getComponentJSONObject(CatalogType type, String name) {
		try {
			return new JSONObject(getJsonFileContent(type, name));
		} catch (Exception e) {
			log.error("Trying to get JSONobject for component '" + name + "'");
		}
		return null;
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

	/**
	 * Returns path if file with given 'name'.json exists
	 * 
	 * @param type
	 *            CatalogType (COMPONENT, DATAFORMAT, LANGUAGE, MODEL)
	 * @param name
	 *            File name
	 * @return String path
	 */
	private String findJSONFile(CatalogType type, String name) {
		Matcher m = catalogNamePattern("(" + name + ")").matcher(getCatalogComponentsList(type));
		while (m.find()) {
			String path = type.path + "/" + m.group(1) + JSON_SUFFIX;
			if (new File(path).exists())
				return path;
		}
		return "";
	}

	/**
	 * Returns the component information from JSon as String format.
	 *
	 * @param name
	 *            (the component name)
	 * @return component details in String format
	 */
	private String getJsonFileContent(CatalogType type, String name) {
		try {
			return FileUtils.getFileContent(findJSONFile(type, name));
		} catch (IOException e) {
			log.error("Resource missing: can't find a failing test case to copy (" + name + ")!");
		}
		return "";
	}

}
