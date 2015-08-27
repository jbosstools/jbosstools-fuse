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

package org.fusesource.ide.camel.editor.provider;

import java.net.URL;
import java.util.Enumeration;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.graphiti.ui.platform.AbstractImageProvider;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.fusesource.ide.camel.editor.Activator;
import org.fusesource.ide.camel.editor.provider.generated.ProviderHelper;
import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.generated.Route;
import org.fusesource.ide.camel.model.generated.UniversalEIPUtility;
import org.osgi.framework.Bundle;


/**
 * @author lhein
 */
public class ImageProvider extends AbstractImageProvider {

	private static final String ROOT_FOLDER_FOR_IMG = "icons/"; //$NON-NLS-1$

	// The prefix for all identifiers of this image provider
	public static final String PREFIX = "org.fusesource.demo.icons."; //$NON-NLS-1$
	public static final String POSTFIX_SMALL = "_small"; //$NON-NLS-1$
	public static final String POSTFIX_LARGE = "_large"; //$NON-NLS-1$

	public static final String IMG_FLOW = PREFIX + "flow"; //$NON-NLS-1$
	public static final String IMG_REDDOT = PREFIX + "reddot"; //$NON-NLS-1$
	public static final String IMG_GREENDOT = PREFIX + "greendot"; //$NON-NLS-1$
	public static final String IMG_GRAYDOT = PREFIX + "graydot"; //$NON-NLS-1$
	public static final String IMG_YELLOWDOT = PREFIX + "yellowdot"; //$NON-NLS-1$
	public static final String IMG_DELETE_BP = PREFIX + "delete_bp"; //$NON-NLS-1$
	public static final String IMG_PROPERTIES_BP = PREFIX + "properties_bp"; //$NON-NLS-1$
	
	// outline
	/**
	 * The Constant IMG_OUTLINE_TREE.
	 */
	public static final String IMG_OUTLINE_TREE = PREFIX + "outline.tree"; //$NON-NLS-1$

	/**
	 * The Constant IMG_OUTLINE_THUMBNAIL.
	 */
	public static final String IMG_OUTLINE_THUMBNAIL = PREFIX + "outline.thumbnail"; //$NON-NLS-1$

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.ui.platform.AbstractImageProvider#addAvailableImages()
	 */
	@Override
	protected void addAvailableImages() {
		addImage(IMG_REDDOT, ROOT_FOLDER_FOR_IMG + "red-dot.png"); //$NON-NLS-1$
		addToImageRegistry(IMG_REDDOT, ROOT_FOLDER_FOR_IMG + "red-dot.png"); //$NON-NLS-1$
		addImage(IMG_GRAYDOT, ROOT_FOLDER_FOR_IMG + "gray-dot.png"); //$NON-NLS-1$
		addToImageRegistry(IMG_GRAYDOT, ROOT_FOLDER_FOR_IMG + "gray-dot.png"); //$NON-NLS-1$
		addImage(IMG_GREENDOT, ROOT_FOLDER_FOR_IMG + "green-dot.png"); //$NON-NLS-1$
		addToImageRegistry(IMG_GREENDOT, ROOT_FOLDER_FOR_IMG + "green-dot.png"); //$NON-NLS-1$
		addImage(IMG_YELLOWDOT, ROOT_FOLDER_FOR_IMG + "yellow-dot.png"); //$NON-NLS-1$
		addToImageRegistry(IMG_YELLOWDOT, ROOT_FOLDER_FOR_IMG + "yellow-dot.png"); //$NON-NLS-1$
		addImage(IMG_DELETE_BP, ROOT_FOLDER_FOR_IMG + "delete_bp.gif"); //$NON-NLS-1$
		addToImageRegistry(IMG_DELETE_BP, ROOT_FOLDER_FOR_IMG + "delete_bp.gif"); //$NON-NLS-1$
		addImage(IMG_PROPERTIES_BP, ROOT_FOLDER_FOR_IMG + "properties_bp.gif"); //$NON-NLS-1$
		addToImageRegistry(IMG_PROPERTIES_BP, ROOT_FOLDER_FOR_IMG + "properties_bp.gif"); //$NON-NLS-1$
		
		addImage(IMG_FLOW, ROOT_FOLDER_FOR_IMG + "flow16.png"); //$NON-NLS-1$
		addToImageRegistry(IMG_FLOW, ROOT_FOLDER_FOR_IMG + "flow16.png"); //$NON-NLS-1$
		addImage(IMG_OUTLINE_THUMBNAIL, ROOT_FOLDER_FOR_IMG + "thumbnail.gif"); //$NON-NLS-1$
		addToImageRegistry(IMG_OUTLINE_THUMBNAIL, ROOT_FOLDER_FOR_IMG + "thumbnail.gif"); //$NON-NLS-1$
		addImage(IMG_OUTLINE_TREE, ROOT_FOLDER_FOR_IMG + "tree.gif"); //$NON-NLS-1$
		addToImageRegistry(IMG_OUTLINE_TREE, ROOT_FOLDER_FOR_IMG + "tree.gif"); //$NON-NLS-1$

		// add the images from extension point users
		addExtensionPointImages();
		
		// let the helper class fill all figure images
		ProviderHelper.addFigureIcons(this);

		addIconsForClass(new Route(), "route16.png", "route.png");

		// add all images from the activator too
        String prefix = "/icons/";
        Enumeration<URL> enu = Activator.getDefault().getBundle().findEntries(prefix, "*", true);
        while (enu.hasMoreElements()) {
            URL u = enu.nextElement();
            String file = u.getFile();
            String fileName = file;
            if (!file.startsWith(prefix)) {
                Activator.getLogger().warning("Warning: image: " + fileName + " does not start with prefix: " + prefix);
            }
            fileName = fileName.substring(prefix.length());
            addIconCustomImages(fileName);
        }
	}

