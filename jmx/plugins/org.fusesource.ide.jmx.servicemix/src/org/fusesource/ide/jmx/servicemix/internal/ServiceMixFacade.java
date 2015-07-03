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

import java.util.List;
import org.apache.servicemix.nmr.management.ManagedEndpointMBean;

/**
 * @author lhein
 *
 */
public interface ServiceMixFacade {

    /**
     * Gets all the ServiceMixContexts in the JVM
     */
    List<ManagedEndpointMBean> getEndpoints() throws Exception;
}
