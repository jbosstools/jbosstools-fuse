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

package org.fusesource.ide.fabric;

import org.eclipse.osgi.util.NLS;

/**
 * @author lhein
 *
 */
public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.fusesource.ide.fabric.l10n.messages";

	public static String DefaultFabricName;

	public static String NewConnectionAction;
	public static String NewConnectionWizardTitle;

	public static String DefaultConnectionWizardPage_Description;
	public static String DefaultConnectionWizardPage_Name;
	public static String DefaultConnectionWizardPage_URL;
	public static String DefaultConnectionWizardPage_Username;
	public static String DefaultConnectionWizardPage_Password;

	public static String RefreshAction_text;
	public static String RefreshAction_description;
	public static String RefreshAction_tooltip;

	public static String MessageDetailBodyTextToolTip;
	public static String MessageDetailFormTitle;

	public static String MessageDetailHeadersTableToolTip;
	public static String MessageDetailHeadersTableNameColumn;
	public static String MessageDetailHeadersTableNameColumnTooltip;
	public static String MessageDetailHeadersTableValueColumn;
	public static String MessageDetailHeadersTableValueColumnTooltip;

	public static String profile_featuresLabel;
	public static String profile_featuresTooltip;
	public static String profile_bundlesLabel;
	public static String profile_bundlesTooltip;
	public static String profile_fabsLabel;
	public static String profile_fabsTooltip;
	public static String profile_repositoriesLabel;
	public static String profile_repositoriesTooltip;
	public static String profilesForm_header;

	public static String profile_addFeatureButtonLabel;
	public static String profile_addFeatureButtonTooltip;
	public static String profile_editFeatureButtonLabel;
	public static String profile_editFeatureButtonTooltip;
	public static String profile_deleteFeatureButtonLabel;
	public static String profile_deleteFeatureButtonTooltip;
	public static String profile_addFeatureDialogTitle;
	public static String profile_addFeatureDialogText;
	public static String profile_editFeatureDialogTitle;
	public static String profile_editFeatureDialogText;
	public static String profile_deleteFeatureDialogTitle;

	public static String profile_addBundleButtonLabel;
	public static String profile_addBundleButtonTooltip;
	public static String profile_editBundleButtonLabel;
	public static String profile_editBundleButtonTooltip;
	public static String profile_deleteBundleButtonLabel;
	public static String profile_deleteBundleButtonTooltip;
	public static String profile_addBundleDialogTitle;
	public static String profile_addBundleDialogText;
	public static String profile_editBundleDialogTitle;
	public static String profile_editBundleDialogText;
	public static String profile_deleteBundleDialogTitle;

	public static String profile_addFabButtonLabel;
	public static String profile_addFabButtonTooltip;
	public static String profile_editFabButtonLabel;
	public static String profile_editFabButtonTooltip;
	public static String profile_deleteFabButtonLabel;
	public static String profile_deleteFabButtonTooltip;
	public static String profile_addFabDialogTitle;
	public static String profile_addFabDialogText;
	public static String profile_editFabDialogTitle;
	public static String profile_editFabDialogText;
	public static String profile_deleteFabDialogTitle;

	public static String profile_addRepositoryButtonLabel;
	public static String profile_addRepositoryButtonTooltip;
	public static String profile_editRepositoryButtonLabel;
	public static String profile_editRepositoryButtonTooltip;
	public static String profile_deleteRepositoryButtonLabel;
	public static String profile_deleteRepositoryButtonTooltip;
	public static String profile_addRepositoryDialogTitle;
	public static String profile_addRepositoryDialogText;
	public static String profile_editRepositoryDialogTitle;
	public static String profile_editRepositoryDialogText;
	public static String profile_deleteRepositoryDialogTitle;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}