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
package org.fusesource.ide.jmx.commons.views.messages;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.fusesource.ide.jmx.commons.messages.IExchangeBrowser;

/**
 * @author Aurelien Pupier
 *
 */
final class SelectionListenerForMessageView implements ISelectionListener {

	private final MessagesView messagesView;

	/**
	 * @param messagesView
	 */
	SelectionListenerForMessageView(MessagesView messagesView) {
		this.messagesView = messagesView;
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {

        // we only want to process selection change events from few selected sources...so filtering here
		if (!isRelevantSelectionSource(part)) {
            return;
        }

        IExchangeBrowser browser = ExchangeBrowsers.getSelectedExchangeBrowser(selection);
		messagesView.setExchangeBrowser(browser);
    }

	private boolean isRelevantSelectionSource(IWorkbenchPart part) {
		// we filter for specific selection sources...
		final String partClassName = part.getClass().getName();
		//@formatter:off
		List<String> relevantPartClassNames = Arrays.asList(new String[]{
				"org.jboss.tools.jmx.ui.internal.views.navigator.JMXNavigator",
				"org.fusesource.ide.jmx.commons.views.diagram.DiagramView",
				"org.eclipse.wst.server.ui.internal.view.servers.ServersView",
				"org.eclipse.wst.server.ui.internal.cnf.ServersView2"});
		//@formatter:on
		return relevantPartClassNames.contains(partClassName);
	}
}