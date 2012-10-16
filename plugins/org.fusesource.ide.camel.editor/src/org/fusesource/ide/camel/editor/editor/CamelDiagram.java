package org.fusesource.ide.camel.editor.editor;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.graphiti.mm.pictograms.impl.DiagramImpl;
import org.fusesource.ide.camel.model.RouteContainer;
import org.fusesource.ide.commons.util.Objects;


public class CamelDiagram extends DiagramImpl {
	private final RiderDesignEditor designEditor;

	public CamelDiagram(RiderDesignEditor designEditor) {
		this.designEditor = designEditor;
	}

	@Override
	public Resource eResource() {
		RouteContainer model = designEditor.getModel();
		Objects.notNull(model, "model");
		return model.eResource();
	}

	public RiderDesignEditor getDesignEditor() {
		return designEditor;
	}



}
