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
import org.fusesource.ide.camel.model.service.core.catalog.CamelModel;
import org.fusesource.ide.camel.model.service.core.catalog.CamelModelFactory;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.service.core.internal.CamelModelServiceCoreActivator;
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
public class CamelModelElement {
	
	protected static final String ID_ATTRIBUTE = "id";
	
	// children is a list of objects which are no route outputs
	private List<CamelModelElement> childElements = new ArrayList<CamelModelElement>();
	
	// input is the element which comes before this one
	private CamelModelElement inputElement;
		
	// output is the route output of this element
	private CamelModelElement outputElement;

	// the parent node
	private CamelModelElement parent;
	
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
	 * @param parent			the parent object
	 * @param underlyingNode	the camel xml node
	 */
	public CamelModelElement(CamelModelElement parent, Node underlyingNode) {
		this.xmlNode = underlyingNode;
		this.parent = parent;
		if (underlyingNode != null) setUnderlyingMetaModelObject(getEipByName(underlyingNode.getNodeName()));
		if (parent != null && parent.getXmlNode() != null && underlyingNode != null) {
			boolean alreadyChild = false;
			for (int i = 0; i < parent.getXmlNode().getChildNodes().getLength(); i++) {
				if (parent.getXmlNode().getChildNodes().item(i).isEqualNode(underlyingNode)) {
					alreadyChild = true;
					break;
				}
			}
			if (!alreadyChild) parent.getXmlNode().appendChild(underlyingNode);
		}
	}
	
	/**
	 * @return the parent
	 */
	public CamelModelElement getParent() {
		return this.parent;
	}
	
