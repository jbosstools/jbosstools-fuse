/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.qe.reddeer.runtime;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;

import org.w3c.dom.Element;

/**
 * 
 * @author apodhrad
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Properties {

	@XmlAnyElement
	private List<Element> any;

	public List<Element> getAny() {
		return any;
	}

	public void setAny(List<Element> any) {
		this.any = any;
	}

	public String getProperty(String key) {
		for (Element element : any) {
			String elemKey = element.getNodeName();
			if (elemKey != null && elemKey.equals(key)) {
				return element.getTextContent();
			}
		}
		return null;
	}

	public List<String> getProperties(String key) {
		List<String> values = new ArrayList<String>();
		for (Element element : any) {
			String elemKey = element.getNodeName();
			if (elemKey != null && elemKey.equals(key)) {
				values.add(element.getTextContent());
			}
		}
		return values;
	}
}
