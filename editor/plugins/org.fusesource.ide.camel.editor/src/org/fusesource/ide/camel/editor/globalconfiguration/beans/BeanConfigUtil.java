/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.editor.globalconfiguration.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.internal.ui.wizards.NewClassCreationWizard;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.wizards.NewClassWizardPage;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.fusesource.ide.camel.editor.internal.CamelEditorUIActivator;
import org.fusesource.ide.camel.editor.internal.UIMessages;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelBean;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.camel.model.service.core.model.GlobalDefinitionCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.eips.GlobalBeanEIP;
import org.fusesource.ide.camel.model.service.core.util.PropertiesUtils;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.foundation.core.xml.namespace.BlueprintNamespaceHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * @author brianf
 *
 */
public class BeanConfigUtil {

	private static final String XMLNAMESPACE = "xmlns"; //$NON-NLS-1$
	
	/*
	 * This code reused from org.fusesource.ide.camel.editor.properties.creators.AbstractClassBasedParameterUICreator in the createBrowseButton method
	 */
	public String handleNewClassWizard(IProject project, Shell shell, String initialClassName) {	
		NewClassCreationWizard newClassCreationWizard = new NewClassCreationWizard();
		newClassCreationWizard.init(PlatformUI.getWorkbench(), new StructuredSelection(project));
		WizardDialog wd = new WizardDialog(shell, newClassCreationWizard);
		wd.create();
		NewClassWizardPage ncwp = (NewClassWizardPage) newClassCreationWizard.getPage("NewClassWizardPage"); //$NON-NLS-1$
		ncwp.setAddComments(true, true);
		if (!Strings.isEmpty(initialClassName)) {
			if (initialClassName.indexOf('.') > -1) {
				String packageName = initialClassName.substring(0, initialClassName.lastIndexOf('.'));
				String simpleClassName = initialClassName.substring(initialClassName.lastIndexOf('.') + 1);
				ncwp.setTypeName(simpleClassName, true);
				setInitialPackageFramentWithName(project, ncwp, packageName);
			} else {
				ncwp.setTypeName(initialClassName, true);
				setInitialPackageFrament(project, ncwp);
			}
		} else {
			setInitialPackageFrament(project, ncwp);
		}
		if (Window.OK == wd.open()) {
			String value = ncwp.getCreatedType().getFullyQualifiedName();
			if (value != null) {
				return value;
			}
		}
		return null;
	}	
	
	private void setInitialPackageFrament(final IProject project, NewClassWizardPage wp) {
		try {
			IJavaProject javaProject = (IJavaProject) project.getNature(JavaCore.NATURE_ID);
			if(javaProject != null){
				IPackageFragmentRoot fragroot = findPackageFragmentRoot(project, javaProject);
				wp.setPackageFragmentRoot(fragroot, true);
				wp.setPackageFragment(PropertiesUtils.getPackage(javaProject, fragroot), true);
			}
		} catch (Exception ex) {
			CamelEditorUIActivator.pluginLog().logError(ex);
		}
	}

	private void setInitialPackageFramentWithName(final IProject project, NewClassWizardPage wp, String packName) {
		try {
			IJavaProject javaProject = (IJavaProject) project.getNature(JavaCore.NATURE_ID);
			if(javaProject != null){
				IPackageFragmentRoot fragroot = findPackageFragmentRoot(project, javaProject);
				wp.setPackageFragmentRoot(fragroot, true);
				wp.setPackageFragment(fragroot.getPackageFragment(packName), true);
			}
		} catch (Exception ex) {
			CamelEditorUIActivator.pluginLog().logError(ex);
		}
	}

	private IPackageFragmentRoot findPackageFragmentRootWithFacade(final IProject project, IJavaProject javaProject) {
		IMavenProjectFacade facade = MavenPlugin.getMavenProjectRegistry().create(project, new NullProgressMonitor());
		if(facade != null){
			List<IPath> paths = facade.getCompileSourceLocations();
			for (IPath p : paths) {
				if (p == null)
					continue;
				IResource res = project.findMember(p);
				if (res != null) {
					return javaProject.getPackageFragmentRoot(res);
				}
			}
		}
		return null;
	}
	
