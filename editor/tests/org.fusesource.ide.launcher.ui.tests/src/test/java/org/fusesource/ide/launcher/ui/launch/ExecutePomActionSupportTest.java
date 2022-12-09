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
package org.fusesource.ide.launcher.ui.launch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.m2e.actions.MavenLaunchConstants;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.fusesource.ide.foundation.core.util.CamelUtils;
import org.fusesource.ide.foundation.ui.io.CamelXMLEditorInput;
import org.fusesource.ide.launcher.run.util.CamelContextLaunchConfigConstants;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
@Ignore
public class ExecutePomActionSupportTest {
	
	@Mock
	private IFile camelFileFromFileEditorInput, camelFileFromCamelXMLEditorInput, camelFileFromAdapter;
	@Mock
	private IEditorPart editor;
	@Mock
	private IFileEditorInput fileEditorInput;
	@Mock
	private CamelXMLEditorInput camelXmlEditorInput;
	@Mock
	private IEditorInput otherEditorInput;
	@Mock (answer = Answers.RETURNS_DEEP_STUBS)
	private IContentDescription contentDescription;
	@Spy
	private ExecutePomAction executePomAction = new ExecutePomAction();
	@Mock
	private ILaunchConfigurationWorkingCopy lc;
	
	@Before
	public void setup() throws CoreException{
		doReturn(camelFileFromFileEditorInput).when(fileEditorInput).getFile();
		doReturn(camelFileFromCamelXMLEditorInput).when(camelXmlEditorInput).getCamelContextFile();
		doReturn(camelFileFromAdapter).when(otherEditorInput).getAdapter(IFile.class);
		doReturn(contentDescription).when(camelFileFromAdapter).getContentDescription();
		when(contentDescription.getContentType().getId()).thenReturn(CamelUtils.FUSE_CAMEL_CONTENT_TYPE);
		doReturn(null).when(executePomAction).launchCamelContext(ArgumentMatchers.any(), ArgumentMatchers.anyString());
	}

	@Test
	public void testLaunchWithFileEditorInput() throws Exception {
		doReturn(fileEditorInput).when(editor).getEditorInput();
		
		executePomAction.launch(editor, "");
		
		verify(executePomAction).launchCamelContext(camelFileFromFileEditorInput, "");
	}
	
	@Test
	public void testLaunchWithCamelXMLEditorInput() throws Exception {
		doReturn(camelXmlEditorInput).when(editor).getEditorInput();
		
		executePomAction.launch(editor, "");
		
		verify(executePomAction).launchCamelContext(camelFileFromCamelXMLEditorInput, "");
	}
	
	@Test
	public void testLaunchWithOtherEditorInput() throws Exception {
		doReturn(otherEditorInput).when(editor).getEditorInput();
		
		executePomAction.launch(editor, "");
		
		verify(executePomAction).launchCamelContext(camelFileFromAdapter, "");
	}
	
	@Test
	public void testSetGoals_War() throws Exception {
		doReturn("clean package").when(lc).getAttribute(MavenLaunchConstants.ATTR_GOALS, (String)null);
		
		String setGoals = executePomAction.setGoals(true, lc);
		
		verify(lc).setAttribute(MavenLaunchConstants.ATTR_GOALS, CamelContextLaunchConfigConstants.DEFAULT_MAVEN_GOALS_WAR);
		assertThat(setGoals).isEqualTo(CamelContextLaunchConfigConstants.DEFAULT_MAVEN_GOALS_WAR);
	}
	
	@Test
	public void testSetGoals_War_forEmpty() throws Exception {
		doReturn("").when(lc).getAttribute(MavenLaunchConstants.ATTR_GOALS, (String)null);
		
		String setGoals = executePomAction.setGoals(true, lc);
		
		verify(lc).setAttribute(MavenLaunchConstants.ATTR_GOALS, CamelContextLaunchConfigConstants.DEFAULT_MAVEN_GOALS_WAR);
		assertThat(setGoals).isEqualTo(CamelContextLaunchConfigConstants.DEFAULT_MAVEN_GOALS_WAR);
	}
	
	@Test
	public void testSetGoals_War_ForExistingValue() throws Exception {
		doReturn(CamelContextLaunchConfigConstants.DEFAULT_MAVEN_GOALS_WAR).when(lc).getAttribute(MavenLaunchConstants.ATTR_GOALS, (String)null);
		
		String setGoals = executePomAction.setGoals(true, lc);
		
		verify(lc).setAttribute(MavenLaunchConstants.ATTR_GOALS, CamelContextLaunchConfigConstants.DEFAULT_MAVEN_GOALS_WAR);
		assertThat(setGoals).isEqualTo(CamelContextLaunchConfigConstants.DEFAULT_MAVEN_GOALS_WAR);
	}
	
	@Test
	public void testSetGoals_War_ForJARExistingValue() throws Exception {
		doReturn(CamelContextLaunchConfigConstants.DEFAULT_MAVEN_GOALS_JAR).when(lc).getAttribute(MavenLaunchConstants.ATTR_GOALS, (String)null);
		
		String setGoals = executePomAction.setGoals(true, lc);
		
		verify(lc).setAttribute(MavenLaunchConstants.ATTR_GOALS, CamelContextLaunchConfigConstants.DEFAULT_MAVEN_GOALS_WAR);
		assertThat(setGoals).isEqualTo(CamelContextLaunchConfigConstants.DEFAULT_MAVEN_GOALS_WAR);
	}
	
