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

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.xml.core.internal.XMLCorePlugin;
import org.fusesource.ide.camel.model.service.core.catalog.CamelModel;
import org.fusesource.ide.camel.model.service.core.catalog.CamelModelFactory;
import org.fusesource.ide.camel.model.service.core.internal.CamelModelServiceCoreActivator;
import org.fusesource.ide.camel.model.service.core.io.CamelIOHandler;
import org.fusesource.ide.foundation.core.util.CamelUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

/**
 * this object represents the camel xml file. It can be of a schema type
 * like Spring or Blueprint. It can also contain Bean Definitions for things
 * like connection factories, property placeholder beans or loadbalancers etc.
 * 
 * The only children for the camel file is the camel context.
 * 
 * @author lhein
 */
public class CamelFile extends AbstractCamelModelElement implements EventListener {
	
	public static final int XML_INDENT_VALUE = 3;
	protected static final String CAMEL_CONTEXT = "camelContext";
	protected static final String CAMEL_ROUTES = "routes";
	protected static final String CAMEL_BEAN = "bean";
	
	/**
	 * these maps contains endpoints and bean definitions stored using their ID value
	 */
	private Map<String, GlobalDefinitionCamelModelElement> globalDefinitions = new HashMap<>();

	/**
	 * the resource the camel file is stored in
	 */
	private IResource resource;
	
	/**
	 * Spring / Blueprint / Routes
	 */
	private CamelSchemaType schemaType;
	
	/**
	 * the xml document
	 */
	private Document document;

	/**
	 * list of listeners looking for changes in the internal model
	 */
	private List<ICamelModelListener> modelListeners = new ArrayList<>();

	/**
	 * creates a camel file object for the given resource
	 * 
	 * @param resource
	 */
	public CamelFile(IResource resource) {
		super(null, null);
		this.resource = resource;
	}

	/**
	 * deletes all contents
	 */
	public void resetContents() {
		getGlobalDefinitions().clear();
		getChildElements().clear();
	}

	@Override
	public void initialize() {
		super.initialize();
		NodeList childNodes = document.getDocumentElement().getChildNodes();
		if (CAMEL_ROUTES.equals(CamelUtils.getTranslatedNodeName(document.getDocumentElement()))) {
			// found a routes element
			CamelRoutesElement cre = new CamelRoutesElement(this, document.getDocumentElement());
			Node namedItem = document.getDocumentElement().getAttributes().getNamedItem("id");
			String containerId = namedItem != null ? namedItem.getNodeValue() : CamelUtils.getTranslatedNodeName(document.getDocumentElement()) + "-" + UUID.randomUUID().toString();
			int startIdx 	= resource.getFullPath().toOSString().indexOf("--");
			int endIdx 		= resource.getFullPath().toOSString().indexOf("--", startIdx+1);
			if (startIdx != endIdx && startIdx != -1) {
				containerId = resource.getFullPath().toOSString().substring(startIdx+2, endIdx);
			}
			cre.setId(containerId);
			cre.initialize();
			// then add the routes to the file
			addChildElement(cre);
		} else {
			for (int i = 0; i<childNodes.getLength(); i++) {
				Node child = childNodes.item(i);
				if (child.getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}
				String name = CamelUtils.getTranslatedNodeName(child);
				String id = computeId(child);
				if (name.equals(CAMEL_CONTEXT)) {
					// found a camel context
					CamelContextElement cce = new CamelContextElement(this, child);
					cce.setId(id);
					cce.initialize();
					// then add the context to the file
					addChildElement(cce);
				} else if (name.equals(CAMEL_ROUTES)) {
					// found a camel context
					CamelRoutesElement cre = new CamelRoutesElement(this, child);
					cre.setId(id);
					cre.initialize();
					// then add the context to the file
					addChildElement(cre);
				} else if (name.equals(CAMEL_BEAN)) {
					// found a camel bean
					CamelBean cb = new CamelBean(this, child);
					cb.setId(id);
					cb.initialize();
					// then add the context to the file
					addGlobalDefinition(id, cb);
				} else {
					// found a global configuration element
					GlobalDefinitionCamelModelElement cme = new GlobalDefinitionCamelModelElement(this, child);
					cme.setId(id);
					cme.initialize();
					addGlobalDefinition(id, cme);
				}
            }
		}
	}

	private String computeId(Node child) {
		Node idNode = child.getAttributes().getNamedItem("id");
		if (idNode != null){
			return idNode.getNodeValue();
		} else if (ignoreNode(child)) {
			return null;
		} else {
			return CamelUtils.getTranslatedNodeName(child) + "-" + UUID.randomUUID().toString();
		}
	}
	
	private boolean ignoreNode(Node child) {
		return !isCamelNamespaceElement(child) && !isSupportedGlobalType(child);
	}
	
	private boolean isCamelNamespaceElement(Node child) {
		return startsWithNamespace(child, "http://camel.apache.org/schema/");
	}
	
