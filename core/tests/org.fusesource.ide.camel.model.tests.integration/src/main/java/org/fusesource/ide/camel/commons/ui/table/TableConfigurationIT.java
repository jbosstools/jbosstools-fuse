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

package org.fusesource.ide.camel.commons.ui.table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.fusesource.ide.foundation.ui.config.ColumnConfiguration;
import org.fusesource.ide.foundation.ui.config.TableConfiguration;
import org.junit.Test;

public class TableConfigurationIT  {

	@Test
	public void testReorderUsingConfiguration() throws Exception {
		// initialise configuration
		SamplePreferenceInitializer init = new SamplePreferenceInitializer();
		init.initializeDefaultPreferences();

		List<String> defaultNames = Arrays.asList("something", "bar", "another", "foo", "whatnot");

		TableConfiguration tableConfig = TableConfiguration.loadDefault(TableConfigurationIT.class);
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
