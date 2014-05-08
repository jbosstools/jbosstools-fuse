/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.server.tests.util;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.jboss.ide.eclipse.as.core.util.FileUtil;
import org.jboss.tools.as.test.core.internal.utils.BundleUtils;

/**
 * @author lhein
 */
public final class ServiceMixMockRuntimeCreationUtil {
	
	public static final String SMX_45 = "org.fusesource.ide.servicemix.runtime.45";
	public static final String SMX_4x = "org.fusesource.ide.servicemix.runtime.4x";
	public static final String SMX_50 = "org.fusesource.ide.servicemix.runtime.50";
	public static final String SMX_5x = "org.fusesource.ide.servicemix.runtime.5x";	
	
	public static final String[] SUPPORTED_4X_RUNTIMES = new String[] {
		SMX_45
	};
	
	public static final String[] SUPPORTED_5X_RUNTIMES = new String[] {
		SMX_50 
	};
	
	/**
	 * creates a mock runtime folder structure 
	 * 
	 * @param runtimeId		the runtime type id to use
	 * @param runtimePath	the path where to create the structure
	 * @return	true on success
	 */
	public static boolean create4xRuntimeMock(String runtimeId, IPath runtimePath) {
		boolean runtimeCreated = true;
		
		if (isSupported4xRuntime(runtimeId)) {
			createBaseServiceMixFolderSkeleton(runtimePath);
			try {
				copyServiceMixJarToSkeleton(runtimeId, runtimePath);
			} catch (CoreException ex) {
				runtimeCreated = false;
			}
		}		
		
		return runtimeCreated;
	}
	
	/**
	 * creates a mock runtime folder structure 
	 * 
	 * @param runtimeId		the runtime type id to use
	 * @param runtimePath	the path where to create the structure
	 * @return	true on success
	 */
	public static boolean create5xRuntimeMock(String runtimeId, IPath runtimePath) {
		boolean runtimeCreated = true;
		
		if (isSupported5xRuntime(runtimeId)) {
			createBaseServiceMixFolderSkeleton(runtimePath);
			try {
				copyServiceMixJarToSkeleton(runtimeId, runtimePath);
			} catch (CoreException ex) {
				runtimeCreated = false;
			}
		}		
		
		return runtimeCreated;
	}
	
	private static boolean isSupported4xRuntime (String runtimeId) {
		for (String id : SUPPORTED_4X_RUNTIMES) {
			if (id.equals(runtimeId)) return true;
		}
		return false;
	}
	
	private static boolean isSupported5xRuntime (String runtimeId) {
		for (String id : SUPPORTED_5X_RUNTIMES) {
			if (id.equals(runtimeId)) return true;
		}
		return false;
	}
	
	private static void createBaseServiceMixFolderSkeleton(IPath path) {
		/**
		 * servicemix
		 *  |-bin
		 *  |-deploy
		 *  |-etc
		 *  |-lib
		 *  |  |-servicemix-version.jar
		 *  |-system
		 */
		IPath folder = path.append("bin");
		folder.toFile().mkdirs();
		
		folder = path.append("deploy");
		folder.toFile().mkdirs();
		
		folder = path.append("etc");
		folder.toFile().mkdirs();
		
		folder = path.append("lib");
		folder.toFile().mkdirs();
		
		folder = path.append("system");
		folder.toFile().mkdirs();
	}
	
	private static void copyServiceMixJarToSkeleton(String runtimeId, IPath runtimePath) throws CoreException {
		IPath libFolder = runtimePath.append("lib");
		// just to be sure its there...
		libFolder.toFile().mkdirs();
		// now copy the jar file in
		String fileName = null;
		if (runtimeId.endsWith(".45")) {
			fileName = "servicemix_4.5.jar";
		} else if (runtimeId.endsWith(".50")) {
			fileName = "servicemix_5.0.jar";
		} else {
			// 5.x - take any of the above
			fileName = "servicemix_5.0.jar";
		}
		
		File serverJarLoc = BundleUtils.getFileLocation(
				"org.fusesource.ide.server.tests",
				"mockData" + File.separator + fileName);
		FileUtil.fileSafeCopy(serverJarLoc, libFolder.append("servicemix-version.jar").toFile());
	}
}
