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
public interface CamelThreadPoolMBean {
    String getId();

    String getSourceId();

    String getRouteId();

    String getThreadPoolProfileId();

    int getCorePoolSize();

    void setCorePoolSize(int corePoolSize);

    int getPoolSize();

    int getMaximumPoolSize();

    void setMaximumPoolSize(int maximumPoolSize);

    int getLargestPoolSize();

    int getActiveCount();

    long getTaskCount();

    long getCompletedTaskCount();

    long getTaskQueueSize();

    boolean isTaskQueueEmpty();

    int getKeepAliveTime();

    void setKeepAliveTime(int keepAliveTimeInSeconds);

    boolean isShutdown();

    void purge();
}
