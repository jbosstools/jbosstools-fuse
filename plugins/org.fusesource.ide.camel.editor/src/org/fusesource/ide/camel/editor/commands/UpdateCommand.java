package org.fusesource.ide.camel.editor.commands;

import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.fusesource.ide.camel.editor.editor.RiderDesignEditor;
import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.RouteSupport;


/**
 * @author lhein
 */
public class UpdateCommand extends RecordingCommand {
	private final RiderDesignEditor designEditor;
	private AbstractNode node;

	public UpdateCommand(RiderDesignEditor designEditor, TransactionalEditingDomain editingDomain, AbstractNode node) {
		super(editingDomain);
		this.designEditor = designEditor;
		this.node = node;
	}

	@Override
	protected void doExecute() {
		AbstractNode selectedNode = this.node == null ? designEditor.getSelectedNode() : node;
		if (selectedNode == null) {
			// use the route node in this case
			selectedNode = designEditor.getSelectedRoute();
		}
		PictogramElement pe = selectedNode instanceof RouteSupport ? designEditor.getDiagram() : designEditor.getFeatureProvider().getPictogramElementForBusinessObject(selectedNode);
		if (pe == null) {
			System.out.println("Warning could not find PictogramElement for selectedNode: " + selectedNode);
		}
		UpdateContext ctx = new UpdateContext(pe);
		IUpdateFeature updateFeature = designEditor.getFeatureProvider().getUpdateFeature(ctx);
		updateFeature.update(ctx);
	}
}
