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
package org.fusesource.ide.projecttemplates.wizards.pages.model;

import java.util.Comparator;

/**
 * @author lhein
 */
public class NameAndWeightComparator implements Comparator<TemplateOrCategoryItem> {

	/* 
	 * (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(TemplateOrCategoryItem o1, TemplateOrCategoryItem o2) {
		int res = Integer.compare(o1.getWeight(), o2.getWeight());
		if (res == 0) {
			res = o1.getName().compareTo(o2.getName());
		}
		return res;
	}
}
