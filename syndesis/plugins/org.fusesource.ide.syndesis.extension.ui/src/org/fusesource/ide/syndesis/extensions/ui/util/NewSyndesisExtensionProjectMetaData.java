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

import org.fusesource.ide.projecttemplates.adopters.AbstractProjectTemplate;
import org.fusesource.ide.projecttemplates.util.CommonNewProjectMetaData;
import org.fusesource.ide.projecttemplates.util.ICamelSupport;
import org.fusesource.ide.projecttemplates.util.ITemplateSupport;
import org.fusesource.ide.syndesis.extensions.core.model.SyndesisExtension;

/**
 * @author lheinema
 */
public class NewSyndesisExtensionProjectMetaData extends CommonNewProjectMetaData implements ITemplateSupport, ICamelSupport {

	private AbstractProjectTemplate template;
	private SyndesisExtension syndesisExtensionConfig;
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.projecttemplates.util.ICamelSupport#getCamelVersion()
	 */
	@Override
	public String getCamelVersion() {
		return syndesisExtensionConfig.getCamelVersion();
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.projecttemplates.util.ICamelSupport#setCamelVersion(java.lang.String)
	 */
	@Override
	public void setCamelVersion(String camelVersion) {
		this.syndesisExtensionConfig.setCamelVersion(camelVersion);
	}
	
	/**
	 * @return the template
	 */
	@Override
	public AbstractProjectTemplate getTemplate() {
		return this.template;
	}
	
	/**
	 * @return the syndesisExtensionConfig
	 */
	public SyndesisExtension getSyndesisExtensionConfig() {
		return this.syndesisExtensionConfig;
	}
	
	/**
	 * @param template the template to set
	 */
	@Override
	public void setTemplate(AbstractProjectTemplate template) {
		this.template = template;
	}
	
	/**
	 * @param syndesisExtensionConfig the syndesisExtensionConfig to set
	 */
	public void setSyndesisExtensionConfig(SyndesisExtension syndesisExtensionConfig) {
		this.syndesisExtensionConfig = syndesisExtensionConfig;
	}
}
