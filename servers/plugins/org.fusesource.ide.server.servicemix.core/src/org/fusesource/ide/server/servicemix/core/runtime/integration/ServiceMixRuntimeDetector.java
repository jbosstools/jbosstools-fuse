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
package org.fusesource.ide.server.servicemix.core.runtime.integration;

import java.util.Arrays;

import org.fusesource.ide.server.servicemix.core.bean.ServiceMixBeanProvider;
import org.fusesource.ide.server.servicemix.core.util.IServiceMixToolingConstants;
import org.jboss.ide.eclipse.as.core.server.bean.ServerBeanType;
import org.jboss.tools.runtime.core.model.AbstractRuntimeDetectorDelegate;

/**
 * @author lhein
 *
 */
public class ServiceMixRuntimeDetector extends AbstractRuntimeDetectorDelegate {

	protected ServerBeanType[] getServerBeanTypes() {
		return new ServerBeanType[]{
				ServiceMixBeanProvider.SMX_4x, ServiceMixBeanProvider.SMX_5x
		};
	}

	protected boolean isValidServerType(String type) {
		return Arrays.asList(IServiceMixToolingConstants.ALL_SMX_SERVER_TYPES).contains(type);
	}
}
