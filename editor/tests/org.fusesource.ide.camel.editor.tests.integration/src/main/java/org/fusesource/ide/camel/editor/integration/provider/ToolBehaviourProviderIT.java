/*************************************************************************************
 * Copyright (c) 2016 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat - Initial implementation.
 ************************************************************************************/
package org.fusesource.ide.camel.editor.integration.provider;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.graphiti.palette.IPaletteCompartmentEntry;
import org.eclipse.graphiti.palette.IToolEntry;
import org.eclipse.graphiti.palette.impl.ObjectCreationToolEntry;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.fusesource.ide.camel.editor.CamelDesignEditor;
import org.fusesource.ide.camel.editor.CamelEditor;
import org.fusesource.ide.camel.editor.features.create.ext.CreateEndpointFigureFeature;
import org.fusesource.ide.camel.editor.integration.globalconfiguration.wizards.pages.CustomPaletteEntry1;
import org.fusesource.ide.camel.editor.integration.globalconfiguration.wizards.pages.CustomPaletteEntry2;
import org.fusesource.ide.camel.editor.provider.ActiveMQPaletteEntry;
import org.fusesource.ide.camel.editor.provider.ActiveMQPaletteEntryDependenciesManager;
import org.fusesource.ide.camel.editor.provider.ToolBehaviourProvider;
import org.fusesource.ide.camel.editor.utils.JobWaiterUtil;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelCatalogCacheManager;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelModel;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.service.core.tests.integration.core.io.FuseProject;
import org.fusesource.ide.camel.model.service.core.util.CamelCatalogUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ToolBehaviourProviderIT {
	
	private ToolBehaviourProvider toolbehaviourprovider;
	
	@Rule
	public FuseProject fuseProject = new FuseProject(ToolBehaviourProviderIT.class.getName());
	
	private static final String DUMMY_POM_CONTENT_WITH_SPRING_BOOT_DEPENDENCY = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<project xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\" xmlns=\"http://maven.apache.org/POM/4.0.0\"\n"
			+ "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n"
			+ "  <modelVersion>4.0.0</modelVersion>\n"
			+ "  <groupId>com.mycompany</groupId>\n"
			+ "  <artifactId>testproject</artifactId>\n"
			+ "  <version>1.0.0-SNAPSHOT</version>\n"
			+ "  <name>Some Dummy Project</name>\n"
			+ "  <dependencies>\n"
		    + "    <dependency>\n"
		    + "      <groupId>org.apache.camel</groupId>\n"
		    + "      <artifactId>camel-spring-boot-starter</artifactId>\n"
		    + "      <version>" + CamelCatalogUtils.getLatestCamelVersion() + "</version>\n"
		    + "    </dependency>\n"
		    + "  </dependencies>\n"
			+ "  <build>\n"
			+ "    <defaultGoal>install</defaultGoal>\n"
			+ "    <plugins>\n"
			+ "      <plugin>\n"
			+ "        <artifactId>maven-compiler-plugin</artifactId>\n"
			+ "        <version>3.5.1</version>\n"
			+ "        <configuration>\n"
			+ "          <source>1.7</source>\n"
			+ "          <target>1.7</target>\n"
			+ "        </configuration>\n"
			+ "      </plugin>\n"
			+ "      <plugin>\n"
			+ "        <artifactId>maven-resources-plugin</artifactId>\n"
			+ "        <version>2.6</version>\n"
			+ "        <configuration>\n"
			+ "          <encoding>UTF-8</encoding>\n"
			+ "        </configuration>\n"
			+ "      </plugin>\n"
			+ "    </plugins>\n"
			+ "  </build>\n"
			+ "</project>";
	
	private void initToolBehaviourProvider() throws CoreException, IOException {
		CamelEditor camelEditor = (CamelEditor)IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), (IFile) fuseProject.createEmptyCamelFile().getResource());
		CamelDesignEditor camelDesignEditor = camelEditor.getDesignEditor();
		toolbehaviourprovider = (ToolBehaviourProvider)camelDesignEditor.getDiagramTypeProvider().getCurrentToolBehaviorProvider();
	}
	
	@Test
	public void testPaletteEntriesfromExtensionPointsContainsAMQForKaraf() throws CoreException, IOException{
		initToolBehaviourProvider();
		testAMQPalette(ActiveMQPaletteEntryDependenciesManager.ACTIVEMQ_CAMEL, ActiveMQPaletteEntry.CAMEL_JMS);
	}
	
	@Test
	public void testPaletteEntriesfromExtensionPointsContainsAMQForSpringBoot() throws CoreException, IOException{
		usePomWithSpringBootDependency();
		
		initToolBehaviourProvider();
		
		List<IToolEntry> aggregatedToolEntries = toolbehaviourprovider.getAggregatedToolEntries();
		ensureCorrectNumberOfTool(aggregatedToolEntries, CustomPaletteEntry1.INTEGRATION_TEST_KARAF_ONLY, 0);
		ensureCorrectNumberOfTool(aggregatedToolEntries, CustomPaletteEntry2.INTEGRATION_TEST_SPRING_BOOT_ONLY, 1);
	}

	private void usePomWithSpringBootDependency() throws CoreException {
		IFile pom = fuseProject.getProject().getFile(IMavenConstants.POM_FILE_NAME);
		pom.setContents(new ByteArrayInputStream(DUMMY_POM_CONTENT_WITH_SPRING_BOOT_DEPENDENCY.getBytes(StandardCharsets.UTF_8)), IResource.FORCE, new NullProgressMonitor());
		new JobWaiterUtil(Arrays.asList(ResourcesPlugin.FAMILY_AUTO_BUILD)).waitJob(new NullProgressMonitor());
	}
	
	@Test
	public void testPaletteEntriesfromExtensionPointsValidityForKaraf() throws CoreException, IOException{
		initToolBehaviourProvider();
		List<IToolEntry> aggregatedToolEntries = toolbehaviourprovider.getAggregatedToolEntries();
		ensureCorrectNumberOfTool(aggregatedToolEntries, CustomPaletteEntry1.INTEGRATION_TEST_KARAF_ONLY, 1);
		ensureCorrectNumberOfTool(aggregatedToolEntries, CustomPaletteEntry2.INTEGRATION_TEST_SPRING_BOOT_ONLY, 0);
	}

	private void ensureCorrectNumberOfTool(List<IToolEntry> aggregatedToolEntries, String toolName, int occurenceToFind) {
		List<IToolEntry> filteredTools = aggregatedToolEntries.stream()
				.filter(toolEntry -> toolEntry instanceof ObjectCreationToolEntry)
				.filter(toolEntry  -> toolName.equals(((ObjectCreationToolEntry)toolEntry).getLabel()))
				.collect(Collectors.toList());
		assertThat(filteredTools).hasSize(occurenceToFind);
	}
	
	@Test
	public void testPaletteEntriesfromExtensionPointsValidityFoSpringBoot() throws CoreException, IOException{
		usePomWithSpringBootDependency();
		
		initToolBehaviourProvider();
		
		testAMQPalette(ActiveMQPaletteEntryDependenciesManager.ACTIVEMQ_CAMEL_STARTER, ActiveMQPaletteEntry.CAMEL_JMS_STARTER);
	}
	
	@Test
	public void testAllRoutingEIPWhichCanBePartOfRouteFlowAreAvailable() throws Exception {
		initToolBehaviourProvider();
    	CamelModel model = CamelCatalogCacheManager.getInstance().getCamelModelForProject(fuseProject.getProject());
    	Collection<Eip> eips = model.getEips();
    	
    	IPaletteCompartmentEntry[] palette = toolbehaviourprovider.getPalette();
    	Set<String> missingEips = new HashSet<>();
    	for(Eip eip : eips){
    		if(isEIPWantedInPalette(eip)){
    			List<IToolEntry> correspondingToolEntry = 
    					Stream.of(palette)
    					.map(IPaletteCompartmentEntry::getToolEntries)
    					.flatMap(List::stream)
    					.filter(toolEntry -> toolEntry instanceof ObjectCreationToolEntry)
    					.filter(toolEntry  -> eip.getTitle().equals(toolEntry.getLabel()))
    					.collect(Collectors.toList());
    			if(correspondingToolEntry.isEmpty()){
    				missingEips.add(eip.getTitle());
    			}
    		}
    	}
    	
    	assertThat(missingEips).isEmpty();
    	
	}

	private boolean isEIPWantedInPalette(Eip eip) {
		return eip.getTags().contains("eip")
				&& ("true".equals(eip.getInput()) || "true".equals(eip.getOutput()))
				&& !Arrays.asList("Script","To D", "Service Call", "To", "Dynamic Router").contains(eip.getTitle());
	}

	@SuppressWarnings("deprecation")
	private void testAMQPalette(String expectedFirstDependency, String expectedSecondDependency) {
		List<IToolEntry> aggregatedToolEntries = toolbehaviourprovider.getAggregatedToolEntries();
		List<IToolEntry> activeMQs = aggregatedToolEntries.stream()
				.filter(toolEntry -> toolEntry instanceof ObjectCreationToolEntry)
				.filter(toolEntry  -> ActiveMQPaletteEntry.ACTIVE_MQ.equals(toolEntry.getLabel()))
				.collect(Collectors.toList());
		assertThat(activeMQs).hasSize(1);
		CreateEndpointFigureFeature createFeature = (CreateEndpointFigureFeature)((ObjectCreationToolEntry)activeMQs.get(0)).getCreateFeature();
		assertThat(createFeature.getDependencies().get(0).getArtifactId()).isEqualTo(expectedFirstDependency);
		assertThat(createFeature.getDependencies().get(1).getArtifactId()).isEqualTo(expectedSecondDependency);
	}
}
