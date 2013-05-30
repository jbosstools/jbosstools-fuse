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

package org.fusesource.ide.camel.editor.utils;

import java.util.Collection;

import org.eclipse.graphiti.mm.StyleContainer;
import org.eclipse.graphiti.mm.algorithms.styles.AdaptedGradientColoredAreas;
import org.eclipse.graphiti.mm.algorithms.styles.Style;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.util.ColorConstant;
import org.eclipse.graphiti.util.IColorConstant;
import org.eclipse.graphiti.util.PredefinedColoredAreas;
import org.eclipse.swt.widgets.Display;
import org.fusesource.ide.preferences.PreferenceManager;
import org.fusesource.ide.preferences.PreferencesConstants;


/**
 * @author lhein
 */
public class StyleUtil {
	private static final IColorConstant E_CLASS_TEXT_FOREGROUND = getColorConstant(PreferenceManager.getInstance().loadPreferenceAsString(PreferencesConstants.EDITOR_TEXT_COLOR));
	private static final IColorConstant E_CLASS_FOREGROUND = getColorConstant(PreferenceManager.getInstance().loadPreferenceAsString(PreferencesConstants.EDITOR_FIGURE_FG_COLOR));
	private static final IColorConstant E_CLASS_BACKGROUND = getColorConstant(PreferenceManager.getInstance().loadPreferenceAsString(PreferencesConstants.EDITOR_FIGURE_BG_COLOR));
	private static final IColorConstant E_CLASS_SHADOW_FOREGROUND = getColorConstant("128,128,128");
	private static final IColorConstant E_CLASS_SHADOW_BACKGROUND = getColorConstant("128,128,128");
	private static final AdaptedGradientColoredAreas E_CLASS_GRADIENT = PredefinedColoredAreas.getBlueWhiteGlossAdaptions();

	private static final String DEFAULT_FONT = Display.getDefault().getSystemFont().getFontData()[0].getName();
	private static final int DEFAULT_FONT_SIZE = 10;

	// use this flag to enable gradients (true) or use the predefined solid background color set in E_CLASS_BACKGROUND (false)
	// TODO - setting this to true breaks round tripping and switching between routes using the outline view!!!
	private static final boolean USE_GRADIENT_BACKGROUND = false;

	public static Style getStyleForEClass(Diagram diagram) {
		final String styleId = "E-CLASS"; //$NON-NLS-1$

		Style style = findStyle(diagram, styleId);

		IGaService gaService = Graphiti.getGaService();
		if (style == null) { // style not found - create new style
			style = gaService.createStyle(diagram, styleId);
			style.setForeground(gaService.manageColor(diagram, E_CLASS_FOREGROUND));
			if (USE_GRADIENT_BACKGROUND) {
				gaService.setRenderingStyle(style, E_CLASS_GRADIENT);
			} else {
				style.setBackground(gaService.manageColor(diagram, E_CLASS_BACKGROUND));
			}
			style.setFilled(true);
			style.setLineWidth(2);
		}
		return style;
	}

	public static Style getStyleForCamelClass(Diagram diagram) {
		final String styleId = "FUSE-CAMEL-NODE"; //$NON-NLS-1$

		Style style = findStyle(diagram, styleId);

		IGaService gaService = Graphiti.getGaService();
		if (style == null) { // style not found - create new style
			style = gaService.createStyle(diagram, styleId);
			style.setForeground(gaService.manageColor(diagram, E_CLASS_FOREGROUND));
			if (USE_GRADIENT_BACKGROUND) {
				gaService.setRenderingStyle(style, E_CLASS_GRADIENT);
			} else {
				style.setBackground(gaService.manageColor(diagram, E_CLASS_BACKGROUND));
			}
			style.setFilled(true);
			style.setLineWidth(2);
		}
		return style;
	}

	public static Style getShadowStyleForCamelClass(Diagram diagram) {
		final String styleId = "FUSE-CAMEL-NODE-SHADOW"; //$NON-NLS-1$

		Style style = findStyle(diagram, styleId);

		IGaService gaService = Graphiti.getGaService();
		if (style == null) { // style not found - create new style
			style = gaService.createStyle(diagram, styleId);
			style.setForeground(gaService.manageColor(diagram, E_CLASS_SHADOW_FOREGROUND));
			style.setBackground(gaService.manageColor(diagram, E_CLASS_SHADOW_BACKGROUND));
			style.setFilled(true);
			style.setLineWidth(2);
		}
		return style;
	}

	public static Style getStyleForCamelText(Diagram diagram) {
		final String styleId = "FUSE-CAMEL-NODE-TEXT"; //$NON-NLS-1$

		IGaService gaService = Graphiti.getGaService();

		// this is a child style of the e-class-style
		Style parentStyle = getStyleForEClass(diagram);
		Style style = null;
		if (parentStyle == null) {
			style = gaService.createStyle(diagram, styleId);
		} else {
			style = findStyle(parentStyle, styleId);
		}

		if (style == null) { // style not found - create new style
			style = gaService.createStyle(getStyleForEClass(diagram), styleId);
			// "overwrites" values from parent style
			style.setForeground(gaService.manageColor(diagram, E_CLASS_TEXT_FOREGROUND));
		}
		style.setFont(gaService.manageFont(diagram, DEFAULT_FONT, DEFAULT_FONT_SIZE, false, true));

		return style;
	}

	public static Style getStyleForPolygon(Diagram diagram) {
		final String styleId = "FUSE-CAMEL-POLYGON-ARROW"; //$NON-NLS-1$

		Style style = findStyle(diagram, styleId);
		IGaService gaService = Graphiti.getGaService();

		if (style == null) { // style not found - create new style
			style = gaService.createStyle(diagram, styleId);
			style.setForeground(gaService.manageColor(diagram, getColorConstant(PreferenceManager.getInstance().loadPreferenceAsString(PreferencesConstants.EDITOR_CONNECTION_COLOR))));
			style.setBackground(gaService.manageColor(diagram, getColorConstant(PreferenceManager.getInstance().loadPreferenceAsString(PreferencesConstants.EDITOR_CONNECTION_COLOR))));
			style.setLineWidth(1);
		}
		return style;
	}

	// find the style with a given id in the style-container, can return null
	private static Style findStyle(StyleContainer styleContainer, String id) {
		// find and return style
		Collection<Style> styles = styleContainer.getStyles();
		if (styles != null) {
			for (Style style : styles) {
				if (id.equals(style.getId())) {
					return style;
				}
			}
		}
		return null;
	}

	public static IColorConstant getColorConstant(String rgb) {
		IColorConstant cc = null;
		String[] parts = rgb.split(",");
		if (parts.length != 3) {
			return null;
		}
		String hex = "";
		for (String part : parts) {
			String h;
			if (Character.isDigit(part.charAt(0))) {
				int i = Integer.parseInt(part.trim());
				h = Integer.toHexString(i);
			} else {
				// lets assume hex
				h = part;
			}
			if (h.length()<2) {
				hex+="0";
			}
			hex+=h;
		}
		cc = new ColorConstant(hex);

		return cc;
	}
}
