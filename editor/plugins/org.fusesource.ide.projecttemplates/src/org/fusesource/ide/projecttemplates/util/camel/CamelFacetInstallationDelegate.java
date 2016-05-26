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
package org.fusesource.ide.projecttemplates.util.camel;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetDataModelProperties;
import org.eclipse.wst.common.componentcore.internal.ComponentResource;
import org.eclipse.wst.common.componentcore.internal.StructureEdit;
import org.eclipse.wst.common.componentcore.internal.WorkbenchComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IDelegate;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.fusesource.ide.camel.model.service.core.util.CamelFileTemplateCreator;
import org.fusesource.ide.projecttemplates.internal.ProjectTemplatesActivator;
import org.fusesource.ide.projecttemplates.util.NewProjectMetaData;

/**
 * The camel facet as currently implemented requires either a utility facet
 * or a web facet.  Natures and other such should be added automatically by them. 
 * 
 * In the event of a utility facet being present (instead of web), 
 * we still need to make a content mapping folder. 
 * 
 */
public class CamelFacetInstallationDelegate implements IDelegate {

	private static final String ROUTES_DSL_TEXT = "Routes";
	private static final String SPRING_DSL_TEXT = "Spring";
	private static final String BLUEPRINT_DSL_TEXT = "Blueprint";
	private static final String JAVA_DSL_TEXT = "Java";
	
