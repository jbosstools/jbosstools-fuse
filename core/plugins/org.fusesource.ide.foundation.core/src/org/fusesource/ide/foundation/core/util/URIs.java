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

package org.fusesource.ide.foundation.core.util;

public class URIs {

	public static String getScheme(String uri) {
		if (uri != null) {
			int idx = uri.indexOf(':');
			if (idx > 0) {
				return uri.substring(0, idx);
			}
		}
		return "";
	}

	public static String getRemaining(String uri) {
		if (uri != null) {
			int idx = uri.indexOf(':');
			if (idx > 0) {
				String answer = uri.substring(idx + 1);
				while (answer.startsWith("/")) {
					answer = answer.substring(1);
				}
				return answer;
			}
		}
		return uri;
	}

	public static boolean isMockEndpointURI(String value) {
		return value.startsWith("mock:");
	}

	public static boolean isTimerEndpointURI(String value) {
		return value.startsWith("timer:") || value.startsWith("quartz:");
	}

}
