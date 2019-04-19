/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.model.service.core.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.content.XMLContentDescriber;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.fusesource.ide.camel.model.service.core.internal.CamelModelServiceCoreActivator;
import org.fusesource.ide.camel.model.service.core.internal.Trace;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.foundation.core.util.CamelUtils;
import org.fusesource.ide.foundation.core.xml.namespace.CamelNamespaceXmlContentDescriber;
import org.fusesource.ide.foundation.ui.io.CamelXMLEditorInput;

public class CamelFilesFinder {
	
	private static final String COM_SPRINGSOURCE_STS_CONFIG_UI_BEAN_CONFIG_FILE_CONTENT_TYPE = "com.springsource.sts.config.ui.beanConfigFile"; //$NON-NLS-1$
	
	/**
	 * @param resource the resource in which the search occurs
	 * @return the Set of IFile with Camel Content Type
	 */
	public Set<IFile> findFiles(IResource resource) {
		boolean isPerformanceTraceOptionActivated = Trace.getInstance(CamelModelServiceCoreActivator.getDefault()).isPerformanceTraceOptionActivated();
		long initialTime = 0;
		if (isPerformanceTraceOptionActivated) {
			initialTime = System.currentTimeMillis();
		}
		Set<IFile> res = new HashSet<>();
		if (resource instanceof IContainer && resource.exists()) {
			try {
				IResource[] children = ((IContainer)resource).members();
				for (IResource f : children) {
					if (f instanceof IContainer) {
						if (!isWorkProjectFolder(resource.getProject(), f) && 
							!isTestProjectFolder(resource.getProject(), f)) {
							res.addAll(findFiles(f));
						}
					} else {
						IFile ifile = (IFile)f;
						try {
							if (isFuseCamelContentType(ifile)) {
								res.add(ifile);
							}
						} catch (CoreException e) {
							CamelModelServiceCoreActivator.pluginLog().logError(e);
						}
					}
				}
			} catch (CoreException e1) {
				CamelModelServiceCoreActivator.pluginLog().logError(e1);
			}
		}
		if(isPerformanceTraceOptionActivated) {
			long totalTime = System.currentTimeMillis() - initialTime;
			Trace.trace(Trace.PERFORMANCE_TRACE_OPTION, "Time (ms) spent on resource "+ resource.getName() + " to determine if it is/contains Camel files : " + totalTime);
		}
		return res;
	}

	public boolean isWorkProjectFolder(IProject project, IResource f) {
		String resourceName = f.getName();
		return ("target".equalsIgnoreCase(resourceName) || "bin".equalsIgnoreCase(resourceName))
				&& f.getParent().getName().equalsIgnoreCase(project.getName());
	}
	
	/**
	 * checks if the resource f is projectname/src/test/ folder
	 * 
	 * @param project
	 * @param f
	 * @return
	 */
	private boolean isTestProjectFolder(IProject project, IResource f) {
		String resourceName = f.getName();
		return "test".equalsIgnoreCase(resourceName) && 
			   "src".equalsIgnoreCase(f.getParent().getName()) && 
			   f.getParent().getParent().getName().equalsIgnoreCase(project.getName());
	}

	/**
	 * @param ifile
	 * @return if the specified IFile has the Fuse Camel Content Type
	 * @throws CoreException
	 */
	public boolean isFuseCamelContentType(IFile ifile) throws CoreException {
		if(isFileReadyForCheck(ifile) && ifile.getName().endsWith(".xml")){
			try {
				ByteArrayInputStream markableAndResettableStream = createMarkableAndResetableInputStream(ifile);
				return XMLContentDescriber.VALID == new CamelNamespaceXmlContentDescriber().describe(markableAndResettableStream, null);
			} catch (IOException e) {
				CamelModelServiceCoreActivator.pluginLog().logInfo("Cannot check Content type of "+ ifile.getName(), e); //$NON-NLS-1$
			}
		}
		return false;
	}

	private ByteArrayInputStream createMarkableAndResetableInputStream(IFile ifile) throws IOException {
		return new ByteArrayInputStream(Files.readAllBytes(ifile.getLocation().toFile().toPath()));
	}

	private boolean isFileReadyForCheck(IFile ifile) {
		return ifile != null
				&& ifile.isSynchronized(IResource.DEPTH_ZERO)
				&& ifile.isLocal(IResource.DEPTH_ZERO);
	}
	
	/**
	 * this method checks if the given file is opened in one of the camel editors
	 * 
	 * @param file
	 * @return
	 */
	public static CamelFile getFileFromEditor(IFile file) {
		for (IWorkbenchWindow window : PlatformUI.getWorkbench().getWorkbenchWindows()) {
		    for (IWorkbenchPage page : window.getPages()) {
		        for (IEditorReference editor : page.getEditorReferences()) {
		            if (editor.getId().equals(CamelUtils.CAMEL_EDITOR_ID)) {
		            	IEditorPart oEditor = editor.getEditor(false);
		            	if (oEditor != null) {
			            	CamelXMLEditorInput editorInput = oEditor.getEditorInput() != null ? (CamelXMLEditorInput)oEditor.getEditorInput() : null;
			            	if (editorInput != null && editorInput.getCamelContextFile().equals(file)) {
			            		// file is currently opened in editor -> use its model
			            		return oEditor.getAdapter(CamelFile.class);
			            	}
		            	}
		            }
		        }
		    }
		}
		return null;
	}
}
