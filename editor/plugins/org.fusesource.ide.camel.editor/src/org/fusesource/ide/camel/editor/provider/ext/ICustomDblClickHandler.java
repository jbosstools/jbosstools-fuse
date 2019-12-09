/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.camel.editor.provider.ext;

import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;

/**
 * @author lhein
 */
public interface ICustomDblClickHandler {
	
	/**
	 * this method can be used to signal your handler is capable of 
	 * handling clicks to the specified node
	 * 
	 * @param clickedNode	the node which has been dbl clicked
	 * @return	true if your handler can handle the dbl click on that node, otherwise false
	 */
	boolean canHandle(AbstractCamelModelElement clickedNode);
	
	/**
	 * use this method to do whatever action you want to happen when dbl clicking the node
	 * 
	 * @param clickedNode	the node which has been dbl clicked
	 */
	void handleDoubleClick(AbstractCamelModelElement clickedNode);
}
