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
package org.fusesource.ide.fabric8.ui.view.logs;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.fusesource.ide.fabric8.core.dto.LogEventDTO;
import org.fusesource.ide.fabric8.ui.FabricPlugin;

public class LogLevelImageProvider extends ColumnLabelProvider {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ColumnLabelProvider#getImage(java.lang.Object)
	 */
	@Override
	public Image getImage(Object element) {
		LogEventDTO le = (LogEventDTO)element;
		if (le != null) {
			return getLevelImage(le);
		}
		return super.getImage(element);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object element) {
		LogEventDTO le = (LogEventDTO)element;
		if (le != null) {
			return le.getLogLevel();
		}
		return super.getText(element);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.CellLabelProvider#getToolTipText(java.lang.
	 * Object)
	 */
	@Override
	public String getToolTipText(Object element) {
		LogEventDTO le = (LogEventDTO)element;
		if (le != null) {
			return le.getLogMessage();
		}
		return super.getToolTipText(element);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getBackground(java.lang.Object)
	 */
	@Override
	public Color getBackground(Object element) {
		LogEventDTO le = (LogEventDTO)element;
		if (le != null) {
			return getBackgroundForLevel(le);
		}
		return super.getBackground(element);
	}
	
	private Image getLevelImage(LogEventDTO event) {
		String l = event.getLogLevel();
		if (l != null) {
			if ("INFO".equalsIgnoreCase(l)) {
				return FabricPlugin.getDefault().getImage("information.gif");
			} else if ("ERROR".equalsIgnoreCase(l)) {
				return FabricPlugin.getDefault().getImage("error.gif");
			} else if ("WARN".equalsIgnoreCase(l)) {
				return FabricPlugin.getDefault().getImage("warning.gif");
			}
		}
		return null;
	}
	
	private Color getBackgroundForLevel(LogEventDTO event) {
		String l = event.getLogLevel();
		if (l != null) {
			if ("INFO".equalsIgnoreCase(l)) {
				return Display.getDefault().getSystemColor(SWT.COLOR_WHITE);
			} else if ("ERROR".equalsIgnoreCase(l)) {
				return Display.getDefault().getSystemColor(SWT.COLOR_RED);
			} else if ("WARN".equalsIgnoreCase(l)) {
				return Display.getDefault().getSystemColor(SWT.COLOR_YELLOW);
			}
		}
		return Display.getDefault().getSystemColor(SWT.COLOR_WHITE);
	}
}
