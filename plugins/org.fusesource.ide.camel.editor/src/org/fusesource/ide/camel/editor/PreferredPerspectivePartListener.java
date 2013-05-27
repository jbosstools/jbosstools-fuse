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

package org.fusesource.ide.camel.editor;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

/**
 * @author lhein
 */
public class PreferredPerspectivePartListener implements IStartup,
		IPartListener {
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPartListener#partActivated(org.eclipse.ui.IWorkbenchPart)
	 */
	@Override
	public void partActivated(IWorkbenchPart part) {
		refresh(part);
	}
	
	public static void refresh(final IWorkbenchPart part) {
        if (!(part instanceof IPrefersPerspective)) {
            return;
        }

        final IWorkbenchWindow workbenchWindow = part.getSite().getPage().getWorkbenchWindow();

        IPerspectiveDescriptor activePerspective = workbenchWindow.getActivePage().getPerspective();
        final String preferredPerspectiveId = ((IPrefersPerspective) part)
                .getPreferredPerspectiveId();

        if (preferredPerspectiveId == null) {
            return;
        }

        if (activePerspective == null || !activePerspective.getId().equals(preferredPerspectiveId)) {
            // Switching of the perspective is delayed using Display.asyncExec
            // because switching the perspective while the workbench is
            // activating parts might cause conflicts.
            Display.getCurrent().asyncExec(new Runnable() {
            	/*
            	 * (non-Javadoc)
            	 * @see java.lang.Runnable#run()
            	 */
            	@Override
                public void run() {
                    try {
                        workbenchWindow.getWorkbench().showPerspective(preferredPerspectiveId,
                                workbenchWindow);
                    } catch (WorkbenchException e) {
                    }
                }

            });
        }
    }

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPartListener#partBroughtToTop(org.eclipse.ui.IWorkbenchPart)
	 */
	@Override
	public void partBroughtToTop(IWorkbenchPart arg0) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPartListener#partClosed(org.eclipse.ui.IWorkbenchPart)
	 */
	@Override
	public void partClosed(IWorkbenchPart arg0) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPartListener#partDeactivated(org.eclipse.ui.IWorkbenchPart)
	 */
	@Override
	public void partDeactivated(IWorkbenchPart arg0) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPartListener#partOpened(org.eclipse.ui.IWorkbenchPart)
	 */
	@Override
	public void partOpened(IWorkbenchPart arg0) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IStartup#earlyStartup()
	 */
	@Override
	public void earlyStartup() {
		Display.getDefault().asyncExec(new Runnable() {
			/*
			 * (non-Javadoc)
			 * @see java.lang.Runnable#run()
			 */
			@Override
            public void run() {
                try {
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                            .addPartListener(PreferredPerspectivePartListener.this);
                } catch (Exception e) {
                }
            }
        });
	}
}
