/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.reddeer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.reddeer.common.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Structure for using different versions of Camel catalog
 * 
 * For Camel components is supported only 'properties' part of available parameters, at this moment not useful to support also
 * 'componentProperties' part (see https://issues.jboss.org/browse/FUSETOOLS-3148)
 *
 * @author djelinek
 */
public class CamelCatalogUtils {

	private static Logger log = Logger.getLogger(CamelCatalogUtils.class);

	public static final String ROOT_PATH = "/org/apache/camel/catalog/";
	public static final String JSON_SUFFIX = ".json";

	private static String components_path = ROOT_PATH + "components";
	private static String components_list_path = ROOT_PATH + "components.properties";
	private static String dataformats_path = ROOT_PATH + "dataformats";
	private static String dataformats_list_path = ROOT_PATH + "dataformats.properties";
	private static String languages_path = ROOT_PATH + "languages";
	private static String languages_list_path = ROOT_PATH + "languages.properties";
	private static String models_path = ROOT_PATH + "models";
	private static String models_list_path = ROOT_PATH + "models.properties";

	public enum CatalogType {

		COMPONENT("component", components_path, components_list_path),
		DATAFORMAT("dataformat", dataformats_path, dataformats_list_path),
		LANGUAGE("language", languages_path, languages_list_path),
		MODEL("model", models_path, models_list_path);

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
		components_path = path + components_path;
		components_list_path = path + components_list_path;
		dataformats_path = path + dataformats_path;
		dataformats_list_path = path + dataformats_list_path;
		languages_path = path + languages_path;
		languages_list_path = path + languages_list_path;
		models_path = path + models_path;
		models_list_path = path + models_list_path;
	}

	/**
	 * Returns all available components
	 *
	 * @return String
	 */
	public List<String> getComponents() {
		return getCatalogComponentsList(CatalogType.COMPONENT);
	}

	/**
	 * Returns all available dataformats
	 *
	 * @return String
	 */
	public List<String> getDataFormats() {
		return getCatalogComponentsList(CatalogType.DATAFORMAT);
	}

	/**
	 * Returns all available languages
	 *
	 * @return String
	 */
	public List<String> getLanguages() {
		return getCatalogComponentsList(CatalogType.LANGUAGE);
	}

	/**
	 * Returns all available models
	 *
	 * @return String
	 */
	public List<String> getModels() {
		return getCatalogComponentsList(CatalogType.MODEL);
	}

	/**
	 * Checks if component is in CamelCatalog
	 *
	 * @param name
	 *                 (name of Component)
	 * @return true/false
	 */
	public boolean isExistComponent(String name) {
		return catalogNamePattern(name).matcher(readListFile(CatalogType.COMPONENT)).find();
	}

	/**
	 * Checks if dataformat is in CamelCatalog
	 *
	 * @param name
	 *                 (name of Component)
	 * @return true/false
	 */
	public boolean isExistDataFormat(String name) {
		return catalogNamePattern(name).matcher(readListFile(CatalogType.DATAFORMAT)).find();
	}

	/**
	 * Checks if language is in CamelCatalog
	 *
	 * @param name
	 *                 (name of Component)
	 * @return true/false
	 */
	public boolean isExistLanguage(String name) {
		return catalogNamePattern(name).matcher(readListFile(CatalogType.LANGUAGE)).find();
	}

	/**
	 * Checks if model is in CamelCatalog
	 *
	 * @param name
	 *                 (name of Component)
	 * @return true/false
	 */
	public boolean isExistModel(String name) {
		return catalogNamePattern(name).matcher(readListFile(CatalogType.MODEL)).find();
	}

	public boolean isRequired(CatalogType type, String name, String label) {
		try {
			return getPropertyJSONObject(type, name, label).getBoolean("required");
		} catch (JSONException e) {
			return false;
		}
	}

	private JSONObject getPropertyJSONObject(CatalogType type, String name, String label) {
		JSONObject obj = null;
		try {
			obj = getComponentJSONObject(type, name).getJSONObject("properties").getJSONObject(label);
		} catch (JSONException e) {
			log.error("Trying to get missing property '" + label + "'");
		}
		return obj;
	}

	/**
	 * Returns property value for given component name
	 *
	 * @return String
	 */
	public String getPropertytValue(CatalogType type, String name, String property) {
		try {
			return getComponentJSONObject(type, name).getJSONObject(type.type).getString(property);
		} catch (Exception e) {
			log.error("Trying to get missing property '" + property + "'");
		}
		return "";
	}

	/**
	 * Returns list of available component properties names
	 *
	 * @param type
	 *                 CatalogType (COMPONENT, DATAFORMAT, LANGUAGE, MODEL)
	 * @param name
	 *                 Name of component
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

	public List<String> getComponentPropertyValueAsList(CatalogType type, String component, String property, String valueOf) {
		List<String> valuesList = new ArrayList<>();
		try {
			JSONObject componentObj = this.getComponentJSONObject(type, component);
			JSONArray propertyValuesArray = componentObj.getJSONObject("properties").getJSONObject(property).getJSONArray(valueOf);
			for (int i = 0; i < propertyValuesArray.length(); i++) {
				valuesList.add(propertyValuesArray.getString(i));
			}
		} catch (JSONException e) {
			log.error(e.getMessage());
		}
		return valuesList;
	}

	private Pattern catalogNamePattern(String name) {
		return Pattern.compile(name + "|" + name.split("\\s+", 2)[0] + "|" + name.replaceAll("\\s+", "") + "|"
				+ name.replaceAll("\\s+", "-") + "|" + name.replaceAll("-", ""), Pattern.CASE_INSENSITIVE);
	}

	private List<String> getCatalogComponentsList(CatalogType type) {
		return Arrays.asList(readListFile(type).split("\n"));
	}

	/**
	 * Returns path if file with given 'name'.json exists
	 *
	 * @param type
	 *                 CatalogType (COMPONENT, DATAFORMAT, LANGUAGE, MODEL)
	 * @param name
	 *                 File name
	 * @return String path
	 */
	private String findJSONFile(CatalogType type, String name) {
		Matcher m = catalogNamePattern("(" + name + ")").matcher(readListFile(type));
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
	 *                 (the component name)
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

	private String readListFile(CatalogType type) {
		try {
			return FileUtils.getFileContent(type.listPath);
		} catch (IOException e) {
			log.error("Resource missing: can't find path (" + type.listPath + ")!");
		}
		return "";
	}

	public MavenDependency getMavenDependency(CatalogType type, String name) {
		String groupId = getPropertytValue(type, name, "groupId");
		String artifactId = getPropertytValue(type, name, "artifactId");
		String version = getPropertytValue(type, name, "version");
		if (groupId == null || groupId.isEmpty()) {
			return null;
		}
		return new MavenDependency(groupId, artifactId, version);
	}

}