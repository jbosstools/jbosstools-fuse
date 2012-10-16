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
