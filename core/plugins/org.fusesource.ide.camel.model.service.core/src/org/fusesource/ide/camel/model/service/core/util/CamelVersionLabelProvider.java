/******************************************************************************* 
 * Copyright (c) 2018 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.fusesource.ide.camel.model.service.core.util;

import java.util.Map;

import org.eclipse.jface.viewers.LabelProvider;
import org.fusesource.ide.camel.model.service.core.util.versionmapper.CamelVersionToDisplayNameMapper;

public class CamelVersionLabelProvider extends LabelProvider {
	
	private static Map<String, String> cachedMapping = null;
	
	@Override
	public String getText(Object element) {
		if (element instanceof String) {
			String camelVersion = (String)element;
			String displayName = getMapping().get(camelVersion);
			if (displayName != null) {
				return displayName;
			}
		}
		return super.getText(element);
	}

	private Map<String, String> getMapping() {
		if (cachedMapping == null) {
			setCachedMapping(new CamelVersionToDisplayNameMapper().getMapping());
		}
		return cachedMapping;
	}

	private static void setCachedMapping(Map<String, String> mapping) {
		cachedMapping = mapping;
	}
	
}
