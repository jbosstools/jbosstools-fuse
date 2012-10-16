package org.fusesource.ide.fabric.propertysheet;

import org.eclipse.ui.views.properties.tabbed.ITypeMapper;
import org.fusesource.fon.util.messages.Exchanges;
import org.fusesource.fon.util.messages.IExchange;

public class MessageTypeMapper implements ITypeMapper {

	@Override
	public Class mapType(Object object) {
		if (Exchanges.asExchange(object) != null) {
			return IExchange.class;
		}
		return null;
	}

}
