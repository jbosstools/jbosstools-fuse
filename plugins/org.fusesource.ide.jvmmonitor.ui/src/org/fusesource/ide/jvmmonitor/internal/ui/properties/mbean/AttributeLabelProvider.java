/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.mbean;

import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Tree;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.PropertiesColumn;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.timeline.MBeanAttribute;
import org.fusesource.ide.jvmmonitor.ui.Activator;
import org.fusesource.ide.jvmmonitor.ui.ISharedImages;

/**
 * The MBean attribute label provider.
 */
public class AttributeLabelProvider extends LabelProvider implements
        ITableLabelProvider {

    /** The image height. */
    private static final int IMAGE_HEIGHT = 16;

    /** The image width that is wider than normal 16 to show overlay outside. */
    private static final int IMAGE_WIDTH = 24;

    /** The attribute image. */
    private Image attributeImage;

    /** The attribute folder image. */
    private Image attributeFolderImage;

    /** The write overlay image. */
    private Image writeOverlayImage;

    /** The viewer. */
    private TreeViewer viewer;

    /**
     * The constructor.
     * 
     * @param viewer
     *            The viewer
     */
    public AttributeLabelProvider(TreeViewer viewer) {
        this.viewer = viewer;
    }

    /*
     * @see ITableLabelProvider#getColumnImage(Object, int)
     */
    @Override
    public Image getColumnImage(Object element, int columnIndex) {
        if (columnIndex == getColumnIndex(PropertiesColumn.PROPERTY)) {
            if (element instanceof AttributeNode) {
                if (((AttributeNode) element).hasChildren()) {
                    return getAttributeFolderImage();
                } else if (((AttributeNode) element).isWritable()) {
                    return getWriteOverlayImage();
                }
                return getAttributeImage();
            }
        }
        return null;
    }

    /*
     * @see ITableLabelProvider#getColumnText(Object, int)
     */
    @Override
    public String getColumnText(Object element, int columnIndex) {
        if (element instanceof AttributeNode) {
            return getColumnText((AttributeNode) element, columnIndex);
        }
        throw new IllegalArgumentException("unknown input"); //$NON-NLS-1$
    }

    /*
     * Gets the text for filtering.
     * 
     * @see LabelProvider#getText(java.lang.Object)
     */
    @Override
    public String getText(Object obj) {
        if (obj instanceof MBeanAttribute) {
            return getColumnText(obj, getColumnIndex(PropertiesColumn.PROPERTY));
        }
        return super.getText(obj);
    }

    /*
     * @see BaseLabelProvider#dispose()
     */
    @Override
    public void dispose() {
        if (attributeImage != null) {
            attributeImage.dispose();
        }
        if (attributeFolderImage != null) {
            attributeFolderImage.dispose();
        }
        if (writeOverlayImage != null) {
            writeOverlayImage.dispose();
        }
    }

    /**
     * Gets the column text with the given MXBean attribute.
     * 
     * @param attribute
     *            The MXBean attribute
     * @param columnIndex
     *            The column index
     * @return The column text
     */
    private String getColumnText(AttributeNode attribute, int columnIndex) {
        if (columnIndex == getColumnIndex(PropertiesColumn.PROPERTY)) {
            return attribute.getName();
        } else if (columnIndex == getColumnIndex(PropertiesColumn.VALUE)) {
            Object value = attribute.getValue();
            if (value == null) {
                return "<not supported>"; //$NON-NLS-1$
            } else if (attribute.isValidLeaf()) {
                return value.toString();
            }
            return ""; //$NON-NLS-1$
        }
        throw new IllegalArgumentException("unknown column"); //$NON-NLS-1$
    }

    /**
     * Gets the column index corresponding to the given column.
     * 
     * @param column
     *            The attribute column
     * @return The column index
     */
    private int getColumnIndex(PropertiesColumn column) {
        Tree tree = viewer.getTree();
        for (int i = 0; i < tree.getColumnCount(); i++) {
            if (tree.getColumn(i).getText().equals(column.label)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Gets the attribute image.
     * 
     * @return The attribute image
     */
    private Image getAttributeImage() {
        if (attributeImage == null || attributeImage.isDisposed()) {
            ImageData imageData = Activator.getImageDescriptor(
                    ISharedImages.ATTRIBUTE_IMG_PATH).getImageData();
            attributeImage = new OverlayImageDescriptor(imageData,
                    new ImageDescriptor[0],
                    new Point(IMAGE_WIDTH, IMAGE_HEIGHT)).createImage();
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
            ImageData imageData = Activator.getImageDescriptor(
                    ISharedImages.ATTRIBUTE_FOLDER_IMG_PATH).getImageData();
            attributeFolderImage = new OverlayImageDescriptor(imageData,
                    new ImageDescriptor[0], new Point(IMAGE_WIDTH, 16))
                    .createImage();
        }
        return attributeFolderImage;
    }

    /**
     * Gets the write overlay image.
     * 
     * @return The write overlay image
     */
    private Image getWriteOverlayImage() {
        if (writeOverlayImage == null || writeOverlayImage.isDisposed()) {
            ImageDescriptor[] descriptors = new ImageDescriptor[] { null, null,
                    null, null };
            descriptors[IDecoration.TOP_RIGHT] = Activator
                    .getImageDescriptor(ISharedImages.WRITE_OVR_IMG_PATH);
            ImageData imageData = Activator.getImageDescriptor(
                    ISharedImages.ATTRIBUTE_IMG_PATH).getImageData();

            writeOverlayImage = new OverlayImageDescriptor(imageData,
                    descriptors, new Point(IMAGE_WIDTH, 16)).createImage();
        }
        return writeOverlayImage;
    }

    /**
     * The overlay image descriptor.
     */
    public static class OverlayImageDescriptor extends CompositeImageDescriptor {

        /** The overlay image descriptors. */
        private ImageDescriptor[] imageDescriptors;

        /** The image size. */
        private Point imageSize;

        /** The base image data. */
        private ImageData baseImageData;

        /**
         * The constructor.
         * 
         * @param baseImageData
         *            The base image data
         * @param imageDescriptors
         *            The image descriptors
         * @param imageSize
         *            The image size
         */
        public OverlayImageDescriptor(ImageData baseImageData,
                ImageDescriptor[] imageDescriptors, Point imageSize) {
            this.baseImageData = baseImageData;
            this.imageSize = imageSize;
            this.imageDescriptors = imageDescriptors;
        }

        /*
         * @see CompositeImageDescriptor#drawCompositeImage(int, int)
         */
        @Override
        protected void drawCompositeImage(int width, int height) {
            drawImage(baseImageData, 0, 0);

            for (int i = 0; i < imageDescriptors.length; i++) {
                ImageDescriptor descriptor = imageDescriptors[i];
                if (descriptor == null) {
                    continue;
                }

                ImageData imageData = descriptor.getImageData();
                if (imageData == null) {
                    imageData = ImageDescriptor.getMissingImageDescriptor()
                            .getImageData();
                }

                switch (i) {
                case IDecoration.TOP_LEFT:
                    drawImage(imageData, 0, 0);
                    break;
                case IDecoration.TOP_RIGHT:
                    drawImage(imageData, imageSize.x - imageData.width, 0);
                    break;
                case IDecoration.BOTTOM_LEFT:
                    drawImage(imageData, 0, imageSize.y - imageData.height);
                    break;
                case IDecoration.BOTTOM_RIGHT:
                    drawImage(imageData, imageSize.x - imageData.width,
                            imageSize.y - imageData.height);
                    break;
                }
            }
        }

        /*
         * @see CompositeImageDescriptor#getSize()
         */
        @Override
        protected Point getSize() {
            return imageSize;
        }
    }
}
