/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
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
public final class KarafMockRuntimeCreationUtil {
	
	public static final String KARAF_22 = "org.fusesource.ide.karaf.runtime.22";
	public static final String KARAF_23 = "org.fusesource.ide.karaf.runtime.23";
	public static final String KARAF_2X = "org.fusesource.ide.karaf.runtime.2x";
	public static final String KARAF_30 = "org.fusesource.ide.karaf.runtime.30";
	public static final String KARAF_3X = "org.fusesource.ide.karaf.runtime.3x";	
	public static final String KARAF_40 = "org.fusesource.ide.karaf.runtime.40";
	public static final String KARAF_41 = "org.fusesource.ide.karaf.runtime.41";
	public static final String KARAF_4X = "org.fusesource.ide.karaf.runtime.4x";
	
	public static final String[] SUPPORTED_2X_RUNTIMES = new String[] {
		KARAF_22, KARAF_23
	};
	
	public static final String[] SUPPORTED_3X_RUNTIMES = new String[] {
		KARAF_30 
	};
	
	public static final String[] SUPPORTED_4X_RUNTIMES = new String[] {
		KARAF_40, KARAF_41
	};
	
	private KarafMockRuntimeCreationUtil() {
		// util class
	}
	
	/**
	 * creates a mock runtime folder structure 
	 * 
	 * @param runtimeId		the runtime type id to use
	 * @param runtimePath	the path where to create the structure
	 * @return	true on success
	 */
	public static boolean create2xRuntimeMock(String runtimeId, IPath runtimePath) {
		boolean runtimeCreated = true;
		
		if (isSupported2xRuntime(runtimeId)) {
			createBaseKarafFolderSkeleton(runtimePath);
			try {
				copyKarafJarToSkeleton(runtimeId, runtimePath);
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
	public static boolean create3xRuntimeMock(String runtimeId, IPath runtimePath) {
		boolean runtimeCreated = true;
		
		if (isSupported3xRuntime(runtimeId)) {
			createBaseKarafFolderSkeleton(runtimePath);
			try {
				copyKarafJarToSkeleton(runtimeId, runtimePath);
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
	public static boolean create4xRuntimeMock(String runtimeId, IPath runtimePath) {
		boolean runtimeCreated = true;
		
		if (isSupported4xRuntime(runtimeId)) {
			createBaseKarafFolderSkeleton(runtimePath);
			try {
				copyKarafJarToSkeleton(runtimeId, runtimePath);
			} catch (CoreException ex) {
				runtimeCreated = false;
			}
		}		
		
		return runtimeCreated;
	}
	
	private static boolean isSupported2xRuntime (String runtimeId) {
		for (String id : SUPPORTED_2X_RUNTIMES) {
			if (id.equals(runtimeId)) return true;
		}
		return false;
	}
	
	private static boolean isSupported3xRuntime (String runtimeId) {
		for (String id : SUPPORTED_3X_RUNTIMES) {
			if (id.equals(runtimeId)) return true;
		}
		return false;
	}
	
	private static boolean isSupported4xRuntime (String runtimeId) {
		for (String id : SUPPORTED_4X_RUNTIMES) {
			if (id.equals(runtimeId)) return true;
		}
		return false;
	}
	
	private static void createBaseKarafFolderSkeleton(IPath path) {
		IPath folder = path.append("bin");
		folder.toFile().mkdirs();
		
		folder = path.append("deploy");
		folder.toFile().mkdirs();
		
		folder = path.append("etc");
		folder.toFile().mkdirs();
		
		folder = path.append("lib").append("boot");
		folder.toFile().mkdirs();
		
		folder = path.append("system");
		folder.toFile().mkdirs();
	}
	
	protected static String getMajorMinor(String runtimeId) {
		return String.format("%s.%s", runtimeId.charAt(runtimeId.length()-2), runtimeId.charAt(runtimeId.length()-1));
	}
	
	private static void copyKarafJarToSkeleton(String runtimeId, IPath runtimePath) throws CoreException {
		IPath libFolder = runtimePath.append("lib");
		// just to be sure its there...
		libFolder.toFile().mkdirs();
		// now copy the jar file in
		String fileName;
		if (runtimeId.endsWith(".22")) {
			fileName = "karaf_2.2.jar";
		} else if (runtimeId.endsWith(".23")) {
			fileName = "karaf_2.3.jar";
		} else if (runtimeId.endsWith(".30")) {
			fileName = "karaf_3.0.jar";
		} else if (runtimeId.endsWith(".40")) {
			fileName = "org.apache.karaf.main_4.0.jar";
		} else if (runtimeId.endsWith(".41")) {
			fileName = "org.apache.karaf.main_4.1.jar";
		} else {
			// 2.x - take any of the above
			fileName = "karaf_2.3.jar";
		}
		
		File serverJarLoc = BundleUtils.getFileLocation(
				"org.fusesource.ide.server.tests",
				"mockData" + File.separator + fileName);
		if (isSupported4xRuntime(runtimeId)) {
			FileUtil.fileSafeCopy(serverJarLoc, libFolder.append("boot").append(String.format("org.apache.karaf.main-%s.0.jar", getMajorMinor(runtimeId))).toFile());
		} else {
			FileUtil.fileSafeCopy(serverJarLoc, libFolder.append("karaf.jar").toFile());			
		}
	}
}
