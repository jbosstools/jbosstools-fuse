/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.projecttemplates.wizards.pages;

import java.io.File;

import org.eclipse.wst.server.core.IRuntime;

public class RuntimeCamelVersionFinder {
	
	static final String FUSE_RUNTIME_PREFIX = "org.fusesource.ide.fuseesb.runtime.";
	static final String EAP_RUNTIME_PREFIX = "org.jboss.ide.eclipse.as.runtime.eap.";
	private static final String CAMEL_CORE_LIB_PREFIX = "camel-core-";
	private static final String CAMEL_CORE_LIB_SUFFIX = ".jar";

	public String getVersion(IRuntime runtime) {
		if (runtime != null) {
			if (isJBossFuseRuntime(runtime)) {
				return getVersionForJbossFuseRuntime(runtime);
			} else if (isFuseOnEAPRuntime(runtime)) {
				return getVersionForEAPRuntime(runtime);
			}
		}
		return FuseIntegrationProjectWizardRuntimeAndCamelPage.UNKNOWN_CAMEL_VERSION;
	}

	private String getVersionForEAPRuntime(IRuntime runtime) {
		File camelFolder = getCamelFolderOnEAP(runtime);
		if(camelFolder.exists()){
			String[] versions = camelFolder.list((dir, name) -> {
				String lowerCaseName = name.toLowerCase();
				return lowerCaseName.startsWith(CAMEL_CORE_LIB_PREFIX) && lowerCaseName.endsWith(CAMEL_CORE_LIB_SUFFIX);
			});
			if (versions.length==1) {
				String jarName = versions[0];
				return jarName.substring(jarName.indexOf(CAMEL_CORE_LIB_PREFIX)+CAMEL_CORE_LIB_PREFIX.length(), jarName.indexOf(CAMEL_CORE_LIB_SUFFIX));
			}
		}
		return FuseIntegrationProjectWizardRuntimeAndCamelPage.UNKNOWN_CAMEL_VERSION;
	}

	private File getCamelFolderOnEAP(IRuntime runtime) {
		return runtime.getLocation().append("modules").append("system").append("layers").append("fuse").append("org").append("apache").append("camel").append("core").append("main").toFile();
	}

	private String getVersionForJbossFuseRuntime(IRuntime runtime) {
		File camelFolder = getCamelFolderOnFuse(runtime);
		if(camelFolder.exists()){
			String[] versions = camelFolder.list();
			if (versions.length==1) {
				return versions[0];
			}
		}
		return FuseIntegrationProjectWizardRuntimeAndCamelPage.UNKNOWN_CAMEL_VERSION;
	}

	private File getCamelFolderOnFuse(IRuntime runtime) {
		return runtime.getLocation().append("system").append("org").append("apache").append("camel").append("camel-core").toFile();
	}
	
	/**
	 * checks whether the given runtime is an EAP runtime
	 * 
	 * @param runtime	the runtime to check
	 * @return	true if the runtime is an EAP runtime
	 */
	private boolean isFuseOnEAPRuntime(IRuntime runtime) {
		return isRuntimeOfType(runtime, EAP_RUNTIME_PREFIX);
	}
	
	/**
	 * checks whether the given runtime is a fuse runtime
	 * 
	 * @param runtime	the runtime to check
	 * @return	true if the runtime is a Red Hat fuse runtime
	 */
	private boolean isJBossFuseRuntime(IRuntime runtime) {
		return isRuntimeOfType(runtime, FUSE_RUNTIME_PREFIX);
	}
	
	private boolean isRuntimeOfType(IRuntime runtime, String runtimeType){
		return runtime.getRuntimeType().getId().startsWith(runtimeType);
	}

}
