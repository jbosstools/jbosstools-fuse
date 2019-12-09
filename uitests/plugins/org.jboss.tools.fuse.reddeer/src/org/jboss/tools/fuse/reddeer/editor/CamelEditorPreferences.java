/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.reddeer.editor;

import org.eclipse.reddeer.direct.preferences.Preferences;

/**
 * This class helps to directly set preferences for Camel editor.
 * 
 * @author Andrej Podhradsky (apodhrad@redhat.com)
 *
 */
public class CamelEditorPreferences {

	public static final String CAMEL_EDITOR_PLUGIN = "org.fusesource.ide.preferences";
	public static final String CAMEL_EDITOR_REST_KEY = "showRestPagePreference";

	public static void setShowRestPagePreference(boolean showRestPagePreference) {
		Preferences.set(CAMEL_EDITOR_PLUGIN, CAMEL_EDITOR_REST_KEY, String.valueOf(showRestPagePreference));
	}

	public static boolean isShowRestPagePreferenceOn() {
		return "true".equalsIgnoreCase(Preferences.get(CAMEL_EDITOR_PLUGIN, CAMEL_EDITOR_REST_KEY));
	}

}
