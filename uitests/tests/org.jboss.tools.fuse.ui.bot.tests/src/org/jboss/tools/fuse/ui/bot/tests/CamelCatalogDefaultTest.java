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
package org.jboss.tools.fuse.ui.bot.tests;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.reddeer.gef.view.PaletteView;
import org.eclipse.reddeer.junit.requirement.inject.InjectRequirement;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.cleanerrorlog.CleanErrorLogRequirement.CleanErrorLog;
import org.eclipse.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement.CleanWorkspace;
import org.eclipse.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.eclipse.reddeer.workbench.handler.EditorHandler;
import org.eclipse.reddeer.workbench.impl.shell.WorkbenchShell;
import org.jboss.tools.fuse.reddeer.CamelCatalogUtils;
import org.jboss.tools.fuse.reddeer.ProjectTemplate;
import org.jboss.tools.fuse.reddeer.ProjectType;
import org.jboss.tools.fuse.reddeer.editor.CamelEditor;
import org.jboss.tools.fuse.reddeer.perspectives.FuseIntegrationPerspective;
import org.jboss.tools.fuse.reddeer.preference.ConsolePreferenceUtil;
import org.jboss.tools.fuse.reddeer.requirement.CamelCatalogRequirement;
import org.jboss.tools.fuse.reddeer.requirement.CamelCatalogRequirement.CamelCatalog;
import org.jboss.tools.fuse.reddeer.view.PaletteViewExt;
import org.jboss.tools.fuse.ui.bot.tests.utils.ProjectFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Verifies components from Camel editor palette view against appropriate Camel catalog
 * 
 * @author djelinek
 */
@CamelCatalog
@CleanWorkspace
@CleanErrorLog
@RunWith(RedDeerSuite.class)
@OpenPerspective(FuseIntegrationPerspective.class)
public class CamelCatalogDefaultTest {

	private static final String PROJECT_NAME = "cbr";

	private static final String CONTEXT = "camel-context.xml";
	
	private static CamelCatalogUtils catalog;

	private List<String> skip = new ArrayList<>();
	
	@InjectRequirement
	private static CamelCatalogRequirement catalogRequirement;

	@BeforeClass
	public static void setupTestEnvironment() {
		new WorkbenchShell().maximize();
		ConsolePreferenceUtil.setConsoleOpenOnError(false);
		ConsolePreferenceUtil.setConsoleOpenOnOutput(false);
		ProjectFactory.newProject(PROJECT_NAME).template(ProjectTemplate.CBR).type(ProjectType.SPRING)
				.version(catalogRequirement.getConfiguration().getVersion()).create();
		catalog = new CamelCatalogUtils(catalogRequirement.getConfiguration().getHome());
	}

	@AfterClass
	public static void cleanSetup() {
		EditorHandler.getInstance().closeAll(true);
		ProjectFactory.deleteAllProjects();
	}

	/**
	 * <p>
	 * Test for verifying presence of components against appropriate Camel catalog
	 * </p>
	 * <ol>
	 * <li>Create a new Fuse Integration Project - template: CBR, project type: Spring</li>
	 * <li>Get list of components from Camel catalog</li>
	 * <li>Get list of components from Camel Editor Palette view</li>
	 * <li>Verify that all components are available in catalog</li>
	 * </ol>
	 */
	@Test
	public void testComponentPresence() {
		new CamelEditor(CONTEXT).activate();
		new PaletteView().open();
		List<String> missing = new ArrayList<>();
		for (String component : new PaletteViewExt().getGroupTools("Components")) {
			if (!isExist(component)) {
				missing.add(component);
			}
		}
		
		if (!missing.isEmpty()) {
			StringBuilder builder = new StringBuilder();
			builder.append("List of missing components:");
			for (String m : missing) {
				builder.append("\n").append(m);
			}
			fail(builder.toString());
		}
	}

	private boolean isExist(String name) {
		/**
		 * 'Generic' - general component (it is not route component), without properties
		 * 'ActiveMQ' - it is not part of the standard Apache Camel distribution, it can be used to extend Camel's functionality
		 * 'Process' - not standard component, it is interface used to implement consumers of message exchanges 
		 */
		skip.add("Generic");
		skip.add("ActiveMQ");
		skip.add("Process");
		if (skip.contains(name)) {
			return true;
		} else {
			return catalog.isExistComponent(name);
		}
	}

}
