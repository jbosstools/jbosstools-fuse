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
package org.fusesource.ide.server;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.jboss.ide.eclipse.as.core.util.FileUtil;
import org.jboss.tools.as.test.core.internal.utils.BundleUtils;

/**
 * @author lhein
 */
public final class MockRuntimeCreationUtil {
	
	public static final String[] SUPPORTED_RUNTIMES = new String[] {
		 "org.fusesource.ide.karaf.runtime.20"
		,"org.fusesource.ide.karaf.runtime.21"
		,"org.fusesource.ide.karaf.runtime.22"
		,"org.fusesource.ide.karaf.runtime.23"
		,"org.fusesource.ide.karaf.runtime.2x"
	};
	
	public static boolean createRuntimeMock(String runtimeId, IPath runtimePath) {
		boolean runtimeCreated = true;
		
		if (isSupportedRuntime(runtimeId)) {
			createBaseKarafFolderSkeleton(runtimePath);
			try {
				copyKarafJarToSkeleton(runtimeId, runtimePath);
			} catch (CoreException ex) {
				runtimeCreated = false;
			}
		}		
		
		return runtimeCreated;
	}
	
	private static boolean isSupportedRuntime (String runtimeId) {
		for (String id : SUPPORTED_RUNTIMES) {
			if (id.equals(runtimeId)) return true;
		}
		return false;
	}
	
	private static void createBaseKarafFolderSkeleton(IPath path) {
		/**
		 * karaf
		 *  |-bin
		 *  |-deploy
		 *  |-etc
		 *  |-lib
		 *  |  |-karaf.jar
		 *  |  |-<NO *-version.jar as this means Karaf used in other product>
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
	
	private static void copyKarafJarToSkeleton(String runtimeId, IPath runtimePath) throws CoreException {
		IPath libFolder = runtimePath.append("lib");
		// just to be sure its there...
		libFolder.toFile().mkdirs();
		// now copy the jar file in
		String fileName = null;
		if (runtimeId.endsWith(".20")) {
			fileName = "karaf_2.0.jar";
		} else if (runtimeId.endsWith(".21")) {
			fileName = "karaf_2.1.jar";
		} else if (runtimeId.endsWith(".22")) {
			fileName = "karaf_2.2.jar";
		} else if (runtimeId.endsWith(".23")) {
			fileName = "karaf_2.3.jar";
		} else {
			// 2.x - take any of the above
			fileName = "karaf_2.3.jar";
		}
		
		File serverJarLoc = BundleUtils.getFileLocation("mockData" + File.separator + fileName);
		FileUtil.fileSafeCopy(serverJarLoc, libFolder.append("karaf.jar").toFile());
	}
}
