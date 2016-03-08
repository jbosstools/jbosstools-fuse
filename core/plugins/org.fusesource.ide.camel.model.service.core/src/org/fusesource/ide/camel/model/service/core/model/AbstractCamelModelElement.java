/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.fusesource.ide.camel.model.service.core.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.ui.PlatformUI;
import org.fusesource.ide.camel.model.service.core.catalog.CamelModel;
import org.fusesource.ide.camel.model.service.core.catalog.CamelModelFactory;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.service.core.internal.CamelModelServiceCoreActivator;
import org.fusesource.ide.foundation.core.util.CamelUtils;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.preferences.PreferenceManager;
import org.fusesource.ide.preferences.PreferencesConstants;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author lhein
 */
public abstract class AbstractCamelModelElement {

	public static final String TOPIC_REMOVE_CAMEL_ELEMENT = "TOPIC_REMOVE_CAMEL_ELEMENT";
	public static final String TOPIC_ID_RENAMING = "TOPIC_ID_RENAMING";
	public static final String PROPERTY_KEY_OLD_ID = "OLD_ID";
	public static final String PROPERTY_KEY_NEW_ID = "NEW_ID";
	public static final String PROPERTY_KEY_CAMEL_FILE = "CAMEL_FILE";

	protected static final String ID_ATTRIBUTE = "id";
	protected static final String DATA_FORMATS_NODE_NAME = "dataFormats";
	protected static final String ENDPOINT_NODE_NAME = "endpoint";
	protected static final String ROUTE_NODE_NAME = "route";
	protected static final String CAMEL_CONTEXT_NODE_NAME = "camelContext";
	// children is a list of objects which are no route outputs
	private List<AbstractCamelModelElement> childElements = new ArrayList<AbstractCamelModelElement>();

	// input is the element which comes before this one
	private AbstractCamelModelElement inputElement;

	// output is the route output of this element
	private AbstractCamelModelElement outputElement;

	// the parent node
	private AbstractCamelModelElement parent;

	// the catalog element which represents this object
	private Node xmlNode;

	// a reference to the meta model class for this object
	private Eip underlyingMetaModelObject;

	// a map containing all the properties of the element
	private Map<String, Object> parameters = new HashMap<String, Object>();

	// the camel file
	private CamelFile cf;

	// the camel context
	private CamelContextElement context;

	private String name;
	private String description;

	/**
	 * creates a camel node using the xml node
	 * 
	 * @param parent
	 *            the parent object
	 * @param underlyingNode
	 *            the camel xml node
	 */
	protected AbstractCamelModelElement(AbstractCamelModelElement parent, Node underlyingNode) {
		this.xmlNode = underlyingNode;
		this.parent = parent;

		if (underlyingNode != null)
			setUnderlyingMetaModelObject(getEipByName(CamelUtils.getTranslatedNodeName(underlyingNode)));
		if (parent != null && parent.getXmlNode() != null && underlyingNode != null && (getXmlNode().getParentNode() == null || CamelUtils.getTranslatedNodeName(getXmlNode().getParentNode()).equals(DATA_FORMATS_NODE_NAME) == false)) {
			boolean alreadyChild = false;
			for (int i = 0; i < parent.getXmlNode().getChildNodes().getLength(); i++) {
				if (parent.getXmlNode().getChildNodes().item(i).isEqualNode(underlyingNode)) {
					alreadyChild = true;
					break;
				}
			}
			if (!alreadyChild) {
				if (parent.getNodeTypeId().equals("choice")) {
					if (this.getNodeTypeId().equals("when")) {
						Node otherwiseNode = null;
						for (int i = 0; i < parent.getXmlNode().getChildNodes().getLength(); i++) {
							if (CamelUtils.getTranslatedNodeName(parent.getXmlNode().getChildNodes().item(i)).equals("otherwise")) {
								otherwiseNode = parent.getXmlNode().getChildNodes().item(i);
								break;
							}
						}
						// move all when nodes before the otherwise
						parent.getXmlNode().insertBefore(getXmlNode(), otherwiseNode);
					} else if (getNodeTypeId().equals("otherwise")) {
						parent.getXmlNode().appendChild(getXmlNode());
					}
				} else {
					parent.getXmlNode().appendChild(getXmlNode());
				}

			}
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getDisplayText();
	}
	
	/**
	 * finds the endpoint with the given uri in the route
	 * 
	 * @param uri
	 * @return
	 */
	public AbstractCamelModelElement findEndpoint(String uri) {
		if (getChildElements().isEmpty()) {
			if (getParameter("uri") != null && ((String)getParameter("uri")).equals(uri)) {
				return this;
			}
		} else {
			for (AbstractCamelModelElement cme : getChildElements()) {
				AbstractCamelModelElement e = cme.findEndpoint(uri);
				if (e != null) return e;
			}	
		}		
		return null;
	}

	/**
	 * returns the first element node of the parent
	 * 
	 * @param parentNode
	 * @return the first element node or null if no elements found
	 */
	protected Node getFirstChild(Node parentNode) {
		for (int i = 0; i < parentNode.getChildNodes().getLength(); i++) {
			Node n = parentNode.getChildNodes().item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE)
				return n;
		}
		return null;
	}

