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

package org.fusesource.ide.foundation.ui.drop;

import org.eclipse.swt.dnd.DropTargetEvent;

/**
 * A factory of {@link DropHandler} objects for dealing with a drag drop
 */
public interface DropHandlerFactory {
	
	DropHandler createDropHandler(DropTargetEvent event);

}
