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
package org.fusesource.ide.launcher.debug.util;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.fusesource.ide.launcher.debug.model.CamelEndpointBreakpoint;
import org.fusesource.ide.launcher.run.util.CamelContextLaunchConfigConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class CamelDebugUtilsTest {

	@Mock
	private ILaunchConfiguration launchConfig;
	
	@Mock
	private CamelEndpointBreakpoint breakpoint;
	String projectName = "myprojectName";
	String fileName = "myFileName";
	
	@Before
	public void setup(){
		doReturn(fileName).when(breakpoint).getFileName();
		doReturn(projectName).when(breakpoint).getProjectName();
	}

	@Test
	public void testRawCamelContextFilePathFromLaunchConfig_withSpace() throws Exception {
		Mockito.doReturn("file:C:\\my%20path%20with%20space").when(launchConfig).getAttribute(CamelContextLaunchConfigConstants.ATTR_FILE, (String) null);

		assertThat(CamelDebugUtils.getRawCamelContextFilePathFromLaunchConfig(launchConfig)).isEqualTo("C:\\my path with space");
	}
	
	@Test
	public void testBreakpointMatchesSelection() throws Exception {
		String endpointId = "myEndpointID";
		doReturn(endpointId).when(breakpoint).getEndpointNodeId();
		
		assertThat(CamelDebugUtils.breakpointMatchesSelection(breakpoint , fileName, endpointId, projectName)).isTrue();
	}
	
	@Test
	public void testBreakpointMatchesSelection_ReturnFalseforbreakpointNullValues() throws Exception {
		String endpointId = "myEndpointID";
		
		assertThat(CamelDebugUtils.breakpointMatchesSelection(null , fileName, endpointId, projectName)).isFalse();
	}
	
	@Test
	public void testBreakpointMatchesSelection_ReturnFalseforFileNameNullValues() throws Exception {
		String endpointId = "myEndpointID";
		doReturn(endpointId).when(breakpoint).getEndpointNodeId();
		doReturn(null).when(breakpoint).getFileName();
		
		assertThat(CamelDebugUtils.breakpointMatchesSelection(breakpoint , fileName, endpointId, projectName)).isFalse();
	}
	
	@Test
	public void testBreakpointMatchesSelection_ReturnFalseforProjectNameNullValues() throws Exception {
		String endpointId = "myEndpointID";
		doReturn(endpointId).when(breakpoint).getEndpointNodeId();
		doReturn(null).when(breakpoint).getProjectName();
		
		assertThat(CamelDebugUtils.breakpointMatchesSelection(breakpoint , fileName, endpointId, projectName)).isFalse();
	}
	
	@Test
	public void testBreakpointMatchesSelection_ReturnFalseforEndpointIdNullValues() throws Exception {
		String endpointId = "myEndpointID";
		doReturn(null).when(breakpoint).getEndpointNodeId();
		
		assertThat(CamelDebugUtils.breakpointMatchesSelection(breakpoint , fileName, endpointId, projectName)).isFalse();
	}
	
	@Test
	public void testBreakpointMatchesSelection_supportsCaseSensitivity() throws Exception {
		String endpointId = "myEndpointID_withDifferentCase";
		doReturn(endpointId).when(breakpoint).getEndpointNodeId();
		
		assertThat(CamelDebugUtils.breakpointMatchesSelection(breakpoint , fileName, "myEndpointID_withDifferentcase", projectName)).isFalse();
	}

}
