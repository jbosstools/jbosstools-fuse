/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.editor.integration.globalconfiguration.dataformat.wizards;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.maven.model.Dependency;
import org.assertj.core.api.Assertions;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.fusesource.ide.camel.editor.globalconfiguration.CamelGlobalConfigEditor;
import org.fusesource.ide.camel.editor.globalconfiguration.dataformat.provider.DataFormatContributor;
import org.fusesource.ide.camel.editor.globalconfiguration.dataformat.wizards.NewDataFormatWizard;
import org.fusesource.ide.camel.editor.internal.CamelEditorUIActivator;
import org.fusesource.ide.camel.editor.utils.BuildAndRefreshJobWaiterUtil;
import org.fusesource.ide.camel.editor.utils.JobWaiterUtil;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelCatalogCacheManager;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelModel;
import org.fusesource.ide.camel.model.service.core.catalog.dataformats.DataFormat;
import org.fusesource.ide.camel.model.service.core.io.CamelIOHandler;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelContextElement;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.camel.model.service.core.tests.integration.core.io.FuseProject;
import org.fusesource.ide.camel.model.service.core.util.CamelCatalogUtils;
import org.fusesource.ide.camel.model.service.core.util.CamelMavenUtils;
import org.fusesource.ide.camel.tests.util.Activator;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.w3c.dom.Element;

/**
 * @author Aurelien Pupier
 *
 */
@RunWith(Parameterized.class)
public class NewDataFormatWizardIT {
	
	@Rule
	public TestRule watcher = new TestWatcher() {
		@Override
		protected void starting(Description description) {
			Activator.pluginLog().logInfo("Starting test: " + description.getDisplayName());
		}
	};

	@Parameter
	public String camelVersion;

	@Parameter(value = 1)
	public String dataFormatName;

	@Parameter(value = 2)
	public DataFormat dataFormat;
	
	private FuseProject fuseProject;
	
	public static final String SCREENSHOT_FOLDER = "./target/MavenLaunchOutputs";
	
	@Parameters(name = "{0} - {1}")
	public static Collection<Object[]> data() {
		List<String> supportedCamelVersions = CamelCatalogUtils.getCamelVersionsToTestWith();
		Collection<Object[]> res = new HashSet<>();
		for (String camelVersion : supportedCamelVersions) {
			CamelModel camelModel = CamelCatalogCacheManager.getInstance().getDefaultCamelModel(camelVersion);
			Collection<DataFormat> supportedDataFormats = camelModel.getDataFormats();
			Stream<Object[]> stream = supportedDataFormats.stream()
					.filter(dataFormat -> 
					dataFormat.getName().contains("bindy") || dataFormat.getName().contains("json") // the special ones
					|| dataFormat.getName().contains("avro"))// a classic one
					.map(dataFormat -> new Object[] { camelVersion, dataFormat.getName(), dataFormat });
			res.addAll(stream.collect(Collectors.toCollection(HashSet::new)));
		}
		return res;
	}
	
	@Before
	public void before() throws Throwable {
		fuseProject = new FuseProject(NewDataFormatWizardIT.class.getName(), camelVersion);
		fuseProject.before();
	}
	
	@After
	public void after() {
		fuseProject.after();
	}

