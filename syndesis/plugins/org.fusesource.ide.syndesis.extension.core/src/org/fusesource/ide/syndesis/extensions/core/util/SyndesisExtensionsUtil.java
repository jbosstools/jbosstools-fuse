/******************************************************************************* 
 * Copyright (c) 2018 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.fusesource.ide.syndesis.extensions.core.util;

/**
 * @author lheinema
 */
public class SyndesisExtensionsUtil {
	
	private SyndesisExtensionsUtil() {
		// util class
	}
	
	/**
	 * checks if the syndesis version given is a valid version
	 * 
	 * @param version
	 * @return
	 */
	public static boolean isValidSyndesisExtensionVersion(String version) {
		boolean valid = true;
		String[] parts = version.split("\\.");
		if (parts.length<2 || version.trim().endsWith(".") || version.trim().startsWith(".")) {
			valid = false;
		} else {
			int foundNumbers = 0;
			for (String part : parts) {
				try {
					Integer.parseInt(part);
					foundNumbers++;
				} catch (NumberFormatException ex) {
					if (foundNumbers<2) {
						valid = false;
					}
					break;
				}
			}			
		}
		return valid;
	}
}
