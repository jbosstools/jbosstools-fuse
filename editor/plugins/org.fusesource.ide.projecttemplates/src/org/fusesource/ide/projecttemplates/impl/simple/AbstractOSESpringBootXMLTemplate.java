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
package org.fusesource.ide.projecttemplates.impl.simple;

import org.fusesource.ide.projecttemplates.adopters.AbstractProjectTemplate;
import org.fusesource.ide.projecttemplates.adopters.creators.DSLDependentUnzipStreamCreator;
import org.fusesource.ide.projecttemplates.adopters.util.CamelDSLType;
import org.fusesource.ide.projecttemplates.wizards.pages.model.EnvironmentData;
import org.fusesource.ide.projecttemplates.wizards.pages.model.FuseRuntimeKind;

public abstract class AbstractOSESpringBootXMLTemplate extends AbstractProjectTemplate {

	@Override
	public boolean supportsDSL(CamelDSLType type) {
		return type == CamelDSLType.SPRING;
	}
	
	@Override
	public boolean isCompatible(EnvironmentData environment) {
		return super.isCompatible(environment) && FuseRuntimeKind.SPRINGBOOT.equals(environment.getFuseRuntime());
	}
	
	/**
	 * creator class for the CBR simple template 
	 */
	protected class OSEUnzipTemplateCreator extends DSLDependentUnzipStreamCreator {

		private static final String TEMPLATE_SPRING = "template-simple-ose-log-springboot-fuse";
		
		public OSEUnzipTemplateCreator(String suffix) {
			super(null, TEMPLATE_SPRING, null, suffix);
		}
		
	}
	
	@Override
	public boolean isDefault(EnvironmentData environment, CamelDSLType dslType) {
		return isCompatible(environment) && CamelDSLType.SPRING.equals(dslType);
	}

}
