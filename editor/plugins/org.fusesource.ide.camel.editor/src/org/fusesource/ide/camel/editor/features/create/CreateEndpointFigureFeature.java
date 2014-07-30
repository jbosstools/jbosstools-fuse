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
import org.fusesource.ide.camel.model.Endpoint;


public class CreateEndpointFigureFeature extends CreateFigureFeature<Endpoint> {
	private final Endpoint endpoint;

	public CreateEndpointFigureFeature(IFeatureProvider fp, String name, String description, Endpoint endpoint) {
		super(fp, name, description, Endpoint.class);
		this.endpoint = endpoint;
		setExemplar(endpoint);
	}

	@Override
	protected AbstractNode createNode() {
		return new Endpoint(endpoint);
	}


}
