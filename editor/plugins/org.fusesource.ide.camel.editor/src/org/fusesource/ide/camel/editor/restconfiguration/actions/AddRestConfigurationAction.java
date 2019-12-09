/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.editor.restconfiguration.actions;

import org.eclipse.jface.resource.ImageRegistry;
import org.fusesource.ide.camel.editor.internal.UIMessages;
import org.fusesource.ide.camel.editor.restconfiguration.RestConfigConstants;
import org.fusesource.ide.camel.editor.restconfiguration.RestConfigEditor;
import org.fusesource.ide.camel.editor.restconfiguration.RestConfigUtil;
import org.fusesource.ide.camel.model.service.core.model.RestConfigurationElement;

/**
 * @author lheinema
 */
public class AddRestConfigurationAction extends RestEditorAction {
	
	private RestConfigUtil util = new RestConfigUtil();

	/**
	 * 
	 * @param imageReg
	 */
	public AddRestConfigurationAction(RestConfigEditor parent, ImageRegistry imageReg) {
		super(parent, imageReg);
	}
	
	@Override
	public void run() {
		RestConfigurationElement newrce = util.createRestConfigurationNode(parent.getCtx());
		newrce.initialize();
		newrce.setHost("localhost"); //$NON-NLS-1$
		newrce.setBindingMode("off"); //$NON-NLS-1$
		if (parent.getCtx().getRestConfigurations().isEmpty()) {
			parent.getCtx().addRestConfiguration(newrce);
		}
		parent.reload();
	}
	
	@Override
	public String getToolTipText() {
		return UIMessages.restEditorAddRestConfigurationActionButtonTooltip;
	}
	
	@Override
	protected String getImageName() {
		return RestConfigConstants.IMG_DESC_ADD;
	}
}
