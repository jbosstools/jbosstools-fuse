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

import javax.management.MBeanServerConnection;

/**
 * @author lhein
 *
 */
public class RemoteJMXServiceMixFacade extends ServiceMixFacadeSupport {

    public RemoteJMXServiceMixFacade(MBeanServerConnection mBeanServer) throws Exception {
        super(mBeanServer);
    }

}