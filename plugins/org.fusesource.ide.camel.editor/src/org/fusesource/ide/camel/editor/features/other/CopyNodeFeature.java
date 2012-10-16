package org.fusesource.ide.camel.editor.features.other;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICopyContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.features.AbstractCopyFeature;

public class CopyNodeFeature extends AbstractCopyFeature {

	public CopyNodeFeature(IFeatureProvider fp) {
		super(fp);
	}

	public boolean canCopy(ICopyContext context) {
		final PictogramElement[] pes = context.getPictogramElements();
		if (pes == null || pes.length == 0) { // nothing selected
			return false;
		}

		// return true, if all selected elements are a EClasses
		for (PictogramElement pe : pes) {
			final Object bo = getBusinessObjectForPictogramElement(pe);
			if (!(bo instanceof EClass)) {
				return false;
			}
		}
		return true;
	}

	public void copy(ICopyContext context) {
		// get the business-objects for all pictogram-elements
		// we already verified, that all business-objets are EClasses
		PictogramElement[] pes = context.getPictogramElements();
		Object[] bos = new Object[pes.length];
		for (int i = 0; i < pes.length; i++) {
			PictogramElement pe = pes[i];
			bos[i] = getBusinessObjectForPictogramElement(pe);
		}
		// put all business objects to the clipboard
		putToClipboard(bos);
	}

}
