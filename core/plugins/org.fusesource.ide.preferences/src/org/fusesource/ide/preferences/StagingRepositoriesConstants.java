/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.preferences;

/**
 * @author bfitzpat
 */
public class StagingRepositoriesConstants {
	
	private StagingRepositoriesConstants() {
		// util class
	}
	
	public static final String ENABLE_STAGING_REPOSITORIES = "enableStagingRepositories"; //$NON-NLS-1$
	public static final String STAGING_REPOSITORIES = "stagingRepositories"; //$NON-NLS-1$

	public static final String NAME_URL_SEPARATOR = ","; //$NON-NLS-1$
	public static final String REPO_SEPARATOR = ";"; //$NON-NLS-1$
	
	public static final String STAGING_REPOSITORIES_PREFERENCE_PAGE_ID = "org.fusesource.ide.preferences.StagingRepositoryPreferencePage"; //$NON-NLS-1$
}
