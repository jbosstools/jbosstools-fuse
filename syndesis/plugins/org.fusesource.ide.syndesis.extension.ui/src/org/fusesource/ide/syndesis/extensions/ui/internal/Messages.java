/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
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
	
	public static String newProjectWizardExtensionDetailsPageName;
	public static String newProjectWizardExtensionDetailsPageTitle;
	public static String newProjectWizardExtensionDetailsPageDescription;
	public static String newProjectWizardExtensionDetailsPageSyndesisVersionLabel;
	public static String newProjectWizardExtensionDetailsPageSyndesisVersionTooltip;
	public static String newProjectWizardExtensionDetailsPageSyndesisVersionValidationLabel;
	public static String newProjectWizardExtensionDetailsPageSyndesisVersionValidationTooltip;

	public static String newProjectWizardExtensionDetailsPageExtensionDetailsLabel;
	public static String newProjectWizardExtensionDetailsPageExtensionIdLabel;
	public static String newProjectWizardExtensionDetailsPageExtensionIdTooltip;
	public static String newProjectWizardExtensionDetailsPageNameLabel;
	public static String newProjectWizardExtensionDetailsPageNameTooltip;
	public static String newProjectWizardExtensionDetailsPageDescriptionLabel;
	public static String newProjectWizardExtensionDetailsPageDescriptionTooltip;
	public static String newProjectWizardExtensionDetailsPageOptionalDescriptionFieldHint;
	public static String newProjectWizardExtensionDetailsPageVersionLabel;
	public static String newProjectWizardExtensionDetailsPageVersionTooltip;
	
	public static String newProjectWizardExtensionDetailsPageTypeSelectionLabel;
	public static String newProjectWizardExtensionDetailsPageTypeSelectionStepLabel;
	public static String newProjectWizardExtensionDetailsPageTypeSelectionStepHint;
	public static String newProjectWizardExtensionDetailsPageTypeSelectionConnectorLabel;
	public static String newProjectWizardExtensionDetailsPageTypeSelectionConnectorHint;

	public static String newProjectWizardExtensionDetailsPageStepTypeSelectionLabel;
	public static String newProjectWizardExtensionDetailsPageStepTypeSelectionJavaBeanLabel;
	public static String newProjectWizardExtensionDetailsPageStepTypeSelectionJavaBeanHint;
	public static String newProjectWizardExtensionDetailsPageStepTypeSelectionCamelRouteLabel;
	public static String newProjectWizardExtensionDetailsPageStepTypeSelectionCamelRouteHint;
	
	public static String newProjectWizardExtensionDetailsPageErrorMissingSyndesisVersion;
	public static String newProjectWizardExtensionDetailsPageSyndesisVersionValid;
	public static String newProjectWizardExtensionDetailsPageErrorInvalidSyndesisVersion;
	public static String newProjectWizardExtensionDetailsPageErrorMissingExtensionId;
	public static String newProjectWizardExtensionDetailsPageErrorInvalidExtensionId;
	public static String newProjectWizardExtensionDetailsPageErrorMissingExtensionVersion;
	public static String newProjectWizardExtensionDetailsPageErrorInvalidExtensionVersion;
	public static String newProjectWizardExtensionDetailsPageErrorMissingExtensionName;
	public static String newProjectWizardExtensionDetailsPageErrorValidationError;

	public static String installingRequiredFacetsForSyndesisExtensionProject;
	
	static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }
}
