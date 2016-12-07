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
package org.fusesource.ide.camel.editor.integration.globalconfiguration.dataformat.wizards;


import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.assertj.core.api.Assertions;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.ide.IDEInternalPreferences;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.fusesource.ide.branding.perspective.FusePerspective;
import org.fusesource.ide.camel.editor.globalconfiguration.CamelGlobalConfigEditor;
import org.fusesource.ide.camel.editor.globalconfiguration.dataformat.provider.DataFormatContributor;
import org.fusesource.ide.camel.editor.globalconfiguration.dataformat.wizards.NewDataFormatWizard;
import org.fusesource.ide.camel.editor.internal.CamelEditorUIActivator;
import org.fusesource.ide.camel.model.service.core.catalog.CamelModel;
import org.fusesource.ide.camel.model.service.core.catalog.CamelModelFactory;
import org.fusesource.ide.camel.model.service.core.catalog.dataformats.DataFormat;
import org.fusesource.ide.camel.model.service.core.io.CamelIOHandler;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.camel.model.service.core.tests.integration.core.io.FuseProject;
import org.fusesource.ide.projecttemplates.adopters.util.CamelDSLType;
import org.fusesource.ide.projecttemplates.preferences.initializer.StagingRepositoriesPreferenceInitializer;
import org.fusesource.ide.projecttemplates.util.JobWaiterUtil;
import org.fusesource.ide.projecttemplates.util.NewProjectMetaData;
import org.fusesource.ide.projecttemplates.wizards.FuseIntegrationProjectCreatorRunnable;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.w3c.dom.Element;
import org.fusesource.ide.foundation.ui.util.ScreenshotUtil;

/**
 * @author Aurelien Pupier
 *
 */
@RunWith(Parameterized.class)
public class NewDataFormatWizardIT {

	private static final int CURRENTLY_SHIPPED_MODEL_BUNDLES = 4;
	
	@Rule
	public FuseProject fuseproject = new FuseProject(NewDataFormatWizardIT.class.getName());

	@Parameter
	public String camelVersion;

	@Parameter(value = 1)
	public String dataFormatName;

	@Parameter(value = 2)
	public DataFormat dataFormat;
	
	public List<IProject> projectList = new ArrayList<IProject>();

	protected IProject project = null;
	public static final String SCREENSHOT_FOLDER = "./target/MavenLaunchOutputs";
	
	@Parameters(name = "{0} - {1}")
	public static Collection<Object[]> data() {
		List<String> supportedCamelVersions = CamelModelFactory.getSupportedCamelVersions();
		Assertions.assertThat(supportedCamelVersions).hasSize(CURRENTLY_SHIPPED_MODEL_BUNDLES);
		Collection<Object[]> res = new HashSet<>();
		for (String camelVersion : supportedCamelVersions) {
			CamelModel camelModel = CamelModelFactory.getModelForVersion(camelVersion);
			List<DataFormat> supportedDataFormats = camelModel.getDataformatModel().getSupportedDataFormats();
			Stream<Object[]> stream = supportedDataFormats.stream().map(dataFormat -> new Object[] { camelVersion, dataFormat.getName(), dataFormat });
			res.addAll(stream.collect(Collectors.toCollection(HashSet::new)));
		}
		return res;
	}

	@Test
	public void testCreationWithDeps() throws CoreException, IOException, InterruptedException, InvocationTargetException {
		final String id = dataFormat.getName() + "-id2";
		final String projectName = dataFormat.getName() + "-project";
		final String camelFilePath = "src/main/resources/META-INF/spring/camel-context.xml";
		
		NewProjectMetaData metadata;
		metadata = new NewProjectMetaData();
		metadata.setProjectName(projectName);
		metadata.setLocationPath(null);
		metadata.setCamelVersion(camelVersion);
		metadata.setTargetRuntime(null);
		metadata.setDslType(CamelDSLType.SPRING);
		metadata.setBlankProject(true);
		metadata.setTemplate(null);
		
		new ProgressMonitorDialog(
				Display.getDefault().getActiveShell()).run(false, true, 
						new FuseIntegrationProjectCreatorRunnable(metadata));

		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		projectList.add(project);
		this.project = project;

		assertThat(project.exists()).describedAs("The project "+ project.getName()+ " doesn't exist.").isTrue();
		CamelEditorUIActivator.pluginLog().logInfo("Project created: "+projectName);
		final IFile camelResource = project.getFile(camelFilePath);
		assertThat(camelResource.exists()).isTrue();

		// TODO: wait for all build job to finish?
		waitJob();

		CamelModel camelModel = CamelModelFactory.getModelForVersion(camelVersion);

		CamelIOHandler handler = new CamelIOHandler();
		final CamelFile camelFile = handler.loadCamelModel(camelResource, new NullProgressMonitor());

		NewDataFormatWizard newDataFormatWizard = new NewDataFormatWizard(camelFile, camelModel.getDataformatModel());
		Element dataFormatNode = newDataFormatWizard.createDataFormatNode(dataFormat, id);
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
		assertThat(dataFormat.getDependencies()).isNotEmpty(); 
	}
	
