/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.transformation.core.camel;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.fusesource.ide.camel.editor.utils.CamelUtils;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelCatalogCacheManager;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelModel;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.service.core.io.CamelIOHandler;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelBasicModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelContextElement;
import org.fusesource.ide.camel.model.service.core.model.CamelEndpoint;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.jboss.tools.fuse.transformation.core.TransformType;

/**
 * CamelConfigBuilder provides read/write access to Camel configuration used in
 * a data transformation project. This class assumes that all Camel
 * configuration is stored in a Spring application context. Any changes to Camel
 * configuration through direct methods on this class or the underlying
 * CamelContextFactoryBean config model are in-memory only and not persisted
 * until saveConfig() is called.
 */
public class CamelConfigBuilder {

	public enum MarshalType {
		MARSHALLER, UNMARSHALLER
	}

	private CamelFile model;

	/**
	 * Load CamelFile from Diagram Editor.
	 */
	public CamelConfigBuilder() {
		this.model = null;
	}

	/*
	 * used for test only
	 */
	public CamelConfigBuilder(File file) {
		this.model = new CamelIOHandler().loadCamelModel(file, new NullProgressMonitor());
	}

	public CamelFile getModel() {
		if (this.model != null)
			return this.model;
		return CamelUtils.getDiagramEditor().getModel();
	}

	/**
	 * creates a dataformat element
	 * 
	 * @param type
	 * @param className
	 * @param marshalType
	 * @return
	 * @throws Exception
	 */
	public AbstractCamelModelElement createDataFormat(TransformType type, String className, MarshalType marshalType)
			throws Exception {
		switch (type) {
		case JSON:
			return MarshalType.UNMARSHALLER.equals(marshalType) ? createJsonDataFormat(className)
					: createJsonDataFormat(null);
		case XML:
			return createJaxbDataFormat(getPackage(className));
		case OTHER:
		case JAVA:
			return null;
		default:
			throw new Exception("Unsupported data format type: " + type); //$NON-NLS-1$
		}
	}

	/**
	 * creates an endpoint
	 * 
	 * @param transformId
	 * @param dozerConfigPath
	 * @param sourceClass
	 * @param targetClass
	 * @param unmarshaller
	 * @param marshaller
	 * @return
	 */
	public AbstractCamelModelElement createEndpoint(String transformId, String dozerConfigPath, String sourceClass,
			String targetClass, AbstractCamelModelElement unmarshaller, AbstractCamelModelElement marshaller) {
		String unmarshallerId = unmarshaller != null ? unmarshaller.getId() : null;
		String marshallerId = marshaller != null ? marshaller.getId() : null;
		String endpointUri = EndpointHelper.createEndpointUri(dozerConfigPath, transformId, sourceClass, targetClass,
				unmarshallerId, marshallerId);
		return addEndpoint(transformId, endpointUri);
	}

	/**
	 * Add a transformation to the Camel configuration. This method adds all
	 * required data formats, Dozer configuration, and the camel-transform
	 * endpoint definition to the Camel config.
	 *
	 * @param transformId
	 *            id for the transformation
	 * @param dozerConfigPath
	 *            path to Dozer config for transformation
	 * @param source
	 *            type of the source data
	 * @param sourceClass
	 *            name of the source model class
	 * @param target
	 *            type of the target data
	 * @param targetClass
	 *            name of the target model class
	 * @throws Exception
	 *             failed to create transformation
	 */
	public void addTransformation(String transformId, String dozerConfigPath, TransformType source, String sourceClass,
			TransformType target, String targetClass) throws Exception {

		// Add data formats
		AbstractCamelModelElement unmarshaller = createDataFormat(source, sourceClass, MarshalType.UNMARSHALLER);
		AbstractCamelModelElement marshaller = createDataFormat(target, targetClass, MarshalType.MARSHALLER);

		// Create a transformation endpoint
		String unmarshallerId = unmarshaller != null ? unmarshaller.getId() : null;
		String marshallerId = marshaller != null ? marshaller.getId() : null;
		String endpointUri = EndpointHelper.createEndpointUri(dozerConfigPath, transformId, sourceClass, targetClass,
				unmarshallerId, marshallerId);
		addEndpoint(transformId, endpointUri);
	}

	public AbstractCamelModelElement getEndpoint(String endpointId) {
		AbstractCamelModelElement endpoint = null;
		for (AbstractCamelModelElement ep : getEndpoints()) {
			if (endpointId.equals(ep.getId())) {
				endpoint = ep;
				break;
			}
		}
		return endpoint;
	}

