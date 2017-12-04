/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.projecttemplates.impl.simple;

import org.fusesource.ide.projecttemplates.adopters.configurators.MavenTemplateConfigurator;
import org.fusesource.ide.projecttemplates.adopters.configurators.TemplateConfiguratorSupport;
import org.fusesource.ide.projecttemplates.adopters.creators.TemplateCreatorSupport;
import org.fusesource.ide.projecttemplates.util.NewProjectMetaData;

public class EAPSpringTemplateForFuse7 extends AbstractEAPSpringTemplate {

	@Override
	public TemplateCreatorSupport getCreator(NewProjectMetaData projectMetaData) {
		return new EAPSpringUnzipTemplateCreator("7");
	}

	@Override
	public boolean isCompatible(String camelVersion) {
		return !isStrictlyLowerThan2200(camelVersion);
	}
	
	@Override
	public TemplateConfiguratorSupport getConfigurator() {
		return new MavenTemplateConfigurator(getBomVersion("org.wildfly.camel", "wildfly-camel-bom"));
	}
}
