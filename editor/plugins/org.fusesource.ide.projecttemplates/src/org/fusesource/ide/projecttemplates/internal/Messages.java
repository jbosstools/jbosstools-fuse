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

package org.fusesource.ide.projecttemplates.internal;

import org.eclipse.osgi.util.NLS;

/**
 * @author lhein
 */
public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.fusesource.ide.projecttemplates.l10n.messages";

	public static String archetypeTemplateCreatorCreatingTemplateFromArchetypeMonitorMessage;
	public static String basicProjectCreatorCreatingProjectMonitorMessage;
	public static String defaultTemplateConfiguratorConfiguringJavaProjectMonitorMessage;
	public static String basicProjectCreatorRunnableCreatingTheProjectMonitorMessage;

	public static String fuseIntegrationProjectWizardRuntimeAndCamelPageWarningMessageWhenCamelVersionCannotBeDeterminedInRuntime;
	public static String mavenTemplateConfiguratorAdaptingprojectToCamelVersionMonitorMessage;
	public static String mavenTemplateConfiguratorConfiguringMavenNatureMonitorMessage;
	public static String mavenTemplateConfiguratorConfiguringTemplatesMonitorMessage;
	public static String mavenTemplateConfiguratorAddingStagingRepositories;
	public static String newProjectWizardTitle;

	public static String newProjectWizardRuntimePageName;
	public static String newProjectWizardRuntimePageTitle;
	public static String newProjectWizardRuntimePageDescription;
	public static String newProjectWizardRuntimePageRuntimeDescription;
	public static String newProjectWizardRuntimePageRuntimeNewButtonLabel;
	public static String newProjectWizardRuntimePageRuntimeNewButtonDescription;
	public static String newProjectWizardRuntimePageCamelLabel;
	public static String newProjectWizardRuntimePageCamelDescription;
	public static String newProjectWizardRuntimePageCamelVersionValidationLabel;
	public static String newProjectWizardRuntimePageCamelVersionValidationDescription;
	public static String newProjectWizardRuntimePageNoRuntimeSelectedLabel;
	public static String newProjectWizardRuntimePageNoCamelVersionSelectedLabel;
	public static String newProjectWizardRuntimePageCamelVersionsDontMatchWarning;
	public static String newProjectWizardRuntimePageCamelVersionInvalidWarning;
	public static String newProjectWizardRuntimePageCamelVersionValidInfo;
	public static String newProjectWizardRuntimePageResolveDependencyStatus;
	public static String newProjectWizardRuntimePageCamelVersionInvalidSyntaxWarning;
	public static String newProjectWizardRuntimePageWhichRuntime;
	public static String newProjectWizardRuntimePageSpringBootChoice;
	public static String newProjectWizardRuntimePageKarafChoice;
	public static String newProjectWizardRuntimePageWildflyChoice;
	
	public static String newProjectWizardTemplatePageName;
	public static String newProjectWizardTemplatePageTitle;
	public static String newProjectWizardTemplatePageDescription;
	public static String newProjectWizardTemplatePageHeadlineLabel;
	public static String newProjectWizardTemplatePageEmptyProjectLabel;
	public static String newProjectWizardTemplatePageEmptyProjectDescription;
	public static String newProjectWizardTemplatePageBlueprintDSLLabel;
	public static String newProjectWizardTemplatePageBlueprintDSLDescription;
	public static String newProjectWizardTemplatePageSpringDSLLabel;
	public static String newProjectWizardTemplatePageSpringDSLDescription;
	public static String newProjectWizardTemplatePageJavaDSLLabel;
	public static String newProjectWizardTemplatePageJavaDSLDescription;
	public static String newProjectWizardTemplatePageTemplateProjectLabel;
	public static String newProjectWizardTemplatePageTemplateProjectDescription;
	public static String newProjectWizardTemplatePageDSLLabel;
	public static String newProjectWizardTemplatePageFilterBoxText;
	public static String newProjectWizardTemplatePageTemplateFilterMessageInformation;
	public static String newProjectWizardTemplatePageWhereToFindMoreExamples;
	public static String newProjectWizardTemplatePageListOfOtherExamplesReposMessage;
	public static String newProjectWizardTemplatePageListOfOtherExamplesRepos;

	public static String unzipStreamCreatorUnzippingTemplateFileMonitorMessage;
	
	public static String switchCamelVersionDialogName;
	public static String switchCamelVersionDialogTitle;
	public static String switchCamelVersionDialogVersionsLabel;
	public static String validatingCamelVersionMessage;
	public static String invalidCamelVersionMessage;
	public static String switchCamelVersionDialogSameVersionErrorMessage;
	
	public static String reOpenCamelEditorAfterVersionVersionChangeDialogTitle;
	public static String reOpenCamelEditorAfterVersionVersionChangeDialogText;

	public static String configuringFacets;
	public static String installingRequiredFacetsForCamelProject;

	public static String dozerInformationApiBreakTitle;
	public static String dozerInformationApiBreakMessage;


	static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }
}
