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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.ui.PlatformUI;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelCatalogCacheManager;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelModel;
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

	private static final String NODE_KIND_VALUE = "value";
	private static final String DESCRIPTION_NODE_NAME = "description";
	
	public static final String NODE_KIND_EXPRESSION = "expression";
	public static final String NODE_KIND_ATTRIBUTE = "attribute";
	public static final String NODE_KIND_ELEMENT = "element";
	
	public static final String URI_PARAMETER_KEY = "uri";
	public static final String ENDPOINT_TYPE_TO = "to";
	public static final String ENDPOINT_TYPE_FROM = "from";
	public static final String TOPIC_REMOVE_CAMEL_ELEMENT = "TOPIC_REMOVE_CAMEL_ELEMENT";
	public static final String TOPIC_ID_RENAMING = "TOPIC_ID_RENAMING";
	
	public static final String PROPERTY_KEY_OLD_ID = "OLD_ID";
	public static final String PROPERTY_KEY_NEW_ID = "NEW_ID";
	public static final String PROPERTY_KEY_CAMEL_FILE = "CAMEL_FILE";

	public static final String PARAMETER_LANGUAGENAME = "languageName";
	
	public static final String CHOICE_NODE_NAME = "choice";
	public static final String WHEN_NODE_NAME = "when";
	public static final String OTHERWISE_NODE_NAME = "otherwise";
	public static final String WIRETAP_NODE_NAME = "wireTap";

	public static final String USER_LABEL_COMPONENT_REGEX = "[\\w-]+";
	public static final String USER_LABEL_PARAMETER_REGEX = USER_LABEL_COMPONENT_REGEX; // these expressions may be changed in the future
	public static final String USER_LABEL_REGEX = USER_LABEL_COMPONENT_REGEX + "\\." + USER_LABEL_PARAMETER_REGEX;
	public static final String USER_LABEL_DELIMETER = ";";


	public static final String ROUTE_NODE_NAME = "route";
	public static final String ID_ATTRIBUTE = "id";
	public static final String ROUTE_ATTRIBUTE = "route";
	public static final String DATA_FORMATS_NODE_NAME = "dataFormats";
	public static final String ENDPOINT_NODE_NAME = "endpoint";
	public static final String CAMEL_CONTEXT_NODE_NAME = "camelContext";
	public static final String BEAN_NODE = "bean";
	
	// children is a list of objects which are no route outputs
	private List<AbstractCamelModelElement> childElements = new ArrayList<>();

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
	private Map<String, Object> parameters = new HashMap<>();

	// the camel file
	private CamelFile cf;

	// the camel route container
	private CamelRouteContainerElement container;

	private String name;
	private String description;
	
	// flag which controls if default values are removed from file or not
	private static boolean optimizedXML = true; 

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

		if (underlyingNode != null) {
			setUnderlyingMetaModelObject(getEipByName(CamelUtils.getTagNameWithoutPrefix(underlyingNode)));
		}
		if (parent != null && parent.getXmlNode() != null && underlyingNode != null
				&& (getXmlNode().getParentNode() == null || !DATA_FORMATS_NODE_NAME.equals(CamelUtils.getTagNameWithoutPrefix(getXmlNode().getParentNode())))) {
			boolean alreadyChild = false;
			final NodeList siblingNodes = parent.getXmlNode().getChildNodes();
			for (int i = 0; i < siblingNodes.getLength(); i++) {
				if (siblingNodes.item(i).isEqualNode(underlyingNode)) {
					alreadyChild = true;
					break;
				}
			}
			if (!alreadyChild) {
				if (CHOICE_NODE_NAME.equals(parent.getNodeTypeId())) {
					if (WHEN_NODE_NAME.equals(this.getNodeTypeId())) {
						Node otherwiseNode = null;
						for (int i = 0; i < siblingNodes.getLength(); i++) {
							if (OTHERWISE_NODE_NAME.equals(CamelUtils.getTagNameWithoutPrefix(siblingNodes.item(i)))) {
								otherwiseNode = siblingNodes.item(i);
								break;
							}
						}
						// move all when nodes before the otherwise
						parent.getXmlNode().insertBefore(getXmlNode(), otherwiseNode);
					} else if (OTHERWISE_NODE_NAME.equals(getNodeTypeId())) {
						parent.getXmlNode().appendChild(getXmlNode());
					}
				} else {
					parent.getXmlNode().appendChild(getXmlNode());
				}

			}
		}
	}

	/**
	 * @return the optimizedXML
	 */
	public static boolean useOptimizedXML() {
		return optimizedXML;
	}
	
	/**
	 * @param optimizedXML the optimizedXML to set
	 */
	public static void setOptimizedXML(boolean optimizedXML) {
		AbstractCamelModelElement.optimizedXML = optimizedXML;
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
		if (uri != null) {
			if (getChildElements().isEmpty()) {
				if (uri.equals(getParameter(URI_PARAMETER_KEY))) {
					return this;
				}
			} else {
				return getChildElements().stream()
						.filter(cme -> cme.findEndpoint(uri) != null)
						.findFirst().orElse(null);
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
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				return n;
			}
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
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				return n;
			}
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
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				return n;
			}
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
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				return n;
			}
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
	 * checks if the given other node has the same parent than this node
	 * 
	 * @param other
	 * @return
	 */
	public boolean hasSameParent(AbstractCamelModelElement other) {
		return getParent().equals(other.getParent());
	}
	
	/**
     * returns true if the element can be added on a camel context 
     * 
     * @return
     */
    public boolean canBeAddedToCamelContextDirectly() {
    	return getUnderlyingMetaModelObject().canBeAddedToCamelContextDirectly(); 
    }

	/**    
	 * returns the first element in a flow 
	 * 
	 * @return
	 */
	public AbstractCamelModelElement getFirstInFlow() {
		AbstractCamelModelElement node = this;
		while (node != null && node.getInputElement() != null) {
			node = node.getInputElement();
		}
		return node;
	}

	/**    
	 * returns the last element in a flow 
	 * 
	 * @return
	 */
	public AbstractCamelModelElement getLastInFlow() {
		AbstractCamelModelElement node = this;
		while (node != null && node.getOutputElement() != null) {
			node = node.getOutputElement();
		}
		return node;
	}
	
	/**
	 * returns the route this endpoint belongs to
	 *
	 * @return
	 */
	public CamelRouteElement getRoute() {
		AbstractCamelModelElement cme = getParent();
		while (!(cme instanceof CamelRouteElement) && cme != null) {
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
		if (getParameter(ID_ATTRIBUTE) != null) {
			return (String) this.getParameter(ID_ATTRIBUTE);
		}
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
		setParameter(DESCRIPTION_NODE_NAME, description);
	}

	/**
	 * returns the string to display in the diagram
	 *
	 * @return
	 */
	public String getDisplayText() {
		return getDisplayText(false);
	}

	/**
	 *
	 * @param useID
	 * @return
	 */
	public final String getDisplayText(boolean useID) {
		String result = String.format("%s ", Strings.capitalize(getNodeTypeId()));

		// we only return the id if we are told so by the preference AND the
		// value of the ID is set != null
		if (useID && getId() != null && getId().trim().length() > 0) {
			result += getId();
			return result;
		}

		if (isEndpointElement()) {
			String uri = (String)this.getParameter(URI_PARAMETER_KEY);
			if (uri != null && uri.trim().length() > 0) {
				// uri specified, use it
				result = uri;
				return result;
			}
		}

		String eipType = getNodeTypeId();
		// For some nodes, we just return their node name
		String[] nodeNameOnly = new String[] { CHOICE_NODE_NAME, "try", "finally", OTHERWISE_NODE_NAME, "marshal", "unmarshal" };
		if (Arrays.asList(nodeNameOnly).contains(eipType)) {
			return result.trim();
		}

		// Some nodes just need the value of a param
		Map<String, String> singlePropertyDisplay = new HashMap<>();
		singlePropertyDisplay.put("bean", "ref");
		singlePropertyDisplay.put("convertBodyTo", "type");
		singlePropertyDisplay.put("enrich", URI_PARAMETER_KEY);
		singlePropertyDisplay.put("inOnly", URI_PARAMETER_KEY);
		singlePropertyDisplay.put("inOut", URI_PARAMETER_KEY);
		singlePropertyDisplay.put("interceptSendToEndpoint", URI_PARAMETER_KEY);
		singlePropertyDisplay.put("log", "logName");
		singlePropertyDisplay.put("onException", "exception");
		singlePropertyDisplay.put("pollEnrich", URI_PARAMETER_KEY);
		singlePropertyDisplay.put("removeHeader", "headerName");
		singlePropertyDisplay.put("removeProperty", "propertyName");
		singlePropertyDisplay.put("rollback", "message");
		singlePropertyDisplay.put("sort", NODE_KIND_EXPRESSION);
		singlePropertyDisplay.put(WHEN_NODE_NAME, NODE_KIND_EXPRESSION);

		// User defined labels
		if (PreferenceManager.getInstance().containsPreference(PreferencesConstants.EDITOR_PREFERRED_LABEL)) {
			String userProperties = PreferenceManager.getInstance()
					.loadPreferenceAsString(PreferencesConstants.EDITOR_PREFERRED_LABEL);
			String[] userLabels = userProperties.split(USER_LABEL_DELIMETER);
			for (String userLabel : userLabels) {
				if (userLabel.matches(USER_LABEL_REGEX)) {
					String[] parts = userLabel.split("\\.");
					singlePropertyDisplay.put(parts[0], parts[1]);
				}
			}
		}

		String propertyToCheck = singlePropertyDisplay.get(eipType);
		if( propertyToCheck != null ) {
			Object propVal = getParameter(propertyToCheck);
			if (propVal == null) {
				// try to get the default value
				Parameter param = getUnderlyingMetaModelObject().getProperties().get(propertyToCheck);
				propVal = param == null ? null : param.getDefaultValue();
			}
			if (propVal != null) {
				if( propVal instanceof AbstractCamelModelElement) {
					// seems to be an expression
					String expression = ((AbstractCamelModelElement)propVal).getParameter(NODE_KIND_EXPRESSION) != null ? (String)((AbstractCamelModelElement)propVal).getParameter(NODE_KIND_EXPRESSION) : null;
					if (expression != null) {
						return result + expression;
					}
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
		return Arrays.asList(ENDPOINT_TYPE_FROM, ENDPOINT_TYPE_TO, ENDPOINT_NODE_NAME).contains(getNodeTypeId());
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
			if (insertPosNode != null && !getXmlNode().isEqualNode(insertPosNode)) {
				inputNode.getParentNode().insertBefore(getXmlNode(), insertPosNode);
			}
			if (insertPosNode == null) {
				inputNode.getParentNode().appendChild(inputNode);
			}
			if (isEndpointElement()) {
				checkEndpointType();
			}
		}
	}

	public void checkEndpointType() {
		if (isFromEndpoint() && getUnderlyingMetaModelObject() != null && getUnderlyingMetaModelObject().getName().equalsIgnoreCase(ENDPOINT_TYPE_TO)) {
			switchEndpointType(ENDPOINT_TYPE_FROM);
		} else if (isToEndpoint() && getUnderlyingMetaModelObject() != null && getUnderlyingMetaModelObject().getName().equalsIgnoreCase(ENDPOINT_TYPE_FROM)) {
			switchEndpointType(ENDPOINT_TYPE_TO);
		} else if (getUnderlyingMetaModelObject() == null) {
			if (isFromEndpoint()) {
				setUnderlyingMetaModelObject(getEipByName(ENDPOINT_TYPE_FROM));
			} else {
				setUnderlyingMetaModelObject(getEipByName(ENDPOINT_TYPE_TO));
			}
		}
	}

	/**
	 * @param newEndpointType
	 */
	private void switchEndpointType(final String newEndpointType) {
		setUnderlyingMetaModelObject(getEipByName(newEndpointType));
		final Node xmlNodeToReplace = getXmlNode();
		if (xmlNodeToReplace != null) {
			Node newNode = createElement(newEndpointType, determineNSPrefixFromParent());
			final Node parentXmlNode = getParent().getXmlNode();
			if (xmlNodeToReplace.getParentNode() != null) {
				parentXmlNode.replaceChild(newNode, xmlNodeToReplace);
			} else {
				parentXmlNode.appendChild(newNode);
			}
			setXmlNode(newNode);
			updateXMLNode();
		}
	}

	/**
	 * @return
	 */
	private String determineNSPrefixFromParent() {
		return parent != null && parent.getXmlNode() != null ? parent.getXmlNode().getPrefix() : null;
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
		if (!childElements.contains(element)) {
			this.childElements.add(element);
			
			// special handling for the otherwise element
			if (getNodeTypeId().equalsIgnoreCase(CHOICE_NODE_NAME) && element.getNodeTypeId().equalsIgnoreCase(OTHERWISE_NODE_NAME)) {
				getParameters().put(OTHERWISE_NODE_NAME, element);
			}
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
			// set the parent to null - otherwise this will cause
			// the node still to reappear in the source code for unknown reasons
			element.setParent(null);
			boolean childFound = false;
			for (int i = 0; i < getXmlNode().getChildNodes().getLength(); i++) {
				if (getXmlNode().getChildNodes().item(i).isEqualNode(element.getXmlNode())) {
					childFound = true;
					break;
				}
			}
			if (childFound) {
				getXmlNode().removeChild(element.getXmlNode());
				notifyAboutDeletion(element);
			}
		}
		// special handling for the otherwise element
		if (getNodeTypeId().equalsIgnoreCase(CHOICE_NODE_NAME) && element.getNodeTypeId().equalsIgnoreCase(OTHERWISE_NODE_NAME)) {
			getParameters().remove(OTHERWISE_NODE_NAME);
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

	private boolean shouldIgnoreParameter(String name) {
		return 	this instanceof CamelRouteContainerElement &&
				getUnderlyingMetaModelObject() != null &&
				getUnderlyingMetaModelObject().getParameter(name) != null &&
				NODE_KIND_ELEMENT.equalsIgnoreCase(getUnderlyingMetaModelObject().getParameter(name).getKind());
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

		if (shouldIgnoreParameter(name)) {
			return;
		}
		
		Object oldValue = this.parameters.get(name);

		if (!overrideChangeCheck) {
			if (oldValue == null && value == null) {
				return;
			}
			if (oldValue != null && value != null && oldValue.equals(value)) {
				return;
			}
			if (oldValue != null && oldValue.equals(value)) {
				return;
			}
			if (value != null && value.equals(oldValue)) {
				return;
			}
			if (oldValue == null && value != null && getUnderlyingMetaModelObject() != null && getUnderlyingMetaModelObject().getParameter(name) != null && value.equals(getUnderlyingMetaModelObject().getParameter(name).getDefaultValue())) {
				// this catches false updates from the properties pages setting default values automatically
				return;
			}
		}

		// save param in internal map
		this.parameters.put(name, value);

		Element e = (Element) getXmlNode();
		if (e == null) {
			return;
		}
		String kind = getKind(name);
		String javaType = getJavaType(name);

		if (this instanceof CamelContextElement) {
			kind = NODE_KIND_ATTRIBUTE;
		}

		// this is needed for FUSETOOLS-1884, otherwise some global config
		// elements lose their children and get corrupted
		if ((!CamelUtils.isCamelNamespaceElement(getXmlNode()) || getEipByName(CamelUtils.getTagNameWithoutPrefix(getXmlNode())) == null)
				&& "id".equalsIgnoreCase(name)) {
			kind = NODE_KIND_ATTRIBUTE;
		}

		
		if (value == null || value.toString().length() < 1) {
			// seems the attribute has been deleted?
			if (kind.equalsIgnoreCase(NODE_KIND_ATTRIBUTE) && e.hasAttribute(name)) {
				e.removeAttribute(name);
			} else if ((kind.equalsIgnoreCase(NODE_KIND_ELEMENT) || kind.equalsIgnoreCase(NODE_KIND_EXPRESSION)) && value == null) {
				// value must be null because we are dealing with nodes, empty
				// nodes will return empty String as value
				for (int i = 0; i < e.getChildNodes().getLength(); i++) {
					Node subElem = e.getChildNodes().item(i);
					if (subElem.getNodeType() == Node.ELEMENT_NODE && CamelUtils.getTagNameWithoutPrefix(subElem).equals(name)) {
						// found the sub element -> delete it
						e.removeChild(subElem);
						break;
					}
				}
			} else if (NODE_KIND_VALUE.equalsIgnoreCase(kind)) {
				e.setTextContent(null);
			}
		} else {
			if (kind == null && !(value instanceof AbstractCamelModelElement)) {
				kind = NODE_KIND_VALUE;
			}
			if (kind == null && value instanceof AbstractCamelModelElement) {
				// special case for nested expressions
				Node oldChild = getFirstChild(e);
				Node newChild = ((AbstractCamelModelElement) value).getXmlNode();
				e.replaceChild(newChild, oldChild);
			} else if (NODE_KIND_ATTRIBUTE.equalsIgnoreCase(kind)) {
				updateAttribute(name, value, oldValue, e);
			} else if (NODE_KIND_ELEMENT.equalsIgnoreCase(kind) && "org.apache.camel.model.DataFormatDefinition".equals(javaType)) {
				updateDataFormatDefinition(value, e);
			} else if (NODE_KIND_ELEMENT.equalsIgnoreCase(kind) && DESCRIPTION_NODE_NAME.equals(name)) {
				updateElement(name, value, e);
			} else if (NODE_KIND_ELEMENT.equalsIgnoreCase(kind) && value instanceof List && !"exception".equals(name)) {
				updateElementList((Element)getXmlNode(), name, (List<?>) value);
			} else if (NODE_KIND_EXPRESSION.equalsIgnoreCase(kind)) {
				updateExpression(name, value, e);
			} else if (NODE_KIND_VALUE.equalsIgnoreCase(kind)) {
				e.setTextContent(getMappedValue(value));
			}
		}
	}

	private void updateElementList(Element element, String nodeName, List<?> value) {
		NodeList grandChildNodes = element.getElementsByTagName(nodeName);
		Set<Node> nodesToRemove = new HashSet<>();
		
		for (int i = 0; i < grandChildNodes.getLength(); i++) {
			Node grandChildNode = grandChildNodes.item(i);
			nodesToRemove.add(grandChildNode);
		}
		
		for (Node nodeToRemove : nodesToRemove) {
			nodeToRemove.getParentNode().removeChild(nodeToRemove);
		}

		for (Object childInTheList : value) {
			Element grandChild = createElement(nodeName, determineNSPrefixFromParent());
			grandChild.setTextContent(childInTheList.toString());
			element.appendChild(grandChild);
		}
	}

	/**
	 * @param name
	 * @return
	 */
	private String getJavaType(String name) {
		if (getUnderlyingMetaModelObject() != null && getUnderlyingMetaModelObject().getParameter(name) != null) {
			return getUnderlyingMetaModelObject().getParameter(name).getJavaType();
		} else {
			return null;
		}
	}

	/**
	 * /!\ Public for test purpose
	 *
	 * @param name
	 * @return
	 */
	public String getKind(String name) {
		if (getUnderlyingMetaModelObject() != null && getUnderlyingMetaModelObject().getParameter(name) != null) {
			return getUnderlyingMetaModelObject().getParameter(name).getKind();
		}
		return null;
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

			String comparedNodeName = name.equals(NODE_KIND_EXPRESSION) ? exp.getNodeTypeId() : name;

			for (int c = 0; c < e.getChildNodes().getLength(); c++) {
				subNode = e.getChildNodes().item(c);
				if (subNode.getNodeType() == Node.ELEMENT_NODE
						&& CamelUtils.getTagNameWithoutPrefix(subNode).equals(comparedNodeName)) {
					createSubNode = false;
					break;
				}
			}
			if (createSubNode) {
				subNode = createElement(comparedNodeName, determineNSPrefixFromParent());
				e.appendChild(subNode);
				if (!NODE_KIND_EXPRESSION.equals(comparedNodeName)) {
					Node subSubNode = createElement(exp.getNodeTypeId(), determineNSPrefixFromParent());
					subNode.appendChild(subSubNode);
					subNode = subSubNode;
				}
			}

			for (int i = 0; i < ((Element) subNode).getAttributes().getLength(); i++) {
				Node attrNode = ((Element) subNode).getAttributes().item(i);
				((Element) subNode).removeAttribute(CamelUtils.getTagNameWithoutPrefix(attrNode));
			}
			Iterator<String> pKeys = exp.getParameters().keySet().iterator();
			while (pKeys.hasNext()) {
				String pKey = pKeys.next();
				Object oValue = exp.getParameter(pKey);
				// expressions shouldn't have expression attributes but
				// values
				if (NODE_KIND_VALUE.equalsIgnoreCase(subEip.getParameter(pKey).getKind())) {
					if (oValue != null && oValue.toString().trim().length() > 0) {
						((Element) subNode).setNodeValue(oValue.toString());
					}
				} else {
					if (oValue != null && oValue.toString().trim().length() > 0) {
						((Element) subNode).setAttribute(pKey, oValue.toString());
					}
				}
			}
		} else {
			// special case for nested expressions
			Node oldChild = null;
			for (int i = 0; i < e.getChildNodes().getLength(); i++) {
				if (e.getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE
						&& CamelUtils.getTagNameWithoutPrefix(e.getChildNodes().item(i)).equals(name)) {
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
						&& CamelUtils.getTagNameWithoutPrefix(subNode).equals(df.getNodeTypeId())) {
					createSubNode = false;
					break;
				}
			}
			if (createSubNode) {
				subNode = createElement(df.getNodeTypeId(), determineNSPrefixFromParent());
				e.appendChild(subNode);
			}

			for (int i = 0; i < ((Element) subNode).getAttributes().getLength(); i++) {
				Node attrNode = ((Element) subNode).getAttributes().item(i);
				((Element) subNode).removeAttribute(CamelUtils.getTagNameWithoutPrefix(attrNode));
			}
			Iterator<String> pKeys = df.getParameters().keySet().iterator();
			while (pKeys.hasNext()) {
				String pKey = pKeys.next();
				Object oValue = df.getParameter(pKey);
				// expressions shouldn't have expression attributes but
				// values
				Parameter subEipParameter = subEip.getParameter(pKey);
				if (oValue != null && oValue.toString().trim().length() > 0) {
					if (NODE_KIND_VALUE.equalsIgnoreCase(subEipParameter.getKind())) {
						((Element) subNode).setNodeValue(oValue.toString());
					} else if(isElementKind(subEipParameter) && isArrayType(subEipParameter) && oValue instanceof List<?>) {
						updateElementList((Element) subNode, pKey, (List<?>) oValue);
					} else {
						((Element) subNode).setAttribute(pKey, oValue.toString());
					}
				}
			}
		}
	}

	/**
	 * @param name
	 * @param value
	 * @param e
	 * 
	 * In this case, we have subnodes to handle.
	 */
	private void updateElement(String name, Object value, Element e) {
		Eip subEip = getEipByName(name);
		if (subEip != null) {
			// seems this parameter is another eip type -> we need to
			// create/modify a subnode
			boolean createSubNode = true;
			Node subNode = null;
			for (int c = 0; c < e.getChildNodes().getLength(); c++) {
				subNode = e.getChildNodes().item(c);
				if (subNode.getNodeType() == Node.ELEMENT_NODE && CamelUtils.getTagNameWithoutPrefix(subNode).equals(name)) {
					createSubNode = false;
					break;
				}
			}
			if (createSubNode) {
				subNode = createElement(name, determineNSPrefixFromParent());
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
				&& this.underlyingMetaModelObject.getParameter(name) != null
				? this.underlyingMetaModelObject.getParameter(name).getDefaultValue() : null;
		if (defaultValue != null && defaultValue.equals(getMappedValue(newValue)) && useOptimizedXML()) {
			// default value -> no need to explicitly set it -> delete
			// existing
			e.removeAttribute(name);
		} else {
			// not the default value, so set it
			e.setAttribute(name, getMappedValue(newValue));
			if ("id".equals(name) && oldValue != null && !oldValue.equals(newValue)) {
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
		if (shouldParseNode()) {
			// first parse direct attributes
			parseAttributes();
	
			// now parse child elements
			parseChildren();
	
			// link child attributes like expressions to parent parameters
			linkChildrenToAttributes();
		}
	}
	
	/**
	 * For downstream nodes to choose whether to ignore contents or not
	 * @return true/false (default true)
	 */
	protected boolean shouldParseNode() {
		// default implementation
		return true;
	}

	/**
	 * parses direct attributes of the node
	 */
	protected void parseAttributes() {
		// first get the element name
		String nodename = getTagNameWithoutPrefix();
		// now try to match with an EIP name
		Eip eip = getEipByName(nodename);
		if (eip != null) {
			for (Parameter param : eip.getParameters()) {
				if (isAttributeKind(param)) {
					parseAttributeKindAttribute(param);
				} else if (isElementKind(param)) {
					if (isDataFormatDefinition(param)) {
						parseDataFormatElementAttribute(param);
					} else if (isRedeliveryPolicy(param)) {
						parseRedeliveryPolicyElementAttribute(param);
					} else {
						parseBasicElementAttribute(param);
					}
				} else if (NODE_KIND_VALUE.equalsIgnoreCase(param.getKind())) {
					parseValueAttribute(param);
				} else if (isAnExpressionGuessedByKind(param)) {
					parseExpressionKindAttribute(param);
				} else {
					// ignore the other kinds
				}
			}
		} else {
			CamelModelServiceCoreActivator.pluginLog().logWarning("ParseAttributes: Unsupported EIP will be ignored: " + nodename);
		}
	}

	/**
	 * @param param
	 */
	private void parseBasicElementAttribute(Parameter param) {
		if (isArrayType(param)) {
			parseNotDataFormatElementArrayElementAttribute(param);
		} else {
			parseNotDataFormatElementSimpleElementAttribute(param);
		}
	}

	/**
	 * @param param
	 * @param childNodes
	 */
	private void parseNotDataFormatElementSimpleElementAttribute(Parameter param) {
		final NodeList childNodes = getXmlNode().getChildNodes();
		Node descNode = null;
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node subNode = childNodes.item(i);
			if (subNode.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			if (CamelUtils.getTagNameWithoutPrefix(subNode).equals(param.getName())) {
				descNode = subNode;
				break;
			}
		}
		if (descNode != null) {
			String val = descNode.getTextContent();
			if (val != null) {
				setParameter(param.getName(), val);
				if (DESCRIPTION_NODE_NAME.equalsIgnoreCase(param.getName())) {
					setDescription(val);
				}
			}
		}
	}

	/**
	 * @param param
	 * @param childNodes
	 */
	private void parseNotDataFormatElementArrayElementAttribute(Parameter param) {
		final NodeList childNodes = getXmlNode().getChildNodes();
		List<String> list = new ArrayList<>();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node subNode = childNodes.item(i);
			if (subNode.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			if (CamelUtils.getTagNameWithoutPrefix(subNode).equals(param.getName())) {
				String val = subNode.getTextContent();
				if (val != null && !val.trim().isEmpty() && !list.contains(val)) {
					list.add(val);
				}
			}
		}
		if (!list.isEmpty()) {
			setParameter(param.getName(), list);
		}
	}

	/**
	 * @param param
	 */
	private void parseAttributeKindAttribute(Parameter param) {
		// now loop all meta model parameter and check if we have
		// them in the node
		Node tmp = getXmlNode().getAttributes().getNamedItem(param.getName());
		if (tmp != null) {
			// now map the node attribute into our EIP parameters
			setParameter(param.getName(), tmp.getNodeValue());
		}
	}

	/**
	 * @param param
	 */
	private void parseValueAttribute(Parameter param) {
		String val = getXmlNode().getTextContent();
		if (val != null) {
			setParameter(param.getName(), val);
			if (DESCRIPTION_NODE_NAME.equalsIgnoreCase(param.getName())) {
				setDescription(val);
			}
		}
	}

	/**
	 * @param param
	 */
	private void parseDataFormatElementAttribute(Parameter param) {
		NodeList childNodes = getXmlNode().getChildNodes();
		String[] dfs = param.getOneOf();
		List<String> dfList = new ArrayList<>(Arrays.asList(dfs));
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node subNode = childNodes.item(i);
			if (subNode.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			if (dfList.contains(CamelUtils.getTagNameWithoutPrefix(subNode))) {
				AbstractCamelModelElement dfNode = new CamelBasicModelElement(this, subNode);
				dfNode.initialize();
				// expNode.setParent(this);
				setParameter(param.getName(), dfNode);
			}
		}
	}

	private void parseRedeliveryPolicyElementAttribute(Parameter param) {
		NodeList childNodes = getXmlNode().getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node subNode = childNodes.item(i);
			if (subNode.getNodeType() == Node.ELEMENT_NODE) {
				AbstractCamelModelElement redeliveryPolicyNode = new CamelBasicModelElement(this, subNode);
				redeliveryPolicyNode.initialize();
				setParameter(param.getName(), redeliveryPolicyNode);
			}
		}
	}

	/**
	 * @param param
	 */
	private void parseExpressionKindAttribute(Parameter param) {
		NodeList childNodes = getXmlNode().getChildNodes();
		AbstractCamelModelElement expNode;
		List<String> langList = Arrays.asList(param.getOneOf());
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node subNode = childNodes.item(i);
			if (subNode.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			if (subNode != null && isAnExpressionGuessedByName(param)
					&& langList.contains(CamelUtils.getTagNameWithoutPrefix(subNode))) {
				// this case is for expressions which are directly
				// stored under the parent node
				// for instance when.<expression>
				expNode = new CamelBasicModelElement(this, subNode);
				expNode.initialize();
				// expNode.setParent(this);
				setParameter(param.getName(), expNode);
			} else if (subNode != null && !isAnExpressionGuessedByName(param)
					&& param.getName().equals(CamelUtils.getTagNameWithoutPrefix(subNode))) {
				// this case is for expressions which are not
				// directly
				// stored under the parent node but under another
				// subnode
				// for instance onException.handled.<expression>
				for (int x = 0; x < subNode.getChildNodes().getLength(); x++) {
					Node subExpNode = subNode.getChildNodes().item(x);
					if (subExpNode.getNodeType() == Node.ELEMENT_NODE && subExpNode != null
							&& langList.contains(CamelUtils.getTagNameWithoutPrefix(subExpNode))) {
						// found the sub -> create container element
						AbstractCamelModelElement expContainer = new CamelBasicModelElement(this, subNode);
						// expContainer.initialize();
						// expContainer.setParent(this);
						expNode = new CamelBasicModelElement(expContainer, subExpNode);
						expNode.initialize();
						// expNode.setParent(this);
						expContainer.setParameter(NODE_KIND_EXPRESSION, expNode);
						setParameter(param.getName(), expContainer);
						break;
					}
				}
			}
		}
	}

	/**
	 * @param param
	 * @return
	 */
	private boolean isAnExpressionGuessedByKind(Parameter param) {
		return NODE_KIND_EXPRESSION.equalsIgnoreCase(param.getKind());
	}

	/**
	 * @param param
	 * @return
	 */
	private boolean isAnExpressionGuessedByName(Parameter param) {
		return NODE_KIND_EXPRESSION.equals(param.getName());
	}

	/**
	 * @param param
	 * @return
	 */
	private boolean isArrayType(Parameter param) {
		return "array".equalsIgnoreCase(param.getType());
	}

	/**
	 * @param param
	 * @return
	 */
	public boolean isElementKind(Parameter param) {
		return NODE_KIND_ELEMENT.equalsIgnoreCase(param.getKind());
	}

	/**
	 * @param param
	 * @return
	 */
	private boolean isAttributeKind(Parameter param) {
		return NODE_KIND_ATTRIBUTE.equalsIgnoreCase(param.getKind());
	}

	/**
	 * @param param
	 * @return
	 */
	public boolean isDataFormatDefinition(Parameter param) {
		return "org.apache.camel.model.DataFormatDefinition".equalsIgnoreCase(param.getJavaType());
	}

	private boolean isRedeliveryPolicy(Parameter param) {
		return "redeliveryPolicy".equals(param.getName());
	}

	/**
	 * retrieves the eip meta model for a given eip name
	 *
	 * @param name
	 * @return the eip or null if not found
	 */
	public Eip getEipByName(String name) {
		IProject project;
		if(getCamelFile() != null && getCamelFile().getResource() != null){
			project = getCamelFile().getResource().getProject();
		} else {
			project = null;
		}
		CamelModel model = CamelCatalogCacheManager.getInstance().getCamelModelForProject(project);
		// then we get the eip meta model
		Eip eip = model.getEip(name);
		// special case for context wide endpoint definitions
		if (eip == null && ENDPOINT_NODE_NAME.equals(name)) {
			eip = model.getEip(ENDPOINT_TYPE_TO);
		}
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
				if (isElementKind(p)
						&& isArrayType(p)
						&& !"exception".equals(p.getName())) {
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
			return CHOICE_NODE_NAME.equalsIgnoreCase(eip.getName()); // choice
																		// is
																// special case
		}
		return false;
	}

	/**
	 * parses the children of this node
	 */
	protected void parseChildren() {
		String nodeName = getTagNameWithoutPrefix();
		boolean canHaveChildren = hasChildren(nodeName) || hasSpecialHandling(nodeName);
		if (canHaveChildren) {
			NodeList children = getXmlNode().getChildNodes();
			AbstractCamelModelElement lastNode = null;
			for (int i = 0; i < children.getLength(); i++) {
				Node tmp = children.item(i);
				if (tmp.getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}
				if (!isUsedAsAttribute(tmp)) {
					AbstractCamelModelElement cme = new CamelBasicModelElement(this, tmp);
					addChildElement(cme);
					cme.initialize();
					boolean createLink = lastNode != null && !CHOICE_NODE_NAME.equals(this.getNodeTypeId());
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
		return CHOICE_NODE_NAME.equalsIgnoreCase(getTagNameWithoutPrefix()) && OTHERWISE_NODE_NAME.equalsIgnoreCase(CamelUtils.getTagNameWithoutPrefix(childNode));
	}

	/**
	 * checks whether the node is used as attribute instead of child
	 * (expressions or alike)
	 *
	 * @param childNode
	 * @return
	 */
	protected boolean isUsedAsAttribute(Node childNode) {
		String nodeName = CamelUtils.getTagNameWithoutPrefix(childNode);

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
				if (NODE_KIND_EXPRESSION.equals(p.getKind())) {
					List<String> langs = Arrays.asList(p.getOneOf());
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
		if (getUnderlyingMetaModelObject() == null) {
			return;
		}
		for (Parameter p : getUnderlyingMetaModelObject().getParameters()) {
			if (isAnExpressionGuessedByKind(p) || isElementKind(p)) {
				for (AbstractCamelModelElement child : getChildElements()) {
					if (child.getNodeTypeId().equalsIgnoreCase(p.getName()) && "object".equalsIgnoreCase(p.getType())) {
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
			if (attr != null) {
				getXmlNode().getAttributes().removeNamedItem(CamelUtils.getTagNameWithoutPrefix(attr));
			}
		}
	}

	/**
	 * returns the key to the icon for this node
	 *
	 * @return
	 */
	public String getIconName() {
		if (isEndpointElement()) {
			String u = (String) getParameter(URI_PARAMETER_KEY);
			if (u != null && u.trim().length() > 0) {
				if (u.startsWith("ref:")) {
					// if its a ref we lookup what is the reference scheme
					String refId = u.substring(u.indexOf(':') + 1);
					CamelRouteContainerElement container = getRouteContainer();
					if (container instanceof CamelContextElement) {
						AbstractCamelModelElement endpointRef = ((CamelContextElement)container).getEndpointDefinitions().get(refId);
						if (endpointRef != null) {
							String refUri = (String) endpointRef.getParameter(URI_PARAMETER_KEY);
							if (refUri != null && refUri.contains(":")) {
								return refUri.substring(0, refUri.indexOf(':'));
							} else {
								// seems we have a broken ref
								return ENDPOINT_NODE_NAME;
							}
						}
					} else {
						CamelModelServiceCoreActivator.pluginLog().logWarning("ref: notation not supported for " + container.getNodeTypeId());
					}
				} else {
					if (u.indexOf(':') != -1) {
						return u.substring(0, u.indexOf(':'));
					}
				}
			}
			return ENDPOINT_NODE_NAME;
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
				: xmlNode != null ? CamelUtils.getTagNameWithoutPrefix(xmlNode) : "camelContext";
	}

	/**
	 * returns the documentation name for the eip
	 *
	 * @return
	 */
	public String getDocumentationFileName() {
		if (isEndpointElement()) {
			return ENDPOINT_NODE_NAME;
		}
		return String.format("%sEIP", getNodeTypeId());
	}

	/**
	 * returns the camel file this element belongs to
	 *
	 * @return the camel file or null if not persisted yet
	 */
	public CamelFile getCamelFile() {
		if (this.cf == null) {
			AbstractCamelModelElement tmp = this;
			while (tmp.getParent() != null && !(tmp.getParent() instanceof CamelFile)) {
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
	public CamelRouteContainerElement getRouteContainer() {
		if (this.container == null) {
			AbstractCamelModelElement tmp = this;
			while (tmp.getParent() != null && !(tmp.getParent() instanceof CamelRouteContainerElement)) {
				tmp = tmp.getParent();
			}
			if (tmp.getParent() != null && tmp.getParent() instanceof CamelRouteContainerElement) {
				this.container = (CamelRouteContainerElement) tmp.getParent();
			}
		}
		return this.container;
	}

	/**
	 * creates a random id
	 *
	 * @return a random id
	 */
	public String getNewID() {
		int i = 1;
		String answer = String.format("_%s%d", getNodeTypeId(), i++);
		while (!getRouteContainer().isNewIDAvailable(answer)) {
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
		if (id == null || id.trim().length() < 1) {
			return false;
		}

		if (getRouteContainer().findAllNodesWithId(id).size()>1) {
			return false;
		}

		return true;
	}

	/**
	 * tests if the given id is context wide unique
	 *
	 * @param newId
	 * @return
	 */
	public boolean isNewIDAvailable(String newId) {
		if (newId == null || newId.trim().isEmpty()) {
			return false;
		}
		return getRouteContainer() == null || getRouteContainer().findNode(newId) == null;
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
			if (ctx.getDataformats().containsKey(nodeId)) {
				return ctx.getDataformats().get(nodeId);
			}
			if (ctx.getEndpointDefinitions().containsKey(nodeId)) {
				return ctx.getEndpointDefinitions().get(nodeId);
			}
		}

		if (getChildElements() != null) {
			for (AbstractCamelModelElement e : getChildElements()) {
				AbstractCamelModelElement cme = e.findNode(nodeId);
				if (cme != null) {
					return cme;
				}
			}
		}

		return null;
	}

	public List<AbstractCamelModelElement> findAllNodesWithId(String nodeId) {
		List<AbstractCamelModelElement> result = new ArrayList<>();

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
				!WHEN_NODE_NAME.equals(getNodeTypeId()) && // not working for
															// When nodes
				!OTHERWISE_NODE_NAME.equals(getNodeTypeId()); // not working for
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

	public String getTagNameWithoutPrefix() {
		return CamelUtils.getTagNameWithoutPrefix(getXmlNode());
	}
	
	/**
	 * Notify registered remove-listeners, that a given CamelModelElement was removed
	 * 
	 * @param modelElement deleted element
	 */
	protected void notifyAboutDeletion(AbstractCamelModelElement modelElement) {
		IEventBroker eventBroker = PlatformUI.getWorkbench().getService(IEventBroker.class);
		if (eventBroker != null) {
			eventBroker.post(AbstractCamelModelElement.TOPIC_REMOVE_CAMEL_ELEMENT, modelElement);
		}
	}
}
