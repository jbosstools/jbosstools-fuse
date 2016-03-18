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
package org.fusesource.ide.camel.editor.globalconfiguration;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

class GlobalConfigContentProvider implements ITreeContentProvider {

	private final CamelGlobalConfigEditor camelGlobalConfigEditor;

	GlobalConfigContentProvider(CamelGlobalConfigEditor camelGlobalConfigEditor) {
		this.camelGlobalConfigEditor = camelGlobalConfigEditor;
	}

	@Override
	public void dispose() {
	}

	@Override
	public Object[] getChildren(Object parent) {
		return getElements(parent);
	}

	@Override
	public Object[] getElements(Object parent) {
		if (parent instanceof HashMap) {
			Object[] catIds = ((HashMap<?, ?>) parent).keySet().toArray();
			Arrays.sort(catIds, new Comparator<Object>() {
				@Override
				public int compare(Object o1, Object o2) {
					if (o1.toString().equals(CamelGlobalConfigEditor.DEFAULT_CAT_ID)) return 1;
					if (o2.toString().equals(CamelGlobalConfigEditor.DEFAULT_CAT_ID)) return -1;
					return o1.toString().compareTo(o2.toString());
				}
			});
			return catIds;
		} else if (parent instanceof String) {
			return this.camelGlobalConfigEditor.getModel().get((String)parent).toArray();
		}
		
		return new Object[0];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang
	 * .Object)
	 */
	@Override
	public Object getParent(Object element) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang
	 * .Object)
	 */
	@Override
	public boolean hasChildren(Object element) {
		return element instanceof String;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse
	 * .jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
	}
}