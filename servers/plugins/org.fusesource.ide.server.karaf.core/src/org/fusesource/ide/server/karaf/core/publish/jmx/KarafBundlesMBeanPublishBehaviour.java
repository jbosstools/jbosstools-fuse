/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.server.karaf.core.publish.jmx;

import java.io.IOException;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.openmbean.TabularData;

/**
 * publisher using the org.apache.karaf:type=bundles mbean
 * 
 * @author lhein
 */
public class KarafBundlesMBeanPublishBehaviour extends
		KarafBundleMBeanPublishBehaviour {
	
	private static final String KARAF_BUNDLES_MBEAN = "org.apache.karaf:type=bundles,*";
	
	@Override
	protected TabularData getTabularData(MBeanServerConnection mbsc)
			throws InstanceNotFoundException, MBeanException, ReflectionException, IOException {
		return (TabularData)mbsc.invoke(this.objectName, "list", null, null);
	}

	@Override
	protected ObjectName getQueryObjectName() throws MalformedObjectNameException {
		return new ObjectName(KARAF_BUNDLES_MBEAN);
	}
}
