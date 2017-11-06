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
import org.fusesource.ide.preferences.PreferenceManager;
import org.fusesource.ide.preferences.PreferencesConstants;


/**
 * @author lhein
 */
public class StyleUtil {
	public static final IColorConstant E_CLASS_TEXT_FOREGROUND = getColorConstant(PreferenceManager.getInstance().loadPreferenceAsString(PreferencesConstants.EDITOR_TEXT_COLOR));
	public static final IColorConstant E_CLASS_TEXT_BACKGROUND = getColorConstant("10,10,10");
	public static final IColorConstant E_CLASS_FOREGROUND = getColorConstant(PreferenceManager.getInstance().loadPreferenceAsString(PreferencesConstants.EDITOR_FIGURE_FG_COLOR));
	public static final IColorConstant E_CLASS_BACKGROUND = getColorConstant(PreferenceManager.getInstance().loadPreferenceAsString(PreferencesConstants.EDITOR_FIGURE_BG_COLOR));
	public static final IColorConstant E_CLASS_SHADOW_FOREGROUND = getColorConstant("128,128,128");
	public static final IColorConstant E_CLASS_SHADOW_BACKGROUND = getColorConstant("128,128,128");

	// constants for the new design start here
	public static final IColorConstant CONTAINER_FIGURE_BACKGROUND_COLOR = getColorConstant("229,248,255");
	public static final IColorConstant CONTAINER_FIGURE_TEXT_COLOR = getColorConstant("54,54,54");
	public static final IColorConstant CONTAINER_FIGURE_BORDER_COLOR = getColorConstant("107,159,218");
	public static final IColorConstant CONTAINER_FIGURE_COLLAPSED_BORDER_COLOR = getColorConstant("22,22,22");
	public static final IColorConstant FROM_FIGURE_BACKGROUND_COLOR = getColorConstant("200,235,121"); //158,224,189
	public static final IColorConstant TO_FIGURE_BACKGROUND_COLOR = getColorConstant("206,190,225"); 
	public static final IColorConstant EIP_FIGURE_BACKGROUND_COLOR = getColorConstant("240,171,0");
	public static final IColorConstant HIGHLIGHT_COLOR = getColorConstant("255,0,0");
	// constants for the new design end here
	
	public static final AdaptedGradientColoredAreas E_CLASS_GRADIENT = PredefinedColoredAreas.getBlueWhiteGlossAdaptions();

	public static final String DEFAULT_FONT = "Liberation Sans";
//	public static final String DEFAULT_FONT = Display.getDefault().getSystemFont().getFontData()[0].getName();
	public static final int DEFAULT_FONT_SIZE = 8;

	// use this flag to enable gradients (true) or use the predefined solid background color set in E_CLASS_BACKGROUND (false)
	// TODO - setting this to true breaks round tripping and switching between routes using the outline view!!!
	public static final boolean USE_GRADIENT_BACKGROUND = false;

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
		Style style = findStyle(parentStyle, styleId); 

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
		String[] parts = rgb.split(",");
		if (parts.length != 3) {
			return null;
		}
		StringBuilder hex = new StringBuilder();
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
				hex.append("0");
			}
			hex.append(h);
		}
		return new ColorConstant(hex.toString());
	}
}
