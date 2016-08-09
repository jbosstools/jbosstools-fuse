/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.camel.editor.utils;

import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.gef.editparts.AbstractEditPart;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.internal.parts.ContainerShapeEditPart;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.fusesource.ide.camel.model.service.core.catalog.CamelModel;
import org.fusesource.ide.camel.model.service.core.catalog.CamelModelFactory;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelRouteElement;
import org.fusesource.ide.foundation.ui.tree.HasOwner;

/**
 * @author lhein
 */
public class NodeUtils {

	public static String getPropertyName(final Object id) {
		String propertyName = id.toString();
		int idx = propertyName.indexOf('.');
		if (idx > 0) {
			propertyName = propertyName.substring(idx + 1);
		}
		propertyName = propertyName.substring(0, 1).toLowerCase() + propertyName.substring(1);
		return propertyName;
	}

	public static boolean isMandatory(Object bean, String propertyName) {
		// lets look at the setter method and see if its got a @Required
		// annotation
		if (bean instanceof AbstractCamelModelElement) {
			AbstractCamelModelElement node = (AbstractCamelModelElement) bean;
			Eip eip = node.getUnderlyingMetaModelObject();
			Parameter p = eip.getParameter(propertyName);
			if (p != null) {
				return p.getRequired().equals("true");
			}
		}
		return false;
	}
	
	/**
	 * checks if the given parent can host the given child element
	 * 
	 * @param parent	the parent / container element
	 * @param child		the child element
	 * @return			true if the child is valid for the given container, otherwise false
	 */
	public static boolean isValidChild(AbstractCamelModelElement parent, AbstractCamelModelElement child) {
		if (parent != null && child != null) {
    		String camelVersion = CamelUtils.getCurrentProjectCamelVersion();
    		CamelModel metaModel = CamelModelFactory.getModelForVersion(camelVersion);
    		
			if (parent.getNodeTypeId().equalsIgnoreCase("choice")) {
				// special case for choice
				return 	 child.getNodeTypeId().equalsIgnoreCase("when") || 
						(child.getNodeTypeId().equalsIgnoreCase("otherwise") && (parent.getParameter("otherwise") == null || parent.getParameter("otherwise") == child));
			} else {
				Eip containerEip = parent.getUnderlyingMetaModelObject();
	        	if (containerEip == null) {
	        		containerEip = metaModel.getEipModel().getEIPByName(parent.getNodeTypeId());
	        	}
	        	return containerEip != null && containerEip.canHaveChildren() && containerEip.getAllowedChildrenNodeTypes().contains(child.getNodeTypeId());
			}
		}
		return false;
	}
	
	/**
	 * checks if the given parent can host the given child eip
	 * 
	 * @param parent	the parent / container element
	 * @param child		the child element
	 * @return			true if the child is valid for the given container, otherwise false
	 */
	public static boolean isValidChild(AbstractCamelModelElement parent, Eip child) {
		return 	parent.getUnderlyingMetaModelObject().canHaveChildren() && 
				(parent.getUnderlyingMetaModelObject().getAllowedChildrenNodeTypes().contains(child.getName()) || 
						(child.getName().equalsIgnoreCase("otherwise") && parent.getUnderlyingMetaModelObject().getName().equalsIgnoreCase("choice")));
	}
	
	/**
	 * collects all container figures recursively
	 * 
	 * @param fp
	 * @param context
	 * @param pes
	 */
	public static void getAllContainers(IFeatureProvider fp, AbstractCamelModelElement context, List<PictogramElement> pes) {
		if (context instanceof CamelRouteElement) {
			pes.add(fp.getDiagramTypeProvider().getFeatureProvider().getPictogramElementForBusinessObject(context));
		}
    	for (AbstractCamelModelElement cme : context.getChildElements()) {
			if (cme.getUnderlyingMetaModelObject() != null && cme.getUnderlyingMetaModelObject().canHaveChildren()) {
				pes.add(fp.getDiagramTypeProvider().getFeatureProvider().getPictogramElementForBusinessObject(cme));
			}
			if (cme.getChildElements().size() > 0) {
				getAllContainers(fp, cme, pes);
			}
		}
	}

    public static AbstractCamelModelElement toCamelElement(Object input) {
		AbstractCamelModelElement answer = null;
		if (input instanceof AbstractCamelModelElement) {
			return (AbstractCamelModelElement) input;
// TODO: check what AbstractNodeFacade is about and how to migrate if needed
//		} else if (input instanceof AbstractNodeFacade) {
//			AbstractNodeFacade facade = (AbstractNodeFacade) input;
//			answer = facade.getAbstractNode();
		} else if (input instanceof ContainerShapeEditPart) {
			ContainerShapeEditPart editPart = (ContainerShapeEditPart) input;
			PictogramElement element = editPart.getPictogramElement();
			if (CamelUtils.getDiagramEditor() != null) {
				if (element != null && element instanceof Diagram) {
					// route selected - this makes properties view work when route is
					// selected in the diagram view
					answer = CamelUtils.getDiagramEditor().getSelectedContainer() != null ? CamelUtils.getDiagramEditor().getSelectedContainer() : CamelUtils.getDiagramEditor().getModel();				
				} else {
					// select the node
					answer = (AbstractCamelModelElement)CamelUtils.getDiagramEditor().getFeatureProvider().getBusinessObjectForPictogramElement(element);
				}
			}
		} else if (input instanceof AbstractEditPart) {
			AbstractEditPart editPart = (AbstractEditPart) input;
			Object model = editPart.getModel();
			answer = toCamelElement(model);
		} else if (input instanceof ContainerShape) {
			ContainerShape shape = (ContainerShape) input;
			answer = (AbstractCamelModelElement)CamelUtils.getDiagramEditor().getFeatureProvider().getBusinessObjectForPictogramElement(shape);
		}
		if (input != null && answer == null) {
			answer = Platform.getAdapterManager().getAdapter(input, AbstractCamelModelElement.class);
		}
		if (answer == null && input instanceof HasOwner) {
			HasOwner ho = (HasOwner) input;
			answer = toCamelElement(ho.getOwner());
		}
		return answer;
	}
    
    public static AbstractCamelModelElement getSelectedNode(ISelection selection) {
    	AbstractCamelModelElement answer = null;
		if (selection instanceof IStructuredSelection) {
			Object input = ((IStructuredSelection) selection).getFirstElement();
			answer = toCamelElement(input);
		}
		return answer;
	}
    
    public static void setSelectedNode(final AbstractCamelModelElement node, final IFeatureProvider fp) {
    	// select the new node
		Display.getCurrent().asyncExec(new Runnable() {
			@Override
			public void run() {
				PictogramElement pe = fp.getPictogramElementForBusinessObject(node);
		        fp.getDiagramTypeProvider().getDiagramBehavior().getDiagramContainer().selectPictogramElements(new PictogramElement[] {pe});		
			}
		});
    }
    
    /**
	 * Returns the EClass belonging to the anchor, or null if not available.
	 */
	public static AbstractCamelModelElement getNode(IFeatureProvider fp, Anchor anchor) {
		if (anchor != null) {
			Object obj = fp.getBusinessObjectForPictogramElement(anchor.getParent());
			if (obj instanceof AbstractCamelModelElement) {
				return (AbstractCamelModelElement) obj;
			}
		}
		return null;
	}
}
