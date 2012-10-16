/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.memory;

import org.fusesource.ide.jvmmonitor.core.IHeapElement;

/**
 * The heap input.
 */
public interface IHeapInput {

    /**
     * Gets the heap list elements.
     * 
     * @return The heap list elements
     */
    IHeapElement[] getHeapListElements();
}
