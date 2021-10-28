/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.launcher.ui.propertytester;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.fusesource.ide.foundation.ui.io.CamelXMLEditorInput;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CamelLocalLaunchPropertyTesterTest {
	
	@Mock
	private IProject project;
	@Mock
	private IFile file;
	
	@Before
	public void setup(){
		doReturn(project).when(file).getProject();
	}

	@Test
	public void test_ReturnTrue_ForProject() throws Exception {
		CamelLocalLaunchPropertyTester camelLocalLaunchPropertyTester = spy(new CamelLocalLaunchPropertyTester());
		Model mavenModel = new Model();
		Build build = new Build();
		Plugin camelPlugin = new Plugin();
		camelPlugin.setGroupId("org.apache.camel");
		camelPlugin.setArtifactId("camel-maven-plugin");
		build.getPlugins().add(camelPlugin);
		mavenModel.setBuild(build);
		doReturn(mavenModel ).when(camelLocalLaunchPropertyTester).retrieveMavenModel(project);
		doReturn(project).when(project).getAdapter(IResource.class);
		
		boolean res = camelLocalLaunchPropertyTester.test(project, CamelLocalLaunchPropertyTester.IS_LOCAL_LAUNCH_AVAILABLE, null, null);
		
		assertThat(res).isTrue();
	}
	
	@Test
	public void test_ReturnFalse_WhenNoBuildInMavenModel() throws Exception {
		CamelLocalLaunchPropertyTester camelLocalLaunchPropertyTester = spy(new CamelLocalLaunchPropertyTester());
		Model mavenModel = new Model();
		doReturn(mavenModel ).when(camelLocalLaunchPropertyTester).retrieveMavenModel(project);
		
		boolean res = camelLocalLaunchPropertyTester.test(project, CamelLocalLaunchPropertyTester.IS_LOCAL_LAUNCH_AVAILABLE, null, null);
		
		assertThat(res).isFalse();
	}
	
	@Test
	public void test_ReturnTrue_ForFile() throws Exception {
		CamelLocalLaunchPropertyTester camelLocalLaunchPropertyTester = spy(new CamelLocalLaunchPropertyTester());
		Model mavenModel = new Model();
		Build build = new Build();
		Plugin camelPlugin = new Plugin();
		camelPlugin.setGroupId("org.apache.camel");
		camelPlugin.setArtifactId("camel-maven-plugin");
		build.getPlugins().add(camelPlugin);
		mavenModel.setBuild(build);
		doReturn(mavenModel).when(camelLocalLaunchPropertyTester).retrieveMavenModel(file);
		doReturn(file).when(file).getAdapter(IResource.class);
		
		boolean res = camelLocalLaunchPropertyTester.test(file, CamelLocalLaunchPropertyTester.IS_LOCAL_LAUNCH_AVAILABLE, null, null);
		
		assertThat(res).isTrue();
	}
	
	@Test
	public void test_ReturnTrue_ForCamelXMLEditorInput() throws Exception {
		CamelLocalLaunchPropertyTester camelLocalLaunchPropertyTester = spy(new CamelLocalLaunchPropertyTester());
		Model mavenModel = new Model();
		Build build = new Build();
		Plugin camelPlugin = new Plugin();
		camelPlugin.setGroupId("org.apache.camel");
		camelPlugin.setArtifactId("camel-maven-plugin");
		build.getPlugins().add(camelPlugin);
		mavenModel.setBuild(build);
		doReturn(mavenModel).when(camelLocalLaunchPropertyTester).retrieveMavenModel(file);
		doReturn(file).when(file).getAdapter(IResource.class);
		
		boolean res = camelLocalLaunchPropertyTester.test(new CamelXMLEditorInput(file, null), CamelLocalLaunchPropertyTester.IS_LOCAL_LAUNCH_AVAILABLE, null, null);
		
		assertThat(res).isTrue();
	}
	
	@Test
	public void test_ReturnFalse_ForNotMavenProject() throws Exception {
		CamelLocalLaunchPropertyTester camelLocalLaunchPropertyTester = spy(new CamelLocalLaunchPropertyTester());
		doReturn(null).when(camelLocalLaunchPropertyTester).retrieveMavenModel(project);
		
		boolean res = camelLocalLaunchPropertyTester.test(project, CamelLocalLaunchPropertyTester.IS_LOCAL_LAUNCH_AVAILABLE, null, null);
		
		assertThat(res).isFalse();
	}
}
