/*******************************************************************************
 * Copyright (c) 2007 Jeff Mesnil
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.jmx.ui.internal.editors;


import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.fusesource.ide.jmx.ui.Messages;
import org.fusesource.ide.jmx.ui.internal.JMXImages;


public class ActionUtils {

    static void createLayoutActions(IManagedForm managedForm,
            final SashForm sashForm) {
        final ScrolledForm form = managedForm.getForm();
        Action haction = new Action("hor", Action.AS_RADIO_BUTTON) { //$NON-NLS-1$
            public void run() {
                sashForm.setOrientation(SWT.HORIZONTAL);
                form.reflow(true);
            }
        };
        haction.setChecked(true);
        haction.setToolTipText(Messages.horizontal);
        JMXImages.setLocalImageDescriptors(haction, "th_horizontal.gif"); //$NON-NLS-1$

        Action vaction = new Action("ver", Action.AS_RADIO_BUTTON) { //$NON-NLS-1$
            public void run() {
                sashForm.setOrientation(SWT.VERTICAL);
                form.reflow(true);
            }
        };
        vaction.setChecked(false);
        vaction.setToolTipText(Messages.vertical);
        JMXImages.setLocalImageDescriptors(vaction, "th_vertical.gif"); //$NON-NLS-1$
        form.getToolBarManager().add(haction);
        form.getToolBarManager().add(vaction);
    }
}
