/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.server.fuse.ui.runtime.fuseesb;

import java.io.File;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.fusesource.ide.server.fuse.ui.Messages;
import org.fusesource.ide.server.karaf.ui.runtime.KarafWizardDataModel;


/**
 * @author lhein
 */
public class FuseESBRuntimeComposite7x extends AbstractFuseESBRuntimeComposite {

	private static final String FUSE_BRANDING_PREFIX = "fuse-branding-";
	private static final String VERSION_FILE = "version.properties";
	
	/**
	 * constructor 
	 * 
	 * @param parentComposite
	 * @param wizardHandle
	 * @param model
	 */
	public FuseESBRuntimeComposite7x(Composite parent, IWizardHandle wizardHandle, KarafWizardDataModel model) {
		super(parent, wizardHandle, model);
		wizardHandle.setTitle(Messages.FuseESBRuntimeComposite_wizard_tite);
		wizardHandle.setDescription(Messages.FuseESBRuntimeComposite_wizard_desc);
	}
	
	@Override
	protected boolean doClassPathEntiresExist(String karafInstallDir) {
		File versionsFile = new File(String.format("%s%s%s", karafInstallDir, SEPARATOR, VERSION_FILE));
		File fuseLibFolder = new File(String.format("%s%s%s", karafInstallDir, SEPARATOR, LIB_FOLDER));
		File[] files = fuseLibFolder.listFiles( (File file) -> file.getName().startsWith(FUSE_BRANDING_PREFIX) && file.getName().endsWith(".jar"));
		boolean brandingFound = false;
		if (files != null) {
			brandingFound = files.length>0;
		}
		return super.doClassPathEntiresExist(karafInstallDir) && (brandingFound || versionsFile.exists() && versionsFile.isFile());
	}
}
