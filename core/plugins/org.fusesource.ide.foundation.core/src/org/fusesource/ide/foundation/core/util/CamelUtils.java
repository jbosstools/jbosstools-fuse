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

package org.fusesource.ide.foundation.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.common.project.facet.core.internal.FacetedProjectNature;
import org.fusesource.ide.foundation.core.internal.FoundationCoreActivator;
import org.fusesource.ide.foundation.core.xml.namespace.BlueprintNamespaceHandler;
import org.fusesource.ide.foundation.core.xml.namespace.FindCamelNamespaceHandler;
import org.fusesource.ide.foundation.core.xml.namespace.FindNamespaceHandlerSupport;
import org.fusesource.ide.foundation.core.xml.namespace.SpringNamespaceHandler;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * @author lhein
 */
public class CamelUtils {
	
	public static final String CAMEL_EDITOR_ID = "org.fusesource.ide.camel.editor";
	public static final String FUSE_CAMEL_CONTENT_TYPE = "org.fusesource.ide.camel.editor.camelContentType";
	public static final String SPRING_BEANS_NAMESPACE = "http://www.springframework.org/schema/beans";
	private static final String GLOBAL_BEAN = "bean";
	
	private static FindNamespaceHandlerSupport blueprintXmlMatcher = new BlueprintNamespaceHandler();
	private static FindNamespaceHandlerSupport springXmlMatcher = new SpringNamespaceHandler();
	private static FindNamespaceHandlerSupport camelXmlMatcher = new FindCamelNamespaceHandler();
	
	private CamelUtils(){
		// only static methods available
	}
	
	/**
	 * checks if the given file is a blueprint file or not
	 * @param filePath
	 * @return
	 */
	public static boolean isCamelContextFile(String filePath) {
		return matchesNamespace(filePath, camelXmlMatcher);
	}
	
	
	/**
	 * checks if the given file is a blueprint file or not
	 * @param filePath
	 * @return
	 */
	public static boolean isBlueprintFile(String filePath) {
		return matchesNamespace(filePath,  blueprintXmlMatcher);
	}
	
	/**
	 * checks if the given file is a spring file or not
	 * @param filePath
	 * @return
	 */
	public static boolean isSpringFile(String filePath) {
		return matchesNamespace(filePath, springXmlMatcher);
	}

	
	private static boolean matchesNamespace(String filePath, FindNamespaceHandlerSupport support) {
		boolean matches = false;
		if (filePath != null && filePath.trim().length()>0) {
			String rawPath;
			if (filePath.startsWith("file:")) {
				rawPath = filePath.substring("file:".length());
			} else {
				rawPath = filePath;
			}
			IPath f = Path.fromOSString(rawPath);
			if (f.toFile().exists() && f.toFile().isFile()) {
				// file exists, now check if its blueprint or spring
				IFile file = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(f);
				if (file != null) 
					matches = matches(support, file);
			}
		}
		
		return matches;
	}
	
	public static boolean matches(FindNamespaceHandlerSupport handler, IFile ifile) {
		try {
			File file = ResourceModelUtils.toFile(ifile);
			if (file != null) {
				handler.parseContents(new InputSource(new FileInputStream(file)));
				return handler.isNamespaceFound();
			}
		} catch (Exception e) {
			FoundationCoreActivator.pluginLog().logError("** Load failed. Using default model. **", e);
		}
		return false;
	}

	public static String getTagNameWithoutPrefix(Node node) {
		if (node == null || node.getNodeName() == null) {
			return null;
		}
		String prefix = node.getPrefix();
		String nodeName = node.getNodeName();
		String resVal = nodeName;
		if ((prefix != null && prefix.trim().length()>0) || nodeName.indexOf(':') != -1) {
			resVal = nodeName.substring(nodeName.indexOf(':')+1);
		}
		return resVal;
	}
	
	public static boolean startsWithNamespace(Node child, String namespace) {
		return child != null && child.getNamespaceURI() != null && child.getNamespaceURI().startsWith(namespace);
	}
	
	public static boolean startsWithOneOfNamespace(Node child, Collection<String> namespaces) {
		return namespaces.stream()
				.filter(namespace -> CamelUtils.startsWithNamespace(child, namespace))
				.findAny().isPresent();
	}
	
	public static boolean isCamelNamespaceElement(Node child) {
		return CamelUtils.startsWithNamespace(child, "http://camel.apache.org/schema/");
	}
	
