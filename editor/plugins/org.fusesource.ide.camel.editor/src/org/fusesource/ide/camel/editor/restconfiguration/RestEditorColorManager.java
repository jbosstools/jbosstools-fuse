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
package org.fusesource.ide.camel.editor.restconfiguration;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.fusesource.ide.camel.model.service.core.model.RestVerbElement;

public class RestEditorColorManager {
	
	private static Map<String, Color> colorMap = new HashMap<>();
	private static Map<String, String> verbToBackgroundColorMap = new HashMap<>();
	private static Map<String, String> verbToImageColorMap = new HashMap<>();
	
	static {
		Display display = Display.getDefault();
		colorMap.put(RestConfigConstants.REST_COLOR_LIGHT_BLUE, new Color(display, 235, 242, 250));
		colorMap.put(RestConfigConstants.REST_COLOR_LIGHT_ORANGE, new Color(display, 250, 241, 230));
		colorMap.put(RestConfigConstants.REST_COLOR_LIGHT_GREEN, new Color(display, 232, 245, 239));
		colorMap.put(RestConfigConstants.REST_COLOR_LIGHT_GREY, new Color(display, 240, 248, 255));
		colorMap.put(RestConfigConstants.REST_COLOR_LIGHT_RED, new Color(display, 250, 231, 231));
		colorMap.put(RestConfigConstants.REST_COLOR_DARK_BLUE, new Color(display, 93, 173, 255));
		colorMap.put(RestConfigConstants.REST_COLOR_DARK_ORANGE, new Color(display, 254, 162, 24));
		colorMap.put(RestConfigConstants.REST_COLOR_DARK_GREEN, new Color(display, 65, 205, 142));
		colorMap.put(RestConfigConstants.REST_COLOR_DARK_RED, new Color(display, 252, 60, 55));
		
		verbToBackgroundColorMap.put(RestVerbElement.GET_VERB,    RestConfigConstants.REST_COLOR_LIGHT_BLUE);
		verbToBackgroundColorMap.put(RestVerbElement.PUT_VERB,    RestConfigConstants.REST_COLOR_LIGHT_GREEN);
		verbToBackgroundColorMap.put(RestVerbElement.POST_VERB,   RestConfigConstants.REST_COLOR_LIGHT_ORANGE);
		verbToBackgroundColorMap.put(RestVerbElement.DELETE_VERB, RestConfigConstants.REST_COLOR_LIGHT_RED);
		
		verbToImageColorMap.put(RestVerbElement.GET_VERB,    RestConfigConstants.REST_COLOR_DARK_BLUE);
		verbToImageColorMap.put(RestVerbElement.PUT_VERB,    RestConfigConstants.REST_COLOR_DARK_GREEN);
		verbToImageColorMap.put(RestVerbElement.POST_VERB,   RestConfigConstants.REST_COLOR_DARK_ORANGE);
		verbToImageColorMap.put(RestVerbElement.DELETE_VERB, RestConfigConstants.REST_COLOR_DARK_RED);
		
	}
	
	public Color getBackgroundColorForType(String tag) {
		return colorMap.get(verbToBackgroundColorMap.getOrDefault(tag, RestConfigConstants.REST_COLOR_LIGHT_GREY));
	}		

	public Color getForegroundColorForType(String tag) {
		if (RestVerbElement.PUT_VERB.equals(tag)) {
			return Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
		} else {
			return  Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
		}
	}		

	public Color getImageColorForType(String tag) {
		return colorMap.getOrDefault(verbToImageColorMap.get(tag), Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
	}

	/**
	 * @param colorConstant from RestConfigConstants.REST_COLOR_XXX
	 * @return
	 */
	public Color get(String colorConstant) {
		return colorMap.get(colorConstant);
	}

}
