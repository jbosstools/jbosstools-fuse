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

import java.util.List;

/**
 * @author lhein
 */
public interface CamelJMXFacade {
    /**
     * Gets all the CamelContexts in the JVM
     */
    List<CamelContextMBean> getCamelContexts() throws Exception;

    // -----------------------------------------------------

    /**
     * Gets the fabric tracer
     *
     * @param managementName  the camel context management name (<b>not</b> context id)
     */
    CamelFabricTracerMBean getFabricTracer(String managementName) throws Exception;

	/**
	 * Gets the camel tracer
	 *
	 * @param managementName  the camel context management name (<b>not</b> context id)
	 */
    CamelBacklogTracerMBean getCamelTracer(String managementName) throws Exception;

    /**
     * Gets the CamelContext
     *
     * @param managementName  the camel context management name (<b>not</b> context id)
     */
    CamelContextMBean getCamelContext(String managementName) throws Exception;

    /**
     * Gets all the components of the given CamelContext
     *
     * @param managementName  the camel context management name (<b>not</b> context id)
     */
    List<CamelComponentMBean> getComponents(String managementName) throws Exception;

    /**
     * Gets all the routes of the given CamelContext
     *
     * @param managementName  the camel context management name (<b>not</b> context id)
     */
    List<CamelRouteMBean> getRoutes(String managementName) throws Exception;

    /**
     * Gets all the endpoints of the given CamelContext
     *
     * @param managementName  the camel context management name (<b>not</b> context id)
     */
    List<CamelEndpointMBean> getEndpoints(String managementName) throws Exception;

    /**
     * Gets all the consumers of the given CamelContext
     *
     * @param managementName  the camel context management name (<b>not</b> context id)
     */
    List<CamelConsumerMBean> getConsumers(String managementName) throws Exception;

    /**
     * Gets all the processors of the given CamelContext
     *
     * @param managementName  the camel context management name (<b>not</b> context id)
     */
    List<CamelProcessorMBean> getProcessors(String managementName) throws Exception;

    /**
     * Gets all the thread pools created and managed by the given CamelContext
     *
     * @param managementName  the camel context management name (<b>not</b> context id)
     */
    List<CamelThreadPoolMBean> getThreadPools(String managementName) throws Exception;

    /**
     * Dumps the performance statistics of all the routes for the given CamelContext, as XML
     *
     * @param managementName  the camel context management name (<b>not</b> context id)
     */
    String dumpRoutesStatsAsXml(String managementName) throws Exception;
}
