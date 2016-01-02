/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.editor.navigator;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.fusesource.ide.camel.model.AbstractNode;

public class CamelCtxNavLabelProvider extends LabelProvider {

	@Override
	public Image getImage(Object element) {
		if( element instanceof AbstractNode) {
			return ((AbstractNode)element).getSmallImage();
		} else if (element instanceof CamelCtxNavRouteNode) {
			return ((CamelCtxNavRouteNode) element).getCamelRoute().getSmallImage();
		}
		return null;
	}

	@Override
	public String getText(Object element) {
		return element!=null?element.toString():null;
	}

}
