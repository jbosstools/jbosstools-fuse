/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.camel.editor.features.custom;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.fusesource.ide.camel.editor.CamelDesignEditor;
import org.fusesource.ide.camel.editor.provider.ImageProvider;
import org.fusesource.ide.camel.editor.utils.CamelUtils;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelContextElement;
import org.fusesource.ide.camel.model.service.core.model.CamelRouteElement;

/**
 * @author lhein
 */
public class GoIntoContainerFeature extends AbstractCustomFeature {

	private PictogramElement lastPE;
	
	/**
	 * 
	 * @param fp
	 */
	public GoIntoContainerFeature(IFeatureProvider fp) {
		super(fp);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#isAvailable(org.eclipse.graphiti.features.context.IContext)
	 */
	@Override
	public boolean isAvailable(IContext context) {
		if (context instanceof ICustomContext) {
			this.lastPE = ((ICustomContext)context).getPictogramElements()[0];
		}
		return super.isAvailable(context);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#canExecute(org.eclipse.graphiti.features.context.ICustomContext)
	 */
	@Override
	public boolean canExecute(ICustomContext context) {
		boolean ret = false;
		PictogramElement[] pes = context.getPictogramElements();
		if (pes != null && pes.length == 1) {
			Object bo = getBusinessObjectForPictogramElement(pes[0]);
			if (bo instanceof AbstractCamelModelElement) {
				AbstractCamelModelElement cme = (AbstractCamelModelElement)bo;
				return isAllowedToExecute(cme);
			}
		}
		return ret;
	}
	
	private boolean isAllowedToExecute(AbstractCamelModelElement cme) {
		CamelDesignEditor editor = (CamelDesignEditor)getDiagramBehavior().getDiagramContainer();
		return 	editor != null && 
				(isGoInto(cme, editor) || isGoUp(cme, editor)); 
	}
	
	private boolean isGoUp(AbstractCamelModelElement cme, CamelDesignEditor editor) {
		// go up is allowed if:
		// - selected container is a route
		// - selected container is the selected element
		return cme instanceof CamelRouteElement && cme.equals(editor.getSelectedContainer());
	}
	
	private boolean isGoInto(AbstractCamelModelElement cme, CamelDesignEditor editor) {
		// go into is allowed if:
		// - selected element is a route
		// - selected container is the camel context
		// - context contains more than one route
		return 	AbstractCamelModelElement.ROUTE_NODE_NAME.equalsIgnoreCase(cme.getNodeTypeId()) && 
				editor.getSelectedContainer() instanceof CamelContextElement &&
				cme.getRouteContainer().getChildElements().size() > 1;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.custom.ICustomFeature#execute(org.eclipse.graphiti.features.context.ICustomContext)
	 */
	@Override
	public void execute(ICustomContext context) {
		PictogramElement[] pes = context.getPictogramElements();
		if (pes != null && pes.length == 1) {
			Object bo = getBusinessObjectForPictogramElement(pes[0]);
	 	   	if(bo instanceof AbstractCamelModelElement) {
	 	   		AbstractCamelModelElement cme = (AbstractCamelModelElement)bo;
	 	   		CamelDesignEditor editor = CamelUtils.getDiagramEditor();
	 	   		if (isGoInto(cme, editor)) {
	 	   			editor.setSelectedContainer(cme);	
	 	   		} else {
	 	   			editor.setSelectedContainer(cme.getRouteContainer());
	 	   		}
	 	   		
	 	   	}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.impl.AbstractFeature#getName()
	 */
	@Override
	public String getName() {
		if (lastPE != null) {
			Object bo = getBusinessObjectForPictogramElement(lastPE);
			if (bo instanceof AbstractCamelModelElement) {
				AbstractCamelModelElement cme = (AbstractCamelModelElement)bo;
				if (!isGoInto(cme, CamelUtils.getDiagramEditor())) {
					return "Show Camel Context";
				} 				 
			}
		}
		return "Go Into";
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#getDescription()
	 */
	@Override
	public String getDescription() {
		if (lastPE != null) {
			Object bo = getBusinessObjectForPictogramElement(lastPE);
			if (bo instanceof AbstractCamelModelElement) {
				AbstractCamelModelElement cme = (AbstractCamelModelElement)bo;
				if (!isGoInto(cme, CamelUtils.getDiagramEditor())) {
					return "Show the whole Camel Context";
				} 				 
			}
		}
		return "Drills into the selected container node...";
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#getImageId()
	 */
	@Override
	public String getImageId() {
		if (lastPE != null) {
			Object bo = getBusinessObjectForPictogramElement(lastPE);
			if (bo instanceof AbstractCamelModelElement) {
				AbstractCamelModelElement cme = (AbstractCamelModelElement)bo;
				if (!isGoInto(cme, CamelUtils.getDiagramEditor())) {
					return ImageProvider.IMG_UP_NAV;
				} 				 
			}
		}
		return ImageProvider.IMG_OUTLINE_TREE;
	}
}
