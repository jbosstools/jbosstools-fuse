package org.fusesource.ide.camel.commons.ui.table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.fusesource.ide.commons.ui.config.ColumnConfiguration;
import org.fusesource.ide.commons.ui.config.TableConfiguration;
import org.junit.Test;



public class TableConfigurationTest  {

	@Test
	public void testReorderUsingConfiguration() throws Exception {
		// initialise configuration
		SamplePreferenceInitializer init = new SamplePreferenceInitializer();
		init.initializeDefaultPreferences();

		List<String> defaultNames = Arrays.asList("something", "bar", "another", "foo", "whatnot");

		TableConfiguration tableConfig = TableConfiguration.loadDefault(TableConfigurationTest.class);
		assertNotNull("No TableConfiguration found!", tableConfig);
		List<ColumnConfiguration> columns = tableConfig.getColumnConfigurations();

		System.out.println("Found columns: " + columns);

		assertTrue("Should have more than one column!", columns.size() > 0);

		ColumnConfiguration column = columns.get(0);

		System.out.println("Column " + column + " visible: " + column.isVisible() + " description: " + column.getDescription());

		List<String> sorted = tableConfig.sortDefaultColumnNames(defaultNames);
		List<String> expected = Arrays.asList("foo", "bar", "something", "another", "whatnot");

		assertEquals("Sorted list", expected, sorted);
	}

}
