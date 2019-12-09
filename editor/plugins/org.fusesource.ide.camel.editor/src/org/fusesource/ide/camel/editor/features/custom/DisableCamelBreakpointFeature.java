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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.platform.IDiagramContainer;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.fusesource.ide.camel.editor.CamelDesignEditor;
import org.fusesource.ide.camel.editor.internal.CamelEditorUIActivator;
import org.fusesource.ide.camel.editor.provider.ImageProvider;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.launcher.debug.util.CamelDebugUtils;

/**
 * @author lhein
 */
public class DisableCamelBreakpointFeature extends SetEndpointBreakpointFeature {

	/**
	 * creates the feature
	 * 
	 * @param fp
	 */
	public DisableCamelBreakpointFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public void execute(ICustomContext context) {
		PictogramElement _pe = getPEFromContext(context);
		final Object bo = getBusinessObjectForPictogramElement(_pe);

		if (bo instanceof AbstractCamelModelElement) {
			AbstractCamelModelElement _ep = (AbstractCamelModelElement) bo;
			try {
				IFile contextFile = getContextFile();
				String fileName = contextFile.getName();
				String projectName = contextFile.getProject().getName();

				IBreakpoint bp = CamelDebugUtils.getBreakpointForSelection(_ep.getId(), fileName, projectName);
				if (bp != null && bp.isEnabled()) {
					bp.setEnabled(false);
				}
			} catch (CoreException e) {
				final IDiagramContainer container = getDiagramBehavior().getDiagramContainer();
				final Shell shell;
				if (container instanceof CamelDesignEditor) {
					shell = ((CamelDesignEditor) container).getEditorSite().getShell();
				} else {
					shell = Display.getCurrent().getActiveShell();
				}
				MessageDialog.openError(shell, "Error on enabling breakpoint", e.getStatus().getMessage());
				return;
			}
		}
		getDiagramBehavior().refreshRenderingDecorators(_pe);
	}

	@Override
	public String getName() {
		return "Disable Breakpoint";
	}

	@Override
	public String getDescription() {
		return "Disables the breakpoint on the selected endpoint node";
	}

	@Override
	public String getImageId() {
		return ImageProvider.IMG_GRAYDOT;
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
			if (_ep.supportsBreakpoint()) {
				IBreakpoint bp = CamelDebugUtils.getBreakpointForSelection(_ep.getId(), fileName, projectName);
				try {
					return bp != null && bp.isEnabled();
				} catch (CoreException ex) {
					CamelEditorUIActivator.pluginLog().logError(ex);
				}
			}
		}
		return false;
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		return isAvailable(context);
	}

	@Override
	public boolean hasDoneChanges() {
		return false;
	}
}
