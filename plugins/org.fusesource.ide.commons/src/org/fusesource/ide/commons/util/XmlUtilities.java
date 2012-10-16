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
