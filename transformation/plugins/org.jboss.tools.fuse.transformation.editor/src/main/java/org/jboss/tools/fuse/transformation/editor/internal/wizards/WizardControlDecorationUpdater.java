/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.transformation.editor.internal.wizards;

import org.eclipse.core.internal.databinding.BindingStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationUpdater;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.graphics.Image;

/**
 * @author brianf
 *
 */
@SuppressWarnings("restriction")
public class WizardControlDecorationUpdater extends ControlDecorationUpdater {

    @Override
    protected Image getImage(IStatus status) {
        // For required validations, we do not want to display an
        // error icon since the user has not done anything wrong.
        if (status instanceof BindingStatus) {
            BindingStatus bs = (BindingStatus) status;
            if (bs.getSeverity() == IStatus.ERROR) {
                // Display a "required" decoration (asterisk).
                FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault().getFieldDecoration(
                        FieldDecorationRegistry.DEC_REQUIRED);
                return fieldDecoration.getImage();
            }
        }
        return super.getImage(status);
    }

}
