/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xml.core.internal.validation.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.validation.internal.operations.ValidatorManager;
import org.eclipse.wst.validation.internal.operations.WorkbenchContext;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.xml.core.internal.Logger;


/**
 * A helper class for validation in the validation framework.
 * 
 * @author Ernest Mah (ernest@ca.ibm.com)
 * @author Lawrence Mandel, IBM
 */
public class Helper extends WorkbenchContext
{
  public static final String GET_PROJECT_FILES = "getAllFiles"; //$NON-NLS-1$
  public static final String GET_FILE = "getFile"; //$NON-NLS-1$
  //dw private static final IContainer[] NO_CONTAINERS = new IContainer[0];
  public static final String VALIDATION_MARKER = "org.eclipse.wst.validation.problemmarker"; //$NON-NLS-1$
  public static final String VALIDATION_MARKER_OWNER = "owner";   //$NON-NLS-1$

  /**
   * Constructor.
   */
  public Helper()
  {
    super();

    // the following will register the helper's symbolic methods
    Class [] args = new Class[1] ;
    args[0] = String.class ;  // a string argument denoting a specific JSP.
    
    registerModel(GET_FILE, "getFile", args);//$NON-NLS-1$
    registerModel(GET_PROJECT_FILES, "getFiles", args);//$NON-NLS-1$
  }

  /**
   * Get the IFile for the given filename.
   * 
   * @param filename The name of the file to retrieve.
   * @return An IFile representing the file specified or null if it can't be resolved.
   */
  public IFile getFile(String filename)
  {
    //    System.out.println("file name = " + filename);
    IResource res = getProject().findMember(filename, true); // true means include phantom resources
    if (res instanceof IFile) 
    {
      return (IFile) res;
    }
    return null;
  }
  
  /**
   * Get the collection of files from the project that are relevant for the
   * validator with the given class name.
   * 
   * @param validatorClassName The name of the validator class.
   * @return The collection of files relevant for the validator class specified.
   */
  public Collection getFiles(String validatorClassName)
  {
    IProject project = getProject();
    List files = new ArrayList();
    getFiles(files, project, validatorClassName);
    return files;
  }

  /**
   * Get the collection of files from the project that are relevant for the
   * validator with the given class name.
   * 
   * @param files The files relevant for the class name.
   * @param resource The resource to look for files in.
   * @param validatorClassName The name of the validator class.
   */
  protected void getFiles(Collection files, IContainer resource, String validatorClassName)
  {
    try
    {
      IResource [] resourceArray = resource.members(false);
      for (int i=0; i<resourceArray.length; i++)
      {       
        if (ValidatorManager.getManager().isApplicableTo(validatorClassName, resourceArray[i])) 
        {
          if (resourceArray[i] instanceof IFile) 
          {
            files.add(resourceArray[i]);
          }
        }
        if (resourceArray[i].getType() == IResource.FOLDER)
         getFiles(files,(IContainer)resourceArray[i], validatorClassName) ;
      }
    }
    catch (Exception e) {
    	Logger.logException(e);
    }
  }

  
/**
 * Return the name of the resource, without the project-specific information 
 * in front.
 *
 * This method is used by ValidationOperation to calculate the non-environment 
 * specific names of the files. Only the IWorkbenchContext implementation knows how 
 * much information to strip off of the IResource name. For example, if there is
 * an EJB Project named "MyEJBProject", and it uses the default names for the 
 * source and output folders, "source" and "ejbModule", respectively, then the
 * current implementation of EJB Helper knows how much of that structure is 
 * eclipse-specific. 
 *
 * Since the "source" folder contains Java source files, a portable name would
 * be the fully-qualified name of the Java class, without the eclipse-specific
 * project and folder names in front of the file name. The EJBHelper knows that 
 * everything up to the "source" folder, for example, can be removed, because, 
 * according to the definition of the EJB Project, everything contained
 * in the source folder is java source code. So if there is an IResource in an
 * EJB Project named "/MyEJBProject/source/com/ibm/myclasses/MyJavaFile.java",
 * this method would make this name portable by stripping off the
 * "/MyEJBProject/source", and returning "com/ibm/myclasses/MyJavaFile.java".
 *
 * The output of this method is used by the ValidationOperation, when it
 * is calculating the list of added/changed/deleted file names for incremental
 * validation. If getPortableName(IResource) returns null, that means
 * that the IWorkbenchContext's implementation does not support that particular
 * type of resource, and the resource should not be included in the array of
 * IFileDelta objects in the IValidator's "validate" method. 
 * 
 * @param resource The resource to get the name from.
 * @return The portable name of the resource.
 */
public String getPortableName(IResource resource)
  {
    //    System.out.println("get portablename for " + resource);
    return resource.getProjectRelativePath().toString();
  }

/**
 * When an IValidator associates a target object with an IMessage,
 * the WorkbenchReporter eventually resolves that target object
 * with an IResource. Sometimes more than one target object resolves
 * to the same IResource (usually the IProject, which is the default
 * IResource when an IFile cannot be found). This method is called,
 * by the WorkbenchReporter, so that the WorkbenchReporter can 
 * distinguish between the IMessages which are on the same IResource, 
 * but refer to different target objects. This is needed for the 
 * removeAllMessages(IValidator, Object) method, so that when one
 * target object removes all of its messages, that it doesn't remove
 * another target object's messages.
 *
 * This method may return null only if object is null. Otherwise, an
 * id which can uniquely identify a particular object must be returned.
 * The id needs to be unique only within one particular IValidator.
 * 
 * @param object The object from which to get the name.
 * @return The name of the object or null if the object is null.
 */
public String getTargetObjectName(Object object)
  {
    if (object == null) 
    {
      return null;
    }
    
    //    System.out.println("get targetname for " + object);
    return object.toString();
  }
  