	/**
	 * @param project
	 * @return the Maven pom dependencies corresponding to the supplied project
	 */
	List<Dependency> getMavenProjectDependencies(IProject project) throws CoreException {
		final IFile pomIFile = project.getFile(new Path(IMavenConstants.POM_FILE_NAME));
		if (pomIFile.exists()) {
			final Model m2m = MavenPlugin.getMaven().readModel(pomIFile.getLocation().toFile());
			return m2m.getDependencies();
		}
		return null;
	}

	/**
	 * @param id
	 * @param camelFile
	 */
	private void check(final String id, CamelFile camelFile) {
		Assertions.assertThat(camelFile.getCamelContext().getDataformats().keySet()).containsExactly(id);
		AbstractCamelModelElement dataFormatCME = camelFile.getCamelContext().getDataformats().get(id);
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
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(false);
		CamelEditorUIActivator.pluginLog().logInfo("All editors closed");
		IPreferenceStore store = IDEWorkbenchPlugin.getDefault().getPreferenceStore();
		store.setValue(IDEInternalPreferences.PROJECT_SWITCH_PERSP_MODE, IDEInternalPreferences.PSPM_ALWAYS);
		
		File f = new File(SCREENSHOT_FOLDER);
		f.mkdirs();		

		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IWorkbenchPart welcomePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
		if (welcomePage != null) {
			welcomePage.dispose();
		}
		page.closeAllPerspectives(false, false);
		CamelEditorUIActivator.pluginLog().logInfo("All perspectives closed");
		PlatformUI.getWorkbench().showPerspective(FusePerspective.ID, page.getWorkbenchWindow());
		CamelEditorUIActivator.pluginLog().logInfo("Opening Fuse perspective");
		waitJob();
		
		CamelEditorUIActivator.pluginLog().logInfo("End setup for "+ NewDataFormatWizardIT.class.getSimpleName());
	}

	protected void waitJob() {
		JobWaiterUtil jobWaiterUtil = new JobWaiterUtil();
		jobWaiterUtil.setEndless(true);
		jobWaiterUtil.waitBuildAndRefreshJob(new NullProgressMonitor());
	}

	@After
	public void tearDown() throws CoreException, InterruptedException, IOException {
		String projectName = project != null ? project.getName() : String.format("%s-%s", getClass().getSimpleName(), camelVersion);
		ScreenshotUtil.saveScreenshotToFile(String.format("%s/MavenLaunchOutput-%s.png", SCREENSHOT_FOLDER, projectName), SWT.IMAGE_PNG);

		for (IProject project : projectList) {

			if (project != null) {
				//refresh otherwise cannot delete due to target folder created
				project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
				waitJob();
				readAndDispatch(0);
				boolean projectSuccesfullyDeleted = false;
				while(!projectSuccesfullyDeleted ){
					try{
						project.delete(true, true, new NullProgressMonitor());
					} catch(Exception e){
						//some lock/stream kept on camel-context.xml surely by the killed process, need time to let OS such as Windows to re-allow deletion
						readAndDispatch(0);
						waitJob();
						continue;
					}
					projectSuccesfullyDeleted = true;
				}
			}
			
		}
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		page.closeAllEditors(false);
		new StagingRepositoriesPreferenceInitializer().setStagingRepositoriesEnablement(false);
	}

	protected void readAndDispatch(int currentNumberOfTry) {
		try{
			while (Display.getDefault().readAndDispatch()) {
				
			}
		} catch(SWTException swtException){
			//TODO: remove try catch when https://issues.jboss.org/browse/FUSETOOLS-1913 is done (CI with valid GUI)
			swtException.printStackTrace();
			if(currentNumberOfTry < 100){
				readAndDispatch(currentNumberOfTry ++);
			} else {
				System.out.println("Tried 100 times to wait for UI... Continue and see what happens.");
			}
		}
	}
}
