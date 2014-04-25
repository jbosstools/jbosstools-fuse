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

package org.fusesource.ide.server.fuse.ui.runtime.fuseesb;

import java.io.File;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.fusesource.ide.server.fuse.ui.Messages;
import org.fusesource.ide.server.karaf.ui.runtime.KarafWizardDataModel;
import org.fusesource.ide.server.karaf.ui.runtime.v2x.KarafRuntimeComposite2x;


/**
 * @author lhein
 */
public class FuseESBRuntimeComposite6x extends KarafRuntimeComposite2x {

	protected static final String CONF_FOLDER = "etc";
	protected static final String CONF_FILE_NAME = "org.apache.karaf.shell.cfg";
	public static final String CONF_FILE = String.format("%s%s%s", CONF_FOLDER, SEPARATOR, CONF_FILE_NAME);
	protected static final String LIB_FOLDER = "lib";
	protected static final String LIB_BIN_FOLDER = String.format("%s%s%s", LIB_FOLDER, SEPARATOR, "bin");
	protected static final String LIB_KARAF_JAR = String.format("%s%s%s", LIB_FOLDER, SEPARATOR, "karaf.jar");
	protected static final String LIB_KARAF_JAAS_JAR = String.format("%s%s%s", LIB_FOLDER, SEPARATOR, "karaf-jaas-boot.jar");
	protected static final String LIB_KARAF_CLIENT_JAR = String.format("%s%s%s", LIB_BIN_FOLDER, SEPARATOR, "karaf-client.jar");
	private static final String LIB_ESB_VERSION_JAR = String.format("%s%s%s", LIB_FOLDER, SEPARATOR, "esb-version.jar");

	/**
	 * constructor 
	 * 
	 * @param parent
	 * @param wizardHandle
	 * @param model
	 */
	public FuseESBRuntimeComposite6x(Composite parent, IWizardHandle wizardHandle, KarafWizardDataModel model) {
		super(parent, wizardHandle, model);
		wizardHandle.setTitle(Messages.FuseESBRuntimeComposite_wizard_tite);
		wizardHandle.setDescription(Messages.FuseESBRuntimeComposite_wizard_desc);
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.server.karaf.ui.runtime.AbstractKarafRuntimeComposite#doClassPathEntiresExist(java.lang.String)
	 */
	@Override
	protected boolean doClassPathEntiresExist(String karafInstallDir) {
		File libESBVersionJar = new File(String.format("%s%s%s", karafInstallDir, SEPARATOR, LIB_ESB_VERSION_JAR));
		return super.doClassPathEntiresExist(karafInstallDir) && libESBVersionJar.exists();
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.server.karaf.ui.runtime.AbstractKarafRuntimeComposite#validate()
	 */
	@Override
	public boolean validate() {
		valid = false;

		String dirLocation = txtKarafDir.getText().trim();
		if (dirLocation != null && !"".equals(dirLocation)) { 
			File file = new File(dirLocation);
			if (!file.exists()) {
				wizardHandle.setMessage(Messages.AbstractKarafRuntimeComposite_no_dir,
						IMessageProvider.ERROR);
			} else if (!file.isDirectory()) {
				wizardHandle.setMessage(Messages.AbstractKarafRuntimeComposite_not_a_dir,
						IMessageProvider.ERROR);
			} else {
				File binFuseESB = new File(dirLocation + SEPARATOR + Messages.FuseESBRuntimeComposite_bin_fuseesb); 
				File binFuseESBBat = new File(dirLocation + SEPARATOR + Messages.FuseESBRuntimeComposite_bin_fuseesb_bat); 
				File confFile = new File(getKarafPropFileLocation(dirLocation));
				if ((binFuseESB.exists() || binFuseESBBat.exists() )&& confFile.exists()
						&& doClassPathEntiresExist(dirLocation)) {
					valid = true;
					wizardHandle.setMessage("", IMessageProvider.NONE); //$NON-NLS-1$
				} else {
					wizardHandle.setMessage(Messages.AbstractKarafRuntimeComposite_invalid_dir, IMessageProvider.ERROR); //$NON-NLS-1$
				}
			}
		} else {
			wizardHandle.setMessage(Messages.AbstractKarafRuntimeComposite_wizard_help_msg, IMessageProvider.NONE); //$NON-NLS-1$
		}

		return valid;
	}

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
}