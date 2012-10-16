package org.fusesource.ide.camel.editor.commands;

import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.fusesource.ide.camel.editor.editor.RiderDesignEditor;
import org.fusesource.ide.camel.editor.features.custom.LayoutDiagramFeature;


public class LayoutCommand extends RecordingCommand {
	private final RiderDesignEditor designEditor;

	public LayoutCommand(RiderDesignEditor designEditor, TransactionalEditingDomain editingDomain) {
		super(editingDomain);
		this.designEditor = designEditor;
	}

	@Override
	protected void doExecute() {
		ICustomFeature[] customFeatures = designEditor.getFeatureProvider().getCustomFeatures(null);
		for (ICustomFeature customFeature : customFeatures) {
			if (customFeature instanceof LayoutDiagramFeature) {
				customFeature.execute(null);
			}
		}
	}
}
