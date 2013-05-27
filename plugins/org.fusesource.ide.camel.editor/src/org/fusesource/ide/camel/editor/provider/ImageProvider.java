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

import org.eclipse.graphiti.ui.platform.AbstractImageProvider;
import org.fusesource.ide.camel.editor.Activator;
import org.fusesource.ide.camel.editor.provider.generated.ProviderHelper;
import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.generated.Route;


/**
 * @author lhein
 */
public class ImageProvider extends AbstractImageProvider {

	private static final String ROOT_FOLDER_FOR_IMG = "icons/"; //$NON-NLS-1$

	// The prefix for all identifiers of this image provider
	protected static final String PREFIX = "org.fusesource.demo.icons."; //$NON-NLS-1$
	protected static final String POSTFIX_SMALL = "_small"; //$NON-NLS-1$
	protected static final String POSTFIX_LARGE = "_large"; //$NON-NLS-1$

	public static final String IMG_FLOW = PREFIX + "flow"; //$NON-NLS-1$

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
		addImage(IMG_FLOW, ROOT_FOLDER_FOR_IMG + "flow16.png"); //$NON-NLS-1$
		addToImageRegistry(IMG_FLOW, ROOT_FOLDER_FOR_IMG + "flow16.png"); //$NON-NLS-1$
		addImage(IMG_OUTLINE_THUMBNAIL, ROOT_FOLDER_FOR_IMG + "thumbnail.gif"); //$NON-NLS-1$
		addToImageRegistry(IMG_OUTLINE_THUMBNAIL, ROOT_FOLDER_FOR_IMG + "thumbnail.gif"); //$NON-NLS-1$
		addImage(IMG_OUTLINE_TREE, ROOT_FOLDER_FOR_IMG + "tree.gif"); //$NON-NLS-1$
		addToImageRegistry(IMG_OUTLINE_TREE, ROOT_FOLDER_FOR_IMG + "tree.gif"); //$NON-NLS-1$

		// let the helper class fill all figure images
		ProviderHelper.addFigureIcons(this);

		addIconsForClass(new Route(), "route16.png", "route.png");

		// lets add some custom images
		addIconCustomImages("endpointDrools.png", "endpointQueue.png", "endpointFile.png", "endpointFolder.png", "endpointTimer.png", "endpointRepository.png");
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

	public static String getKeyForSmallIcon(String iconName) {
		return String.format("%s%s%s", PREFIX, iconName, POSTFIX_SMALL);
	}

	public static String getKeyForLargeIcon(String iconName) {
		return String.format("%s%s%s", PREFIX, iconName, POSTFIX_LARGE);
	}
}
