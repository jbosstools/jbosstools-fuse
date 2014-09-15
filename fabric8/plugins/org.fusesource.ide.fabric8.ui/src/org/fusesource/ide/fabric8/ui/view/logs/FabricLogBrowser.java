/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.fabric8.ui.view.logs;

import java.util.ArrayList;
import java.util.List;

import org.fusesource.ide.fabric8.core.connector.Fabric8Facade;
import org.fusesource.ide.fabric8.core.dto.LogEventDTO;
import org.fusesource.ide.fabric8.core.dto.LogResultsDTO;
import org.fusesource.ide.fabric8.ui.navigator.ContainerNode;
import org.fusesource.ide.fabric8.ui.navigator.ContainersNode;
import org.fusesource.ide.fabric8.ui.navigator.Fabric;
import org.fusesource.ide.fabric8.ui.navigator.FabricNodeSupport;

/**
 * @author lhein
 */
public class FabricLogBrowser implements ILogBrowser {

	private final FabricNodeSupport node;

	public FabricLogBrowser(FabricNodeSupport node) {
		this.node = node;
	}

	@Override
	public void queryLogs(LogContext context, boolean filterChanged) {
		Fabric8Facade fabricService = getFabricService();
		if (fabricService == null) return;
		LogResultsDTO res = fabricService.queryLog(context.getLogFilter());
		
		List<LogEventDTO> logs = res.getLogEvents();
		if (this.node instanceof ContainerNode) {
			// filter container
			String containerId = ((ContainerNode)this.node).getId();
			List<LogEventDTO> unwantedEntries = new ArrayList<LogEventDTO>();
			for (LogEventDTO ev : logs) {
				if (!ev.getContainer().equalsIgnoreCase(containerId)) {
					unwantedEntries.add(ev);
				}
			}
			logs.removeAll(unwantedEntries);
		}
		
		context.addLogResults(logs);
		context.getLogFilter().setAfterTimestamp(res.getTo());	
		context.getLogFilter().setBeforeTimestamp(System.currentTimeMillis());
	}

	private Fabric8Facade getFabricService() {
		if (this.node instanceof Fabric) {
			return ((Fabric)this.node).getFabricService();
		} else if (this.node instanceof ContainersNode) {
			return ((ContainersNode)this.node).getFabric().getFabricService();
		} else if (this.node instanceof ContainerNode) {
			return ((ContainerNode)this.node).getFabricService();
		}
		return null;
	}
}