	@Test
	public void testCreationWithDeps() throws CoreException, IOException, InterruptedException, InvocationTargetException {
		final String id = dataFormat.getName() + "-id2";
		Activator.pluginLog().logInfo("NewDataFormatWizardIT.testCreationWithDeps for: " + dataFormat.getName());

		IProject project = fuseProject.getProject();
		final CamelFile camelFile = fuseProject.createEmptyCamelFile();

		assertThat(project.exists()).describedAs("The project " + project.getName() + " doesn't exist.").isTrue();
		CamelEditorUIActivator.pluginLog().logInfo("Project created: " + project.getName());

		CamelModel camelModel = CamelCatalogCacheManager.getInstance().getCamelModelForProject(fuseProject.getProject(), new NullProgressMonitor());

		NewDataFormatWizard newDataFormatWizard = new NewDataFormatWizard(camelFile, camelModel);
		Element dataFormatNode = newDataFormatWizard.createDataFormatNode(dataFormat, id, new NullProgressMonitor());
		new CamelGlobalConfigEditor(null).addDataFormat(camelFile, dataFormatNode);
		
		// Check that element just created has been correctly initialized
		check(id, camelFile);
		// Check that Model is valid after reloading from the filesystem
		final CamelIOHandler camelIOHandler = new CamelIOHandler();
		camelIOHandler.setDocument(camelFile.getDocument());
		camelIOHandler.saveCamelModel(camelFile, camelFile.getResource().getLocation().toFile(), new NullProgressMonitor());
		CamelFile reloadedCamelFile = camelIOHandler.loadCamelModel(camelFile.getResource(), new NullProgressMonitor());
		check(id, reloadedCamelFile);
		
		// check that dependencies were added
		List<Dependency> mavenProjectDependencies = new CamelMavenUtils().getDependencyList(project);
		for(org.fusesource.ide.camel.model.service.core.catalog.Dependency dataFormatDependency : dataFormat.getDependencies()){
			Stream<Dependency> filter = mavenProjectDependencies.stream()
			.filter(dep -> dep.getGroupId().equals(dataFormatDependency.getGroupId()))
			.filter(dep -> dep.getArtifactId().equals(dataFormatDependency.getArtifactId()));
			assertThat(filter.findFirst().isPresent())
				.as("The dependency "+ dataFormatDependency.getGroupId() + ":"+dataFormatDependency.getArtifactId() + " has not been added to the maven project dependency")
				.isTrue();
		}
	}
	
	/**
	 * @param id
	 * @param camelFile
	 */
	private void check(final String id, CamelFile camelFile) {
		Assertions.assertThat(camelFile.getRouteContainer() instanceof CamelContextElement).isTrue();
		Assertions.assertThat(((CamelContextElement)camelFile.getRouteContainer()).getDataformats().keySet()).containsExactly(id);
		AbstractCamelModelElement dataFormatCME = ((CamelContextElement)camelFile.getRouteContainer()).getDataformats().get(id);
		checkSpecialParameterLoaded(id, dataFormatCME);
		assertThat(new DataFormatContributor().canHandle(dataFormatCME)).isTrue();
	}

	/**
	 * @param id
	 * @param dataFormatReloaded
	 */
	private void checkSpecialParameterLoaded(final String id, AbstractCamelModelElement dataFormatReloaded) {
		final Object typeParameter = dataFormatReloaded.getParameter("type");
		if ("bindy-csv-id".equals(id)) {
			Assertions.assertThat(typeParameter).isEqualTo("Csv");
		} else if ("bindy-kpv-id".equals(id)) {
			Assertions.assertThat(typeParameter).isEqualTo("KeyValue");
		} else if ("bindy-fixed-id".equals(id)) {
			Assertions.assertThat(typeParameter).isEqualTo("Fixed");
		}

		final Object libraryParameter = dataFormatReloaded.getParameter("library");
		if ("json-jackson-id".equals(id)) {
			Assertions.assertThat(libraryParameter).isEqualTo("Jackson");
		} else if ("json-xstream-id".equals(id)) {
			// Default value in general, don't know how to check it currently
			// because it is removed from the model attributes list then
			// Assertions.assertThat(libraryParameter).isEqualTo("XStream");
		} else if ("json-gson-id".equals(id)) {
			Assertions.assertThat(libraryParameter).isEqualTo("Gson");
		}
	}

	@Before
	public void setup() throws Exception {
		CamelEditorUIActivator.pluginLog().logInfo("Starting setup for "+ NewDataFormatWizardIT.class.getSimpleName());
		waitJob();
		CamelEditorUIActivator.pluginLog().logInfo("End setup for "+ NewDataFormatWizardIT.class.getSimpleName());
	}

	protected void waitJob() {
		JobWaiterUtil jobWaiterUtil = new BuildAndRefreshJobWaiterUtil();
		jobWaiterUtil.setEndless(true);
		jobWaiterUtil.waitJob(new NullProgressMonitor());
	}

}
