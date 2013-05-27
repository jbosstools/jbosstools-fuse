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
 
import java.util.Iterator;
import java.util.Map.Entry;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.parts.SelectionSynchronizer;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.fusesource.ide.camel.editor.Activator;
import org.fusesource.ide.camel.editor.editor.RiderDesignEditor;
import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.RouteContainer;
import org.fusesource.ide.camel.model.RouteSupport;

 
/**
 * @author lhein
 */
public class CamelContextSelectionSynchronizer extends SelectionSynchronizer {
     
    private RiderDesignEditor editor;
     
    /**
     * 
     * @param editor
     */
    public CamelContextSelectionSynchronizer(RiderDesignEditor editor) {
        super();
        this.editor = editor;
    }
     
    /* (non-Javadoc)
     * @see org.eclipse.gef.ui.parts.SelectionSynchronizer#convert(org.eclipse.gef.EditPartViewer, org.eclipse.gef.EditPart)
     */
    @Override
    protected EditPart convert(EditPartViewer viewer, EditPart part) {
         
        Object o = part.getModel();
        if (o instanceof RouteSupport) {
            RouteSupport newRoute = (RouteSupport)o;
            RouteSupport oldRoute = editor.getSelectedRoute();
            if (!oldRoute.equals(newRoute)) {
                // user selected a route which is not currently displayed in the diagram editor -> we need to load the route into the editor now first
                editor.switchRoute(newRoute);
            }
            // the route was selected - so we need to return the topmost editPart
            return viewer.getRootEditPart();
        } else if (o instanceof RouteContainer) {
            return viewer.getRootEditPart();
        } else if (o instanceof AbstractNode) {
            PictogramElement[] pes = editor.getFeatureProvider().getAllPictogramElementsForBusinessObject(o);
            if (pes.length>0) {
                AbstractNode node = (AbstractNode)o;
                RouteSupport oldRoute = editor.getSelectedRoute();
                RouteSupport newRoute = (RouteSupport)node.getParent();
                if (!oldRoute.equals(newRoute)) {
                    editor.switchRoute(newRoute);
                    editor.autoLayoutRoute();
                }
            }
            if (pes.length == 0) {
                // an element from another route was selected - we need to load the route into the diagram
                AbstractNode node = (AbstractNode)o;
                editor.switchRoute((RouteSupport)node.getParent());
                editor.getDiagramBehavior().refreshContent();
                Iterator<?> it = viewer.getEditPartRegistry().entrySet().iterator();
                while (it.hasNext()) {
                    Entry<?, ?> e = (Entry<?, ?>)it.next();
                    Object key = e.getKey();
                    EditPart value = (EditPart)e.getValue();
                    if (key instanceof ContainerShape) {
                        ContainerShape cs = (ContainerShape)key;
                        if (cs.getLink() != null &&
                            cs.getLink().getBusinessObjects() != null &&
                            cs.getLink().getBusinessObjects().size() > 0 &&
                            cs.getLink().getBusinessObjects().get(0) != null &&
                            cs.getLink().getBusinessObjects().get(0).equals(o)) {
                            // found the correct entry
                            return value;
                        }
                    }
                    continue;
                }
                // if all fails return the root edit part
                return viewer.getRootEditPart();
            }
            return editor.getDiagramBehavior().getEditPartForPictogramElement(pes[0]);
        } else if (o instanceof Diagram) {
            // someone selected the route inside the diagram editor
            RouteSupport route = editor.getSelectedRoute();
            return getEditPart(viewer, route);
        } else if (o instanceof ContainerShape) {
            // someone selected a diagram figure in the diagram editor
        	AbstractNode node = null;
        	node = (AbstractNode)Activator.getDiagramEditor().getFeatureProvider().getBusinessObjectForPictogramElement(((ContainerShape)o));
        	if (node != null) {
        		return getEditPart(viewer, node);
        	}
        }         
        return super.convert(viewer, part);
    }
     
    private EditPart getEditPart(EditPartViewer viewer, AbstractNode node) {
        Iterator<?> it = viewer.getEditPartRegistry().entrySet().iterator();
        while (it.hasNext()) {
            Entry<?, ?> e = (Entry<?, ?>)it.next();
            Object key = e.getKey();
            EditPart value = (EditPart)e.getValue();
            if (key instanceof AbstractNode) {
                AbstractNode n = (AbstractNode)key;
                if (n.equals(node)) {
                    // found the correct entry
                    return value;
                }
            }
            continue;
        }
        // if all fails return the root edit part
        return viewer.getRootEditPart();
    }
}