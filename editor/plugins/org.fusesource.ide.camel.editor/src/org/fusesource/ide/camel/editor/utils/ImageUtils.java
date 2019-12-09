/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.camel.editor.utils;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.graphics.Image;
import org.fusesource.ide.camel.editor.internal.CamelEditorUIActivator;

/**
 * @author lhein
 */
public final class ImageUtils {
	
	private ImageUtils() {
		/* provide static utilities only */
	}
	
	/**
	 * retrieves the image dimensions of an image stored in the provider using
	 * the provided id
	 * 
	 * @param imageId	the id of the image in the provider store
	 * @return	the dimension or 0x0 on errors
	 */
	public static Dimension getImageSize(String imageId) {
		Image img = CamelEditorUIActivator.getDefault().getImage(imageId);
		if (img != null) {
			return new Dimension(img); 
		} else {
			return new Dimension(0, 0);
		}
	}
}
