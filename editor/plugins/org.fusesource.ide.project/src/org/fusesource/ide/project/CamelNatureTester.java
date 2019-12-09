/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.project;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.common.project.facet.core.FacetedProjectFramework;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.fusesource.ide.foundation.ui.logging.RiderLogFacade;
import org.fusesource.ide.project.providers.CamelVirtualFolder;

/**
 * @author lhein
 */
public class CamelNatureTester extends PropertyTester {
	
	private Map<IProject, CamelVirtualFolder> cache = new HashMap<>();

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (receiver instanceof IProject) {
			IProject project = (IProject) receiver;
			boolean enabled = isCamelNatureDefined(project);
			if ("camelNatureEnabled".equals(property)) {
				return enabled;
			} else if ("camelNatureDisabled".equals(property)) {
				return !enabled;
			} else if ("projectOpen".equals(property)) {
				return project.isOpen();
			} else if ("hasChildren".equals(property)) {
				return hasChildren(project);
			}
		}
		return false;
	}

	private boolean hasChildren(IProject project) {
		if (project.isOpen()) {
			CamelVirtualFolder cvf = cache.get(project);
			if (cvf == null) {
				cvf = new CamelVirtualFolder(project);
				cvf.populateChildren();
				cache.put(project, cvf);
			}
			return !cvf.getCamelFiles().isEmpty();
		}
		return false;
	}
	
	private boolean isCamelNatureDefined(IProject project) {
		if (project.isAccessible()) {
			try {
				IProjectDescription description = project.getDescription();
				String[] natures = description.getNatureIds();

				for (int i = 0; i < natures.length; ++i) {
					if (RiderProjectNature.NATURE_ID.equals(natures[i])) {
						return true;
					}
				}
				
				// no camel nature found - check for facet
				if(FacetedProjectFramework.isFacetedProject(project)) {
					IFacetedProject fp = ProjectFacetsManager.create(project);
					Set<IProjectFacetVersion> enabled = fp.getProjectFacets();
					Iterator<IProjectFacetVersion> it = enabled.iterator();
					while(it.hasNext()) {
						IProjectFacetVersion i = it.next();
						if("jst.camel".equals(i.getProjectFacet().getId())) {
							return true;
						}
					}	
				}
			} catch (CoreException e) {
				RiderLogFacade.getLog(Activator.getDefault().getLog()).error(e);
			}
		}
		return false;
	}
}
