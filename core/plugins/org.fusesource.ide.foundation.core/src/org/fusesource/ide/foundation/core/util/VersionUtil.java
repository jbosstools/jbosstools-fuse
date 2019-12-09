/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.foundation.core.util;

import org.apache.maven.artifact.versioning.ComparableVersion;

public class VersionUtil {
	
	protected static final ComparableVersion COMPARABLE_CAMEL_2_20_0_VERSION = new ComparableVersion("2.20.0");
	
	public boolean isStrictlyGreaterThan(String version1, String version2) {
		return new ComparableVersion(version1).compareTo(new ComparableVersion(version2)) > 0; 
	}

	public boolean isGreaterThan(String version1, String version2) {
		return new ComparableVersion(version1).compareTo(new ComparableVersion(version2)) >= 0; 
	}
	
	public boolean isStrictlyLowerThan2200(String camelVersion) {
		return new ComparableVersion(camelVersion).compareTo(COMPARABLE_CAMEL_2_20_0_VERSION) < 0;
	}
}
