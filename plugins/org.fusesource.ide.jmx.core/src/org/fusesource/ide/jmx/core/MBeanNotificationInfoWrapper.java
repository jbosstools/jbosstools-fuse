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

import javax.management.MBeanNotificationInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.eclipse.core.runtime.Assert;

public class MBeanNotificationInfoWrapper {

    private MBeanNotificationInfo info;

    private MBeanServerConnection mbsc;

    private ObjectName on;

    public MBeanNotificationInfoWrapper(MBeanNotificationInfo info,
            ObjectName on, MBeanServerConnection mbsc) {
        Assert.isNotNull(info);
        Assert.isNotNull(on);
        Assert.isNotNull(mbsc);
        this.info = info;
        this.on = on;
        this.mbsc = mbsc;
    }

    public MBeanNotificationInfo getMBeanNotificationInfo() {
        return info;
    }

    public MBeanServerConnection getMBeanServerConnection() {
        return mbsc;
    }

    public ObjectName getObjectName() {
        return on;
    }

}