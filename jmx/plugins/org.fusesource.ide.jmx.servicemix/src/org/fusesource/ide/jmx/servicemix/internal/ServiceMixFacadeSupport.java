/*******************************************************************************
 * Copyright (c)2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.jmx.servicemix.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.QueryExp;

import org.apache.servicemix.nmr.management.ManagedEndpointMBean;

/**
 * @author lhein
 *
 */
public abstract class ServiceMixFacadeSupport implements ServiceMixFacade {
    
    protected final MBeanServerConnection mBeanServer;

    protected ServiceMixFacadeSupport(MBeanServerConnection mBeanServer) throws Exception {
        this.mBeanServer = mBeanServer;
    }

    protected MBeanServerConnection getMBeanServerConnection() throws Exception {
        return mBeanServer;
    }

    protected Set<ObjectInstance> queryNames(ObjectName name, QueryExp query) throws Exception {
        return getMBeanServerConnection().queryMBeans(name, query);
    }

    @SuppressWarnings("unchecked")
    protected Object newProxyInstance(ObjectName objectName, Class interfaceClass, boolean notificationBroadcaster) throws Exception {
        return MBeanServerInvocationHandler.newProxyInstance(getMBeanServerConnection(), objectName, interfaceClass, notificationBroadcaster);
    }


    // ServiceMixFacade
    //---------------------------------------------------------------
    public List<ManagedEndpointMBean> getEndpoints() throws Exception {
        ObjectName query = ObjectName.getInstance("org.apache.servicemix:Type=Endpoint,Id=*");

        Set<ObjectInstance> names = queryNames(query, null);
        List<ManagedEndpointMBean> answer = new ArrayList<ManagedEndpointMBean>();
        for (ObjectInstance on : names) {
            ManagedEndpointMBean component = (ManagedEndpointMBean) newProxyInstance(on.getObjectName(), ManagedEndpointMBean.class, true);
            answer.add(component);
        }
        return answer;
    }

}
