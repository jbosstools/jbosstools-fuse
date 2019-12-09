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
package org.fusesource.ide.camel.editor.globalconfiguration;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

class GlobalConfigContentProvider implements ITreeContentProvider {

	private final CamelGlobalConfigEditor camelGlobalConfigEditor;

	GlobalConfigContentProvider(CamelGlobalConfigEditor camelGlobalConfigEditor) {
		this.camelGlobalConfigEditor = camelGlobalConfigEditor;
	}

	@Override
	public void dispose() {
		// Nothing to dispose
	}

	@Override
	public Object[] getChildren(Object parent) {
		return getElements(parent);
	}

	@Override
	public Object[] getElements(Object parent) {
		if (parent instanceof Map) {
			Object[] catIds = ((Map<?, ?>) parent).keySet().toArray();
			Arrays.sort(catIds, new Comparator<Object>() {
				@Override
				public int compare(Object o1, Object o2) {
					if (CamelGlobalConfigEditor.DEFAULT_CAT_ID.equals(o1.toString())) {
						return 1;
					} else if (CamelGlobalConfigEditor.DEFAULT_CAT_ID.equals(o2.toString())) {
						return -1;
					} else {
						return o1.toString().compareTo(o2.toString());
					}
				}
			});
			return catIds;
		} else if (parent instanceof String) {
			return this.camelGlobalConfigEditor.getModel().get((String)parent).toArray();
		}
		
		return new Object[0];
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		return element instanceof String;
	}

	@Override
	public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
		// Nothing special to update
	}
}