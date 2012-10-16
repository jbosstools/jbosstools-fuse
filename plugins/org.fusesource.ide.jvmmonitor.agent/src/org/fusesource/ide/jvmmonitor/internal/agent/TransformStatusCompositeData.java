/*******************************************************************************
 * Copyright (c) 2011 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.agent;

import java.beans.ConstructorProperties;

/**
 * The status of transforming classes that is converted into
 * <tt>CompositeData</tt>.
 */
public class TransformStatusCompositeData {

    /** The number of target classes to transform. */
    private int targetClassesCount;

    /** The number of transformed classes. */
    private int transformedClassesCount;

    /**
     * The constructor.
     * 
     * @param targetClassesCount
     *            The number of target classes to transform
     * @param transformedClassesCount
     *            The number of transformed classes
     */
    @ConstructorProperties({ "targetClassesCount", "transformedClassesCount" })
    public TransformStatusCompositeData(int targetClassesCount,
            int transformedClassesCount) {
        this.targetClassesCount = targetClassesCount;
        this.transformedClassesCount = transformedClassesCount;
    }

    /**
     * Gets the number of target classes to transform.
     * 
     * @return The number of target classes to transform
     */
    public int getTargetClassesCount() {
        return targetClassesCount;
    }

    /**
     * Gets the number of transformed classes.
     * 
     * @return The number of transformed classes
     */
    public int getTransformedClassesCount() {
        return transformedClassesCount;
    }

}
