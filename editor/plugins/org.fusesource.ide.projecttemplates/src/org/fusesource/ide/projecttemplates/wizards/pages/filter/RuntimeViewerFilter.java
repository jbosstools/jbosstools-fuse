/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.projecttemplates.wizards.pages.filter;

import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.wst.server.core.IRuntime;

public class RuntimeViewerFilter extends ViewerFilter {

	private Map<String, IRuntime> serverRuntimes;
	private List<String> possibleIds;

	public RuntimeViewerFilter(Map<String, IRuntime> serverRuntimes, List<String> possibleRuntimeIds) {
		this.serverRuntimes = serverRuntimes;
		this.possibleIds = possibleRuntimeIds;
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		IRuntime serverRuntime = serverRuntimes.get(element);
		if (serverRuntime != null) {
			String runtimeId = serverRuntime.getRuntimeType().getId();
			for (String possibleId : possibleIds) {
				if(runtimeId.startsWith(possibleId)) {
					return true;
				}
			}
			return false;
		} else {
			return true;
		}
	}

}
