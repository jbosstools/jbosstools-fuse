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

package org.fusesource.ide.project;

import org.eclipse.osgi.util.NLS;

/**
 * @author lhein
 */
public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.fusesource.ide.project.l10n.messages";

	public static String riderBrowseCamelContextLabel;
	public static String riderBrowseCamelContextButton;
	public static String invalidCamelContextFileMessage;
	public static String riderLaunchConfigTabTitle;
	
	
	

	public static String NewCamelProject_ContentRootLabel;
	public static String NewCamelProject_FacetInstallationPage;
	public static String NewCamelProject_FacetInstallationPageDesc;
	public static String NewCamelProject_FacetInstallationPage_ContentRootError;
	public static String NewCamelProject_FirstPageTitle;
	public static String NewCamelProject_FirstPageDesc;
	
	static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }
}
