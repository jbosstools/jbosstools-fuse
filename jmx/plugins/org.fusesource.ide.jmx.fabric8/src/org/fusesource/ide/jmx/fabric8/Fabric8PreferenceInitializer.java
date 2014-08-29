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

package org.fusesource.ide.jmx.fabric8;

import org.fusesource.ide.commons.ui.UIPreferencesInitialiserSupport;
import org.fusesource.ide.commons.ui.config.TableConfiguration;
import org.fusesource.ide.jmx.fabric8.navigator.properties.LogViewTabSection;

public class Fabric8PreferenceInitializer extends UIPreferencesInitialiserSupport {

	@Override
	protected void initiailzeTableConfigurations() {
		
		TableConfiguration table = createTableConfiguration(LogViewTabSection.class.getName());
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
	}

}
