/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.projecttemplates.util;

import java.util.Comparator;

import org.osgi.framework.Version;

/**
 * basically an osgi version comparator
 * 
 * @author lhein
 */
public class CamelFacetVersionComparator implements Comparator<String> {

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(String v1, String v2) {
		Version cv1 = new Version(v1);
		Version cv2 = new Version(v2);
		return cv1.compareTo(cv2);
	}
}
