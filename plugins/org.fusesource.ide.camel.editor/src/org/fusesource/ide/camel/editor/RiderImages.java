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

package org.fusesource.ide.camel.editor;

import org.eclipse.jface.resource.ImageDescriptor;

public class RiderImages {

	public static final ImageDescriptor REFRESH = createImageDescriptor("refresh.gif");

	private static ImageDescriptor createImageDescriptor(String key) {
		return Activator.getDefault().getImageDescriptor(key);
	}
}
