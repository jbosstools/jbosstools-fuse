package org.jboss.fuse.wsdl2rest.impl.codegen;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;

import org.apache.cxf.tools.common.ToolContext;
import org.apache.cxf.tools.common.model.JavaInterface;
import org.apache.cxf.tools.common.model.JavaModel;
import org.apache.cxf.tools.common.model.JavaServiceClass;
import org.apache.cxf.tools.wsdlto.WSDLToJava;

/**
 * Generate Java Types from WSDL using Apache CXF WSDL2Java
 *
 * http://cxf.apache.org/docs/wsdl-to-java.html
 * 
 * @since 10-Nov-2016
 * @author tdiesler@redhat.com
 */
public class JavaTypeGenerator {

    private final URL wsdlURL;
    private final Path outpath;
    
    public JavaTypeGenerator(Path outpath, URL wsdlURL) {
        this.wsdlURL = wsdlURL;
        this.outpath = outpath;
    }


    public void execute() throws Exception {
        try {
	        final String[] args = new String[] {
	                "-d", outpath.toString(),
	                wsdlURL.toExternalForm(),
	        };
	        final ToolContext ctx = new ToolContext();
	        final WSDLToJava wsdl2Java = new WSDLToJava(args);
	        wsdl2Java.run(ctx);
	        
	        JavaModel javaModel = ctx.getJavaModel();
	        for (JavaInterface aux : javaModel.getInterfaces().values()) {
	            File auxFile = outpath.resolve(aux.getPackageName().replace('.', '/') + "/" + aux.getName() + ".java").toFile();
	            auxFile.delete();
	        }
	        for (JavaServiceClass aux : javaModel.getServiceClasses().values()) {
	            File auxFile = outpath.resolve(aux.getPackageName().replace('.', '/') + "/" + aux.getName() + ".java").toFile();
	            auxFile.delete();
	        }
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }
}