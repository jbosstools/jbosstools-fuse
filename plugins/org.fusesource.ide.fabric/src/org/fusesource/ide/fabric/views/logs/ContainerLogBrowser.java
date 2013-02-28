/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.fabric.views.logs;

import org.fusesource.ide.fabric.navigator.ContainerNode;
import org.fusesource.insight.log.service.LogQueryCallback;


public class ContainerLogBrowser extends LogBrowserSupport {
	private final ContainerNode node;

	public ContainerLogBrowser(ContainerNode node) {
		this.node = node;
	}


	@Override
	protected <T> T execute(LogQueryCallback<T> callback) {
		return node.getContainerTemplate().execute(callback);
	}

}
