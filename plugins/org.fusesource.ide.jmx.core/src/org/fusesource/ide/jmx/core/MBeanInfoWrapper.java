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

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanServerConnection;
import javax.management.NotificationBroadcaster;
import javax.management.ObjectName;

import org.eclipse.core.runtime.Assert;
import org.fusesource.ide.commons.tree.HasName;


public class MBeanInfoWrapper implements Comparable, HasName {
    private final ObjectName on;

    private final MBeanInfo info;

    private final MBeanServerConnection mbsc;

    public MBeanInfoWrapper(ObjectName on, MBeanInfo info,
            MBeanServerConnection mbsc) {
        Assert.isNotNull(on);
        Assert.isNotNull(info);
        Assert.isNotNull(mbsc);
        this.on = on;
        this.info = info;
        this.mbsc = mbsc;
    }

    public ObjectName getObjectName() {
        return on;
    }

	public String getName() {
		return getObjectName().toString();
	}

	public MBeanInfo getMBeanInfo() {
        return info;
    }

    public MBeanServerConnection getMBeanServerConnection() {
        return mbsc;
    }

    /**
     * Test if the wrapped MBean is a <code>NotificationBroadcaster</code>
     * using {@link MBeanServerConnection#isInstanceOf(ObjectName, String)}.
     *
     * @return <code>true</code> if the wrapped MBean is a
     *         <code>NotificationBroadcaster</code>, <code>false</code>
     *         else
     */
    public boolean isNotificationBroadcaster() {
        try {
            return mbsc.isInstanceOf(on, NotificationBroadcaster.class
                    .getName());
        } catch (Exception e) {
            return false;
        }
    }

    public MBeanAttributeInfoWrapper[] getMBeanAttributeInfoWrappers() {
        MBeanAttributeInfo[] attributes = info.getAttributes();
        MBeanAttributeInfoWrapper[] attrWrappers = new MBeanAttributeInfoWrapper[attributes.length];
        for (int i = 0; i < attributes.length; i++) {
            MBeanAttributeInfo attrInfo = attributes[i];
            attrWrappers[i] = new MBeanAttributeInfoWrapper(attrInfo, this);
        }
        return attrWrappers;
    }

    public MBeanOperationInfoWrapper[] getMBeanOperationInfoWrappers() {
        MBeanOperationInfo[] operations = info.getOperations();
        MBeanOperationInfoWrapper[] opWrappers = new MBeanOperationInfoWrapper[operations.length];
        for (int i = 0; i < operations.length; i++) {
            MBeanOperationInfo opInfo = operations[i];
            opWrappers[i] = new MBeanOperationInfoWrapper(opInfo, this);
        }
        return opWrappers;
    }

    public MBeanNotificationInfoWrapper[] getMBeanNotificationInfoWrappers() {
        MBeanNotificationInfo[] notifications = info.getNotifications();
        MBeanNotificationInfoWrapper[] notificationWrappers = new MBeanNotificationInfoWrapper[notifications.length];
        for (int i = 0; i < notifications.length; i++) {
            MBeanNotificationInfo opInfo = notifications[i];
            notificationWrappers[i] = new MBeanNotificationInfoWrapper(opInfo,
                    on, mbsc);
        }
        return notificationWrappers;
    }

    public MBeanFeatureInfoWrapper[] getMBeanFeatureInfos() {
        MBeanAttributeInfo[] attributes = info.getAttributes();
        MBeanOperationInfo[] operations = info.getOperations();
        MBeanFeatureInfoWrapper[] o = new MBeanFeatureInfoWrapper[attributes.length
                + operations.length];
        for (int i = 0; i < attributes.length; i++) {
            MBeanAttributeInfo attrInfo = attributes[i];
            o[i] = new MBeanAttributeInfoWrapper(attrInfo, this);
        }
        for (int i = 0; i < operations.length; i++) {
            MBeanOperationInfo opInfo = operations[i];
            o[attributes.length + i] = new MBeanOperationInfoWrapper(opInfo,
                    this);
        }
        return o;
    }

    public int compareTo(Object object) {
        if (object instanceof MBeanInfoWrapper) {
            MBeanInfoWrapper other = (MBeanInfoWrapper) object;
            return on.toString().compareTo(other.on.toString());
        }
        return 0;
    }

    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((info == null) ? 0 : info.hashCode());
        result = PRIME * result + ((mbsc == null) ? 0 : mbsc.hashCode());
        result = PRIME * result + ((on == null) ? 0 : on.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final MBeanInfoWrapper other = (MBeanInfoWrapper) obj;
        if (info == null) {
            if (other.info != null)
                return false;
        } else if (!info.equals(other.info))
            return false;
        if (mbsc == null) {
            if (other.mbsc != null)
                return false;
        } else if (!mbsc.equals(other.mbsc))
            return false;
        if (on == null) {
            if (other.on != null)
                return false;
        } else if (!on.equals(other.on))
            return false;
        return true;
    }
}
