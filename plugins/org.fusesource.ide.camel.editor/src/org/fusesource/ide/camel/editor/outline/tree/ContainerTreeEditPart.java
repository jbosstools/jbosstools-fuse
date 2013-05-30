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
 
import java.util.List;

import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.RouteContainer;

 
/**
 * @author lhein
 */
public class ContainerTreeEditPart extends AbstractNodeTreeEditPart {
     
    /*
     * (non-Javadoc)
     * @see org.eclipse.gef.editparts.AbstractEditPart#getModelChildren()
     */
    @Override
    protected List<AbstractNode> getModelChildren() {
        RouteContainer container = (RouteContainer)getModel();
        return container.getSourceNodes();
    }
 
    /*
     * (non-Javadoc)
     * @see org.eclipse.gef.editparts.AbstractTreeEditPart#createEditPolicies()
     */
//    @Override 
//    protected void createEditPolicies() {
//        installEditPolicy(EditPolicy.COMPONENT_ROLE, new AbstractNodeDeletePolicy());
//    }
 
//  /*
//   * (non-Javadoc)
//   * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
//   */
//  @Override 
//  public void propertyChange(PropertyChangeEvent evt) {
//      super.propertyChange(evt);
//      
//      if (evt != null) {
//          String propertyName = evt.getPropertyName();
//          if (propertyName != null) {
//              List ch = getChildren();
//              if (ch == null) {
//                  // ignore
//              } else if (isTreeRelationshipPropertyName(propertyName)) {
//                  refreshChildren();
//              }
//          }
//      }
//  }
}