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
package org.jboss.tools.fuse.qe.reddeer.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.jboss.tools.fuse.qe.reddeer.utils.CamelComponentUtils;

/**
 * 
 * @author apodhrad
 *
 */
public abstract class AbstractURICamelComponent implements CamelComponent {

	private String baseUri;
	private List<String> keyList;
	private Properties properties;

	public AbstractURICamelComponent(String baseUri) {
		this.baseUri = baseUri;
		keyList = new ArrayList<String>();
		properties = new Properties();
	}

	@Override
	public String getLabel() {
		return CamelComponentUtils.getLabel(getUri());
	}

	@Override
	public String getTooltip() {
		return getUri();
	}

	public String getUri() {
		StringBuffer uri = new StringBuffer(baseUri);
		for (String key : keyList) {
			uri.append(":").append(getProperty(key));
		}
		return uri.toString();
	}

	public String getProperty(String key) {
		return properties.getProperty(key);
	}

	public void setProperty(String key, String value) {
		properties.setProperty(key, value);
	}

	protected void addProperty(String key, String value) {
		properties.setProperty(key, value);
		keyList.add(key);
	}

}
