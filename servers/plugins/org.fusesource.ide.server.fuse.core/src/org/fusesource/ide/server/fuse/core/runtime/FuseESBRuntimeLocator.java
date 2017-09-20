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

package org.fusesource.ide.server.fuse.core.runtime;

import org.fusesource.ide.server.fuse.core.bean.FuseBeanProvider;
import org.fusesource.ide.server.karaf.core.runtime.KarafRuntimeLocator;
import org.jboss.ide.eclipse.as.core.server.bean.ServerBean;
import org.jboss.ide.eclipse.as.core.server.bean.ServerBeanType;


/**
 * @author lhein
 */
public class FuseESBRuntimeLocator extends KarafRuntimeLocator {
	
	@Override
	protected boolean isValidServerBeanType(ServerBean sb) {
		if (sb != null) {
			ServerBeanType type = sb.getBeanType();
			return FuseBeanProvider.FUSE_6x.equals(type) ||
					FuseBeanProvider.FUSE_7x.equals(type);
		}
		return false;
	}
}
