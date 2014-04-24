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
package org.fusesource.ide.commons.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.fusesource.ide.commons.Activator;
import org.osgi.framework.Bundle;

/**
 * @author lhein
 */
public abstract class BundleResourceUtils {
	
	/**
	 * retrieves a resource from the given bundle
	 * 
	 * @param bundleSymbolicName
	 * @param path
	 * @return
	 * @throws CoreException
	 */
	public static File getFileFromBundle(String bundleSymbolicName, String path) throws CoreException {
		Bundle bundle = Platform.getBundle(bundleSymbolicName);
		URL url = null;
		try {
			url = FileLocator.resolve(bundle.getEntry(path));
		} catch (IOException e) {
			String msg = "Cannot find file " + path + " in bundle " + bundle.getSymbolicName();
			IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, msg, e);
			throw new CoreException(status);
		}
		String location = url.getFile();
		return new File(location);
	}
}
