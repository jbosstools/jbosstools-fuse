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

package org.fusesource.ide.camel.editor.features.other;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IPasteContext;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.features.AbstractPasteFeature;

public class PasteNodeFeature extends AbstractPasteFeature {

	public PasteNodeFeature(IFeatureProvider fp) {
		super(fp);
	}

	public boolean canPaste(IPasteContext context) {
		// only support pasting directly in the diagram (nothing else selected)
		PictogramElement[] pes = context.getPictogramElements();
		if (pes.length != 1 || !(pes[0] instanceof Diagram)) {
			return false;
		}

		// Get from clipboard not working
		// can paste, if all objects on the clipboard are EClasses
		Object[] fromClipboard = getFromClipboard();
		if (fromClipboard == null || fromClipboard.length == 0) {
			return false;
		}
		for (Object object : fromClipboard) {
			if (!(object instanceof EClass)) {
				return false;
			}
		}
		return true;
		// return false;
	}

	public void paste(IPasteContext context) {
		// we already verified, that we paste directly in the diagram
		PictogramElement[] pes = context.getPictogramElements();
		Diagram diagram = (Diagram) pes[0];
		// get the EClasses from the clipboard without copying them
		// (only copy the pictogram element, not the business object)
		// then create new pictogram elements using the add feature
		Object[] objects = getFromClipboard();
		for (Object object : objects) {
			AddContext ac = new AddContext();
			ac.setLocation(0, 0); // for simplicity paste at (0, 0)
			ac.setTargetContainer(diagram);
			addGraphicalRepresentation(ac, object);
		}
	}

}
