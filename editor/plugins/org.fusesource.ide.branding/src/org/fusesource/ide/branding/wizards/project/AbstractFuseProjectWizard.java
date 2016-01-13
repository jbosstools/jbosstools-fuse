/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.branding.wizards.project;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkingSet;
import org.fusesource.ide.branding.Activator;
import org.fusesource.ide.foundation.ui.archetypes.ArchetypeHelper;

public abstract class AbstractFuseProjectWizard extends Wizard {

	protected IStructuredSelection selection;

	protected List<IWorkingSet> workingSets = new ArrayList<IWorkingSet>();

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
		IWorkingSet workingSet = SelectionUtil.getSelectedWorkingSet(selection);
		if (workingSet != null) {
			this.workingSets.add(workingSet);
		}
	}

	/**
	 * Create a new project from an archetype
	 */
	public void createProject(InputStream archetypeJarIn, File outputDir,
			String groupId, String artifactId, String version,
			String packageName) {
		ArchetypeHelper helper = new ArchetypeHelper(archetypeJarIn, outputDir,
				groupId, artifactId, version);
		if (packageName != null && packageName.length() > 0) {
			helper.setPackageName(packageName);
		}
		Activator.getLogger().debug(
				"Creating archetype for outputDir: " + outputDir);

		try {
			helper.execute();
		} catch (IOException ex) {
			Activator.getLogger().error(ex);
		}

		Activator.getLogger().debug("Done!");
	}
}
