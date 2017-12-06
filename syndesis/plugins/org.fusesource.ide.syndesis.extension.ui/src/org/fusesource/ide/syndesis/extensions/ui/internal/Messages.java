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

package org.fusesource.ide.syndesis.extensions.ui.internal;

import org.eclipse.osgi.util.NLS;

/**
 * @author lhein
 */
public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.fusesource.ide.syndesis.extensions.ui.l10n.messages";

	public static String newProjectWizardTitle;
	public static String syndesisExtensionProjectCreatorRunnableCreatingTheProjectMonitorMessage;
	
	public static String newProjectWizardLocationPageName;
	public static String newProjectWizardLocationPageTitle;
	public static String newProjectWizardLocationPageDescription;
	public static String newProjectWizardLocationPageProjectNameLabel;
	public static String newProjectWizardLocationPageProjectNameDescription;
	public static String newProjectWizardLocationPageLocationGroupLabel;
	public static String newProjectWizardLocationPageLocationDefaultButtonLabel;
	public static String newProjectWizardLocationPageLocationDefaultButtonDescription;
	public static String newProjectWizardLocationPageLocationLabel;
	public static String newProjectWizardLocationPageLocationDescription;
	public static String newProjectWizardLocationPageLocationBrowseButtonLabel;
	public static String newProjectWizardLocationPageLocationBrowseButtonDescription;
	public static String newProjectWizardLocationPageLocationSelectionDialogTitle;
	public static String newProjectWizardLocationPageInvalidProjectNameText;
	public static String newProjectWizardLocationPageDuplicateProjectNameText;
	public static String newProjectWizardLocationPageInvalidProjectLocationText;
	
	public static String newProjectWizardExtensionVersionsPageName;
	public static String newProjectWizardExtensionVersionsPageTitle;
	public static String newProjectWizardExtensionVersionsPageDescription;
	public static String newProjectWizardExtensionVersionsPageSpringBootVersionLabel;
	public static String newProjectWizardExtensionVersionsPageSpringBootVersionTooltip;
	public static String newProjectWizardExtensionVersionsPageCamelVersionLabel;
	public static String newProjectWizardExtensionVersionsPageCamelVersionTooltip;
	public static String newProjectWizardExtensionVersionsPageCamelVersionValidationLabel;
	public static String newProjectWizardExtensionVersionsPageCamelVersionValidationTooltip;
	public static String newProjectWizardExtensionVersionsPageSyndesisVersionLabel;
	public static String newProjectWizardExtensionVersionsPageSyndesisVersionTooltip;
	public static String newProjectWizardExtensionVersionsPageErrorMissingSpringBootVersion;
	public static String newProjectWizardExtensionVersionsPageErrorMissingCamelVersion;
	public static String newProjectWizardExtensionVersionsPageErrorMissingSyndesisVersion;
	public static String newProjectWizardExtensionVersionsPageErrorInvalidCamelVersion;
	public static String newProjectWizardExtensionVersionsPageCamelVersionValid;
	
	public static String newProjectWizardExtensionDetailsPageName;
	public static String newProjectWizardExtensionDetailsPageTitle;
	public static String newProjectWizardExtensionDetailsPageDescription;
	public static String newProjectWizardExtensionDetailsPageExtensionIdLabel;
	public static String newProjectWizardExtensionDetailsPageExtensionIdTooltip;
	public static String newProjectWizardExtensionDetailsPageVersionLabel;
	public static String newProjectWizardExtensionDetailsPageVersionTooltip;
	public static String newProjectWizardExtensionDetailsPageNameLabel;
	public static String newProjectWizardExtensionDetailsPageNameTooltip;
	public static String newProjectWizardExtensionDetailsPageDescriptionLabel;
	public static String newProjectWizardExtensionDetailsPageDescriptionTooltip;
	public static String newProjectWizardExtensionDetailsPageOptionalDescriptionFieldHint;
	public static String newProjectWizardExtensionDetailsPageIconLabel;
	public static String newProjectWizardExtensionDetailsPageIconTooltip;
	public static String newProjectWizardExtensionDetailsPageOptionalIconFieldHint;
	public static String newProjectWizardExtensionDetailsPageIconBrowseLabel;
	public static String newProjectWizardExtensionDetailsPageIconSelectionDialogTitle;
	public static String newProjectWizardExtensionDetailsPageIconSelectionDialogFileTypeLabel;
	public static String newProjectWizardExtensionDetailsPageTagsLabel;
	public static String newProjectWizardExtensionDetailsPageTagsTooltip;
	public static String newProjectWizardExtensionDetailsPageOptionalTagsFieldHint;
	public static String newProjectWizardExtensionDetailsPageErrorMissingExtensionId;
	public static String newProjectWizardExtensionDetailsPageErrorInvalidExtensionId;
	public static String newProjectWizardExtensionDetailsPageErrorMissingExtensionVersion;
	public static String newProjectWizardExtensionDetailsPageErrorMissingExtensionName;
	public static String newProjectWizardExtensionDetailsPageIconSelectionDialogFileUnavailableError;

	public static String configuringFacets;
	public static String installingRequiredFacetsForSyndesisExtensionProject;
	
	static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }
}
