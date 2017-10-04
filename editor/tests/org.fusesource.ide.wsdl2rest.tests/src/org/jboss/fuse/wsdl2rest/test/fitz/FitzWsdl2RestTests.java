package org.jboss.fuse.wsdl2rest.test.fitz;
/*
 * Copyright (c) 2008 SL_OpenSource Consortium
 * All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.jboss.fuse.wsdl2rest.ClassGenerator;
import org.jboss.fuse.wsdl2rest.EndpointInfo;
import org.jboss.fuse.wsdl2rest.MethodInfo;
import org.jboss.fuse.wsdl2rest.ResourceMapper;
import org.jboss.fuse.wsdl2rest.impl.ResourceMapperImpl;
import org.jboss.fuse.wsdl2rest.impl.Wsdl2Rest;
import org.jboss.fuse.wsdl2rest.impl.codegen.ClassGeneratorFactory;
import org.jboss.fuse.wsdl2rest.impl.codegen.JavaTypeGenerator;
import org.junit.Assert;
import org.junit.Test;


public class FitzWsdl2RestTests {

    @Test
    public void testGenerateHello() throws Exception {
        File wsdlFile = new File("resources/fitz/helloService.wsdl");
    	testGenerate(wsdlFile.toURI().toURL(), 
    			"target/generated-sources/fitz");
    }
    
    @Test
    public void testGenerateWeather() throws Exception {
    	testGenerate(new URL("http://www.webservicex.com/globalweather.asmx?wsdl"), 
    			"target/generated-sources/weather");
    }

    private void testGenerate(final URL wsdlLocation, final String outputPath) throws Exception {

        Path outpath = new File(outputPath).toPath();
        
        Wsdl2Rest tool = new Wsdl2Rest(wsdlLocation, outpath);

        List<EndpointInfo> clazzDefs = tool.process();

        ResourceMapper resMapper = new ResourceMapperImpl();
        resMapper.assignResources(clazzDefs);

        JavaTypeGenerator typeGen = new JavaTypeGenerator(outpath, wsdlLocation);
        typeGen.execute();
        
        ClassGenerator classGen = ClassGeneratorFactory.getClassGenerator(outpath);
        classGen.generateClasses(clazzDefs);
    }
}
    