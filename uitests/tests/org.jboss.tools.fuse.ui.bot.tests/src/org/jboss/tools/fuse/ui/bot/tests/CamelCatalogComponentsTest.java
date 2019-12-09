/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.ui.bot.tests;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.jboss.tools.fuse.reddeer.ProjectTemplate.CBR_SPRING;
import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardDeploymentType.STANDALONE;
import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardRuntimeType.KARAF;
import static org.jboss.tools.fuse.reddeer.view.PaletteViewExt.GROUP_COMPONENTS;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.reddeer.gef.view.PaletteView;
import org.eclipse.reddeer.junit.internal.runner.ParameterizedRequirementsRunnerFactory;
import org.eclipse.reddeer.junit.requirement.inject.InjectRequirement;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.cleanerrorlog.CleanErrorLogRequirement.CleanErrorLog;
import org.eclipse.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement;
import org.eclipse.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement.CleanWorkspace;
import org.eclipse.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.eclipse.reddeer.workbench.impl.shell.WorkbenchShell;
import org.jboss.tools.fuse.reddeer.CamelCatalogUtils;
import org.jboss.tools.fuse.reddeer.CamelCatalogUtils.CatalogType;
import org.jboss.tools.fuse.reddeer.editor.CamelEditor;
import org.jboss.tools.fuse.reddeer.perspectives.FuseIntegrationPerspective;
import org.jboss.tools.fuse.reddeer.projectexplorer.CamelProject;
import org.jboss.tools.fuse.reddeer.requirement.CamelCatalogRequirement;
import org.jboss.tools.fuse.reddeer.requirement.CamelCatalogRequirement.CamelCatalog;
import org.jboss.tools.fuse.reddeer.utils.ProjectFactory;
import org.jboss.tools.fuse.reddeer.view.FusePropertiesView;
import org.jboss.tools.fuse.reddeer.view.PaletteViewExt;
import org.jboss.tools.fuse.ui.bot.tests.utils.EditorManipulator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Parameterized.UseParametersRunnerFactory;

/**
 * This test verifies availability of all properties which should have been in component 'Properties view' according to 'Camel catalog'
 * 
 * @author djelinek
 */
@CamelCatalog
@CleanWorkspace
@CleanErrorLog
@RunWith(RedDeerSuite.class)
@OpenPerspective(FuseIntegrationPerspective.class)
@UseParametersRunnerFactory(ParameterizedRequirementsRunnerFactory.class)
public class CamelCatalogComponentsTest {

	public static final String PROJECT_NAME = "cbr";
	public static final String CONTEXT = "camel-context.xml";
	public static final String ADVANCED_TAB = "Advanced";

	private static CamelCatalogUtils catalog;

	private String component;

	@InjectRequirement
	private static CamelCatalogRequirement catalogRequirement;
	
	@Rule
	public ErrorCollector collector = new ErrorCollector();

	/**
	 * Sets parameters for parameterized test
	 * 
	 * @return List of all available components
	 */
	@Parameters(name = "{0}")
	public static Collection<String> setupData() {
		ProjectFactory.newProject(PROJECT_NAME).deploymentType(STANDALONE).runtimeType(KARAF)
			.version(catalogRequirement.getConfiguration().getVersion()).template(CBR_SPRING).create();
		new PaletteView().open();
		List<String> comps = new PaletteViewExt().getGroupTools(GROUP_COMPONENTS);

		/**
		 * Components without Camel catalog JSON file 
		 * 'Generic' - general component (it is not route component), without properties 
		 * 'ActiveMQ' - it is not part of the standard Apache Camel distribution, it can be used to extend Camel's functionality 
		 * 'Process' - not standard component, it is interface used to implement consumers of  message exchanges
		 */
		comps.remove("Generic");
		comps.remove("ActiveMQ");
		comps.remove("Process");

		/**
		 * Components without properties 'Advanced' tab  
		 * see https://issues.jboss.org/browse/FUSETOOLS-2917
		 */
		comps.remove("Bean");
		comps.remove("Log");
		return comps;
	}

	/**
	 * Utilizes passing parameters using the constructor
	 * 
	 * @param template
	 *            a Global element
	 */
	public CamelCatalogComponentsTest(String component) {
		this.component = component;
	}

	@BeforeClass
	public static void setupTestEnvironment() {
		new WorkbenchShell().maximize();
		ProjectFactory.newProject(PROJECT_NAME).deploymentType(STANDALONE).runtimeType(KARAF)
				.version(catalogRequirement.getConfiguration().getVersion()).template(CBR_SPRING).create();	
		catalog = new CamelCatalogUtils(catalogRequirement.getConfiguration().getHome());
	}

	/**
	 * Cleans up test environment and switch to required tab in camel editor
	 */
	@After
	public void clearSetup() {
		new CamelProject(PROJECT_NAME).openCamelContext(CONTEXT);
		CamelEditor.switchTab(CamelEditor.SOURCE_TAB);
		EditorManipulator.copyFileContentToCamelXMLEditor("resources/camel-context-cbr.xml");
		CamelEditor.switchTab(CamelEditor.DESIGN_TAB);
	}

	@AfterClass
	public static void cleanSetup() {
		new CleanWorkspaceRequirement().fulfill();
	}

	/**
	 * <p>
	 * Verifies availability of component properties against Camel catalog
	 * </p>
	 * <ol>
	 * <li>Get list of all components from Camel editor <p>Palette view</p></li>
	 * <li>Create new <p>Fuse Integration project - CBR - SPRING</p></li>
	 * <li>Open <p>camel-context.xml</p> and activate <p>Camel editor</p></li>
	 * <li>Add Camel component from components list (from first step) into Camel editor</li>
	 * <li>Verifies that component has Properties view <p>Advanced</p> tab</li>
	 * <li>Get all available properties labels from Properties <p>Advanced</p> tab and customize labels text for comparing</li>
	 * <li>Verifies available component properties against camel catalog requirement properties list (fails only missing required properties)</li>
	 * </ol>
	 */
	@Test
	public void testComponentPropertiesPresence() {
		CamelEditor editor = new CamelEditor(CONTEXT);
		editor.activate();
		editor.addCamelComponent(component, "Route cbr-route");
		
		FusePropertiesView view = new FusePropertiesView();
		view.open();
		collector.checkThat("Component '" + component + "' has not '" + ADVANCED_TAB + "' properties tab", view.getTabs().contains(ADVANCED_TAB), equalTo(true));
		view.selectTab(ADVANCED_TAB);

		/*
		 * Get all available properties labels from Properties 'Advanced' tab and customize labels text for comparing
		 */
		List<String> viewAvailableProperties = new ArrayList<>();
		for (String tab : view.getPropertiesTabsTitles()) {
			viewAvailableProperties.addAll(view.getPropertiesLabelsList(tab));
		}

		for (int i = 0; i < viewAvailableProperties.size(); i++) {
			viewAvailableProperties.set(i, viewAvailableProperties.get(i).replaceAll("[\\s\\W]", "").toLowerCase());
		}

		/*
		 * Verifies available component properties against camel catalog requirement properties list (fails only missing REQUIRED properties)
		 */
		for (String property : catalog.getPropertiesNamesList(CatalogType.COMPONENT, component)) {
			if (catalog.isRequired(CatalogType.COMPONENT, component, property))
				collector.checkThat(property, viewAvailableProperties.contains(property.toLowerCase()), equalTo(true));
		}

	}
}
