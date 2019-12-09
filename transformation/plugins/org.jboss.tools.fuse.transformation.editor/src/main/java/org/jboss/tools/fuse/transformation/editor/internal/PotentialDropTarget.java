/******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial implementation.
 *****************************************************************************/
package org.jboss.tools.fuse.transformation.editor.internal;

import org.eclipse.swt.widgets.Control;

/**
 *
 */
public abstract class PotentialDropTarget {

    final Control control;

    /**
     * @param control
     */
    public PotentialDropTarget(final Control control) {
        this.control = control;
    }

    /**
     * @return <code>true</code> if this is a valid drop target for the drag source
     */
    public abstract boolean valid();
}
