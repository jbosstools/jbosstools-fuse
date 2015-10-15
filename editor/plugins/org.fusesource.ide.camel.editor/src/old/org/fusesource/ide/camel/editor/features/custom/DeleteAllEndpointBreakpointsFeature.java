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

package old.org.fusesource.ide.camel.editor.features.custom;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.fusesource.ide.camel.editor.Activator;
import org.fusesource.ide.camel.editor.editor.RiderDesignEditor;
import org.fusesource.ide.camel.editor.provider.ImageProvider;
import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.RouteSupport;
import org.fusesource.ide.launcher.debug.model.CamelEndpointBreakpoint;
import org.fusesource.ide.launcher.debug.util.CamelDebugUtils;

/**
 * @author lhein
 */
public class DeleteAllEndpointBreakpointsFeature extends DeleteEndpointBreakpointFeature {

	/**
     * Create a new DeleteAllEndpointBreakpointsFeature.
     * 
     * @param fp the feature provider
     * @param context the context
     */
    public DeleteAllEndpointBreakpointsFeature(IFeatureProvider fp) {
        super(fp);
    }
    
    /*
     * (non-Javadoc)
     * @see org.fusesource.ide.camel.editor.features.custom.DeleteEndpointBreakpointFeature#execute(org.eclipse.graphiti.features.context.ICustomContext)
     */
    @Override
    public void execute(ICustomContext context) {
    	IFile contextFile = getContextFile();
    	String fileName = contextFile.getName();
    	String projectName = contextFile.getProject().getName();
    	IBreakpoint[] bps = CamelDebugUtils.getBreakpointsForContext(fileName, projectName);
		for (IBreakpoint bp : bps) {
			AbstractNode bo = ((RiderDesignEditor)getDiagramBehavior().getDiagramContainer()).getModel().getNode(((CamelEndpointBreakpoint)bp).getEndpointNodeId());
			try {
				bp.delete();
			} catch (CoreException ex) {
				Activator.getLogger().error("Unable to delete breakpoint " + bp, ex);
			} finally {
				// update the pictogram element
				PictogramElement[] pes = getFeatureProvider().getAllPictogramElementsForBusinessObject(bo);
				for (PictogramElement pe : pes) getDiagramBehavior().refreshRenderingDecorators(pe);
			}
        }
    }
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.impl.AbstractFeature#getName()
	 */
	@Override
	public String getName() {
		return "Delete all breakpoints";
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Deletes all breakpoints in the selected context";
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#getImageId()
	 */
	@Override
	public String getImageId() {
		return ImageProvider.IMG_GRAYDOT;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#isAvailable(org.eclipse.graphiti.features.context.IContext)
	 */
	@Override
	public boolean isAvailable(IContext context) {
		IFile contextFile = getContextFile();
    	String fileName = contextFile.getName();
    	String projectName = contextFile.getProject().getName();
    	return CamelDebugUtils.getBreakpointsForContext(fileName, projectName).length>0 && isRouteSelected(context);
	}
	
	private boolean isRouteSelected(IContext context) {
		ICustomContext cc = (ICustomContext) context;
		PictogramElement _pe = cc.getPictogramElements()[0] instanceof Connection ? ((Connection) cc.getPictogramElements()[0])
                .getStart().getParent() : cc.getPictogramElements()[0];
        final Object bo = getBusinessObjectForPictogramElement(_pe);
       
        return bo == null || bo instanceof RouteSupport;
	}
}
