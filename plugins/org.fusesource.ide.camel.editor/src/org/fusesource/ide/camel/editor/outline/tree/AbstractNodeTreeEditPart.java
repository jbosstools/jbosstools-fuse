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

package org.fusesource.ide.camel.editor.outline.tree;
 
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.camel.util.ObjectHelper;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.editparts.AbstractTreeEditPart;
import org.eclipse.swt.graphics.Image;
import org.fusesource.ide.camel.editor.Activator;
import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.RouteSupport;

 
/**
 * @author lhein
 */
public class AbstractNodeTreeEditPart extends AbstractTreeEditPart implements PropertyChangeListener {
 
    protected static final List<AbstractNode> EMPTY_LIST = new ArrayList<AbstractNode>();
 
    protected static final Set<String> treeRelationshipPropertyNames = new HashSet<String>(Arrays.asList(
            RouteSupport.CHILD_ADDED_PROP, RouteSupport.CHILD_REMOVED_PROP, AbstractNode.SOURCE_CONNECTIONS,
            AbstractNode.TARGET_CONNECTIONS));
 
    /**
     * Finds the given node by walking the edit part tree looking for the
     * correct one
     */
    @SuppressWarnings("unchecked")
    public static AbstractNodeTreeEditPart findEditPart(AbstractNode node, EditPart part) {
        if (part instanceof AbstractNodeTreeEditPart) {
            AbstractNodeTreeEditPart nodeEditPart = (AbstractNodeTreeEditPart) part;
            AbstractNode modelNode = nodeEditPart.getModelNode();
            if (ObjectHelper.equal(node, modelNode)) {
                return nodeEditPart;
            }
        }
        List<EditPart> children = part.getChildren();
        for (EditPart childPart : children) {
            AbstractNodeTreeEditPart answer = findEditPart(node, childPart);
            if (answer != null) {
                return answer;
            }
        }
        return null;
    }
 
    private Image image;
 
//    /*
//     * (non-Javadoc)
//     * 
//     * @see org.eclipse.gef.editparts.AbstractTreeEditPart#createEditPolicies()
//     */
//    @Override
//    protected void createEditPolicies() {
//        installEditPolicy(EditPolicy.COMPONENT_ROLE, new AbstractNodeDeletePolicy());
//    }
 
    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.gef.editparts.AbstractEditPart#activate()
     */
    @Override
    public void activate() {
        super.activate();
        getModelNode().addPropertyChangeListener(this);
    }
 
    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.gef.editparts.AbstractEditPart#deactivate()
     */
    @Override
    public void deactivate() {
        getModelNode().removePropertyChangeListener(this);
        super.deactivate();
    }
 
    public AbstractNode getModelNode() {
        return (AbstractNode) getModel();
    }
 
    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.gef.editparts.AbstractTreeEditPart#refreshVisuals()
     */
    @Override
    public void refreshVisuals() {
        AbstractNode model = getModelNode();
        String displayText = model.getDisplayText();
        if (displayText == null) {
            Activator.getLogger().warning("No display text for " + model + " of type: " + model.getClass().getCanonicalName());
        } else {
            setWidgetText(displayText);
        }
        //setWidgetImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_DEF_VIEW));
        setWidgetImage(getImage());
    }
 
    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.gef.editparts.AbstractEditPart#getModelChildren()
     */
    @Override
    protected List<AbstractNode> getModelChildren() {
        AbstractNode node = (AbstractNode) getModel();
        return node.getOutputs();
    }
 
    /*
     * (non-Javadoc)
     * 
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.
     * PropertyChangeEvent)
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt != null) {
            String propertyName = evt.getPropertyName();
            if (getModelNode().isPropertyName(propertyName)) {
                refreshVisuals();
            } else {
                if (propertyName != null) {
                    List ch = getChildren();
                    if (ch == null) {
                        // ignore
                    } else if (isTreeRelationshipPropertyName(propertyName)) {
                        refreshChildren();
 
                        // the target node may now be the child of source in the
                        // outline view
                        // rather than being the child of the container, so lets
                        // fire an event to that the outline
                        // view updates
                        //
                        // lets get the container and refresh it just in case
                        EditPart parent = getParent();
                        while (parent != null) {
                            if (parent instanceof ContainerTreeEditPart) {
                                ((ContainerTreeEditPart) parent).refreshChildren();
                                break;
                            } else {
                                parent = parent.getParent();
                            }
                        }
                    } else {
                        //                      System.out.println("PROP: " + evt.getPropertyName());
                    }
                }
            }
        }
    }
 
    @Override
    public void setModel(Object model) {
        super.setModel(model);
        image = getModelNode().getSmallImage();
 
        // lets force an update of the image
        refreshVisuals();
    }
 
 
    @Override
    protected Image getImage() {
        AbstractNode node = getModelNode();
        if (image == null && node != null) {
            image = node.getSmallImage();
        }
        return image;
    }
 
    protected boolean isTreeRelationshipPropertyName(String propertyName) {
        return treeRelationshipPropertyNames.contains(propertyName);
    }
 
    public String getToolTip() {
        AbstractNode node = (AbstractNode) getModel();
        return node.getDisplayToolTip();
    }
}