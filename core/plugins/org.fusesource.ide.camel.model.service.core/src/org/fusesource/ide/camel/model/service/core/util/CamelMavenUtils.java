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
package org.fusesource.ide.camel.model.service.core.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Repository;
import org.apache.maven.project.MavenProject;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.IMavenProjectRegistry;
import org.fusesource.ide.camel.model.service.core.internal.CamelModelServiceCoreActivator;

public class CamelMavenUtils {

	private CamelMavenUtils() {
		// util class
	}
	
	public static List<Dependency> getDependencies(MavenProject project, final Model model) {
		List<Dependency> deps = new ArrayList<>();
		deps.addAll(project.getDependencies());
		if (project.getDependencyManagement() != null) {
			deps.addAll(project.getDependencyManagement().getDependencies());
		}
		deps.addAll(project.getCompileDependencies());
		deps.addAll(project.getRuntimeDependencies());
		deps.addAll(project.getSystemDependencies());
		deps.addAll(model.getDependencies());
		return deps;
	}
	
	public static List<Repository> getRepositories(IProject project) {
		IMavenProjectFacade projectFacade = getMavenProjectFacade(project);
		List<Repository> reps = new ArrayList<>();
		if (projectFacade != null) {
			try {
				MavenProject mavenProject = projectFacade.getMavenProject(new NullProgressMonitor());
				reps.addAll(getRepositories(mavenProject));
			} catch (CoreException e) {
				CamelModelServiceCoreActivator.pluginLog().logError(
						"Maven project has not been found (not imported?). Repositories won't be resolved.", e);
			}
		}
		return reps;
	}
	
	public static List<Repository> getRepositories(MavenProject project) {
		if (project != null) {
			String pomPath = project.getFile().getPath(); // TODO: check if we need to append pom.xml
			final File pomFile = new File(pomPath);
			if (!pomFile.exists() || !pomFile.isFile()) {
				return Collections.emptyList();
			}
			try {
				final Model model = MavenPlugin.getMaven().readModel(pomFile);
				List<Repository> repos = new ArrayList<>();
				if (model.getRepositories() != null) {
					repos.addAll(model.getRepositories());
				}
				if (model.getPluginRepositories() != null) {
					repos.addAll(model.getPluginRepositories());
				}
				return repos;
			} catch (Exception ex) {
				CamelModelServiceCoreActivator.pluginLog().logError(ex);
			}
		}
		return Collections.emptyList();
	}

	
	/**
	 * returns the dependencies for the supplied Maven model in the supplied project
	 * 
	 * @param project
	 * @param model
	 * @return
	 */
	public static List<Dependency> getDependencies(IProject project, final Model model) {
		IMavenProjectFacade projectFacade = getMavenProjectFacade(project);
		List<Dependency> deps = new ArrayList<>();
		if (projectFacade != null) {
			try {
				MavenProject mavenProject = projectFacade.getMavenProject(new NullProgressMonitor());
				deps.addAll(getDependencies(mavenProject, model));
			} catch (CoreException e) {
				CamelModelServiceCoreActivator.pluginLog().logError(
						"Maven project has not been found (not imported?). Managed Dependencies won't be resolved.", e);
				deps.addAll(model.getDependencies());
			}
		} else {
			// In case the project was not imported in the workspace
			deps.addAll(model.getDependencies());
		}
		return deps;
	}

	/**
	 * /!\ public for test purpose
	 * 
	 * @param project
	 * @return the Maven project facade corresponding to the supplied project
	 */
	public static IMavenProjectFacade getMavenProjectFacade(IProject project) {
		final IMavenProjectRegistry projectRegistry = MavenPlugin.getMavenProjectRegistry();
		final IFile pomIFile = project.getFile(new Path(IMavenConstants.POM_FILE_NAME));
		return projectRegistry.create(pomIFile, false, new NullProgressMonitor());
	}

	/**
	 * resolves the given artifact (assuming a jar)
	 * 
	 * @param groupId
	 * @param artifactId
	 * @param version
	 * @return	the artifact or null if not resolvable 
	 */
	public static Artifact resolveArtifact(String groupId, String artifactId, String version) {
		try {
			return MavenPlugin.getMaven().resolve(groupId, artifactId, version, "jar", //$NON-NLS-1$
					null, null, new NullProgressMonitor());
		} catch (CoreException ex) {
			CamelModelServiceCoreActivator.pluginLog().logError(ex);
		}
		return null;
	}

