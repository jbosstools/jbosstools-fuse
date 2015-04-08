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

import org.eclipse.core.databinding.ValidationStatusProvider;
import org.eclipse.jface.databinding.dialog.ValidationMessageProvider;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.core.databinding.Binding;
import org.eclipse.core.runtime.IStatus;

/**
 * @author brianf
 *
 */
public class WizardValidationMessageProvider extends ValidationMessageProvider {
    @Override
    public int getMessageType(ValidationStatusProvider statusProvider) {
        if (statusProvider instanceof Binding) {
            Binding binding = (Binding) statusProvider;
            IStatus status = (IStatus) binding.getValidationStatus()
                    .getValue();

            // For required validations, we do not want to display an error
            // icon since the user has not done anything wrong.
            if (status.matches(IStatus.ERROR)) {
                return IMessageProvider.INFORMATION;
            }
        }
        return super.getMessageType(statusProvider);
    }

}
