/*******************************************************************************
 * Copyright (c) 2006 Jeff Mesnil
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    "Rob Stryker" <rob.stryker@redhat.com> - Initial implementation
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

package org.fusesource.ide.jmx.ui.internal.actions;


import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.fusesource.ide.jmx.ui.Messages;
import org.fusesource.ide.jmx.ui.internal.JMXImages;
import org.fusesource.ide.jmx.ui.internal.wizards.NewConnectionWizard;


/**
 * Create a new connection
 */
public class NewConnectionAction extends Action {
	public NewConnectionAction() {
		super(Messages.NewConnectionAction);
        JMXImages.setLocalImageDescriptors(this, "add_obj.gif");  //$NON-NLS-1$
        //JMXImages.setLocalImageDescriptors(this, "attachAgent.gif");  //$NON-NLS-1$
	}

	public void run() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				IWizard wizard = new NewConnectionWizard();
				WizardDialog d = new WizardDialog(new Shell(), wizard);
				d.open();
			}
		} );
	}
}
