/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.projecttemplates;

import static org.assertj.core.api.Assertions.assertThat;

import org.fusesource.ide.projecttemplates.util.BasicProjectCreatorRunnableUtils;
import org.junit.Test;

/**
 * @author lhein
 */
public class BundleSymbolicNameCorrectionTest {
	
	@Test
	public void testInvalidBundleSymbolicNameCorrection() throws Exception {
		assertThat(BasicProjectCreatorRunnableUtils.getBundleSymbolicNameForProjectName("Ä:strönge~P Oroject!;:~$! name_44")).isEqualToIgnoringCase("strngePOrojectname_44");
	}
	
	@Test
	public void testAlmostValidBundleSymbolicNameCorrection() throws Exception {
		assertThat(BasicProjectCreatorRunnableUtils.getBundleSymbolicNameForProjectName("My Test Project-33")).isEqualToIgnoringCase("MyTestProject-33");
	}
	
	@Test
	public void testValidBundleSymbolicNameCorrection() throws Exception {
		assertThat(BasicProjectCreatorRunnableUtils.getBundleSymbolicNameForProjectName("My_TestProject-33")).isEqualToIgnoringCase("My_TestProject-33");
	}
}
