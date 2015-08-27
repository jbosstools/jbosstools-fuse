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

package org.fusesource.ide.jmx.camel.navigator;

import java.util.List;

import org.fusesource.ide.camel.model.Activator;
import org.fusesource.ide.foundation.core.util.Objects;
import org.fusesource.ide.jmx.camel.internal.CamelFacade;
import org.fusesource.ide.jmx.camel.internal.CamelProcessorMBean;


public class CamelFacades {

	public static CamelProcessorMBean getProcessorMBean(CamelFacade camelFacade, String camelContextId, String nodeId) {
		try {
			List<CamelProcessorMBean> processors = camelFacade.getProcessors(camelContextId);
			for (CamelProcessorMBean processorMBean : processors) {
				String processorId = processorMBean.getProcessorId();
				if (Objects.equal(nodeId, processorId)) {
					return processorMBean;
				}
			}
		} catch (Exception e) {
			Activator.getLogger().warning(
					"Failed to find statistics for node: " + nodeId + " in camelContext: " + camelContextId + ". " + e,
					e);
		}
		return null;
	}

}
