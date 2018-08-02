/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.editor.restconfiguration.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Display;
import org.fusesource.ide.camel.editor.internal.UIMessages;
import org.fusesource.ide.camel.editor.restconfiguration.RestConfigConstants;
import org.fusesource.ide.camel.editor.restconfiguration.RestConfigEditor;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelContextElement;

/**
 * @author lheinema
 */
public class DeleteRestConfigurationAction extends RestEditorAction {
	
	/**
	 * 
	 * @param imageReg
	 */
	public DeleteRestConfigurationAction(RestConfigEditor parent, ImageRegistry imageReg) {
		super(parent, imageReg);
	}
	
	@Override
	public void run() {
		if (MessageDialog.openQuestion(Display.getCurrent().getActiveShell(), UIMessages.restEditorDeleteRestConfigurationActionDialogTitle, UIMessages.restEditorDeleteRestConfigurationActionDialogMessage)) {
			deleteWithoutUserConfirmation();
		}
	}
	
	@Override
	public String getToolTipText() {
		return UIMessages.restEditorDeleteRestConfigurationActionButtonTooltip;
	}
	
	public void deleteWithoutUserConfirmation() {
		CamelContextElement ctx = parent.getCtx();
		if (!ctx.getRestConfigurations().isEmpty()) {
			ctx.removeRestConfiguration(ctx.getRestConfigurations().values().iterator().next());
			ctx.clearRestConfigurations();
		}
		if (!ctx.getRestElements().isEmpty()) {
			List<AbstractCamelModelElement> toDelete = new ArrayList<>(ctx.getRestElements().values());
			for (AbstractCamelModelElement cme : toDelete) {
				ctx.removeRestElement(cme);
			}
			ctx.clearRestElements();
		}
		parent.reload();
	}	
	
	@Override
	public String getImageName() {
		return RestConfigConstants.IMG_DESC_DELETE;
	}
}
