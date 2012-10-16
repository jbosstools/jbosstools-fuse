/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.tools;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.fusesource.ide.jvmmonitor.tools.Activator;

/**
 * The initializer for Tools preference page.
 */
public class ToolsPreferenceInitializer extends AbstractPreferenceInitializer {

    /**
     * The default value for period to detect running JVMs on local host. The
     * unit is milliseconds.
     */
    private static final int DEFAULT_UPDATE_PERIOD = 3000;

    /** The default value for max number of classes. */
    private static final int DEFAULT_MAX_CLASSES_NUMBER = 50;

    /*
     * @see AbstractPreferenceInitializer#initializeDefaultPreferences()
     */
    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        store.setDefault(IConstants.UPDATE_PERIOD, DEFAULT_UPDATE_PERIOD);
        store.setDefault(IConstants.MAX_CLASSES_NUMBER,
                DEFAULT_MAX_CLASSES_NUMBER);
    }
}
