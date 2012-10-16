package org.fusesource.ide.camel.commons.ui.table;

import org.fusesource.ide.commons.ui.UIPreferencesInitialiserSupport;
import org.fusesource.ide.commons.ui.config.TableConfiguration;

public class SamplePreferenceInitializer extends UIPreferencesInitialiserSupport {

	@Override
	protected void initiailzeTableConfigurations() {
		TableConfiguration table = createTableConfiguration(TableConfigurationTest.class);
		table.column("foo");
		table.column("bar");
	}

}
