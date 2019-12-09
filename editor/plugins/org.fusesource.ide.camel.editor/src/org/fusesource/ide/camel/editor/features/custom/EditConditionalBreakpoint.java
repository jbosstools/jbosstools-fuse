/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.editor.features.custom;

import org.eclipse.core.resources.IFile;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.fusesource.ide.camel.editor.dialogs.ConditionalBreakpointEditorDialog;
import org.fusesource.ide.camel.editor.provider.ImageProvider;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.launcher.debug.model.CamelConditionalBreakpoint;
import org.fusesource.ide.launcher.debug.util.CamelDebugUtils;

/**
 * @author lhein
 *
 */
public class EditConditionalBreakpoint extends SetConditionalBreakpointFeature {
	/**
	 * creates the feature
	 * 
	 * @param fp
	 */
	public EditConditionalBreakpoint(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public void execute(ICustomContext context) {
		PictogramElement _pe = getPEFromContext(context);
		final Object bo = getBusinessObjectForPictogramElement(_pe);

		if (bo instanceof AbstractCamelModelElement) {
			AbstractCamelModelElement _ep = (AbstractCamelModelElement) bo;
			IFile contextFile = getContextFile();
			String fileName = contextFile.getName();
			String projectName = contextFile.getProject().getName();

			// now ask the user to define a condition using a language
			IBreakpoint bp = CamelDebugUtils.getBreakpointForSelection(_ep.getId(), fileName, projectName);
			if (bp != null && bp instanceof CamelConditionalBreakpoint) {
				CamelConditionalBreakpoint ccb = (CamelConditionalBreakpoint)bp;
				// TODO: open a dialog for the user to select language and enter the condition - maybe provide a helper for predefined variables
				ConditionalBreakpointEditorDialog dlg = new ConditionalBreakpointEditorDialog(Display.getDefault().getActiveShell(), _ep);
				dlg.setLanguage(ccb.getLanguage());
				dlg.setCondition(ccb.getConditionPredicate());
				dlg.setBlockOnOpen(true);
				if (Window.OK == dlg.open()) {
					String language = dlg.getLanguage();
					String condition = dlg.getCondition();
					ccb.setConditionPredicate(condition);
					ccb.setLanguage(language);
					// notify debug framework that this breakpoint has changed
					DebugPlugin.getDefault().getBreakpointManager().fireBreakpointChanged(ccb);
				}
			}
		}
		getDiagramBehavior().refreshRenderingDecorators(_pe);
	}

	@Override
	public String getName() {
		return "Edit Conditional Breakpoint";
	}

	@Override
	public String getDescription() {
		return "Modifies a conditional breakpoint on the selected endpoint node";
	}

	@Override
	public String getImageId() {
		return ImageProvider.IMG_PROPERTIES_BP;
	}

	@Override
	public boolean isAvailable(IContext context) {
		ICustomContext cc = (ICustomContext) context;
		PictogramElement _pe = getPEFromContext(cc);
		final Object bo = getBusinessObjectForPictogramElement(_pe);

		if (bo instanceof AbstractCamelModelElement) {
			AbstractCamelModelElement _ep = (AbstractCamelModelElement) bo;
			IFile contextFile = getContextFile();
			String fileName = contextFile.getName();
			String projectName = contextFile.getProject().getName();

			// now ask the user to define a condition using a language
			IBreakpoint bp = CamelDebugUtils.getBreakpointForSelection(_ep.getId(), fileName, projectName);
			return bp != null && bp instanceof CamelConditionalBreakpoint;
		}
		return false;
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		return isAvailable(context);
	}
}
