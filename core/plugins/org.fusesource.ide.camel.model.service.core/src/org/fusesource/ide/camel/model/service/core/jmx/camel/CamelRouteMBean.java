/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 

package org.fusesource.ide.camel.model.service.core.jmx.camel;

/**
 * @author lhein
 *
 */
public interface CamelRouteMBean extends CamelPerformanceCounterMBean {
    String getRouteId();

    String getDescription();

    String getEndpointUri();

    String getState();

    Integer getInflightExchanges();

    String getCamelId();

    Boolean getTracing();

    void setTracing(java.lang.Boolean tracing);

    String getRoutePolicyList();

    void start();

    void stop();

    void stop(long timeout);

    boolean stop(Long timeout, Boolean abortAfterTimeout);

    boolean remove();

    String dumpRouteAsXml();

    void updateRouteFromXml(java.lang.String xml);

    String dumpRouteStatsAsXml(boolean fullStats, boolean includeProcessors);
}