	public static List<Dependency> getDependencyList(IProject project) {
		if (project != null) {
			IPath pomPathValue = project.getProject().getRawLocation() != null
					? project.getProject().getRawLocation().append("pom.xml")
					: ResourcesPlugin.getWorkspace().getRoot().getLocation()
							.append(project.getFullPath().append("pom.xml"));
			String pomPath = pomPathValue.toOSString();
			final File pomFile = new File(pomPath);
			if (!pomFile.exists() || pomFile.isDirectory()) {
				return Collections.emptyList();
			}
			try {
				final Model model = MavenPlugin.getMaven().readModel(pomFile);
				return getDependencies(project, model);
			} catch (Exception ex) {
				CamelModelServiceCoreActivator.pluginLog().logError(ex);
			}
		}
		return Collections.emptyList();
	}
	
	public static List<Dependency> getDependencyList(MavenProject project) {
		if (project != null) {
			String pomPath = project.getFile().getPath(); // TODO: check if we need to append pom.xml
			final File pomFile = new File(pomPath);
			if (!pomFile.exists() || !pomFile.isFile()) {
				return Collections.emptyList();
			}
			try {
				final Model model = MavenPlugin.getMaven().readModel(pomFile);
				return getDependencies(project, model);
			} catch (Exception ex) {
				CamelModelServiceCoreActivator.pluginLog().logError(ex);
			}
		}
		return Collections.emptyList();
	}
	
	/**
	 * checks for the camel version in the dependencies of the pom.xml
	 * 
	 * @param project
	 * @return
	 */
	public static String getCamelVersionFromMaven(IProject project) {
		// get camel-core or another camel dep
		List<Dependency> deps = getDependencyList(project);
		if (deps != null) {
			for (Dependency pomDep : deps) {
				if (pomDep.getGroupId().equalsIgnoreCase(CamelCatalogUtils.CATALOG_KARAF_GROUPID)
						&& pomDep.getArtifactId().startsWith("camel-")) {
					return pomDep.getVersion();
				}
			}
		}
		return null;
	}
	
	public static String getCamelVersionFromMaven(MavenProject project) {
		List<Dependency> deps = getDependencyList(project);
		for (Dependency pomDep : deps) {
			if (pomDep.getGroupId().equalsIgnoreCase(CamelCatalogUtils.CATALOG_KARAF_GROUPID)
					&& pomDep.getArtifactId().startsWith("camel-")) {
				return pomDep.getVersion();
			}
		}
		return null;
	}
	
	/**
	 * checks for the camel version in the dependencies of the pom.xml
	 * 
	 * @param project
	 * @return
	 */
	public static String getWildFlyCamelVersionFromMaven(IProject project) {
		// get any wildfly camel dep
		List<Dependency> deps = getDependencyList(project);
		for (Dependency pomDep : deps) {
			if (pomDep.getGroupId().equalsIgnoreCase(CamelCatalogUtils.CATALOG_WILDFLY_GROUPID)) {
				return pomDep.getVersion();
			}
		}
		return null;
	}
	
	public static RepositorySystem newRepositorySystem() {
		return newManualRepositorySystem();
	}

	public static DefaultRepositorySystemSession newRepositorySystemSession(RepositorySystem system) {
		DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();

		LocalRepository localRepo = new LocalRepository("target/local-repo");
		session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo));

//		session.setTransferListener(new ConsoleTransferListener());
//		session.setRepositoryListener(new ConsoleRepositoryListener());

		return session;
	}

	public static List<RemoteRepository> newRepositories(RepositorySystem system, RepositorySystemSession session) {
		return new ArrayList<>(Arrays.asList(newCentralRepository()));
	}

	private static RemoteRepository newCentralRepository() {
		return new RemoteRepository.Builder("central", "default", "http://central.maven.org/maven2/").build();
	}

	
	private static RepositorySystem newManualRepositorySystem() {
		/*
		 * Aether's components implement org.eclipse.aether.spi.locator.Service
		 * to ease manual wiring and using the prepopulated
		 * DefaultServiceLocator, we only need to register the repository
		 * connector and transporter factories.
		 */
		DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
		locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
//		locator.addService(TransporterFactory.class, FileTransporterFactory.class);
//		locator.addService(TransporterFactory.class, HttpTransporterFactory.class);

		locator.setErrorHandler(new DefaultServiceLocator.ErrorHandler() {
			@Override
			public void serviceCreationFailed(Class<?> type, Class<?> impl, Throwable exception) {
				CamelModelServiceCoreActivator.pluginLog().logError(exception);
			}
		});

		return locator.getService(RepositorySystem.class);
	}
}
