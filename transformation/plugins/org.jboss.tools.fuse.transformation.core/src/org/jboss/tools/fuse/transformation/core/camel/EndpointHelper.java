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

import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;

/**
 * Utility methods that help with constructing or modifying a transformation
 * endpoint.
 */
public final class EndpointHelper {

    public static String DOZER_SCHEME = "dozer";
    public static String SOURCE_MODEL = "sourceModel";
    public static String TARGET_MODEL = "targetModel";
    public static String MARSHAL_ID = "marshalId";
    public static String UNMARSHAL_ID = "unmarshalId";
    public static String MAPPING_FILE = "mappingFile";

    public static String createEndpointUri(
            String dozerConfigPath,
            String transformId,
            String sourceClass,
            String targetClass,
            String unmarshallerId,
            String marshallerId) {

        StringBuffer uriBuf = new StringBuffer(DOZER_SCHEME + ":" + transformId + "?");
        uriBuf.append(SOURCE_MODEL + "=" + sourceClass);
        uriBuf.append("&" + TARGET_MODEL + "=" + targetClass);
        if (marshallerId != null) {
            uriBuf.append("&" + MARSHAL_ID + "=" + marshallerId);
        }
        if (unmarshallerId != null) {
            uriBuf.append("&" + UNMARSHAL_ID + "=" + unmarshallerId);
        }
        if (dozerConfigPath != null) {
            uriBuf.append("&" + MAPPING_FILE + "=" + dozerConfigPath);
        }

        return uriBuf.toString();
    }

    public static void setSourceModel(AbstractCamelModelElement endpoint, String sourceModel) {
        replaceEndpointParameter(endpoint, SOURCE_MODEL, sourceModel);
    }

    public static void setTargetModel(AbstractCamelModelElement endpoint, String targetModel) {
        replaceEndpointParameter(endpoint, TARGET_MODEL, targetModel);
    }

    public static void replaceEndpointParameter(AbstractCamelModelElement endpoint, String key, String val) {

        StringBuilder uriStr = new StringBuilder((String)endpoint.getParameter("uri"));
        if (uriStr.indexOf(key + "=") < 0) {
            throw new IllegalArgumentException("Endpoint does not contain parameter: " + key);
        }
        int startIdx = uriStr.indexOf(key);
        int endIdx = uriStr.indexOf("&", startIdx);
        if (endIdx < 0) {
            endIdx = uriStr.length();
        }
        uriStr.replace(startIdx, endIdx, key + "=" + val);
        endpoint.setParameter("uri", uriStr.toString());
    }
}
