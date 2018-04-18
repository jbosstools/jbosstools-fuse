/******************************************************************************* 
 * Copyright (c) 2018 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.fusesource.ide.projecttemplates.tests.integration.wizards.pages.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.fusesource.ide.camel.model.service.core.util.CamelCatalogUtils;
import org.fusesource.ide.projecttemplates.wizards.pages.model.EnvironmentData;
import org.fusesource.ide.projecttemplates.wizards.pages.model.FuseDeploymentPlatform;
import org.fusesource.ide.projecttemplates.wizards.pages.model.FuseRuntimeKind;
import org.fusesource.ide.projecttemplates.wizards.pages.model.TemplateItem;
import org.fusesource.ide.projecttemplates.wizards.pages.model.TemplateModel;
import org.fusesource.ide.projecttemplates.wizards.pages.model.TemplateOrCategoryItem;
import org.fusesource.ide.projecttemplates.wizards.pages.provider.TemplateContentProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class TemplateModelSingleDefaultIT {
	
	@Parameters
	public static Iterable<? extends Object> data() {
		return Arrays.asList(
				new EnvironmentData(CamelCatalogUtils.CAMEL_VERSION_LATEST_PRODUCTIZED_63, FuseDeploymentPlatform.STANDALONE, FuseRuntimeKind.KARAF),
				new EnvironmentData(CamelCatalogUtils.CAMEL_VERSION_LATEST_PRODUCTIZED_63, FuseDeploymentPlatform.STANDALONE, FuseRuntimeKind.WILDFLY),
				new EnvironmentData(CamelCatalogUtils.CAMEL_VERSION_LATEST_PRODUCTIZED_63, FuseDeploymentPlatform.STANDALONE, FuseRuntimeKind.SPRINGBOOT),
				new EnvironmentData(CamelCatalogUtils.CAMEL_VERSION_LATEST_COMMUNITY, FuseDeploymentPlatform.STANDALONE, FuseRuntimeKind.KARAF),
				new EnvironmentData(CamelCatalogUtils.CAMEL_VERSION_LATEST_COMMUNITY, FuseDeploymentPlatform.STANDALONE, FuseRuntimeKind.WILDFLY),
				new EnvironmentData(CamelCatalogUtils.CAMEL_VERSION_LATEST_COMMUNITY, FuseDeploymentPlatform.STANDALONE, FuseRuntimeKind.SPRINGBOOT),
				new EnvironmentData(CamelCatalogUtils.CAMEL_VERSION_LATEST_COMMUNITY, FuseDeploymentPlatform.OPENSHIFT, FuseRuntimeKind.SPRINGBOOT),
				new EnvironmentData(CamelCatalogUtils.CAMEL_VERSION_LATEST_COMMUNITY, FuseDeploymentPlatform.OPENSHIFT, FuseRuntimeKind.KARAF),
				new EnvironmentData(CamelCatalogUtils.CAMEL_VERSION_LATEST_COMMUNITY, FuseDeploymentPlatform.OPENSHIFT, FuseRuntimeKind.WILDFLY)
		);
	}
	
	@Parameter
	public EnvironmentData environment;
	
	@Test
	public void ensureSingleDefaultValueAvailable() throws Exception {
		Set<TemplateItem> allTemplates = getAllTemplates();
		List<TemplateItem> compatibleTemplates = allTemplates.stream().filter(template -> template.isCompatible(environment)).collect(Collectors.toList());
		List<TemplateItem> defaultTemplate = allTemplates.stream().filter(template -> template.isDefault(environment)).collect(Collectors.toList());
		if (!compatibleTemplates.isEmpty()) {
			assertThat(defaultTemplate).hasSize(1);
		}
	}

	protected Set<TemplateItem> getAllTemplates() {
		Set<TemplateItem> res = new HashSet<>();
		List<TemplateOrCategoryItem> rootTemplates = new TemplateModel().getRootTemplates();
		for (Object item : getAllTemplates(rootTemplates)) {
			if(item instanceof TemplateItem) {
				res.add((TemplateItem) item);
			}
		}
		return res;
	}

	protected Set<Object> getAllTemplates(List<? extends Object> templates) {
		Set<Object> res = new HashSet<>();
		res.addAll(templates);
		TemplateContentProvider templateContentProvider = new TemplateContentProvider();
		for (Object templateOrCategoryItem : templates) {
			res.addAll(getAllTemplates(Arrays.asList(templateContentProvider.getChildren(templateOrCategoryItem))));
		}
		return res;
	}
	
}
