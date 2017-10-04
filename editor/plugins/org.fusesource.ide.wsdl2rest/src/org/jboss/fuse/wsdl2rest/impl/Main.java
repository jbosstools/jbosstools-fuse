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

import java.util.List;

import org.jboss.fuse.wsdl2rest.EndpointInfo;
//import org.kohsuke.args4j.CmdLineException;
//import org.kohsuke.args4j.CmdLineParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

	private static final Logger LOG = LoggerFactory.getLogger(Main.class);
	
    public static void main(String[] args) {
        try {
//            new Main().mainInternal(args);
        } catch (Throwable th) {
            Runtime.getRuntime().exit(1);
        }
    }

    // Entry point with no system exit
    public List<EndpointInfo> mainInternal(String[] args) throws Exception {
		return null;
        
//        Options options = new Options();
//        CmdLineParser parser = new CmdLineParser(options);
//        try {
//            parser.parseArgument(args);
//        } catch (CmdLineException ex) {
//            helpScreen(parser);
//            throw ex;
//        }

//        try {
//            Wsdl2Rest tool = new Wsdl2Rest(options.wsdlUrl, options.outpath);
//            tool.setTargetContext(options.targetContext);
//            tool.setTargetAddress(options.targetAddress);
//            tool.setTargetBean(options.targetBean);
//            return tool.process();
//        } catch (Throwable th) {
//            LOG.error("Error executing command", th);
//            throw th;
//        }
    }

//    private static void helpScreen(CmdLineParser cmdParser) {
//        System.err.println("wsdl2rest [options...]");
//        cmdParser.printUsage(System.err);
//    }
}
