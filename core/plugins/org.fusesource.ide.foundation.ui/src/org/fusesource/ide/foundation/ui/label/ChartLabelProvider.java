/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.foundation.ui.label;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.OwnerDrawLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.TreeItem;
import org.fusesource.ide.foundation.core.functions.Function1;
import org.fusesource.ide.preferences.PreferenceManager;
import org.fusesource.ide.preferences.PreferencesConstants;


public class ChartLabelProvider extends OwnerDrawLabelProvider implements WrappedCellLabelProvider {
	private int arcWidth = 5;
	private int arcHeight = 5;
	private int widthOffset = 0;
	private int heightOffset = 0;
	private int textWidthOffset = 2;
	private int textHeightOffset = 1;
	private Color chartColour;
	private final CellLabelProvider labelProvider;
	private final ColumnViewer viewer;

	public ChartLabelProvider(CellLabelProvider labelProvider, ColumnViewer viewer) {
		this.labelProvider = labelProvider;
		this.viewer = viewer;
	}

	@Override
	public CellLabelProvider getWrappedLabelProvider() {
		return labelProvider;
	}

	@Override
	protected void measure(Event event, Object element) {
	}

	protected double maximumValue(Function1 fn) {
		// TODO could we cache this value for a given fn within a small range of time?
		// as we re-evaluate it for every cell!
		double maximum = 0;
		boolean first = true;
		boolean allSame = true;
		IContentProvider contentProvider = viewer.getContentProvider();
		if (contentProvider instanceof IStructuredContentProvider) {
			Object[] elements = getElements(contentProvider);
			if (elements != null) {
				for (Object object : elements) {
					Object value = fn.apply(object);
					double n = doubleValue(value);
					if (first) {
						maximum = n;
						first = false;
					} else {
						if (n != maximum) {
							allSame = false;
						}
						if (n > maximum) {
							maximum = n;
						}
					}
				}
			}
		}
		if (allSame) {
			return 0;
		}
		return maximum;
	}

	protected Object[] getElements(IContentProvider contentProvider) {
		IStructuredContentProvider scp = (IStructuredContentProvider) contentProvider;
		Object[] elements = scp.getElements(viewer.getInput());
		if (scp instanceof ITreeContentProvider) {
			ITreeContentProvider tcp = (ITreeContentProvider) scp;
			List<Object> list = new ArrayList<>();
			appendChildren(list, tcp, elements);
			return list.toArray();
		}
		return elements;
	}

	protected void appendChildren(List<Object> list, ITreeContentProvider tcp, Object[] elements) {
		for (Object object : elements) {
			list.add(object);
			Object[] children = tcp.getChildren(object);
			if (children != null && children.length > 0) {
				appendChildren(list, tcp, children);
			}
		}

	}

	public static double doubleValue(Object value) {
		if (value instanceof Number) {
			Number n = (Number) value;
			return n.doubleValue();
		}
		return 0;
	}


	@Override
	protected void paint(Event event, Object element) {
		Rectangle bounds;
		Color foreground;
		Color background;
		if (event.item instanceof TableItem) {
			TableItem item = (TableItem) event.item;
			bounds = item.getBounds(event.index);
			foreground = item.getForeground();
			background = item.getBackground();
		} else if (event.item instanceof TreeItem) {
			TreeItem item = (TreeItem) event.item;
			bounds = item.getBounds(event.index);
			foreground = item.getForeground();
			background = item.getBackground();
		} else {
			return;
		}
		String text = "";
		double percent = 0;
		if (labelProvider instanceof Function1) {
			Function1 fn = (Function1) labelProvider;
			Object value = fn.apply(element);
			if (value != null) {
				text = value.toString();
				double d = doubleValue(value);

				// lets find the maximum value
				if (d != 0.0) {
					double max = maximumValue(fn);
					if (max > 0) {
						percent = d / max;
					}
				}
			}
		}

		// lets find the min, max, start, finish values
		// so we can draw either a simple bar chart
		// or show a timeline style view

		// TODO colour the bar based on if its 'big' or not.
		int x = bounds.width > 0 ? bounds.x + widthOffset : bounds.x;
		int y = bounds.height > 0 ? bounds.y + heightOffset : bounds.y;

		GC gc = event.gc;

		int width = (int) Math.floor((bounds.width - (2 * widthOffset)) * percent);
		int height = bounds.height - (2 * heightOffset);

		if (chartColour != null) {
			chartColour.dispose();
			chartColour = null;
		}
		
		if (chartColour == null) {
			//int c = SWT.COLOR_YELLOW;
			String colorString = PreferenceManager.getInstance().loadPreferenceAsString(PreferencesConstants.EDITOR_TABLE_CHART_BG_COLOR);
			int r = 0, g = 0, b = 0;
			StringTokenizer strTok = new StringTokenizer(colorString, ",");
			int i = 0;
			while (strTok.hasMoreTokens()) {
				String tok = strTok.nextToken();
				switch (i) {
					case 0: 	r = Integer.parseInt(tok);
								break;
					case 1:		g = Integer.parseInt(tok);
								break;
					case 2:		b = Integer.parseInt(tok);
								break;
					default:	// do nothing
				}
				i++;
			}
			chartColour = new Color(gc.getDevice(), r, g, b);
		}
		
		gc.setForeground(chartColour);
		gc.setBackground(chartColour);
		gc.fillRoundRectangle(x, y, width, height, arcWidth, arcHeight);

		gc.setForeground(foreground);
		gc.setBackground(background);
		gc.drawText(text, x + textWidthOffset, y + textHeightOffset, true);
		
		chartColour.dispose();
	}
}