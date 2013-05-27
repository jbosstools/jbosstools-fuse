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

package org.fusesource.ide.camel.editor.handlers;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.fusesource.ide.camel.editor.Activator;
import org.fusesource.ide.camel.editor.EditorMessages;


/**
 * @author lhein
 */
public class AutoLayoutAction extends Action {
	
	public static final String AUTO_LAYOUT_COMMAND_ID = "org.fusesource.ide.camel.editor.commands.autoLayoutCommand";
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		IHandlerService handlerService = (IHandlerService)PlatformUI.getWorkbench().getService(IHandlerService.class);
		try {
			handlerService.executeCommand(AUTO_LAYOUT_COMMAND_ID, null);
		} catch (Exception ex) {
			Activator.getLogger().error(ex);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#getId()
	 */
	@Override
	public String getId() {
		return AUTO_LAYOUT_COMMAND_ID;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#getImageDescriptor()
	 */
	@Override
	public ImageDescriptor getImageDescriptor() {
		return Activator.getDefault().getImageDescriptor("layout.png");
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#getDescription()
	 */
	@Override
	public String getDescription() {
		return EditorMessages.autoLayoutActionDescription;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#getText()
	 */
	@Override
	public String getText() {
		return EditorMessages.autoLayoutActionLabel;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#getToolTipText()
	 */
	@Override
	public String getToolTipText() {
		return EditorMessages.autoLayoutActionTooltip;
	}
}
