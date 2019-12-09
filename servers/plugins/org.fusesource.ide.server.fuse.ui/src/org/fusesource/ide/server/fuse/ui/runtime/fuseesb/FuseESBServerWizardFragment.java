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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.server.ui.internal.ImageResource;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.fusesource.ide.server.fuse.ui.Messages;
import org.fusesource.ide.server.karaf.ui.runtime.v2x.KarafServerWizardFragment2x;


/**
 * @author lhein
 */
public class FuseESBServerWizardFragment extends
		KarafServerWizardFragment2x {

	/*
	 * (non-Javadoc)
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
}
