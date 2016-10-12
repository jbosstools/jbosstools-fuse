/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.model.service.core.tests.integration.core.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.fusesource.ide.camel.model.service.core.catalog.CamelModel;
import org.fusesource.ide.camel.model.service.core.catalog.CamelModelFactory;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.catalog.components.Component;
import org.fusesource.ide.camel.model.service.core.io.CamelIOHandler;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.camel.model.service.core.tests.integration.core.io.FuseProject;
import org.fusesource.ide.camel.model.service.core.util.PropertiesUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class PropertiesUtilsTestQuestionMarkIT {
	
	@Rule
	public FuseProject fuseProject = new FuseProject("PropertiesUtilsTestQuestionMarkIT");

	private AbstractCamelModelElement selectedEP;
	
	@Before
	public void setup() throws CoreException{
		InputStream inputStream = PropertiesUtilsTestQuestionMarkIT.class.getClassLoader().getResourceAsStream("/withQuestionMark.xml");

		IFile fileInProject = fuseProject.getProject().getFile("withQuestionMark.xml");
		fileInProject.create(inputStream, true, new NullProgressMonitor());

		CamelFile model1 = new CamelIOHandler().loadCamelModel(fileInProject, new NullProgressMonitor());
		selectedEP = model1.getChildElements().get(0/*context*/).getChildElements().get(0/*route*/).getChildElements().get(0);
	}

	@Test
	public void testWithQuestionMark() throws Exception {
		CamelModel camelModel = CamelModelFactory.getModelForVersion(CamelModelFactory.getLatestCamelVersion());
		final Component rssComponent = camelModel.getComponentModel().getComponentForScheme("rss");
		Parameter feedUriParameter = rssComponent.getUriParameters().stream().filter(p -> "feedUri".equals(p.getName())).findFirst().get();
		
		assertThat(PropertiesUtils.getPropertyFromUri(selectedEP, feedUriParameter, rssComponent)).isEqualTo("http://my.url?with=some&param=eter");
	}
}