	/**
	 * returns the last element node of the parent
	 * 
	 * @param parentNode
	 * @return the last element node or null if no elements found
	 */
	protected Node getLastChild(Node parentNode) {
		for (int i = parentNode.getChildNodes().getLength() - 1; i >= 0; i--) {
			Node n = parentNode.getChildNodes().item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE)
				return n;
		}
		return null;
	}

	/**
	 * gets the previous element node if existing
	 * 
	 * @param node
	 * @return the previous element or null
	 */
	protected Node getPreviousNode(Node node) {
		Node n = node.getPreviousSibling();
		while (n != null) {
			if (n.getNodeType() == Node.ELEMENT_NODE)
				return n;
			n = n.getPreviousSibling();
		}
		return null;
	}

	/**
	 * returns the next element node
	 * 
	 * @param node
	 * @return the next element node or null
	 */
	protected Node getNextNode(Node node) {
		Node n = node.getNextSibling();
		while (n != null) {
			if (n.getNodeType() == Node.ELEMENT_NODE)
				return n;
			n = n.getNextSibling();
		}
		return null;
	}

	/**
	 * @return the parent
	 */
	public AbstractCamelModelElement getParent() {
		return this.parent;
	}

	/**
	 * returns the route this endpoint belongs to
	 * 
	 * @return
	 */
	public CamelRouteElement getRoute() {
		AbstractCamelModelElement cme = getParent();
		while (cme instanceof CamelRouteElement == false && cme != null) {
			cme = cme.getParent();
		}
		if (cme != null && cme instanceof CamelRouteElement) {
			return (CamelRouteElement)cme;
		}
		return null;
	}

	/**
	 * @param parent
	 *            the parent to set
	 */
	public void setParent(AbstractCamelModelElement parent) {
		this.parent = parent;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		if (getParameter(ID_ATTRIBUTE) != null)
			return (String) this.getParameter(ID_ATTRIBUTE);
		return null;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.setParameter(ID_ATTRIBUTE, id);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
		setParameter("description", description);
	}

	/**
	 * returns the string to display in the diagram
	 * 
	 * @return
	 */
	public String getDisplayText() {
		return getDisplayText(true);
	}

	/**
	 * 
	 * @param useID
	 * @return
	 */
	public final String getDisplayText(boolean useID) {
		String result = String.format("%s ", Strings.capitalize(getNodeTypeId()));
		
		// honor the PREFER_ID_AS_LABEL preference
		// we initially set it to the value of the contains method
		boolean preferID = false;
		if (useID) {
			preferID = PreferenceManager.getInstance()
					.containsPreference(PreferencesConstants.EDITOR_PREFER_ID_AS_LABEL);
			// as second step if the value is there, we use it for the flag
			if (PreferenceManager.getInstance().containsPreference(PreferencesConstants.EDITOR_PREFER_ID_AS_LABEL)) {
				preferID = PreferenceManager.getInstance()
						.loadPreferenceAsBoolean(PreferencesConstants.EDITOR_PREFER_ID_AS_LABEL);
			}
		}

		// we only return the id if we are told so by the preference AND the
		// value of the ID is set != null
		if (preferID && getId() != null && getId().trim().length() > 0) {
			result += getId();
			return result;
		}

		if (isEndpointElement()) {
			String uri = (String)this.getParameter("uri");
			if (uri != null && uri.trim().length() > 0) {
				// uri specified, use it
				result = uri;
				return result;
			}
		}

		String eipType = getNodeTypeId();
		// For some nodes, we just return their node name
		String[] nodeNameOnly = new String[] { "choice", "try", "finally", "otherwise", "marshal", "unmarshal" };
		if (Arrays.asList(nodeNameOnly).contains(eipType))
			return result.trim();

		// Some nodes just need the value of a param
		HashMap<String, String> singlePropertyDisplay = new HashMap<String, String>();
		singlePropertyDisplay.put("bean", "ref");
		singlePropertyDisplay.put("convertBodyTo", "type");
		singlePropertyDisplay.put("enrich", "uri");
		singlePropertyDisplay.put("inOnly", "uri");
		singlePropertyDisplay.put("inOut", "uri");
		singlePropertyDisplay.put("interceptSendToEndpoint", "uri");
		singlePropertyDisplay.put("log", "logName");
		singlePropertyDisplay.put("onException", "exception");
		singlePropertyDisplay.put("pollEnrich", "uri");
		singlePropertyDisplay.put("removeHeader", "headerName");
		singlePropertyDisplay.put("removeProperty", "propertyName");
		singlePropertyDisplay.put("rollback", "message");
		singlePropertyDisplay.put("sort", "expression");
		singlePropertyDisplay.put("when", "expression");

		String propertyToCheck = singlePropertyDisplay.get(eipType);
		if( propertyToCheck != null ) {
			Object propVal = getParameter(propertyToCheck);
			if (propVal != null) {
				if( propVal instanceof AbstractCamelModelElement) {
					// seems to be an expression
					String expression = ((AbstractCamelModelElement)propVal).getParameter("expression") != null ? (String)((AbstractCamelModelElement)propVal).getParameter("expression") : null;
					if (expression != null)	return result + expression;			
				} else {
					return result + propVal.toString();
				}
			}
		}

		String answer = null;
		if (Strings.isBlank(answer)) {
			answer = result + getId();
		}
		if (Strings.isBlank(answer) && getUnderlyingMetaModelObject() != null) {
			answer = getUnderlyingMetaModelObject().getName();
		}
		if (Strings.isBlank(answer) && getNodeTypeId() != null) {
			answer = getNodeTypeId();
		}
		
		return answer;
	}

	/**
	 * @return the inputElement
	 */
	public AbstractCamelModelElement getInputElement() {
		return this.inputElement;
	}

	public boolean isEndpointElement() {
		return getParameter("uri") != null && ((String) getParameter("uri")).trim().length() > 0;
	}

	/**
	 * @param inputElement
	 *            the inputElement to set
	 */
	public void setInputElement(AbstractCamelModelElement inputElement) {
		this.inputElement = inputElement;
		// now move the node directly after inputElement in DOM tree
		if (inputElement != null) {
			Node inputNode = inputElement.getXmlNode();
			Node insertPosNode = getNextNode(inputNode);
			if (insertPosNode != null && getXmlNode().isEqualNode(insertPosNode) == false)
				inputNode.getParentNode().insertBefore(getXmlNode(), insertPosNode);
			if (insertPosNode == null)
				inputNode.getParentNode().appendChild(inputNode);
			if (isEndpointElement()) {
				checkEndpointType();
			}
		}		
	}

	protected void checkEndpointType() {
		if (isFromEndpoint() && getUnderlyingMetaModelObject() != null && 
			getUnderlyingMetaModelObject().getName().equalsIgnoreCase("to")) {
			// switch from a TO endpoint to a FROM endpoint
			setUnderlyingMetaModelObject(getEipByName("from"));
			if (getXmlNode() != null) {
				Node newNode = createElement("from", parent != null && parent.getXmlNode() != null ? parent.getXmlNode().getPrefix() : null);
				getParent().getXmlNode().replaceChild(newNode, getXmlNode());
				setXmlNode(newNode);
				updateXMLNode();
			}
		} else if (isToEndpoint() && getUnderlyingMetaModelObject() != null
				&& getUnderlyingMetaModelObject().getName().equalsIgnoreCase("from")) {
			// switch from a FROM endpoint to a TO endpoint
			setUnderlyingMetaModelObject(getEipByName("to"));
			if (getXmlNode() != null) {
				Node newNode = createElement("to", parent != null && parent.getXmlNode() != null ? parent.getXmlNode().getPrefix() : null);
				getParent().getXmlNode().replaceChild(newNode, getXmlNode());
				setXmlNode(newNode);
				updateXMLNode();
			}
		} else if (getUnderlyingMetaModelObject() == null) {
			if (isFromEndpoint()) {
				setUnderlyingMetaModelObject(getEipByName("from"));
			} else {
				setUnderlyingMetaModelObject(getEipByName("to"));
			}
		}
	}

	/**
	 * writes all params to the xml node
	 */
	public void updateXMLNode() {
		Iterator<String> it = getParameters().keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			Object val = getParameter(key);
			setParameter(key, val, true);
		}
	}

	/**
	 * this is an input endpoint if it is not the target of another node
	 * 
	 * @return true if this is a FROM endpoint
	 */
	public boolean isFromEndpoint() {
		return getInputElement() == null && getParent() instanceof CamelRouteElement;
	}

	/**
	 * this is an output endpoint if it is a target
	 * 
	 * @return true if this is a TO endpoint
	 */
	public boolean isToEndpoint() {
		return getInputElement() != null;
	}

	/**
	 * @return the outputElement
	 */
	public AbstractCamelModelElement getOutputElement() {
		return this.outputElement;
	}

	/**
	 * @param outputElement
	 *            the outputElement to set
	 */
	public void setOutputElement(AbstractCamelModelElement outputElement) {
		this.outputElement = outputElement;
		if (isEndpointElement()) {
			checkEndpointType();
		}
	}

	/**
	 * @return the childElements
	 */
	public List<AbstractCamelModelElement> getChildElements() {
		return this.childElements;
	}

	/**
	 * @param childElements
	 *            the childElements to set
	 */
	public void setChildElements(List<AbstractCamelModelElement> childElements) {
		this.childElements = childElements;
	}

	/**
	 * deletes all child elements
	 */
	public void clearChildElements() {
		this.childElements.clear();
	}

	/**
	 * adds a child element to this element if not already existing
	 * 
	 * @param element
	 */
	public void addChildElement(AbstractCamelModelElement element) {
		if (this.childElements.contains(element) == false) {
			this.childElements.add(element);
		}
	}

	/**
	 * removes a child element
	 * 
	 * @param element
	 */
	public void removeChildElement(AbstractCamelModelElement element) {
		if (childElements.contains(element)) {
			childElements.remove(element);
			boolean childFound = false;
			for (int i = 0; i < getXmlNode().getChildNodes().getLength(); i++) {
				if (getXmlNode().getChildNodes().item(i).isEqualNode(element.getXmlNode())) {
					childFound = true;
					break;
				}
			}
			if (childFound) {
				getXmlNode().removeChild(element.getXmlNode());
				IEventBroker eventBroker = PlatformUI.getWorkbench().getService(IEventBroker.class);
				eventBroker.post(AbstractCamelModelElement.TOPIC_REMOVE_CAMEL_ELEMENT, element);
			}
		}
		// special handling for the otherwise element
		if (getNodeTypeId().equalsIgnoreCase("choice") && element.getNodeTypeId().equalsIgnoreCase("otherwise")) {
			getParameters().remove("otherwise");
		}
	}

	/**
	 * @return the parameters
	 */
	public Map<String, Object> getParameters() {
		return this.parameters;
	}

	/**
	 * @param parameters
	 *            the parameters to set
	 */
	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}

	/**
	 * removes the parameter
	 * 
	 * @param name
	 */
	public void removeParameter(String name) {
		if (this.parameters.containsKey(name)) {
			Object removedItem = this.parameters.remove(name);
			if (removedItem instanceof AbstractCamelModelElement) {
				getXmlNode().removeChild(((AbstractCamelModelElement)removedItem).getXmlNode());
			}
			((Element) getXmlNode()).removeAttribute(name);
		}
	}

	/**
	 * returns the parameter with the given name or null if not available
	 * 
	 * @param name
	 *            the parameter name
	 * @return the parameter or null if not available
	 */
	public Object getParameter(String name) {
		return parameters.get(name);
	}

	/**
	 * sets the parameter with the given name to the given value. If the
	 * parameter doesn't exist it will be created
	 * 
	 * @param name
	 * @param value
	 */
	public void setParameter(String name, Object value) {
		setParameter(name, value, false);
	}

	/**
	 * 
	 */
	private void removePossibleDataFormatsInFavorToREF() {
		if (getUnderlyingMetaModelObject() != null) {
			for (Parameter p : getUnderlyingMetaModelObject().getParameters()) {
				if (p.getKind().equalsIgnoreCase("element") && p.getJavaType().equalsIgnoreCase("org.apache.camel.model.DataFormatDefinition")) {
					if (getParameter(p.getName()) != null) {
						removeParameter(p.getName());
						break;
					}
				}
			}
		}
	}

	/**
	 * sets the parameter with the given name to the given value. If the
	 * parameter doesn't exist it will be created
	 * 
	 * @param name
	 * @param value
	 * @param overrideChangeCheck
	 *            if true params are written regardless if changed or not
	 */
	protected void setParameter(String name, Object value, boolean overrideChangeCheck) {
		Object oldValue = this.parameters.get(name);

		if (overrideChangeCheck == false) {
			if (oldValue == null && value == null)
				return;
			if (oldValue != null && value != null && oldValue.equals(value))
				return;
			if (oldValue != null && oldValue.equals(value))
				return;
			if (value != null && value.equals(oldValue))
				return;
		}

		// save param in internal map
		this.parameters.put(name, value);
		
		if (value instanceof AbstractCamelModelElement && getParameter("ref") != null) {
			removeParameter("ref");
		} else if (name.equalsIgnoreCase("ref")) {
			removePossibleDataFormatsInFavorToREF();
		}

		Element e = (Element) getXmlNode();
		if (e == null)
			return;
		String kind = getKind(name);
		String javaType = getUnderlyingMetaModelObject() != null ? getUnderlyingMetaModelObject().getParameter(name).getJavaType() : null;

		if (this instanceof CamelContextElement) kind = "attribute";
		
		if (value == null || value.toString().length() < 1) {
			// seems the attribute has been deleted?
			if (kind.equalsIgnoreCase("attribute") && e.hasAttribute(name)) {
				e.removeAttribute(name);
			} else if (kind.equalsIgnoreCase("element") || kind.equalsIgnoreCase("expression")) {
				for (int i = 0; i < e.getChildNodes().getLength(); i++) {
					Node subElem = e.getChildNodes().item(i);
					if (subElem.getNodeType() == Node.ELEMENT_NODE && CamelUtils.getTranslatedNodeName(subElem).equals(name)) {
						// found the sub element -> delete it
						e.removeChild(subElem);
						break;
					}
				}
			} else if (kind.equalsIgnoreCase("value")) {
				e.setTextContent(null);
			}
		} else {
			if (kind == null && value instanceof AbstractCamelModelElement == false) {
				kind = "value";
			}
			if (kind == null && value instanceof AbstractCamelModelElement) {
				// special case for nested expressions
				Node oldChild = getFirstChild(e);
				Node newChild = ((AbstractCamelModelElement) value).getXmlNode();
				e.replaceChild(newChild, oldChild);
			} else if (kind.equalsIgnoreCase("attribute")) {
				updateAttribute(name, value, oldValue, e);
			} else if (kind.equalsIgnoreCase("element") && name.equals("description")) {
				updateElementDescription(name, value, e);
			} else if (kind.equalsIgnoreCase("element") && javaType.equals("org.apache.camel.model.DataFormatDefinition")) {
				updateDataFormatDefinition(value, e);
			} else if (kind.equalsIgnoreCase("expression")) {
				updateExpression(name, value, e);
			} else if (kind.equalsIgnoreCase("value")) {
				e.setTextContent(getMappedValue(value));
			}
		}
	}

	/**
	 * /!\ Public for test purpose
	 * 
	 * @param name
	 * @return
	 */
	public String getKind(String name) {
		return getUnderlyingMetaModelObject() != null ? getUnderlyingMetaModelObject().getParameter(name).getKind() : null;
	}

	/**
	 * @param name
	 * @param value
	 * @param e
	 */
	private void updateExpression(String name, Object value, Element e) {
		// expression element handling
		AbstractCamelModelElement exp = null;
		if (value instanceof AbstractCamelModelElement) {
			exp = (AbstractCamelModelElement) value;
		}
		Eip subEip = getEipByName(exp.getNodeTypeId());
		if (subEip != null) {
			// seems this parameter is another eip type -> we need to
			// create/modify a subnode
			boolean createSubNode = true;
			Node subNode = null;

			String comparedNodeName = name.equals("expression") ? exp.getNodeTypeId() : name;

			for (int c = 0; c < e.getChildNodes().getLength(); c++) {
				subNode = e.getChildNodes().item(c);
				if (subNode.getNodeType() == Node.ELEMENT_NODE
						&& CamelUtils.getTranslatedNodeName(subNode).equals(comparedNodeName)) {
					createSubNode = false;
					break;
				}
			}
			if (createSubNode) {
				subNode = createElement(comparedNodeName, parent != null && parent.getXmlNode() != null ? parent.getXmlNode().getPrefix() : null);
				e.appendChild(subNode);
				if (comparedNodeName.equals("expression") == false) {
					Node subSubNode = createElement(exp.getNodeTypeId(), parent != null && parent.getXmlNode() != null ? parent.getXmlNode().getPrefix() : null);
					subNode.appendChild(subSubNode);
					subNode = subSubNode;
				}
			}

			for (int i = 0; i < ((Element) subNode).getAttributes().getLength(); i++) {
				Node attrNode = ((Element) subNode).getAttributes().item(i);
				((Element) subNode).removeAttribute(CamelUtils.getTranslatedNodeName(attrNode));
			}
			Iterator<String> pKeys = exp.getParameters().keySet().iterator();
			while (pKeys.hasNext()) {
				String pKey = pKeys.next();
				Object oValue = exp.getParameter(pKey);
				// expressions shouldn't have expression attributes but
				// values
				if (subEip.getParameter(pKey).getKind().equalsIgnoreCase("value")) {
					if (oValue != null && oValue.toString().trim().length() > 0)
						((Element) subNode).setNodeValue(oValue.toString());
				} else {
					if (oValue != null && oValue.toString().trim().length() > 0)
						((Element) subNode).setAttribute(pKey, oValue.toString());
				}
			}
		} else {
			// special case for nested expressions
			Node oldChild = null;
			for (int i = 0; i < e.getChildNodes().getLength(); i++) {
				if (e.getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE
						&& CamelUtils.getTranslatedNodeName(e.getChildNodes().item(i)).equals(name)) {
					oldChild = e.getChildNodes().item(i);
					break;
				}
			}
			Node newChild = ((AbstractCamelModelElement) value).getXmlNode();
			e.replaceChild(newChild, oldChild);
		}
	}

	/**
	 * @param value
	 * @param e
	 */
	private void updateDataFormatDefinition(Object value, Element e) {
		// expression element handling
		AbstractCamelModelElement df = null;
		if (value instanceof AbstractCamelModelElement) {
			df = (AbstractCamelModelElement) value;
		}
		Eip subEip = getEipByName(df.getNodeTypeId());
		if (subEip != null) {
			// seems this parameter is another eip type -> we need to
			// create/modify a subnode
			boolean createSubNode = true;
			Node subNode = null;

			for (int c = 0; c < e.getChildNodes().getLength(); c++) {
				subNode = e.getChildNodes().item(c);
				if (subNode.getNodeType() == Node.ELEMENT_NODE
						&& CamelUtils.getTranslatedNodeName(subNode).equals(df.getNodeTypeId())) {
					createSubNode = false;
					break;
				}
			}
			if (createSubNode) {
				subNode = createElement(df.getNodeTypeId(), parent != null && parent.getXmlNode() != null ? parent.getXmlNode().getPrefix() : null);
				e.appendChild(subNode);
			}

			for (int i = 0; i < ((Element) subNode).getAttributes().getLength(); i++) {
				Node attrNode = ((Element) subNode).getAttributes().item(i);
				((Element) subNode).removeAttribute(CamelUtils.getTranslatedNodeName(attrNode));
			}
			Iterator<String> pKeys = df.getParameters().keySet().iterator();
			while (pKeys.hasNext()) {
				String pKey = pKeys.next();
				Object oValue = df.getParameter(pKey);
				// expressions shouldn't have expression attributes but
				// values
				if (subEip.getParameter(pKey).getKind().equalsIgnoreCase("value")) {
					if (oValue != null && oValue.toString().trim().length() > 0)
						((Element) subNode).setNodeValue(oValue.toString());
				} else {
					if (oValue != null && oValue.toString().trim().length() > 0)
						((Element) subNode).setAttribute(pKey, oValue.toString());
				}
			}
		}
	}

	/**
	 * @param name
	 * @param value
	 * @param e
	 */
	private void updateElementDescription(String name, Object value, Element e) {
		// description element handling
		Eip subEip = getEipByName(name);
		if (subEip != null) {
			// seems this parameter is another eip type -> we need to
			// create/modify a subnode
			boolean createSubNode = true;
			Node subNode = null;
			for (int c = 0; c < e.getChildNodes().getLength(); c++) {
				subNode = e.getChildNodes().item(c);
				if (subNode.getNodeType() == Node.ELEMENT_NODE && CamelUtils.getTranslatedNodeName(subNode).equals(name)) {
					createSubNode = false;
					break;
				}
			}
			if (createSubNode) {
				subNode = createElement(name, parent != null && parent.getXmlNode() != null ? parent.getXmlNode().getPrefix() : null);
				e.appendChild(subNode);
			}
			subNode.setTextContent(getMappedValue(value));
		}
	}

	/**
	 * @param name
	 * @param newValue
	 * @param e
	 */
	private void updateAttribute(String name, Object newValue, Object oldValue, Element e) {
		String defaultValue = this.underlyingMetaModelObject != null
				? this.underlyingMetaModelObject.getParameter(name).getDefaultValue() : null;
		if (defaultValue != null && defaultValue.equals(getMappedValue(newValue))) {
			// default value -> no need to explicitely set it -> delete
			// existing
			e.removeAttribute(name);
		} else {
			// not the default value, so set it
			e.setAttribute(name, getMappedValue(newValue));
			if ("id".equals(name)) {
				IEventBroker eventBroker = PlatformUI.getWorkbench().getService(IEventBroker.class);
				Map<String, Object> eventMap = new HashMap<>();
				eventMap.put(PROPERTY_KEY_OLD_ID, oldValue);
				eventMap.put(PROPERTY_KEY_NEW_ID, newValue);
				eventMap.put(PROPERTY_KEY_CAMEL_FILE, getCamelFile());
				eventBroker.send(TOPIC_ID_RENAMING, eventMap);
			}
		}
	}

	/**
	 * @return the xmlNode
	 */
	public Node getXmlNode() {
		return this.xmlNode;
	}

	/**
	 * @param xmlNode
	 *            the xmlNode to set
	 */
	public void setXmlNode(Node xmlNode) {
		this.xmlNode = xmlNode;
	}

	/**
	 * returns true if the object has an underlying xml node
	 * 
	 * @return
	 */
	public boolean hasUnderlyingXmlNode() {
		return this.xmlNode != null;
	}

	/**
	 * @return the underlyingMetaModelObject
	 */
	public Eip getUnderlyingMetaModelObject() {
		return this.underlyingMetaModelObject;
	}

	/**
	 * @param underlyingMetaModelObject
	 *            the underlyingMetaModelObject to set
	 */
	public void setUnderlyingMetaModelObject(Eip underlyingMetaModelObject) {
		this.underlyingMetaModelObject = underlyingMetaModelObject;
	}

	/**
	 * initializes the object
	 */
	public void initialize() {
		if (this.xmlNode != null) {
			parseNode();
		}
	}

	/**
	 * parses the node attributes into the params map and starts the parsing of
	 * subnodes
	 */
	protected void parseNode() {
		// first parse direct attributes
		parseAttributes();

		// now parse child elements
		parseChildren();

		// link child attributes like expressions to parent parameters
		linkChildrenToAttributes();
	}

	public void ensureUniqueID(AbstractCamelModelElement elem) {
		// if this element is also a parent element parameter then we don't
		// set ID values (example: parent = onException, element: exception)
		if (elem.getParent().getParameter(elem.getTranslatedNodeName()) != null && 
			elem.getParent().getUnderlyingMetaModelObject().getParameter(elem.getTranslatedNodeName()).getKind().equals("element") &&
			elem.getUnderlyingMetaModelObject().getName().equalsIgnoreCase("otherwise") == false)
			return;

		if (elem.getUnderlyingMetaModelObject() == null && elem instanceof CamelContextElement == false) {
			// don't give ID for attributes
		} else {
			if (elem.getId() == null || elem.getId().trim().length() < 1) {
				elem.setId(elem.getNewID());
			}
		}
		for (AbstractCamelModelElement e : elem.getChildElements()) {
			e.ensureUniqueID(e);
		}
	}

	/**
	 * parses direct attributes of the node
	 */
	protected void parseAttributes() {
		// first get the element name
		String nodename = getTranslatedNodeName();
		// now try to match with an EIP name
		Eip eip = getEipByName(nodename);
		if (eip != null) {
			for (Parameter param : eip.getParameters()) {
				if (param.getKind().equalsIgnoreCase("attribute")) {
					// now loop all meta model parameter and check if we have
					// them in the node
					Node tmp = getXmlNode().getAttributes().getNamedItem(param.getName());
					if (tmp != null) {
						// now map the node attribute into our EIP parameters
						setParameter(param.getName(), tmp.getNodeValue());
					}
				} else if (param.getKind().equalsIgnoreCase("element") && param.getJavaType().equalsIgnoreCase("org.apache.camel.model.DataFormatDefinition") == false) {
					if (param.getType().equalsIgnoreCase("array")) {
						ArrayList<String> list = new ArrayList<String>();
						for (int i = 0; i < getXmlNode().getChildNodes().getLength(); i++) {
							Node subNode = getXmlNode().getChildNodes().item(i);
							if (subNode.getNodeType() != Node.ELEMENT_NODE) continue;
							if (CamelUtils.getTranslatedNodeName(subNode).equals(param.getName())) {
								String val = subNode.getTextContent();
								if (val != null && val.trim().length() > 0 && list.contains(val) == false) {
									list.add(val);
								}
							}
						}
						if (list.isEmpty() == false) {
							setParameter(param.getName(), list);
						}
					} else {
						Node descNode = null;
						for (int i = 0; i < getXmlNode().getChildNodes().getLength(); i++) {
							Node subNode = getXmlNode().getChildNodes().item(i);
							if (subNode.getNodeType() != Node.ELEMENT_NODE) continue;
							if (CamelUtils.getTranslatedNodeName(subNode).equals(param.getName())) {
								descNode = subNode;
								break;
							}
						}
						if (descNode != null) {
							String val = descNode.getTextContent();
							if (val != null) {
								setParameter(param.getName(), val);
								if (param.getName().equalsIgnoreCase("description"))
									setDescription(val);
							}
						}
					}
				} else if (param.getKind().equalsIgnoreCase("value")) {
					String val = getXmlNode().getTextContent();
					if (val != null) {
						setParameter(param.getName(), val);
						if (param.getName().equalsIgnoreCase("description"))
							setDescription(val);
					}
				} else if (	param.getKind().equalsIgnoreCase("element") && 
							param.getJavaType().equalsIgnoreCase("org.apache.camel.model.DataFormatDefinition")) {
					AbstractCamelModelElement dfNode = null;
					String[] dfs = param.getOneOf().split(",");
					ArrayList<String> dfList = new ArrayList<String>();
					for (String df : dfs)
						dfList.add(df);
					for (int i = 0; i < getXmlNode().getChildNodes().getLength(); i++) {
						Node subNode = getXmlNode().getChildNodes().item(i);
						if (subNode.getNodeType() != Node.ELEMENT_NODE) continue;
						if (subNode != null && dfList.contains(CamelUtils.getTranslatedNodeName(subNode))) {
							dfNode = new CamelBasicModelElement(this, subNode);
							dfNode.initialize();
							// expNode.setParent(this);
							setParameter(param.getName(), dfNode);
						}
					}
				} else if (param.getKind().equalsIgnoreCase("expression")) {
					AbstractCamelModelElement expNode = null;
					String[] langs = param.getOneOf().split(",");
					ArrayList<String> langList = new ArrayList<String>();
					for (String lang : langs)
						langList.add(lang);
					for (int i = 0; i < getXmlNode().getChildNodes().getLength(); i++) {
						Node subNode = getXmlNode().getChildNodes().item(i);
						if (subNode.getNodeType() != Node.ELEMENT_NODE)
							continue;
						if (subNode != null && param.getName().equals("expression")
								&& langList.contains(CamelUtils.getTranslatedNodeName(subNode))) {
							// this case is for expressions which are directly
							// stored under the parent node
							// for instance when.<expression>
							expNode = new CamelBasicModelElement(this, subNode);
							expNode.initialize();
							// expNode.setParent(this);
							setParameter(param.getName(), expNode);
						} else if (subNode != null && param.getName().equals("expression") == false
								&& param.getName().equals(CamelUtils.getTranslatedNodeName(subNode))) {
							// this case is for expressions which are not
							// directly
							// stored under the parent node but under another
							// subnode
							// for instance onException.handled.<expression>
							for (int x = 0; x < subNode.getChildNodes().getLength(); x++) {
								Node subExpNode = subNode.getChildNodes().item(x);
								if (subExpNode.getNodeType() == Node.ELEMENT_NODE && subExpNode != null
										&& langList.contains(CamelUtils.getTranslatedNodeName(subExpNode))) {
									// found the sub -> create container element
									AbstractCamelModelElement expContainer = new CamelBasicModelElement(this, subNode);
									// expContainer.initialize();
									// expContainer.setParent(this);
									expNode = new CamelBasicModelElement(expContainer, subExpNode);
									expNode.initialize();
									// expNode.setParent(this);
									expContainer.setParameter("expression", expNode);
									setParameter(param.getName(), expContainer);
									break;
								}
							}
						}
					}
				} else {
					// ignore the other kinds
				}
			}
		} else {
			CamelModelServiceCoreActivator.pluginLog()
					.logWarning("ParseAttributes: Unsupported EIP will be ignored: " + nodename);
		}
	}

	/**
	 * retrieves the eip meta model for a given eip name
	 * 
	 * @param name
	 * @return the eip or null if not found
	 */
	public Eip getEipByName(String name) {
		// TODO: project camel version vs latest camel version
		String prjCamelVersion = CamelModelFactory.getLatestCamelVersion();
		if (getCamelFile() != null) {
			// get the project from the camel file resource
			IResource camelResource = getCamelFile().getResource();
			if (camelResource != null) {
				IProject prj = camelResource.getProject();
				// now try to determine the configured camel version from the
				// project
				prjCamelVersion = CamelModelFactory.getCamelVersion(prj);
				// if project doesn't define a camel version we grab the latest
				// supported
				if (prjCamelVersion == null)
					prjCamelVersion = CamelModelFactory.getLatestCamelVersion();
			}
		}
		// then get the meta model for the given camel version
		CamelModel model = CamelModelFactory.getModelForVersion(prjCamelVersion);
		if (model == null) {
			// if we don't support the defined camel version we take the latest
			// supported instead
			model = CamelModelFactory.getModelForVersion(CamelModelFactory.getLatestCamelVersion());
		}
		// then we get the eip meta model
		Eip eip = model.getEipModel().getEIPByName(name);
		// special case for context wide endpoint definitions
		if (eip == null && name.equals("endpoint")) eip = model.getEipModel().getEIPByName("to");
		// and return it
		return eip;
	}

	/**
	 * returns true if the eip with the given name can have children
	 * 
	 * @param name
	 * @return
	 */
	protected boolean hasChildren(String name) {
		Eip eip = getEipByName(name);
		if (eip != null) {
			Iterator<Parameter> it = eip.getParameters().iterator();
			while (it.hasNext()) {
				Parameter p = it.next();
				if (p.getKind().equalsIgnoreCase("element") && p.getType().equalsIgnoreCase("array")
						&& p.getName().equals("exception") == false) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * returns true if the eip with the given name can have outputs
	 * 
	 * @param name
	 * @return
	 */
	protected boolean hasSpecialHandling(String name) {
		Eip eip = getEipByName(name);
		if (eip != null) {
			return eip.getName().equalsIgnoreCase("choice"); // choice is
																// special case
		}
		return false;
	}

	/**
	 * parses the children of this node
	 */
	protected void parseChildren() {
		String nodeName = getTranslatedNodeName();
		boolean canHaveChildren = hasChildren(nodeName) || hasSpecialHandling(nodeName);
		if (canHaveChildren) {
			NodeList children = getXmlNode().getChildNodes();
			AbstractCamelModelElement lastNode = null;
			for (int i = 0; i < children.getLength(); i++) {
				Node tmp = children.item(i);
				if (tmp.getNodeType() != Node.ELEMENT_NODE)
					continue;
				if (!isUsedAsAttribute(tmp)) {
					AbstractCamelModelElement cme = new CamelBasicModelElement(this, tmp);
					addChildElement(cme);
					cme.initialize();
					boolean createLink = lastNode != null && this.getNodeTypeId().equals("choice") == false;
					cme.setParent(this);
					if (createLink) {
						cme.setInputElement(lastNode);
						lastNode.setOutputElement(cme);
					}
					lastNode = cme;
				}
			}
		}
	}

	protected boolean isSpecialCase(Node childNode) {
		if (getTranslatedNodeName().equalsIgnoreCase("choice")) {
			if (CamelUtils.getTranslatedNodeName(childNode).equalsIgnoreCase("otherwise")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * checks whether the node is used as attribute instead of child
	 * (expressions or alike)
	 * 
	 * @param childNode
	 * @return
	 */
	protected boolean isUsedAsAttribute(Node childNode) {
		String nodeName = CamelUtils.getTranslatedNodeName(childNode);

		if (isSpecialCase(childNode)) {
			return false;
		}

		if (getUnderlyingMetaModelObject() != null) {
			Iterator<Parameter> it = getUnderlyingMetaModelObject().getParameters().iterator();
			while (it.hasNext()) {
				Parameter p = it.next();

				// check if the node name equals a known parameter
				if (p.getName().equals(nodeName)) {
					return true;
				}

				// check if node name equals the language of an expression
				if (p.getKind().equals("expression")) {
					List<String> langs = Arrays.asList(p.getOneOf().split(","));
					if (langs.contains(nodeName)) {
						return true;
					}
				}
			}
		}

		return false;
	}

	/**
	 * checks for each parameter if type element or expression if there is a
	 * child and links them
	 */
	protected void linkChildrenToAttributes() {
		if (getUnderlyingMetaModelObject() == null)
			return;
		for (Parameter p : getUnderlyingMetaModelObject().getParameters()) {
			if (p.getKind().equalsIgnoreCase("expression") || p.getKind().equalsIgnoreCase("element")) {
				for (AbstractCamelModelElement child : getChildElements()) {
					if (child.getNodeTypeId().equalsIgnoreCase(p.getName()) && p.getType().equalsIgnoreCase("object")) {
						// so we have a child of type element or expression
						// which should be handled as an attribute
						parameters.put(p.getName(), child);
					}
				}
			}
		}
	}

	/**
	 * converts an object into the xml string representation used in the
	 * attribute value
	 * 
	 * @param value
	 * @return
	 */
	protected String getMappedValue(Object value) {
		return value.toString();
	}

	/**
	 * removes all attributes
	 */
	protected void clearAttributes() {
		NamedNodeMap attribs = getXmlNode().getAttributes();
		for (int i = 0; i < attribs.getLength(); i++) {
			Node attr = attribs.item(i);
			if (attr != null)
				getXmlNode().getAttributes().removeNamedItem(CamelUtils.getTranslatedNodeName(attr));
		}
	}

	/**
	 * returns the key to the icon for this node
	 * 
	 * @return
	 */
	public String getIconName() {
		if (isEndpointElement()) {
			String u = (String) getParameter("uri");
			if (u != null && u.trim().length() > 0) {
				String scheme = null;
				if (u.startsWith("ref:")) {
					// if its a ref we lookup what is the reference scheme
					String refId = u.substring(u.indexOf(":") + 1);
					AbstractCamelModelElement endpointRef = getCamelContext().getEndpointDefinitions().get(refId);
					if (endpointRef != null) {
						String refUri = (String) endpointRef.getParameter("uri");
						if (refUri != null) {
							scheme = refUri.substring(0, refUri.indexOf(":"));
						} else {
							// seems we have a broken ref
							return "endpoint";
						}
					}
				} else {
					scheme = u.substring(0, u.indexOf(":"));
				}
				return scheme;
			}
			return "endpoint";
		}
		return getNodeTypeId();
	}

	/**
	 * Return the typeid of this node, if applicable. This should match the
	 * parameter name from the eip.xml model, so for example, doTry, resequence,
	 * etc
	 * 
	 * @return
	 */
	public String getNodeTypeId() {
		return underlyingMetaModelObject != null ? underlyingMetaModelObject.getName()
				: xmlNode != null ? CamelUtils.getTranslatedNodeName(xmlNode) : "camelContext";
	}

	/**
	 * returns the documentation name for the eip
	 * 
	 * @return
	 */
	public String getDocumentationFileName() {
		if (isEndpointElement())
			return "endpoint";
		return String.format("%sEIP", getNodeTypeId());
	}

	/**
	 * returns the category this item belongs to
	 * 
	 * @return
	 */
	public String getCategoryName() {
		if (isEndpointElement())
			return "Components";
		return getUnderlyingMetaModelObject().getTags().get(getUnderlyingMetaModelObject().getTags().size() - 1);
	}

	/**
	 * returns the camel file this element belongs to
	 * 
	 * @return the camel file or null if not persisted yet
	 */
	public CamelFile getCamelFile() {
		if (this.cf == null) {
			AbstractCamelModelElement tmp = this;
			while (tmp.getParent() != null && tmp.getParent() instanceof CamelFile == false) {
				tmp = tmp.getParent();
			}
			if (tmp.getParent() != null && tmp.getParent() instanceof CamelFile) {
				this.cf = (CamelFile) tmp.getParent();
			}
		}
		return this.cf;
	}

	/**
	 * returns the camel context for this element
	 * 
	 * @return
	 */
	public CamelContextElement getCamelContext() {
		if (this.context == null) {
			AbstractCamelModelElement tmp = this;
			while (tmp.getParent() != null && tmp.getParent() instanceof CamelContextElement == false) {
				tmp = tmp.getParent();
			}
			if (tmp.getParent() != null && tmp.getParent() instanceof CamelContextElement) {
				this.context = (CamelContextElement) tmp.getParent();
			}
		}
		return this.context;
	}

	/**
	 * creates a random id
	 * 
	 * @return a random id
	 */
	public String getNewID() {
		String answer = null;
		int i = 1;
		answer = String.format("_%s%d", getNodeTypeId(), i++);
		while (getCamelContext().doesNewIDExist(answer) == false) {
			answer = String.format("_%s%d", getNodeTypeId(), i++);
		}
		return answer;
	}

	/**
	 * tests if the given id is context wide unique
	 * 
	 * @param newId
	 * @return
	 */
	public boolean isIDUnique(String id) {
		if (id == null || id.trim().length() < 1)
			return false;

		if (getCamelContext().findAllNodesWithId(id).size()>1) return false;

		return true;
	}
	
	/**
	 * tests if the given id is context wide unique
	 * 
	 * @param newId
	 * @return
	 */
	public boolean doesNewIDExist(String newId) {
		if (newId == null || newId.trim().length() < 1)
			return false;

		if (getCamelContext().findNode(newId) != null)
			return false;

		return true;
	}

	/**
	 * searches the model for a node with the given id
	 * 
	 * @param nodeId
	 * @return the node or null
	 */
	public AbstractCamelModelElement findNode(String nodeId) {
		if (getId() != null && getId().equals(nodeId)) {
			return this;
		}

		if (this instanceof CamelContextElement) {
			CamelContextElement ctx = (CamelContextElement)this;
			if (ctx.getDataformats().isEmpty() == false) {
				if (ctx.getDataformats().containsKey(nodeId)) {
					return ctx.getDataformats().get(nodeId);
				}
			}
			if (ctx.getEndpointDefinitions().isEmpty() == false) {
				if (ctx.getEndpointDefinitions().containsKey(nodeId)) {
					return ctx.getEndpointDefinitions().get(nodeId);
				}
			}
		}
		
		if (getChildElements() != null) {
			for (AbstractCamelModelElement e : getChildElements()) {
				AbstractCamelModelElement cme = e.findNode(nodeId);
				if (cme != null)
					return cme;
			}
		}

		return null;
	}
	
	public List<AbstractCamelModelElement> findAllNodesWithId(String nodeId) {
		List<AbstractCamelModelElement> result = new ArrayList<AbstractCamelModelElement>();
		
		if (getId() != null && getId().equals(nodeId)) {
			result.add(this);
		}
		
		if (getChildElements() != null) {
			for (AbstractCamelModelElement e : getChildElements()) {
				result.addAll(e.findAllNodesWithId(nodeId));
			}
		}

		return result;
	}

	/**
	 * checks if the node is the from node and therefore the first node in my
	 * route
	 * 
	 * @return
	 */
	public boolean isFirstNodeInRoute() {
		return getInputElement() == null && getParent() instanceof CamelRouteElement;
	}

	/**
	 * checks if the node can handle breakpoints
	 * 
	 * @return
	 */
	public boolean supportsBreakpoint() {
		return !isFirstNodeInRoute() && // not working on the From node
				!"when".equals(getNodeTypeId()) && // not working for When nodes
				!"otherwise".equals(getNodeTypeId()); // not working for
														// Otherwise nodes
	}
	
	/**
	 * use this method to create a new element node
	 * 
	 * @param nodeTypeId	the name of the node
	 * @param namespace		the namespace prefix to use
	 * @return	the created element node
	 */
	public Element createElement(String nodeTypeId, String namespace) {
		String nodeName = nodeTypeId;
		if (namespace != null && namespace.trim().length()>0) {
			nodeName = namespace + ":" + nodeTypeId;
		}
		return getCamelFile().getDocument().createElement(nodeName);
	}
	
	public String getTranslatedNodeName() {
		return CamelUtils.getTranslatedNodeName(getXmlNode());
	}
}
