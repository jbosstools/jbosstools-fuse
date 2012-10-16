package org.fusesource.ide.commons.util;

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

}
