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

package org.fusesource.ide.jmx.karaf;

import org.fusesource.ide.foundation.ui.config.TableConfiguration;
import org.fusesource.ide.foundation.ui.util.UIPreferencesInitialiserSupport;
import org.fusesource.ide.jmx.karaf.navigator.osgi.BundlesTableSheetPage;
import org.fusesource.ide.jmx.karaf.navigator.osgi.ServicesTableSheetPage;

public class KarafPreferenceInitializer extends UIPreferencesInitialiserSupport {

	@Override
	protected void initiailzeTableConfigurations() {
		
		TableConfiguration table = createTableConfiguration(BundlesTableSheetPage.class);
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
