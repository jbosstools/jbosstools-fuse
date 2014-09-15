/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.jmx.camel.local;

import org.eclipse.swt.graphics.Image;
import org.fusesource.ide.jmx.camel.CamelJMXPlugin;
import org.fusesource.ide.jmx.camel.CamelJMXSharedImages;
import org.jboss.tools.jmx.jvmmonitor.core.IActiveJvm;
import org.jboss.tools.jmx.local.ui.JVMLabelProviderDelegate;

public class CamelContextLabelProvider implements JVMLabelProviderDelegate {
	public boolean accepts(IActiveJvm jvm) {
		return jvm.getMainClass().endsWith("org.apache.camel:camel-maven-plugin:run");
	}
	public Image getImage(IActiveJvm jvm) {
		return CamelJMXPlugin.getDefault().getSharedImages().image(CamelJMXSharedImages.CAMEL_PNG);
	}
	public String getDisplayString(IActiveJvm jvm) {
		return "Local Camel Context";
	}
}