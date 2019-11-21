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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.map.ObservableMap;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelCatalogCacheManager;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelModel;
import org.fusesource.ide.camel.model.service.core.catalog.components.Component;
import org.fusesource.ide.camel.model.service.core.io.CamelIOHandler;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.camel.model.service.core.tests.integration.core.io.FuseProject;
import org.fusesource.ide.camel.model.service.core.util.PropertiesUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class PropertiesUtilsForPathParameterCheckIT {
	
	@Rule
	public FuseProject fuseProject = new FuseProject("PropertiesUtilsTestQuestionMarkIT");

	@Before
	public void setup() throws CoreException{

	}

	@Test
	public void testWithQuestionMark() throws Exception {
		AbstractCamelModelElement selectedEP = importModel("withQuestionMark.xml");
		
		CamelModel camelModel = CamelCatalogCacheManager.getInstance().getCamelModelForProject(fuseProject.getProject());
		final Component rssComponent = camelModel.getComponentForScheme("rss");
		Parameter feedUriParameter = rssComponent.getParameters().stream().filter(p -> "feedUri".equals(p.getName())).findFirst().get();
		assertThat(PropertiesUtils.getPropertyFromUri(selectedEP, feedUriParameter, rssComponent)).isEqualTo("http://my.url?with=some&param=eter");
	}

	private AbstractCamelModelElement importModel(String fileName) throws CoreException {
		InputStream inputStream = PropertiesUtilsForPathParameterCheckIT.class.getClassLoader().getResourceAsStream("/"+fileName);

		IFile fileInProject = fuseProject.getProject().getFile(fileName);
		fileInProject.create(inputStream, true, new NullProgressMonitor());

		CamelFile model1 = new CamelIOHandler().loadCamelModel(fileInProject, new NullProgressMonitor());
		AbstractCamelModelElement selectedEP = model1.getChildElements().get(0/*context*/).getChildElements().get(0/*route*/).getChildElements().get(0);
		return selectedEP;
	}
	
	@Test
	public void testSeveralURiPaths() throws Exception {
		AbstractCamelModelElement selectedEP = importModel("withPathParameter.xml");
		CamelModel camelModel = CamelCatalogCacheManager.getInstance().getCamelModelForProject(fuseProject.getProject());
		final Component gdriveComponent = camelModel.getComponentForScheme("google-drive");
		Parameter apiNamePathParameter = gdriveComponent.getParameters().stream().filter(p -> "apiName".equals(p.getName())).findFirst().get();
		Parameter methodNamePathParameter = gdriveComponent.getParameters().stream().filter(p -> "methodName".equals(p.getName())).findFirst().get();
		
		assertThat(PropertiesUtils.getPropertyFromUri(selectedEP, apiNamePathParameter, gdriveComponent)).isEqualTo("drive-files");
		assertThat(PropertiesUtils.getPropertyFromUri(selectedEP, methodNamePathParameter, gdriveComponent)).isEqualTo("get");		
	}
	
	@Test
	public void testStartingWithSlash() throws Exception {
		AbstractCamelModelElement selectedEP = importModel("withPathParameterStartingWithSlash.xml");
		CamelModel camelModel = CamelCatalogCacheManager.getInstance().getCamelModelForProject(fuseProject.getProject());
		final Component ftpsComponent = camelModel.getComponentForScheme("ftps");
		Parameter hostPathParameter = ftpsComponent.getParameters().stream().filter(p -> "host".equals(p.getName())).findFirst().get();
		Parameter portPathParameter = ftpsComponent.getParameters().stream().filter(p -> "port".equals(p.getName())).findFirst().get();
		
		assertThat(PropertiesUtils.getPropertyFromUri(selectedEP, hostPathParameter, ftpsComponent)).isEqualTo("//localhost");
		assertThat(PropertiesUtils.getPropertyFromUri(selectedEP, portPathParameter, ftpsComponent)).isEqualTo("32");
		
		PropertiesUtils.updateURIParams(selectedEP, hostPathParameter, "//newlocalhost", ftpsComponent, modelMap(ftpsComponent.getParameters()));
	
		assertThat(PropertiesUtils.getPropertyFromUri(selectedEP, hostPathParameter, ftpsComponent)).isEqualTo("//newlocalhost");
	}
	
	@Test
	public void testWithNotFullyInitializedModelMap() throws Exception {
		AbstractCamelModelElement selectedEP = importModel("withPathParameterStartingWithSlash.xml");
		CamelModel camelModel = CamelCatalogCacheManager.getInstance().getCamelModelForProject(fuseProject.getProject());
		final Component ftpsComponent = camelModel.getComponentForScheme("ftps");
		List<Parameter> uriParameters = ftpsComponent.getParameters();
		Parameter hostPathParameter = uriParameters.stream().filter(p -> "host".equals(p.getName())).findFirst().get();
		Parameter portPathParameter = uriParameters.stream().filter(p -> "port".equals(p.getName())).findFirst().get();
		
		assertThat(PropertiesUtils.getPropertyFromUri(selectedEP, hostPathParameter, ftpsComponent)).isEqualTo("//localhost");
		assertThat(PropertiesUtils.getPropertyFromUri(selectedEP, portPathParameter, ftpsComponent)).isEqualTo("32");
		
		
		List<Parameter> allParametersExceptPort = uriParameters.stream().filter(param -> !("port".equals(param.getName()))).collect(Collectors.toList());
		IObservableMap<String, String> modelMap = modelMap(allParametersExceptPort);
		
		PropertiesUtils.updateURIParams(selectedEP, hostPathParameter, "//newlocalhost", ftpsComponent, modelMap);
	
		assertThat(PropertiesUtils.getPropertyFromUri(selectedEP, hostPathParameter, ftpsComponent)).isEqualTo("//newlocalhost");
		assertThat(PropertiesUtils.getPropertyFromUri(selectedEP, portPathParameter, ftpsComponent)).isEqualTo("32");
	}
	
	private IObservableMap<String, String> modelMap(List<Parameter> params) {
		Map<String, String> map = new HashMap<String, String>();
		for (Parameter param : params) {
			map.put(param.getName(), param.getName());
		}
		return new ObservableMap<String, String>(map);
	}
}
