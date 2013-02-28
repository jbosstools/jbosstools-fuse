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

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.eclipse.core.runtime.Assert;

public class MBeanFeatureInfoWrapper {

    private MBeanInfoWrapper parent;

    MBeanFeatureInfoWrapper(MBeanInfoWrapper parent) {
        Assert.isNotNull(parent);
        this.parent = parent;
    }

    public ObjectName getObjectName() {
        return parent.getObjectName();
    }

    public MBeanInfoWrapper getMBeanInfoWrapper() {
        return parent;
    }

    public MBeanServerConnection getMBeanServerConnection() {
        return parent.getMBeanServerConnection();
    }

    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((parent == null) ? 0 : parent.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final MBeanFeatureInfoWrapper other = (MBeanFeatureInfoWrapper) obj;
        if (parent == null) {
            if (other.parent != null)
                return false;
        } else if (!parent.equals(other.parent))
            return false;
        return true;
    }

}