	private IPackageFragmentRoot findPackageFragmentRoot(final IProject project, IJavaProject javaProject) throws CoreException {
		IPackageFragmentRoot fromFacade = findPackageFragmentRootWithFacade(project, javaProject);
		if (fromFacade != null) {
			return fromFacade;
		} else {
			IPackageFragmentRoot[] allPackageFragmentRoots = javaProject.getAllPackageFragmentRoots();
			if(allPackageFragmentRoots.length == 1) {
				return allPackageFragmentRoots[0];
			} else {
				IFolder tstFolder = project.getFolder("src/main/java"); //$NON-NLS-1$
	            IPackageFragmentRoot tstRoot = javaProject.getPackageFragmentRoot(tstFolder);
	            if (tstRoot.exists()) {
	            	return tstRoot;
	            } else {
	            	tstFolder.create(true, true, new NullProgressMonitor());
	            	
	            	// now refresh the package root to ensure we have the right fragment
	            	tstRoot = javaProject.getPackageFragmentRoot(tstFolder);
	            }
				return tstRoot;
			}
		}
	}
	
	public String handleClassBrowse(IProject project, Shell shell) {
		IJavaSearchScope scope = null;
		if (project != null) {
			IJavaProject jproject = JavaCore.create(project);
			if (jproject == null) {
				scope = SearchEngine.createWorkspaceScope();
			} else {
				scope = SearchEngine.createJavaSearchScope(new IJavaElement[] { jproject });
			}
		}

		try {
			SelectionDialog dialog = JavaUI.createTypeDialog(shell, null, scope,
					IJavaElementSearchConstants.CONSIDER_CLASSES, false, "*Bean"); //$NON-NLS-1$
			if (dialog.open() == SelectionDialog.OK) {
				Object[] result = dialog.getResult();
				if (result.length > 0 && result[0] instanceof IType) {
					return ((IType) result[0]).getFullyQualifiedName();
				}
			}
		} catch (JavaModelException e) {
			CamelEditorUIActivator.pluginLog().logError(e);
		}
		return null;
	}

	private String openStaticOrPublicMethodDialog(IJavaProject jproject, String className, Shell shell) throws JavaModelException {
		IType foundClass = jproject.findType(className);
		if (foundClass != null) {
			return openMethodDialog(shell, getStaticOrPublicMethods(foundClass), UIMessages.beanConfigUtilSelectStaticPublicMethod);
		}
		return null;
	}

	private IMethod[] getStaticOrPublicMethods(IType foundClass) throws JavaModelException {
		return Stream.of(foundClass.getMethods())
				.filter(method -> {
					try {
						return (Flags.isStatic(method.getFlags()) && Flags.isPublic(method.getFlags())) ||
								(Flags.isPublic(method.getFlags()));
					} catch (JavaModelException e) {
						CamelEditorUIActivator.pluginLog().logInfo("Issue when testing method for public & static flags.", e); //$NON-NLS-1$
						return false;
					}
				}).toArray(IMethod[]::new);
	}

	private String openStaticAndPublicNonVoidNonConstructorMethodDialog(IJavaProject jproject, String className, Shell shell) throws JavaModelException {
		IType foundClass = jproject.findType(className);
		if (foundClass != null) {
			return openMethodDialog(shell, getStaticPublicNonVoidNonConstructorMethods(foundClass), UIMessages.beanConfigUtilSelectStaticPublicMethod);
		}
		return null;
	}

	private IMethod[] getStaticPublicNonVoidNonConstructorMethods(IType foundClass) throws JavaModelException {
		return Stream.of(foundClass.getMethods())
				.filter(method -> {
					try {
						return Flags.isStatic(method.getFlags())
								&& Flags.isPublic(method.getFlags())
								&& !method.isConstructor()
								&& !Character.toString(Signature.C_VOID).equals(method.getReturnType());
					} catch (JavaModelException e) {
						CamelEditorUIActivator.pluginLog().logInfo("Issue when testing method for public & static flags.", e); //$NON-NLS-1$
						return false;
					}
				}).toArray(IMethod[]::new);
	}

	private IMethod[] getPublicNoArgMethods(IType foundClass) throws JavaModelException {
		return Stream.of(foundClass.getMethods())
				.filter(method -> {
					try {
						return isPublicNoArgNotConstructorMethod(method);
					} catch (JavaModelException e) {
						CamelEditorUIActivator.pluginLog().logInfo("Issue when testing method for public & no arguments.", e); //$NON-NLS-1$
						return false;
					}
				}).toArray(IMethod[]::new);
	}
	
