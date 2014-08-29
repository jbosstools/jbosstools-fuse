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
package org.fusesource.ide.jmx.fabric8.navigator.properties;

import io.fabric8.insight.log.LogEvent;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.fusesource.ide.jmx.fabric8.Fabric8JMXPlugin;

public class LogLevelImageProvider extends ColumnLabelProvider {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ColumnLabelProvider#getImage(java.lang.Object)
	 */
	@Override
	public Image getImage(Object element) {
		LogEvent le = LogViewTabSection.toLogEvent(element);
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
		LogEvent le = LogViewTabSection.toLogEvent(element);
		if (le != null) {
			return le.getLevel();
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
		LogEvent le = LogViewTabSection.toLogEvent(element);
		if (le != null) {
			return le.getMessage();
		}
		return super.getToolTipText(element);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getBackground(java.lang.Object)
	 */
	@Override
	public Color getBackground(Object element) {
		LogEvent le = LogViewTabSection.toLogEvent(element);
		if (le != null) {
			return getBackgroundForLevel(le);
		}
		return super.getBackground(element);
	}
	
	private Image getLevelImage(LogEvent event) {
		String l = event.getLevel();
		if (l != null) {
			if ("INFO".equalsIgnoreCase(l)) {
				return Fabric8JMXPlugin.getDefault().getImage("information.gif");
			} else if ("ERROR".equalsIgnoreCase(l)) {
				return Fabric8JMXPlugin.getDefault().getImage("error.gif");
			} else if ("WARN".equalsIgnoreCase(l)) {
				return Fabric8JMXPlugin.getDefault().getImage("warning.gif");
			}
		}
		return null;
	}
	
	private Color getBackgroundForLevel(LogEvent event) {
		String l = event.getLevel();
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