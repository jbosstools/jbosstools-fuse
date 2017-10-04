package org.jboss.fuse.wsdl2rest.impl.service;

import java.util.ArrayList;
import java.util.List;

import org.jboss.fuse.wsdl2rest.MethodInfo;
import org.jboss.fuse.wsdl2rest.ParamInfo;

public class MethodInfoImpl extends MetaInfoImpl implements MethodInfo {

    private String style;
    private String returnType;
    private String methodName;
    private List<ParamInfo> params = new ArrayList<>();
    private String exceptionType;
    private String httpMethod;

    public MethodInfoImpl(String methodName) {
        this.methodName = methodName;
    }

    @Override
    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    @Override
    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    @Override
    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    @Override
    public List<ParamInfo> getParams() {
        return params;
    }

    public void setParams(List<ParamInfo> params) {
        this.params = params;
    }

    @Override
    public String getExceptionType() {
        return exceptionType;
    }

    public void setExceptionType(String exceptionType) {
        this.exceptionType = exceptionType;
    }

    @Override
    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    @Override
    public String getPath() {
        String result = null;
        List<String> resources = getResources();
        if (getPreferredResource() != null) {
            resources = new ArrayList<String>();
            resources.add(getPreferredResource());
        }
        if (resources != null) {
            int loc = resources.size() >= 2 ? 1 : 0;
            StringBuilder path = new StringBuilder();
            for (int i = loc; i < resources.size(); i++) {
                path.append(resources.get(i));
            }
            result = path.toString().toLowerCase();

        }
        if (result != null && getParams().size() > 0) {
            ParamInfo pinfo = getParams().get(0);
            if (hasPathParam(pinfo)) {
                result += "/{" + pinfo.getParamName() + "}";
            }
        }
        return result;
    }

    private boolean hasPathParam(ParamInfo pinfo) {
        String httpMethod = getHttpMethod();
        boolean pathParam = httpMethod.equals("GET") || httpMethod.equals("DELETE");
        return pathParam && pinfo.getParamType() != null;
    }

    @Override
    public String toString() {
        return methodName + "(): " + returnType;
    }

}
