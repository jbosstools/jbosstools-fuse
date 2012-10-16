package org.fusesource.ide.camel.editor;

import org.eclipse.jface.resource.ImageDescriptor;

public class RiderImages {

	public static final ImageDescriptor REFRESH = createImageDescriptor("refresh.gif");

	private static ImageDescriptor createImageDescriptor(String key) {
		return Activator.getDefault().getImageDescriptor(key);
	}
}
