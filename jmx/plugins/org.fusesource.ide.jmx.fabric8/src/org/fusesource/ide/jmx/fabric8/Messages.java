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

package org.fusesource.ide.jmx.fabric8;

import org.eclipse.osgi.util.NLS;

/**
 * @author lhein
 *
 */
public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.fusesource.ide.jmx.fabric8.l10n.messages";

	public static String openTerminalLabel;
	public static String openTerminalToolTip;
	public static String StartAgentAction;
	public static String StartAgentActionToolTip;
	public static String StopAgentAction;
	public static String StopAgentActionToolTip;
	public static String DestroyContainerAction;
	public static String DestroyContainerActionToolTip;
	public static String openWebConsoleLabel;
	public static String openWebConsoleToolTip;
		
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}