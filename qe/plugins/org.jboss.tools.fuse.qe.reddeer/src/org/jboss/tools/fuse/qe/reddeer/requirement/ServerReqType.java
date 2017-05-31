/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.qe.reddeer.requirement;

import org.jboss.tools.fuse.qe.reddeer.runtime.ServerBase;
import org.jboss.tools.fuse.qe.reddeer.runtime.impl.ServerAS;
import org.jboss.tools.fuse.qe.reddeer.runtime.impl.ServerEAP;
import org.jboss.tools.fuse.qe.reddeer.runtime.impl.ServerFuse;
import org.jboss.tools.fuse.qe.reddeer.runtime.impl.ServerKaraf;
import org.jboss.tools.fuse.qe.reddeer.runtime.impl.ServerWildFly;

/**
 * 
 * @author apodhrad
 * 
 */
public enum ServerReqType {

	ANY(null),
	AS(ServerAS.class),
	WildFly(ServerWildFly.class),
	EAP(ServerEAP.class),
	Karaf(ServerKaraf.class),
	Fuse(ServerFuse.class);

	private Class<?> clazz;

	private ServerReqType(Class<?> clazz) {
		this.clazz = clazz;
	}

	public boolean matches(ServerBase serverFamily) {
		if (serverFamily == null) {
			return false;
		}
		if (clazz == null) {
			return true;
		}
		return clazz.equals(serverFamily.getClass());
	}
}
