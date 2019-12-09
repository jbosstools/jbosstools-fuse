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
public interface CamelScheduledPollConsumerMBean extends CamelConsumerMBean {

    long getDelay();

    void setDelay(long delay);

    long getInitialDelay();

    void setInitialDelay(long initialDelay);

    boolean isUseFixedDelay();

    void setUseFixedDelay(boolean useFixedDelay);

    String getTimeUnit();

    void setTimeUnit(String timeUnit);

}