	/**
	 * @param parent the parent to set
	 */
	public void setParent(CamelModelElement parent) {
		this.parent = parent;
		if (getCamelFile() != null) getCamelFile().fireModelChanged();
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		if (getParameter(ID_ATTRIBUTE) != null) return (String)this.getParameter(ID_ATTRIBUTE);
		return null;
	}
	
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.setParameter(ID_ATTRIBUTE, id);
		if (getCamelFile() != null) getCamelFile().fireModelChanged();
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
		if (getCamelFile() != null) getCamelFile().fireModelChanged();
	}
	
	/**
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}
	
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
		setParameter("description", description);
		if (getCamelFile() != null) getCamelFile().fireModelChanged();
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
		// honor the PREFER_ID_AS_LABEL preference
		// we initially set it to the value of the contains method
		boolean preferID = false;
		if (useID) {
			preferID = PreferenceManager.getInstance().containsPreference(
					PreferencesConstants.EDITOR_PREFER_ID_AS_LABEL);
			// as second step if the value is there, we use it for the flag
			if (PreferenceManager.getInstance().containsPreference(PreferencesConstants.EDITOR_PREFER_ID_AS_LABEL)) {
				preferID = PreferenceManager.getInstance().loadPreferenceAsBoolean(
						PreferencesConstants.EDITOR_PREFER_ID_AS_LABEL);
			}
		}

		// we only return the id if we are told so by the preference AND the
		// value of the ID is set != null
		if (preferID && getId() != null) {
			return getId();
		}

		if (this.getUnderlyingMetaModelObject().getName().equalsIgnoreCase("from") ||
			this.getUnderlyingMetaModelObject().getName().equalsIgnoreCase("to")) {
			String uri = (String)getParameter("uri");
			if (uri != null && uri.trim().length()>0) {
				// uri specified, use it
				return uri;
			}
		} 
		
		String eipType = getNodeTypeId();
		// For some nodes, we just return their node name
		String[] nodeNameOnly = new String[]{
				"choice", "try", "finally","otherwise", "marshal",  "unmarshal" 
		};
		if( Arrays.asList(nodeNameOnly).contains(eipType))
			return eipType;
		
		// Some nodes just need the value of a param
		HashMap<String, String> singlePropertyDisplay = new HashMap<String, String>();
		singlePropertyDisplay.put("bean",  "ref");
		singlePropertyDisplay.put("convertBodyTo",  "type");
		singlePropertyDisplay.put("enrich",  "uri");
		singlePropertyDisplay.put("inOnly",  "uri");
		singlePropertyDisplay.put("inOut",  "uri");
		singlePropertyDisplay.put("interceptSendToEndpoint",  "uri");
		singlePropertyDisplay.put("log",  "logName");
		singlePropertyDisplay.put("onException",  "exception");
		singlePropertyDisplay.put("pollEnrich",  "uri");
		singlePropertyDisplay.put("removeHeader",  "headerName");
		singlePropertyDisplay.put("removeProperty",  "propertyName");
		singlePropertyDisplay.put("rollback",  "message");
		singlePropertyDisplay.put("sort",  "expression");
		singlePropertyDisplay.put("when",  "expression");
		
		String propertyToCheck = singlePropertyDisplay.get(eipType);
//		if( propertyToCheck != null ) {
//			Object propVal = getShortPropertyValue(propertyToCheck, Object.class);
//			String suffix = null;
//			if( propVal instanceof ExpressionDefinition ) {
//				suffix = Expressions.getExpressionOrElse(((ExpressionDefinition)propVal));
//			} else {
//				suffix = Strings.getOrElse(propVal);
//			}
//			String ret = convertCamelCase(eipType) + " " + suffix;
//			return ret;
//		}
//		
//		
//		if ("catch".equals(eipType)) {
//			List exceptions = getShortPropertyValue("exception", List.class);
//			if (exceptions != null && exceptions.size() > 0) {
//				return "catch " + exceptions;
//			} else {
//				return "catch " + Expressions.getExpressionOrElse(getShortPropertyValue("handled", ExpressionDefinition.class));
//			}
//		} else if ("setExchangePattern".equals(eipType)) {
//			ExchangePattern pattern = getShortPropertyValue("handled", ExchangePattern.class);
//			if (pattern == null) {
//				return "setExchangePattern";
//			} else {
//				return "set " + pattern;
//			}
//		} else if ("loadBalance".equals(eipType)) {
//			String ref = getShortPropertyValue("ref", String.class);
//			if (ref != null) {
//				return "custom " + Strings.getOrElse(ref);
//			} 
//			Object loadType = getShortPropertyValue("loadBalancerType", Object.class);
//			if (loadType  != null) {
//				if (loadType.getClass().isAssignableFrom(CustomLoadBalancerDefinition.class)) {
//					CustomLoadBalancerDefinition custom = (CustomLoadBalancerDefinition) loadType;
//					return "custom " + Strings.getOrElse(custom.getRef());
//				} else if (loadType.getClass().isAssignableFrom(FailoverLoadBalancerDefinition.class)) {
//					return "failover";
//				} else if (loadType.getClass().isAssignableFrom(RandomLoadBalancerDefinition.class)) {
//					return "random";
//				} else if (loadType.getClass().isAssignableFrom(RoundRobinLoadBalancerDefinition.class)) {
//					return "round robin";
//				} else if (loadType.getClass().isAssignableFrom(StickyLoadBalancerDefinition.class)) {
//					return "sticky";
//				} else if (loadType.getClass().isAssignableFrom(TopicLoadBalancerDefinition.class)) {
//					return "topic";
//				} else if (loadType.getClass().isAssignableFrom(WeightedLoadBalancerDefinition.class)) {
//					return "weighted";
//				}
//			} else {
//				return "load balance";
//			}
//		}

		String answer = null;
		if (Strings.isBlank(answer)) {
			answer = getId();
		}
		if (Strings.isBlank(answer)) {
			answer = getUnderlyingMetaModelObject().getName();
		}
		return answer;
	}
	
	/**
	 * @return the inputElement
	 */
	public CamelModelElement getInputElement() {
		return this.inputElement;
	}
	
	/**
	 * @param inputElement the inputElement to set
	 */
	public void setInputElement(CamelModelElement inputElement) {
		this.inputElement = inputElement;
		// now move the node directly after inputElement in DOM tree
		if (inputElement != null) {
			Node inputNode = inputElement.getXmlNode();
			inputNode.getParentNode().insertBefore(getXmlNode(), inputNode.getNextSibling());
		}
		if (getCamelFile() != null) getCamelFile().fireModelChanged();
	}
	
	/**
	 * @return the outputElement
	 */
	public CamelModelElement getOutputElement() {
		return this.outputElement;
	}
	
	/**
	 * @param outputElement the outputElement to set
	 */
	public void setOutputElement(CamelModelElement outputElement) {
		this.outputElement = outputElement;
		if (getCamelFile() != null) getCamelFile().fireModelChanged();
	}
	
	/**
	 * @return the childElements
	 */
	public List<CamelModelElement> getChildElements() {
		return this.childElements;
	}
	
	/**
	 * @param childElements the childElements to set
	 */
	public void setChildElements(List<CamelModelElement> childElements) {
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
	public void addChildElement(CamelModelElement element) {
		if (this.childElements.contains(element) == false) {
			this.childElements.add(element);
			if (getCamelFile() != null) getCamelFile().fireModelChanged();
		}			
	}
	
	/**
	 * removes a child element
	 * 
	 * @param element
	 */
	public void removeChildElement(CamelModelElement element) {
		if (childElements.contains(element)) {
			childElements.remove(element);
			getXmlNode().removeChild(element.getXmlNode());
			if (getCamelFile() != null) getCamelFile().fireModelChanged();
		}
	}
	
	/**
	 * @return the parameters
	 */
	public Map<String, Object> getParameters() {
		return this.parameters;
	}
	
	/**
	 * @param parameters the parameters to set
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
			this.parameters.remove(name);
			((Element)getXmlNode()).removeAttribute(name);
			if (getCamelFile() != null) getCamelFile().fireModelChanged();
		}
	}
	
	/**
	 * returns the parameter with the given name or null if not available
	 * 
	 * @param name	the parameter name
	 * @return	the parameter or null if not available
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
		Object oldValue = this.parameters.get(name);
		this.parameters.put(name, value);
		Element e = (Element)getXmlNode();
		String kind = getUnderlyingMetaModelObject() != null ? getUnderlyingMetaModelObject().getParameter(name).getKind() : null;
		if (this instanceof CamelContextElement) kind = "attribute";
		if (value == null || value.toString().trim().length()<1) {
			// seems the attribute has been deleted?
			if (kind.equalsIgnoreCase("attribute") && e.hasAttribute(name)) {
				e.removeAttribute(name);
			} else if (kind.equalsIgnoreCase("element") || kind.equalsIgnoreCase("expression")) {
				for (int i=0; i<e.getChildNodes().getLength(); i++) {
					Node subElem = e.getChildNodes().item(i);
					if (subElem.getNodeName().equals(name)) {
						// found the sub element -> delete it
						e.removeChild(subElem);
						break;
					}
				}
			} else if (kind.equalsIgnoreCase("value")) {
				e.setTextContent(null);
			}
		} else {
			if (kind.equalsIgnoreCase("attribute")) {
				String defaultValue = this.underlyingMetaModelObject != null ? this.underlyingMetaModelObject.getParameter(name).getDefaultValue() : null;
				if (defaultValue != null && defaultValue.equals(getMappedValue(value))) {
					// default value -> no need to explicitely set it -> delete existing
					e.removeAttribute(name);
				} else {
					// not the default value, so set it
					e.setAttribute(name, getMappedValue(value));				
				}
			} else if (kind.equalsIgnoreCase("element") && name.equals("description")) {
				// description element handling
				Eip subEip = getEipByName(name);
				if (subEip != null) {
					// seems this parameter is another eip type -> we need to create/modify a subnode
					boolean createSubNode = true;
					Node subNode = null;
					for (int c = 0; c < e.getChildNodes().getLength(); c++) {
						subNode = e.getChildNodes().item(c);
						if (subNode.getNodeName().equals(name)) {
							createSubNode = false;
							break;
						}
					}
					if (createSubNode) {
						subNode = getCamelFile().getDocument().createElement(name);
						e.appendChild(subNode);
					}
					subNode.setTextContent(getMappedValue(value));
				}
			} else if (kind.equalsIgnoreCase("expression")) {
				// expression element handling
				CamelModelElement exp = null;
				if (value instanceof CamelModelElement) {
					exp = (CamelModelElement)value;
				}
				Eip subEip = getEipByName(exp.getNodeTypeId());
				if (subEip != null) {
					// seems this parameter is another eip type -> we need to create/modify a subnode
					boolean createSubNode = true;
					Node subNode = null;
					for (int c = 0; c < e.getChildNodes().getLength(); c++) {
						subNode = e.getChildNodes().item(c);
						if (subNode.getNodeName().equals(exp.getNodeTypeId())) {
							createSubNode = false;
							break;
						}
					}
					if (createSubNode) {
						subNode = getCamelFile().getDocument().createElement(name);
						e.appendChild(subNode);
					}
					for (int i = 0; i<((Element)subNode).getAttributes().getLength(); i++) {
						Node attrNode = ((Element)subNode).getAttributes().item(i);
						((Element)subNode).removeAttribute(attrNode.getNodeName());
					}
					Iterator<String> pKeys = exp.getParameters().keySet().iterator();
					while (pKeys.hasNext()) {
						String pKey = pKeys.next();
						Object oValue = exp.getParameter(pKey);
						// expressions shouldn't have expression attributes but values
						if (subEip.getParameter(pKey).getKind().equalsIgnoreCase("value")) {
							if (oValue != null && oValue.toString().trim().length()>0) ((Element)subNode).setNodeValue(oValue.toString());
						} else {
							if (oValue != null && oValue.toString().trim().length()>0) ((Element)subNode).setAttribute(pKey, oValue.toString());									
						}
					}
				}
			} else if (kind.equalsIgnoreCase("value")) {
				e.setTextContent(getMappedValue(value));
			}
		}
		if (getCamelFile() != null && oldValue != value) getCamelFile().fireModelChanged();
	}
	
//	/**
//	 * deletes all parameters
//	 */
//	public void clearParameters() {
//		this.parameters.clear();
//	}

	/**
	 * @return the xmlNode
	 */
	public Node getXmlNode() {
		return this.xmlNode;
	}
	
	/**
	 * @param xmlNode the xmlNode to set
	 */
	public void setXmlNode(Node xmlNode) {
		this.xmlNode = xmlNode;
		if (getCamelFile() != null) getCamelFile().fireModelChanged();
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
	 * @param underlyingMetaModelObject the underlyingMetaModelObject to set
	 */
	public void setUnderlyingMetaModelObject(Eip underlyingMetaModelObject) {
		this.underlyingMetaModelObject = underlyingMetaModelObject;
	}
	
	/**
	 * puts back all changes to the underlying xml node to be saved to disc
	 */
	public void saveChanges() {
//		if (!hasUnderlyingXmlNode()) {
//			this.xmlNode = createNode();
//		}
//		updateUnderlyingNode();
	}
	
	/**
	 * creates an empty node object with the name of the element
	 * 
	 * @return	the new node object which can be injected to the dom afterwards
	 */
	protected Node createNode() {
		return getCamelFile() != null && getCamelFile().getDocument() != null ? getCamelFile().getDocument().createElement(name) : null;
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
	 * parses the node attributes into the params map and starts the parsing of subnodes
	 */
	protected void parseNode() {
		// first parse direct attributes
		parseAttributes();
		
		// now parse child elements
		parseChildren();
		
		// link child attributes like expressions to parent parameters
		linkChildrenToAttributes();
	}
	
	protected void ensureUniqueID(CamelModelElement elem) {
		if (elem.getId() == null || elem.getId().trim().length()<1) {
			elem.setId(elem.getNewID());
		}
		for (CamelModelElement e : elem.getChildElements()) {
			ensureUniqueID(e);
		}
	}
	
	/**
	 * parses direct attributes of the node
	 */
	protected void parseAttributes() {
		// first get the element name
		String nodename = getXmlNode().getNodeName();
		// now try to match with an EIP name
		Eip eip = getEipByName(nodename);
		if (eip != null) {
			for (Parameter param : eip.getParameters()) {
				if (param.getKind().equalsIgnoreCase("attribute")) {
					// now loop all meta model parameter and check if we have them in the node
					Node tmp = getXmlNode().getAttributes().getNamedItem(param.getName());
					if (tmp != null) {
						// now map the node attribute into our EIP parameters
						setParameter(param.getName(), tmp.getNodeValue());
					}
				} else if (param.getKind().equalsIgnoreCase("element")) {
					Node descNode = null;
					for (int i=0; i<getXmlNode().getChildNodes().getLength(); i++) {
						Node subNode = getXmlNode().getChildNodes().item(i);
						if (subNode.getNodeName().equals(param.getName())) {
							descNode = subNode;
							break;
						}
					}
					if (descNode != null) {
						String val = descNode.getTextContent();
						if (val != null) {
							setParameter(param.getName(), val);
							if (param.getName().equalsIgnoreCase("description")) setDescription(val);
						}						
					}
				} else if (param.getKind().equalsIgnoreCase("value")) {
					String val = getXmlNode().getTextContent();
					if (val != null) {
						setParameter(param.getName(), val);
						if (param.getName().equalsIgnoreCase("description")) setDescription(val);
					}
				} else if (param.getKind().equalsIgnoreCase("expression")) {
					CamelModelElement expNode = null;
					String[] langs = param.getOneOf().split(",");
					ArrayList<String> langList = new ArrayList<String>();
			        for (String lang : langs) langList.add(lang);
					for (int i = 0; i<getXmlNode().getChildNodes().getLength(); i++) {
						Node subNode = getXmlNode().getChildNodes().item(i);
						if (subNode != null && langList.contains(subNode.getNodeName())) {
							expNode = new CamelModelElement(this, subNode);
							expNode.initialize();
							expNode.setParent(this);
							setParameter(param.getName(), expNode);
						}
					}
				} else {
					// ignore the other kinds
				}
			}
		} else {
			CamelModelServiceCoreActivator.pluginLog().logWarning("ParseAttributes: Unsupported EIP will be ignored: " + nodename);
		}
	}
	
	/**
	 * retrieves the eip meta model for a given eip name
	 * 
	 * @param name
	 * @return	the eip or null if not found
	 */
	protected Eip getEipByName(String name) {
		String prjCamelVersion = CamelModelFactory.getLatestCamelVersion();
		if (getCamelFile() != null) {
			// get the project from the camel file resource
			IProject prj = getCamelFile().getResource().getProject();
			// now try to determine the configured camel version from the project
			prjCamelVersion = CamelModelFactory.getCamelVersion(prj);
			// if project doesn't define a camel version we grab the latest supported
			if (prjCamelVersion == null) prjCamelVersion = CamelModelFactory.getLatestCamelVersion();
		}
		// then get the meta model for the given camel version
		CamelModel model = CamelModelFactory.getModelForVersion(prjCamelVersion);
		if (model == null) {
			// if we don't support the defined camel version we take the latest supported instead
			model = CamelModelFactory.getModelForVersion(CamelModelFactory.getLatestCamelVersion());
		}
		// then we get the eip meta model
		Eip eip = model.getEipModel().getEIPByName(name);
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
				if (p.getKind().equalsIgnoreCase("element") && p.getType().equalsIgnoreCase("array")) {
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
			return eip.getName().equalsIgnoreCase("choice");  // choice is special case
		}
		return false;
	}
	
	/**
	 * parses the children of this node
	 */
	protected void parseChildren() {
		String nodeName = this.getXmlNode().getNodeName();
		boolean canHaveChildren = hasChildren(nodeName) || hasSpecialHandling(nodeName);
		if (canHaveChildren) {
			NodeList children = getXmlNode().getChildNodes();
			CamelModelElement lastNode = null;
			for (int i=0; i<children.getLength(); i++) {
				Node tmp = children.item(i);
				if (tmp.getNodeType() != Node.ELEMENT_NODE) continue;
				CamelModelElement cme = new CamelModelElement(this, tmp);
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
	
	/**
	 * checks for each parameter if type element or expression if there is a child and links them
	 */
	protected void linkChildrenToAttributes() {
		if (getUnderlyingMetaModelObject() == null) return;
		for (Parameter p : getUnderlyingMetaModelObject().getParameters()) {
			if (p.getKind().equalsIgnoreCase("expression") || 
				p.getKind().equalsIgnoreCase("element")) {
				for (CamelModelElement child : getChildElements()) {
					if (child.getNodeTypeId().equalsIgnoreCase(p.getName()) && p.getType().equalsIgnoreCase("object")) {
						// so we have a child of type element or expression which should be handled as an attribute
						parameters.put(p.getName(), child);
					}					
				}
			}
		}
	}
	
//	/**
//	 * puts all changes back into the xml node
//	 */
//	protected void updateUnderlyingNode() {
//		// first update children
//		if (getChildElements().isEmpty() == false) {
//			for (CamelModelElement cme : getChildElements()) {
//				cme.updateUnderlyingNode();
//			}
//		}
//		
//		if (getXmlNode() != null) {
//			// then update this
//			// first get the element name
//			String nodename = getXmlNode().getNodeName();
//			// now try to match with an EIP name
//			Eip eip = getEipByName(nodename);
//			Element e = (Element)xmlNode;
//			if (eip != null && xmlNode instanceof Element) {
//				clearAttributes();
//				for (String key : getParameters().keySet()) {
//					Object value = getParameter(key);
//					if (eip.getParameter(key).getKind().equalsIgnoreCase("attribute")) {
//						e.setAttribute(key, getMappedValue(value));
//					} else if (eip.getParameter(key).getKind().equalsIgnoreCase("element") &&
//							   key.equalsIgnoreCase("description")) {
//						// description element handling
//						Eip subEip = getEipByName(key);
//						if (subEip != null) {
//							// seems this parameter is another eip type -> we need to create/modify a subnode
//							boolean createSubNode = true;
//							Node subNode = null;
//							for (int c = 0; c < e.getChildNodes().getLength(); c++) {
//								subNode = e.getChildNodes().item(c);
//								if (subNode.getNodeName().equals(key)) {
//									createSubNode = false;
//									break;
//								}
//							}
//							if (createSubNode) {
//								subNode = getCamelFile().getDocument().createElement(key);
//								e.appendChild(subNode);
//							}
//							subNode.setTextContent(getMappedValue(value));
//						}
//					} else if (eip.getParameter(key).getKind().equalsIgnoreCase("expression")) {
//						// expression element handling
//						CamelModelElement exp = null;
//						if (value instanceof CamelModelElement) {
//							exp = (CamelModelElement)value;
//						}
//						Eip subEip = getEipByName(exp.getNodeTypeId());
//						if (subEip != null) {
//							// seems this parameter is another eip type -> we need to create/modify a subnode
//							boolean createSubNode = true;
//							Node subNode = null;
//							for (int c = 0; c < e.getChildNodes().getLength(); c++) {
//								subNode = e.getChildNodes().item(c);
//								if (subNode.getNodeName().equals(exp.getNodeTypeId())) {
//									createSubNode = false;
//									break;
//								}
//							}
//							if (createSubNode) {
//								subNode = getCamelFile().getDocument().createElement(key);
//								e.appendChild(subNode);
//							}
//							for (int i = 0; i<((Element)subNode).getAttributes().getLength(); i++) {
//								Node attrNode = ((Element)subNode).getAttributes().item(i);
//								((Element)subNode).removeAttribute(attrNode.getNodeName());
//							}
//							Iterator<String> pKeys = exp.getParameters().keySet().iterator();
//							while (pKeys.hasNext()) {
//								String pKey = pKeys.next();
//								Object oValue = exp.getParameter(pKey);
//								// expressions shouldn't have expression attributes but values
//								if (subEip.getParameter(pKey).getKind().equalsIgnoreCase("value")) {
//									if (oValue != null && oValue.toString().trim().length()>0) ((Element)subNode).setNodeValue(oValue.toString());
//								} else {
//									if (oValue != null && oValue.toString().trim().length()>0) ((Element)subNode).setAttribute(pKey, oValue.toString());									
//								}
//							}
//						}
//					} else {
//						// ignore the other kinds
//					}
//				}
//			} else {
//				if (this instanceof CamelContextElement) {
//					if (getId() != null && getId().trim().length()>0) {
//						e.setAttribute("id", getId());
//					}
//				} else {
//					CamelModelServiceCoreActivator.pluginLog().logWarning("UpdateUnderlyingNode: Unsupported EIP will be ignored: " + nodename);					
//				}
//			}
//		} else {
//			// no xml node for this object -> CamelFile
//		}
//		
//		// finally update the output
//		if (getOutputElement() != null) {
//			getOutputElement().updateUnderlyingNode();
//		}
//	}
	
	/**
	 * converts an object into the xml string representation used in the attribute value
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
			if (attr != null) getXmlNode().getAttributes().removeNamedItem(attr.getNodeName());
		}
	}
	
	/**
	 * returns the key to the icon for this node
	 * 
	 * @return
	 */
    public String getIconName() {
    	if (getNodeTypeId().equalsIgnoreCase("from") || getNodeTypeId().equalsIgnoreCase("to")) {
    		String u = (String)getParameter("uri");
    		if (u != null && u.trim().length()>0) {
    			String scheme = null;
    			if (u.startsWith("ref:")) {
    				// if its a ref we lookup what is the reference scheme
    				String refId = u.substring(u.indexOf(":") + 1);
    				CamelModelElement endpointRef = getCamelContext().getEndpointDefinitions().get(refId);
    				if (endpointRef != null) {
    					String refUri = (String)endpointRef.getParameter("uri");
        				if (refUri != null) {
        					scheme = refUri.substring(0, refUri.indexOf(":")+1);
        				} else {
        					// seems we have a broken ref
        					return "endpoint.png";
        				}    					
    				}
    			} else {
    				scheme = u.substring(0, u.indexOf(":")+1);
    			}
    			
    			if (scheme.startsWith("drools:")) {
    				return "endpointDrools.png";
    			} else if (scheme.startsWith("jms:") || scheme.startsWith("activemq") || scheme.startsWith("mq") || scheme.startsWith("sjms")) {
    				return "endpointQueue.png";
    			} else if (scheme.startsWith("file:") || scheme.startsWith("ftp") || scheme.startsWith("sftp") || scheme.startsWith("jcr") || scheme.startsWith("scp")) {
    				return "endpointFolder.png";
    			} else if (scheme.startsWith("log:") || scheme.startsWith("hdfs") || scheme.startsWith("paxlogging")) {
    				return "endpointFile.png";
    			} else if (scheme.startsWith("timer:") || scheme.startsWith("quartz")) {
    				return "endpointTimer.png";
    			} else if (scheme.startsWith("elasticsearch:") || scheme.startsWith("hazelcast:") || scheme.startsWith("hibernate:") || scheme.startsWith("jpa:")
    					|| scheme.startsWith("jdbc:") || scheme.startsWith("sql:") || scheme.startsWith("ibatis:") || scheme.startsWith("mybatis:")
    					|| scheme.startsWith("javaspace:") || scheme.startsWith("jcr:") || scheme.startsWith("ldap:") || scheme.startsWith("mongodb:") || scheme.startsWith("zookeeper:")) {
    				return "endpointRepository.png";
    			} else if (scheme.startsWith("twitter:")) {
    			    return "endpointTwitter.png";
    			} else if (scheme.startsWith("weather:")) {
    			    return "endpointWeather.png";
    			} else if (scheme.startsWith("sap-netweaver:")) {
                    return "endpointSAPNetweaver.png";
                } else if (scheme.startsWith("sap:")) {
                    return "endpointSAP.png";
                } else if (scheme.startsWith("salesforce:")) {
                    return "endpointSalesforce.png";
                } else if (scheme.startsWith("facebook:")) {
                    return "endpointFacebook.png";
                } else if (scheme.startsWith("dozer:")) {
                    return "endpointDozer.png";
                }
    		}
    		return "endpoint.png";
    	}
    	return String.format("%s.png", getNodeTypeId());
    }

	/**
	 * Return the typeid of this node, if applicable. 
	 * This should match the parameter name from the eip.xml model, so for example, 
	 * doTry, resequence, etc
	 * @return
	 */
	public String getNodeTypeId() {
		return underlyingMetaModelObject != null ? underlyingMetaModelObject.getName() : "camelContext";
	}
    
	/**
	 * returns the documentation name for the eip
	 * 
	 * @return
	 */
    public String getDocumentationFileName() {
    	return String.format("%sEIP", getNodeTypeId());
    }

    /**
     * returns the category this item belongs to
     * 
     * @return
     */
    public String getCategoryName() {
    	// TODO: find a good way to define categories
    	return getUnderlyingMetaModelObject().getTags().get(getUnderlyingMetaModelObject().getTags().size()-1);
    }
    
    /**
     * returns the camel file this element belongs to
     * 
     * @return	the camel file or null if not persisted yet
     */
    public CamelFile getCamelFile() {
    	if (this.cf == null) {
        	CamelModelElement tmp = this;
        	while (tmp.getParent() != null && tmp.getParent() instanceof CamelFile == false) {
        		tmp = tmp.getParent();
        	}
        	if (tmp.getParent() != null && tmp.getParent() instanceof CamelFile) {
        		this.cf = (CamelFile)tmp.getParent();
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
        	CamelModelElement tmp = this;
        	while (tmp.getParent() != null && tmp.getParent() instanceof CamelContextElement == false) {
        		tmp = tmp.getParent();
        	}
        	if (tmp.getParent() != null && tmp.getParent() instanceof CamelContextElement) {
        		this.context = (CamelContextElement)tmp.getParent();
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
		while (getCamelContext().isUniqueId(answer) == false) {
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
	public boolean isUniqueId(String newId) {
		if (newId == null || newId.trim().length()<1) return false;
		
		if (getCamelContext().findNode(newId) != null) return false;
		
		return true;
	}
	
	/**
	 * searches the model for a node with the given id
	 * 
	 * @param nodeId
	 * @return	the node or null
	 */
	public CamelModelElement findNode(String nodeId) {
		if (getId() != null && getId().equals(nodeId)) {
			return this;
		}
		
		if (getChildElements() != null) {
			for (CamelModelElement e : getChildElements()) {
				CamelModelElement cme = e.findNode(nodeId);
				if (cme != null) return cme;
			}
		}
		
		return null;
	}
	
	/**
	 * checks if the node is the from node and therefore
	 * the first node in my route
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
		return  !isFirstNodeInRoute() && 				// not working on the From node
				!"when".equals(getNodeTypeId()) && 		// not working for When nodes
				!"otherwise".equals(getNodeTypeId());	// not working for Otherwise nodes
	}
}
