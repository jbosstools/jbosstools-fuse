/**
 * Copyright (C) 2010, FuseSource Corp.  All rights reserved.
 * http://fusesource.com
 *
 * The software in this package is published under the terms of the
 * AGPL license a copy of which has been included with this distribution
 * in the license.txt file.
 */
package org.fusesource.ide.generator;

import org.apache.camel.CamelContext;
import org.apache.camel.model.ProcessorDefinition;
import org.fusesource.camel.tooling.util.CamelModelUtils;

/**
 * Work around Scala casting woes :)
 */
public class CamelModelJavaHelper {
    public static boolean canAcceptOutput(CamelContext camelContext, Class<?> clazz) {
        Object bean = camelContext.getInjector().newInstance(clazz);
        return CamelModelUtils.canAcceptOutput(clazz, (ProcessorDefinition)bean);
    }

    public static boolean isNextSiblingStepAddedAsNodeChild(CamelContext camelContext, Class<?> clazz) {
        Object bean = camelContext.getInjector().newInstance(clazz);
        return CamelModelUtils.isNextSiblingStepAddedAsNodeChild(clazz, (ProcessorDefinition)bean);
    }
}
