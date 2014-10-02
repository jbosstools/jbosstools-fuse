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
import org.fusesource.ide.camel.model.generated.Bean;


public class CreateDefinedBeanFigureFeature extends CreateBeanFigureFeature {
	private final DefinedBean bean;

	public CreateDefinedBeanFigureFeature(IFeatureProvider fp, String name, String description, Bean endpoint) {
		super(fp, name, description, endpoint);
		this.bean = new DefinedBean(endpoint);
	}

	@Override
	protected AbstractNode createNode() {
		Bean answer = new Bean();
		answer.setRef(bean.getRef());
		answer.setBeanType(bean.getBeanType());
		answer.setName(bean.getName());
		return answer;
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.editor.features.create.CreateFigureFeature#getExemplar()
	 */
	@Override
	protected AbstractNode getExemplar() {
        return new DefinedBean();
	}
}
