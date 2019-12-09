/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
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
public class FuseESBRuntimeComposite6x extends AbstractFuseESBRuntimeComposite {

	private static final String LIB_FUSE_VERSION_JAR = String.format("%s%s%s", LIB_FOLDER, SEPARATOR, "esb-version.jar");
	
	/**
	 * constructor 
	 * 
	 * @param parentComposite
	 * @param wizardHandle
	 * @param model
	 */
	public FuseESBRuntimeComposite6x(Composite parent, IWizardHandle wizardHandle, KarafWizardDataModel model) {
		super(parent, wizardHandle, model);
		wizardHandle.setTitle(Messages.FuseESBRuntimeComposite_wizard_tite);
		wizardHandle.setDescription(Messages.FuseESBRuntimeComposite_wizard_desc);
	}
	
	@Override
	protected boolean doClassPathEntiresExist(String karafInstallDir) {
		File libESBVersionJar = new File(String.format("%s%s%s", karafInstallDir, SEPARATOR, LIB_FUSE_VERSION_JAR));
		return super.doClassPathEntiresExist(karafInstallDir) && libESBVersionJar.exists();
	}
}
