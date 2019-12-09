/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.jmx.camel;

import org.eclipse.swt.graphics.Image;
import org.jboss.tools.foundation.ui.plugin.BaseUISharedImages;
import org.osgi.framework.Bundle;

public class CamelJMXSharedImages extends BaseUISharedImages {

	public static final String CAMEL_PNG = "icons/camel.png";//$NON-NLS-1$

	private static CamelJMXSharedImages shared;
	public static CamelJMXSharedImages getDefault() {
		if( shared == null )
			shared = new CamelJMXSharedImages();
		return shared;
	}
	
	
	public CamelJMXSharedImages(Bundle pluginBundle) {
		super(pluginBundle);
		addImage(CAMEL_PNG, CAMEL_PNG);
	}
	
	private CamelJMXSharedImages() {
		this(CamelJMXPlugin.getDefault().getBundle());
	}

	public static Image getImage(String k) {
		return getDefault().image(k);
	}
}
