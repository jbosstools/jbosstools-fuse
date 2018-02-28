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
package org.fusesource.ide.syndesis.extensions.ui.wizards.pages;

import java.util.Map;

import org.eclipse.jface.viewers.LabelProvider;
import org.fusesource.ide.syndesis.extensions.core.util.IgniteVersionMapper;

/**
 * @author lheinema
 */
public class SyndesisVersionLabelProvider extends LabelProvider {

	private static Map<String, String> cachedMapping = null;

	@Override
	public String getText(Object element) {
		if (element instanceof String) {
			String syndesisVersion = (String) element;
			String displayName = getMapping().get(syndesisVersion);
			if (displayName != null) {
				return displayName;
			}
		}
		return super.getText(element);
	}

	private Map<String, String> getMapping() {
		if (cachedMapping == null) {
			setCachedMapping(new IgniteVersionMapper().getMapping());
		}
		return cachedMapping;
	}

	private static void setCachedMapping(Map<String, String> mapping) {
		cachedMapping = mapping;
	}
}
