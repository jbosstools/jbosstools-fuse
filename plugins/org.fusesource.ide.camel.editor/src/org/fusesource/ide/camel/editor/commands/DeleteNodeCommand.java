package org.fusesource.ide.camel.editor.commands;

import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.context.impl.DeleteContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.fusesource.ide.camel.editor.editor.RiderDesignEditor;
import org.fusesource.ide.camel.model.AbstractNode;


/**
 * @author lhein
 */
public class DeleteNodeCommand extends RecordingCommand {
	private final RiderDesignEditor designEditor;
	private final AbstractNode selectedNode;

	public DeleteNodeCommand(RiderDesignEditor designEditor, TransactionalEditingDomain editingDomain, AbstractNode selectedNode) {
		super(editingDomain);
		this.designEditor = designEditor;
		this.selectedNode = selectedNode;
	}

	@Override
	protected void doExecute() {
		if (selectedNode != null) {
			PictogramElement[] pes = designEditor.getDiagramTypeProvider().getFeatureProvider().getAllPictogramElementsForBusinessObject(selectedNode);
			if (pes != null && pes.length>0) {
				for (PictogramElement pe : pes) {
					DeleteContext ctx = new DeleteContext(pe);
					IDeleteFeature f = designEditor.getFeatureProvider().getDeleteFeature(ctx);
					if (f.canDelete(ctx)) {
						f.delete(ctx);
					}
				}
			}
		}
	}
}
