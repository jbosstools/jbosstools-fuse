/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.foundation.ui.util;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Display;

/**
 * @author lhein
 */
public class ScreenshotUtil {
	
	/**
	 * creates a screenshot inside the given file
	 * 
	 * @param path	the file
	 */
	public static void saveScreenshotToFile(String path, int imageType) {
		// take the screenshot
		Display display = Display.getCurrent();
        final Image image = new Image(display, display.getBounds());
        GC gc = new GC(display);
        
        // save to disk
        try {
            gc.copyArea(image, 0, 0);
	        ImageLoader imgLoader = new ImageLoader();
	        imgLoader.data = new ImageData[] { image.getImageData() };
	        imgLoader.save(path, imageType);
        } finally {
        	gc.dispose();
        	image.dispose();
        }
	}
}
