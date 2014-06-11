/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.cpu;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.fusesource.ide.jvmmonitor.ui.Activator;
import org.fusesource.ide.jvmmonitor.ui.ISharedImages;

/**
 * The package label provider.
 */
public class PackageLabelProvider extends LabelProvider {

    /** The package image. */
    private Image packageImage;

    /*
     * @see LabelProvider#getImage(Object)
     */
    @Override
    public Image getImage(Object element) {
        if (packageImage == null || packageImage.isDisposed()) {
            packageImage = Activator.getImageDescriptor(
                    ISharedImages.PACKAGE_OBJ_IMG_PATH).createImage();
        }
        return packageImage;
    }

    /*
     * @see BaseLabelProvider#dispose()
     */
    @Override
    public void dispose() {
        super.dispose();
        if (packageImage != null) {
            packageImage.dispose();
        }
    }
}
