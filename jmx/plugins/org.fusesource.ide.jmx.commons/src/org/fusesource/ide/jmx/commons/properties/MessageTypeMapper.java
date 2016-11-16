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

package org.fusesource.ide.jmx.commons.properties;

import org.eclipse.ui.views.properties.tabbed.ITypeMapper;
import org.fusesource.ide.jmx.commons.messages.Exchanges;
import org.fusesource.ide.jmx.commons.messages.IExchange;

public class MessageTypeMapper implements ITypeMapper {

	@Override
	public Class<?> mapType(Object object) {
		if (Exchanges.asExchange(object) != null) {
			return IExchange.class;
		}
		return null;
	}

}
