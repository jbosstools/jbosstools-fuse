/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.fabric8.ui.navigator;

import org.fusesource.ide.commons.ui.UIPreferencesInitialiserSupport;
import org.fusesource.ide.commons.ui.config.TableConfiguration;
import org.fusesource.ide.fabric8.ui.navigator.properties.ContainerTableSheetPage;
import org.fusesource.ide.fabric8.ui.view.logs.LogsView;

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
		table.column("Level").setWidth(80);
		table.column("Host").setWidth(90);
		table.column("Container");
		table.column("Time").setLabelProviderStyle("timeThenDate");
		table.column("Time").setWidth(250);
		table.column("Category").setWidth(250);
		table.column("Message").setWidth(800);
		table.column("Location");
		table.column("Thread");
		table.column("Properties");
		table.column("ID");
	}
}
