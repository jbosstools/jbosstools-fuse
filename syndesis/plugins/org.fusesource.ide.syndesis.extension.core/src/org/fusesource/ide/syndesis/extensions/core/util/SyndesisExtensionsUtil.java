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
		}
		
		// major version needs to be a digit
		if (valid && !isNumber(parts[0])) {
			valid = false;
		} else if (valid && parts.length>1) { // minor version needs to be a digit or a digit with qualifier
			String minorVersion = parts[1];
			valid = isValidVersionPart(minorVersion);
		}
		
		// micro version can be either a digit or a combination of digit and qualifier or might miss fully
		if (valid && parts.length > 2) {
			String microVersion = parts[2];
			valid = isValidVersionPart(microVersion);
		}
		
		return valid;
	}

	private static boolean isValidVersionPart(String version) {
		if (version.indexOf('-') != -1) {
			if (!isValidVersionWithQualifier(version)) {
				return false;				
			}
		} else if (!isNumber(version)) {
			return false;
		}
		return true;
	}
	
	private static boolean isValidVersionWithQualifier(String part) {
		String[] parts = part.split("-");
		return parts.length>1 && isNumber(parts[0]);
	}
	
	private static boolean isNumber(String part) {
		try {
			Integer.parseInt(part);
		} catch (NumberFormatException ex) {
			return false;
		}
		return true;
	}
}
