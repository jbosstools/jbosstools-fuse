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

package org.fusesource.ide.camel.editor.provider;

import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.graphiti.ui.platform.AbstractImageProvider;
import org.eclipse.jface.resource.ImageDescriptor;
import org.fusesource.ide.camel.editor.internal.CamelEditorUIActivator;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelRouteElement;
import org.osgi.framework.Bundle;


/**
 * @author lhein
 */
public class ImageProvider extends AbstractImageProvider {

	private static final String PATTERN_FOR_IMAGE_NAME = "%s%s%s";

	private static final String ROOT_FOLDER_FOR_IMG = "icons/"; //$NON-NLS-1$

	// The prefix for all identifiers of this image provider
	public static final String PREFIX = "org.fusesource.ide.icons."; //$NON-NLS-1$
	public static final String POSTFIX_SMALL = "_small"; //$NON-NLS-1$
	public static final String POSTFIX_LARGE = "_diagram"; //$NON-NLS-1$

	public static final String IMG_FLOW = PREFIX + "flow"; //$NON-NLS-1$
	public static final String IMG_REDDOT = PREFIX + "reddot"; //$NON-NLS-1$
	public static final String IMG_GREENDOT = PREFIX + "greendot"; //$NON-NLS-1$
	public static final String IMG_GRAYDOT = PREFIX + "graydot"; //$NON-NLS-1$
	public static final String IMG_YELLOWDOT = PREFIX + "yellowdot"; //$NON-NLS-1$
	public static final String IMG_DELETE_BP = PREFIX + "delete_bp"; //$NON-NLS-1$
	public static final String IMG_PROPERTIES_BP = PREFIX + "properties_bp"; //$NON-NLS-1$
	public static final String IMG_UP_NAV = PREFIX + "up_nav"; //$NON-NLS-1$
	
	// outline
	/**
	 * The Constant IMG_OUTLINE_TREE.
	 */
	public static final String IMG_OUTLINE_TREE = PREFIX + "outline.tree"; //$NON-NLS-1$

	/**
	 * The Constant IMG_OUTLINE_THUMBNAIL.
	 */
	public static final String IMG_OUTLINE_THUMBNAIL = PREFIX + "outline.thumbnail"; //$NON-NLS-1$

	private static List<String> externalImages = new ArrayList<>();
	
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
		addImage(IMG_DELETE_BP, ROOT_FOLDER_FOR_IMG + "delete_bp.png"); //$NON-NLS-1$
		addToImageRegistry(IMG_DELETE_BP, ROOT_FOLDER_FOR_IMG + "delete_bp.png"); //$NON-NLS-1$
		addImage(IMG_PROPERTIES_BP, ROOT_FOLDER_FOR_IMG + "properties_bp.png"); //$NON-NLS-1$
		addToImageRegistry(IMG_PROPERTIES_BP, ROOT_FOLDER_FOR_IMG + "properties_bp.png"); //$NON-NLS-1$
		
		addImage(IMG_FLOW, ROOT_FOLDER_FOR_IMG + "flow16.png"); //$NON-NLS-1$
		addToImageRegistry(IMG_FLOW, ROOT_FOLDER_FOR_IMG + "flow16.png"); //$NON-NLS-1$
		addImage(IMG_OUTLINE_THUMBNAIL, ROOT_FOLDER_FOR_IMG + "thumbnail.png"); //$NON-NLS-1$
		addToImageRegistry(IMG_OUTLINE_THUMBNAIL, ROOT_FOLDER_FOR_IMG + "thumbnail.png"); //$NON-NLS-1$
		addImage(IMG_OUTLINE_TREE, ROOT_FOLDER_FOR_IMG + "tree.png"); //$NON-NLS-1$
		addToImageRegistry(IMG_OUTLINE_TREE, ROOT_FOLDER_FOR_IMG + "tree.png"); //$NON-NLS-1$
		addImage(IMG_UP_NAV, ROOT_FOLDER_FOR_IMG + "up_nav.gif"); //$NON-NLS-1$
		addToImageRegistry(IMG_UP_NAV, ROOT_FOLDER_FOR_IMG + "up_nav.gif"); //$NON-NLS-1$

		// add the images from extension point users
		addExtensionPointImages();
		
		// let the helper class fill all figure images
		ProviderHelper.addFigureIcons(this);

		addIconsForClass(new CamelRouteElement(null, null), "route16.png", "route.png");

