/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.camel.model.service.core.catalog;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.fusesource.ide.camel.model.service.core.CamelServiceManagerUtil;
import org.fusesource.ide.camel.model.service.core.ICamelManagerService;

/**
 * @author lhein
 *
 */
public class CamelModelFactory {
	
	private static HashMap<String, CamelModel> supportedCamelModels;
	
	/**
	 * initializes all available models for the connectors group of the camel editor palette
	 */
	public static void initializeModels() {
		supportedCamelModels = new HashMap<String, CamelModel>();
		
		String[] versions = CamelServiceManagerUtil.getAvailableVersions();
		for (String version : versions) {
			supportedCamelModels.put(version, null); // we initialize on access
		}
	}
	
	/**
	 * returns the list of supported camel versions
	 * 
	 * @return
	 */
	public static List<String> getSupportedCamelVersions() {
		if (supportedCamelModels == null || supportedCamelModels.isEmpty()) {
			initializeModels();
		}
		return Arrays.asList(supportedCamelModels.keySet().toArray(new String[supportedCamelModels.size()]));
	}
	
	/**
	 * returns the model for a given camel version or null if not supported
	 * 
	 * @param camelVersion
	 * @return
	 */
	public static CamelModel getModelForVersion(String camelVersion) {
		CamelModel cm = supportedCamelModels.get(camelVersion);
		
		if (cm == null) {
			// not initialized yet
			ICamelManagerService svc = CamelServiceManagerUtil.getManagerService(camelVersion);
			if (svc != null) {
				cm = svc.getCamelModel();
				cm.setCamelVersion(camelVersion);
				supportedCamelModels.put(camelVersion, cm);
			}
		}
		
		return cm;
	}
	
	/**
	 * returns the latest and greatest supported Camel version we have a catalog 
	 * for. If there are 2 catalogs with the same version (for instance 2.15.1 and 
	 * 2.15.1.redhat-114) then we will always prefer the Red Hat variant.
	 * 
	 * @return
	 */
	public static String getLatestCamelVersion() {
		String latest = null;
		for (String v : supportedCamelModels.keySet()) {
			if (latest == null) {
				latest = v;
			} else if (v.compareTo(latest)>0) {
				latest = v;
			}
		}
		if (latest != null) return latest;
		
		return supportedCamelModels.keySet().iterator().next();
	}
	
	/**
	 * TODO   This method should be used as much as possible to make sure
	 * the editor is pulling the proper model for the given project. 
	 * 
	 * 
	 * @return
	 */
	public static String getCamelVersion(IProject p) {
		// TODO stubbed out for now. We should check the facets if possible. 
		return getLatestCamelVersion();
	}
}
