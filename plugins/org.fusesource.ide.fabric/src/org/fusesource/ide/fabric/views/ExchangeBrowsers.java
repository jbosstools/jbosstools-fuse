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

package org.fusesource.ide.fabric.views;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.fusesource.fon.util.messages.Exchanges;
import org.fusesource.fon.util.messages.IExchangeBrowser;
import org.fusesource.fon.util.messages.contenttype.MessagesNamespaceXmlContentDescriber;
import org.fusesource.ide.fabric.FabricPlugin;



public class ExchangeBrowsers {

	public static IExchangeBrowser getSelectedExchangeBrowser(ISelection selection) {
		IExchangeBrowser answer = null;
		if (selection instanceof IStructuredSelection) {
			Object input = ((IStructuredSelection) selection).getFirstElement();
			answer = toIExchangeBrowser(input);
		}
		return answer;
	}

	public static IExchangeBrowser toIExchangeBrowser(Object input) {
		IExchangeBrowser answer = null;
		if (input instanceof IExchangeBrowser) {
			answer = (IExchangeBrowser) input;
		} else if (input != null) {
			answer = Exchanges.asExchangeList(input);
			if (answer == null && input instanceof IFile) {
				IFile file = (IFile) input;
				try {
					boolean messagesXml = MessagesNamespaceXmlContentDescriber.isXmlFormat(file);
					if (messagesXml) {
						answer = Exchanges.loadExchanges(file, file.getContents());
					}
				} catch (Exception e) {
					FabricPlugin.getLogger().info("Failed to parse " + file + " as message exchange XML: " + e, e);
				}
				
			}
			if (answer == null) {
				answer = (IExchangeBrowser) Platform.getAdapterManager().getAdapter(input, IExchangeBrowser.class);
			}
		}
		return answer;
	}
}
