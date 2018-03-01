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
package org.fusesource.ide.syndesis.extensions.ui.util;

import org.fusesource.ide.projecttemplates.util.CommonNewProjectMetaData;
import org.fusesource.ide.syndesis.extensions.core.model.SyndesisExtension;

/**
 * @author lheinema
 */
public class NewSyndesisExtensionProjectMetaData extends CommonNewProjectMetaData {

	private SyndesisExtension syndesisExtensionConfig;
	
	/**
	 * @return the syndesisExtensionConfig
	 */
	public SyndesisExtension getSyndesisExtensionConfig() {
		return this.syndesisExtensionConfig;
	}
	
	/**
	 * @param syndesisExtensionConfig the syndesisExtensionConfig to set
	 */
	public void setSyndesisExtensionConfig(SyndesisExtension syndesisExtensionConfig) {
		this.syndesisExtensionConfig = syndesisExtensionConfig;
	}
}
