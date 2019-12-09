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

import java.util.UUID;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.platform.IDiagramContainer;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.fusesource.ide.camel.editor.CamelDesignEditor;
import org.fusesource.ide.camel.editor.internal.CamelEditorUIActivator;
import org.fusesource.ide.camel.editor.provider.ImageProvider;
import org.fusesource.ide.camel.editor.utils.CamelUtils;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.launcher.debug.util.CamelDebugUtils;
import org.fusesource.ide.launcher.debug.util.ICamelDebugConstants;

/**
 * @author lhein
 */
public class SetEndpointBreakpointFeature extends AbstractCustomFeature {

	/**
	 * creates the feature
	 * 
	 * @param fp
	 */
	public SetEndpointBreakpointFeature(IFeatureProvider fp) {
		super(fp);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.custom.ICustomFeature#execute(org.eclipse.graphiti.features.context.ICustomContext)
	 */
	@Override
	public void execute(ICustomContext context) {
		PictogramElement _pe = context.getPictogramElements()[0] instanceof Connection ? ((Connection) context.getPictogramElements()[0])
				.getStart().getParent() : context.getPictogramElements()[0];
				final Object bo = getBusinessObjectForPictogramElement(_pe);
				final IResource resource = getResource();

				if (bo instanceof AbstractCamelModelElement) {
					AbstractCamelModelElement _ep = (AbstractCamelModelElement) bo;
					try {
						Boolean userWantsUpdate = null;
						IFile contextFile = getContextFile();
						String fileName = contextFile.getName();
						String projectName = contextFile.getProject().getName();
						if (Strings.isBlank(_ep.getRouteContainer().getId()) ||
								Strings.isBlank(_ep.getId()) ) {
							// important ID fields are not yet set - ask the user if we
							// can update those fields for him
							userWantsUpdate = askForIDUpdate(_ep);
							if (userWantsUpdate) {
								// update the context id if needed
								if (Strings.isBlank(_ep.getRouteContainer().getId())) {
									String newContextId = ICamelDebugConstants.PREFIX_CONTEXT_ID + UUID.randomUUID().toString();
									_ep.getRouteContainer().setId(newContextId);
								}

								// update the node id if blank
								boolean foundUniqueId = false;
								if (Strings.isBlank(_ep.getId())) {
									String newNodeId = null;
									while (!foundUniqueId) {
										newNodeId = _ep.getNewID();
										// we need to check if the id is really unique in our context
										if (((CamelDesignEditor)getDiagramBehavior().getDiagramContainer()).getModel().findNode(newNodeId) == null) {
											foundUniqueId = true;
										}
									}
									if (Strings.isBlank(newNodeId) == false) {
										_ep.setId(newNodeId);
									} else {
										throw new CoreException(new Status(IStatus.ERROR, CamelEditorUIActivator.PLUGIN_ID, "Unable to determine a unique ID for node " + _ep));
									}
								}
							}
						}
						if (userWantsUpdate == null || userWantsUpdate == true) {
							// finally create the endpoint
							CamelDebugUtils.createAndRegisterEndpointBreakpoint(resource, _ep, projectName, fileName);  
						}
					} catch (CoreException e) {
						final IDiagramContainer container = getDiagramBehavior().getDiagramContainer();
						final Shell shell;
						if (container instanceof CamelDesignEditor) {
							shell = ((CamelDesignEditor) container).getEditorSite().getShell();
						} else {
							shell = Display.getCurrent().getActiveShell();
						}
						MessageDialog.openError(shell, "Error on adding breakpoint", e.getStatus().getMessage());
						return;
					}
				}
				getDiagramBehavior().refresh();
				getDiagramBehavior().refreshRenderingDecorators(_pe);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.impl.AbstractFeature#getName()
	 */
	@Override
	public String getName() {
		return "Set Breakpoint";
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Sets a breakpoint on the selected endpoint node";
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#getImageId()
	 */
	@Override
	public String getImageId() {
		return ImageProvider.IMG_REDDOT;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#isAvailable(org.eclipse.graphiti.features.context.IContext)
	 */
	@Override
	public boolean isAvailable(IContext context) {
		ICustomContext cc = (ICustomContext) context;
		PictogramElement _pe = getPEFromContext(cc);
		final Object bo = getBusinessObjectForPictogramElement(_pe);

		if (bo instanceof AbstractCamelModelElement) {
			AbstractCamelModelElement ep = (AbstractCamelModelElement) bo;
			IResource contextFile = ep.getCamelFile().getResource();
			String fileName = contextFile.getName();
			String projectName = contextFile.getProject().getName();
			return ep.supportsBreakpoint() && CamelDebugUtils.getBreakpointForSelection(ep.getId(), fileName, projectName) == null;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#canExecute(org.eclipse.graphiti.features.context.ICustomContext)
	 */
	@Override
	public boolean canExecute(ICustomContext context) {
		return isAvailable(context);
	}

	protected IResource getResource() {
		final IDiagramContainer container = getDiagramBehavior().getDiagramContainer();
		if (container instanceof CamelDesignEditor) {
			return ((CamelDesignEditor) container).getModel().getResource();
		}
		return null;
	}

	protected PictogramElement getPEFromContext(ICustomContext context) {
		return context.getPictogramElements()[0] instanceof Connection ?
				((Connection) context.getPictogramElements()[0]).getStart().getParent()
				: context.getPictogramElements()[0];
	}

	protected IFile getContextFile() {
		CamelDesignEditor diagramEditor = CamelUtils.getDiagramEditor(getFeatureProvider().getDiagramTypeProvider());
		return diagramEditor.asFileEditorInput(diagramEditor.getEditorInput()).getFile();
	}

	/**
	 * ask the user if we can update some id fields
	 * 
	 * @param node
	 * @return
	 */
	protected boolean askForIDUpdate(AbstractCamelModelElement node) {
		final IDiagramContainer container = getDiagramBehavior().getDiagramContainer();
		final Shell shell;
		if (container instanceof CamelDesignEditor) {
			shell = ((CamelDesignEditor) container).getEditorSite().getShell();
		} else {
			shell = Display.getCurrent().getActiveShell();
		}
		return MessageDialog.openConfirm(shell, "Please confirm...", "Debugging is only possible if the context and the nodes which have breakpoints have a unique ID set. Do you want to generate the ID values now?\n\n(Warning: All changes in the editor will be written to the context file!)");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.impl.AbstractFeature#hasDoneChanges()
	 */
	@Override
	public boolean hasDoneChanges() {
		return false;
	}

	protected void saveEditor() throws CoreException {
		final IDiagramContainer container = getDiagramBehavior().getDiagramContainer();
		if (container instanceof CamelDesignEditor) {
			CamelDesignEditor editor = (CamelDesignEditor) container;
			editor.getParent().doSave(new NullProgressMonitor());
		} else {
			throw new CoreException(new Status(IStatus.ERROR, CamelEditorUIActivator.PLUGIN_ID, "Can't find the editor to set the breakpoint!"));
		}
	}

}
