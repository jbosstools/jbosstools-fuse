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

import java.util.Iterator;

import org.fusesource.ide.foundation.core.xml.XmlEscapeUtility;
import org.w3c.dom.Node;

/**
 * @author lhein
 */
public class CamelEndpoint extends CamelModelElement {

	private static final String ICON = "endpoint.png";
	
	/**
	 * @param parent
	 * @param underlyingNode
	 */
	public CamelEndpoint(String uri) {
		super(null, null);
		setParameter("uri", XmlEscapeUtility.unescape(uri));
	}

	/**
	 * retrieves the uri of the endpoint
	 * 
	 * @return
	 */
	public String getUri() {
		return (String)getParameter("uri");
	}
	
	/**
	 * sets the uri of the endpoint
	 * 
	 * @param uri
	 */
	public void setUri(String uri) {
		setParameter("uri", XmlEscapeUtility.unescape(uri));
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.model.CamelModelElement#setParent(org.fusesource.ide.camel.model.service.core.model.CamelModelElement)
	 */
	@Override
	public void setParent(CamelModelElement parent) {
		super.setParent(parent);
		if (parent != null && parent.getXmlNode() != null && getXmlNode() != null) {
			boolean alreadyChild = false;
			for (int i = 0; i < parent.getXmlNode().getChildNodes().getLength(); i++) {
				if (parent.getXmlNode().getChildNodes().item(i).isEqualNode(getXmlNode())) {
					alreadyChild = true;
					break;
				}
			}
			if (!alreadyChild) {				
				parent.getXmlNode().appendChild(getXmlNode());	
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.model.CamelModelElement#getIconName()
	 */
	@Override
	public String getIconName() {
		String u = getUri();
		if (u != null && u.trim().length()>0) {
			String scheme = null;
			if (u.startsWith("ref:")) {
				// if its a ref we lookup what is the reference scheme
				String refId = u.substring(u.indexOf(":") + 1);
				CamelContextElement c = getCamelContext();
				String refUri = (String)c.getEndpointDefinitions().get(refId).getParameter("uri");
				if (refUri != null) {
					scheme = refUri.substring(0, refUri.indexOf(":")+1);
				} else {
					// seems we have a broken ref
					return ICON;
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
		return ICON;
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.model.CamelModelElement#getDocumentationFileName()
	 */
	@Override
	public String getDocumentationFileName() {
		return "endpoint";
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.model.CamelModelElement#getCategoryName()
	 */
	@Override
	public String getCategoryName() {
		return "Components";
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.model.CamelModelElement#setInputElement(org.fusesource.ide.camel.model.service.core.model.CamelModelElement)
	 */
	@Override
	public void setInputElement(CamelModelElement inputElement) {
		super.setInputElement(inputElement);
		checkEndpointType();
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.model.CamelModelElement#setOutputElement(org.fusesource.ide.camel.model.service.core.model.CamelModelElement)
	 */
	@Override
	public void setOutputElement(CamelModelElement outputElement) {
		super.setOutputElement(outputElement);
		checkEndpointType();
	}
	
	private void checkEndpointType() {
		if (isFromEndpoint() && getUnderlyingMetaModelObject() != null && getUnderlyingMetaModelObject().getName().equalsIgnoreCase("to")) {
			// switch from a TO endpoint to a FROM endpoint
			setUnderlyingMetaModelObject(getEipByName("from"));
			if (getXmlNode() != null) {
				Node newNode = getCamelFile().getDocument().createElement("from");
				getParent().getXmlNode().replaceChild(newNode, getXmlNode());
				setXmlNode(newNode);
				updateXMLNode();
			}
		} else if (isToEndpoint() && getUnderlyingMetaModelObject() != null && getUnderlyingMetaModelObject().getName().equalsIgnoreCase("from")) {
			// switch from a FROM endpoint to a TO endpoint
			setUnderlyingMetaModelObject(getEipByName("to"));
			if (getXmlNode() != null) {
				Node newNode = getCamelFile().getDocument().createElement("to");
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
	protected void updateXMLNode() {
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
	 * @return	true if this is a FROM endpoint
	 */
	public boolean isFromEndpoint() {
		return getInputElement() == null;
	}

	/**
	 * this is an output endpoint if it is a target
	 * 
	 * @return	true if this is a TO endpoint
	 */
	public boolean isToEndpoint() {
		return getInputElement() != null;
	}
}
