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
package org.fusesource.ide.server.servicemix.core.bean;

import org.jboss.ide.eclipse.as.core.server.bean.IServerBeanTypeProvider;
import org.jboss.ide.eclipse.as.core.server.bean.ServerBeanType;

/**
 * @author lhein
 */
public class ServiceMixBeanProvider implements IServerBeanTypeProvider {
	
	public static final ServerBeanTypeServiceMix4x SMX_4x = new ServerBeanTypeServiceMix4x();
	public static final ServerBeanTypeServiceMix5x SMX_5x = new ServerBeanTypeServiceMix5x();
	
	/*
	 * (non-Javadoc)
	 * @see org.jboss.ide.eclipse.as.core.server.bean.IServerBeanTypeProvider#getServerBeanTypes()
	 */
	@Override
	public ServerBeanType[] getServerBeanTypes() {
		return new ServerBeanType[] { SMX_4x, SMX_5x };
	}
}
