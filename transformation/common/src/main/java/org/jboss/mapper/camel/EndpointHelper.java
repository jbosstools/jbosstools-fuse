/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.jboss.mapper.camel;

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

    public static void setSourceModel(CamelEndpoint endpoint, String sourceModel) {
        replaceEndpointParameter(endpoint, SOURCE_MODEL, sourceModel);
    }

    public static void setTargetModel(CamelEndpoint endpoint, String targetModel) {
        replaceEndpointParameter(endpoint, TARGET_MODEL, targetModel);
    }

    public static void replaceEndpointParameter(CamelEndpoint endpoint, String key, String val) {

        StringBuilder uriStr = new StringBuilder(endpoint.getUri());
        if (uriStr.indexOf(key + "=") < 0) {
            throw new IllegalArgumentException("Endpoint does not contain parameter: " + key);
        }
        int startIdx = uriStr.indexOf(key);
        int endIdx = uriStr.indexOf("&", startIdx);
        if (endIdx < 0) {
            endIdx = uriStr.length();
        }
        uriStr.replace(startIdx, endIdx, key + "=" + val);
        endpoint.setUri(uriStr.toString());
    }
}
