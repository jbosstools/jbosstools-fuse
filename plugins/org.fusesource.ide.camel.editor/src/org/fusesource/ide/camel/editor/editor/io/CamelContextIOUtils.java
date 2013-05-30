/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.camel.editor.editor.io;
 
import org.eclipse.core.resources.IFile;
import org.fusesource.camel.tooling.util.ValidationHandler;
import org.fusesource.ide.camel.model.RouteContainer;
import org.fusesource.ide.camel.model.io.ContainerMarshaler;
import org.fusesource.ide.camel.model.io.XmlContainerMarshaller;
 
 
/**
 * @author lhein
 */
public class CamelContextIOUtils {
     
    private static ContainerMarshaler marshaller = new XmlContainerMarshaller();
     
    /**
     * loads the model from file
     * 
     * @param file
     *            the file to load from
     */
    public static RouteContainer loadModelFromFile(IFile file) {
        return marshaller.loadRoutes(file);
    }
     
    public static RouteContainer loadModelFromText(String text) {
        return marshaller.loadRoutesFromText(text);
    }
 
    /**
     * validates the given model
     * 
     * @param model
     * @return  the validation handler or null on errors
     */
    public static ValidationHandler validateModel(RouteContainer model) {
        if (model != null) {
            return model.validate();
        }
        return null;
    }
}