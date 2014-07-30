/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.project;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.fusesource.ide.commons.logging.RiderLogFacade;

/**
 * @author lhein
 */
public class CamelNatureTester extends PropertyTester {

	@Override
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {

		if (receiver instanceof IProject) {
			IProject project = (IProject) receiver;
			boolean enabled = isCamelNatureDefined(project);
			if (property.equals("camelNatureEnabled")) {
				return enabled == true;
			} else if (property.equals("camelNatureDisabled")) {
				return enabled == false;
			} else if (property.equals("projectOpen")) {
				return project.isOpen() == true;
			}
		}
		return false;
	}

	private boolean isCamelNatureDefined(IProject project) {
		if (project.isOpen()) {
			try {
				IProjectDescription description = project.getDescription();
				String[] natures = description.getNatureIds();

				for (int i = 0; i < natures.length; ++i) {
					if (RiderProjectNature.NATURE_ID.equals(natures[i])) {
						return true;
					}
				}
			} catch (CoreException e) {
				RiderLogFacade.getLog(Activator.getDefault().getLog()).error(e);
			}
		}
		return false;
	}
}
