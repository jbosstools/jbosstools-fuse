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
package org.fusesource.ide.preferences;

import org.eclipse.osgi.util.NLS;

/**
 * @author lhein
 */
public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.fusesource.ide.preferences.l10n.messages";

	public static String enableStagingRepositories_field;
	public static String stagingRepositoriesList_field;
	public static String stagingRepositoriesPreferencePageDescription;
	public static String NewRepoDialog_message;
	public static String NewRepoDialog_nameinvalid;
	public static String NewRepoDialog_nameNotUnique;
	public static String NewRepoDialog_urlinvalid;
	public static String NewStagingRepositoryDialogTitle;
	public static String RepositoryName_field;
	public static String RepositoryName_tooltip;
	public static String RepositoryURL_field;
	public static String RepositoryURL_tooltip;

	static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }
}
