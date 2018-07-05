/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 

package org.fusesource.ide.camel.editor.internal;

import org.eclipse.osgi.util.NLS;

/**
 * @author lhein
 */
public class UIMessages extends NLS {

	private static final String BUNDLE_NAME = "org.fusesource.ide.camel.editor.internal.l10n.messages";

	public static String addGlobalBeanWizardBeanPageMessage;
	public static String addGlobalBeanWizardBeanPageTitle;
	public static String addGlobalBeanWizardWindowTitle;
	public static String addGlobalEndpointWizardWindowTitle;

	public static String addRestElementActionTooltip;
	public static String advancedBeanPropertiesSectionTitle;
	public static String argumentInputDialogDialogMessage;
	public static String argumentInputDialogDialogTitle;
	public static String argumentInputDialogDialogWindowTitle;
	public static String argumentInputDialogErrorMessage;
	public static String argumentInputDialogTypeFieldLabel;
	public static String argumentInputDialogValueFieldLabel;
	public static String argumentStyleChildTableControlAddButtonLabel;
	public static String argumentStyleChildTableControlEditButtonLabel;
	public static String argumentStyleChildTableControlRemoveButtonLabel;
	public static String argumentStyleChildTableControlTypeColumnLabel;
	public static String argumentStyleChildTableControlValueColumnLabel;
	public static String argumentXMLStyleChildTableControlAddButtonLabel;
	public static String argumentXMLStyleChildTableControlEditButtonLabel;
	public static String argumentXMLStyleChildTableControlRemoveButtonLabel;
	public static String argumentXMLStyleChildTableControlTypeColumnLabel;
	public static String argumentXMLStyleChildTableControlValueColumnLabel;
	public static String editorSourcePageTitle;
	public static String editorDesignPageTitle;
	public static String editorGlobalConfigurationPageTitle;
	public static String connectorsDrawerTitle;
	public static String endpointsDrawerTitle;
	public static String routingDrawerTitle;
	public static String controlFlowDrawerTitle;
	public static String transformationDrawerTitle;
	public static String methodSelectionDialogNoMethodSelectedError;

	public static String miscellaneousDrawerTitle;
	public static String propertiesDetailsTitle;
	public static String propertiesDocumentationTitle;
	public static String unconnectedNodeFoundTitle;
	public static String unconnectedNodeFoundText;
	public static String failedXMLValidationTitle;
	public static String failedXMLValidationText;
	public static String editorPreferencePageDescription;
	public static String editorPreferencePageDefaultLanguageSetting;
	public static String editorPreferencePagePreferIdAsLabelSetting;
	public static String editorPreferencePageLayoutOrientationSetting;
	public static String editorPreferencePageLayoutOrientationSOUTH;
	public static String editorPreferencePageLayoutOrientationEAST;
	public static String editorPreferencePageGridVisibilitySetting;
	public static String editorPreferencePageUserLabels;

	public static String colorPreferencePageDescription;
	public static String colorPreferencePageGridColorSetting;
	public static String colorPreferencePageTextColorSetting;
	public static String colorPreferencePageConnectionColorSetting;
	public static String colorPreferencePageFigureBGColorSetting;
	public static String colorPreferencePageFigureFGColorSetting;
	public static String colorPreferencePageTableChartBGColorSetting;
	public static String autoLayoutActionDescription;
	public static String autoLayoutActionLabel;
	public static String autoLayoutActionTooltip;
	public static String beanClassExistsValidatorErrorBeanClassMandatory;
	public static String beanClassExistsValidatorErrorBeanClassMustExist;
	public static String beanConfigUtilNoMethodsAvailable;
	public static String beanConfigUtilSelectStaticPublicMethod;
	public static String beanConfigUtilSelectPublicNotStaticMethod;
	public static String beanConfigUtilMethodSelectionDialogTitle;
	public static String beanConfigUtilMethodSelectionErrorCreatingXML;
	public static String beanConfigUtilMethodSelectionErrorNoTypeFound;
	public static String beanConfigUtilMethodSelectionMessage;
	public static String beanConfigUtilNoParmMethodSelectionMessage;
	public static String beanConfigUtilNoParmAndVoidMethodSelectionMessage;

	public static String beanRefClassExistsValidatorBeanClassOrBeanRefRequired;

	public static String beanRefClassExistsValidatorMustPickEitherBeanRefOrBeanClass;
	public static String beanRequiredPropertyValidatorErrorMandatoryProperty;
	public static String createGlobalElementDialogTitle;
	public static String createGlobalElementDiaglogText;
	public static String globalElementsTabAddButtonLabel;
	public static String globalElementsTabAddButtonTooltip;
	public static String globalElementsTabEditButtonLabel;
	public static String globalElementsTabEditButtonTooltip;
	public static String globalElementsTabDeleteButtonLabel;
	public static String globalElementsTabDeleteButtonTooltip;
	public static String newGlobalConfigurationTypeWizardDialogTitle;
	public static String camelDiagramBehaviourMessageOnErrorEditorInitialization;
	public static String componentGroupedByTagsTreeContenProviderUncategorized;
	public static String newGlobalConfigurationTypeDataFormatWizardDialogTitle;
	public static String newGlobalConfigurationTypeEndpointWizardDialogTitle;
	public static String dataFormatSelectionPageDataformatLabel;
	public static String dataFormatSelectionPageDataFormatSelectionPageDescription;
	public static String dataFormatSelectionPageDataFormatSelectionPageTitle;
	public static String dataFormatSelectionPageIdLabel;
	public static String dataFormatSelectionPageDescriptionLabel;

