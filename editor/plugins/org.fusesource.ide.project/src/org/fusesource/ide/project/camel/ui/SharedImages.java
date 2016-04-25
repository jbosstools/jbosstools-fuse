package org.fusesource.ide.project.camel.ui;

import java.util.Hashtable;
import java.util.Iterator;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.fusesource.ide.project.Activator;
import org.osgi.framework.Bundle;

public class SharedImages {
	public static final String IMG_CAMEL_16 = "camel16"; //$NON-NLS-1$
	public static final String IMG_CAMEL_64 = "camel64"; //$NON-NLS-1$

	private static SharedImages instance;
	
	private Hashtable<String, Object> images, descriptors;
	
	private SharedImages () {
		instance = this;
		images = new Hashtable<String, Object>();
		descriptors = new Hashtable<String, Object>();
		
		Bundle pluginBundle = Activator.getDefault().getBundle();
		descriptors.put(IMG_CAMEL_16, createImageDescriptor(pluginBundle, "icons/camel_context_icon.png")); //$NON-NLS-1$
		descriptors.put(IMG_CAMEL_64, createImageDescriptor(pluginBundle, "icons/camel_project_64x64.png")); //$NON-NLS-1$
		
		Iterator<String> iter = descriptors.keySet().iterator();

		while (iter.hasNext()) {
			String key = (String) iter.next();
			ImageDescriptor descriptor = descriptor(key);
			images.put(key,  descriptor.createImage());	
		}
	}
	
	private ImageDescriptor createImageDescriptor (Bundle pluginBundle, String relativePath)
	{
		return ImageDescriptor.createFromURL(pluginBundle.getEntry(relativePath));
	}
	
	private static SharedImages instance() {
		if (instance == null)
			return new SharedImages();
		
		return instance;
	}
	
	public static Image getImage(String key)
	{
		return instance().image(key);
	}
	
	public static ImageDescriptor getImageDescriptor(String key)
	{
		return instance().descriptor(key);
	}
	
	public Image image(String key)
	{
		return (Image) images.get(key);
	}
	
	public ImageDescriptor descriptor(String key)
	{
		return (ImageDescriptor) descriptors.get(key);
	}
	
	protected void finalize() throws Throwable {
		Iterator<String> iter = images.keySet().iterator();
		while (iter.hasNext())
		{
			Image image = (Image) images.get(iter.next());
			image.dispose();
		}
		super.finalize();
	}
}
