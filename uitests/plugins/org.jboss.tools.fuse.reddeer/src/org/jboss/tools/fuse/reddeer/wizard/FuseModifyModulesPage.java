/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.reddeer.wizard;

import org.eclipse.reddeer.core.reference.ReferencedComposite;
import org.eclipse.reddeer.eclipse.wst.server.ui.wizard.ModifyModulesPage;
import org.eclipse.reddeer.swt.impl.button.CheckBox;

/**
 * Wizard page for adding and removing Fuse modules on the Fuse server.
 * 
 * @author tsedmik
 */
public class FuseModifyModulesPage extends ModifyModulesPage {

	public FuseModifyModulesPage(ReferencedComposite ref) {
		super(ref);
	}

	/**
	 * Sets option 'If server is started, publish changes immediately'
	 * 
	 * @param value
	 *            true - option is checked, false - option is not checked
	 */
	public void setImmeadiatelyPublishing(boolean value) {

		new CheckBox().toggle(value);
	}
}
