/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.foundation.ui.util;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;

public class ControlDecorationHelper {

	public ControlDecoration addInformationOnFocus(Control control, String informationText) {
		ControlDecoration decoration = null;
		if (control != null) {
			decoration = new ControlDecoration(control, SWT.BOTTOM | SWT.LEFT);
			Image image = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_INFORMATION).getImage();
			decoration.setImage(image);
			decoration.setShowOnlyOnFocus(true);
			decoration.setDescriptionText(informationText);
		}
		return decoration;
	}
	
	public ControlDecoration addErrorToControl(Control control, String informationText) {
		ControlDecoration decoration = null;
		if (control != null) {
			decoration = new ControlDecoration(control, SWT.TOP | SWT.LEFT);
			Image image = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_ERROR).getImage();
			decoration.setImage(image);
			decoration.setShowOnlyOnFocus(false);
			decoration.setDescriptionText(informationText);
		}
		return decoration;
	}
	
	public void removeDecorationFromControl(ControlDecoration decoration) {
		if (decoration != null) {
			decoration.hide();
			decoration.dispose();
		}
	}
}
