/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.foundation.ui.tree;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonViewer;
import org.jboss.tools.jmx.ui.internal.views.navigator.JMXNavigator;

/**
 * @author Aurelien Pupier
 *
 */
public final class RefreshNodeRunnable implements Runnable {

	private final NodeSupport nodeSupport;

	/**
	 * @param nodeSupportToRefresh
	 */
	public RefreshNodeRunnable(NodeSupport nodeSupportToRefresh) {
		this.nodeSupport = nodeSupportToRefresh;
	}

	@Override
	public void run() {
		JMXNavigator jmxView = getJMXNavigatorView();
		if (jmxView != null) {
			CommonViewer commonViewer = (CommonViewer) jmxView.getAdapter(CommonViewer.class);
			commonViewer.update(this.nodeSupport, null);
		}
	}

	private JMXNavigator getJMXNavigatorView() {
		final IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (activeWorkbenchWindow != null) {
			final IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
			if (activePage != null) {
				return (JMXNavigator) activePage.findView(JMXNavigator.VIEW_ID);
			}
		}
		return null;
	}
}