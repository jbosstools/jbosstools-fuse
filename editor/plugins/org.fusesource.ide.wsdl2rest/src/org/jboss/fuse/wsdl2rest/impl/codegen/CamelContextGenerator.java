package org.jboss.fuse.wsdl2rest.impl.codegen;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.jboss.fuse.wsdl2rest.EndpointInfo;
import org.jboss.fuse.wsdl2rest.util.IllegalArgumentAssertion;
import org.jboss.fuse.wsdl2rest.util.IllegalStateAssertion;

public class CamelContextGenerator {

    private final Path outpath;
    
    private Path targetContext;
    private URL targetAddress;
    private String targetBean;

    public CamelContextGenerator(Path outpath) {
        this.outpath = outpath;
    }

    public void setTargetContext(Path targetContext) {
        this.targetContext = targetContext;
    }
    
    public void setTargetAddress(URL targetAddress) {
        this.targetAddress = targetAddress;
    }

    public void setTargetBean(String targetBean) {
        this.targetBean = targetBean;
    }

    public void process(List<EndpointInfo> clazzDefs) throws IOException {
        IllegalArgumentAssertion.assertNotNull(clazzDefs, "clazzDefs");
        IllegalArgumentAssertion.assertTrue(clazzDefs.size() == 1, "Multiple endpoints not supported");
        
        IllegalStateAssertion.assertNotNull(targetContext, "Camel context file name not set");
        IllegalStateAssertion.assertNotNull(targetBean, "Target bean not set");
        
        EndpointInfo epinfo = clazzDefs.get(0);
        
        VelocityEngine ve = new VelocityEngine();
        ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        ve.init();

        String tmplPath = "org/jboss/fuse/wsdl2rest/impl/codegen/jaxrs-camel-context.vm";
        try (InputStreamReader reader = new InputStreamReader(getClass().getClassLoader().getResourceAsStream(tmplPath))) {

            VelocityContext context = new VelocityContext();
            context.put("targetAddress", targetAddress != null ? targetAddress : "http://localhost:8080/somepath");
            context.put("serviceClass", epinfo.getFQN());
            context.put("targetBean", targetBean);
            context.put("allMethods", epinfo.getMethods());

            File outfile = outpath.resolve(targetContext).toFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outfile))) {
                ve.evaluate(context, writer, tmplPath, reader);
            }
        }
    }
}
