package org.fusesource.ide.camel.editor.editor;

import org.eclipse.graphiti.mm.pictograms.impl.DiagramImpl;


public class CamelDiagram extends DiagramImpl {
	private final RiderDesignEditor designEditor;

	public CamelDiagram(RiderDesignEditor designEditor) {
		this.designEditor = designEditor;
	}

//	@Override
//	public Resource eResource() {
//		RouteContainer model = designEditor.getModel();
//		Objects.notNull(model, "model");
//		return model.eResource();
//	}

	public RiderDesignEditor getDesignEditor() {
		return designEditor;
	}



}
