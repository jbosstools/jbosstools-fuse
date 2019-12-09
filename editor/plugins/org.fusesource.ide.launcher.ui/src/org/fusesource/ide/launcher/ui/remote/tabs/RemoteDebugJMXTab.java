/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.launcher.ui.remote.tabs;

import org.fusesource.ide.launcher.debug.util.ICamelDebugConstants;
import org.fusesource.ide.launcher.ui.tabs.DebugJmxTab;

public class RemoteDebugJMXTab extends DebugJmxTab {
	
	@Override
	protected String getDefaultJMXUri() {
		return ICamelDebugConstants.DEFAULT_REMOTE_JMX_URI;
	}
}
