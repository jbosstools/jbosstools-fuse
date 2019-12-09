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

package org.fusesource.ide.server.karaf.ui.runtime.v2x;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.fusesource.ide.server.karaf.ui.runtime.AbstractKarafServerWizardFragment;


/**
 * @author lhein
 */
public class KarafServerWizardFragment2x extends
		AbstractKarafServerWizardFragment {

	private static final String DEFAULT_HOST = "localhost";
	private static final String PROP_SSH_PORT_NUM = "sshPort";
	private static final String PROP_SSH_HOST = "sshHost";
	private static final String PROP_SSH_USER = "sshLogin";
	private static final String PROP_SSH_PASS = "sshPassword";
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.server.karaf.ui.runtime.AbstractKarafServerWizardFragment#readFromPropertiesFile(java.io.File)
	 */
	@Override
	protected void readFromPropertiesFile(File confFile)
			throws FileNotFoundException, IOException, NumberFormatException {
		Properties pr = new Properties();
		pr.load(new FileInputStream(confFile));
		
		String hostString = pr.getProperty(PROP_SSH_HOST);
		model.setHostName(hostString != null ? hostString : DEFAULT_HOST);
		String portString = pr.getProperty(PROP_SSH_PORT_NUM);
		model.setPortNumber(Integer.parseInt(portString));
		
		// TODO: username and password should be obtained from the karaf install somehow
		model.setUserName(pr.getProperty(PROP_SSH_USER));
		model.setPassword(pr.getProperty(PROP_SSH_PASS));
	}
}
