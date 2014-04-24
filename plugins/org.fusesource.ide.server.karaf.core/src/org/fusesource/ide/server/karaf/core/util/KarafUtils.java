/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.server.karaf.core.util;

import java.io.File;

import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.model.ServerBehaviourDelegate;
import org.jboss.ide.eclipse.as.core.server.bean.ServerBeanLoader;
import org.jboss.ide.eclipse.as.core.server.bean.ServerBeanType;

/**
 * @author lhein
 */
public class KarafUtils {
	
	/**
	 * A constant representing that no publish is required. 
	 * This constant is different from the wtp constants in that
	 * this constant is used after taking into account 
	 * the server flags of kind and deltaKind, as well as the module restart state,
	 * to come to a conclusion of what a publisher needs to do.
	 */
	public static final int NO_PUBLISH = 0;
	/**
	 * A constant representing that an incremental publish is required. 
	 * This constant is different from the wtp constants in that
	 * this constant is used after taking into account 
	 * the server flags of kind and deltaKind, as well as the module restart state,
	 * to come to a conclusion of what a publisher needs to do.
	 */
	public static final int INCREMENTAL_PUBLISH = 1;
	/**
	 * A constant representing that a full publish is required. 
	 * This constant is different from the wtp constants in that
	 * this constant is used after taking into account 
	 * the server flags of kind and deltaKind, as well as the module restart state,
	 * to come to a conclusion of what a publisher needs to do.
	 */
	public static final int FULL_PUBLISH = 2;
	
	/**
	 * A constant representing that a removal-type publish is required. 
	 * This constant is different from the wtp constants in that
	 * this constant is used after taking into account 
	 * the server flags of kind and deltaKind, as well as the module restart state,
	 * to come to a conclusion of what a publisher needs to do.
	 */
	public static final int REMOVE_PUBLISH = 3;
	
	/**
	 * retrieves the version of the installation
	 * 
	 * @param installFolder	the installation folder
	 * @return	the version string or null on errors
	 */
	public static String getVersion(File installFolder) {
		ServerBeanLoader loader = new ServerBeanLoader(installFolder);
		if( loader.getServerBeanType() != ServerBeanType.UNKNOWN) {
			return loader.getFullServerVersion();
		}
		return null;
	}
	
	/**
	 * Given the various flags, return which of the following options 
	 * our publishers should perform:
	 *    1) A full publish
	 *    2) A removed publish (remove the module)
	 *    3) An incremental publish, or
	 *    4) No publish at all. 
	 * @param module
	 * @param kind
	 * @param deltaKind
	 * @return
	 */
	public static int getPublishType(IServer server, IModule[] module, int kind, int deltaKind) {
		int modulePublishState = server.getModulePublishState(module);
		if( deltaKind == ServerBehaviourDelegate.ADDED ) 
			return FULL_PUBLISH;
		else if (deltaKind == ServerBehaviourDelegate.REMOVED) {
			return REMOVE_PUBLISH;
		} else if (kind == IServer.PUBLISH_FULL 
				|| modulePublishState == IServer.PUBLISH_STATE_FULL 
				|| kind == IServer.PUBLISH_CLEAN ) {
			return FULL_PUBLISH;
		} else if (kind == IServer.PUBLISH_INCREMENTAL 
				|| modulePublishState == IServer.PUBLISH_STATE_INCREMENTAL 
				|| kind == IServer.PUBLISH_AUTO) {
			if( ServerBehaviourDelegate.CHANGED == deltaKind ) 
				return INCREMENTAL_PUBLISH;
		} 
		return NO_PUBLISH;
	}
	
	
	/*
	 * The following methods appear to be unused. 
	 */
//	
//	/**
//	 * checks if the version required is matched
//	 * 
//	 * @param installFolder	the installation folder
//	 * @param version		the required version or null
//	 * @return	true if version is null or matched
//	 */
//	public static boolean checkVersion(File installFolder, String version) {
//		boolean match = false;
//		
//		if (version != null) {
//			// we will check for correct version string (. is appended if not there)
//			String checkVersion = (version.trim().endsWith(".") ? version : version + ".");
//			String jarVersion = getVersion(installFolder);
//			if (jarVersion != null && jarVersion.trim().startsWith(checkVersion)) {
//				// bundle name and version matches
//				match = true;
//			}
//		} else {
//			// no version specified so always true
//			match = true;
//		}
//		
//		return match;
//	}
//	
//	/**
//	 * checks if the given folder contains a valid karaf version with the given
//	 * version (must match version to return true)
//	 * 
//	 * @param path		the path to the installation
//	 * @param version	the version to match or null if version doesn't matter
//	 * @return	true if karaf install folder and version matches
//	 */
//	public static boolean isValidKarafInstallation(File path, String version) {
//		boolean valid = false;
//		
//		if (path != null && path.isDirectory()) {
//			File[] folders = path.listFiles(new FileFilter() {
//				/*
//				 * (non-Javadoc)
//				 * @see java.io.FileFilter#accept(java.io.File)
//				 */
//				public boolean accept(File checkFile) {
//					return checkFile.isDirectory() && 
//						(checkFile.getName().equalsIgnoreCase("bin") ||
//						 checkFile.getName().equalsIgnoreCase("etc") ||
//					     checkFile.getName().equalsIgnoreCase("deploy") ||
//						 checkFile.getName().equalsIgnoreCase("system"));
//				}
//			});
//			// all folders must be there
//			if (folders.length == 4) {
//				// now check if the version matches
//				valid = checkVersion(path, version);
//			}
//		}
//				
//		return valid;
//	}
//	
//
//	/**
//	 * we check for jar files like servicemix-version.jar or activemq-version.jar etc. as they indicate
//	 * that Karaf is here used as a basement for another product
//	 * 
//	 * @param path
//	 * @return
//	 */
//	public static boolean isUsedAsFramework(File path) {
//		boolean karafUsedAsFramework = false;
//		
//		File[] files = new File(path, "lib").listFiles(new FileFilter() {
//			/*
//			 * (non-Javadoc)
//			 * @see java.io.FileFilter#accept(java.io.File)
//			 */
//			@Override
//			public boolean accept(File checkFile) {
//				return checkFile.getName().toLowerCase().endsWith("-version.jar");
//			}
//		});
//		
//		karafUsedAsFramework = files.length > 0;
//		
//		return karafUsedAsFramework;
//	}
}
