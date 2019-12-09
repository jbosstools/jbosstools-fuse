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

package org.fusesource.ide.jmx.karaf.navigator.osgi;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.ui.views.properties.IPropertySource;
import org.fusesource.ide.jmx.karaf.KarafJMXPlugin;

public class BundleStatusLabelProvider extends StyledCellLabelProvider {

	@Override
	public void update(ViewerCell cell) {
		Object element = cell.getElement();
		if (element instanceof IPropertySource) {
			IPropertySource bean = (IPropertySource) element;
			String status = null;
			Object value = bean.getPropertyValue("State");
			if (value != null) {
				status = value.toString();
			}
			String image = null;
			if (status != null) {
				String lowerStatus = status.toLowerCase();
				if (lowerStatus.startsWith("a")) {
					image = "green-dot.png";
				} else if (lowerStatus.startsWith("inst")) {
					image = "gray-dot.png";
				} else if (lowerStatus.startsWith("res")) {
					image = "yellow-dot.png";
				} else {
					image = "red-dot.png";
				}
			}
			if (status != null) {
				Styler style = null;
				StyledString styledString = new StyledString(status, style);
				cell.setText(styledString.toString());
				cell.setStyleRanges(styledString.getStyleRanges());
			}
			if (image != null) {
				cell.setImage(KarafJMXPlugin.getDefault().getImage(image));
			}
		}
		super.update(cell);
	}





}
