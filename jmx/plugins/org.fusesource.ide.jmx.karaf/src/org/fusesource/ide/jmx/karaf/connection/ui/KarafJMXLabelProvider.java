/******************************************************************************* 
 * Copyright (c) 2014 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.jmx.karaf.connection.ui;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.ServerUICore;
import org.fusesource.ide.jmx.karaf.connection.KarafServerConnection;
import org.fusesource.ide.server.karaf.core.server.KarafServerDelegate;

public class KarafJMXLabelProvider extends LabelProvider {

	@Override
	public Image getImage(Object element) {
		if( element instanceof KarafServerConnection) {
			IServer s = ((KarafServerConnection)element).getServer();
			KarafServerDelegate del = (KarafServerDelegate)s.loadAdapter(KarafServerDelegate.class, new NullProgressMonitor());
			if( del != null ) {
				return ServerUICore.getLabelProvider().getImage(s);
			}
		}
		return null;
	}

	@Override
	public String getText(Object element) {
		if( element instanceof KarafServerConnection) {
			IServer s = ((KarafServerConnection)element).getServer();
			return s.getName();
		}
		return null;
	}

}
