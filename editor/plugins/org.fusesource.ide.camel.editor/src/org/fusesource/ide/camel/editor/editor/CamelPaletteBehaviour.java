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

package org.fusesource.ide.camel.editor.editor;

import org.eclipse.gef.dnd.TemplateTransferDragSourceListener;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.palette.PaletteViewerProvider;
import org.eclipse.graphiti.ui.editor.DefaultPaletteBehavior;

/**
 * @author lhein
 */
public class CamelPaletteBehaviour extends DefaultPaletteBehavior {
	RiderDesignEditor riderDesignEditor = null;
	/**
	 * @param riderDesignEditor
	 */
	public CamelPaletteBehaviour(RiderDesignEditor riderDesignEditor) {
		super(riderDesignEditor.getDiagramBehavior());
		this.riderDesignEditor = riderDesignEditor;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.ui.editor.DefaultPaletteBehavior#createPaletteRoot()
	 */
	@Override
	protected PaletteRoot createPaletteRoot() {
		return new CamelPaletteRoot(riderDesignEditor.getConfigurationProvider());
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.ui.editor.DefaultPaletteBehavior#createPaletteViewerProvider()
	 */
	@Override
	protected PaletteViewerProvider createPaletteViewerProvider() {
		return new PaletteViewerProvider(diagramBehavior.getEditDomain()) {
			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.gef.ui.palette.PaletteViewerProvider#
			 * configurePaletteViewer(org.eclipse.gef.ui.palette.PaletteViewer)
			 */
			@Override
			protected void configurePaletteViewer(PaletteViewer viewer) {
				super.configurePaletteViewer(viewer);
				// create a drag source listener for this palette viewer
				// together with an appropriate transfer drop target listener,
				// this will enable
				// model element creation by dragging a
				// CombinatedTemplateCreationEntries
				// from the palette into the editor
				// @see ShapesEditor#createTransferDropTargetListener()
				viewer.addDragSourceListener(new TemplateTransferDragSourceListener(
						viewer));
			}
		};
	}
}
