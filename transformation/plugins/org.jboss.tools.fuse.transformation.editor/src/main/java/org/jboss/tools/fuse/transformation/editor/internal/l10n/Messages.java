/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.fuse.transformation.editor.internal.l10n;

import org.eclipse.osgi.util.NLS;

/**
 * @author Aurelien Pupier
 *
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.jboss.tools.fuse.transformation.editor.internal.l10n.messages"; //$NON-NLS-1$
	public static String JavaPage_groupText_ClassStructurepreview;
	public static String JavaPage_jobName_openError;
	public static String JavaPage_label_theSourceClass;
	public static String JavaPage_label_theSourClassTooltip;
	public static String JavaPage_label_theTargetClass;
	public static String JavaPage_label_theTargetClassTooltip;
	public static String JavaPage_pageTitle;
	public static String JavaPage_pathEmptyError_source;
	public static String JavaPage_pathEmptyError_target;
	public static String JavaPage_SelectClass_title;
	public static String JavaPage_SelectClassDialog_message;
	public static String JavaPage_SourceTypeJava_Description;
	public static String JavaPage_SourceTypeJavaTitle;
	public static String JavaPage_TargetTypeJava_Description;
	public static String JavaPage_TargetTypeJavaTitle;
	public static String JavaPage_tooltipBrowseButton;
	public static String JavaPage_unableToFindError_source;
	public static String JavaPage_unableToFindError_target;
	public static String DateFormatInputDialog_DateFormat;
	public static String DateFormatInputDialog_DateFormatTitle;
	public static String DateFormatInputDialog_SelectOrEnterDateFormatForConversion;
	public static String DateFormatInputDialog_TypeOwnFormatIsNotListed;

	public static String Util_CustomTransformationClass;
	public static String Util_Select_DialogTtile;
	public static String Util_SelectACustomTransformationClass;
	public static String Util_SelectCamelFileDialogTitle;
	public static String Util_SelectCamelXMLFileFromProject_DialogTitle;
	public static String Util_SelectTransformationFileFromProject;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
