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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.fusesource.ide.camel.editor.internal.UIMessages;

/**
 * @author Aurelien Pupier
 *
 */
public final class ComponentGroupedByTagsTreeContenProvider implements ITreeContentProvider {

	private ComponentManager componentManager;

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput instanceof ComponentManager) {
			componentManager = (ComponentManager) newInput;
		}
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean hasChildren(Object element) {
		return element instanceof String;
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof ComponentManager) {
			componentManager = (ComponentManager) inputElement;
			Set<String> res = new HashSet<>();
			res.addAll(componentManager.getTags());
			if (!componentManager.getComponentWithoutTag().isEmpty()) {
				res.add(UIMessages.componentGroupedByTagsTreeContenProviderUncategorized);
			}
			return res.toArray();
		}
		return (Object[]) inputElement;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof String){
			if (UIMessages.componentGroupedByTagsTreeContenProviderUncategorized.equals(parentElement)) {
				return componentManager.getComponentWithoutTag().toArray();
			} else {
				return componentManager.getComponentForTag((String) parentElement).toArray();
			}
		}
		return null;
	}
}