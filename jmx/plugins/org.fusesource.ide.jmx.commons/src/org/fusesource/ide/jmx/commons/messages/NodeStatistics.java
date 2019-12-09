/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.jmx.commons.messages;

public class NodeStatistics extends InvocationStatistics implements INodeStatistics {
	/* (non-Javadoc)
	 * @see org.fusesource.fon.util.messages.INodeStatistics#addExchange(org.fusesource.fon.util.messages.IExchange)
	 */
	@Override
	public void addExchange(IExchange exchange) {
		Long delta = null;
		IMessage in = exchange.getIn();
		if (in != null) {
			delta = in.getElapsedTime();
		}
		increment(delta);
	}
}