	private boolean startsWithNamespace(Node child, String namespace) {
		return child.getNamespaceURI() != null && child.getNamespaceURI().startsWith(namespace);
	}
	
	private boolean isSupportedGlobalType(Node child) {
		// TODO: think about a better way to support storage without ID value
		return AbstractCamelModelElement.BEAN_NODE.equalsIgnoreCase(child.getNodeName());
	}
	
	/**
	 * 
	 * @param xmlString
	 */
	public CamelFile reloadModelFromXML(String xmlString) {
		// load the model
		try {
			CamelIOHandler ioHandler = new CamelIOHandler();
			return ioHandler.loadCamelModel(xmlString, new NullProgressMonitor(), this);
		} catch (Exception ex) {
			CamelModelServiceCoreActivator.pluginLog().logError("Unable to load Camel context file from String: \n" + xmlString, ex);
		}
		return null;
	}
	
	/**
	 * @return the globalDefinitions
	 */
	public Map<String, GlobalDefinitionCamelModelElement> getGlobalDefinitions() {
		return this.globalDefinitions;
	}
	
	/**
	 * @param globalDefinitions the globalDefinitions to set
	 */
	public void setGlobalDefinitions(Map<String, GlobalDefinitionCamelModelElement> globalDefinitions) {
		this.globalDefinitions = globalDefinitions;
	}
	
	/**
	 * adds the given global definition to the context 
	 * 
	 * @param id
	 * @param def
	 * @return the id used for adding the definition or null if not added
	 */
	public String addGlobalDefinition(String id, GlobalDefinitionCamelModelElement cme) {
		if (globalDefinitions.containsKey(id)) {
			return id;
		}
		String usedId = id != null ? id : "_def" + UUID.randomUUID().toString();
		if (globalDefinitions.containsKey(usedId) || id == null && globalDefinitions.containsValue(cme)) {
			return null;
		}
		globalDefinitions.put(usedId, cme);
		final Node parentNode = cme.getXmlNode().getParentNode();
		final Element documentElement = getDocument().getDocumentElement();
		if (parentNode == null || !parentNode.isEqualNode(documentElement)) {
			documentElement.insertBefore(cme.getXmlNode(), documentElement.getChildNodes().item(0));
			fireModelChanged();
		}
		return usedId;
	}
	
	public String updateGlobalDefinition(String id, GlobalDefinitionCamelModelElement cme) {
		String usedId = id != null ? id : "_def" + UUID.randomUUID().toString();
		Node oldDef = globalDefinitions.put(usedId, cme).getXmlNode();
		final Node parentNode = cme.getXmlNode().getParentNode();
		final Element documentElement = getDocument().getDocumentElement();
		if (parentNode == null || !parentNode.isEqualNode(documentElement)) {
			// to avoid the occasional org.w3c.dom.DOMException: WRONG_DOCUMENT_ERR: 
			// A node is used in a different document than the one that created it.
			Node imported = getDocument().importNode(cme.getXmlNode(), true);
			documentElement.replaceChild(imported, oldDef);
			fireModelChanged();
		}
		return usedId;
	}

	/**
	 * removes the global definition from context 
	 * 
	 * @param id
	 */
	public void removeGlobalDefinition(String id) {
		GlobalDefinitionCamelModelElement cmeToremove = globalDefinitions.remove(id);
		if (cmeToremove != null) {
			Node nodeToRemove = cmeToremove.getXmlNode();
			if (nodeToRemove != null) {
				getDocument().getDocumentElement().removeChild(nodeToRemove);
				fireModelChanged();
				notifyAboutDeletion(cmeToremove);
			}
		}
	}
	
	/**
	 * deletes all global definitions
	 */
	public void clearGlobalDefinitions() {
		this.globalDefinitions.clear();
	}
	
	/**
	 * @return the schemaType
	 */
	public CamelSchemaType getSchemaType() {
		return this.schemaType;
	}
	
	/**
	 * @param schemaType the schemaType to set
	 */
	public void setSchemaType(CamelSchemaType schemaType) {
		this.schemaType = schemaType;
	}
	
	/**
	 * @return the resource
	 */
	public IResource getResource() {
		return this.resource;
	}
	
	/**
	 * @param resource the resource to set
	 */
	public void setResource(IResource resource) {
		this.resource = resource;
	}
	
	/**
	 * @return the document
	 */
	public Document getDocument() {
		return this.document;
	}
	
	/**
	 * @param document the document to set
	 */
	public void setDocument(Document document) {
		this.document = document;
	}
	
	/**
	 * registers a dom listener to the document
	 */
	public void registerDOMListener() {
		if (this.document != null && this.document.getDocumentElement() instanceof EventTarget) {
			((EventTarget)this.document.getDocumentElement()).addEventListener("DOMSubtreeModified", this, true);
		}
	}
	
	/**
	 * unregisters the dom listener from the document
	 */
	public void unregisterDOMListener() {
		// unregister event listener on old document
		if (this.document != null && this.document.getDocumentElement() instanceof EventTarget) {
			((EventTarget)this.document.getDocumentElement()).removeEventListener("DOMSubtreeModified", this, true);
		}
	}
	
