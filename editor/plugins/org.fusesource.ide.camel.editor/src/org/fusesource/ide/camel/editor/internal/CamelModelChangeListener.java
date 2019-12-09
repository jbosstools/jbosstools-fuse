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

package org.fusesource.ide.camel.editor.internal;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.transaction.NotificationFilter;
import org.eclipse.emf.transaction.ResourceSetChangeEvent;
import org.eclipse.emf.transaction.ResourceSetListener;
import org.eclipse.emf.transaction.RollbackException;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.platform.IDiagramBehavior;
import org.eclipse.swt.widgets.Display;
import org.fusesource.ide.camel.editor.CamelDesignEditor;

/**
 * @author lhein
 */
public class CamelModelChangeListener  implements ResourceSetListener {

	private IDiagramBehavior diagramBehavior;

	public CamelModelChangeListener(CamelDesignEditor camelDesignEditor) {
		setDiagramEditor(camelDesignEditor.getDiagramBehavior());
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.emf.transaction.ResourceSetListener#resourceSetChanged(org.eclipse.emf.transaction.ResourceSetChangeEvent)
	 */
	@Override
	public void resourceSetChanged(ResourceSetChangeEvent event) {
		// Compute changed BOs.
		final Set<PictogramElement> changedBOs = new HashSet<PictogramElement>();
		List<Notification> notifications = event.getNotifications();
		for (Notification notification : notifications) {
			Object notifier = notification.getNotifier();
			if (!(notifier instanceof PictogramElement)) {
				continue;
			}
			changedBOs.add((PictogramElement) notifier);
		}

		final PictogramElement[] dirtyPes = getDiagramTypeProvider().getNotificationService().calculateRelatedPictogramElements(changedBOs.toArray());

		// Do nothing if no BO linked to the diagram changed.
		if (dirtyPes.length == 0) {
			return;
		}

		// Do an asynchronous update in the UI thread.
		Display.getDefault().asyncExec(new Runnable() {
			/*
			 * (non-Javadoc)
			 * @see java.lang.Runnable#run()
			 */
			@Override
			public void run() {

				if (getDiagramTypeProvider().isAutoUpdateAtRuntime()) { //&& getDiagramContainer().isDirty()) {
					// The notification service takes care of not only the
					// linked BOs but also asks the diagram provider about
					// related BOs.
					getDiagramTypeProvider().getNotificationService().updatePictogramElements(dirtyPes);
				} else {
					getDiagramTypeProvider().getDiagramBehavior().refresh();
				}
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.emf.transaction.ResourceSetListener#transactionAboutToCommit(org.eclipse.emf.transaction.ResourceSetChangeEvent)
	 */
	@Override
	public Command transactionAboutToCommit(ResourceSetChangeEvent event) throws RollbackException {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.emf.transaction.ResourceSetListener#getFilter()
	 */
	@Override
	public NotificationFilter getFilter() {
		return NotificationFilter.NOT_TOUCH;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.emf.transaction.ResourceSetListener#isAggregatePrecommitListener()
	 */
	@Override
	public boolean isAggregatePrecommitListener() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.emf.transaction.ResourceSetListener#isPostcommitOnly()
	 */
	@Override
	public boolean isPostcommitOnly() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.emf.transaction.ResourceSetListener#isPrecommitOnly()
	 */
	@Override
	public boolean isPrecommitOnly() {
		return false;
	}
	
	private IDiagramTypeProvider getDiagramTypeProvider() {
		return getDiagramBehavior().getDiagramContainer().getDiagramTypeProvider();
	}
	
	private IDiagramBehavior getDiagramBehavior() {
		return diagramBehavior;
	}

	private void setDiagramEditor(IDiagramBehavior diagramBehavior) {
		this.diagramBehavior = diagramBehavior;
	}
}
