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

package org.fusesource.ide.deployment.handler;

import org.fusesource.ide.deployment.config.HotfolderDeploymentConfiguration;

public class DeployInNamedContainerAction extends ExecutePomAction {

	private final HotfolderDeploymentConfiguration cfg;

	public DeployInNamedContainerAction(HotfolderDeploymentConfiguration cfg) {
		this.cfg = cfg;
	}

	@Override
	protected HotfolderDeploymentConfiguration getHotfolderConfiguration() {
		return cfg;
	}

}
