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

package org.fusesource.ide.commons.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class XmlUtilities {

	protected static Map<String, String> unescape = new HashMap<String, String>();
	
	static {
		unescape.put("&lt;", "<");
		unescape.put("&gt;", ">");
		unescape.put("&amp;", "&");
		unescape.put("&quot;", "\"");
	}
	
	public static String unescape(String text) {
		if (text == null) {
			return null;
		}
		Set<Entry<String, String>> entrySet = unescape.entrySet();
		for (Entry<String, String> entry : entrySet) {
			text = text.replaceAll(entry.getKey(), entry.getValue());
		}
		return text;
	}
}
