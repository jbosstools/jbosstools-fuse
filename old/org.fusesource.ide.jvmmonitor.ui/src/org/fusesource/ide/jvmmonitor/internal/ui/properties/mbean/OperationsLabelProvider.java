/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.mbean;

import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;

import org.eclipse.jdt.core.Signature;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.fusesource.ide.jvmmonitor.ui.Activator;
import org.fusesource.ide.jvmmonitor.ui.ISharedImages;

/**
 * The operations label provider.
 */
public class OperationsLabelProvider extends LabelProvider implements
        ISharedImages {

    /** the method image */
    private Image methodImage;

    /*
     * @see LabelProvider#getText(Object)
     */
    @Override
    public String getText(Object element) {
        if (element instanceof MBeanOperationInfo) {
            MBeanOperationInfo info = (MBeanOperationInfo) element;
            return getMethodSignature(info);
        }
        return super.getText(element);
    }

    /*
     * @see LabelProvider#getImage(Object)
     */
    @Override
    public Image getImage(Object element) {
        if (element instanceof MBeanOperationInfo) {
            return getMethodImage();
        }
        return super.getImage(element);
    }

    /*
     * @see BaseLabelProvider#dispose()
     */
    @Override
    public void dispose() {
        if (methodImage != null) {
            methodImage.dispose();
        }
    }

    /**
     * Gets the method image.
     * 
     * @return the image
     */
    private Image getMethodImage() {
        if (methodImage == null || methodImage.isDisposed()) {
            methodImage = Activator.getImageDescriptor(METHOD_IMG_PATH)
                    .createImage();
        }
        return methodImage;
    }

    /**
     * Gets the method signature.
     * 
     * @param info
     *            The operation info
     * @return The method signature
     */
    private String getMethodSignature(MBeanOperationInfo info) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(info.getName());
        buffer.append("("); //$NON-NLS-1$
        StringBuffer paramBuffer = new StringBuffer();
        for (MBeanParameterInfo parameterInfo : info.getSignature()) {
            if (paramBuffer.length() != 0) {
                paramBuffer.append(", "); //$NON-NLS-1$
            }
            String param = parameterInfo.getType();
            if (param.startsWith("[")) { //$NON-NLS-1$
                param = Signature.toString(param);
            }
            int index = param.lastIndexOf('.');
            if (index > 0) {
                param = param.substring(index + 1);
            }
            paramBuffer.append(param);
        }
        buffer.append(paramBuffer);
        buffer.append(")"); //$NON-NLS-1$
        return buffer.toString();
    }
}
