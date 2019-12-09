/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.reddeer.editor;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitWhile;
import org.eclipse.reddeer.gef.matcher.IsEditPartWithTooltip;
import org.eclipse.reddeer.swt.condition.ShellIsAvailable;
import org.eclipse.reddeer.swt.impl.button.PushButton;
import org.eclipse.reddeer.swt.impl.menu.ContextMenuItem;
import org.eclipse.reddeer.swt.impl.shell.DefaultShell;
import org.eclipse.reddeer.workbench.core.condition.JobIsRunning;
import org.jboss.tools.fuse.reddeer.component.AbstractURICamelComponent;

/**
 * EditPart inside the CamelEditor implementation which is looking for a given label inside the edit part.
 * 
 * @author tsedmik
 */
public class CamelComponentEditPart extends FuseEditPart {

	public CamelComponentEditPart(String label) {
		this(label, 0);
	}

	public CamelComponentEditPart(String label, int index) {
		super(new IsEditPartWithLabelPrefix(label), index);
	}

	public CamelComponentEditPart(AbstractURICamelComponent uriComponent) {
		super(new IsEditPartWithTooltip(uriComponent.getUri()), 0);
	}

	public Rectangle getBounds() {
		Rectangle bounds = getFigure().getBounds();
		final Rectangle rec = bounds.getCopy();
		getFigure().translateToAbsolute(rec);
		return rec;
	}

	public void remove() {
		select();
		new ContextMenuItem("Remove").select();
	}

	public void delete() {
		getContextButton("Delete").click();
		String deleteShellText = "Confirm Delete";
		new DefaultShell(deleteShellText);
		new PushButton("Yes").click();
		new WaitWhile(new ShellIsAvailable(deleteShellText));
		new WaitWhile(new JobIsRunning(), TimePeriod.LONG);
	}
}
