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

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.foundation.core.functions.Function1;
import org.fusesource.ide.foundation.core.functions.Function1WithReturnType;
import org.fusesource.ide.foundation.core.util.Objects;
import org.jboss.tools.jmx.ui.ImageProvider;


public class FunctionColumnLabelProvider extends ColumnLabelProvider implements Function1WithReturnType {
	private final Function1 function;

	public FunctionColumnLabelProvider(Function1 function) {
		this.function = function;
	}

	@Override
	public String getText(Object element) {
		Object answer = apply(element);
		return Strings.getOrElse(answer);
	}


	public Function1 getFunction() {
		return function;
	}

	@Override
	public Object apply(Object element) {
		return function.apply(element);
	}

	@Override
	public Class<?> getReturnType() {
		return Objects.getReturnType(function);
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof ImageProvider) {
			ImageProvider ip = (ImageProvider) element;
			return ip.getImage();
		}
		return super.getImage(element);
	}

}