/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.server.karaf.core.server;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author lhein
 */
public class BaseKarafConfigPropertyProvider implements
		IKarafConfigurationPropertyProvider {

	private Properties configProps = new Properties();
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.server.karaf.core.server.IKarafConfigurationPropertyProvider#getConfigurationProperty(java.lang.String, java.io.File)
	 */
	@Override
	public String getConfigurationProperty(String propertyName,
			File configPropertyFile) {
		
		if (configProps.isEmpty()) {
			loadPropertiesFromFile(configPropertyFile);
		}
		
		return configProps.getProperty(propertyName, null);
	}

	private void loadPropertiesFromFile(File configPropertyFile) {
		BufferedInputStream bis = null;
		try {
			bis = new BufferedInputStream(new FileInputStream(configPropertyFile));
			configProps.load(bis);	
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException ex) {
					// unable to close the stream
				}
			}
		}
	}
}
