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
