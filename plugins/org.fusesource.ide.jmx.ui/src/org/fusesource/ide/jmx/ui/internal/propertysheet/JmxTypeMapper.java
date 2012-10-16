package org.fusesource.ide.jmx.ui.internal.propertysheet;


import org.eclipse.ui.views.properties.tabbed.ITypeMapper;
import org.fusesource.ide.jmx.core.tree.ObjectNameNode;
import org.fusesource.ide.jmx.ui.internal.editors.MBeanEditor;


public class JmxTypeMapper implements ITypeMapper {

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.tabbed.ITypeMapper#mapType(java.lang.Object)
	 */
	public Class mapType(Object object) {
		if (object instanceof MBeanEditor) {
			return ObjectNameNode.class;
		} else {
			return object.getClass();
		}
	}
}
