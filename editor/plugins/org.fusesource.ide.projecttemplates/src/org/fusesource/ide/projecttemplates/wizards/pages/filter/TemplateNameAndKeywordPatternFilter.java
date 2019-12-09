/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 

package org.fusesource.ide.projecttemplates.wizards.pages.filter;

import java.util.List;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.dialogs.PatternFilter;
import org.fusesource.ide.projecttemplates.wizards.pages.model.TemplateItem;

/**
 * @author lhein
 *
 */
public class TemplateNameAndKeywordPatternFilter extends PatternFilter {
	
	public TemplateNameAndKeywordPatternFilter() {
		setIncludeLeadingWildcard(true);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.PatternFilter#isLeafMatch(org.eclipse.jface.viewers.Viewer, java.lang.Object)
	 */
	@Override
	protected boolean isLeafMatch(Viewer viewer, Object element) {
		return super.isLeafMatch(viewer, element) || isMatchingTag(element);
	}

	private boolean isMatchingTag(Object element) {
		if (element instanceof TemplateItem) {
			TemplateItem template = (TemplateItem) element;
			final List<String> tags = template.getKeywords();
			for (String tag : tags) {
				if (wordMatches(tag)) {
					return true;
				}
			}
			if (wordMatches(template.getDescription())) {
				return true;
			}
		}
		return false;
	}
}
