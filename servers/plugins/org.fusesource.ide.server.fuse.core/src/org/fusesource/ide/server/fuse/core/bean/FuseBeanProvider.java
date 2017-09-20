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
package org.fusesource.ide.server.fuse.core.bean;

import org.jboss.ide.eclipse.as.core.server.bean.IServerBeanTypeProvider;
import org.jboss.ide.eclipse.as.core.server.bean.ServerBeanType;

/**
 * @author lhein
 */
public class FuseBeanProvider implements IServerBeanTypeProvider {
	
	public static final ServerBeanTypeFuse6x FUSE_6x = new ServerBeanTypeFuse6x();
	public static final ServerBeanTypeFuse7x FUSE_7x = new ServerBeanTypeFuse7x();
	
	/*
	 * (non-Javadoc)
	 * @see org.jboss.ide.eclipse.as.core.server.bean.IServerBeanTypeProvider#getServerBeanTypes()
	 */
	@Override
	public ServerBeanType[] getServerBeanTypes() {
		return new ServerBeanType[] { FUSE_6x, FUSE_7x };
	}
}
