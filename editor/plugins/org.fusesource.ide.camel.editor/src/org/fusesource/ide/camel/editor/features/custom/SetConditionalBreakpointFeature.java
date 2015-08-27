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
package org.fusesource.ide.camel.editor.features.custom;

import java.util.UUID;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.platform.IDiagramContainer;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.fusesource.ide.camel.editor.Activator;
import org.fusesource.ide.camel.editor.editor.ConditionalBreakpointEditorDialog;
import org.fusesource.ide.camel.editor.editor.RiderDesignEditor;
import org.fusesource.ide.camel.editor.provider.ImageProvider;
import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.RouteContainer;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.launcher.debug.util.CamelDebugUtils;
import org.fusesource.ide.launcher.debug.util.ICamelDebugConstants;

/**
 * @author lhein
 */
public class SetConditionalBreakpointFeature extends SetEndpointBreakpointFeature {
	
	/**
	 * creates the feature
	 * 
	 * @param fp
	 */
	public SetConditionalBreakpointFeature(IFeatureProvider fp) {
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
       
        if (bo instanceof AbstractNode) {
        	AbstractNode _ep = (AbstractNode) bo;
            try {
            	Boolean userWantsUpdate = null;
            	IFile contextFile = getContextFile();
            	String fileName = contextFile.getName();
            	String projectName = contextFile.getProject().getName();
            	
            	ConditionalBreakpointEditorDialog dlg = new ConditionalBreakpointEditorDialog(Display.getDefault().getActiveShell(), _ep);
        		dlg.setBlockOnOpen(true);
        		if (Window.OK == dlg.open()) {
        			String language = dlg.getLanguage();
        			String condition = dlg.getCondition();
        		
	            	if (Strings.isBlank(_ep.getCamelContextId()) ||
	            		Strings.isBlank(_ep.getId()) ) {
	            		// important ID fields are not yet set - ask the user if we
	            		// can update those fields for him
	            		userWantsUpdate = askForIDUpdate(_ep);

	            		if (userWantsUpdate) {
	            			// update the context id if needed
	            			if (Strings.isBlank(_ep.getCamelContextId())) {
	            				String newContextId = ICamelDebugConstants.PREFIX_CONTEXT_ID + UUID.randomUUID().toString();
	            				((RouteContainer)_ep.getParent().getParent()).setContextId(newContextId);
	            			}
	            			
	            			// update the node id if blank
	            			boolean foundUniqueId = false;
	            			if (Strings.isBlank(_ep.getId())) {
	            				String newNodeId = null;
	            				while (!foundUniqueId) {
	            					newNodeId = ICamelDebugConstants.PREFIX_NODE_ID + _ep.getNewID();
	            					// we need to check if the id is really unique in our context
	            					if (((RiderDesignEditor)getDiagramBehavior().getDiagramContainer()).getModel().getNode(newNodeId) == null) {
	            						foundUniqueId = true;
	            					}
	            				}
	            				if (Strings.isBlank(newNodeId) == false) {
	            					_ep.setId(newNodeId);
	            				} else {
	            					throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Unable to determine a unique ID for node " + _ep));
	            				}
	            			}
	            			
	            			// then do a save
	            			saveEditor();
                		}
            		}
	            	if (userWantsUpdate == null || userWantsUpdate == true) {
		            	// finally create the endpoint
		    			CamelDebugUtils.createAndRegisterConditionalBreakpoint(resource, _ep, projectName, fileName, language, condition);  
	            	}
        		}
            } catch (CoreException e) {
                final IDiagramContainer container = getDiagramBehavior().getDiagramContainer();
                final Shell shell;
                if (container instanceof RiderDesignEditor) {
                    shell = ((RiderDesignEditor) container).getEditorSite().getShell();
                } else {
                    shell = Display.getCurrent().getActiveShell();
                }
                MessageDialog.openError(shell, "Error on adding breakpoint", e.getStatus().getMessage());
                return;
            }
        }
        getDiagramBehavior().refreshRenderingDecorators(_pe);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.impl.AbstractFeature#getName()
	 */
	@Override
	public String getName() {
		return "Set Conditional Breakpoint";
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Sets a conditional breakpoint on the selected endpoint node";
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#getImageId()
	 */
	@Override
	public String getImageId() {
		return ImageProvider.IMG_YELLOWDOT;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#isAvailable(org.eclipse.graphiti.features.context.IContext)
	 */
	@Override
	public boolean isAvailable(IContext context) {
		ICustomContext cc = (ICustomContext) context;
		PictogramElement _pe = cc.getPictogramElements()[0] instanceof Connection ? ((Connection) cc.getPictogramElements()[0])
                .getStart().getParent() : cc.getPictogramElements()[0];
        final Object bo = getBusinessObjectForPictogramElement(_pe);
       
        if (bo instanceof AbstractNode) {
        	AbstractNode _ep = (AbstractNode) bo;
        	IFile contextFile = getContextFile();
        	String fileName = contextFile.getName();
        	String projectName = contextFile.getProject().getName();
            return _ep.supportsBreakpoint() && CamelDebugUtils.getBreakpointForSelection(_ep.getId(), fileName, projectName) == null;
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
}
