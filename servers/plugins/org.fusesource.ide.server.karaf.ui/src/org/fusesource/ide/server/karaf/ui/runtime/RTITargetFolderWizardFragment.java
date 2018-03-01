/*******************************************************************************
 * Create a WST wizard fragment associated with a composite to handle the 
 * user-specification of a Red Hat Fuse Runtime installation directory.
 * 
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.server.karaf.ui.runtime;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.eclipse.wst.server.ui.wizard.WizardFragment;
import org.fusesource.ide.server.karaf.ui.Messages;

public class RTITargetFolderWizardFragment extends WizardFragment {
	public static final String FUSE_RT_LOC = "fuseRTLoc";

	protected RTIComposite comp;

	public RTITargetFolderWizardFragment() {
		// do nothing
	}

	@Override
	public boolean hasComposite() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.server.ui.internal.task.WizardTask#getWizardPage()
	 */
	@Override
	public Composite createComposite(Composite parent, IWizardHandle wizard) {
		comp = new RTIComposite(parent, getTaskModel(), wizard);
		wizard.setTitle(Messages.AbstractKarafRuntimeComposite_jboss_fuse_rt_label);
		wizard.setDescription(Messages.AbstractKarafRuntimeComposite_selectInstallDir);
		return comp;
	}

	@Override
	public boolean isComplete() {
		return this.comp == null || this.comp.isDisposed() ? false : this.comp.isComplete();
	}
}