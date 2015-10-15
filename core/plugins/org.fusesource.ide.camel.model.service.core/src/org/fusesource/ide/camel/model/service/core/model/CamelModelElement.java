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
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return (String)this.parameters.get(ID_ATTRIBUTE);
	}
	
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.parameters.put(ID_ATTRIBUTE, id);
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
		}			
	}
	
	/**
	 * removes a child element
	 * 
	 * @param element
	 */
	public void removeChildElement(CamelModelElement element) {
		childElements.remove(element);
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
		this.parameters.remove(name);
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
		this.parameters.put(name, value);
	}
	
	/**
	 * deletes all parameters
	 */
	public void clearParameters() {
		this.parameters.clear();
	}

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
		if (!hasUnderlyingXmlNode()) {
			this.xmlNode = createNode();
		}
		updateUnderlyingNode();
	}
	
	/**
	 * creates an empty node object
	 * 
	 * @return	the new node object
	 */
	protected Node createNode() {
		/**
		 * TODO: obtain the document from some parent and create a new Node object
		 */
		return null;
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
				if (param.getKind().equalsIgnoreCase("attribute") == false) continue; // we only process attributes
				// now loop all meta model parameter and check if we have them in the node
				Node tmp = getXmlNode().getAttributes().getNamedItem(param.getName());
				if (tmp != null) {
					// now map the node attribute into our EIP parameters
					setParameter(param.getName(), tmp.getNodeValue());
				}
			}
		} else {
			CamelModelServiceCoreActivator.pluginLog().logWarning("Unsupported EIP will be ignored: " + nodename);
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
		System.err.println("Checking linkable child attributes of: " + getNodeTypeId());
		for (Parameter p : getUnderlyingMetaModelObject().getParameters()) {
			if (p.getKind().equalsIgnoreCase("expression") || 
				p.getKind().equalsIgnoreCase("element")) {
				for (CamelModelElement child : getChildElements()) {
					if (child.getNodeTypeId().equalsIgnoreCase(p.getName()) && p.getType().equalsIgnoreCase("object")) {
						// so we have a child of type element or expression which should be handled as an attribute
						parameters.put(p.getName(), child);
						System.err.println("assigned child node " + child.getNodeTypeId() + " as attribute of element " + getNodeTypeId());
					}					
				}
			}
		}
	}
	
	/**
	 * puts all changes back into the xml node
	 */
	protected void updateUnderlyingNode() {
		// first update children
		if (getChildElements().isEmpty() == false) {
			for (CamelModelElement cme : getChildElements()) {
				cme.updateUnderlyingNode();
			}
		}
		
		// then update this
		// first get the element name
		String nodename = getXmlNode().getNodeName();
		// now try to match with an EIP name
		Eip eip = getEipByName(nodename);
		if (eip != null && xmlNode instanceof Element) {
			clearAttributes();
			Element e = (Element)xmlNode;
			for (String key : getParameters().keySet()) {
				Object value = getParameter(key);
				e.setAttribute(key, getMappedValue(value));
			}
		} else {
			CamelModelServiceCoreActivator.pluginLog().logWarning("Unsupported EIP will be ignored: " + nodename);
		}
		
		// finally update the output
		if (getOutputElement() != null) {
			getOutputElement().updateUnderlyingNode();
		}
	}
	
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
			xmlNode.removeChild(attr);
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
		return underlyingMetaModelObject.getName();
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
}