	@Test
	public void testSetGoals_War_KeepOtherValues() throws Exception {
		doReturn(CamelContextLaunchConfigConstants.DEFAULT_MAVEN_GOALS_WAR + "anotherGoal").when(lc).getAttribute(MavenLaunchConstants.ATTR_GOALS, (String)null);
		
		String setGoals = executePomAction.setGoals(true, lc);
		
		verify(lc).setAttribute(MavenLaunchConstants.ATTR_GOALS, CamelContextLaunchConfigConstants.DEFAULT_MAVEN_GOALS_WAR+ "anotherGoal");
		assertThat(setGoals).isEqualTo(CamelContextLaunchConfigConstants.DEFAULT_MAVEN_GOALS_WAR+ "anotherGoal");
	}
	
	@Test
	public void testSetGoals_Jar() throws Exception {
		doReturn("clean package").when(lc).getAttribute(MavenLaunchConstants.ATTR_GOALS, (String)null);
		
		String setGoals = executePomAction.setGoals(false, lc);
		
		verify(lc).setAttribute(MavenLaunchConstants.ATTR_GOALS, CamelContextLaunchConfigConstants.DEFAULT_MAVEN_GOALS_JAR);
		assertThat(setGoals).isEqualTo(CamelContextLaunchConfigConstants.DEFAULT_MAVEN_GOALS_JAR);
	}
	
	@Test
	public void testSetGoals_Jar_forEmpty() throws Exception {
		doReturn("").when(lc).getAttribute(MavenLaunchConstants.ATTR_GOALS, (String)null);
		
		String setGoals = executePomAction.setGoals(false, lc);
		
		verify(lc).setAttribute(MavenLaunchConstants.ATTR_GOALS, CamelContextLaunchConfigConstants.DEFAULT_MAVEN_GOALS_JAR);
		assertThat(setGoals).isEqualTo(CamelContextLaunchConfigConstants.DEFAULT_MAVEN_GOALS_JAR);
	}
	
	@Test
	public void testSetGoals_Jar_ForExistingValue() throws Exception {
		doReturn(CamelContextLaunchConfigConstants.DEFAULT_MAVEN_GOALS_JAR).when(lc).getAttribute(MavenLaunchConstants.ATTR_GOALS, (String)null);
		
		String setGoals = executePomAction.setGoals(false, lc);
		
		verify(lc).setAttribute(MavenLaunchConstants.ATTR_GOALS, CamelContextLaunchConfigConstants.DEFAULT_MAVEN_GOALS_JAR);
		assertThat(setGoals).isEqualTo(CamelContextLaunchConfigConstants.DEFAULT_MAVEN_GOALS_JAR);
	}
	
	@Test
	public void testSetGoals_Jar_ForWARExistingValue() throws Exception {
		doReturn(CamelContextLaunchConfigConstants.DEFAULT_MAVEN_GOALS_WAR).when(lc).getAttribute(MavenLaunchConstants.ATTR_GOALS, (String)null);
		
		String setGoals = executePomAction.setGoals(false, lc);
		
		verify(lc).setAttribute(MavenLaunchConstants.ATTR_GOALS, CamelContextLaunchConfigConstants.DEFAULT_MAVEN_GOALS_JAR);
		assertThat(setGoals).isEqualTo(CamelContextLaunchConfigConstants.DEFAULT_MAVEN_GOALS_JAR);
	}
	
	@Test
	public void testSetGoals_Jar_KeepOtherValues() throws Exception {
		doReturn(CamelContextLaunchConfigConstants.DEFAULT_MAVEN_GOALS_JAR + "anotherGoal").when(lc).getAttribute(MavenLaunchConstants.ATTR_GOALS, (String)null);
		
		String setGoals = executePomAction.setGoals(false, lc);
		
		verify(lc).setAttribute(MavenLaunchConstants.ATTR_GOALS, CamelContextLaunchConfigConstants.DEFAULT_MAVEN_GOALS_JAR+ "anotherGoal");
		assertThat(setGoals).isEqualTo(CamelContextLaunchConfigConstants.DEFAULT_MAVEN_GOALS_JAR+ "anotherGoal");
	}
	
	@Test
	public void testSetGoals_Jar_KeepOtherValuesForTest() throws Exception {
		doReturn(CamelContextLaunchConfigConstants.DEFAULT_MAVEN_GOALS_ALL + " -Dmaven.test.skip=true").when(lc).getAttribute(MavenLaunchConstants.ATTR_GOALS, (String)null);
		
		String setGoals = executePomAction.setGoals(false, lc);
		
		verify(lc).setAttribute(MavenLaunchConstants.ATTR_GOALS, CamelContextLaunchConfigConstants.DEFAULT_MAVEN_GOALS_ALL + " -Dmaven.test.skip=true " + CamelContextLaunchConfigConstants.SPECIFIC_MAVEN_GOAL_JAR);
		assertThat(setGoals).isEqualTo(CamelContextLaunchConfigConstants.DEFAULT_MAVEN_GOALS_ALL + " -Dmaven.test.skip=true " + CamelContextLaunchConfigConstants.SPECIFIC_MAVEN_GOAL_JAR);
	}
	

}
