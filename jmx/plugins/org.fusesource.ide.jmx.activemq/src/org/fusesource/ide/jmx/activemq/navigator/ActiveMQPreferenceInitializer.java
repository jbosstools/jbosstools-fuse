/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.jmx.activemq.navigator;

import org.fusesource.ide.foundation.ui.config.TableConfiguration;
import org.fusesource.ide.foundation.ui.util.UIPreferencesInitialiserSupport;

public class ActiveMQPreferenceInitializer extends UIPreferencesInitialiserSupport {

	@Override
	protected void initiailzeTableConfigurations() {
		TableConfiguration table = createTableConfiguration(QueuesNode.class);
		table.column("Name").setWidth(300);
		table.column("Queue Size");
		table.column("Enqueue Count");
		table.column("Consumer Count");
		table.column("Producer Count");
		table.column("In Flight Count");

		table = createTableConfiguration(TopicsNode.class);
		table.column("Name").setWidth(300);
		table.column("Consumer Count");
		table.column("Producer Count");
		table.column("In Flight Count");
	}

}
