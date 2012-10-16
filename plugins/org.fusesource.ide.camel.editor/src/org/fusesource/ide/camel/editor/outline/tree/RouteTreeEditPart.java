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