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

package org.fusesource.ide.jmx.camel.navigator;

import org.fusesource.ide.camel.model.service.core.jmx.camel.CamelProcessorMBean;
import org.fusesource.ide.foundation.ui.config.TableConfiguration;
import org.fusesource.ide.foundation.ui.util.UIPreferencesInitialiserSupport;


public class CamelPreferenceInitializer extends UIPreferencesInitialiserSupport {
	@Override
	protected void initiailzeTableConfigurations() {
		TableConfiguration table = createTableConfiguration(RoutesNode.class);
		table.column("Route Id").setLabelProviderStyle("image");
		table.column("State");
		table.column("Tracing");
		table.column("Exchanges Total");
		table.column("Exchanges Failed");
		table.column("Exchanges Completed");
		table.column("Total Processing Time");
		table.column("Endpoint Uri").setWidth(400);
		table.column("Camel Id").setVisible(false);
		table.column("Description").setVisible(false);

		table = createTableConfiguration(EndpointsNode.class);
		table.column("Endpoint Uri").setWidth(400);

		table = createTableConfiguration(EndpointSchemeNode.class);
		table.column("Endpoint Uri").setWidth(400);

		table = createTableConfiguration(CamelProcessorMBean.class);
		table.column("Route Id"); // only required for Routes view so we can see which route a processor belongs to
		table.column("Processor Id").setLabelProviderStyle("image");
		table.column("Exchanges Total");
		table.column("Exchanges Completed");
		table.column("Exchanges Failed");
		table.column("Mean Processing Time");
		table.column("Min Processing Time");
		table.column("Max Processing Time");
		table.column("Last Processing Time");
		table.column("Total Processing Time");
		table.column("State");
		table.column("Statistics Enabled");
		table.column("Id").setVisible(false);
		table.column("Camel Id").setVisible(false);

		table = createTableConfiguration(ProcessorCallView.ID);
		table.column("ID").setWidth(400);
	}

}
