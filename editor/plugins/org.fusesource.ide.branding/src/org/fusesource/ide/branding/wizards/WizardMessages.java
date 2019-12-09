/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Johannes Utzig <mail@jutzig.de> - [JUnit] Update test suite wizard for JUnit 4: @RunWith(Suite.class)... - https://bugs.eclipse.org/155828
 *     James Strachan <jstracha@redhat.com> - Camel specific updates
 *******************************************************************************/
package org.fusesource.ide.branding.wizards;

import org.eclipse.osgi.util.NLS;

public final class WizardMessages extends NLS {

	private static final String BUNDLE_NAME= "org.fusesource.ide.branding.l10n.WizardMessages";//$NON-NLS-1$

	private WizardMessages() {
		// Do not instantiate
	}

	public static String Wizard_title_new_testcase;
	public static String Wizard_title_new_testsuite;
	public static String CheckedTableSelectionDialog_emptyListMessage;
	public static String CheckedTableSelectionDialog_selectAll;
	public static String CheckedTableSelectionDialog_deselectAll;
	public static String NewCamelTestWizardPageOne_title;
	public static String NewCamelTestWizardPageOne_description;
	public static String NewCamelTestWizardPageOne_methodStub_setUp;
	public static String NewCamelTestWizardPageOne_methodStub_tearDown;
	public static String NewCamelTestWizardPageOne_methodStub_constructor;
	public static String NewCamelTestWizardPageOne_method_Stub_label;
	public static String NewCamelTestWizardPageOne_class_to_test_label;
	public static String NewCamelTestWizardPageOne_class_to_test_browse;
	public static String NewCamelTestWizardPageOne_class_to_test_dialog_title;
	public static String NewCamelTestWizardPageOne_class_to_test_dialog_message;
	public static String NewCamelTestWizardPageOne_error_superclass_not_exist;
	public static String NewCamelTestWizardPageOne_error_superclass_is_interface;
	public static String NewCamelTestWizardPageOne_error_superclass_not_implementing_test_interface;
	public static String NewCamelTestWizardPageOne_error_superclass_empty;
	public static String NewCamelTestWizardPageOne_error_class_to_test_not_valid;
	public static String NewCamelTestWizardPageOne_error_class_to_test_not_exist;
	public static String NewCamelTestWizardPageOne_warning_class_to_test_is_interface;
	public static String NewTestCaseCreationWizard_fix_selection_junit3_description;
	public static String NewTestCaseCreationWizard_fix_selection_junit4_description;
	public static String NewTestCaseCreationWizard_fix_selection_open_build_path_dialog;
	public static String NewTestCaseCreationWizard_fix_selection_invoke_fix;
	public static String NewTestCaseCreationWizard_create_progress;
	public static String NewTestCaseCreationWizard_fix_selection_not_now;
	public static String NewCamelTestWizardPageOne_warning_class_to_test_not_visible;
	public static String NewCamelTestWizardPageOne_comment_class_to_test;
	public static String NewCamelTestWizardPageOne_error_junitNotOnbuildpath;
	public static String NewCamelTestWizardPageTwo_selected_endpoints_label_one;
	public static String NewCamelTestWizardPageTwo_selected_endpoints_label_many;
	public static String NewCamelTestWizardPageTwo_title;
	public static String NewCamelTestWizardPageTwo_description;
	public static String NewCamelTestWizardPageTwo_create_tasks_text;
	public static String NewCamelTestWizardPageTwo_create_final_method_stubs_text;
	public static String NewCamelTestWizardPageTwo_methods_tree_label;
	public static String NewCamelTestWizardPageTwo_selectAll;
	public static String NewCamelTestWizardPageTwo_deselectAll;
	public static String NewTestSuiteWiz_unsavedchangesDialog_title;
	public static String NewTestSuiteWiz_unsavedchangesDialog_message;
	public static String NewTestSuiteWizPage_title;
	public static String NewTestSuiteWizPage_description;
	public static String NewTestSuiteWizPage_classes_in_suite_label;
	public static String NewTestSuiteWizPage_selectAll;
	public static String NewTestSuiteWizPage_deselectAll;
	public static String NewTestSuiteWizPage_createType_beginTask;
	public static String NewTestSuiteWizPage_createType_updating_suite_method;
	public static String NewTestSuiteWizPage_createType_updateErrorDialog_title;
	public static String NewTestSuiteWizPage_createType_updateErrorDialog_message;
	public static String NewTestSuiteWizPage_classes_in_suite_error_no_testclasses_selected;
	public static String NewTestSuiteWizPage_typeName_error_name_empty;
	public static String NewTestSuiteWizPage_typeName_error_name_qualified;
	public static String NewTestSuiteWizPage_typeName_error_name_not_valid;
	public static String NewTestSuiteWizPage_typeName_error_name_name_discouraged;
	public static String NewTestSuiteWizPage_typeName_warning_already_exists;
	public static String NewTestSuiteWizPage_typeName_warning_already_exists_junit4;
	public static String NewTestSuiteWizPage_typeName_error_filtered;
	public static String NewTestSuiteWizPage_cannotUpdateDialog_title;
	public static String NewTestSuiteWizPage_cannotUpdateDialog_message;
	public static String NewTestClassWizPage_junit3_radio_label;
	public static String NewTestClassWizPage_junit4_radio_label;
	public static String NewTestClassWizPage_treeCaption_classSelected;
	public static String NewTestClassWizPage_treeCaption_classesSelected;
	public static String NewTestSuiteCreationWizardPage_infinite_recursion;
	public static String UpdateAllTests_selected_methods_label_one;
	public static String UpdateAllTests_selected_methods_label_many;
	public static String UpdateAllTests_title;
	public static String UpdateAllTests_message;
	public static String UpdateAllTests_beginTask;
	public static String UpdateAllTests_cannotUpdate_errorDialog_title;
	public static String UpdateAllTests_cannotUpdate_errorDialog_message;
	public static String UpdateAllTests_cannotFind_annotation_errorDialog_message;
	public static String UpdateAllTests_cannotFind_annotation_errorDialog_title;
	public static String UpdateAllTests_cannotFind_errorDialog_title;
	public static String UpdateAllTests_cannotFind_errorDialog_message;
	public static String NewJUnitWizard_op_error_title;
	public static String NewJUnitWizard_op_error_message;
	public static String ExceptionDialog_seeErrorLogMessage;
	public static String UpdateTestSuite_infinite_recursion;
	public static String UpdateTestSuite_error;
	public static String UpdateTestSuite_update;
	public static String UpdateTestSuite_could_not_update;


