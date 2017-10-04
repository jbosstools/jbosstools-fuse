package org.jboss.fuse.wsdl2rest;

import java.net.URL;
import java.util.List;

import javax.wsdl.WSDLException;

public interface WSDLProcessor {

    void process(URL wsdlURL) throws WSDLException;

    List<EndpointInfo> getClassDefinitions();
}
