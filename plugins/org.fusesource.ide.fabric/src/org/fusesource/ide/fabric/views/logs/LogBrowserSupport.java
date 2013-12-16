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

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import io.fabric8.jolokia.facade.JolokiaFabricConnector;
import io.fabric8.jolokia.facade.utils.Helpers;
import org.fusesource.ide.fabric.FabricPlugin;
import org.fusesource.insight.log.LogEvent;
import org.fusesource.insight.log.LogFilter;
import org.fusesource.insight.log.LogResults;

import com.google.common.collect.Lists;

public abstract class LogBrowserSupport implements ILogBrowser {
	private static final String INSIGHT_MBEAN_URL 	= "org.fusesource.insight:type=LogQuery";
	private static final String LOG_QUERY_OPERATION = "jsonQueryLogResults(java.lang.String)";
		
	private ObjectMapper mapper = new ObjectMapper();

	public LogBrowserSupport() {
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	public ObjectMapper getMapper() {
		return mapper;
	}

	@Override
	public void queryLogs(final LogContext context, boolean filterChanged) throws IOException {
		final LogFilter logFilter = context.getLogFilter();
		final String queryJson = JsonHelper.toJSON(mapper, logFilter);
		FabricPlugin.getLogger().debug("Query JSON: " + queryJson);

		String json = filterLogEvents(queryJson);
		FabricPlugin.getLogger().debug("===== JSON: " + json);
		
		List<LogEventBean> answer = Lists.newArrayList();
		if (json != null) {
			json = json.trim();
			if (!json.equals("[]") && json.length() > 2) {
				LogResults results = mapper.reader(LogResults.class).readValue(json);
				if (results != null) {
					Long to = results.getToTimestamp();
					if (to != null) {
						logFilter.setAfterTimestamp(to);
					}
					List<LogEvent> events = results.getEvents();
					if (events != null) {
						for (LogEvent event : events) {
							if (event.getHost() == null) {
								event.setHost(results.getHost());
							}
							answer.add(LogEventBean.toLogEventBean(event));
						}
					}
				}
			}
		}
		context.addLogResults(answer);
	}

	protected abstract String getJolokiaUser();
	
	protected abstract String getJolokiaPassword();
	
	protected abstract String getJolokiaUrl();
	
	private String filterLogEvents(String queryJSON) {
		String result = null;
		String jUser = getJolokiaUser();
		
		if (jUser.isEmpty())
		    return "";
		
		JolokiaFabricConnector connector = JolokiaFabricConnector.getFabricConnector(jUser, getJolokiaPassword(), getJolokiaUrl());
		result = Helpers.execCustomToJSON(connector.getJolokiaClient(),INSIGHT_MBEAN_URL, LOG_QUERY_OPERATION, queryJSON);
		
		return result;
	}
}
