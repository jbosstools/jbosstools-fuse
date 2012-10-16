package org.fusesource.ide.fabric.navigator;

import org.fusesource.ide.commons.ui.UIPreferencesInitialiserSupport;
import org.fusesource.ide.commons.ui.config.TableConfiguration;
import org.fusesource.ide.fabric.navigator.osgi.BundlesTableSheetPage;
import org.fusesource.ide.fabric.navigator.osgi.ServicesTableSheetPage;
import org.fusesource.ide.fabric.views.logs.LogsView;


public class FabricPreferenceInitializer extends UIPreferencesInitialiserSupport {

	@Override
	protected void initiailzeTableConfigurations() {
		TableConfiguration table = createTableConfiguration(ContainerTableSheetPage.class);
		table.column("Id");
		table.column("Version");
		table.column("Profile Ids").setWidth(200);
		table.column("Status");
		table.column("Alive");
		table.column("Provisioning Complete").setWidth(150);
		table.column("Root");
		table.column("Type");
		table.column("Ssh Url").setWidth(160);
		table.column("Jmx Url").setWidth(370);

		table = createTableConfiguration(LogsView.ID);
		table.column("Level").setWidth(65);
		table.column("Host").setWidth(80);
		table.column("Container");
		table.column("Time").setLabelProviderStyle("timeThenDate");
		table.column("Category").setWidth(250);
		table.column("Message").setWidth(800);
		table.column("Location");
		table.column("Thread");
		table.column("Properties");
		table.column("ID");

		table = createTableConfiguration(BundlesTableSheetPage.class);
		table.column("Identifier").setWidth(60);
		table.column("Symbolic Name").setWidth(300);
		table.column("Version").setWidth(140);
		table.column("State");
		table.column("Last Modified").setWidth(160);
		table.column("Start Level");
		table.column("Location").setWidth(500);
		table.column("Persistently Started");
		table.column("Removal Pending");
		table.column("Fragment");

		table = createTableConfiguration(ServicesTableSheetPage.class);
		table.column("Identifier").setWidth(60);
		table.column("Object Class").setWidth(500);
		table.column("Bundle Identifier").setWidth(60);

	}

}
