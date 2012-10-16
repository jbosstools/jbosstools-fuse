package org.fusesource.ide.deployment.maven;

import org.apache.maven.model.Model;
import org.eclipse.core.resources.IProject;

public interface ProjectDropTarget {

	/**
	 * Handles dropping the project which may have a maven model associated with it
	 */
	public void dropProject(IProject project, Model mavenModel);
}
