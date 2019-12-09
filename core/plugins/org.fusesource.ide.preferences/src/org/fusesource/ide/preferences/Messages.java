/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
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

	public static String enableStagingRepositoriesField;
	public static String stagingRepositoriesListField;
	public static String stagingRepositoriesPreferencePageDescription;
	public static String newRepoDialogMessage;
	public static String newRepoDialogNameInvalid;
	public static String newRepoDialogNameNotUnique;
	public static String newRepoDialogUrlInvalid;
	public static String newStagingRepositoryDialogTitle;
	public static String repositoryNameField;
	public static String repositoryNameTooltip;
	public static String repositoryURLField;
	public static String repositoryURLTooltip;

	static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }
}
