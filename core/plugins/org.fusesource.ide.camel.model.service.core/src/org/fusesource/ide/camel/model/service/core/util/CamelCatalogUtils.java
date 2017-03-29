/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.model.service.core.util;

import java.util.ArrayList;

import org.fusesource.ide.camel.model.service.core.catalog_json.Dependency;
import org.fusesource.ide.camel.model.service.core.catalog_json.cache.CamelCatalogCoordinates;

/**
 * collection of camel catalog related util methods
 * 
 * @author lhein
 */
public class CamelCatalogUtils {
	
	/**
	 * takes a label from the catalog which is a possibly comma separated string and splits it into pieces storing 
	 * each of the substrings in a string array list
	 * 
	 * @param label
	 * @return
	 */
	public static ArrayList<String> initializeTags(String label) {
		ArrayList<String> tags = new ArrayList<>();
		if (label != null && label.trim().length()>0) {
			String[] s_tags = label.split(",");
			for (String s_tag : s_tags) {
				tags.add(s_tag);
			}
		}
		return tags;
	}
	
	/**
	 * takes maven coordinates and creates a dependency from it storing it in the array list
	 * 
	 * @param groupId
	 * @param artifactId
	 * @param version
	 * @return
	 */
	public static ArrayList<Dependency> initializeDependency(String groupId, String artifactId, String version) {
		ArrayList<Dependency> dependencies = new ArrayList<>();
		if (groupId != null && groupId.trim().length()>0 &&
			artifactId != null && artifactId.trim().length()>0 && 
			version != null && version.trim().length()>0) {
			Dependency dep = new Dependency();
			dep.setGroupId(groupId);
			dep.setArtifactId(artifactId);
			dep.setVersion(version);
			dependencies.add(dep);
		}
		return dependencies;
	}
	
	/**
	 * returns the catalog coordinates for the given maven coords
	 * 
	 * @param groupId
	 * @param artifactId
	 * @param version
	 * @return
	 */
	public static CamelCatalogCoordinates getCatalogCoordinatesFor(String groupId, String artifactId, String version) {
		return new CamelCatalogCoordinates(groupId, artifactId, version);
	}
}
