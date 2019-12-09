/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.jmx.commons.labelproviders;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.fusesource.ide.foundation.ui.tree.NodeSupport;
import org.jboss.tools.jmx.ui.ImageProvider;

public class NodeSupportLabelProvider extends LabelProvider {

	@Override
	public Image getImage(Object element) {
		if( element instanceof ImageProvider ) {
			return ((ImageProvider)element).getImage();
		}
		return null;
	}

	@Override
	public String getText(Object element) {
		if(element instanceof NodeSupport && ((NodeSupport) element).isConnectionAvailable()) {
			return element.toString();
		}
		return null;
	}
	
}
