/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.fusesource.ide.jvmmonitor.ui.Activator;

/**
 * The preference initializer.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

    /** The default value for period to update model. The unit is milliseconds. */
    private static final int DEFAULT_UPDATE_PERIOD = 1000;

    /*
     * @see AbstractPreferenceInitializer#initializeDefaultPreferences()
     */
    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();

        store.setDefault(IConstants.UPDATE_PERIOD, DEFAULT_UPDATE_PERIOD);
        store.setDefault(IConstants.LEGEND_VISIBILITY, false);
        store.setDefault(IConstants.WIDE_SCOPE_THREAD_FILTER, true);
        store.setDefault(IConstants.WIDE_SCOPE_SWT_RESOURCE_FILTER, true);
    }
}
