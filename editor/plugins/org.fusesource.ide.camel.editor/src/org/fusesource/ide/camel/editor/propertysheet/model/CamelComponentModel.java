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
package org.fusesource.ide.camel.editor.propertysheet.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

/**
 * @author lhein
 */
public class CamelComponentModel {
    private List<CamelComponent> components = new ArrayList<CamelComponent>();
    
    /**
     * @return the components
     */
    @XmlElementWrapper(name = "components")
    @XmlElement(name = "component")
    public List<CamelComponent> getComponents() {
        return this.components;
    }
    
    /**
     * @param components the components to set
     */
    public void setComponents(List<CamelComponent> components) {
        this.components = components;
    }
    
    public CamelComponent getComponent(String clazz) {
        for (CamelComponent c : components) {
            if (c.getComponentClass().equalsIgnoreCase(clazz)) return c;
        }
        return null;
    }
}
