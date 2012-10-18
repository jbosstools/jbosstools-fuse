package org.fusesource.ide.camel.editor;

import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

/**
 * @author lhein
 */
public class CamelModelNotificationService extends DefaultNotificationService {
	
	public CamelModelNotificationService(IDiagramTypeProvider diagramTypeProvider) {
		super(diagramTypeProvider);
	}

	/*
	 * (non-Javadoc)
	 * @see org.fusesource.ide.camel.editor.DefaultNotificationService#updatePictogramElements(org.eclipse.graphiti.mm.pictograms.PictogramElement[])
	 */
	@Override
	public void updatePictogramElements(PictogramElement[] dirtyPes) {
		super.updatePictogramElements(dirtyPes);
	}

	/*
	 * (non-Javadoc)
	 * @see org.fusesource.ide.camel.editor.DefaultNotificationService#calculateRelatedPictogramElements(java.lang.Object[])
	 */
	@Override
	public PictogramElement[] calculateRelatedPictogramElements(Object[] changedBOs) {
		return super.calculateRelatedPictogramElements(changedBOs);
	}
}