	private String openPublicNotStaticNonVoidNonConstructorMethodDialog(IJavaProject jproject, String className, Shell shell) throws JavaModelException {
		IType foundClass = jproject.findType(className);
		if (foundClass != null) {
			return openMethodDialog(shell, getPublicNotStaticNonVoidNonConstructorMethods(foundClass), UIMessages.beanConfigUtilSelectPublicNotStaticMethod);
		}
		return null;
	}
	
	private IMethod[] getPublicNotStaticNonVoidNonConstructorMethods(IType foundClass) throws JavaModelException {
		return Stream.of(foundClass.getMethods())
				.filter(method -> {
					try {
						return !Flags.isStatic(method.getFlags())
								&& Flags.isPublic(method.getFlags())
								&& !method.isConstructor()
								&& !Character.toString(Signature.C_VOID).equals(method.getReturnType());
					} catch (JavaModelException e) {
						CamelEditorUIActivator.pluginLog().logInfo("Issue when testing method for public & not static flags.", e); //$NON-NLS-1$
						return false;
					}
				}).toArray(IMethod[]::new);
	}

	protected boolean isPublicNoArgNotConstructorMethod(IMethod method) throws JavaModelException {
		return Flags.isPublic(method.getFlags()) && method.getNumberOfParameters() == 0 && !method.isConstructor();
	}
	

	private IMethod[] getVoidPublicNoArgMethods(IType foundClass) throws JavaModelException {
		return Stream.of(foundClass.getMethods())
				.filter(method -> {
					try {
						return isPublicNoArgNotConstructorMethod(method)&& Signature.SIG_VOID.equals(method.getReturnType());
					} catch (JavaModelException e) {
						CamelEditorUIActivator.pluginLog().logInfo("Issue when testing method for public void & no arguments.", e); //$NON-NLS-1$
						return false;
					}
				}).toArray(IMethod[]::new);
	}

	private String openNoArgMethodDialog(IJavaProject jproject, String className, Shell shell) throws JavaModelException {
		IType foundClass = jproject.findType(className);
		if (foundClass != null) {
			return openMethodDialog(shell, getNoParamMethods(foundClass), UIMessages.beanConfigUtilNoParmMethodSelectionMessage);
		}
		return null;
	}

	private String openPublicNoArgMethodDialog(IJavaProject jproject, String className, Shell shell) throws JavaModelException {
		IType foundClass = jproject.findType(className);
		if (foundClass != null) {
			return openMethodDialog(shell, getPublicNoArgMethods(foundClass), UIMessages.beanConfigUtilNoParmMethodSelectionMessage);
		}
		return null;
	}
	
	private String openVoidPublicNoArgMethodDialog(IJavaProject jproject, String className, Shell shell) throws JavaModelException {
		IType foundClass = jproject.findType(className);
		if (foundClass != null) {
			return openMethodDialog(shell, getVoidPublicNoArgMethods(foundClass), UIMessages.beanConfigUtilNoParmAndVoidMethodSelectionMessage);
		}
		return null;
	}

