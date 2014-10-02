/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.editor.propertysheet;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.corext.util.JavaConventionsUtil;
import org.fusesource.ide.camel.editor.propertysheet.model.CamelComponent;
import org.fusesource.ide.camel.editor.propertysheet.model.CamelComponentUriParameter;
import org.fusesource.ide.camel.editor.propertysheet.model.CamelComponentUriParameterKind;
import org.fusesource.ide.camel.editor.propertysheet.model.CamelComponentUtils;
import org.fusesource.ide.camel.model.Endpoint;

/**
 * @author lhein
 */
public class PropertiesUtils {
    /**
     * 
     * @param kind
     * @return
     */
    public static List<CamelComponentUriParameter> getPropertiesFor(Endpoint selectedEP, CamelComponentUriParameterKind kind) {
        ArrayList<CamelComponentUriParameter> result = new ArrayList<CamelComponentUriParameter>();

        if (selectedEP != null && selectedEP.getUri() != null) {
            int protocolSeparatorIdx = selectedEP.getUri().indexOf(":");
            if (protocolSeparatorIdx != -1) {
                CamelComponent componentModel = CamelComponentUtils.getComponentModel(selectedEP.getUri().substring(0, protocolSeparatorIdx));
                if (componentModel != null) {
                    for (CamelComponentUriParameter p : componentModel.getUriParameters()) {
                        if (p.getKind().equals(kind)) {
                            result.add(p);
                        }
                    }
                }
            }
        }

        return result;
    }

    /**
     * 
     * @param p
     * @return
     */
    public static String getPropertyFromUri(Endpoint selectedEP, CamelComponentUriParameter p) {
        int idx = selectedEP.getUri().indexOf(p.getName() + "=");
        if (idx != -1) {
            return selectedEP.getUri().substring(idx + (p.getName() + "=").length(),
                    selectedEP.getUri().indexOf('&', idx + 1) != -1 ? selectedEP.getUri().indexOf('&', idx + 1) : selectedEP.getUri().length());
        }
        return null;
    }

    /**
     * 
     * @param p
     * @return
     */
    public static Object getTypedPropertyFromUri(Endpoint selectedEP, CamelComponentUriParameter p) {
        String val = getPropertyFromUri(selectedEP, p);

        if (CamelComponentUtils.isBooleanProperty(p)) {
            return Boolean.parseBoolean(val);
        }

        if (CamelComponentUtils.isTextProperty(p)) {
            return val;
        }

        if (CamelComponentUtils.isNumberProperty(p)) {
            return val;
        }

        return null;
    }
    
    /**
     * updates the uri for the changed value
     * 
     * @param p
     * @param value
     */
    public static void updateURIParams(Endpoint selectedEP, CamelComponentUriParameter p, Object value) {
//        if (p.getName().equals(EndpointPropertyModel.PROTOCOL_PROPERTY) && CamelComponentUtils.isChoiceProperty(p)) {
//            String oldProtocol = getUsedProtocol();
//            if (oldProtocol.equalsIgnoreCase(value.toString()) == false) {
//                // protocol changed - update uri
//                selectedEP.setUri(selectedEP.getUri().replaceFirst(oldProtocol, value.toString()));
//            }
//        } else {
            boolean valueDeleted = value == null || value.toString().trim().length()<1;
            String val = getPropertyFromUri(selectedEP, p);
            if (val != null) {
                // special replace logic needed as special expression chars can break the replacement
                String key = String.format("%s=", p.getName());
                int idx = selectedEP.getUri().indexOf(key);
                String newUri = "";
                boolean firstParam = selectedEP.getUri().charAt(idx-1) == '?';
                newUri = valueDeleted ? selectedEP.getUri().substring(0, firstParam ? idx : idx-1) : selectedEP.getUri().substring(0, idx + key.length());
                if (!valueDeleted) newUri += value.toString();
                if (valueDeleted && firstParam) {
                    newUri += selectedEP.getUri().substring(idx + key.length() + val.length() + 1);
                } else {
                    newUri += selectedEP.getUri().substring(idx + key.length() + val.length());
                }
                selectedEP.setUri(newUri);
            } else {
                String newUri = selectedEP.getUri();
                if (selectedEP.getUri().indexOf('?') == -1) {
                    newUri += '?';
                }
                if (selectedEP.getUri().indexOf('=') != -1) {
                    newUri += '&';
                }
                newUri += String.format("%s=%s", p.getName(), value.toString());
                selectedEP.setUri(newUri);
            }
//        }
    }
    
    public static String getUsedProtocol(Endpoint selectedEP) {
        return selectedEP.getUri().substring(0, selectedEP.getUri().indexOf(':'));
    }
    
    /**
     * Checks if the package field has to be pre-filled in this page and returns the package
     * fragment to be used for that. The package fragment has the name of the project if the source
     * folder does not contain any package and if the project name is a valid package name. If the
     * source folder contains exactly one package then the name of that package is used as the
     * package fragment's name. <code>null</code> is returned if none of the above is applicable.
     * 
     * @param javaProject the containing Java project of the selection used to initialize this page
     * 
     * @return the package fragment to be pre-filled in this page or <code>null</code> if no
     *         suitable package can be suggested for the given project
     * 
     * @since 3.9
     */
    public static IPackageFragment getPackage(IJavaProject javaProject, final IPackageFragmentRoot pkgFragmentRoot) {
        String packName= null;
        IJavaElement[] packages= null;
        try {
            if (pkgFragmentRoot != null && pkgFragmentRoot.exists()) {
                packages= pkgFragmentRoot.getChildren();
                if (packages.length == 1) { // only default package -> use Project name
                    packName= javaProject.getElementName();
                    // validate package name
                    IStatus status= validatePackageName(packName, javaProject);
                    if (status.getSeverity() == IStatus.OK) {
                        return pkgFragmentRoot.getPackageFragment(packName);
                    }
                } else {
                    int noOfPackages= 0;
                    IPackageFragment thePackage= null;
                    for (final IJavaElement pack : packages) {
                        IPackageFragment pkg= (IPackageFragment) pack;
                        // ignoring empty parent packages and default package
                        if ((!pkg.hasSubpackages() || pkg.hasChildren()) && !pkg.isDefaultPackage()) {
                            noOfPackages++;
                            thePackage= pkg;
                            if (noOfPackages > 1) {
                                return null;
                            }
                        }
                    }
                    if (noOfPackages == 1) { // use package name
                        packName= thePackage.getElementName();
                        return pkgFragmentRoot.getPackageFragment(packName);
                    }
                }
            }
        } catch (JavaModelException e) {
            // fall through
        }
        return null;
    }

    public static IStatus validatePackageName(String text, IJavaProject project) {
        if (project == null || !project.exists()) {
            return JavaConventions.validatePackageName(text, JavaCore.VERSION_1_3, JavaCore.VERSION_1_3);
        }
        return JavaConventionsUtil.validatePackageName(text, project);
    }
}
