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

import java.util.Iterator;
import java.util.Set;

import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.eclipse.core.runtime.Assert;

public class DomainWrapper {

    private final String name;

    private final MBeanServerConnection mbsc;

    public DomainWrapper(String name, MBeanServerConnection mbsc) {
        Assert.isNotNull(name);
        Assert.isNotNull(mbsc);
        this.name = name;
        this.mbsc = mbsc;
    }

    private ObjectName getPattern() throws MalformedObjectNameException {
        return new ObjectName(name + ":*"); //$NON-NLS-1$
    }

    public String getName() {
        return name;
    }

    public MBeanInfoWrapper[] getMBeanInfos() {
        try {
            Set set = mbsc.queryNames(getPattern(), null);
            MBeanInfoWrapper[] instances = new MBeanInfoWrapper[set.size()];
            int i = 0;
            for (Iterator iter = set.iterator(); iter.hasNext();) {
                ObjectName on = (ObjectName) iter.next();
                MBeanInfo info = mbsc.getMBeanInfo(on);
                instances[i] = new MBeanInfoWrapper(on, info, mbsc);
                i++;
            }
            return instances;
        } catch (Exception e) {
            e.printStackTrace();
            return new MBeanInfoWrapper[0];
        }
    }
}
