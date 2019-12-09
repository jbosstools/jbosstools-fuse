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
package org.fusesource.ide.camel.editor.dialogs.provider;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.fusesource.ide.camel.editor.dialogs.GlobalConfigCategoryItem;

/**
 * @author lhein
 */
public class GlobalConfigElementsDialogContentProvider implements ITreeContentProvider {
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang
	 * .Object)
	 */
	@Override
	public Object[] getChildren(Object parent) {
		return getElements(parent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITreeContentProvider#getElements(java.lang
	 * .Object)
	 */
	@Override
	public Object[] getElements(Object parent) {
		if (parent instanceof List) {
			return ((List<?>)parent).toArray();
		} else if (parent instanceof GlobalConfigCategoryItem) {
			return ((GlobalConfigCategoryItem)parent).getChildren().toArray();
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
		return element instanceof GlobalConfigCategoryItem && ((GlobalConfigCategoryItem)element).getChildren().isEmpty()==false;
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
		// TODO Auto-generated method stub

	}
}
