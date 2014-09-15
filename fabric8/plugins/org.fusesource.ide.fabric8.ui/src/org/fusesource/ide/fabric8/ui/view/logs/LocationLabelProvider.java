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

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.jface.viewers.ViewerCell;
import org.fusesource.ide.fabric8.core.dto.LogEventDTO;

public class LocationLabelProvider extends StyledCellLabelProvider {

	@Override
	public void update(ViewerCell cell) {
		Object element = cell.getElement();
		LogEventDTO log = (LogEventDTO)element;
		if (log != null) {
			String className = log.getClassName();
			
			// we don't want to display ? as location
			if (className.trim().equals("?")) className = null;
			
			if (className != null) {
				Styler style = null;
				StyledString styledString = new StyledString(className, style );

				String fileName = log.getFileName();
				if (fileName != null) {
					styledString.append(fileName, StyledString.COUNTER_STYLER);
				}
				cell.setText(styledString.toString());
				cell.setStyleRanges(styledString.getStyleRanges());
			}
		}
		super.update(cell);
	}





}
