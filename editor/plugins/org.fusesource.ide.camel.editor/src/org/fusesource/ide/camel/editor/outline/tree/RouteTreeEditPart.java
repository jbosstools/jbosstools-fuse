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
import org.fusesource.ide.camel.model.RouteSupport;
 
 
/**
 * @author lhein
 */
public class RouteTreeEditPart extends ContainerTreeEditPart {
 
    /*
     * (non-Javadoc)
     * @see org.eclipse.gef.editparts.AbstractEditPart#getModelChildren()
     */
    @Override
    protected List<AbstractNode> getModelChildren() {
        RouteSupport container = (RouteSupport)getModel();
        return container.getRootNodes();
    }
}