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

import javax.management.openmbean.CompositeData;

/**
 * @author lhein
 *
 */
public class JobFacade {
	
    private final CompositeData data;
    
    public JobFacade(CompositeData data) {
        this.data = data;
    }
    
    public String getCronEntry() {
        return data.get("cronEntry").toString();
    }

    public String getJobId() {
        return toString(data.get("jobId"));
    }

    public String getNextExecutionTime() {
        return toString(data.get("next"));
    }
    
    public long getDelay() {
        Long result = (Long) data.get("delay");
        if (result != null) {
            return result.longValue();
        }
        return 0l;
    }

    public long getPeriod() {
        Long result = (Long) data.get("period");
        if (result != null) {
            return result.longValue();
        }
        return 0l;
    }

    public int getRepeat() {
        Integer result = (Integer) data.get("repeat");
        if (result != null) {
            return result.intValue();
        }
        return 0;
    }

    public String getStart() {
        return toString(data.get("start"));
    }

    private String toString(Object object) {
        return object != null ? object.toString() : "";
    }
}
