package org.fusesource.ide.camel.editor.propertysheet;

import org.eclipse.ui.views.properties.tabbed.ITypeMapper;
import org.fusesource.ide.camel.editor.AbstractNodes;
import org.fusesource.ide.camel.model.AbstractNode;


public class RiderTypeMapper implements ITypeMapper {

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.tabbed.ITypeMapper#mapType(java.lang.Object)
	 */
	@Override
	public Class mapType(Object object) {
		AbstractNode node = AbstractNodes.toAbstractNode(object);
		if (node != null) {
			return AbstractNode.class;
		}
		return object.getClass();
	}
}
