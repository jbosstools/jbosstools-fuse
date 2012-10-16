package org.fusesource.ide.camel.editor.commands;

import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.context.impl.CustomContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.fusesource.ide.camel.editor.editor.RiderDesignEditor;
import org.fusesource.ide.camel.editor.features.custom.CreateNodeConnectionFeature;
import org.fusesource.ide.camel.model.AbstractNode;


public class AddNodeCommand extends RecordingCommand {
	private final RiderDesignEditor editor;
	private final Class<? extends AbstractNode> aClass;
	private final AbstractNode selectedNode;

	public AddNodeCommand(RiderDesignEditor editor, TransactionalEditingDomain editingDomain, Class<? extends AbstractNode> aClass, AbstractNode selectedNode) {
		super(editingDomain);
		this.editor = editor;
		this.aClass = aClass;
		this.selectedNode = selectedNode;
	}

	@Override
	protected void doExecute() {
		CreateNodeConnectionFeature feature = new CreateNodeConnectionFeature(editor.getFeatureProvider(), aClass);
		PictogramElement selectedElement = editor.getFeatureProvider().getPictogramElementForBusinessObject(selectedNode);
		PictogramElement[] selectedElements;
		if (selectedElement != null) {
			selectedElements = new PictogramElement[] {selectedElement};
		} else {
			System.out.println("==== TODO - could not find PictogramElement for node " + selectedNode + " probably its that the RiderOutlinePage is out of sync with the Diagram model!");
			selectedElements = editor.getSelectedPictogramElements();
		}
		CustomContext context = new CustomContext(selectedElements);
		feature.execute(context);
	}


}
