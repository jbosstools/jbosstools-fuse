/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.launcher.tests.integration;

import org.eclipse.core.runtime.Plugin;

public class Activator extends Plugin {
	
	public static final String ID = "org.fusesource.ide.launcher.tests.integration";
	private static Activator instance;
	
	public Activator() {
		instance = this;
	}
	
	public static Activator getDefault() {
		return instance;
	}
}
