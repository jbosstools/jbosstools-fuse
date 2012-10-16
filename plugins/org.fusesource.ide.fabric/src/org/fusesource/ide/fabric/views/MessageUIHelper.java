package org.fusesource.ide.fabric.views;

import org.fusesource.fon.util.messages.Exchanges;
import org.fusesource.fon.util.messages.IExchange;
import org.fusesource.ide.commons.util.Strings;
import org.fusesource.ide.commons.util.XmlUtilities;



public class MessageUIHelper {
	

	public static String getBody(IExchange selectedExchange) {
		String body = Strings.getOrElse(Exchanges.getBody(selectedExchange), "");
		
		// lets XML unescape it...
		return XmlUtilities.unescape(body);
	}		
}
