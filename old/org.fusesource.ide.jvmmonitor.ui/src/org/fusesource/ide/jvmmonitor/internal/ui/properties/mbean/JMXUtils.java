package org.fusesource.ide.jvmmonitor.internal.ui.properties.mbean;

import javax.management.ObjectName;

public class JMXUtils {

	/**
	 * Gets the type name.
	 * 
	 * @param objectName
	 *            The object name
	 * @return The type name
	 */
	public static String getTypeName(ObjectName objectName) {
		String canonicalName = objectName.getCanonicalName();
		if (canonicalName != null) {
			String[] split = canonicalName.split("type=");
			if (split.length < 2) {
				split = canonicalName.split("Type=");
			}
			if (split.length >= 2) {
				String type = split[1];
				return type.split(",")[0]; //$NON-NLS-1$
			}
			//System.out.println("Can't split: " + canonicalName);
		}
		return "";
	}

}
