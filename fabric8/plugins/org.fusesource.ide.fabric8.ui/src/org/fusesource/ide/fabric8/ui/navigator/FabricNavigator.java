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
package org.fusesource.ide.fabric8.ui.navigator;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.part.ResourceTransfer;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.fusesource.ide.commons.Viewers;
import org.fusesource.ide.commons.tree.HasViewer;
import org.fusesource.ide.commons.tree.RefreshableUI;
import org.fusesource.ide.commons.ui.Selections;
import org.fusesource.ide.commons.ui.actions.HasDoubleClickAction;
import org.fusesource.ide.commons.ui.drop.DelegateDropListener;
import org.fusesource.ide.fabric8.ui.navigator.cloud.CloudsNode;
import org.fusesource.ide.fabric8.ui.perspective.FabricPerspective;

public class FabricNavigator extends CommonNavigator implements
		ITabbedPropertySheetPageContributor {

	public static String ID = FabricPerspective.ID_FABRIC_EXPORER;

	public final class RefreshableUIImplementation implements RefreshableUI,
			HasViewer {
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

	private RefreshableUI refreshableUI = new RefreshableUIImplementation();
	private Fabrics fabrics = new Fabrics(refreshableUI);
	private CloudsNode clouds = new CloudsNode(refreshableUI);

	@Override
	protected IAdaptable getInitialInput() {
		return this;
	}

	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		if (adapter == IPropertySheetPage.class) {
			return new TabbedPropertySheetPage(this);
		}
		return super.getAdapter(adapter);
	}

	@Override
	public String getContributorId() {
		return ID;
	}

	@Override
	public void createPartControl(Composite aParent) {
		super.createPartControl(aParent);
		CommonViewer viewer = getCommonViewer();
		viewer.setExpandedElements(new Object[] { clouds, fabrics });
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				if (!event.getSelection().isEmpty()) {
					Object o = Selections.getFirstSelection(event.getSelection());
					if (o instanceof HasDoubleClickAction) {
						final Action a = ((HasDoubleClickAction)o).getDoubleClickAction();
						Job job = new Job("Execute double click action...") {
							/* (non-Javadoc)
							 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
							 */
							@Override
							protected IStatus run(IProgressMonitor monitor) {
								if (a.isEnabled()) a.run();
								return Status.OK_STATUS;
							}
						};
						job.schedule();
					}
				}
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.navigator.CommonNavigator#createCommonViewer(org.eclipse
	 * .swt.widgets.Composite)
	 */
	@Override
	protected CommonViewer createCommonViewer(Composite aParent) {
		return new CommonViewer(getViewSite().getId(), aParent, SWT.SINGLE
				| SWT.H_SCROLL | SWT.V_SCROLL) {

			@Override
			public void addDragSupport(int operations,
					Transfer[] transferTypes, DragSourceListener listener) {
				super.addDragSupport(operations, transferTypes, listener);
			}

			@Override
			public void addDropSupport(int operations,
					Transfer[] transferTypes, DropTargetListener listener) {

				Transfer[] newTransferTypes = new Transfer[] {
						LocalSelectionTransfer.getTransfer(),
						FileTransfer.getInstance(),
						ResourceTransfer.getInstance(),
						TextTransfer.getInstance() };

				super.addDropSupport(operations, newTransferTypes,
						new DelegateDropListener(this, listener));
			}
		};
	}

	public CloudsNode getCloudsNode() {
		return clouds;
	}

	public Fabrics getFabrics() {
		return fabrics;
	}
}
