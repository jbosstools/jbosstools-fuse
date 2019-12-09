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

import org.eclipse.jface.viewers.OwnerDrawLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.TreeItem;
import org.fusesource.ide.foundation.core.functions.Function1;


/**
 * Renders a health indicator based on the current value, min and max values showing if things are as expected,
 * under the minimum, over minimum or over maximum
 *
 */
public class HealthLabelProvider extends OwnerDrawLabelProvider {
	private final Function1 function;
	//private final ColumnViewer viewer;
	private int arcWidth = 5;
	private int arcHeight = 5;
	private int widthOffset = 0;
	private int heightOffset = 0;
	private int textWidthOffset = 2;
	private int textHeightOffset = 1;
	private Color chartColour;

	public HealthLabelProvider(Function1 function) {
		this.function = function;
	}

	@Override
	protected void measure(Event event, Object element) {
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
		List<Bar> bars = getBars(element);

		int x = bounds.width > 0 ? bounds.x + widthOffset : bounds.x;
		int y = bounds.height > 0 ? bounds.y + heightOffset : bounds.y;

		GC gc = event.gc;

		int height = bounds.height - (2 * heightOffset);

		for (Bar bar : bars) {
			int width = (int) Math.floor((bounds.width - (2 * widthOffset)) * bar.getRate());

			if (chartColour != null) {
				chartColour.dispose();
				chartColour = null;
			}

			if (chartColour == null) {
				chartColour = bar.getColour().create(gc);
			}

			gc.setForeground(chartColour);
			gc.setBackground(chartColour);
			gc.fillRoundRectangle(x, y, width, height, arcWidth, arcHeight);

			String text = bar.getText();
			if (text != null) {
				gc.setForeground(foreground);
				gc.setBackground(background);
				gc.drawText(text, x + textWidthOffset, y + textHeightOffset, true);
			}

			x += width;
		}
		chartColour.dispose();
	}

	protected List<Bar> getBars(Object element) {
		List<Bar> bars = new ArrayList<Bar>();
		Health health = getHealth(element);
		if (health != null) {
			health.addBars(bars);
		}
		return bars;
	}

	protected Health getHealth(Object element) {
		if (function != null) {
			Object value = function.apply(element);
			if (value instanceof Health) {
				return (Health) value;
			}
		}
		return null;
	}
}