package org.fusesource.ide.deployment;

import org.eclipse.ui.menus.ExtensionContributionFactory;
import org.eclipse.ui.menus.IContributionRoot;
import org.eclipse.ui.services.IServiceLocator;


public class DeploymentExtensionFactory extends ExtensionContributionFactory {

	public DeploymentExtensionFactory() {
	}
	
	@Override
	public void createContributionItems(IServiceLocator serviceLocator, IContributionRoot additions) {
		DeploymentContributionItem item = new DeploymentContributionItem();
		additions.addContributionItem(item, null);
	}

}
