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

package org.fusesource.ide.foundation.ui.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.fusesource.ide.foundation.ui.config.TableConfiguration;


/**
 * A useful base class for initialising UI baesd preferences
 */
public abstract class UIPreferencesInitialiserSupport extends AbstractPreferenceInitializer {
	private List<IFlushable> syncables = new ArrayList<IFlushable>();

	protected abstract void initiailzeTableConfigurations();

	@Override
	public void initializeDefaultPreferences() {
		initiailzeTableConfigurations();

		// now lets sync
		for (IFlushable table : syncables) {
			table.flush();
		}
	}

	protected TableConfiguration createTableConfiguration(Class<?> aType) {
		TableConfiguration answer = TableConfiguration.loadDefault(aType);
		addSyncable(answer);
		return answer;
	}

	protected TableConfiguration createTableConfiguration(String name) {
		TableConfiguration answer = TableConfiguration.loadDefault(name);
		addSyncable(answer);
		return answer;
	}

	protected void addSyncable(IFlushable answer) {
		syncables.add(answer);
	}

}