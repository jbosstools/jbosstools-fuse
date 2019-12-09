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

import java.util.Date;

import org.eclipse.jface.viewers.ViewerCell;
import org.fusesource.ide.foundation.core.util.Strings;


public class LongTimestampAsTimeThenDateLabelProvider extends TimeThenDateLabelProvider {

	@Override
	protected Object convertValue(ViewerCell cell) {
		String text = cell.getText();
		if (Strings.isBlank(text)) {
			return null;
		}
		long n = Long.parseLong(text);
		return new Date(n);
	}

}
