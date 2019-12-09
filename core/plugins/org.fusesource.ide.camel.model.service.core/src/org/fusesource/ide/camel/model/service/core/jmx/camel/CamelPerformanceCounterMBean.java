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
public interface CamelPerformanceCounterMBean {
    String getId();

    void reset();

    long getExchangesTotal();

    long getExchangesCompleted();

    long getExchangesFailed();

    long getMinProcessingTime();

    long getMeanProcessingTime();

    long getMaxProcessingTime();

    long getTotalProcessingTime();

    long getLastProcessingTime();

    java.util.Date getLastExchangeCompletedTimestamp();

    java.util.Date getFirstExchangeCompletedTimestamp();

    java.util.Date getLastExchangeFailureTimestamp();

    java.util.Date getFirstExchangeFailureTimestamp();

    boolean isStatisticsEnabled();

    void setStatisticsEnabled(boolean statisticsEnabled);
}