		// add all images from the activator too
        String prefix = "/icons/";
        Enumeration<URL> enu = CamelEditorUIActivator.getDefault().getBundle().findEntries(prefix, "*", true);
        while (enu.hasMoreElements()) {
            URL u = enu.nextElement();
            String file = u.getFile();
            String fileName = file;
            if (!file.startsWith(prefix)) {
                CamelEditorUIActivator.pluginLog().logWarning("Warning: image: " + fileName + " does not start with prefix: " + prefix);
            }
            addIconCustomImage(fileName);
        }
	}

	/**
	 * loads all images provided by the extension points
	 */
	private void addExtensionPointImages() {
		// inject palette entries icons delivered via extension points
        IConfigurationElement[] extensions = Platform.getExtensionRegistry().getConfigurationElementsFor(ToolBehaviourProvider.PALETTE_ENTRY_PROVIDER_EXT_POINT_ID);
        for (IConfigurationElement e : extensions) {
            Bundle b = getBundleById(e.getContributor().getName());
        	if (b == null) {
        		return; // seems there is a problem with the osgi framework
        	}
        	String entryId = e.getAttribute(ToolBehaviourProvider.EXT_ID_ATTR);
            String paletteIconPath = e.getAttribute(ToolBehaviourProvider.PALETTE_ICON_ATTR);
            String diagramImagePath = e.getAttribute(ToolBehaviourProvider.DIAGRAM_IMAGE_ATTR);
            if (paletteIconPath != null && paletteIconPath.trim().length()>0) {
            	cacheImage(b, entryId, paletteIconPath, POSTFIX_SMALL);
            }
            if (diagramImagePath != null && diagramImagePath.trim().length()>0) {
            	cacheImage(b, entryId, diagramImagePath, POSTFIX_LARGE);
            }
        }
	}

	private void cacheImage(Bundle b, String entryId, String iconPath, String postfix) {
		String key = String.format(PATTERN_FOR_IMAGE_NAME, PREFIX, entryId, postfix);
		URL imgUrl = b.getEntry(iconPath);
		String fileExt = imgUrl.getFile().substring(imgUrl.getFile().lastIndexOf('.'));
		addImageFilePath(key, imgUrl.toString());
		CamelEditorUIActivator.getDefault().getImageRegistry().put(key, getExternalImage(b, iconPath));
		externalImages.add(entryId+fileExt);
	}
	
	/**
	 * returns the bundle with the given id
	 * 
	 * @param id
	 * @return
	 */
	private Bundle getBundleById(String id) {
		for (Bundle b : CamelEditorUIActivator.getDefault().getBundle().getBundleContext().getBundles()) {
			if (b.getSymbolicName().equals(id)) {
				return b;
			}
		}
		return null;
	}
	
	/**
	 * gets an image from an external bundle
	 * 
	 * @param bundle
	 * @param iconPath
	 * @return
	 */
	private ImageDescriptor getExternalImage(Bundle bundle, String iconPath) {
		URL url = bundle.getResource(iconPath);
		if (url != null) {
			return ImageDescriptor.createFromURL(url);
		}
		return null;
	}
	
	/**
	 * 
	 * @param icon
	 */
	private void addIconCustomImage(String iconPath) {
		if (iconPath == null || iconPath.endsWith("/")){
			return;
		}
		String iconName = iconPath.substring(iconPath.lastIndexOf('/')+1, iconPath.lastIndexOf('.'));
		if (iconName.endsWith("16")) {
			iconName = iconName.substring(0, iconName.lastIndexOf("16"));
		}
		String key = String.format(PATTERN_FOR_IMAGE_NAME, PREFIX, iconName, iconPath.toLowerCase().endsWith("16.png") ? POSTFIX_SMALL : POSTFIX_LARGE);
		addImage(key, iconPath);
		addToImageRegistry(key, iconPath);
	}

	/**
	 * adds a small and a large version of the icon to the image library
	 * 
	 * @param node			the node which needs an icon
	 * @param fileNameSmall	the file name of the small icon
	 * @param fileNameLarge	the file name of the large icon
	 */
	public void addIconsForClass(AbstractCamelModelElement node, String fileNameSmall, String fileNameLarge) {
		addIconsForIconName(node.getIconName(), fileNameSmall, fileNameLarge);
	}

	protected void addIconsForIconName(String iconName, String fileNameSmall, String fileNameLarge) {
		addImage(String.format(PATTERN_FOR_IMAGE_NAME, PREFIX, iconName, POSTFIX_SMALL), fileNameSmall);
		addImage(String.format(PATTERN_FOR_IMAGE_NAME, PREFIX, iconName, POSTFIX_LARGE), fileNameLarge); //$NON-NLS-1$
		addToImageRegistry(String.format(PATTERN_FOR_IMAGE_NAME, PREFIX, iconName, POSTFIX_SMALL), fileNameSmall); //$NON-NLS-1$
		addToImageRegistry(String.format(PATTERN_FOR_IMAGE_NAME, PREFIX, iconName, POSTFIX_LARGE), fileNameLarge); //$NON-NLS-1$
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
		CamelEditorUIActivator.getDefault().getImageRegistry().put(key, CamelEditorUIActivator.imageDescriptorFromPlugin(CamelEditorUIActivator.PLUGIN_ID, path));
	}

	public void addIconsForClass(AbstractCamelModelElement node) {
		addIconsForClass(node, String.format("%s%s16.png", ROOT_FOLDER_FOR_IMG, node.getIconName()), String.format("%s%s.png", ROOT_FOLDER_FOR_IMG, node.getIconName()));
	}
	
	public void addIconsForEIP(Eip eip ) {
		String eipName = eip.getName();
		addIconsForIconName(eipName, ROOT_FOLDER_FOR_IMG + eipName + "16.png", ROOT_FOLDER_FOR_IMG + eipName + ".png");
	}

	public static String getKeyForSmallIcon(boolean isEndpoint, String iconName) {
		return getKeyForIcon(isEndpoint, iconName, "%s16.png", POSTFIX_SMALL);
	}

	public static String getKeyForDiagramIcon(boolean isEndpoint, String iconName) {
		return getKeyForIcon(isEndpoint, iconName, "%s.png", POSTFIX_LARGE);
	}

	/**
	 * @param isEndpoint
	 * @param iconName
	 * @param iconNamePattern
	 * @param postfix
	 * @return
	 */
	private static String getKeyForIcon(boolean isEndpoint, String iconName, final String iconNamePattern, final String postfix) {
		if (isImageAvailable(String.format(iconNamePattern, iconName))) {
			return String.format(PATTERN_FOR_IMAGE_NAME, PREFIX, iconName, postfix);
		} else {
			return String.format(PATTERN_FOR_IMAGE_NAME, PREFIX, isEndpoint ? "endpoint" : "generic", postfix);
		}
	}
	
	protected static boolean isImageAvailable(String iconName) {
		if (externalImages.contains(iconName)) {
			return true;
		}
		return CamelEditorUIActivator.getDefault().getBundle().getEntry(String.format("%s%s", ROOT_FOLDER_FOR_IMG, iconName)) != null;
	}
}