	private String openMethodDialog(Shell shell, IMethod[] methods, String dialogMessage) {
		MethodSelectionDialog dialog = new MethodSelectionDialog(shell, new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_DEFAULT));
		dialog.setTitle(UIMessages.beanConfigUtilMethodSelectionDialogTitle);
		dialog.setMessage(dialogMessage);
		dialog.setElements(methods);
		dialog.setHelpAvailable(false);
		if (dialog.open()  == SelectionDialog.OK) {
			IMethod result = (IMethod) dialog.getFirstResult();
			return result.getElementName();
		}
		return null;
	}
	
	private boolean methodIsConstructor(IMethod method) {
		try {
			return method.isConstructor();
		} catch (JavaModelException e) {
			return false;
		}
	}
	
	private IMethod[] getNoParamMethods(IType foundClass) throws JavaModelException {
		return Stream.of(foundClass.getMethods())
				.filter(method -> method.getNumberOfParameters() == 0 && !methodIsConstructor(method))
				.toArray(IMethod[]::new);
	}

	private String openMethodDialog(IJavaProject jproject, String className, Shell shell) throws JavaModelException {
		IType foundClass = jproject.findType(className);
		if (foundClass != null) {
			return openMethodDialog(shell, getPublicNoArgMethods(foundClass), UIMessages.beanConfigUtilMethodSelectionMessage);
		}
		return null;
	}

	public String handleNoArgMethodBrowse(IProject project, String className, Shell shell) {
		if (project != null) {
			IJavaProject jproject = JavaCore.create(project);
			if (jproject.exists()) {
				try {
					return openNoArgMethodDialog(jproject, className, shell);
				} catch (JavaModelException e) {
					CamelEditorUIActivator.pluginLog().logError(e);
				}
			}
		}
		return null;
	}

	public String handlePublicNoArgMethodBrowse(IProject project, String className, Shell shell) {
		if (project != null) {
			IJavaProject jproject = JavaCore.create(project);
			if (jproject.exists()) {
				try {
					return openPublicNoArgMethodDialog(jproject, className, shell);
				} catch (JavaModelException e) {
					CamelEditorUIActivator.pluginLog().logError(e);
				}
			}
		}
		return null;
	}
	
	public String handleVoidPublicNoArgMethodBrowse(IProject project, String className, Shell shell) {
		if (project != null) {
			IJavaProject jproject = JavaCore.create(project);
			if (jproject.exists()) {
				try {
					return openVoidPublicNoArgMethodDialog(jproject, className, shell);
				} catch (JavaModelException e) {
					CamelEditorUIActivator.pluginLog().logError(e);
				}
			}
		}
		return null;
	}

	public String handleMethodBrowse(IProject project, String className, Shell shell) {
		if (project != null) {
			IJavaProject jproject = JavaCore.create(project);
			if (jproject.exists()) {
				try {
					return openMethodDialog(jproject, className, shell);
				} catch (JavaModelException e) {
					CamelEditorUIActivator.pluginLog().logError(e);
				}
			}
		}
		return null;
	}

	public String handlePublicOrStaticMethodBrowse(IProject project, String className, Shell shell) {
		if (project != null) {
			IJavaProject jproject = JavaCore.create(project);
			if (jproject.exists()) {
				try {
					return openStaticOrPublicMethodDialog(jproject, className, shell);
				} catch (JavaModelException e) {
					CamelEditorUIActivator.pluginLog().logError(e);
				}
			}
		}
		return null;
	}

	public String handlePublicStaticNonVoidNonConstructorMethodBrowse(IProject project, String className, Shell shell) {
		if (project != null) {
			IJavaProject jproject = JavaCore.create(project);
			if (jproject.exists()) {
				try {
					return openStaticAndPublicNonVoidNonConstructorMethodDialog(jproject, className, shell);
				} catch (JavaModelException e) {
					CamelEditorUIActivator.pluginLog().logError(e);
				}
			}
		}
		return null;
	}
	
	public String handlePublicNonStaticNonVoidNonConstructorMethodBrowse(IProject project, String className, Shell shell) {
		if (project != null) {
			IJavaProject jproject = JavaCore.create(project);
			if (jproject.exists()) {
				try {
					return openPublicNotStaticNonVoidNonConstructorMethodDialog(jproject, className, shell);
				} catch (JavaModelException e) {
					CamelEditorUIActivator.pluginLog().logError(e);
				}
			}
		}
		return null;
	}

	public Parameter createParameter(String name, String jType) {
		Parameter outParm = new Parameter();
		outParm.setName(name);
		outParm.setJavaType(jType);
		return outParm;
	}

	public boolean hasMethod(String methodName, IType type) {
		if (type == null) {
			return false;
		}

		SearchPattern pattern = SearchPattern.createPattern(
				methodName, 
				IJavaSearchConstants.METHOD, 
				IJavaSearchConstants.DECLARATIONS, 
				SearchPattern.R_EXACT_MATCH);

		IJavaSearchScope scope = SearchEngine.createJavaSearchScope(new IJavaElement[] { type });

		CountingSearchRequestor matchCounter = new CountingSearchRequestor();

		SearchEngine search = new SearchEngine();
		try {
			search.search(pattern, 
					new SearchParticipant[] { SearchEngine.getDefaultSearchParticipant() }, 
					scope, 
					matchCounter, 
					null);
		} catch (CoreException ce) {
			CamelEditorUIActivator.pluginLog().logError(UIMessages.beanConfigUtilMethodSelectionErrorNoTypeFound + ce.getMessage(), ce);
		} 

		return matchCounter.getNumMatch() > 0;
	}

	public IJavaProject getJavaProject(IProject project) {
		if (project != null) {
			IJavaProject jproject = JavaCore.create(project);
			if (jproject.exists()) {
				return jproject;
			}
		}
		return null;
	}


	public Object getAttributeValue(AbstractCamelModelElement element, String attrName) {
		if (element.getXmlNode() != null) {
			return getAttributeValue(element.getXmlNode(), attrName);
		}
		return null;
	}

	public Object getAttributeValue(Node element, String attrName) {
		if (element != null && element.hasAttributes()) {
			Node attrNode = element.getAttributes().getNamedItem(attrName);
			if (attrNode != null) {
				return attrNode.getNodeValue();
			}
		}
		return null;
	}

	public void setAttributeValue(AbstractCamelModelElement element, String attrName, String attrValue) {
		if (element.getXmlNode() != null) {
			setAttributeValue(element.getXmlNode(), attrName, attrValue);
		}
	}

	public void setAttributeValue(Node node, String attrName, String attrValue) {
		if (node != null) {
			Element e = (Element) node;
			Object oldValue = getAttributeValue(node, attrName);
			// if values are both null, no change
			if (oldValue == null && attrValue == null) {
				// no change
				return;
			}
			// if both values are same string, no change
			if (oldValue != null && attrValue != null) {
				String oldValueStr = (String) oldValue;
				if (oldValueStr.contentEquals(attrValue)) {
					// no change
					return;
				}
			}
			// otherwise we have a change, set new value or clear value
			if (!Strings.isEmpty(attrValue)) {
				e.setAttribute(attrName, attrValue);
			} else {
				e.removeAttribute(attrName);
			}
		}
	}

	public String getNamespace(Node node) {
		if (node != null) {
			String nsURI = node.getNamespaceURI();
			if (nsURI == null && node.getParentNode() != null) {
				return getNamespace(node.getParentNode());
			}
			if (nsURI != null) {
				return nsURI;
			}
		}
		return null;
	}
	
	public boolean isBlueprintConfig(Node node) {
		if (node != null) {
			String nsURI = getNamespace(node);
			if(!Strings.isEmpty(nsURI) && nsURI != null) {
				return nsURI.contains("blueprint"); //$NON-NLS-1$
			}
		}
		return false;
	}
	
	public String getArgumentTag(Node node) {
		if (node != null) {
			boolean isBlueprint = isBlueprintConfig(node);
			String tagName;
			if (isBlueprint) {
				tagName = GlobalBeanEIP.TAG_ARGUMENT;
			} else {
				tagName = GlobalBeanEIP.TAG_CONSTRUCTOR_ARG;
			}
			return tagName;
		}
		return null;
	}
	
	public String getFactoryBeanTag(Node node) {
		if (node != null) {
			boolean isBlueprint = isBlueprintConfig(node);
			String tagName;
			if (isBlueprint) {
				tagName = GlobalBeanEIP.PROP_FACTORY_REF;
			} else {
				tagName = GlobalBeanEIP.PROP_FACTORY_BEAN;
			}
			return tagName;
		}
		return null;
	}

	public String getArgumentTag(AbstractCamelModelElement camelElement) {
		if (camelElement instanceof CamelFile) {
			return getArgumentTag(camelElement.getRouteContainer().getXmlNode());
		}
		if (camelElement != null && camelElement.getXmlNode() != null) {
			return getArgumentTag(camelElement.getXmlNode());
		}
		return null;
	}

	public String getFactoryMethodAttribute() {
		return GlobalBeanEIP.PROP_FACTORY_METHOD;
	}
	
	public Element createBeanArgument(final Element inputElement, String type, String value) {
		String prefixNS = inputElement.getPrefix();
		String tagName = getArgumentTag(inputElement);
		Element propertyNode = inputElement.getOwnerDocument().createElementNS(prefixNS, tagName);
		if (!Strings.isEmpty(type)) {
			propertyNode.setAttribute(GlobalBeanEIP.ARG_TYPE, type);
		} else {
			propertyNode.removeAttribute(GlobalBeanEIP.ARG_TYPE);
		}
		if (!Strings.isEmpty(value)) {
			propertyNode.setAttribute(GlobalBeanEIP.ARG_VALUE, value);
		}
		return propertyNode;
	}
	
	public Element createBeanArgument(final CamelFile camelFile, String type, String value) {
		final String prefixNS = camelFile.getRouteContainer().getXmlNode().getPrefix();
		AbstractCamelModelElement newRoot = camelFile.getChildElements().get(0);
		String tagName = getArgumentTag(newRoot);
		Element propertyNode = newRoot.createElement(tagName, prefixNS);
		if (!Strings.isEmpty(type)) {
			propertyNode.setAttribute(GlobalBeanEIP.ARG_TYPE, type);
		} else {
			propertyNode.removeAttribute(GlobalBeanEIP.ARG_TYPE);
		}
		if (!Strings.isEmpty(value)) {
			propertyNode.setAttribute(GlobalBeanEIP.ARG_VALUE, value);
		}
		return propertyNode;
	}

	public void editBeanArgument(Element xmlElement, String type, String value) {
		setAttributeValue(xmlElement, GlobalBeanEIP.ARG_TYPE, type);
		if (!Strings.isEmpty(value)) {
			xmlElement.setAttribute(GlobalBeanEIP.PROP_VALUE, value);
		}
	}

	private String getFirstNSPrefixForURI(Node rootNode, String namespaceUri) {
		NamedNodeMap atts = rootNode.getAttributes();
		for (int i = 0; i < atts.getLength(); i++) {
			Node node = atts.item(i);
			String name = node.getNodeName();
			if (namespaceUri.equals(node.getNodeValue())
					&& (name != null && (XMLNAMESPACE.equals(name) || name.startsWith(XMLNAMESPACE + ":")))) { //$NON-NLS-1$
				if (name.startsWith(XMLNAMESPACE + ":")) { //$NON-NLS-1$
					return name.substring(name.indexOf(':') + 1);
				} else {
					return node.getPrefix();
				}
			}
		}
		return null;
	}
	
	private String getBeanPrefix(Node rootNode) {
		String blueprintPrefix = getFirstNSPrefixForURI(rootNode, BlueprintNamespaceHandler.NAMESPACEURI_OSGI_BLUEPRINT_HTTP);
		if (blueprintPrefix != null) {
			return blueprintPrefix;
		}
		String springPrefix = getFirstNSPrefixForURI(rootNode, org.fusesource.ide.foundation.core.util.CamelUtils.SPRING_BEANS_NAMESPACE);
		if (springPrefix != null) {
			return springPrefix;
		}
		return null;
	}

	public Element createBeanNode(final CamelFile camelFile, String id, String className) {
		// get NS prefix from parent document, not route container node
		final String prefixNS = 
				getBeanPrefix(camelFile.getRouteContainer().getXmlNode().getOwnerDocument().getDocumentElement());
		Element newBeanNode = camelFile.createElement(CamelBean.BEAN_NODE, prefixNS);
		newBeanNode.setAttribute(GlobalBeanEIP.PROP_ID, id);
		if (!Strings.isBlank(className)) {
			newBeanNode.setAttribute(GlobalBeanEIP.PROP_CLASS, className);
		}
		// default for both Blueprint and Spring global beans to "singleton" as the scope
		newBeanNode.setAttribute(GlobalBeanEIP.PROP_SCOPE, "singleton"); //$NON-NLS-1$
		return newBeanNode;
	}
	
	public Element createBeanNode(final CamelFile camelFile, String id, String className, String refId) {
		// get NS prefix from parent document, not route container node
		final String prefixNS = 
				getBeanPrefix(camelFile.getRouteContainer().getXmlNode().getOwnerDocument().getDocumentElement());
		Element newBeanNode = camelFile.createElement(CamelBean.BEAN_NODE, prefixNS);
		newBeanNode.setAttribute(GlobalBeanEIP.PROP_ID, id);
		if (!Strings.isBlank(className)) {
			newBeanNode.setAttribute(GlobalBeanEIP.PROP_CLASS, className);
		}
		if (!Strings.isBlank(refId)) {
			String beanTag =
					getFactoryBeanTag(camelFile.getRouteContainer().getXmlNode().getOwnerDocument().getDocumentElement());
			newBeanNode.setAttribute(beanTag, refId);
		}
		// default for both Blueprint and Spring global beans to "singleton" as the scope
		newBeanNode.setAttribute(GlobalBeanEIP.PROP_SCOPE, "singleton"); //$NON-NLS-1$
		return newBeanNode;
	}

	public Element createBeanProperty(String name, String value) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = dbf.newDocumentBuilder();
			Document doc = builder.newDocument();
			Element propertyNode = doc.createElement(GlobalBeanEIP.TAG_PROPERTY);
			propertyNode.setAttribute(GlobalBeanEIP.PROP_NAME, name);
			propertyNode.setAttribute(GlobalBeanEIP.PROP_VALUE, value);
			return propertyNode;
		} catch (ParserConfigurationException pse) {
			CamelEditorUIActivator.pluginLog().logError(UIMessages.beanConfigUtilMethodSelectionErrorCreatingXML, pse);
			return null;
		}
	}

	public Element createBeanProperty(final CamelFile camelFile, String name, String value) {
		final String prefixNS = camelFile.getRouteContainer().getXmlNode().getPrefix();
		AbstractCamelModelElement newRoot = camelFile.getChildElements().get(0);
		Element propertyNode = newRoot.createElement(GlobalBeanEIP.TAG_PROPERTY, prefixNS);
		propertyNode.setAttribute(GlobalBeanEIP.PROP_NAME, name);
		propertyNode.setAttribute(GlobalBeanEIP.PROP_VALUE, value);
		return propertyNode;
	}

	public Element createBeanProperty(final Element inputElement, String name, String value) {
		String prefixNS = inputElement.getPrefix();
		Element propertyNode = inputElement.getOwnerDocument().createElementNS(prefixNS, GlobalBeanEIP.TAG_PROPERTY);
		propertyNode.setAttribute(GlobalBeanEIP.PROP_NAME, name);
		propertyNode.setAttribute(GlobalBeanEIP.PROP_VALUE, value);
		return propertyNode;
	}

	public void addBeanArgument(Element beanParent, Element arg) {
		if (arg != null) {
			Element children = (Element) beanParent.getChildNodes();
			children.appendChild(arg);
		}
	}

	public void addBeanArgument(final CamelFile camelFile, Element beanParent, String type, String value) {
		if (!Strings.isBlank(value)) {
			Element argument = createBeanArgument(camelFile, type, value);
			addBeanArgument(beanParent, argument);
		}
	}
	
	public void addBeanProperty(Element beanParent, Element property) {
		if (property != null) {
			Element children = (Element) beanParent.getChildNodes();
			children.appendChild(property);
		}
	}

	public void addBeanProperty(final CamelFile camelFile, Element beanParent, String name, String value) {
		if (!Strings.isBlank(name)) {
			Element property = createBeanProperty(camelFile, name, value); 
			Element children = (Element) beanParent.getChildNodes();
			children.appendChild(property);
		}
	}
	
	public void editBeanProperty(Element xmlElement, String name, String value) {
		setAttributeValue(xmlElement, GlobalBeanEIP.PROP_NAME, name);
		if (!Strings.isEmpty(value)) {
			xmlElement.setAttribute(GlobalBeanEIP.PROP_VALUE, value);
		}
	}
	
	public String getClassNameFromReferencedCamelBean(AbstractCamelModelElement selectedEP, String refID) {
		if (!Strings.isEmpty(refID) && selectedEP != null) {
			Map<String, GlobalDefinitionCamelModelElement> globalDefs = 
					selectedEP.getCamelFile().getGlobalDefinitions();
			GlobalDefinitionCamelModelElement referencedBean = globalDefs.get(refID);
			if (referencedBean instanceof CamelBean) {
				return ((CamelBean) referencedBean).getClassName();
			}
		}
		return null;
	}
	
	public String[] removeStringFromStringArray(String[] input, String deleteMe) {
		return Stream.of(input)
			.filter(item -> !deleteMe.equals(item))
			.toArray(String[]::new);
	}
	
	public String[] removeRefsWithNoClassFromArray(String[] input, AbstractCamelModelElement selectedEP) {
		List<String> result = new ArrayList<>();
		for(String item : input) {
			String referencedClassName = getClassNameFromReferencedCamelBean(selectedEP, item);
			if(!Strings.isEmpty(referencedClassName) || Strings.isEmpty(item)) {
				result.add(item);
			}
		}
		return result.toArray(new String[result.size()]);
	}

	public String getBeanRef(AbstractCamelModelElement selectedEP) {
		if (selectedEP != null) {
			String beanRefTag = getFactoryBeanTag(selectedEP.getXmlNode());
			if (beanRefTag != null) {
				Object refParm = selectedEP.getParameter(beanRefTag);
				if (refParm instanceof String) {
					return (String) refParm;
				}
			}
		}
		return null;
	}



}
