package org.jboss.fuse.wsdl2rest.impl;

import java.util.List;
import java.util.Arrays;
import java.util.Collections;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.jboss.fuse.wsdl2rest.EndpointInfo;
import org.jboss.fuse.wsdl2rest.MethodInfo;
import org.jboss.fuse.wsdl2rest.ParamInfo;
import org.jboss.fuse.wsdl2rest.ResourceMapper;
import org.jboss.fuse.wsdl2rest.impl.service.MethodInfoImpl;
import org.jboss.fuse.wsdl2rest.impl.service.ParamImpl;

import java.util.regex.Matcher;

public class ResourceMapperImpl implements ResourceMapper {

    private List<String> resources;
    private String httpMethod;

    private String httpGetWords = "[Gg]et|[Rr]ead|[Ff]etch|[Ll]ist";
    private String httpPostWords = "[Pp]ost|[Aa]dd|[Cc]reate";
    private String httpPutWords = "[Pp]ut|[Ss]et|[Uu]pd|[Mm]od";
    private String httpDelWords = "[Dd]el|[Rr]em";

    private Pattern httpGetWordsPattern = Pattern.compile(httpGetWords);
    private Pattern httpPostWordsPattern = Pattern.compile(httpPostWords);
    private Pattern httpDeleteWordsPattern = Pattern.compile(httpDelWords);
    private Pattern httpPutWordsPattern = Pattern.compile(httpPutWords);
    private Pattern resourcePattern = Pattern.compile("[a-z]+|([A-Z][a-z]+)*");

    public List<String> getResources() {
        return Collections.unmodifiableList(resources);
    }

    // This method will iterate through Class, Method and Parameter definitions
    // and assign respective resources to them
    public void assignResources(List<EndpointInfo> svcClasses) {
        for (EndpointInfo clazzDef : svcClasses) {
            // Don't break up class name
            if (clazzDef.getClassName() != null) {
                clazzDef.setResources(Arrays.asList(clazzDef.getClassName()));
                for (MethodInfo m : clazzDef.getMethods()) {
                    MethodInfoImpl minfo = (MethodInfoImpl) m;
                    if (minfo.getMethodName() != null) {
                        // Parse the method name
                        resources = new ArrayList<String>();
                        mapResources(minfo.getMethodName());
                        minfo.setResources(resources);
                        minfo.setHttpMethod(httpMethod);

                        if (minfo.getParams() != null) {
                            for (ParamInfo p : minfo.getParams()) {
                                ParamImpl param = (ParamImpl) p;
                                if (param.getParamName() != null)
                                    param.setResources(Arrays.asList(param.getParamName()));
                            }
                        }
                    }
                }
            }
        }
    }

    private void addResource(String resouce) {
        if (!resourceExists(resouce)) {
            this.resources.add(resouce);
        }
    }

    private boolean resourceExists(String resource) {
        for (String str : this.resources) {
            if (str.equals(resource)) {
                return true;
            }
        }
        return false;
    }

    private void mapResources(String resourceName) {
        Matcher resourceMatcher = resourcePattern.matcher(resourceName);
        Matcher httpMethodMatcher;
        boolean foundHttpMethod = false;

        while (resourceMatcher.find()) {
            if (!resourceMatcher.group().equals("")) {
                addResource(resourceMatcher.group());
                if (!foundHttpMethod) {
                    httpMethodMatcher = httpGetWordsPattern.matcher(resourceMatcher.group());
                    if (httpMethodMatcher.find() && !httpMethodMatcher.group().equals("")) {
                        httpMethod = "GET";
                        foundHttpMethod = true;
                        continue;
                    }
                    httpMethodMatcher.usePattern(httpPostWordsPattern);
                    if (httpMethodMatcher.find() && !httpMethodMatcher.group().equals("")) {
                        httpMethod = "POST";
                        foundHttpMethod = true;
                        continue;
                    }
                    httpMethodMatcher.usePattern(httpDeleteWordsPattern);
                    if (httpMethodMatcher.find() && !httpMethodMatcher.group().equals("")) {
                        httpMethod = "DELETE";
                        foundHttpMethod = true;
                        continue;
                    }
                    httpMethodMatcher.usePattern(httpPutWordsPattern);
                    if (httpMethodMatcher.find() && !httpMethodMatcher.group().equals("")) {
                        httpMethod = "PUT";
                        foundHttpMethod = true;
                        continue;
                    }

                    // Set default http method as GET
                    if (httpMethod == null)
                        this.httpMethod = "GET";
                }
            }
        }
    }

    public String toString() {
        return resources.toString();
    }

}
