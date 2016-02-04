/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.transformation.core.camel;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.fusesource.ide.camel.editor.utils.CamelUtils;
import org.fusesource.ide.camel.model.service.core.catalog.CamelModel;
import org.fusesource.ide.camel.model.service.core.catalog.CamelModelFactory;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.service.core.model.CamelEndpoint;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.camel.model.service.core.model.CamelModelElement;
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

	public enum MarshalType {MARSHALLER, UNMARSHALLER}
	
	private CamelFile model;
	
	/**
	 * 
	 */
	public CamelConfigBuilder(File file) {
		this.model = CamelUtils.getDiagramEditor().getModel();
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
    public CamelModelElement createDataFormat(TransformType type, String className, MarshalType marshalType) throws Exception {
        
    	CamelModelElement dataFormat = null;

        switch (type) {
            case JSON:
                dataFormat = MarshalType.UNMARSHALLER.equals(marshalType) ? createJsonDataFormat(className) : createJsonDataFormat(null);
                break;
            case XML:
                dataFormat = createJaxbDataFormat(getPackage(className));
                break;
            case OTHER:
            case JAVA:
                dataFormat = null;
                break;
            default:
                throw new Exception("Unsupported data format type: " + type);
        }

        return dataFormat;
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
    public CamelModelElement createEndpoint(String transformId, String dozerConfigPath, String sourceClass, String targetClass, CamelModelElement unmarshaller, CamelModelElement marshaller) {
        String unmarshallerId = unmarshaller != null ? unmarshaller.getId() : null;
        String marshallerId = marshaller != null ? marshaller.getId() : null;
        String endpointUri = EndpointHelper.createEndpointUri(dozerConfigPath,
                transformId, sourceClass, targetClass, unmarshallerId, marshallerId);
        return addEndpoint(transformId, endpointUri);
    }

    /**
     * Add a transformation to the Camel configuration. This method adds all
     * required data formats, Dozer configuration, and the camel-transform
     * endpoint definition to the Camel config.
     *
     * @param transformId id for the transformation
     * @param dozerConfigPath path to Dozer config for transformation
     * @param source type of the source data
     * @param sourceClass name of the source model class
     * @param target type of the target data
     * @param targetClass name of the target model class
     * @throws Exception failed to create transformation
     */
    public void addTransformation(String transformId, String dozerConfigPath,
            TransformType source, String sourceClass,
            TransformType target, String targetClass) throws Exception {

        // Add data formats
    	CamelModelElement unmarshaller = createDataFormat(source, sourceClass, MarshalType.UNMARSHALLER);
    	CamelModelElement marshaller = createDataFormat(target, targetClass, MarshalType.MARSHALLER);

        // Create a transformation endpoint
        String unmarshallerId = unmarshaller != null ? unmarshaller.getId() : null;
        String marshallerId = marshaller != null ? marshaller.getId() : null;
        String endpointUri = EndpointHelper.createEndpointUri(dozerConfigPath, transformId, sourceClass, targetClass, unmarshallerId, marshallerId);
        addEndpoint(transformId, endpointUri);
    }

    public CamelModelElement getEndpoint(String endpointId) {
    	CamelModelElement endpoint = null;
        for (CamelModelElement ep : getEndpoints()) {
            if (endpointId.equals(ep.getId())) {
                endpoint = ep;
                break;
            }
        }
        return endpoint;
    }

    public List<String> getTransformEndpointIds() {
        List<String> endpointIds = new LinkedList<String>();
        for (CamelModelElement ep : getEndpoints()) {
            if (((String)ep.getParameter("uri")).startsWith(EndpointHelper.DOZER_SCHEME)) {
                endpointIds.add(ep.getId());
            }
        }
        return endpointIds;
    }

    public CamelModelElement getDataFormat(String id) {
    	CamelModelElement dataFormat = null;
        for (CamelModelElement df : getDataFormats()) {
            if (id.equals(df.getId())) {
                dataFormat = df;
                break;
            }
        }
        return dataFormat;
    }

    public Collection<CamelModelElement> getDataFormats() {
    	return model.getCamelContext().getDataformats().values();
    }

    public Collection<CamelModelElement> getEndpoints() {
    	return model.getCamelContext().getEndpointDefinitions().values();
    }

    protected CamelModelElement addEndpoint(String id, String uri) {
    	CamelModelElement parent = model.getCamelContext();
		CamelEndpoint ep = new CamelEndpoint(uri);
		ep.setId(id);
		ep.setParent(parent);
		ep.setUnderlyingMetaModelObject(getEipByName("from"));
		model.getCamelContext().addEndpointDefinition(ep);
		return ep;
    }

    protected String getPackage(String type) {
        int idx = type.lastIndexOf('.');
        return idx > 0 ? type.substring(0, idx) : type;
    }

    protected CamelModelElement createJsonDataFormat(String className) throws Exception {
        final String id = className != null ? className.replaceAll("\\.", "") : "transform-json";
        Eip json = getEipByName("json");
        CamelModelElement dataFormat = getDataFormat(id);
        if (dataFormat == null) {
            // Looks like we need to create a new one
        	CamelModelElement parent = model.getCamelContext();
    		dataFormat = new CamelModelElement(parent, null);
    		dataFormat.setId(id);
    		dataFormat.setUnderlyingMetaModelObject(json);
    		dataFormat.setParameter("library", "Jackson");
    		dataFormat.setParameter("unmarshalTypeName", className);
    		parent.getCamelContext().addDataFormat(dataFormat);
        }
        return dataFormat;
    }


    protected CamelModelElement createJaxbDataFormat(String contextPath) throws Exception {
        final String id = contextPath.replaceAll("\\.", "");
        Eip jaxb = getEipByName("jaxb");
        CamelModelElement dataFormat = getDataFormat(id);
        if (dataFormat == null) {
            // Looks like we need to create a new one
        	CamelModelElement parent = model.getCamelContext();
    		dataFormat = new CamelModelElement(parent, null);
    		dataFormat.setId(id);
    		dataFormat.setUnderlyingMetaModelObject(jaxb);
    		dataFormat.setParameter("contextPath", contextPath);
    		parent.getCamelContext().addDataFormat(dataFormat);
        }
        return dataFormat;
    }
    
	/**
	 * retrieves the eip meta model for a given eip name
	 * 
	 * @param name
	 * @return	the eip or null if not found
	 */
	public Eip getEipByName(String name) {
		String camelVersion = CamelModelFactory.getCamelVersion(model.getResource().getProject());
		if (camelVersion == null) {
			camelVersion = CamelModelFactory.getLatestCamelVersion();
		}
		// then get the meta model for the given camel version
		CamelModel model = CamelModelFactory.getModelForVersion(camelVersion);
		if (model == null) {
			return null;
		}
		// then we get the eip meta model
		Eip eip = model.getEipModel().getEIPByName(name);
		// and return it
		return eip;
	}
}
