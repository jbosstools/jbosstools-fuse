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
package org.fusesource.ide.camel.editor.component.wizard;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.fusesource.ide.camel.editor.provider.DiagramTypeProvider;
import org.fusesource.ide.camel.editor.provider.ToolBehaviourProvider;
import org.fusesource.ide.camel.model.service.core.catalog.components.Component;

/**
 * @author Aurelien Pupier
 *
 */
public class WhiteListComponentFilter extends ViewerFilter {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof Component) {
			return select((Component) element);
		} else if (element instanceof String) {
			ComponentManager componentManager = (ComponentManager) viewer.getInput();
			for (Component component : componentManager.getComponentForTag((String) element)) {
				if (select(component)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @param component
	 * @return
	 */
	private boolean select(Component component) {
		return !new ToolBehaviourProvider(new DiagramTypeProvider()).shouldBeIgnored(component.getSchemeTitle());
	}

}
