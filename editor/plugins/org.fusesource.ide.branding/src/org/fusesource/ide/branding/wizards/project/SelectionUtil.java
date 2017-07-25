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

package org.fusesource.ide.branding.wizards.project;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.PlatformUI;


/**
 * Helper methods to deal with workspace resources passed as navigator selection to actions and wizards.
 */
public class SelectionUtil {
  public static final int UNSUPPORTED = 0;

  public static final int PROJECT_WITH_NATURE = 1;

  public static final int PROJECT_WITHOUT_NATURE = 2;

  public static final int POM_FILE = 4;

  public static final int JAR_FILE = 8;

  public static final int WORKING_SET = 16;

  /** Checks which type the given selection belongs to. */
  public static int getSelectionType(IStructuredSelection selection) {
    int type = UNSUPPORTED;
    if(selection != null) {
      for(Iterator<?> it = selection.iterator(); it.hasNext();) {
        int elementType = getElementType(it.next());
        if(elementType == UNSUPPORTED) {
          return UNSUPPORTED;
        }
        type |= elementType;
      }
    }
    return type;
  }

  /** Checks which type the given element belongs to. */
  public static int getElementType(Object element) {
	/*
	  IProject project = getType(element, IProject.class);
    if(project != null) {
      try {
        if(project.hasNature(IMavenConstants.NATURE_ID)) {
          return PROJECT_WITH_NATURE;
        }
        return PROJECT_WITHOUT_NATURE;
      } catch(CoreException e) {
        // ignored
      }
    }

    IFile file = getType(element, IFile.class);
    if(file != null) {
      if(IMavenConstants.POM_FILE_NAME.equals(file.getFullPath().lastSegment())) {
        return POM_FILE;
      }
    }

    ArtifactKey artifactKey  = getType(element, ArtifactKey.class);
    if(artifactKey != null) {
      return JAR_FILE;
    }
    */

    IWorkingSet workingSet = getType(element, IWorkingSet.class);
    if(workingSet!=null) {
      return WORKING_SET;
    }
    
    return UNSUPPORTED;
  }

  /**
   * Checks if the object belongs to a given type and returns it or a suitable adapter.
   */
  @SuppressWarnings("unchecked")
  public static <T> T getType(Object element, Class<T> type) {
    if(element==null) {
      return null;
    }
    if(type.isInstance(element)) {
      return (T) element;
    }
    if(element instanceof IAdaptable) {
      T adapter = (T) ((IAdaptable) element).getAdapter(type);
      if(adapter != null) {
        return adapter;
      }
    }
    return (T) Platform.getAdapterManager().getAdapter(element, type);
  }

  public static IPath getSelectedLocation(IStructuredSelection selection) {
    Object element = selection == null ? null : selection.getFirstElement();

    IPath path = getType(element, IPath.class);
    if(path != null) {
      return path;
    }

    IResource resource = getType(element, IResource.class);
    if(resource != null) {
      return resource.getLocation();
    }
    
//    IPackageFragmentRoot fragment = getType(element, IResource.class);
//    if(fragment != null) {
//      IJavaProject javaProject = fragment.getJavaProject();
//      if(javaProject != null) {
//        IResource resource = getType(javaProject, IResource.class);
//        if(resource != null) {
//          return resource.getProject().getProject().getLocation();
//        }
//      }
//    }
    
    return null; 
  }

  public static IWorkingSet getSelectedWorkingSet(IStructuredSelection selection) {
    Object element = selection == null ? null : selection.getFirstElement();
    if(element == null) {
      return null;
    }

    IWorkingSet workingSet = getType(element, IWorkingSet.class);
    if(workingSet != null) {
      return workingSet;
    }

    IResource resource = getType(element, IResource.class);
    if(resource != null) {
      return getWorkingSet(resource.getProject());
    }

    return null;

//    IResource resource = getType(element, IResource.class);
//    if(resource != null) {
//      return getWorkingSet(resource);
//    }
    
//    IPackageFragmentRoot fragment = getType(element, IPackageFragmentRoot.class);
//    if(fragment != null) {
//      IJavaProject javaProject = fragment.getJavaProject();
//      if(javaProject != null) {
//        IResource resource = getType(javaProject, IResource.class);
//        if(resource != null) {
//          return getWorkingSet(resource.getProject());
//        }
//      }
//    }
  }

  public static IWorkingSet getWorkingSet(Object element) {
    IWorkingSetManager workingSetManager = PlatformUI.getWorkbench().getWorkingSetManager();
    for(IWorkingSet workingSet : workingSetManager.getWorkingSets()) {
      for(IAdaptable adaptable : workingSet.getElements()) {
        if(adaptable.getAdapter(IResource.class) == element) {
          return workingSet;
        }
      }
    }
    return null;
  }

  public static List<IWorkingSet> getAssignedWorkingSets(Object element) {
    List<IWorkingSet> list = new ArrayList<IWorkingSet>();
    IWorkingSetManager workingSetManager = PlatformUI.getWorkbench().getWorkingSetManager();
    for(IWorkingSet workingSet : workingSetManager.getWorkingSets()) {
      for(IAdaptable adaptable : workingSet.getElements()) {
        if(adaptable.getAdapter(IResource.class) == element) {
          list.add(workingSet);
        }
      }
    }
    return list;
  }

}
