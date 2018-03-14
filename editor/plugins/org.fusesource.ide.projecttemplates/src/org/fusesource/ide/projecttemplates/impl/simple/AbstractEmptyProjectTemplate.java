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

import org.fusesource.ide.projecttemplates.adopters.AbstractProjectTemplate;
import org.fusesource.ide.projecttemplates.adopters.creators.DSLDependentUnzipStreamCreator;
import org.fusesource.ide.projecttemplates.wizards.pages.model.EnvironmentData;
import org.fusesource.ide.projecttemplates.wizards.pages.model.FuseDeploymentPlatform;
import org.fusesource.ide.projecttemplates.wizards.pages.model.FuseRuntimeKind;

public abstract class AbstractEmptyProjectTemplate extends AbstractProjectTemplate {

	public AbstractEmptyProjectTemplate() {
		super();
	}
	
	@Override
	public boolean isCompatible(EnvironmentData environment) {
		return super.isCompatible(environment)
				&& FuseDeploymentPlatform.STANDALONE.equals(environment.getDeploymentPlatform())
				&& FuseRuntimeKind.KARAF.equals(environment.getFuseRuntime());
	}

	protected class BlankProjectCreator extends DSLDependentUnzipStreamCreator {

		private static final String TEMPLATE_BLUEPRINT = "template-blank-blueprint-fuse";
		private static final String TEMPLATE_SPRING = "template-blank-spring-fuse";
		private static final String TEMPLATE_JAVA = "template-blank-java-fuse";

		public BlankProjectCreator(String suffix) {
			super(TEMPLATE_BLUEPRINT, TEMPLATE_SPRING, TEMPLATE_JAVA, suffix);
		}

	}

}