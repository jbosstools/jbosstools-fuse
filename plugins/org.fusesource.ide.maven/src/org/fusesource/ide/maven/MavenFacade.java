package org.fusesource.ide.maven;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.maven.model.Model;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.project.IProjectConfigurationManager;
import org.eclipse.m2e.core.project.MavenProjectInfo;
import org.eclipse.m2e.core.project.ProjectImportConfiguration;

public class MavenFacade {

	public void importProjects(IProgressMonitor monitor, File pomFile, String projectName, String groupId, String artifactId, String version) throws CoreException {
		
		IProjectConfigurationManager manager = MavenPlugin.getProjectConfigurationManager();
		ProjectImportConfiguration config = new ProjectImportConfiguration();
		Collection<MavenProjectInfo> infos = new ArrayList<MavenProjectInfo>();
		Model model = new Model();
		model.setGroupId(groupId);
		model.setArtifactId(artifactId);
		model.setVersion(version);
		model.setPomFile(pomFile);
		MavenProjectInfo info = new MavenProjectInfo(projectName, pomFile, model, null);
		infos.add(info);

		manager.importProjects(infos, config , monitor);
	}
	
}