	private IDataModel model;
	private NewProjectMetaData metadata;
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.wst.common.project.facet.core.IDelegate#execute(org.eclipse.core.resources.IProject, org.eclipse.wst.common.project.facet.core.IProjectFacetVersion, java.lang.Object, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void execute(IProject project, IProjectFacetVersion fv,
			Object config, IProgressMonitor monitor) throws CoreException {
		model = (IDataModel) config;
		if (!model.isPropertySet(ICamelFacetDataModelProperties.CAMEL_PROJECT_METADATA)) return;
		metadata = (NewProjectMetaData)model.getProperty(ICamelFacetDataModelProperties.CAMEL_PROJECT_METADATA);
		
		// store the camel version as project property
		project.setPersistentProperty(ICamelFacetDataModelProperties.QNAME_CAMEL_VERSION, metadata.getCamelVersion());
		
//		boolean hasWeb = hasWebFacet();
//		if( !hasWeb ) {
//			createUtilityProjectStructure(project);
//		} else {
//			// TODO create structure for inside a web project
//		}
	}
	

	private boolean hasWebFacet() {
		boolean webfound = false;
		IFacetedProjectWorkingCopy wc = (IFacetedProjectWorkingCopy) model.getProperty(IFacetDataModelProperties.FACETED_PROJECT_WORKING_COPY);
		Set<IProjectFacetVersion> enabled = wc.getProjectFacets();
		Iterator<IProjectFacetVersion> it = enabled.iterator();
		while(it.hasNext()) {
			IProjectFacetVersion i = it.next();
			if( i.getProjectFacet().getId().equals(ICamelFacetDataModelProperties.FACET_JST_WEB)) {
				return true;
			}
		}
		return webfound;
	}
	

	private void createUtilityProjectStructure(IProject project) throws CoreException{
		boolean shouldUpdateStructure = model.getBooleanProperty(ICamelFacetDataModelProperties.UPDATE_PROJECT_STRUCTURE);
		if( shouldUpdateStructure ) {
			createUtilityProjectReorganized(project);
		} else {
			addElementsToUtilityProject(project);
		}
	}
	
	private void addElementsToUtilityProject(IProject project) throws CoreException {
		IPath[] mappedRoots = getAllSourceMappings(project);
		IContainer[] all = new IContainer[mappedRoots.length];
		for( int i = 0; i < mappedRoots.length; i++ ) {
			all[i] = project.getFolder(mappedRoots[i]);
		}
		IFolder metaInf = findFolder(all, ICamelFacetDataModelProperties.META_INF);
		if( metaInf != null ) {
			if (metadata.isBlankProject()) {
				createCamelContextFile(project, metaInf);
			}
			createUtilityManifest(project, metaInf, new NullProgressMonitor());
		}
	}
	
	protected IPath[] getAllSourceMappings(IProject p) {
		ComponentResource[] res = findAllMappings(p);
		IPath[] sourcePaths = new IPath[res.length];
		for( int i = 0; i < res.length; i++ ) {
			sourcePaths[i] = res[i].getSourcePath();
		}
		return sourcePaths;
	}
	
	protected ComponentResource[] findAllMappings(IProject project) {
		StructureEdit structureEdit = null;

		try {
			structureEdit = StructureEdit.getStructureEditForRead(project);			
			if (structureEdit != null) {
				WorkbenchComponent component = structureEdit.getComponent();
				if (component != null) {
					Object[] arr = component.getResources().toArray();
					ComponentResource[] result = new ComponentResource[arr.length];
					for( int i = 0; i < arr.length; i++ )
						result[i] = (ComponentResource)arr[i];
					return result;					
				}
			}			
		} finally {
			if(structureEdit != null)
				structureEdit.dispose();
		}
		return new ComponentResource[]{};
	}
	
	private IFolder findFolder(IContainer[] p, final String name) {
		final IFolder[] found = new IFolder[1]; 
		
		for( int i = 0; i < p.length && found[0] == null; i++ ) {
			try {
				p[i].accept(new IResourceVisitor() {
					public boolean visit(IResource resource) throws CoreException {
						if( resource instanceof IFolder && resource.getName().equals(name))
							found[0] = (IFolder)resource;
						return found[0] == null;
					}
				});
			} catch(CoreException ce) {
			}
		}
		return found[0];
	}
	
	
	private void createUtilityProjectReorganized(IProject project) throws CoreException{
		String strContentFolder = model.getStringProperty(ICamelFacetDataModelProperties.CAMEL_CONTENT_FOLDER);
		project.setPersistentProperty(ICamelFacetDataModelProperties.QNAME_CAMEL_CONTENT_FOLDER, strContentFolder);
		
		IFolder camelContent = project.getFolder(strContentFolder);
		IProgressMonitor monitor = new NullProgressMonitor();
		
		IVirtualComponent newComponent = ComponentCore.createComponent(project);
		final IVirtualFolder jbiRoot = newComponent.getRootFolder();

		// Map the CAMELcontent to root for deploy
		String resourcesFolder = model.getStringProperty(ICamelFacetDataModelProperties.CAMEL_CONTENT_FOLDER);
		jbiRoot.createLink(new Path("/" + resourcesFolder), 0, null); //$NON-NLS-1$

		IFolder metainf = project.getFolder("src").getFolder(ICamelFacetDataModelProperties.META_INF); 
		if( metainf.exists())
			metainf.move(camelContent.getFolder(ICamelFacetDataModelProperties.META_INF).getFullPath(), true, new NullProgressMonitor());
		IFolder osgiinf = project.getFolder("src").getFolder(ICamelFacetDataModelProperties.OSGI_INF); 
		if( osgiinf.exists())
			osgiinf.move(camelContent.getFolder(ICamelFacetDataModelProperties.OSGI_INF).getFullPath(), true, new NullProgressMonitor());
		createUtilityManifest(project, camelContent.getFolder(ICamelFacetDataModelProperties.META_INF), monitor);
		if (metadata.isBlankProject()) {
			createCamelContextFile(project, camelContent.getFolder(ICamelFacetDataModelProperties.META_INF));	
		}						
	}
	
	private void createCamelContextFile(IProject project, IFolder folder) throws CoreException {
		String dsl = model.getStringProperty(ICamelFacetDataModelProperties.CAMEL_DSL);
		if( dsl.equalsIgnoreCase(CamelFacetInstallationDelegate.BLUEPRINT_DSL_TEXT) ) {
			IFolder osgiInf = folder.getParent().getFolder(new Path(ICamelFacetDataModelProperties.OSGI_INF)); 
			createFolder(osgiInf, new NullProgressMonitor());
			project.refreshLocal(IResource.DEPTH_ZERO, null);
			createBlueprintDescriptor(osgiInf);
		} else if (dsl.equalsIgnoreCase(CamelFacetInstallationDelegate.SPRING_DSL_TEXT)) {
			createSpringDescriptor(folder);
		} else if (dsl.equalsIgnoreCase(CamelFacetInstallationDelegate.ROUTES_DSL_TEXT)) {
			createRoutesDescriptor(folder);
		} else if (dsl.equalsIgnoreCase(CamelFacetInstallationDelegate.JAVA_DSL_TEXT)) {
			createJavaDescriptor(folder);
		}
	}
	
	private void createUtilityManifest(IProject project, IFolder metainf, IProgressMonitor monitor) throws CoreException {
		IFile manifestFile = metainf.getFile("MANIFEST.MF");
		StringBuffer sb = new StringBuffer();
		
		sb.append("Manifest-Version: 1.0\n");
		sb.append("Bundle-ManifestVersion: 2\n");
		sb.append("Bundle-Name: " + project.getName().getBytes(StandardCharsets.UTF_8) + "\n");
		sb.append("Bundle-SymbolicName: " + project.getName().getBytes(StandardCharsets.UTF_8) +"\n"); // TODO this string won't be osgi-valid as a symbolic name possibly
		sb.append("Bundle-Version: 1.0.0.SNAPSHOT\n");
		if( manifestFile.exists())
			manifestFile.setContents(new ByteArrayInputStream(sb.toString().getBytes()), IResource.FORCE, monitor);
		else 
			manifestFile.create(new ByteArrayInputStream(sb.toString().getBytes()), true, monitor);
	}
	
	private void createBlueprintDescriptor(IFolder folder) throws CoreException {
		IFolder bp = folder.getFolder("blueprint");
		bp.create(true, true, new NullProgressMonitor());
		IFile bpFile = bp.getFile("camel-context.xml");
		CamelFileTemplateCreator cftc = new CamelFileTemplateCreator();
		cftc.createBlueprintTemplateFile(bpFile);
	}

	private void createSpringDescriptor(IFolder folder) throws CoreException {
		IFolder spring = folder.getFolder("spring");
		spring.create(true, true, new NullProgressMonitor());
		IFile springFile = spring.getFile("camel-context.xml");
		CamelFileTemplateCreator cftc = new CamelFileTemplateCreator();
		cftc.createSpringTemplateFile(springFile);
	}
	
	private void createRoutesDescriptor(IFolder folder) throws CoreException {
		IFile springFile = folder.getFile("routes.xml");
		CamelFileTemplateCreator cftc = new CamelFileTemplateCreator();
		cftc.createRoutesTemplateFile(springFile);
	}
	
	private void createJavaDescriptor(IFolder folder) throws CoreException {
		// TODO: create a java template
		throw new CoreException(new Status(IStatus.ERROR, ProjectTemplatesActivator.PLUGIN_ID, "Java DSL creator not implemented!"));
	}
	
	/**
	 * Creates the underlying folder if it doesn't exist.
	 * It also recursively creates parent folders if necesCAMELy
	 * @param folder the folder to create
	 * @throws CoreException 
	 */
	//TODO Check if that kind of method exists elsewhere to avoid duplication
	private void createFolder(IFolder folder, IProgressMonitor monitor) throws CoreException {
	    if(!folder.exists()) {
	        IContainer parent = folder.getParent();
	        if(parent != null && !parent.exists()) {
	          createFolder((IFolder) parent, monitor);
	        }
	        folder.create(true, true, monitor);
	    }
	}
}
