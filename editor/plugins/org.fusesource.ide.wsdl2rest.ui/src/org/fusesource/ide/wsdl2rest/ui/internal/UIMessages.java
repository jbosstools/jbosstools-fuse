/******************************************************************************* 
 * Copyright (c) 2018 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 

package org.fusesource.ide.wsdl2rest.ui.internal;

import org.eclipse.osgi.util.NLS;

/**
 * @author bfitzpat
 */
public class UIMessages extends NLS {

	private static final String BUNDLE_NAME = "org.fusesource.ide.wsdl2rest.ui.internal.i10n.messages";

	public static String wsdl2RestWizardFirstPagePageOneDescription;
	public static String wsdl2RestWizardFirstPageProjectLabel;
	public static String wsdl2RestWizardFirstPageValidatorProjectMustBeInWorkspace;
	public static String wsdl2RestWizardFirstPageValidatorProjectNameRequired;
	public static String wsdl2RestWizardFirstPageValidatorWSDLInaccessible;
	public static String wsdl2RestWizardFirstPageValidatorWSDLUrlRequired;
	public static String wsdl2RestWizardFirstPageWSDLFileLabel;
	public static String wsdl2RestWizardPageOneTitle;
	public static String wsdl2RestWizardPageTwoTitle;
	public static String wsdl2RestWizardSecondPageCamelFolderLabel;
	public static String wsdl2RestWizardSecondPageContainerSelectionDialogMessage;
	public static String wsdl2RestWizardSecondPageContainerSelectionDialogTitle;
	public static String wsdl2RestWizardSecondPageJavaFolderLabel;
	public static String wsdl2RestWizardSecondPagePageTwoDescription;
	public static String wsdl2RestWizardSecondPageTargetRESTServiceAddressLabel;

	public static String wsdl2RestWizardSecondPageTargetServiceAddressLabel;
	public static String wsdl2RestWizardSecondPageValidatorJavaClassMustExist;
	public static String wsdl2RestWizardSecondPageValidatorPathMustBeAccessible;

	public static String wsdl2RestWizardSecondPageValidatorPathOverwriteWarning;
	public static String wsdl2RestWizardSecondPageValidatorFileOverwriteWarning;
	public static String wsdl2RestWizardSecondPageValidatorPathRequired;

	public static String wsdl2RestWizardSecondPageValidatorPathWarning;
	public static String wsdl2RestWizardSecondPageValidatorServiceAddressMustBeValid;
	public static String wsdl2RestWizardWindowTitle;
	public static String wsdl2RestWizardErrorMessage;
	public static String wsdl2RestWizardErrorWindowTitle;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, UIMessages.class);
	}

}
