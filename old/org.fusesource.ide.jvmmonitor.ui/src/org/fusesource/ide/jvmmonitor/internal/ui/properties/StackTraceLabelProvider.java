/*******************************************************************************
 * Copyright (c) 2010-2011 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.fusesource.ide.jvmmonitor.ui.Activator;
import org.fusesource.ide.jvmmonitor.ui.ISharedImages;

/**
 * The label provider for stack trace viewer.
 */
public class StackTraceLabelProvider extends LabelProvider {

    /** The frame object image. */
    private Image frameObjImage;

    /*
     * @see LabelProvider#getText(Object)
     */
    @Override
    public String getText(Object element) {
        if (element instanceof StackTraceElement) {
            return element.toString();
        }
        return super.getText(element);
    }

    /*
     * @see LabelProvider#getImage(Object)
     */
    @Override
    public Image getImage(Object element) {
        if (element instanceof StackTraceElement) {
            return getStackObjImage();
        }
        return super.getImage(element);
    }

    /*
     * @see BaseLabelProvider#dispose()
     */
    @Override
    public void dispose() {
        if (frameObjImage != null) {
            frameObjImage.dispose();
        }
    }

    /**
     * Gets the stack frame object image.
     * 
     * @return The image
     */
    private Image getStackObjImage() {
        if (frameObjImage == null || frameObjImage.isDisposed()) {
            frameObjImage = Activator.getImageDescriptor(
                    ISharedImages.STACK_FRAME_OBJ_IMG_PATH).createImage();
        }
        return frameObjImage;
    }
}