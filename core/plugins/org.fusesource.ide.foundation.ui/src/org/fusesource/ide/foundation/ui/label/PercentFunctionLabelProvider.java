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

import java.text.Format;
import java.text.NumberFormat;

import org.fusesource.ide.foundation.core.functions.Function1;


public class PercentFunctionLabelProvider extends FormatFunctionLabelProvider {

	public PercentFunctionLabelProvider(Function1 function) {
		super(function);
	}

	@Override
	protected Format createFormat() {
		return NumberFormat.getPercentInstance();
	}


}
