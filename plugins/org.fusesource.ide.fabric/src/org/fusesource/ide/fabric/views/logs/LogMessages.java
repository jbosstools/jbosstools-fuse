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

import org.eclipse.osgi.util.NLS;

public class LogMessages extends NLS {
	private static final String BUNDLE_NAME = LogMessages.class.getName();

	public static String messageLabel;
	public static String messageTooltip;

	public static String exceptionLabel;
	public static String exceptionTooltip;

	public static String openStackTrace;

	static {
		NLS.initializeMessages(BUNDLE_NAME, LogMessages.class);
	}
}