	public static String NewCamelTestWizardPageOne__error_junit4NotOnbuildpath;
	public static String NewCamelTestWizardPageOne_error_java5required;
	public static String NewCamelTestWizardPageOne_junit3_radio_label;
	public static String NewCamelTestWizardPageOne_junit4_radio_label;
	public static String NewCamelTestWizardPageOne_linkedtext_java5required;
	public static String NewCamelTestWizardPageOne_methodStub_setUpBeforeClass;
	public static String NewCamelTestWizardPageOne_methodStub_tearDownAfterClass;
	public static String NewCamelTestWizardPageOne_not_implemented_define_expectations;
	public static String NewCamelTestWizardPageOne_not_implemented_sending_messages;
	public static String NewCamelTestWizardPageOne_producer_template_fields;
	public static String NewCamelTestWizardPageOne_mock_endpoint_fields;
	public static String NewCamelTestWizardPageOne_consume_output_endpoints_to_mocks;
	
	public static String NewCamelXMLWizard_wizardTitle;
	public static String NewCamelXMLWizard_beginTaskMessage;
	public static String NewCamelXMLWizard_endTaskMessage;

	public static String NewCamelXMLWizardPage_pageTitle;
	public static String NewCamelXMLWizardPage_description;
	public static String NewCamelXMLWizardPage_browseButton;
	public static String NewCamelXMLWizardPage_labelFile;
	public static String NewCamelXMLWizardPage_labelFormat;

	public static String NewCamelXMLWizardPage_containerSelectionLabel;
	public static String NewCamelXMLWizardPage_statusUnspecifiedContainer;
	public static String NewCamelXMLWizardPage_statusContainerNotExisting;
	public static String NewCamelXMLWizardPage_statusProjectReadOnly;
	public static String NewCamelXMLWizardPage_statusUnspecifiedFileName;
	public static String NewCamelXMLWizardPage_statusInvalidFileName;
	public static String NewCamelXMLWizardPage_statusInvalidExtension;
	public static String NewCamelXMLWizardPage_statusFileAlreadyExists;
	
	public static String NewMessageWizard_Title;
	public static String NewMessageWizardPage_pageTitle;
	public static String NewMessageWizardPage_description;
	public static String NewMessageWizardPage_browseButton;
	public static String NewMessageWizardPage_labelFile;
	public static String NewMessageWizardPage_containerSelectionLabel;
	public static String NewMessageWizardPage_statusUnspecifiedContainer;
	public static String NewMessageWizardPage_statusContainerNotExisting;
	public static String NewMessageWizardPage_statusProjectReadOnly;
	public static String NewMessageWizardPage_statusUnspecifiedFileName;
	public static String NewMessageWizardPage_statusInvalidFileName;
	public static String NewMessageWizardPage_statusInvalidExtension;
	
	public static String FuseProjectWizardArchetypePage_btnProperty;
	public static String FuseProjectWizardArchetypePage_tableColName;
	public static String FuseProjectWizardArchetypePage_tableColValue;
	public static String FuseProjectWizardArchetypePage_dgPropertyTitle;
	public static String FuseProjectWizardArchetypePage_dgPropertyMessage;
	public static String FuseProjectWizardArchetypePage_missingProp;
	public static String FuseProjectWizardArchetypePage_missingPropTitle;
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, WizardMessages.class);
	}
}
