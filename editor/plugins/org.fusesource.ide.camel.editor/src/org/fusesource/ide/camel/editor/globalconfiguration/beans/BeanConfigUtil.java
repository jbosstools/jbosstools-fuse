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
package org.fusesource.ide.camel.editor.globalconfiguration.beans;

import java.util.ArrayList;
import java.util.List;
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
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.fusesource.ide.camel.editor.internal.CamelEditorUIActivator;
import org.fusesource.ide.camel.editor.internal.UIMessages;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelBean;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.camel.model.service.core.model.eips.GlobalBeanEIP;
import org.fusesource.ide.camel.model.service.core.util.PropertiesUtils;
import org.fusesource.ide.foundation.core.util.Strings;
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
	private static final String BLUEPRINT_NS_URI = "http://www.osgi.org/xmlns/blueprint/v1.0.0"; //$NON-NLS-1$
	private static final String SPRING_NS_URI = "http://www.springframework.org/schema/beans"; //$NON-NLS-1$
	
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
			IPath[] paths = facade.getCompileSourceLocations();
			if (paths != null && paths.length > 0) {
				for (IPath p : paths) {
					if (p == null)
						continue;
					IResource res = project.findMember(p);
					if (res != null) {
						return javaProject.getPackageFragmentRoot(res);
					}
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
	            	tstFolder.create(false, true, null);
	            	
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

	private String openStaticPublicMethodDialog(IJavaProject jproject, String className, Shell shell) throws JavaModelException {
		IType foundClass = jproject.findType(className);
		if (foundClass != null) {
			return openMethodDialog(shell, getStaticPublicMethods(foundClass), UIMessages.beanConfigUtilSelectStaticPublicMethod);
		}
		return null;
	}

	private IMethod[] getStaticPublicMethods(IType foundClass) throws JavaModelException {
		return Stream.of(foundClass.getMethods())
				.filter(method -> {
					try {
						return Flags.isStatic(method.getFlags()) && Flags.isPublic(method.getFlags());
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
						return Flags.isPublic(method.getFlags()) && method.getNumberOfParameters() == 0;
					} catch (JavaModelException e) {
						CamelEditorUIActivator.pluginLog().logInfo("Issue when testing method for public & no arguments.", e); //$NON-NLS-1$
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

	private String openMethodDialog(Shell shell, IMethod[] methods, String dialogMessage) {
		MethodSelectionDialog dialog = new MethodSelectionDialog(shell, new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_DEFAULT));
		dialog.setTitle(UIMessages.beanConfigUtilMethodSelectionDialogTitle);
		dialog.setMessage(dialogMessage);
		dialog.setMultipleSelection(false);
		dialog.setEmptyListMessage(UIMessages.beanConfigUtilNoMethodsAvailable);
		dialog.setElements(methods);
		dialog.setHelpAvailable(false);
		if (dialog.open()  == SelectionDialog.OK) {
			IMethod result = (IMethod) dialog.getFirstResult();
			return result.getElementName();
		}
		return null;
	}
	
	private class MethodSelectionDialog extends ElementListSelectionDialog {

		public MethodSelectionDialog(Shell parent, ILabelProvider renderer) {
			super(parent, renderer);
		}
		@Override
	    public void setElements(Object[] elements) {
	    	super.setElements(elements);
	    	if (elements == null || elements.length == 0) {
	    		setMessage(UIMessages.beanConfigUtilNoMethodsAvailable);
	    	}
	    }		
	}

	private IMethod[] getNoParamMethods(IType foundClass) throws JavaModelException {
		return Stream.of(foundClass.getMethods())
				.filter(method -> method.getNumberOfParameters() == 0)
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

	public String handlePublicStaticMethodBrowse(IProject project, String className, Shell shell) {
		if (project != null) {
			IJavaProject jproject = JavaCore.create(project);
			if (jproject.exists()) {
				try {
					return openStaticPublicMethodDialog(jproject, className, shell);
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
	
	public String getArgumentTag(AbstractCamelModelElement camelElement) {
		if (camelElement instanceof CamelFile) {
			return getArgumentTag(camelElement.getRouteContainer().getXmlNode());
		}
		if (camelElement != null && camelElement.getXmlNode() != null) {
			return getArgumentTag(camelElement.getXmlNode());
		}
		return null;
	}

	public String getFactoryMethodAttribute(Node node) {
		if (node != null) {
			String nsURI = getNamespace(node);
			if(!Strings.isEmpty(nsURI) && nsURI != null) {
				boolean isBlueprint = nsURI.contains("blueprint"); //$NON-NLS-1$
				String tagName;
				if (isBlueprint) {
					tagName = GlobalBeanEIP.PROP_FACTORY_METHOD;
				} else {
					tagName = GlobalBeanEIP.PROP_FACTORY_BEAN;
				}
				return tagName;
			}
		}
		return null;
	}
	
	public String getFactoryMethodAttribute(AbstractCamelModelElement camelElement) {
		if (camelElement instanceof CamelFile) {
			return getFactoryMethodAttribute(camelElement.getRouteContainer().getXmlNode());
		}
		if (camelElement != null && camelElement.getXmlNode() != null) {
			return getFactoryMethodAttribute(camelElement.getXmlNode());
		}
		return null;
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

	private List<String> getNSPrefixes(Node rootNode, String namespaceUri) {
		List<String> prefixes = new ArrayList<>();
		NamedNodeMap atts = rootNode.getAttributes();
		for (int i = 0; i < atts.getLength(); i++) {
			Node node = atts.item(i);
			String name = node.getNodeName();
			if (namespaceUri.equals(node.getNodeValue())
					&& (name != null && (XMLNAMESPACE.equals(name) || name.startsWith(XMLNAMESPACE + ":")))) { //$NON-NLS-1$
				if (name.startsWith(XMLNAMESPACE + ":")) { //$NON-NLS-1$
					String woPrefix = name.substring(name.indexOf(':') + 1);
					prefixes.add(woPrefix);
				} else {
					prefixes.add(node.getPrefix());
				}
			}
		}
		return prefixes;
	}
	
	private String getNSPrefixForURI(Node rootNode, String namespaceUri) {
		List<String> prefixes = getNSPrefixes(rootNode, namespaceUri);
		if (!prefixes.isEmpty()) {
			return prefixes.get(0);
		}
		return null;
	}
	
	private String getBeanPrefix(Node rootNode) {
		String blueprintPrefix = getNSPrefixForURI(rootNode, BLUEPRINT_NS_URI);
		if (blueprintPrefix != null) {
			return blueprintPrefix;
		}
		String springPrefix = getNSPrefixForURI(rootNode, SPRING_NS_URI);
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
}
