package org.jboss.fuse.wsdl2rest.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Fault;
import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.schema.Schema;
import javax.wsdl.extensions.soap.SOAPOperation;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;

import org.jboss.fuse.wsdl2rest.EndpointInfo;
import org.jboss.fuse.wsdl2rest.ParamInfo;
import org.jboss.fuse.wsdl2rest.WSDLProcessor;
import org.jboss.fuse.wsdl2rest.impl.service.ClassDefinitionImpl;
import org.jboss.fuse.wsdl2rest.impl.service.MethodInfoImpl;
import org.jboss.fuse.wsdl2rest.impl.service.ParamImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Following class process WSDL document and generate a list of service interfaces & methods
 * It follows  JSR-101 specifications
 */
public class WSDLProcessorImpl implements WSDLProcessor {

    private static Logger log = LoggerFactory.getLogger(WSDLProcessorImpl.class);


    private static final String xsdURI = "http://www.w3.org/2001/XMLSchema";
    private static final String jaxbArrayURI = "http://jaxb.dev.java.net/array";

    private Map<QName, String> typeRegistry = new HashMap<>(); 
    private Map<QName, EndpointInfo> portTypeMap = new LinkedHashMap<>();

    /*
     * JAXB XML Schema binding
     * https://docs.oracle.com/javase/tutorial/jaxb/intro/bind.html
     */
    public WSDLProcessorImpl() {
        typeRegistry.put(new QName(xsdURI, "anyURI"), URI.class.getName());
        typeRegistry.put(new QName(xsdURI, "base64Binary"), "byte[]");
        typeRegistry.put(new QName(xsdURI, "boolean"), "boolean");
        typeRegistry.put(new QName(xsdURI, "byte"), "byte");
        typeRegistry.put(new QName(xsdURI, "date"), Date.class.getName());
        typeRegistry.put(new QName(xsdURI, "dateTime"), Date.class.getName());
        typeRegistry.put(new QName(xsdURI, "decimal"), BigDecimal.class.getName());
        typeRegistry.put(new QName(xsdURI, "double"), "double");
        typeRegistry.put(new QName(xsdURI, "float"), "float");
        typeRegistry.put(new QName(xsdURI, "hexBinary"), "byte[]");
        typeRegistry.put(new QName(xsdURI, "int"), "int");
        typeRegistry.put(new QName(xsdURI, "integer"), BigInteger.class.getName());
        typeRegistry.put(new QName(xsdURI, "long"), "long");
        typeRegistry.put(new QName(xsdURI, "short"), "short");
        typeRegistry.put(new QName(xsdURI, "string"), String.class.getName());
        typeRegistry.put(new QName(xsdURI, "time"), Date.class.getName());
        typeRegistry.put(new QName(xsdURI, "unsignedByte"), "short");
        typeRegistry.put(new QName(xsdURI, "unsignedInt"), "long");
        typeRegistry.put(new QName(xsdURI, "unsignedShort"), "int");
        typeRegistry.put(new QName(xsdURI, "QName"), QName.class.getName());
        
        typeRegistry.put(new QName(jaxbArrayURI, "intArray"), "int[]");
    }

    public void process(URL wsdlURL) throws WSDLException {

        WSDLFactory factory = WSDLFactory.newInstance();
        WSDLReader reader = factory.newWSDLReader();
        reader.setFeature("javax.wsdl.verbose", true);
        reader.setFeature("javax.wsdl.importDocuments", true);
        Definition def = reader.readWSDL(null, wsdlURL.toString());

        processSchemaTypes(def);
        processServices(def);
    }

    public List<EndpointInfo> getClassDefinitions() {
        List<EndpointInfo> result = new ArrayList<>(portTypeMap.values());
        return Collections.unmodifiableList(result);
    }

    @SuppressWarnings("unchecked")
    private void processSchemaTypes(Definition def) {
        for (ExtensibilityElement exel : (List<ExtensibilityElement>) def.getExtensibilityElements()) {
            if (exel instanceof Schema) {
                throw new UnsupportedOperationException("Schema types not supported");
            }
        }
    }

    private void processBinding(Definition def, Binding binding) {
        QName qname = binding.getPortType().getQName();
        log.info("\tBinding: {}", qname.getLocalPart());
        processPortType(def, binding, binding.getPortType());
    }

