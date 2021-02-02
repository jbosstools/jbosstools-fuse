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
package org.fusesource.ide.projecttemplates.actions;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.fusesource.ide.camel.editor.utils.BuildAndRefreshJobWaiterUtil;
import org.fusesource.ide.camel.model.service.core.tests.integration.core.io.FuseProject;
import org.fusesource.ide.camel.model.service.core.util.CamelMavenUtils;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author lheinema
 *
 */
public class SwitchCamelVersionIT {
	
	private static final String VALID_VERSION = "2.20.1";
	
	@Rule
	public FuseProject fuseProject = new FuseProject();

	private CamelMavenUtils utils = new CamelMavenUtils();
	
	@After
	public void tearDown() throws CoreException {
		if (fuseProject != null) {
			new BuildAndRefreshJobWaiterUtil().waitJob(new NullProgressMonitor());
			fuseProject.getProject().delete(true, new NullProgressMonitor());
		}
	}
	
	@Test
	public void testSetValidCamelVersion() throws Exception {
		IProject project = fuseProject.getProject();
		ChangeCamelVersionJob job = new ChangeCamelVersionJob(project, VALID_VERSION);
		job.schedule();
		job.join();
		
		//FIXME: the wait of build job should be preferably handled in ChangeCamelversionJob or CamelMavenUtils but as it is not a regression, wait in test fo rnow
		new BuildAndRefreshJobWaiterUtil().waitJob(new NullProgressMonitor());
		
		String newCamelVersion = utils.getCamelVersionFromMaven(project, false);
		assertThat(newCamelVersion).isEqualTo(VALID_VERSION);
	}
}
