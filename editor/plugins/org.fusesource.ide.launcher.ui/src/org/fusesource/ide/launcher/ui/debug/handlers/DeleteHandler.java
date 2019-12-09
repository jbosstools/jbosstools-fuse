/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.launcher.ui.debug.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.fusesource.ide.foundation.ui.util.Selections;
import org.fusesource.ide.launcher.debug.model.variables.CamelBodyVariable;
import org.fusesource.ide.launcher.debug.model.variables.CamelHeaderVariable;

/**
 * @author lhein
 *
 */
public class DeleteHandler extends AbstractHandler {
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection sel = HandlerUtil.getCurrentSelection(event);
		Object o = Selections.getFirstSelection(sel);
		
		if (o instanceof CamelHeaderVariable) {
			if (MessageDialog.openQuestion(HandlerUtil.getActiveShell(event), "Delete Header Variable", "Do you really want to delete the header variable?")) {
				((CamelHeaderVariable) o).delete();
			}						
		} else if (o instanceof CamelBodyVariable) {
			if (MessageDialog.openQuestion(HandlerUtil.getActiveShell(event), "Delete Message Body", "Do you really want to delete the message body?")) {
				((CamelBodyVariable) o).delete();
			}
		}
		return null;
	}
}
