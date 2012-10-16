package org.fusesource.ide.fabric.activemq.navigator;

import org.fusesource.ide.commons.ui.UIPreferencesInitialiserSupport;
import org.fusesource.ide.commons.ui.config.TableConfiguration;

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
