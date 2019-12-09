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

package org.fusesource.ide.server.karaf.core;

import org.eclipse.osgi.util.NLS;

/**
 * @author lhein
 */
public class Messages extends NLS {
	
	private static final String BUNDLE_NAME = "org.fusesource.ide.server.karaf.core.l10n.messages";

	public static String shellViewLabel;
	public static String KarafPollerServerFound;
	public static String KarafPollerServerNotFound;
	
	/**
	 * the below keys belong the PollThread class which will be moved back to JBT asap
	 */
	public static String ServerPollerThreadName;
	public static String ServerStatePollerUnexpectedError;
	public static String PollingStarting;
	public static String PollingShuttingDown;
	public static String PollerFailure;
	public static String PollerAborted;
	public static String PollingStartupFailed;
	public static String PollingShutdownFailed;
	public static String PollingStartupSuccess;
	public static String PollingShutdownSuccess;
	public static String StartupPollerNotFound;
	public static String ShutdownPollerNotFound;

	public static String CreateDownloadRuntimes;
	public static String LoadRemoteRuntimes;
	
	public static String DeployErrorMissingManifest;
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}