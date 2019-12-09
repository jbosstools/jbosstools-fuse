/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.jmx.commons;

import java.io.IOException;
import java.util.Arrays;
import java.util.Hashtable;

import javax.management.JMX;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;

/**
 * @author lhein
 */
public abstract class JmxTemplateSupport {

	public interface JmxConnectorCallback<T> {
        T doWithJmxConnector(JMXConnector connector) throws Exception;
    }

	/**
	 * executes a jmx call 
	 * 
	 * @param callback
	 * @return
	 */
    public abstract <T> T execute(JmxConnectorCallback<T> callback);

    /**
     * creates the object name
     * 
     * @param domain
     * @param args
     * @return
     */
    public static ObjectName safeObjectName(String domain, String ... args) {
        if ((args.length % 2) != 0) {
             Activator.getLogger().warning(String.format("Not all values were defined for arguments %s", Arrays.toString(args)));
        }
        Hashtable<String, String> table = new Hashtable<>();
        for (int i = 0; i < args.length; i += 2) {
            table.put(args[i], args[i + 1]);
        }
        try {
            return new ObjectName(domain, table);
        } catch (MalformedObjectNameException e) {
            throw new RuntimeException("Object name is invalid", e);
        }
    }

    /**
     * returns a mbean proxy
     * 
     * @param connector
     * @param type
     * @param domain
     * @param params
     * @return
     */
    public <T> T getMBean(JMXConnector connector, Class<T> type, String domain, String ... params) {
        try {
            return JMX.newMBeanProxy(connector.getMBeanServerConnection(), safeObjectName(domain, params), type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
