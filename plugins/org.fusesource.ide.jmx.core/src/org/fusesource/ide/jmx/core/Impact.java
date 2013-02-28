/*******************************************************************************
 * Copyright (c) 2006 Jeff Mesnil
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

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

package org.fusesource.ide.jmx.core;

import javax.management.MBeanOperationInfo;

public class Impact {

    public static final Impact ACTION = new Impact("ACTION"); //$NON-NLS-1$

    public static final Impact ACTION_INFO = new Impact("ACTION_INFO"); //$NON-NLS-1$

    public static final Impact INFO = new Impact("INFO"); //$NON-NLS-1$

    public static final Impact UNKNOWN = new Impact("UNKNOWN"); //$NON-NLS-1$

    public static Impact parseInt(int impact) {
        switch (impact) {
        case MBeanOperationInfo.ACTION:
            return ACTION;
        case MBeanOperationInfo.ACTION_INFO:
            return ACTION_INFO;
        case MBeanOperationInfo.INFO:
            return INFO;
        default:
            return UNKNOWN;
        }
    }

    private final String name;

    private Impact(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }
}
