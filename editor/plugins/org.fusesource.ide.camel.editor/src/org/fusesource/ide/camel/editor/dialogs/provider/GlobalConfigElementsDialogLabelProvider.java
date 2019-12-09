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

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;
import org.fusesource.ide.camel.editor.dialogs.GlobalConfigCategoryItem;
import org.fusesource.ide.camel.editor.dialogs.GlobalConfigElementItem;

/**
 * @author lhein
 */
public class GlobalConfigElementsDialogLabelProvider extends StyledCellLabelProvider {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.StyledCellLabelProvider#update(org.eclipse
	 * .jface.viewers.ViewerCell)
	 */
	@Override
	public void update(ViewerCell cell) {
		Object element = cell.getElement();
		StyledString text = new StyledString();
		
		if (element instanceof GlobalConfigCategoryItem) {
			GlobalConfigCategoryItem cat = (GlobalConfigCategoryItem)element;
			Image img = cat.getIcon();
			text.append(cat.getName());
			cell.setImage(img);
			cell.setText(text.toString());
			cell.setStyleRanges(text.getStyleRanges());
		} else if (element instanceof GlobalConfigElementItem) {
			GlobalConfigElementItem elem = (GlobalConfigElementItem)element;
			Image img = elem.getIcon();
			text.append(elem.getName());
			cell.setImage(img);
			cell.setText(text.toString());
			cell.setStyleRanges(text.getStyleRanges());
		} else {
			// unhandled
		}
		super.update(cell);
	}
}
