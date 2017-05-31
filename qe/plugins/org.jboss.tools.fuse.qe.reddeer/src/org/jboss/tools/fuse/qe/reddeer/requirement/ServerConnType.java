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

public enum ServerConnType {

	LOCAL, REMOTE, ANY;

	public boolean matches(ServerBase serverBase) {
		boolean isRemote = serverBase.isRemote();
		switch (this) {
		case LOCAL:
			return !isRemote;
		case REMOTE:
			return isRemote;
		default:
			return true;
		}
	}

}