	private void addExtensionPointImages() {
		// inject palette entries icons delivered via extension points
        IConfigurationElement[] extensions = Platform.getExtensionRegistry().getConfigurationElementsFor(ToolBehaviourProvider.PALETTE_ENTRY_PROVIDER_EXT_POINT_ID);
        for (IConfigurationElement e : extensions) {
            Bundle b = getBundleById(e.getContributor().getName());
        	if (b == null) return; // seems there is a problem with the osgi framework
        	String entryId = e.getAttribute(ToolBehaviourProvider.EXT_ID_ATTR);
            String paletteIconPath = e.getAttribute(ToolBehaviourProvider.PALETTE_ICON_ATTR);
            String diagramImagePath = e.getAttribute(ToolBehaviourProvider.DIAGRAM_IMAGE_ATTR);
            if (paletteIconPath != null && paletteIconPath.trim().length()>0) {
            	String key = String.format("%s%s%s", PREFIX, entryId, POSTFIX_SMALL);
            	URL imgUrl = b.getEntry(paletteIconPath);
            	addImageFilePath(key, imgUrl.toString());
            	Activator.getDefault().getImageRegistry().put(key, getExternalImage(b, paletteIconPath));
            }
            if (diagramImagePath != null && diagramImagePath.trim().length()>0) {
            	String key = String.format("%s%s%s", PREFIX, entryId, POSTFIX_LARGE);
            	URL imgUrl = b.getEntry(diagramImagePath);
            	addImageFilePath(key, imgUrl.toString());
            	Activator.getDefault().getImageRegistry().put(key, getExternalImage(b, diagramImagePath));
            }
        }
	}
	
	private Bundle getBundleById(String id) {
		for (Bundle b : Activator.getDefault().getBundle().getBundleContext().getBundles()) {
			if (b.getSymbolicName().equals(id)) return b;
		}
		return null;
	}
	
	private ImageDescriptor getExternalImage(Bundle bundle, String iconPath) {
		URL url = bundle.getResource(iconPath);
		if (url != null) return ImageDescriptor.createFromURL(url);
		return null;
	}
	
	private void addIconCustomImages(String... iconNames) {
		for (String iconName : iconNames) {
			String littleIcon = iconName.replace(".png", "16.png");
			addIconsForIconName(iconName, littleIcon, iconName);
		}
	}

	/**
	 * adds a small and a large version of the icon to the image library
	 * 
	 * @param node			the node which needs an icon
	 * @param fileNameSmall	the file name of the small icon
	 * @param fileNameLarge	the file name of the large icon
	 */
	public void addIconsForClass(AbstractNode node, String fileNameSmall, String fileNameLarge) {
		addIconsForIconName(node.getIconName(), fileNameSmall, fileNameLarge);
	}

	protected void addIconsForIconName(String iconName, String fileNameSmall, String fileNameLarge) {
		addImage(String.format("%s%s%s", PREFIX, iconName, POSTFIX_SMALL), ROOT_FOLDER_FOR_IMG + fileNameSmall);
		addImage(String.format("%s%s%s", PREFIX, iconName, POSTFIX_LARGE), ROOT_FOLDER_FOR_IMG + fileNameLarge); //$NON-NLS-1$
		addToImageRegistry(String.format("%s%s%s", PREFIX, iconName, POSTFIX_SMALL), ROOT_FOLDER_FOR_IMG + fileNameSmall); //$NON-NLS-1$
		addToImageRegistry(String.format("%s%s%s", PREFIX, iconName, POSTFIX_LARGE), ROOT_FOLDER_FOR_IMG + fileNameLarge); //$NON-NLS-1$
	}

	/**
	 * adds an image to the registry if its not already existing
	 * 
	 * @param key
	 * @param value
	 */
	private void addImage(String key, String value) {
		if (getImageFilePath(key) == null) {
			addImageFilePath(key, value);
		}
	}

	private void addToImageRegistry(String key, String path) {
		Activator.getDefault().getImageRegistry().put(key, Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, path));
	}

	public void addIconsForClass(AbstractNode node) {
		addIconsForClass(node, node.getSmallIconName(), node.getIconName());
	}
	
	public void addIconsForEIP(Eip eip ) {
		String eipName = eip.getName();
		addIconsForIconName(UniversalEIPUtility.getIconName(eipName), 
				UniversalEIPUtility.getSmallIconName(eipName), 
				UniversalEIPUtility.getIconName(eipName));
	}

	

	public static String getKeyForSmallIcon(String iconName) {
	    if (isImageAvailable(iconName)) return String.format("%s%s%s", PREFIX, iconName, POSTFIX_SMALL);
	    return String.format("%s%s%s", PREFIX, "endpoint.png", POSTFIX_SMALL);
	}

	public static String getKeyForLargeIcon(String iconName) {
	    if (isImageAvailable(iconName)) return String.format("%s%s%s", PREFIX, iconName, POSTFIX_LARGE);
		return String.format("%s%s%s", PREFIX, "endpoint", POSTFIX_LARGE);
	}
	
	protected static boolean isImageAvailable(String iconName) {
		if (Activator.getDiagramEditor() != null) {
			Image img = GraphitiUi.getImageService().getImageForId(Activator.getDiagramEditor().getDiagramTypeProvider().getProviderId(), iconName);
			return img != null;
		}
	    return Activator.getDefault().getBundle().getEntry(String.format("%s%s", ROOT_FOLDER_FOR_IMG, iconName)) != null;
	}
}
