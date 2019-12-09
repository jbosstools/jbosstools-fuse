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
package org.fusesource.ide.jmx.karaf;

import org.eclipse.swt.graphics.Image;
import org.jboss.tools.foundation.ui.plugin.BaseUISharedImages;
import org.osgi.framework.Bundle;

public class KarafJMXSharedImages extends BaseUISharedImages {
	public static final String CONTAINER_PNG = "icons/container.png";//$NON-NLS-1$
	public static final String FABRIC_PNG = "icons/fabric.png";//$NON-NLS-1$
	public static final String FUSE_PNG = "icons/fuse_server.png";//$NON-NLS-1$
	public static final String MQ_PNG = "icons/mq_server.png";//$NON-NLS-1$
	public static final String SMX_PNG = "icons/smx_server.png";//$NON-NLS-1$
	public static final String KARAF_PNG = "icons/karaf.png";//$NON-NLS-1$

	private static KarafJMXSharedImages shared;
	public static KarafJMXSharedImages getDefault() {
		if( shared == null )
			shared = new KarafJMXSharedImages();
		return shared;
	}
	
	
	public KarafJMXSharedImages(Bundle pluginBundle) {
		super(pluginBundle);
		addImage(CONTAINER_PNG, CONTAINER_PNG);
		addImage(FABRIC_PNG, FABRIC_PNG);
		addImage(FUSE_PNG, FUSE_PNG);
		addImage(MQ_PNG, MQ_PNG);
		addImage(SMX_PNG, SMX_PNG);
		addImage(KARAF_PNG, KARAF_PNG);
	}
	
	private KarafJMXSharedImages() {
		this(KarafJMXPlugin.getDefault().getBundle());
	}

	public static Image getImage(String k) {
		return getDefault().image(k);
	}
}
