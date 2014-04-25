/*************************************************************************************
 * Copyright (c) 2014 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     JBoss by Red Hat - Initial implementation.
 ************************************************************************************/
package org.fusesource.ide.server.karaf.core.runtime.integration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.fusesource.ide.server.karaf.core.Activator;
import org.jboss.jdf.stacks.model.Stacks;
import org.jboss.tools.as.runtimes.integration.util.AbstractStacksDownloadRuntimesProvider;
import org.jboss.tools.as.test.core.ASMatrixTests;
import org.jboss.tools.runtime.core.model.DownloadRuntime;
import org.jboss.tools.stacks.core.model.StacksManager;
import org.osgi.framework.Bundle;

/**
 * Pull runtimes from a stacks file and return them to runtimes framework
 */
public class KarafDownloadRuntimesProvider extends AbstractStacksDownloadRuntimesProvider {
	private class CustomStacksManager extends StacksManager {
		public Stacks getStacksFromFile(File f) throws IOException {
			return super.getStacksFromFile(f);
		}
	}
	
	protected Stacks[] getStacks(IProgressMonitor monitor) {
		try {
			File f = getFileLocation(Activator.PLUGIN_ID, "resources/karaf.yaml");
			CustomStacksManager csm = new CustomStacksManager();
			Stacks s = csm.getStacksFromFile(f);
			return new Stacks[]{s};
		} catch(CoreException ce) {
			// TODO handle
		} catch(IOException ioe) {
			// TODO handle
		}
		return new Stacks[0];
	}
	
	
	
	
	
	// TODO move to util class
	public static File getFileLocation(String bundleId, String path) throws CoreException {
		Bundle bundle = Platform.getBundle(bundleId);
		URL url = null;
		try {
			url = FileLocator.resolve(bundle.getEntry(path));
		} catch (IOException e) {
			String msg = "Cannot find file " + path + " in " + ASMatrixTests.PLUGIN_ID;
			IStatus status = new Status(IStatus.ERROR, ASMatrixTests.PLUGIN_ID, msg, e);
			throw new CoreException(status);
		}
		String location = url.getFile();
		return new File(location);
	}
	

	@Override
	protected void traverseStacks(Stacks stacks,
			ArrayList<DownloadRuntime> list, IProgressMonitor monitor) {
		traverseStacks(stacks, list, "OSGI_SERVER", monitor);
	}

	@Override
	protected String getLegacyId(String id) {
		// TODO Auto-generated method stub
		return null;
	}
}