	public static boolean isGlobalBean(Node child) {
		return GLOBAL_BEAN.equals(CamelUtils.getTagNameWithoutPrefix(child))
				&& CamelUtils.startsWithOneOfNamespace(child,
						Arrays.asList(
								BlueprintNamespaceHandler.NAMESPACEURI_OSGI_BLUEPRINT_HTTP,
								BlueprintNamespaceHandler.NAMESPACEURI_OSGI_BLUEPRINT_HTTPS,
								SPRING_BEANS_NAMESPACE));
	}
	
	/**
	 * Fetch the list of files in the project that match the camel content type. 
	 * This method looks at only the source folders if the project is a Java project.
	 * @param project
	 * @return list of camel files with content-type org.fusesource.ide.camel.editor.camelContentType
	 * @throws CoreException
	 */
	public static List<IFile> getFilesWithCamelContentType(IProject project) throws CoreException{ 
		final List<IFile> files = new ArrayList<>();
		if (project.hasNature(JavaCore.NATURE_ID)) {
			//limit the search to source folders
	        IJavaProject javaProject = JavaCore.create(project);
	        if(javaProject!=null){
	        	for(IPackageFragmentRoot ifr:javaProject.getAllPackageFragmentRoots()){
	        		if(ifr.getKind()==IPackageFragmentRoot.K_SOURCE){
						files.addAll(getFilesWithCamelContentTypeInResource(ifr.getCorrespondingResource()));
	        		}
	        	}
	        }
		}
		if (project.hasNature(FacetedProjectNature.NATURE_ID)) {
			// TODO: search in deployed resources
		}

		if (files.isEmpty()) {// or should we throw an error?
			files.addAll(getFilesWithCamelContentTypeInResource(project));
		}
		return files;
	}
	
	private static List<IFile> getFilesWithCamelContentTypeInResource(IResource root) throws CoreException {
		final List<IFile> files = new ArrayList<>();
		if (root != null) {
			root.accept(new IResourceVisitor() {		
				@Override
				public boolean visit(IResource resource) throws CoreException {
					if(resource instanceof IFile){
						IFile file = (IFile)resource;
						IContentDescription contentDescription  = null;
						try{
							contentDescription  = file.getContentDescription();
						} catch (CoreException e) {
							if (e.getStatus().getCode() == IResourceStatus.OUT_OF_SYNC_LOCAL) {
								//refresh and retry once
								resource.refreshLocal(IResource.DEPTH_ONE, null);
								contentDescription  = file.getContentDescription();
							} else {
								throw e;
							}
						}						
						if (contentDescription != null && 
							FUSE_CAMEL_CONTENT_TYPE.equals(contentDescription.getContentType().getId())) {
							files.add(file);
						}
					}
					return true; //depth infinite
				}
			});
		}
		return files;
	}
	
	/**
	 * retrieves the camel design editor
	 * 
	 * @return
	 */
	public static IEditorPart getDiagramEditor() {
		IWorkbench wb = PlatformUI.getWorkbench();
		if (wb != null) {
			IWorkbenchWindow wbw = wb.getActiveWorkbenchWindow();
			if (wbw != null) {
				IWorkbenchPage page = wbw.getActivePage();
				if (page != null && page.getActiveEditor() != null) {
					IEditorReference[] refs = page.getEditorReferences();
					for (IEditorReference ref : refs) {
						// we need to check if the id of the editor ref matches our editor id
						// and also if the active editor is equal to the ref editor otherwise we might pick
						// a wrong editor and return it...bad thing
						if (ref.getId().equals(CAMEL_EDITOR_ID) && 
							page.getActiveEditor().equals(ref.getEditor(false))) {
							// ok, we found a route editor and it is also the acitve editor
							return ref.getEditor(true);
						}
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * tries to figure out the used project
	 * 
	 * @return
	 */
	public static IProject getCurrentProject() {
		IEditorPart ep = getDiagramEditor();
		if (ep != null) {
			IProject wsProject = null;			
			try {
				Object designEditor = ep.getClass().getMethod("getDesignEditor", null).invoke(ep, null);
				if (designEditor != null) {
					Object prj = designEditor.getClass().getMethod("getWorkspaceProject", null).invoke(designEditor, null);
					if (prj != null && prj instanceof IProject) {
						wsProject = (IProject)prj;
						return wsProject;
					}
				}
			} catch (Exception ex) {
				FoundationCoreActivator.pluginLog().logError("Unable to retrieve the currently opened project.", ex);
			}
		}
		return null;
	}
}
