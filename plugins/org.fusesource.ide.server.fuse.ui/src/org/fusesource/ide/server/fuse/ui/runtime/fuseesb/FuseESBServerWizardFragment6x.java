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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.server.ui.internal.ImageResource;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.fusesource.ide.server.fuse.core.FuseESBUtils;
import org.fusesource.ide.server.fuse.ui.Messages;
import org.fusesource.ide.server.karaf.core.runtime.IKarafRuntime;
import org.fusesource.ide.server.karaf.ui.runtime.v2x.KarafServerWizardFragment2x;


/**
 * @author lhein
 */
public class FuseESBServerWizardFragment6x extends
		KarafServerWizardFragment2x {
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.server.karaf.ui.runtime.AbstractKarafServerWizardFragment#createComposite(org.eclipse.swt.widgets.Composite, org.eclipse.wst.server.ui.wizard.IWizardHandle)
	 */
	@Override
	public Composite createComposite(Composite parent, IWizardHandle handle) {
		Composite c = super.createComposite(parent, handle);
		handle.setTitle(Messages.FuseESBServerPorpertiesComposite_wizard_title);
		handle.setDescription(Messages.FuseESBServerPorpertiesComposite_wizard_desc);
		handle.setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_WIZBAN_NEW_RUNTIME));
		return c;
	}

	/**
	 * determines the version of the karaf installation from the manifest of the main bundle
	 * 
	 * @param runtime	the runtime to use for grabbing the install location
	 * @return	the version as string or null on errors
	 */
	protected String determineVersion(IKarafRuntime runtime) {
		String version = null;

		if (runtime != null && runtime.getLocation() != null) {
			File folder = runtime.getLocation().toFile();
			if (folder.exists() && folder.isDirectory()) {
				version = FuseESBUtils.getVersion(folder);
			}
		}

		return version;
	}
}