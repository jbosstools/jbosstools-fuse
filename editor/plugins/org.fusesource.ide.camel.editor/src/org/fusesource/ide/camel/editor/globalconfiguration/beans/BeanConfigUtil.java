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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.internal.ui.viewsupport.JavaUILabelProvider;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.actions.OpenNewClassWizardAction;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.fusesource.ide.camel.editor.internal.CamelEditorUIActivator;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelBean;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.foundation.core.util.Strings;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author brianf
 *
 */
public class BeanConfigUtil {

	public String handleNewClassWizard(IProject project, Shell shell) {
		OpenNewClassWizardAction action = new OpenNewClassWizardAction();
		action.setShell(shell);
        action.setSelection(new StructuredSelection(project));
		action.run();
        IType type = (IType)action.getCreatedElement();
        if (type != null) {
			return type.getFullyQualifiedName();
		}
        return null;
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

	private String openNoArgMethodDialog(IJavaProject jproject, String className, Shell shell) throws JavaModelException {
		IType foundClass = jproject.findType(className);
		if (foundClass != null) {
			IMethod[] methods = foundClass.getMethods();
			ArrayList<IMethod> noParamsList = new ArrayList<>();
			for (int i = 0; i < methods.length; i++) {
				IMethod iMethod = methods[i];
				if (iMethod.getNumberOfParameters() == 0) {
					noParamsList.add(iMethod);
				}
			}
			methods = new IMethod[noParamsList.size()];
			methods = noParamsList.toArray(methods);
			ElementListSelectionDialog dialog = new ElementListSelectionDialog(shell, new JavaUILabelProvider());
			dialog.setTitle("Method Selection");
			dialog.setMessage("Select a no-parameter method:");
			dialog.setElements(methods);
			if (dialog.open()  == SelectionDialog.OK) {
				IMethod result = (IMethod) dialog.getFirstResult();
				return result.getElementName();
			}
		}
		return null;
	}

	private String openMethodDialog(IJavaProject jproject, String className, Shell shell) throws JavaModelException {
		IType foundClass = jproject.findType(className);
		if (foundClass != null) {
			IMethod[] methods = foundClass.getMethods();
			ElementListSelectionDialog dialog = new ElementListSelectionDialog(shell, new JavaUILabelProvider());
			dialog.setTitle("Method Selection");
			dialog.setMessage("Select a method:");
			dialog.setElements(methods);
			if (dialog.open()  == SelectionDialog.OK) {
				IMethod result = (IMethod) dialog.getFirstResult();
				return result.getElementName();
			}
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
			CamelEditorUIActivator.pluginLog().logError("Couldn't find type: " + ce.getMessage());
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
				tagName = CamelBean.TAG_ARGUMENT;
			} else {
				tagName = CamelBean.TAG_CONSTRUCTOR_ARG;
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
					tagName = CamelBean.PROP_FACTORY_METHOD;
				} else {
					tagName = CamelBean.PROP_FACTORY_BEAN;
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
			propertyNode.setAttribute(CamelBean.ARG_TYPE, type);
		} else {
			propertyNode.removeAttribute(CamelBean.ARG_TYPE);
		}
		if (!Strings.isEmpty(value)) {
			propertyNode.setAttribute(CamelBean.ARG_VALUE, value);
		}
		return propertyNode;
	}
	
	public Element createBeanArgument(final CamelFile camelFile, String type, String value) {
		final String prefixNS = camelFile.getRouteContainer().getXmlNode().getPrefix();
		AbstractCamelModelElement newRoot = camelFile.getChildElements().get(0);
		String tagName = getArgumentTag(newRoot);
		Element propertyNode = newRoot.createElement(tagName, prefixNS);
		if (!Strings.isEmpty(type)) {
			propertyNode.setAttribute(CamelBean.ARG_TYPE, type);
		} else {
			propertyNode.removeAttribute(CamelBean.ARG_TYPE);
		}
		if (!Strings.isEmpty(value)) {
			propertyNode.setAttribute(CamelBean.ARG_VALUE, value);
		}
		return propertyNode;
	}

	public void editBeanArgument(Element xmlElement, String type, String value) {
		setAttributeValue(xmlElement, CamelBean.ARG_TYPE, type);
		if (!Strings.isEmpty(value)) {
			xmlElement.setAttribute(CamelBean.PROP_VALUE, value);
		}
	}
	
	public Element createBeanNode(final CamelFile camelFile, String id, String className) {
		final String prefixNS = camelFile.getRouteContainer().getXmlNode().getPrefix();
		Element newBeanNode = camelFile.createElement(CamelBean.BEAN_NODE, prefixNS);
		newBeanNode.setAttribute(CamelBean.PROP_ID, id);
		if (!Strings.isBlank(className)) {
			newBeanNode.setAttribute(CamelBean.PROP_CLASS, className);
		}
		return newBeanNode;
	}
	
	public Element createBeanProperty(final CamelFile camelFile, String name, String value) {
		final String prefixNS = camelFile.getRouteContainer().getXmlNode().getPrefix();
		AbstractCamelModelElement newRoot = camelFile.getChildElements().get(0);
		Element propertyNode = newRoot.createElement(CamelBean.TAG_PROPERTY, prefixNS);
		propertyNode.setAttribute(CamelBean.PROP_NAME, name);
		propertyNode.setAttribute(CamelBean.PROP_VALUE, value);
		return propertyNode;
	}

	public Element createBeanProperty(final Element inputElement, String name, String value) {
		String prefixNS = inputElement.getPrefix();
		Element propertyNode = inputElement.getOwnerDocument().createElementNS(prefixNS, CamelBean.TAG_PROPERTY);
		propertyNode.setAttribute(CamelBean.PROP_NAME, name);
		propertyNode.setAttribute(CamelBean.PROP_VALUE, value);
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
		setAttributeValue(xmlElement, CamelBean.PROP_NAME, name);
		if (!Strings.isEmpty(value)) {
			xmlElement.setAttribute(CamelBean.PROP_VALUE, value);
		}
	}
}
