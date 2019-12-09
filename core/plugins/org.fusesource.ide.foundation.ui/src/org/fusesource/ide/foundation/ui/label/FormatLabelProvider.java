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

import java.text.DateFormat;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.fusesource.ide.foundation.ui.internal.FoundationUIActivator;


public class FormatLabelProvider extends StyledCellLabelProvider {
	private DateFormat format;

	@Override
	public void update(ViewerCell cell) {
		Object element = cell.getElement();
		if (element != null) {
			try {
				DateFormat f = getFormat();
				if (f != null) {
					String text = "";
					Object convertValue = convertValue(cell);
					if (convertValue != null) {
						text = f.format(convertValue);
					}
					cell.setText(text);
				}
			} catch (Exception e) {
				FoundationUIActivator.pluginLog().logWarning("Failed to format " + element
						+ " of type " + element.getClass().getName()
						+ " using formatter: " + format + ". " + e, e);
			}
		}
		super.update(cell);
	}

	public DateFormat getFormat() {
		if (format == null) {
			format = createFormat();
		}
		return format;
	}

	public void setFormat(DateFormat format) {
		this.format = format;
	}

	protected DateFormat createFormat() {
		return null;
	}


	/**
	 * Strategy method to allow derived classes to convert the value
	 */
	protected Object convertValue(ViewerCell cell) {
		return cell.getText();
	}
}