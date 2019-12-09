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

import org.fusesource.ide.foundation.ui.config.TableConfiguration;
import org.fusesource.ide.foundation.ui.util.UIPreferencesInitialiserSupport;

public class SamplePreferenceInitializer extends UIPreferencesInitialiserSupport {

	@Override
	protected void initiailzeTableConfigurations() {
		TableConfiguration table = createTableConfiguration(TableConfigurationIT.class);
		table.column("foo");
		table.column("bar");
	}

}
