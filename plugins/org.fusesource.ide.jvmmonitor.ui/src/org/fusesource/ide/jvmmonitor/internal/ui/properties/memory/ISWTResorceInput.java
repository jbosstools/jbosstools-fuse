/*******************************************************************************
 * Copyright (c) 2011 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.memory;

import org.fusesource.ide.jvmmonitor.core.ISWTResourceElement;

/**
 * The SWT resource input.
 */
public interface ISWTResorceInput {

    /**
     * Gets the SWT resource elements.
     * 
     * @return The SWT resource elements
     */
    ISWTResourceElement[] getSWTResourceElements();
}
