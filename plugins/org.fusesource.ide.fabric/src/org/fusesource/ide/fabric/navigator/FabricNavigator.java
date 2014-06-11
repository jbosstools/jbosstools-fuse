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
package org.fusesource.ide.fabric.navigator;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.part.ResourceTransfer;
import org.fusesource.ide.commons.Viewers;
import org.fusesource.ide.commons.tree.HasViewer;
import org.fusesource.ide.commons.tree.RefreshableUI;
import org.fusesource.ide.commons.ui.UIHelper;
import org.fusesource.ide.commons.ui.drop.DelegateDropListener;
import org.fusesource.ide.fabric.navigator.cloud.CloudsNode;
import org.jboss.tools.jmx.ui.internal.actions.RefreshAction;
import org.jboss.tools.jmx.ui.internal.views.navigator.JMXNavigator;

public class FabricNavigator extends JMXNavigator {

	public static String ID = UIHelper.ID_FABRIC_EXPORER;

	public final class RefreshableUIImplementation implements RefreshableUI, HasViewer {
		public void fireRefresh(final Object node, final boolean full) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					getCommonViewer().refresh(node, full);
				}
			});
		}

		public void fireRefresh() {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					Viewers.refresh(getCommonViewer());
				}
			});
		}

		public Viewer getViewer() {
			return getCommonViewer();
		}
	}
	
	protected RefreshableUI refreshableUI = new RefreshableUIImplementation();
	
	private Fabrics fabrics = new Fabrics(refreshableUI);
	private CloudsNode clouds = new CloudsNode(refreshableUI);
	
	@Override
	protected IAdaptable getInitialInput() {
		return this;
	}


	@Override
	public void createPartControl(Composite aParent) {
		super.createPartControl(aParent);
		CommonViewer viewer = getCommonViewer();
		viewer.setExpandedElements(new Object[] {clouds, fabrics});
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.navigator.CommonNavigator#createCommonViewer(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected CommonViewer createCommonViewer(Composite aParent) {
		return new CommonViewer(getViewSite().getId(), aParent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL) {

			@Override
			public void addDragSupport(int operations,
					Transfer[] transferTypes,
					DragSourceListener listener) {
				super.addDragSupport(operations, transferTypes, listener);
			}

			@Override
			public void addDropSupport(int operations,
					Transfer[] transferTypes,
					DropTargetListener listener) {

				Transfer[] newTransferTypes = new Transfer[] {
						LocalSelectionTransfer.getTransfer(),
						FileTransfer.getInstance(),
						ResourceTransfer.getInstance(),
						TextTransfer.getInstance() };

				super.addDropSupport(operations, newTransferTypes, new DelegateDropListener(this, listener));
			}
		};
	}

	public CloudsNode getCloudsNode() {
		return clouds;
	}

	public Fabrics getFabrics() {
		return fabrics;
	}

	@Override
	public void fillActionBars() {
		// queryContribution = new QueryContribution(this);
		// getViewSite().getActionBars().getToolBarManager().add(queryContribution);
		IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();
		//toolBarManager.add(new NewConnectionAction(this));
		toolBarManager.add(fabrics.getAddAction());
		toolBarManager.add(new RefreshAction(getViewSite().getId()));
		toolBarManager.add(new Separator());
		getViewSite().getActionBars().updateActionBars();
	}
}