	public List<String> getTransformEndpointIds() {
		List<String> endpointIds = new LinkedList<String>();
		for (AbstractCamelModelElement ep : getEndpoints()) {
			if (((String) ep.getParameter("uri")).startsWith(EndpointHelper.DOZER_SCHEME)) { //$NON-NLS-1$
				endpointIds.add(ep.getId());
			}
		}
		return endpointIds;
	}

	public AbstractCamelModelElement getDataFormat(String id) {
		AbstractCamelModelElement dataFormat = null;
		for (AbstractCamelModelElement df : getDataFormats()) {
			if (id.equals(df.getId())) {
				dataFormat = df;
				break;
			}
		}
		return dataFormat;
	}

	public Collection<AbstractCamelModelElement> getDataFormats() {
		if (getModel().getRouteContainer() instanceof CamelContextElement) {
			return ((CamelContextElement) getModel().getRouteContainer()).getDataformats().values();
		}
		return Collections.emptyList();
	}

	public Collection<AbstractCamelModelElement> getEndpoints() {
		if (getModel().getRouteContainer() instanceof CamelContextElement) {
			return ((CamelContextElement) getModel().getRouteContainer()).getEndpointDefinitions().values();
		}
		return Collections.emptyList();
	}

	protected AbstractCamelModelElement addEndpoint(String id, String uri) {
		AbstractCamelModelElement parent = getModel().getRouteContainer();
		if (parent instanceof CamelContextElement) {
			CamelEndpoint ep = new CamelEndpoint(uri);
			ep.setId(id);
			ep.setParent(parent);
			ep.setUnderlyingMetaModelObject(getEipByName("from")); //$NON-NLS-1$
			((CamelContextElement) parent).addEndpointDefinition(ep);
			return ep;
		}
		return null; // maybe better throw illegaloperationexception?
	}

	protected String getPackage(String type) {
		int idx = type.lastIndexOf('.');
		return idx > 0 ? type.substring(0, idx) : type;
	}

	protected AbstractCamelModelElement createJsonDataFormat(String className) throws Exception {
		final String id = className != null ? className.replaceAll("\\.", "") : "transform-json"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		Eip json = getEipByName("json"); //$NON-NLS-1$
		AbstractCamelModelElement dataFormat = getDataFormat(id);
		if (dataFormat == null) {
			// Looks like we need to create a new one
			AbstractCamelModelElement parent = getModel().getRouteContainer();
			if (parent instanceof CamelContextElement) {
				dataFormat = new CamelBasicModelElement(parent, null);
				dataFormat.setId(id);
				dataFormat.setUnderlyingMetaModelObject(json);
				dataFormat.setParameter("library", "Jackson"); //$NON-NLS-1$ //$NON-NLS-2$
				dataFormat.setParameter("unmarshalTypeName", className); //$NON-NLS-1$
				((CamelContextElement) parent).addDataFormat(dataFormat);
			}
		}
		return dataFormat;
	}

	protected AbstractCamelModelElement createJaxbDataFormat(String contextPath) throws Exception {
		final String id = contextPath.replaceAll("\\.", ""); //$NON-NLS-1$ //$NON-NLS-2$
		Eip jaxb = getEipByName("jaxb"); //$NON-NLS-1$
		AbstractCamelModelElement dataFormat = getDataFormat(id);
		if (dataFormat == null) {
			// Looks like we need to create a new one
			AbstractCamelModelElement parent = getModel().getRouteContainer();
			if (parent instanceof CamelContextElement) {
				dataFormat = new CamelBasicModelElement(parent, null);
				dataFormat.setId(id);
				dataFormat.setUnderlyingMetaModelObject(jaxb);
				dataFormat.setParameter("contextPath", contextPath); //$NON-NLS-1$
				((CamelContextElement) parent).addDataFormat(dataFormat);
			}
		}
		return dataFormat;
	}

	/**
	 * retrieves the eip meta model for a given eip name
	 * 
	 * @param name
	 * @return the eip or null if not found
	 */
	public Eip getEipByName(String name) {
		IResource resource = getModel().getResource();
		// then get the meta model for the given camel version
		CamelModel model = CamelCatalogCacheManager.getInstance().getCamelModelForProject(resource != null ? resource.getProject() : null);
		if (model == null) {
			return null;
		}
		// then we get the eip meta model
		Eip eip = model.getEip(name);
		// and return it
		return eip;
	}
}
