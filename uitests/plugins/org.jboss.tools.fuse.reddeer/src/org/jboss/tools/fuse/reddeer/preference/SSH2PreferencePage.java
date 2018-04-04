/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.reddeer.preference;

import org.eclipse.reddeer.core.reference.ReferencedComposite;
import org.eclipse.reddeer.jface.preference.PreferencePage;
import org.eclipse.reddeer.swt.impl.text.LabeledText;

/**
 * Represents the SSH2 (Network Connections) preference page
 * 
 * @author tsedmik
 */
public class SSH2PreferencePage extends PreferencePage {

	public static final String SSH2HOME = "SSH2 home:";
	
	public SSH2PreferencePage(ReferencedComposite ref) {
		super(ref, "General", "Network Connections", "SSH2");
	}

	public void setSSH2Home(String path) {
		new LabeledText(SSH2HOME).setText(path);
	}
}
