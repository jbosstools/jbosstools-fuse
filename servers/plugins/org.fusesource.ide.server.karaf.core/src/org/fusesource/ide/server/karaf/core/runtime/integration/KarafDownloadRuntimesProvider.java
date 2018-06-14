/*************************************************************************************
 * Copyright (c) 2014 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat - Initial implementation.
 ************************************************************************************/
package org.fusesource.ide.server.karaf.core.runtime.integration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.fusesource.ide.foundation.core.util.BundleUtils;
import org.fusesource.ide.server.karaf.core.Activator;
import org.jboss.jdf.stacks.model.Stacks;
import org.jboss.tools.runtime.core.model.DownloadRuntime;
import org.jboss.tools.stacks.core.model.StacksManager;

/**
 * Pull runtimes from a stacks file and return them to runtimes framework
 */
public class KarafDownloadRuntimesProvider extends AbstractStacksDownloadRuntimesProvider {
	
	private class CustomStacksManager extends StacksManager {
		/*
		 * (non-Javadoc)
		 * @see org.jboss.tools.stacks.core.model.StacksManager#getStacksFromFile(java.io.File)
		 */
		@Override
		public Stacks getStacksFromFile(File f) throws IOException {
			return super.getStacksFromFile(f);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.as.runtimes.integration.util.AbstractStacksDownloadRuntimesProvider#getStacks(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected Stacks[] getStacks(IProgressMonitor monitor) {
		try {
			File f = BundleUtils.getFileFromBundle(Activator.PLUGIN_ID, "resources/karaf.yaml");
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

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.as.runtimes.integration.util.AbstractStacksDownloadRuntimesProvider#traverseStacks(org.jboss.jdf.stacks.model.Stacks, java.util.ArrayList, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected void traverseStacks(Stacks stacks,
			ArrayList<DownloadRuntime> list, IProgressMonitor monitor) {
		traverseStacks(stacks, list, "OSGI_SERVER", monitor);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.as.runtimes.integration.util.AbstractStacksDownloadRuntimesProvider#getLegacyId(java.lang.String)
	 */
	@Override
	protected String getLegacyId(String id) {
		// TODO Auto-generated method stub
		return null;
	}
}
