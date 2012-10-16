/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved.
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.mbean;

import javax.management.ObjectName;

import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.fusesource.ide.jvmmonitor.core.IActiveJvm;
import org.fusesource.ide.jvmmonitor.ui.Activator;
import org.fusesource.ide.jvmmonitor.ui.ISharedImages;


/**
 * The MBean label provider.
 */
public class MBeanLabelProvider implements IStyledLabelProvider {

	/** The name property in object name. */
	private static final String NAME_PROPERTY = "name="; //$NON-NLS-1$

	/** The MBean image. */
	private Image mBeanImage;

	/** The MBean folder image. */
	private Image mBeanFolderImage;

	/*
	 * @see
	 * DelegatingStyledCellLabelProvider.IStyledLabelProvider#getStyledText(
	 * Object)
	 */
	@Override
	public StyledString getStyledText(Object element) {
		StyledString text = new StyledString();
		boolean appendSuffix = false;
		ObjectName objectName = null;
		IActiveJvm jvm = null;

		if (element instanceof MBeanDomain) {
			text.append(((MBeanDomain) element).getDomainName());
		} else if (element instanceof MBeanType) {
			MBeanName[] mBeanNames = ((MBeanType) element).getMBeanNames();
			text.append(((MBeanType) element).getName());
			if (mBeanNames.length == 1
					&& mBeanNames[0].isNotificationSubsctibed()) {
				appendSuffix = true;
				objectName = mBeanNames[0].getObjectName();
				jvm = ((MBeanType) element).getJvm();
			}
		} else if (element instanceof MBeanName) {
			objectName = ((MBeanName) element).getObjectName();
			text.append(getName(objectName));
			if (((MBeanName) element).isNotificationSubsctibed()) {
				appendSuffix = true;
				jvm = ((MBeanName) element).getJvm();
			}
		}

		if (appendSuffix && jvm != null) {
			int notificationCount = jvm.getMBeanServer().getMBeanNotification()
					.getNotifications(objectName).length;
			String suffix = " [notifications: " + notificationCount + "]"; //$NON-NLS-1$ //$NON-NLS-2$
			int offset = text.length();
			text.append(suffix);
			text.setStyle(offset, suffix.length(),
					StyledString.DECORATIONS_STYLER);
		}

		return text;
	}

	/*
	 * @see LabelProvider#getImage(Object)
	 */
	@Override
	public Image getImage(Object element) {
		if (element instanceof MBeanDomain) {
			return getMBeanFolderImage();
		}

		if (element instanceof MBeanType) {
			MBeanName[] mBeanNames = ((MBeanType) element).getMBeanNames();
			if (mBeanNames.length > 1) {
				return getMBeanFolderImage();
			}
			return getMBeanImage();
		}

		if (element instanceof MBeanName) {
			return getMBeanImage();
		}
		return null;
	}

	/*
	 * @see BaseLabelProvider#dispose()
	 */
	@Override
	public void dispose() {
		if (mBeanImage != null) {
			mBeanImage.dispose();
		}
		if (mBeanFolderImage != null) {
			mBeanFolderImage.dispose();
		}
	}

	/*
	 * @see IBaseLabelProvider#addListener(ILabelProviderListener)
	 */
	@Override
	public void addListener(ILabelProviderListener listener) {
		// do nothing
	}

	/*
	 * @see IBaseLabelProvider#isLabelProperty(Object, String)
	 */
	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	/*
	 * @see IBaseLabelProvider#removeListener(ILabelProviderListener)
	 */
	@Override
	public void removeListener(ILabelProviderListener listener) {
		// do nothing
	}

	/**
	 * Gets the name.
	 * 
	 * @param objectName
	 *            The object name
	 * @return The name
	 */
	private String getName(ObjectName objectName) {
		String canonicalName = objectName.getCanonicalName();
		if (!canonicalName.contains(NAME_PROPERTY)) {
			return canonicalName;
			//return ""; //$NON-NLS-1$
		}
		String type = canonicalName.split(NAME_PROPERTY)[1];
		return type.split(",")[0]; //$NON-NLS-1$
	}

	/**
	 * Gets the MBean image.
	 * 
	 * @return The MBean image
	 */
	private Image getMBeanImage() {
		if (mBeanImage == null || mBeanImage.isDisposed()) {
			mBeanImage = Activator.getImageDescriptor(
					ISharedImages.MBEAN_IMG_PATH).createImage();
		}
		return mBeanImage;
	}

	/**
	 * Gets the MBean folder image.
	 * 
	 * @return The MBean folder image
	 */
	private Image getMBeanFolderImage() {
		if (mBeanFolderImage == null || mBeanFolderImage.isDisposed()) {
			mBeanFolderImage = Activator.getImageDescriptor(
					ISharedImages.MBEAN_FOLDER_IMG_PATH).createImage();
		}
		return mBeanFolderImage;
	}
}
