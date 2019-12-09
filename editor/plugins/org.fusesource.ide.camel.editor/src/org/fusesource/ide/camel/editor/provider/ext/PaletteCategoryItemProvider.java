/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.camel.editor.provider.ext;

/**
 * @author lhein
 */
public interface PaletteCategoryItemProvider {
	enum CATEGORY_TYPE {
		COMPONENTS, ENDPOINTS, ROUTING, CONTROL_FLOW, TRANSFORMATION, MISCELLANEOUS, NONE, USER_DEFINED;

		public static CATEGORY_TYPE getCategoryType(String name) {
			if (name != null) {
				String enumName = name.toUpperCase().replace(' ', '_');
				for (CATEGORY_TYPE t : CATEGORY_TYPE.values()) {
					if (t.name().equalsIgnoreCase(enumName)) {
						return t;
					}
				}
				return USER_DEFINED;
			}
			return CATEGORY_TYPE.NONE;
		}
	}

	/**
	 * returns the category type
	 * 
	 * @return
	 */
	CATEGORY_TYPE getCategoryType();

	public abstract String getCategoryName();
}