	/**
	 * checks whether this is a blueprint file or not
	 * 
	 * @return true if its a blueprint, false if no schema type detected or not blueprint
	 */
	public boolean isBlueprint() {
		return this.schemaType != null && schemaType.equals(CamelSchemaType.BLUEPRINT);
	}
	
	/**
	 * checks whether this is a blueprint file or not
	 * 
	 * @return true if its a blueprint, false if no schema type detected or not blueprint
	 */
	public boolean isSpring() {
		return this.schemaType != null && schemaType.equals(CamelSchemaType.SPRING);
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.model.CamelModelElement#getCamelFile()
	 */
	@Override
	public CamelFile getCamelFile() {
		return this;
	}
	
	/**
	 * returns true if there are neither childelements nor a Camel Context element
	 * 
	 * @return
	 */
	public boolean isEmpty() {
		return 	getRouteContainer() == null && 
				getChildElements().isEmpty();
	}
	
	/**
	 * returns the string representing the dom model
	 * 
	 * @return	the dom model as string or null on error
	 */
	public String getDocumentAsXML() {
		try {
			// taking line width and indentation size from xml / editor preferences of eclipse -> we always use spaces for indentation...
			int lineWidth = Integer.parseInt(XMLCorePlugin.getDefault().getPluginPreferences().getString("lineWidth"));
			int indentValue = XMLCorePlugin.getDefault().getPluginPreferences().getInt("indentationSize");
			String indentChar = XMLCorePlugin.getDefault().getPluginPreferences().getString("indentationChar");
			if (indentChar.equalsIgnoreCase("tab")) {
				// calculate tabWidth * indent
				int tabWidth = org.eclipse.ui.internal.editors.text.EditorsPlugin.getDefault().getPreferenceStore().getInt("tabWidth");
				indentValue = indentValue * tabWidth;
			}

			final Document document = getDocument();
			OutputFormat format = new OutputFormat(document);
			format.setIndenting(true);
			format.setIndent(indentValue);
			format.setEncoding("UTF-8");
			format.setPreserveEmptyAttributes(false);
			format.setMethod("xml");
			format.setPreserveSpace(false);
			format.setOmitComments(false);
			format.setOmitDocumentType(false);
			format.setOmitXMLDeclaration(false);
			format.setLineWidth(lineWidth);

			Writer out = new StringWriter();
			XMLSerializer serializer = new XMLSerializer(out, format);
			serializer.serialize(document);

			return out.toString();
		} catch (Exception ex) {
			CamelModelServiceCoreActivator.pluginLog().logError("Unable to save the camel file to " + getResource().getFullPath().toOSString(), ex);
		}
		return null;
	}
	
	/**
	 * adds a model listener
	 * 
	 * @param listener
	 */
	public void addModelListener(ICamelModelListener listener) {
		if (this.modelListeners.contains(listener) == false) {
			this.modelListeners.add(listener);
		}
	}
	
	/**
	 * removes a listener
	 * 
	 * @param listener
	 */
	public void removeModelListener(ICamelModelListener listener) {
		if (this.modelListeners.contains(listener)) {
			this.modelListeners.remove(listener);
		}
	}
	
	/**
	 * notifies all listeners that the model has been changed
	 */
	public void fireModelChanged() {
		for (ICamelModelListener listener : this.modelListeners) {
			if (listener != null) listener.modelChanged();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement#getRouteContainer()
	 */
	@Override
	public CamelRouteContainerElement getRouteContainer() {
		for (AbstractCamelModelElement e : getChildElements()) {
			String translatedNodeName = e.getTranslatedNodeName();
			if (CAMEL_CONTEXT.equalsIgnoreCase(translatedNodeName) || 
				CAMEL_ROUTES.equalsIgnoreCase(translatedNodeName)) {
				return (CamelRouteContainerElement) e;
			}
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.model.CamelModelElement#supportsBreakpoint()
	 */
	@Override
	public boolean supportsBreakpoint() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.w3c.dom.events.EventListener#handleEvent(org.w3c.dom.events.Event)
	 */
	@Override
	public void handleEvent(Event evt) {
		fireModelChanged();
	}
	
	public CamelModel getCamelModel(){
		return CamelModelFactory.getModelForProject(resource != null ? resource.getProject() : null);
	}

	@Override
	public List<AbstractCamelModelElement> findAllNodesWithId(String nodeId) {
		List<AbstractCamelModelElement> result = new ArrayList<>();

		if (getGlobalDefinitions() != null && !getGlobalDefinitions().isEmpty()) {
			for (AbstractCamelModelElement e : getGlobalDefinitions().values()) {
				if (e.getId() != null && e.getId().equals(nodeId)) {
					result.add(e);
				}
			}
		}
		List<AbstractCamelModelElement> superResult = super.findAllNodesWithId(nodeId);
		if (superResult != null && !superResult.isEmpty()) {
			result.addAll(superResult);
		}

		return result;
	}

}
