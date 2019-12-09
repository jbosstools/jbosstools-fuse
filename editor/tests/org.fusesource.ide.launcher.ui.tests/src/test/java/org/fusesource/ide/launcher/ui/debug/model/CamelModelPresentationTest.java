/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.launcher.ui.debug.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

import org.fusesource.ide.launcher.debug.model.CamelDebugTarget;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CamelModelPresentationTest {
	
	private static final String MY_NAME = "myName";
	@Mock
	private CamelDebugTarget cdt;
	
	@Before
	public void setup(){
		doReturn(MY_NAME).when(cdt).getName();
	}

	@Test
	public void testGetText_onCamelDebugTarget_ConsiderDisconnection() throws Exception {
		doReturn(true).when(cdt).isDisconnected();
		assertThat(getText()).isEqualTo("<disconnected>"+MY_NAME);
	}
	
	@Test
	public void testGetText_onCamelDebugTarget() throws Exception {
		doReturn(false).when(cdt).isDisconnected();
		assertThat(getText()).isNull();
	}
	
	@Test
	public void testGetText_onCamelDebugTarget_Considertermination() throws Exception {
		doReturn(true).when(cdt).isTerminated();
		assertThat(getText()).isEqualTo("<terminated>"+MY_NAME);
	}
	
	@Test
	public void testGetText_onCamelDebugTarget_ConsiderSuspension() throws Exception {
		doReturn(true).when(cdt).isSuspended();
		assertThat(getText()).isEqualTo("<suspended>"+MY_NAME);
	}

	private String getText() {
		return new CamelModelPresentation().getText(cdt);
	}

}
