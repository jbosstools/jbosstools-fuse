package org.fusesource.ide.camel.editor.utils;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.graphics.Image;
import org.fusesource.ide.camel.editor.Activator;


/**
 * @author lhein
 */
public final class ImageUtils {
	
	private ImageUtils() {
	}
	
	/**
	 * retrieves the image dimensions of an image stored in the provider using
	 * the provided id
	 * 
	 * @param imageId	the id of the image in the provider store
	 * @return	the dimension or 0x0 on errors
	 */
	public static Dimension getImageSize(String imageId) {
		Dimension dim = null;
		Image img = Activator.getDefault().getImage(imageId);
		if (img != null) 
			dim = new Dimension(img); 
		else 
			dim = new Dimension(0, 0);
		return dim;
	}
}
