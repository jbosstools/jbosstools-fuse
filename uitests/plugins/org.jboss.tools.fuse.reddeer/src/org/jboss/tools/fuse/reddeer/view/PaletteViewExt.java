/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.reddeer.view;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.reddeer.common.util.Display;
import org.eclipse.reddeer.common.util.ResultRunnable;
import org.eclipse.reddeer.gef.impl.palette.AbstractPalette;
import org.eclipse.reddeer.gef.matcher.IsToolEntryWithParent;
import org.eclipse.reddeer.workbench.core.lookup.EditorPartLookup;

/**
 * Extends RedDeer palette view implementation - allows getting tools only from specific palette view group
 * 
 * @author djelinek
 */
public class PaletteViewExt extends AbstractPalette {

	public static final String GROUP_COMPONENTS = "Components";
	public static final String GROUP_ROUTING = "Routing";
	public static final String GROUP_CONTROL_FLOW = "Control Flow";
	public static final String GROUP_TRANSFORMATION = "Transformation";
	public static final String GROUP_MISCELLANEOUS = "Miscellaneous";
	
	public PaletteViewExt() {
		super(Display.syncExec(new ResultRunnable<PaletteViewer>() {

			@Override
			public PaletteViewer run() {
				return getGraphicalViewer().getEditDomain().getPaletteViewer();
			}
		}));
	}

	public List<String> getGroupTools(String group) {
		List<PaletteEntry> entries = paletteHandler.getPaletteEntries(paletteViewer, new IsToolEntryWithParent(group));
		return entries.stream().map(PaletteEntry::getLabel).collect(Collectors.toList());
	}

	private static GraphicalViewer getGraphicalViewer() {
		return Display.syncExec(new ResultRunnable<GraphicalViewer>() {

			@Override
			public GraphicalViewer run() {
				return (GraphicalViewer) EditorPartLookup.getInstance().getEditor().getAdapter(GraphicalViewer.class);
			}
		});
	}
}
