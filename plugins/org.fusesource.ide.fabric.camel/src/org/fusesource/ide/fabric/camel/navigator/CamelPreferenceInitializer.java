package org.fusesource.ide.fabric.camel.navigator;

import org.fusesource.fabric.camel.facade.mbean.CamelProcessorMBean;
import org.fusesource.ide.commons.ui.UIPreferencesInitialiserSupport;
import org.fusesource.ide.commons.ui.config.TableConfiguration;


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
