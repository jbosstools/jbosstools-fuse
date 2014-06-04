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
package org.fusesource.ide.server.fabric8.core.runtime.integration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.fusesource.ide.commons.util.BundleResourceUtils;
import org.fusesource.ide.server.fabric8.core.Activator;
import org.fusesource.ide.server.karaf.core.runtime.integration.AbstractStacksDownloadRuntimesProvider;
import org.jboss.jdf.stacks.model.Stacks;
import org.jboss.tools.runtime.core.model.DownloadRuntime;
import org.jboss.tools.stacks.core.model.StacksManager;

/**
 * @author lhein
 *
 */
public class Fabric8DownloadRuntimesProvider extends AbstractStacksDownloadRuntimesProvider {
	
	private class CustomStacksManager extends StacksManager {
		@Override
		public Stacks getStacksFromFile(File f) throws IOException {
			return super.getStacksFromFile(f);
		}
	}

	@Override
	protected Stacks[] getStacks(IProgressMonitor monitor) {
		try {
			File f = BundleResourceUtils.getFileFromBundle(Activator.PLUGIN_ID, "resources/fabric8.yaml");
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