package org.fusesource.fon.util.messages;

import java.util.List;

/**
 * Supports browsing of exchanges or messages such as endpoints or routes with tracing
 *
 */
public interface IExchangeBrowser {

	public List<IExchange> browseExchanges();
	

}
