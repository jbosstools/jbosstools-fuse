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

package org.fusesource.ide.camel.model.service.core.jmx.camel;

/**
 * @author lhein
 *
 */
public interface CamelBrowsableEndpointMBean extends CamelEndpointMBean {

    long queueSize();

    String browseExchange(Integer index);

    String browseMessageBody(Integer index);

    /**
     * @deprecated is removed in future Camel releases
     */
    @Deprecated
    String browseMessageAsXml(Integer index);

    String browseMessageAsXml(Integer index, Boolean includeBody);

    String browseAllMessagesAsXml(Boolean includeBody);

    String browseRangeMessagesAsXml(Integer fromIndex, Integer toIndex, Boolean includeBody);

}
