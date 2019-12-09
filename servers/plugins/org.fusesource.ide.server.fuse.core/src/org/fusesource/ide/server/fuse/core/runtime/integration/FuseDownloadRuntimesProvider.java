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
package org.fusesource.ide.server.fuse.core.runtime.integration;

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.fusesource.ide.server.karaf.core.runtime.integration.AbstractStacksDownloadRuntimesProvider;
import org.jboss.jdf.stacks.model.Stacks;
import org.jboss.tools.runtime.core.model.DownloadRuntime;
import org.jboss.tools.stacks.core.model.StacksManager;

/**
 * @author lhein
 *
 */
public class FuseDownloadRuntimesProvider extends AbstractStacksDownloadRuntimesProvider {
	
	@Override
	protected Stacks[] getStacks(IProgressMonitor monitor) {
		return new StacksManager().getStacks("Loading Downloadable Runtimes", monitor, StacksManager.StacksType.PRESTACKS_TYPE, StacksManager.StacksType.STACKS_TYPE);
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