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
package org.fusesource.ide.projecttemplates.impl.medium;

import org.fusesource.ide.projecttemplates.adopters.AbstractProjectTemplate;
import org.fusesource.ide.projecttemplates.adopters.creators.DSLDependentUnzipStreamCreator;
import org.fusesource.ide.projecttemplates.adopters.util.CamelDSLType;

public abstract class AbstractCxfCodeFirstProjectTemplate extends AbstractProjectTemplate {

	public AbstractCxfCodeFirstProjectTemplate() {
		super();
	}

	@Override
	public boolean supportsDSL(CamelDSLType type) {
		switch (type) {
		case BLUEPRINT:	return false;
		case SPRING:	return true;
		case JAVA:		return true;
		default:		return false;
		}	
	}

	protected class CXfCodeFirstUnzipTemplateCreator extends DSLDependentUnzipStreamCreator {

		private static final String TEMPLATE_BLUEPRINT = "template-medium-cxf-codefirst-blueprint-fuse";
		private static final String TEMPLATE_SPRING = "template-medium-cxf-codefirst-spring-fuse";
		private static final String TEMPLATE_JAVA = "template-medium-cxf-codefirst-java-fuse";

		public CXfCodeFirstUnzipTemplateCreator(String suffix) {
			super(TEMPLATE_BLUEPRINT, TEMPLATE_SPRING, TEMPLATE_JAVA, suffix);
		}

	}

}