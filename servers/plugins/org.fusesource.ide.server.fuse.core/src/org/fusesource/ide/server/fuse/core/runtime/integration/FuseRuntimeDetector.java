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
package org.fusesource.ide.server.fuse.core.runtime.integration;

import java.util.Arrays;

import org.fusesource.ide.server.fuse.core.bean.FuseBeanProvider;
import org.fusesource.ide.server.fuse.core.util.FuseToolingConstants;
import org.fusesource.ide.server.karaf.core.runtime.integration.KarafRuntimeDetector;
import org.jboss.ide.eclipse.as.core.server.bean.ServerBeanType;

/**
 * @author lhein
 *
 */
public class FuseRuntimeDetector extends KarafRuntimeDetector {
	

	@Override
	protected ServerBeanType[] getServerBeanTypes() {
		return new ServerBeanType[]{
				FuseBeanProvider.FUSE_6x, FuseBeanProvider.FUSE_7x
		};
	}

	@Override
	protected boolean isValidServerType(String type) {
		return Arrays.asList(FuseToolingConstants.ALL_FUSE_SERVER_TYPES).contains(type);
	}
}
