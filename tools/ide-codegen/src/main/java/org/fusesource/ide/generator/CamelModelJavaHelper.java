/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.generator;

import io.fabric8.camel.tooling.util.CamelModelUtils;
import org.apache.camel.CamelContext;
import org.apache.camel.model.ProcessorDefinition;

/**
 * Work around Scala casting woes :)
 */
public class CamelModelJavaHelper {
    public static boolean canAcceptOutput(CamelContext camelContext, Class<?> clazz) {
        Object bean = camelContext.getInjector().newInstance(clazz);
        return CamelModelUtils.canAcceptOutput(clazz, (ProcessorDefinition) bean);
    }

    public static boolean isNextSiblingStepAddedAsNodeChild(CamelContext camelContext, Class<?> clazz) {
        Object bean = camelContext.getInjector().newInstance(clazz);
        return CamelModelUtils.isNextSiblingStepAddedAsNodeChild(clazz, (ProcessorDefinition)bean);
    }
}
