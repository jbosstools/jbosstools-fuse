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

import org.fusesource.ide.foundation.core.functions.Function1;
import org.fusesource.ide.foundation.ui.internal.FoundationUIActivator;


public class FormatFunctionLabelProvider extends FunctionColumnLabelProvider {
	private Format format;

	public FormatFunctionLabelProvider(Function1 function) {
		super(function);
	}


	@Override
	public String getText(Object object) {
		Object element = apply(object);
		if (element != null) {
			try {
				return getFormat().format(element);
			} catch (Exception e) {
				FoundationUIActivator.pluginLog().logWarning("Failed to format " + element
						+ " of type " + element.getClass().getName()
						+ " using formatter: " + format + ". " + e, e);
			}
		}
		return null;
	}

	public Format getFormat() {
		if (format == null) {
			format = createFormat();
		}
		return format;
	}

	public void setFormat(Format format) {
		this.format = format;
	}

	protected Format createFormat() {
		// TODO Auto-generated method stub
		return null;
	}
}