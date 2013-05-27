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

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.graphiti.datatypes.ILocation;
import org.eclipse.graphiti.features.IFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.impl.CustomContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.tb.ContextMenuEntry;
import org.eclipse.graphiti.tb.IContextMenuEntry;
import org.eclipse.graphiti.tb.IToolBehaviorProvider;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;
import org.eclipse.graphiti.ui.editor.DiagramEditorContextMenuProvider;
import org.eclipse.graphiti.ui.internal.action.CustomAction;
import org.eclipse.graphiti.ui.internal.action.RemoveAction;
import org.eclipse.graphiti.ui.platform.IConfigurationProvider;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.eclipse.graphiti.util.ILocationInfo;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.fusesource.ide.camel.editor.features.custom.DeleteRouteFeature;


public class CamelDiagramEditorContextMenuProvider extends DiagramEditorContextMenuProvider {

	private IConfigurationProvider configurationProvider;

	public CamelDiagramEditorContextMenuProvider(EditPartViewer viewer, ActionRegistry registry,
			IConfigurationProvider configurationProvider) {
		super(viewer, registry, configurationProvider);
		this.configurationProvider = configurationProvider;
	}

	@Override
	protected void addDefaultMenuGroupRest(IMenuManager manager) {
		addAlignmentSubMenu(manager, GEFActionConstants.GROUP_REST);

		PictogramElement pes[] = getBehavior().getSelectedPictogramElements();
		ICustomContext context = new CustomContext(pes);

		IToolBehaviorProvider tb = getConfigurationProvider().getDiagramTypeProvider().getCurrentToolBehaviorProvider();
		IContextMenuEntry[] contextMenuEntries = tb.getContextMenu(context);

		// the last one is an add menu; lets move it to a different group
		int lastIndex = contextMenuEntries.length - 1;
		IContextMenuEntry addEntries = contextMenuEntries[lastIndex];
		addEntry(manager, addEntries, context, GEFActionConstants.GROUP_ADD, null);
		addActionToMenuIfAvailable(manager, RemoveAction.ACTION_ID, GEFActionConstants.GROUP_ADD);

		IFeatureProvider fp = getConfigurationProvider().getFeatureProvider();
		//IFeatureProvider fp = addEntries.getFeature().getFeatureProvider();
		addEntry(manager, new ContextMenuEntry(new DeleteRouteFeature(fp), context), context, GEFActionConstants.GROUP_ADD, null);

		IContextMenuEntry[] otherEntries = new IContextMenuEntry[lastIndex];
		System.arraycopy(contextMenuEntries, 0, otherEntries, 0, lastIndex);

		addEntries(manager, otherEntries, context, GEFActionConstants.GROUP_REST, null);


		if (pes.length == 1) {
			extendCustomContext(pes[0], (CustomContext) context);
		}

		/*
		addActionToMenuIfAvailable(manager, UpdateAction.ACTION_ID, GEFActionConstants.GROUP_REST);
		addActionToMenuIfAvailable(manager, DeleteAction.ACTION_ID, GEFActionConstants.GROUP_REST);
		 */

	}

	private IConfigurationProvider getConfigurationProvider() {
		return this.configurationProvider;
	}

	private DiagramBehavior getBehavior() {
		return getConfigurationProvider().getDiagramBehavior();
	}

	private void addEntries(IMenuManager manager, IContextMenuEntry[] contextMenuEntries, ICustomContext context, String groupID,
			String textParentEntry) {

		for (int i = 0; i < contextMenuEntries.length; i++) {
			IContextMenuEntry cmEntry = contextMenuEntries[i];
			addEntry(manager, cmEntry, context, groupID, textParentEntry);
		}
	}

	protected void addEntry(IMenuManager manager, IContextMenuEntry cmEntry, ICustomContext context, String groupID,
			String textParentEntry) {
		String text = cmEntry.getText();
		IContextMenuEntry[] children = cmEntry.getChildren();
		if (children.length == 0) {
			IFeature feature = cmEntry.getFeature();
			if (feature instanceof ICustomFeature && feature.isAvailable(context)) {
				IAction action = new CustomAction((ICustomFeature) feature, context, getBehavior());
				if (textParentEntry != null) {
					text = textParentEntry + " " + text; //$NON-NLS-1$
				}
				action.setText(text);
				action.setDescription(cmEntry.getDescription());
				// TODO: pleacu - changed getImageDescriptorForId to getPlatformImageDescriptorForId
				ImageDescriptor image = GraphitiUi.getImageService().getPlatformImageDescriptorForId(cmEntry.getIconId());
				action.setImageDescriptor(image);
				appendContributionItem(manager, groupID, new ActionContributionItem(action));
			}
		} else {
			if (cmEntry.isSubmenu()) {

				MenuManager subMenu = new MenuManager(text);
				addEntries(subMenu, children, context, null, null);
				if (!subMenu.isEmpty()) {
					appendContributionItem(manager, groupID, subMenu);
				}
			} else {
				appendContributionItem(manager, groupID, new Separator());
				addEntries(manager, children, context, groupID, text);
				appendContributionItem(manager, groupID, new Separator());
			}
		}
	}


	private void appendContributionItem(IMenuManager manager, String groupID, IContributionItem contributionItem) {
		if (groupID != null) {
			manager.appendToGroup(groupID, contributionItem);
		} else {
			manager.add(contributionItem);
		}
	}

	// ====================== add single menu-entries =========================

	private void extendCustomContext(PictogramElement pe, CustomContext context) {
		Point location = getBehavior().getMouseLocation();
		int mX = location.x;
		int mY = location.y;
		context.setX(mX);
		context.setY(mY);

		if (pe instanceof Shape && !(pe instanceof Diagram)) {
			GraphicsAlgorithm ga = pe.getGraphicsAlgorithm();
			if (ga != null) {
				ILocation relLocation = Graphiti.getPeService().getLocationRelativeToDiagram((Shape) pe);
				int x = relLocation.getX();
				int y = relLocation.getY();
				int width = ga.getWidth();
				int height = ga.getHeight();

				if (mX > x && mX < x + width && mY > y && mY < y + height) {
					int relativeX = mX - x;
					int relativeY = mY - y;
					ILocationInfo locationInfo = Graphiti.getLayoutService().getLocationInfo((Shape) pe, relativeX, relativeY);
					context.setInnerPictogramElement(locationInfo.getShape());
					context.setInnerGraphicsAlgorithm(locationInfo.getGraphicsAlgorithm());
				}
			}
		}
	}

}
