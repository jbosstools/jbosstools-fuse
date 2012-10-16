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
