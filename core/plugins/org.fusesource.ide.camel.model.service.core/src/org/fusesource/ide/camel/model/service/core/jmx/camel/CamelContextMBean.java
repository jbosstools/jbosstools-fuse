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

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author lhein
 *
 */
public interface CamelContextMBean {

    String getId();
    String getCamelId();

    String getManagementName();
    
    String getCamelVersion();
    
    String getState();
    
    String getUptime();
    
    Map<String,String> getProperties();
    
    Boolean getTracing();
    void setTracing(java.lang.Boolean tracing);
    
    Integer getInflightExchanges();
    
    void setTimeout(long timeout);
    long getTimeout();
    
    void setTimeUnit(TimeUnit timeUnit);
    TimeUnit getTimeUnit();
    
    void setShutdownNowOnTimeout(boolean shutdownNowOnTimeout);
    boolean isShutdownNowOnTimeout();
    
    void start();
    void stop();
    void suspend();
    void resume();
    
    void sendBody(String endpointUri, Object body);
    void sendStringBody(String endpointUri, String body);
    void sendBodyAndHeaders(String endpointUri, Object body, java.util.Map<String, Object> headers);
    Object requestBody(String endpointUri, Object body);
    Object requestStringBody(String endpointUri, String body);
    Object requestBodyAndHeaders(String endpointUri, Object body, Map<String, Object> headers);
    
    String dumpRoutesAsXml();
    void addOrUpdateRoutesFromXml(String xml);
    String dumpRoutesStatsAsXml(boolean fullStats, boolean includeProcessors);

    boolean createEndpoint(String uri);
    int removeEndpoints(String pattern);
}
