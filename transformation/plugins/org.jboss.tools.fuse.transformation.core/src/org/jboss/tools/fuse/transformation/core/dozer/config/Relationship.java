/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.transformation.core.dozer.config;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for relationship.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="relationship">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *     &lt;enumeration value="cumulative"/>
 *     &lt;enumeration value="non-cumulative"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "relationship")
@XmlEnum
public enum Relationship {

    @XmlEnumValue("cumulative")
    CUMULATIVE("cumulative"), //$NON-NLS-1$
    @XmlEnumValue("non-cumulative")
    NON_CUMULATIVE("non-cumulative"); //$NON-NLS-1$
    private final String value;

    Relationship(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static Relationship fromValue(String v) {
        for (Relationship c: Relationship.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
