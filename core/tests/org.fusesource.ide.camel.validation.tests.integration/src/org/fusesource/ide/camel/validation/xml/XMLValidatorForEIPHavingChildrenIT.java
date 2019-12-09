/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.validation.xml;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class XMLValidatorForEIPHavingChildrenIT extends AbstractXMLCamelRouteValidorTestHelper {

	@Parameters(name = "{0} expects {1} validation markers")
    public static Collection<Object[]> data() {
        return Arrays.asList((Object[][]) new Object[][] {
                 { "routeWithEmptyAggregate", 1 },
                 { "routeWithEmptyChoice", 1 },
                 { "routeWithEmptyFilter", 1 },
                 { "routeWithEmptyIdempotentConsumer", 1 },
                 { "routeWithEmptyIntercept", 1 },
                 { "routeWithEmptyInterceptFrom", 1 },
                 { "routeWithEmptyInterceptSendToEndpoint", 1 },
                 { "routeWithEmptyLoop", 1 },
                 { "routeWithEmptyMulticast", 1 },
                 { "routeWithEmptyOnCompletion", 1 },
                 { "routeWithEmptyPipeline", 1 },
                 { "routeWithEmptyPolicy", 0 },
                 { "routeWithEmptyResequence", 1 },
                 { "routeWithEmptySample", 1 },
                 { "routeWithEmptySplit", 1 },
                 { "routeWithEmptyThreads", 1 },
                 { "routeWithEmptyThrottle", 1 },
                 { "routeWithEmptyTransacted", 0 },
                 { "routeWithValidChoice", 0 },
                 { "routeWithValidContainers", 0 }
           });
    }
    
    @Parameter
	public String fileName;
    @Parameter(1)
	public int numberOfValidationErrorExpected;
	    
	@Test
	public void testValidate() throws Exception {
		testValidate(fileName+".xml", numberOfValidationErrorExpected);
	}

}
