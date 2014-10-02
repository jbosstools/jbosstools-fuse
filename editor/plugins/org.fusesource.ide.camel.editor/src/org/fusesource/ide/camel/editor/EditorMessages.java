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

package org.fusesource.ide.camel.editor;

import org.eclipse.osgi.util.NLS;

/**
 * @author lhein
 */
public class EditorMessages extends NLS {

	private static final String BUNDLE_NAME = "org.fusesource.ide.camel.editor.l10n.editorMessages";

	public static String paletteToolbarTitle;

	public static String paletteSelectTitle;
	public static String paletteSelectDescription;

	public static String paletteMarqueeTitle;
	public static String paletteMarqueeDescription;

	public static String paletteFlowTitle;
	public static String paletteFlowDescription;

	public static String paletteRouteTitle;
	public static String paletteRouteDescription;

	public static String addRouteTitle;
	public static String addRouteDescription;

	public static String addRouteCommandLabel;
	public static String addRouteInAddMenuLabel;
	public static String deleteRouteCommandLabel;
	public static String deleteRouteCommandDescription;
	public static String deleteNodeCommandLabel;
	public static String deleteNodeCommandDescription;

	public static String autoLayoutActionDescription;
	public static String autoLayoutActionLabel;
	public static String autoLayoutActionTooltip;

	public static String selectCommandLabel;

	public static String saveModifiedTextFailedTitle;
	public static String saveModifiedTextFailedText;

	public static String propertiesDetailsTitle;
	public static String propertiesDocumentationTitle;
	public static String propertiesLanguageTitle;

	public static String editorPreferencePageDescription;
	public static String editorPreferencePageDefaultLanguageSetting;
	public static String editorPreferencePagePreferIdAsLabelSetting;
	public static String editorPreferencePageLayoutOrientationSetting;
	public static String editorPreferencePageLayoutOrientationSOUTH;
	public static String editorPreferencePageLayoutOrientationEAST;
	public static String editorPreferencePageGridVisibilitySetting;

	public static String colorPreferencePageDescription;
	public static String colorPreferencePageGridColorSetting;
	public static String colorPreferencePageTextColorSetting;
	public static String colorPreferencePageConnectionColorSetting;
	public static String colorPreferencePageFigureBGColorSetting;
	public static String colorPreferencePageFigureFGColorSetting;
	public static String colorPreferencePageTableChartBGColorSetting;

	public static String camelMenuLabel;
	public static String camelMenuAddLabel;
	
	public static String switchyardFoundTitle;
	public static String switchyardFoundText;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, EditorMessages.class);
	}
}
