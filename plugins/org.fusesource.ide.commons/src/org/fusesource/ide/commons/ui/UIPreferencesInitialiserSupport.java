package org.fusesource.ide.commons.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.fusesource.ide.commons.IFlushable;
import org.fusesource.ide.commons.ui.config.TableConfiguration;


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