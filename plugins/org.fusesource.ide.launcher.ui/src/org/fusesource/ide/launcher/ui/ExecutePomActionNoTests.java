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
package org.fusesource.ide.launcher.ui;

import org.fusesource.ide.launcher.CamelContextLaunchConfigConstants;

/**
 * @author lhein
 *
 */
public class ExecutePomActionNoTests extends ExecutePomActionSupport {

	public ExecutePomActionNoTests() {
		super(
				"org.fusesource.ide.launcher.ui.launchConfigurationTabGroup.camelContext",
				CamelContextLaunchConfigConstants.CAMEL_CONTEXT_NO_TESTS_LAUNCH_CONFIG_TYPE_ID,
				CamelContextLaunchConfigConstants.DEFAULT_MAVEN_GOALS + " -Dmaven.test.skip=true");
	}
}
