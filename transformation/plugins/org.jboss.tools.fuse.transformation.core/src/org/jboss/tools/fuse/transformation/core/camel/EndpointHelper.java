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

import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;

/**
 * Utility methods that help with constructing or modifying a transformation
 * endpoint.
 */
public final class EndpointHelper {

    public static final String DOZER_SCHEME = "dozer"; //$NON-NLS-1$
    private static final String SOURCE_MODEL = "sourceModel"; //$NON-NLS-1$
    private static final String TARGET_MODEL = "targetModel"; //$NON-NLS-1$
    private static final String MARSHAL_ID = "marshalId"; //$NON-NLS-1$
    private static final String UNMARSHAL_ID = "unmarshalId"; //$NON-NLS-1$
    public static final String MAPPING_FILE = "mappingFile"; //$NON-NLS-1$

    public static String createEndpointUri(
            String dozerConfigPath,
            String transformId,
            String sourceClass,
            String targetClass,
            String unmarshallerId,
            String marshallerId) {

    	StringBuilder uriBuf = new StringBuilder(DOZER_SCHEME + ":" + transformId + "?"); //$NON-NLS-1$ //$NON-NLS-2$
        uriBuf.append(SOURCE_MODEL + "=" + sourceClass); //$NON-NLS-1$
        uriBuf.append("&" + TARGET_MODEL + "=" + targetClass); //$NON-NLS-1$ //$NON-NLS-2$
        if (marshallerId != null) {
            uriBuf.append("&" + MARSHAL_ID + "=" + marshallerId); //$NON-NLS-1$ //$NON-NLS-2$
        }
        if (unmarshallerId != null) {
            uriBuf.append("&" + UNMARSHAL_ID + "=" + unmarshallerId); //$NON-NLS-1$ //$NON-NLS-2$
        }
        if (dozerConfigPath != null) {
            uriBuf.append("&" + MAPPING_FILE + "=" + dozerConfigPath); //$NON-NLS-1$ //$NON-NLS-2$
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

        StringBuilder uriStr = new StringBuilder((String)endpoint.getParameter("uri")); //$NON-NLS-1$
        if (uriStr.indexOf(key + "=") < 0) { //$NON-NLS-1$
            throw new IllegalArgumentException("Endpoint does not contain parameter: " + key); //$NON-NLS-1$
        }
        int startIdx = uriStr.indexOf(key);
        int endIdx = uriStr.indexOf("&", startIdx); //$NON-NLS-1$
        if (endIdx < 0) {
            endIdx = uriStr.length();
        }
        uriStr.replace(startIdx, endIdx, key + "=" + val); //$NON-NLS-1$
        endpoint.setParameter("uri", uriStr.toString()); //$NON-NLS-1$
    }
}
