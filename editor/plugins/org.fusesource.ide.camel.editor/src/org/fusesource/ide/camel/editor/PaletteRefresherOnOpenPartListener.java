/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.camel.editor;

import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;

/**
 * @author Aurelien Pupier
 * Used to fix FUSETOOLS-1661 waiting for general refactor handled by FUSETOOLS-2110
 */
class PaletteRefresherOnOpenPartListener implements IPartListener2 {
	
	private CamelDesignEditor camelDesignEditor;
	
	public PaletteRefresherOnOpenPartListener(CamelDesignEditor camelDesignEditor) {
		this.camelDesignEditor = camelDesignEditor;
	}
	
	@Override
	public void partVisible(IWorkbenchPartReference partRef) {
	}

	@Override
	public void partOpened(IWorkbenchPartReference partRef) {
		IWorkbenchPart openedPart = partRef.getPart(false);
		if(openedPart instanceof CamelEditor && camelDesignEditor.equals(((CamelEditor) openedPart).getDesignEditor())){
			camelDesignEditor.getDiagramBehavior().refreshPalette();
		}
	}

	@Override
	public void partInputChanged(IWorkbenchPartReference partRef) {
	}

	@Override
	public void partHidden(IWorkbenchPartReference partRef) {
	}

	@Override
	public void partDeactivated(IWorkbenchPartReference partRef) {
	}

	@Override
	public void partClosed(IWorkbenchPartReference partRef) {
	}

	@Override
	public void partBroughtToTop(IWorkbenchPartReference partRef) {
	}

	@Override
	public void partActivated(IWorkbenchPartReference partRef) {
	}
}