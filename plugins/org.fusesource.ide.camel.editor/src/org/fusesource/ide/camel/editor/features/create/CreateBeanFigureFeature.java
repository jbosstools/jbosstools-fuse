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
import org.fusesource.ide.camel.model.generated.Bean;


public class CreateBeanFigureFeature extends CreateFigureFeature<Bean> {
	private final Bean bean;

	public CreateBeanFigureFeature(IFeatureProvider fp, String name, String description, Bean endpoint) {
		super(fp, name, description, Bean.class);
		this.bean = endpoint;
	}

	@Override
	protected AbstractNode createNode() {
		Bean answer = new Bean();
		answer.setRef(bean.getRef());
		answer.setBeanType(bean.getBeanType());
		answer.setName(bean.getName());
		return answer;
	}


}
