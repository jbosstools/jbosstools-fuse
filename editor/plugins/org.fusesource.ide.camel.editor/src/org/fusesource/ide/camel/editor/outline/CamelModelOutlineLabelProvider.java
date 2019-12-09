/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.camel.editor.outline;

import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.fusesource.ide.camel.editor.internal.CamelEditorUIActivator;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;

/**
 * @author lhein
 */
public class CamelModelOutlineLabelProvider extends LabelProvider implements IStyledLabelProvider {

	/**
	 * creates a label provider for the outline page
	 */
	public CamelModelOutlineLabelProvider() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
	 */
	@Override
	public Image getImage(Object element) {
		if (element instanceof AbstractCamelModelElement) {
			AbstractCamelModelElement cme = (AbstractCamelModelElement)element;
			Image icon = null;
			if (cme.getIconName() != null) {
				icon = CamelEditorUIActivator.getDefault().getImage(cme.getIconName().replaceAll(".png", "16.png"));
			}
			if (icon == null) {
				icon = CamelEditorUIActivator.getDefault().getImage("endpoint16.png");
			}
			return icon;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object element) {
		if (element instanceof AbstractCamelModelElement) {
			AbstractCamelModelElement cme = (AbstractCamelModelElement)element;
			return cme.getDisplayText();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider#getStyledText(java.lang.Object)
	 */
	@Override
	public StyledString getStyledText(Object element) {
		return null;
	}
}