    /* WSDL 1.1 spec: 2.4.5 Names of Elements within an Operation
     * If the name attribute is not specified on a one-way or notification message, it defaults to the name of the operation.
     * If the name attribute is not specified on the input or output messages of a request-response or solicit-response operation,
     * the name defaults to the name of the operation with "Request"/"Solicit" or "Response" appended, respectively.
     * Each fault element must be named to allow a binding to specify the concrete format of the fault message.
     * The name of the fault element is unique within the set of faults defined for the operation.
     */
    @SuppressWarnings("unchecked")
    private void processPortType(Definition def, Binding binding, PortType portType) {

        QName qname = portType.getQName();
        log.info("\tPortType: {}", qname.getLocalPart());

        ClassDefinitionImpl clazzDef = new ClassDefinitionImpl();
        clazzDef.setPackageName(toPackageName(qname.getNamespaceURI()));
        clazzDef.setClassName(qname.getLocalPart());
        portTypeMap.put(qname, clazzDef);
        
        log.info("\tOperations: ");
        for (Operation op : (List<Operation>) portType.getOperations()) {
            String opName = op.getName();
            log.info("\t\tOperation: {}", opName);

            ClassDefinitionImpl svcDef = (ClassDefinitionImpl) portTypeMap.get(portType.getQName());
            MethodInfoImpl methodInf = new MethodInfoImpl(opName);
            BindingOperation bop = binding.getBindingOperation(opName, null, null);
            for (Object aux : bop.getExtensibilityElements()) {
                if (aux instanceof SOAPOperation) {
                    SOAPOperation soap = (SOAPOperation) aux;
                    methodInf.setStyle(soap.getStyle());
                }
            }
            svcDef.addMethod(methodInf);
            Input in = op.getInput();
            Output out = op.getOutput();
            Map<QName, Fault> f = op.getFaults();

            log.info("\t\t\tInput: ");
            if (in != null) {
                if (in.getName() == null) {
                    in.setName(opName);
                }
                processMessages(def, portType, in.getMessage(), opName, 0);
            }

            log.info("\t\t\tOutput: ");
            if (out != null) {
                if (out.getName() == null) {
                    out.setName(opName + "Response");
                }
                processMessages(def, portType, out.getMessage(), opName, 1);
            }

            log.info("\t\t\tFaults: ");
            if (f != null) {
                for (Object o : f.values()) {
                    Fault fault = (Fault) o;
                    processMessages(def, portType, fault.getMessage(), opName, 2);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void processMessages(Definition def, PortType portType, Message message, String opname, int type) {
        log.info("\t\t\tMessage: {}", message.getQName().getLocalPart());
        if (!message.isUndefined() && message.getParts() != null) {
            List<Part> parts = message.getOrderedParts(null);
            List<String> imports = new ArrayList<String>();
            List<ParamInfo> params = new ArrayList<>();
            int index = 0;
            for (Part part : parts) {
                QName elmtQName = part.getElementName();
                if (elmtQName == null) {
                    elmtQName = new QName(part.getName());
                }
                log.info("\t\t\tPart: {}:{}", elmtQName.getPrefix(), elmtQName.getLocalPart());
                QName typeQName = part.getTypeName();
                String javaType = typeRegistry.get(typeQName);
                if (javaType == null) {
                    javaType = toJavaType(typeQName != null ? typeQName : elmtQName);
                }
                if (javaType.startsWith("java.lang.")) {
                    javaType = javaType.substring(10);
                }
                log.info("\t\t\t\tParams: {} {}", javaType, elmtQName);
                params.add(new ParamImpl("arg" + index++, javaType));
            }
            if (parts.size() > 0) {
                ClassDefinitionImpl svcDef = (ClassDefinitionImpl) portTypeMap.get(portType.getQName());
                MethodInfoImpl minfo = (MethodInfoImpl) svcDef.getMethod(opname);
                switch (type) {
                    case 0:
                        // no params on doclit methods that list stuff
                        if (!("document".equals(minfo.getStyle()) && opname.toLowerCase().startsWith("list"))) {
                            minfo.setParams(params);
                        }
                        break;
                    case 1:
                        minfo.setReturnType(params.get(0).getParamType());
                        break;
                    case 2:
                        minfo.setExceptionType(params.get(0).getParamType());
                }
                svcDef.setImports(imports);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void processServices(Definition def) {
        Map<QName, Service> services = def.getServices();
        log.info("Services: ");
        for (Service svc : services.values()) {
            String svcName = svc.getQName().getLocalPart();
            log.info("\t{}", svcName);
            Map<QName, Port> ports = svc.getPorts();
            for (Port port : ports.values()) {
                log.info("\tPort: {}", port.getName());
                processBinding(def, port.getBinding());
            }
        }
    }

    static String toPackageName(String nsuri) {
        if (nsuri.startsWith("http://")) {
            nsuri = nsuri.substring(7);
            nsuri = nsuri.endsWith("/") ? nsuri.substring(0, nsuri.length() - 1) : nsuri;
            StringBuffer buffer = new StringBuffer();
            for (String tok : nsuri.split("\\.")) {
                buffer.insert(0, tok + ".");
            }
            return buffer.substring(0, buffer.length() - 1);
        } else {
            return "";
        }
    }
    
    static String toJavaType(QName qname) {
        String lpart = qname.getLocalPart();
        String pname = toPackageName(qname.getNamespaceURI());
        return pname + "." + lpart.substring(0, 1).toUpperCase() + lpart.substring(1);
    }
}
