/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.launcher.ui;

import org.eclipse.osgi.util.NLS;

/**
 * @author lhein
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.fusesource.ide.launcher.ui.l10n.messages";

	public static String ExecutePomActionSupport_NoCamelXMLFileFoundMessage;
	public static String ExecutePomActionSupport_NoCamelXMLFileFoundTitle;
	public static String ExecutePomActionSupport_SelectConfigurationDialogTitle;
	public static String ExecutePomActionSupport_SelectDebugConfigurationmessage;
	public static String ExecutePomActionSupport_SelectRunConfigurationmessage;
	public static String ExecutePomActionSupport_UnableToLaunchMessage;
	public static String ExecutePomActionSupport_UnableToLaunchTitle;
	public static String xmlSelectionDialogOnRunAndDebugTitle;
	public static String disconnected;
	public static String suspended;
	public static String terminated;
	public static String launchConfigurationName;
	public static String launchConfiguraNameWithoutTest;
	
	static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }
}
