/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.reddeer.requirement;

import org.eclipse.reddeer.common.matcher.VersionMatcher;
import org.eclipse.reddeer.junit.requirement.configuration.RequirementConfiguration;
import org.eclipse.reddeer.junit.requirement.matcher.RequirementMatcher;
import org.jboss.tools.fuse.reddeer.requirement.CamelExampleRequirement.CamelExample;

/**
 * Requirement matcher which specifies a Camel example restriction for name and
 * version.
 * 
 * @author Andrej Podhradsky (apodhrad@redhat.com)
 *
 */
public class CamelExampleRequirementMatcher extends RequirementMatcher {

	private RequirementMatcher versionRequirementMatcher;

	/**
	 * Constructs the requirement matcher with a given name and version.
	 * 
	 * @param name
	 *            name
	 * @param version
	 *            version
	 */
	public CamelExampleRequirementMatcher(String name, String version) {
		super(CamelExample.class, "name", name);
		versionRequirementMatcher = new RequirementMatcher(CamelExample.class, "version", new VersionMatcher(version));
	}

	@Override
	protected boolean matchesSafely(RequirementConfiguration configuration) {
		return super.matchesSafely(configuration) && versionRequirementMatcher.matches(configuration);
	}

}
