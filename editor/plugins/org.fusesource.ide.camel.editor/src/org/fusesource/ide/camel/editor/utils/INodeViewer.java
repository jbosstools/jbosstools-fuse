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

package org.fusesource.ide.camel.editor.utils;

import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;

public interface INodeViewer {

	AbstractCamelModelElement getSelectedNode();

	void setSelectedNode(AbstractCamelModelElement newSelection);
}
