package org.jboss.fuse.wsdl2rest.impl.service;

import org.jboss.fuse.wsdl2rest.ParamInfo;

public class ParamImpl extends MetaInfoImpl implements ParamInfo {

    private final String paramName;
    private final String paramType;

    public ParamImpl(String paramName, String paramType) {
        this.paramType = paramType;
        this.paramName = paramName;
    }

    public String getParamType() {
        return paramType;
    }

    public String getParamName() {
        return paramName;
    }
}
