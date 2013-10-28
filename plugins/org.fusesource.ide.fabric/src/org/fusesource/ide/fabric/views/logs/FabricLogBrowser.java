/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.fabric.views.logs;

import org.fusesource.fabric.jolokia.facade.JolokiaFabricConnector;
import org.fusesource.fabric.jolokia.facade.utils.Helpers;
import org.fusesource.ide.fabric.FabricPlugin;
import org.fusesource.ide.fabric.navigator.Fabric;
import org.fusesource.insight.log.LogFilter;
import org.fusesource.insight.log.rest.LogRequest;

public class FabricLogBrowser implements ILogBrowser {
	private static final String INSIGHT_MBEAN_URL 	= "org.fusesource.insight:type=LogQuery";
	private static final String LOG_QUERY_OPERATION = "queryLogResults(org.fusesource.insight.log.LogFilter)";
	
	private final Fabric fabric;

	public FabricLogBrowser(Fabric fabric) {
		this.fabric = fabric;
	}

	@Override
	public void queryLogs(LogContext context, boolean filterChanged) {
		FabricPlugin.getLogger().debug("================ Querying logs.....");

		LogFilter logFilter = context.getLogFilter();
		LogRequest search = LogRequest.newInstance(logFilter.getAfterTimestamp());

		if (fabric.getConnector() == null) return;

		JolokiaFabricConnector connector = fabric.getConnector().getConnector();
		String result = Helpers.execCustomToJSON(connector.getJolokiaClient(), INSIGHT_MBEAN_URL, LOG_QUERY_OPERATION, logFilter);
		
		System.err.println("TODO: queryLogs(): " + result);
		
//				LogResponse result = resource.post(LogResponse.class, search);
//				if (result != null) {
//					LogResponseHits hits = result.getHits();
//					if (hits != null) {
//						List<LogResponseHit> hits2 = hits.getHits();
//						if (hits2 != null) {
//							List<LogEventBean> events = Lists.newArrayList();
//							for (LogResponseHit rh : hits2) {
//								LogEventBean event = LogEventBean.toLogEventBean(rh.getEvent());
//								if (event != null) {
//									events.add(event);
//
//									Long seq = event.getSeq();
//									if (seq != null) {
//										// TODO we should really be getting the maximum ID from the result, not from the actual found items!
//										Long maxLogSeq = logFilter.getAfterTimestamp();
//										if (maxLogSeq == null || seq > maxLogSeq) {
//											logFilter.setAfterTimestamp(seq);
//										}
//									}
//
//								}
//							}
//							context.addLogResults(events);
//						}
//					}
//				}
	}
}
