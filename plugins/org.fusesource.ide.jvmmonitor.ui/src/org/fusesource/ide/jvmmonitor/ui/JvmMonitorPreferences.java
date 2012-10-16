package org.fusesource.ide.jvmmonitor.ui;

import org.fusesource.ide.jvmmonitor.internal.ui.IConstants;

public class JvmMonitorPreferences {

	public static int getJvmUpdatePeriod() {
		int answer = Activator.getDefault()
		        .getPreferenceStore()
		        .getInt(IConstants.UPDATE_PERIOD);
		return answer;
	}

}
