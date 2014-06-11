/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.timeline;

import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.mbean.MBeanLabelProvider;
import org.fusesource.ide.jvmmonitor.ui.Activator;
import org.fusesource.ide.jvmmonitor.ui.ISharedImages;

/**
 * The attribute label provider.
 */
public class AttributeLabelProvider extends MBeanLabelProvider {

    /** The attribute image. */
    private Image attributeImage;

    /** The attribute folder image. */
    private Image attributeFolderImage;

    /*
     * @see MBeanLabelProvider#getStyledText(Object)
     */
    @Override
    public StyledString getStyledText(Object element) {
        if (element instanceof AttributeNode) {
            return new StyledString(((AttributeNode) element).getName());
        }
        return super.getStyledText(element);
    }

    /*
     * @see LabelProvider#getImage(Object)
     */
    @Override
    public Image getImage(Object element) {
        if (element instanceof AttributeNode) {
            if (((AttributeNode) element).hasChildren()) {
                return getAttributeFolderImage();
            }
            return getAttributeImage();
        }
        return super.getImage(element);
    }

    /*
     * @see BaseLabelProvider#dispose()
     */
    @Override
    public void dispose() {
        super.dispose();
        if (attributeImage != null) {
            attributeImage.dispose();
        }
        if (attributeFolderImage != null) {
            attributeFolderImage.dispose();
        }
    }

    /**
     * Gets the attribute image.
     * 
     * @return The attribute image
     */
    private Image getAttributeImage() {
        if (attributeImage == null || attributeImage.isDisposed()) {
            attributeImage = Activator.getImageDescriptor(
                    ISharedImages.ATTRIBUTE_IMG_PATH).createImage();
        }
        return attributeImage;
    }

    /**
     * Gets the attribute image.
     * 
     * @return The attribute image
     */
    private Image getAttributeFolderImage() {
        if (attributeFolderImage == null || attributeFolderImage.isDisposed()) {
            attributeFolderImage = Activator.getImageDescriptor(
                    ISharedImages.ATTRIBUTE_FOLDER_IMG_PATH).createImage();
        }
        return attributeFolderImage;
    }
}
