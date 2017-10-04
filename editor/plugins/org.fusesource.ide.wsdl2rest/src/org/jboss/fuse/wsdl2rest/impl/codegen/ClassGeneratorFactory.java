package org.jboss.fuse.wsdl2rest.impl.codegen;

import java.nio.file.Path;

import org.jboss.fuse.wsdl2rest.ClassGenerator;

public class ClassGeneratorFactory {
    
    public static ClassGenerator getClassGenerator(Path outputPath) {
        return new JSR311ClassGenerator(outputPath);
    }
}
