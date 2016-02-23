/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.project.camel;

import java.io.ByteArrayInputStream;
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
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
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

/**
 * The camel facet as currently implemented requires either a utility facet
 * or a web facet.  Natures and other such should be added automatically by them. 
 * 
 * In the event of a utility facet being present (instead of web), 
 * we still need to make a content mapping folder. 
 * 
 */

public class CamelFacetInstallationDelegate implements IDelegate {

	
	private IDataModel model;
	public void execute(IProject project, IProjectFacetVersion fv,
			Object config, IProgressMonitor monitor) throws CoreException {
		model = (IDataModel) config;
		boolean hasWeb = hasWebFacet();
		if( !hasWeb ) {
			createUtilityProjectStructure(project);
		} else {
			// TODO create structure for inside a web project
		}
	}
	

	private boolean hasWebFacet() {
		boolean webfound = false;
		IFacetedProjectWorkingCopy wc = (IFacetedProjectWorkingCopy) model.getProperty(IFacetDataModelProperties.FACETED_PROJECT_WORKING_COPY);
		Set<IProjectFacetVersion> enabled = wc.getProjectFacets();
		Iterator<IProjectFacetVersion> it = enabled.iterator();
		while(it.hasNext()) {
			IProjectFacetVersion i = it.next();
			if( i.getProjectFacet().getId().equals("jst.web")) {
				webfound = true;
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
		IFolder metaInf = findFolder(all, "META-INF");
		if( metaInf != null ) {
			IFolder osgiInf = metaInf.getParent().getFolder(new Path(ICamelFacetDataModelProperties.OSGI_INF)); 
			createFolder(osgiInf, new NullProgressMonitor());
			project.refreshLocal(IResource.DEPTH_ZERO, null);
			Boolean createBlueprint = model.getBooleanProperty(ICamelFacetDataModelProperties.CREATE_BLUEPRINT_DESCRIPTOR);
			if( createBlueprint.booleanValue() ) {
				createBlueprintDescriptor(osgiInf);
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
			WorkbenchComponent component = structureEdit.getComponent();
			Object[] arr = component.getResources().toArray();
			ComponentResource[] result = new ComponentResource[arr.length];
			for( int i = 0; i < arr.length; i++ )
				result[i] = (ComponentResource)arr[i];
			return result;
		} catch (NullPointerException e) {
			// TODO log
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
		IFolder osgiInf = camelContent.getFolder(ICamelFacetDataModelProperties.OSGI_INF); 
		createFolder(osgiInf, monitor);
		project.refreshLocal(IResource.DEPTH_ZERO, null);
		

		IVirtualComponent newComponent = ComponentCore.createComponent(project);
		final IVirtualFolder jbiRoot = newComponent.getRootFolder();

		// Map the CAMELcontent to root for deploy
		String resourcesFolder = model.getStringProperty(
				ICamelFacetDataModelProperties.CAMEL_CONTENT_FOLDER);
		jbiRoot.createLink(new Path("/" + resourcesFolder), 0, null); //$NON-NLS-1$

		Boolean createBlueprint = model.getBooleanProperty(ICamelFacetDataModelProperties.CREATE_BLUEPRINT_DESCRIPTOR);
		if( createBlueprint.booleanValue() ) {
			createBlueprintDescriptor(osgiInf);
		}
		IFolder metainf = project.getFolder("src").getFolder("META-INF"); 
		if( metainf.exists())
			metainf.move(camelContent.getFolder("META-INF").getFullPath(), true, new NullProgressMonitor());
		createUtilityManifest(project, camelContent.getFolder("META-INF"), monitor);
	}
	
	private void createUtilityManifest(IProject project, IFolder metainf, IProgressMonitor monitor) throws CoreException {
		IFile manifestFile = metainf.getFile("MANIFEST.MF");
		StringBuffer sb = new StringBuffer();
		
		sb.append("Manifest-Version: 1.0\n");
		sb.append("Bundle-ManifestVersion: 2\n");
		sb.append("Bundle-Name: " + project.getName() + "\n");
		sb.append("Bundle-SymbolicName: " + project.getName() +"\n"); // TODO this string won't be osgi-valid as a symbolic name possibly
		sb.append("Bundle-Version: 1.0.0.SNAPSHOT\n");
		if( manifestFile.exists())
			manifestFile.setContents(new ByteArrayInputStream(sb.toString().getBytes()), IResource.FORCE, monitor);
		else 
			manifestFile.create(new ByteArrayInputStream(sb.toString().getBytes()), true, monitor);
	}
	
	private void createBlueprintDescriptor(IFolder folder) throws CoreException {
		IFolder bp = folder.getFolder("blueprint");
		bp.create(true, true, new NullProgressMonitor());
		IFile bpFile = bp.getFile("blueprint.xml");
		bpFile.create(new ByteArrayInputStream(getBlueprintStubText().getBytes()), true, new NullProgressMonitor());
	}
	
	private String getBlueprintStubText() {
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append("<blueprint xmlns=\"http://www.osgi.org/xmlns/blueprint/v1.0.0\"\n");
		sb.append("		xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
		sb.append("		       xsi:schemaLocation=\"\n");
		
		sb.append("       http://www.osgi.org/xmlns/blueprint/v1.0.0 https://www.osgi.org/xmlns/blueprint/v1.0.0/blueprint.xsd\n");
		sb.append("       http://camel.apache.org/schema/blueprint http://camel.apache.org/schema/blueprint/camel-blueprint.xsd\">\n");
		sb.append("\n");
		sb.append("</blueprint>\n");
		return sb.toString();
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
