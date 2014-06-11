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

package org.fusesource.ide.jmx.ui.internal.editors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.internal.Workbench;
import org.fusesource.ide.jmx.core.ExtensionManager;
import org.fusesource.ide.jmx.core.IConnectionProviderListener;
import org.fusesource.ide.jmx.core.IConnectionWrapper;


public class EditorConnectionMapping implements IConnectionProviderListener {	
	private HashMap<IConnectionWrapper, ArrayList<IEditorPart>> map;
	private IWorkbenchPage page;
	public EditorConnectionMapping() {
		this.map = new HashMap<IConnectionWrapper, ArrayList<IEditorPart>>();
		page = Workbench.getInstance().getActiveWorkbenchWindow().getActivePage();
		ExtensionManager.addConnectionProviderListener(this);
	}

	public void open(IConnectionWrapper wrapper, IEditorPart editor) {
		ArrayList<IEditorPart> list = map.get(wrapper);
		if( list == null ) {
			list = new ArrayList<IEditorPart>();
			map.put(wrapper, list);
		}
		if( !list.contains(editor))
			list.add(editor);
	}
	
	public void close(final IConnectionWrapper wrapper) {
		Display.getDefault().asyncExec(new Runnable() { 
			public void run() {
				ArrayList<IEditorPart> list = map.get(wrapper);
				if( list != null ) {
					Iterator<IEditorPart> i = list.iterator();
					IEditorPart ep;
					while(i.hasNext()) {
						ep = i.next();
						page.closeEditor(ep, false);
					}
					map.remove(wrapper);
				}
			}
		});
	}

	public void connectionChanged(IConnectionWrapper connection) {
		if( !connection.isConnected() ) {
			close(connection);
		}
	}

	public void connectionAdded(IConnectionWrapper connection) {
		// do nothing
	}
	public void connectionRemoved(IConnectionWrapper connection) {
		if( !connection.isConnected() ) {
			close(connection);
		}
	}
}
