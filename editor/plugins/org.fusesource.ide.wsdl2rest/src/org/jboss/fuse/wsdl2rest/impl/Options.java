/*
 * #%L
 * Fuse Patch :: Core
 * %%
 * Copyright (C) 2015 Private
 * %%
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
 * #L%
 */
package org.jboss.fuse.wsdl2rest.impl;

import java.net.URL;
import java.nio.file.Path;

//import org.kohsuke.args4j.Option;

final class Options {

//	@Option(name = "--help", help = true)
	boolean help;

//    @Option(name = "--wsdl", required = true, usage = "URL to the input WSDL (required)")
    URL wsdlUrl;
    
//    @Option(name = "--out", required = true, usage = "Output path for generated artefacts (required)")
    Path outpath;

//    @Option(name = "--target-context", usage = "Path to the generated camel context")
    Path targetContext;

//    @Option(name = "--target-address", usage = "Address for the generated camel endpoint")
    URL targetAddress;

//    @Option(name = "--target-bean", usage = "Classname for the bean that camel delegates to")
    String targetBean;
}
