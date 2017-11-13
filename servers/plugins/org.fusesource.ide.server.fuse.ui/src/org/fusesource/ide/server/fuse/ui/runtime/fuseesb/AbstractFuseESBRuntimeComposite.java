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
package org.fusesource.ide.server.fuse.ui.runtime.fuseesb;

import java.io.File;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.server.fuse.ui.Messages;
import org.fusesource.ide.server.karaf.ui.runtime.KarafWizardDataModel;
import org.fusesource.ide.server.karaf.ui.runtime.v2x.KarafRuntimeComposite2x;

/**
 * @author lheinema
 *
 */
public abstract class AbstractFuseESBRuntimeComposite extends KarafRuntimeComposite2x {

	public AbstractFuseESBRuntimeComposite(Composite parent, IWizardHandle wizardHandle, KarafWizardDataModel model) {
		super(parent, wizardHandle, model);
		wizardHandle.setTitle(Messages.FuseESBRuntimeComposite_wizard_tite);
		wizardHandle.setDescription(Messages.FuseESBRuntimeComposite_wizard_desc);
	}
	
	@Override
	public void handleEvent(Event event) {
		boolean valid = false;
		if (event.type == SWT.FocusIn){
			handleFocusEvent(event);
		} else{
			if (event.widget == txtKarafDir) {
				valid = validate();
				if (valid){
					String installDir = txtKarafDir.getText();
					model.setKarafInstallDir(installDir);
				}
			}
		}
		
		wizardHandle.update();
	}
	
	@Override
	public boolean validate() {
		valid = super.validate();
		
		if (valid) {
			String dirLocation = txtKarafDir.getText().trim();
			if (!Strings.isBlank(dirLocation)) { 
				valid = validateRuntimeFolder(dirLocation);
			} else {
				wizardHandle.setMessage(Messages.AbstractKarafRuntimeComposite_wizard_help_msg, IMessageProvider.NONE); //$NON-NLS-1$
			}	
		}
		
		return valid;
	}
	
	protected boolean validateRuntimeFolder(String dirLocation) {
		File file = new File(dirLocation);
		if (!file.exists()) {
			wizardHandle.setMessage(Messages.AbstractKarafRuntimeComposite_no_dir,
					IMessageProvider.ERROR);
		} else if (!file.isDirectory()) {
			wizardHandle.setMessage(Messages.AbstractKarafRuntimeComposite_not_a_dir,
					IMessageProvider.ERROR);
		} else{
			File binFuse = new File(dirLocation + SEPARATOR + Messages.FuseESBRuntimeComposite_bin_fuseesb); 
			File binFuseBat = new File(dirLocation + SEPARATOR + Messages.FuseESBRuntimeComposite_bin_fuseesb_bat); 
			if (binFuse.exists() || binFuseBat.exists() ) {
				valid = true;
				wizardHandle.setMessage("", IMessageProvider.NONE); //$NON-NLS-1$
			} else {
				wizardHandle.setMessage(Messages.AbstractKarafRuntimeComposite_invalid_dir, IMessageProvider.ERROR); //$NON-NLS-1$
			}
		}
		return false;
	}
}
