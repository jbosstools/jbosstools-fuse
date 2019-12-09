/*******************************************************************************
 * Copyright (c)2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.jmx.activemq.internal;

import org.apache.activemq.broker.jmx.QueueViewMBean;

/**
 * @author lhein
 *
 */
public interface QueueViewFacade extends QueueViewMBean {

    /**
     * @return a unique id for this resource, typically a JMX ObjectName
     * @throws Exception
     */
    String getId() throws Exception;

}