  /**
   * Delete the markers of the specified type from the specified resource.
   * 
   * @param resource The resource to delete the markers from.
   * @param markerType The type of markers to delete from the resource.
   * @param attributeName The name of the attribute which the markers must have to be deleted.
   * @param attributeValue The value of the attribute corresponding to attributeName which the markers must have to be deleted.
   * @throws CoreException
   */
  public static void deleteMarkers(IResource resource, String markerType, final String attributeName, final Object attributeValue) throws CoreException
  {          
    final IMarker[] v400Markers = resource.findMarkers(IMarker.PROBLEM, false, IResource.DEPTH_INFINITE);
    final IMarker[] markers = resource.findMarkers(markerType, true, IResource.DEPTH_INFINITE);
    IWorkspaceRunnable op = new IWorkspaceRunnable() 
     {
       public void run(IProgressMonitor progressMonitor) throws CoreException 
       {    
         // this fixes defect 193406
         // here we remove markers that may have been added by the v400 code
         // hopefully the '.markers' metadata files will be removed for the V5 install
         // and this kludge will not be needed there
         for (int i = 0; i < v400Markers.length; i++)
         {
           IMarker marker = markers[i];           
           marker.delete();           
         }
    
         for (int i = 0; i < markers.length; i++) 
         {
           IMarker marker = markers[i];
           
           Object value = marker.getAttribute(attributeName);
           if (value != null &&
               value.equals(attributeValue)) 
           {
             marker.delete();
           }
         }
       }
     };
    
    try
    {
      ResourcesPlugin.getWorkspace().run(op, null);
    }
    catch (Exception e) {
    	Logger.logException(e);
    }
  }
  
  /**
   * Get the validation framework severity for the given severity.
   * 
   * @param severity The severity to convert to validation framework severity.
   * @return The validation framework severity for the given severity.
   */
  static public int getValidationFrameworkSeverity(int severity)
  {
    switch (severity)
    {
      case IMarker.SEVERITY_ERROR:
        return IMessage.HIGH_SEVERITY;
      case IMarker.SEVERITY_WARNING:
        return IMessage.NORMAL_SEVERITY;
      case IMarker.SEVERITY_INFO:
        return IMessage.LOW_SEVERITY;
    }
    return IMessage.LOW_SEVERITY;  
  }
}
