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

package org.fusesource.ide.camel.editor.features.create;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.DefinedBean;
import org.fusesource.ide.camel.model.catalog.CamelModelFactory;
import org.fusesource.ide.camel.model.catalog.eips.Eip;
import org.fusesource.ide.camel.model.generated.UniversalEIPNode;


public class CreateDefinedBeanFigureFeature extends CreateBeanFigureFeature {
	private final DefinedBean bean;

	public CreateDefinedBeanFigureFeature(IFeatureProvider fp, String name, 
			String description, UniversalEIPNode endpoint) {
		super(fp, name, description, endpoint);
		this.bean = new DefinedBean(endpoint);
	}

	@Override
	protected AbstractNode createNode() {
		Eip eip = CamelModelFactory.getModelForVersion(CamelModelFactory.getCamelVersion(null)).getEipModel().getEIPByClass("bean");
		UniversalEIPNode answer = new UniversalEIPNode(eip);
    	answer.setShortPropertyValue("ref", bean.getShortPropertyValue("ref"));
    	answer.setShortPropertyValue("beanType", bean.getShortPropertyValue("beanType"));
		answer.setName(bean.getName());
		return answer;
	}
}
