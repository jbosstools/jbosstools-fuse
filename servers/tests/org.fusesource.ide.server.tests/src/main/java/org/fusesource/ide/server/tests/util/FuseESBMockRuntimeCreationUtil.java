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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.fusesource.ide.server.tests.FuseServerTestActivator;
import org.jboss.ide.eclipse.as.core.util.FileUtil;
import org.jboss.tools.as.test.core.internal.utils.BundleUtils;

/**
 * @author lhein
 */
public final class FuseESBMockRuntimeCreationUtil {
	
	public static final String FUSEESB_60 = "org.fusesource.ide.fuseesb.runtime.60";
	public static final String FUSEESB_61 = "org.fusesource.ide.fuseesb.runtime.61";
	public static final String FUSEESB_62 = "org.fusesource.ide.fuseesb.runtime.62";
	public static final String FUSEESB_63 = "org.fusesource.ide.fuseesb.runtime.63";
	public static final String FUSEESB_6X = "org.fusesource.ide.fuseesb.runtime.6x";
	
	public static final String FUSEESB_70 = "org.fusesource.ide.fuseesb.runtime.70";
	public static final String FUSEESB_7X = "org.fusesource.ide.fuseesb.runtime.7x";
	
	public static final String[] SUPPORTED_6X_RUNTIMES = new String[] {
		FUSEESB_60, FUSEESB_61, FUSEESB_62, FUSEESB_63
	};
	
	public static final String[] SUPPORTED_7X_RUNTIMES = new String[] {
		FUSEESB_70
	};
	
	/**
	 * creates a mock runtime folder structure 
	 * 
	 * @param runtimeId		the runtime type id to use
	 * @param runtimePath	the path where to create the structure
	 * @return	true on success
	 */
	public static boolean create6xRuntimeMock(String runtimeId, IPath runtimePath) {
		boolean runtimeCreated = true;
		
		if (isSupported6xRuntime(runtimeId)) {
			createBaseFuseFolderSkeleton(runtimePath);
			try {
				copyFuseJarToSkeleton(runtimeId, runtimePath);
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
	public static boolean create7xRuntimeMock(String runtimeId, IPath runtimePath) {
		boolean runtimeCreated = true;
		
		if (isSupported7xRuntime(runtimeId)) {
			createBaseFuseFolderSkeleton(runtimePath);
			try {
				copyFuseJarToSkeleton(runtimeId, runtimePath);
			} catch (CoreException ex) {
				runtimeCreated = false;
			}
		}		
		
		return runtimeCreated;
	}
	
	private static boolean isSupported6xRuntime (String runtimeId) {
		for (String id : SUPPORTED_6X_RUNTIMES) {
			if (id.equals(runtimeId)) return true;
		}
		return false;
	}
	
	private static boolean isSupported7xRuntime (String runtimeId) {
		for (String id : SUPPORTED_7X_RUNTIMES) {
			if (id.equals(runtimeId)) return true;
		}
		return false;
	}
	
	private static void createBaseFuseFolderSkeleton(IPath path) {
		/**
		 * fuse
		 *  |-bin
		 *  |-deploy
		 *  |-etc
		 *  |-lib
		 *  |  |-esb-version.jar
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
		
		Properties versionProperties = new Properties();
		versionProperties.setProperty("version", "7.0.0.fuse-000080");
		try {
			versionProperties.store(new FileOutputStream(path.append("version.properties").toFile()), "");
		} catch (IOException ex) {
			FuseServerTestActivator.getDefault().getLog().log(new Status(IStatus.ERROR, FuseServerTestActivator.PLUGIN_ID, "Failed to create version.properties file.", ex));
		}
	}
	
	protected static String getMajorMinor(String runtimeId) {
		return String.format("%s.%s", runtimeId.charAt(runtimeId.length()-2), runtimeId.charAt(runtimeId.length()-1));
	}
	
	private static void copyFuseJarToSkeleton(String runtimeId, IPath runtimePath) throws CoreException {
		IPath libFolder = runtimePath.append("lib");
		// just to be sure its there...
		libFolder.toFile().mkdirs();
		// now copy the jar file in
		String fileName;
		if (runtimeId.endsWith(".60")) {
			fileName = "fuse_6.0.jar";
		} else if (runtimeId.endsWith(".61")) {
			fileName = "fuse_6.1.jar";
		} else if (runtimeId.endsWith(".62")) {
			fileName = "fuse_6.2.jar";
		} else if (runtimeId.endsWith(".63")) {
			fileName = "fuse_6.3.jar";
		} else if (runtimeId.endsWith(".70")) {
			fileName = "fuse_7.0.jar";
		} else {
			// 6.x - take any of the above
			fileName = "fuse_6.1.jar";
		}
		
		File serverJarLoc = BundleUtils.getFileLocation(
				"org.fusesource.ide.server.tests",
				"mockData" + File.separator + fileName);
		
		if (isSupported7xRuntime(runtimeId)) {
			FileUtil.fileSafeCopy(serverJarLoc, libFolder.append(String.format("fuse-branding-%s.0.fuse-000080.jar", getMajorMinor(runtimeId))).toFile());
		} else {
			FileUtil.fileSafeCopy(serverJarLoc, libFolder.append("esb-version.jar").toFile());			
		}
	}
}
