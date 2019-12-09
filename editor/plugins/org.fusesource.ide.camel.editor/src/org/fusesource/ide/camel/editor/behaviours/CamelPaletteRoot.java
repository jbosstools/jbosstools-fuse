/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/

package org.fusesource.ide.camel.editor.behaviours;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.ConnectionCreationToolEntry;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PaletteSeparator;
import org.eclipse.gef.palette.PaletteStack;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.palette.IConnectionCreationToolEntry;
import org.eclipse.graphiti.palette.ICreationToolEntry;
import org.eclipse.graphiti.palette.IObjectCreationToolEntry;
import org.eclipse.graphiti.palette.IPaletteCompartmentEntry;
import org.eclipse.graphiti.palette.IPaletteSeparatorEntry;
import org.eclipse.graphiti.palette.IStackToolEntry;
import org.eclipse.graphiti.palette.IToolEntry;
import org.eclipse.graphiti.tb.IToolBehaviorProvider;
import org.eclipse.graphiti.ui.editor.IEclipseImageDescriptor;
import org.eclipse.graphiti.ui.internal.Messages;
import org.eclipse.graphiti.ui.internal.editor.GFConnectionCreationTool;
import org.eclipse.graphiti.ui.internal.editor.GFCreationTool;
import org.eclipse.graphiti.ui.internal.editor.GFPanningSelectionToolEntry;
import org.eclipse.graphiti.ui.internal.util.gef.MultiCreationFactory;
import org.eclipse.graphiti.ui.platform.IConfigurationProvider;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.eclipse.jface.resource.ImageDescriptor;

/**
 * @author lhein
 */
public class CamelPaletteRoot extends PaletteRoot {
	/*
	 * later we can make this configurable in the toolbehaviour provider, so
	 * that the dtp developer can influence the creation style from palette
	 */
	private static boolean DND_FROM_PALETTE = true; // if true then drag&drop
													// from the palette is
													// possible

	private IConfigurationProvider cfgProvider;

	private String filter;

	/**
	 * Creates a new GenericPaletteRoot for the given Model. It is constructed
	 * by calling createModelIndependentTools() and createCreationTools().
	 * 
	 * @param configurationProvider
	 *            the configuration provider
	 */
	public CamelPaletteRoot(IConfigurationProvider configurationProvider, String filter) {
		cfgProvider = configurationProvider;
		setFilter(filter);
		updatePaletteEntries();
	}

	/**
	 * Creates resp. updates the PaletteEntries. All old PaletteEntries will be
	 * removed and new ones will be created by calling the corresponding
	 * create-methods.
	 */
	public void updatePaletteEntries() {
		// remove old entries
		setDefaultEntry(null);
		@SuppressWarnings("unchecked")
		List<PaletteEntry> allEntries = new ArrayList<>(getChildren()); // MUST make a copy
		for (Iterator<PaletteEntry> iter = allEntries.iterator(); iter.hasNext();) {
			PaletteEntry entry = iter.next();
			remove(entry);
		}

		// create new entries
		add(createModelIndependentTools());

		if (cfgProvider != null && cfgProvider.getDiagramTypeProvider() != null) {
			IToolBehaviorProvider currentToolBehaviorProvider = cfgProvider.getDiagramTypeProvider()
					.getCurrentToolBehaviorProvider();

			IPaletteCompartmentEntry[] paletteCompartments = currentToolBehaviorProvider.getPalette();

			for (IPaletteCompartmentEntry compartmentEntry : paletteCompartments) {
				PaletteDrawer drawer = new PaletteDrawer(compartmentEntry.getLabel(), getImageDescriptor(compartmentEntry));
				if (isFiltered()) {
					drawer.setInitialState(PaletteDrawer.INITIAL_STATE_PINNED_OPEN);
				} else if (!compartmentEntry.isInitiallyOpen()) {
					drawer.setInitialState(PaletteDrawer.INITIAL_STATE_CLOSED);
				}

				List<IToolEntry> toolEntries = compartmentEntry.getToolEntries();

				for (IToolEntry toolEntry : toolEntries) {

					if (toolEntry instanceof ICreationToolEntry) {
						ICreationToolEntry creationToolEntry = (ICreationToolEntry) toolEntry;

						PaletteEntry createTool = createTool(creationToolEntry);
						if (createTool != null && filter(createTool)) {
							drawer.add(createTool);
						}

					} else if (toolEntry instanceof IStackToolEntry) {
						IStackToolEntry stackToolEntry = (IStackToolEntry) toolEntry;
						PaletteStack stack = new PaletteStack(stackToolEntry.getLabel(), stackToolEntry.getDescription(),
								GraphitiUi.getImageService().getImageDescriptorForId(
										cfgProvider.getDiagramTypeProvider().getProviderId(), stackToolEntry.getIconId()));
						drawer.add(stack);
						List<ICreationToolEntry> creationToolEntries = stackToolEntry.getCreationToolEntries();
						for (ICreationToolEntry creationToolEntry : creationToolEntries) {
							PaletteEntry createTool = createTool(creationToolEntry);
							if (createTool != null && filter(createTool)) {
								stack.add(createTool);
							}
						}
					} else if (toolEntry instanceof IPaletteSeparatorEntry) {
						drawer.add(new PaletteSeparator());
					}

				}

				if (!drawer.getChildren().isEmpty()) {
					add(drawer);
				}
			}
			
			// PaletteEntry creationTools = createCreationTools();
			// if (creationTools != null)
			// add(creationTools);
		}
	}

