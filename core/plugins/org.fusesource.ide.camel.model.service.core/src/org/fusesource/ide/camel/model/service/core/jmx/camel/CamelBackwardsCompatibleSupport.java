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

import java.util.List;

/**
 * @author lhein
 *
 */
public class CamelBackwardsCompatibleSupport {

    /**
     * This operation is only available from Apache Camel 2.10, and Fuse Camel 2.9 onwards.
     */
    public static String dumpRoutesStatsAsXml(CamelJMXFacade facade, String contextId) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("<camelContextStat").append(String.format(" id=\"%s\"", contextId)).append(">\n");

            // gather all the routes for this CamelContext, which requires JMX
        List<CamelRouteMBean> routes = facade.getRoutes(contextId);
        List<CamelProcessorMBean> processors = facade.getProcessors(contextId);
            
        // loop the routes, and append the processor stats if needed
        sb.append("  <routeStats>\n");
        for (CamelRouteMBean route : routes) {
            sb.append("    <routeStat").append(String.format(" id=\"%s\"", route.getRouteId()));
            sb.append(" ").append(dumpStatsAsXmlAttributes(route)).append(">\n");

            // add processor details if needed
            sb.append("      <processorStats>\n");
            for (CamelProcessorMBean processor : processors) {
                // the processor must belong to this route
                if (route.getRouteId().equals(processor.getRouteId())) {
                    sb.append("        <processorStat").append(String.format(" id=\"%s\"", processor.getProcessorId()));
                    sb.append(" ").append(dumpStatsAsXmlAttributes(processor)).append("/>\n");
                }
            }
            sb.append("      </processorStats>\n");
            sb.append("    </routeStat>\n");
        }
        sb.append("  </routeStats>\n");
        sb.append("</camelContextStat>");
        return sb.toString();
    }
    
    private static String dumpStatsAsXmlAttributes(CamelPerformanceCounterMBean mbean) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("exchangesCompleted=\"%s\"", mbean.getExchangesCompleted()));
        sb.append(String.format(" exchangesFailed=\"%s\"", mbean.getExchangesFailed()));
        sb.append(String.format(" minProcessingTime=\"%s\"", mbean.getMinProcessingTime()));
        sb.append(String.format(" maxProcessingTime=\"%s\"", mbean.getMaxProcessingTime()));
        sb.append(String.format(" totalProcessingTime=\"%s\"", mbean.getTotalProcessingTime()));
        sb.append(String.format(" lastProcessingTime=\"%s\"", mbean.getLastProcessingTime()));
        sb.append(String.format(" meanProcessingTime=\"%s\"", mbean.getMeanProcessingTime()));
        return sb.toString();
    }
}