	public static String deleteRestElementActionTooltip;
	public static String editGlobalBeanWizardBeanEditPageMessage;
	public static String editGlobalBeanWizardBeanEditPageTitle;
	public static String editGlobalBeanWizardWindowTitle;
	public static String genericEndpointFigureFeaturePaletteDescription;
	public static String genericEndpointFigureFeaturePaletteName;
	public static String globalBeanBaseWizardPageFactoryBeanLabel;

	public static String globalBeanEditWizardPageArgumentsGroupLabel;
	public static String globalBeanEditWizardPageClassLabel;
	public static String globalBeanEditWizardPageDefaultName;
	public static String globalBeanEditWizardPagePropertiesGroupLabel;
	public static String globalBeanWizardPageArgumentsGroupLabel;
	public static String globalBeanWizardPageClassLabel;
	public static String globalBeanWizardPageDefaultName;
	public static String globalBeanWizardPagePropertiesGroupLabel;
	public static String globalEndpointWizardPageComponentSelectionGroupTitle;
	public static String globalEndpointWizardPageComponentSelectionMandatoryMessage;
	public static String globalEndpointWizardPageDescriptionFieldLabel;
	public static String globalEndpointWizardPageFilterSearchMessage;
	public static String globalEndpointWizardPageGlobalEndpointTypeSelectionWizardpageDescription;
	public static String selectComponentWizardPagePageName;
	public static String globalEndpointWizardPageGroupByCategories;
	public static String globalEndpointWizardPageIdFieldLabel;
	public static String globalEndpointWizardPageIdMandatoryMessage;
	public static String globalEndpointWizardPageIdExistingMessage;
	public static String globalEndpointWizardPageShowOnlyPaletteComonentsChecboxText;
	public static String newBeanIdValidatorErrorBeanIDAlreadyUsed;
	public static String newBeanIdValidatorErrorBeanIDMandatory;
	public static String propertyInputDialogErrorNoNameSpecified;
	public static String propertyInputDialogNameFieldLabel;
	public static String propertyInputDialogNameNotUnique;
	public static String propertyInputDialogPropertyDialogEditMessage;
	public static String propertyInputDialogPropertyDialogNewPropertyMessage;
	public static String propertyInputDialogPropertyDialogTitle;
	public static String propertyInputDialogPropertyDialogWindowTitle;
	public static String propertyInputDialogValueFieldLabel;
	public static String propertyInputDialogValueNotSpecified;
	public static String propertyMethodValidatorMethodValidationError;
	public static String propertyMethodValidatorMethodValidatorErrorPt2;
	public static String propertyMethodValidatorMethodValidatorErrorPt3;
	public static String propertyRequiredValidatorMandatoryParameterEmptyPt;
	public static String propertyStyleChildTableControlAddButtonLabel;
	public static String propertyStyleChildTableControlEditButtonLabel;
	public static String propertyStyleChildTableControlNameColumnLabel;
	public static String propertyStyleChildTableControlRemoveButtonLabel;
	public static String propertyStyleChildTableControlValueColumnLabel;
	public static String propertyXMLStyleChildTableControlAddButtonLabel;
	public static String propertyXMLStyleChildTableControlEditButtonLabel;
	public static String propertyXMLStyleChildTableControlNameColumnLabel;
	public static String propertyXMLStyleChildTableControlRemoveButtonLabel;
	public static String propertyXMLStyleChildTableControlValueColumnLabel;
	public static String selectEndpointWizardPageSelectionComponentDescription;
	public static String selectEndpointWizardPageSelectionComponentTitle;
	public static String selectEndpointWizardWindowTitle;

	public static String updatePomDependenciesProgressDialogLabel;
	public static String loadingCamelFile;
	public static String pending;

	public static String editorPreferencePageTechPreviewRESTEditorPageSetting;
	public static String restConfigEditorBindingModeLabel;
	public static String restConfigEditorComponentLabel;
	public static String restConfigEditorConfigurationTab;
	public static String restConfigEditorContextPathLabel;
	public static String restConfigEditorHostLabel;
	public static String restConfigEditorOperationsTab;
	public static String restConfigEditorPortLabel;
	public static String restConfigEditorRestConfigSectionLabelText;
	public static String restConfigEditorRestConfigurationTabDescription;
	public static String restConfigEditorRestOperationTabDescription;
	public static String restConfigEditorRestSectionLabel;
	public static String restConfigEditorRestSectionLabelText;
	public static String restConfigEditorRestTabDescription;

  /* Preferred Labels */	
	public static String preferredLabelsNewDialogTitle;
	public static String preferredLabelsNewDialogDescription;
	public static String preferredLabelsEditDialogTitle;
	public static String preferredLabelsEditDialogDescription;
	public static String preferredLabelsTitle;
	public static String preferredLabelsComponent;
	public static String preferredLabelsComponentHeader;
	public static String preferredLabelsParameter;
	public static String preferredLabelsParameterHeader;
	public static String preferredLabelsErrorMessageEmptyComponent;
	public static String preferredLabelsErrorMessageEmptyParameter;
	public static String preferredLabelsErrorMessageWrongCharacter;
	public static String preferredLabelsErrorMessageDuplicateComponent;
	public static String preferredLabelsAddButtonText;
	public static String preferredLabelsEditButtonText;
	public static String preferredLabelsRemoveButtonText;
	public static String restVerbParameterCannotBeNull;
	public static String validationIDAlreadyUsed;
	
	/* rest editor tab actions */
	public static String restEditorAddRestConfigurationActionButtonTooltip;
	public static String restEditorDeleteRestConfigurationActionButtonTooltip;
	public static String restEditorDeleteRestConfigurationActionDialogTitle;
	public static String restEditorDeleteRestConfigurationActionDialogMessage;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, UIMessages.class);
	}
}
