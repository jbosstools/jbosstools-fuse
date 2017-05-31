/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.qe.reddeer.preference;

import org.jboss.reddeer.common.logging.Logger;
import org.jboss.reddeer.direct.preferences.Preferences;

/**
 * 
 * @author Andrej Podhradsky (apodhrad@redhat.com)
 *
 */
public class FuseToolingEditorPreferenceUtil {

	private static final Logger log = Logger.getLogger(FuseToolingEditorPreferenceUtil.class);

	public static final String FUSE_EDITOR_PLUGIN = "org.fusesource.ide.preferences";
	public static final String FUSE_EDITOR_ID_PREFERRED_KEY = "preferIdAsLabelPreference";

	/**
	 * Decides whether ID is preferred as a label in the editor
	 * 
	 * @return whether ID is preferred as a label
	 */
	public static boolean isIdPreferredAsLabel() {
		return "true".equals(Preferences.get(FUSE_EDITOR_PLUGIN, FUSE_EDITOR_ID_PREFERRED_KEY));
	}

	/**
	 * Sets ID as preferred as a label or not
	 */
	public static void setIdPreferredAsLabel(boolean idPreferredAsLabel) {
		log.info("Sets ID preferred as Label to '" + idPreferredAsLabel + "'");
		Preferences.set(FUSE_EDITOR_PLUGIN, FUSE_EDITOR_ID_PREFERRED_KEY, String.valueOf(idPreferredAsLabel));
	}

}
