/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.projecttemplates.util.camel;

import java.util.Comparator;

import org.apache.maven.artifact.versioning.ComparableVersion;

public class CamelFacetVersionComparator implements Comparator<String> {

	@Override
	public int compare(String facetVersion1, String facetVersion2) {
		return new ComparableVersion(facetVersion1).compareTo(new ComparableVersion(facetVersion2));
	}

}
