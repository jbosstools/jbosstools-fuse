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
package org.fusesource.ide.jmx.fabric8.navigator.actions;

import java.net.URL;

import org.eclipse.swt.program.Program;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.fusesource.ide.commons.camel.tools.Strings;
import org.fusesource.ide.commons.ui.actions.ActionSupport;
import org.fusesource.ide.jmx.fabric8.Fabric8JMXPlugin;
import org.fusesource.ide.jmx.fabric8.Messages;
import org.fusesource.ide.jmx.fabric8.navigator.Fabric8Node;

/**
 * opens the hawtio web console from inside eclipse using the external system 
 * browser configured in Eclipse settings. On problems it will try to invoke
 * the default program for the url registered in the operating system.
 * 
 * @author lhein
 */
public class OpenWebConsoleAction extends ActionSupport {

	private Fabric8Node fabric;
	
	/**
	 * creates the open web console action
	 * 
	 * @param fabric
	 */
	public OpenWebConsoleAction(Fabric8Node fabric) {
		super(Messages.openWebConsoleLabel, Messages.openWebConsoleToolTip, Fabric8JMXPlugin.getDefault().getImageDescriptor("fabric8.png"));
		this.fabric = fabric;
	}
	
	/**
	 * Sets the enabled flag based on whether there's a single root element
	 */
	public void updateEnabled() {
		boolean hasWebConsoleUrl = false;
		try {
			if (this.fabric != null) hasWebConsoleUrl = !Strings.isEmpty(this.fabric.getFacade().queryWebConsoleUrl());
		} catch (Exception ex) {
			Fabric8JMXPlugin.getLogger().error(ex);
		}
		setEnabled(hasWebConsoleUrl);
	}
	
	public void setFabric(Fabric8Node fabric) {
		this.fabric = fabric;
		updateEnabled();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		String url = null;
		try {
			url = this.fabric.getFacade().queryWebConsoleUrl();
		} catch (Exception ex) {
			Fabric8JMXPlugin.getLogger().error(ex);
		}
		try {
			IWebBrowser browser = PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser();
			browser.openURL(new URL(url));
		} catch (Exception ex) {
			Fabric8JMXPlugin.getLogger().error(ex);
			Program.launch(url);
		}
	}
}