	public boolean isFiltered() {
		return filter != null && filter.length() > 0;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		if (filter == null) {
			this.filter = null;
		} else {
			this.filter = filter.toLowerCase().trim();
		}
	}

	/**
	 * Return <code>true</code> if <em>label</em> or <em>description</em> of
	 * <code>paletteEntry</code> contains the current palette root's filter;
	 * returns <code>false</code> otherwise.
	 * 
	 * @param paletteEntry
	 *            - the palette entry to filter.
	 * @return <code>true</code> if <em>label</em> or <em>description</em> of
	 *         <code>paletteEntry</code> contains the current palette root's
	 *         filter; returns <code>false</code> otherwise.
	 */
	protected boolean filter(PaletteEntry paletteEntry) {
		if (filter == null || filter.length() == 0) {
			return true;
		}
		if (paletteEntry.getLabel() != null && paletteEntry.getLabel().toLowerCase().contains(filter)) {
			return true;
		}
		return false;
	}

	/**
	 * Creates and adds the model-independent tools to a new PaletteContainer.
	 * Those are for example: selection-tool, marque-tool, connection-tool.
	 * 
	 * @return The PaletteContainer withe the model-independent tools.
	 */
	protected PaletteContainer createModelIndependentTools() {
		PaletteGroup controlGroup = new PaletteGroup(Messages.GraphicsPaletteRoot_0_xmen);
		List<PaletteEntry> entries = new ArrayList<>();

		// selection tool
		ToolEntry tool = new GFPanningSelectionToolEntry();
		entries.add(tool);
		setDefaultEntry(tool);

		controlGroup.addAll(entries);
		return controlGroup;
	}

	private PaletteEntry createTool(ICreationToolEntry creationToolEntry) {

		if (creationToolEntry instanceof IObjectCreationToolEntry) {
			IObjectCreationToolEntry objectCreationToolEntry = (IObjectCreationToolEntry) creationToolEntry;

			ICreateFeature feat = objectCreationToolEntry.getCreateFeature();

			DefaultCreationFactory cf = new DefaultCreationFactory(feat, ICreateFeature.class);
			Object template = DND_FROM_PALETTE ? cf : null;

			CombinedTemplateCreationEntry pe = new CombinedTemplateCreationEntry(feat.getCreateName(),
					feat.getCreateDescription(), template, cf, getImageDescriptor(creationToolEntry, true),
					getImageDescriptor(creationToolEntry, false));
			pe.setToolClass(GFCreationTool.class);

			return pe;

		} else if (creationToolEntry instanceof IConnectionCreationToolEntry) {
			IConnectionCreationToolEntry connectionCreationToolEntry = (IConnectionCreationToolEntry) creationToolEntry;

			MultiCreationFactory multiCreationFactory = new MultiCreationFactory(
					connectionCreationToolEntry.getCreateConnectionFeatures());

			ConnectionCreationToolEntry pe = new ConnectionCreationToolEntry(creationToolEntry.getLabel(),
					creationToolEntry.getDescription(), multiCreationFactory,
					getImageDescriptor(creationToolEntry, true), getImageDescriptor(creationToolEntry, false));
			pe.setToolClass(GFConnectionCreationTool.class);

			return pe;

		}

		return null;
	}

	private class DefaultCreationFactory implements CreationFactory {

		private Object obj;

		private Object objType;

		public DefaultCreationFactory(Object obj, Object objType) {
			super();
			this.obj = obj;
			this.objType = objType;
		}

		@Override
		public Object getNewObject() {
			return obj;
		}

		@Override
		public Object getObjectType() {
			return objType;
		}

	}

	private ImageDescriptor getImageDescriptor(ICreationToolEntry creationToolEntry, boolean smallImage) {
		ImageDescriptor imageDescriptor;
		if (creationToolEntry instanceof IEclipseImageDescriptor) {
			imageDescriptor = ((IEclipseImageDescriptor) creationToolEntry).getImageDescriptor();
		} else {
			String iconId = smallImage ? creationToolEntry.getIconId() : creationToolEntry.getLargeIconId();
			imageDescriptor = GraphitiUi.getImageService()
					.getImageDescriptorForId(cfgProvider.getDiagramTypeProvider().getProviderId(), iconId);
		}
		return imageDescriptor;
	}

	private ImageDescriptor getImageDescriptor(IPaletteCompartmentEntry compartmentEntry) {
		ImageDescriptor imageDescriptor;
		if (compartmentEntry instanceof IEclipseImageDescriptor) {
			imageDescriptor = ((IEclipseImageDescriptor) compartmentEntry).getImageDescriptor();
		} else {
			imageDescriptor = GraphitiUi.getImageService().getImageDescriptorForId(
					cfgProvider.getDiagramTypeProvider().getProviderId(), compartmentEntry.getIconId());
		}
		return imageDescriptor;
	}
}
