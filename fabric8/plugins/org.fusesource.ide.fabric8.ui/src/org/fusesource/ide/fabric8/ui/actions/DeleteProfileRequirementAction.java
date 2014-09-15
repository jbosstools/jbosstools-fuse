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

package org.fusesource.ide.fabric8.ui.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.views.properties.PropertySheet;
import org.fusesource.ide.commons.ui.Selections;
import org.fusesource.ide.fabric8.core.dto.ProfileStatusDTO;
import org.fusesource.ide.fabric8.core.dto.RequirementsDTO;
import org.fusesource.ide.fabric8.ui.FabricPlugin;
import org.fusesource.ide.fabric8.ui.navigator.Fabric;
import org.fusesource.ide.fabric8.ui.navigator.properties.FabricTabViewPage;


/**
 * @author lhein
 */
public class DeleteProfileRequirementAction extends AbstractHandler {
	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection sel = HandlerUtil.getCurrentSelection(event);
		if (sel instanceof IStructuredSelection) {
			IStructuredSelection isel = (IStructuredSelection)sel;
			for (Object o : isel.toList()) {
				if (o instanceof ProfileStatusDTO) {
					deleteRequirementsForProfile(event, ((ProfileStatusDTO)o).getProfile());
				}
			}
		}
		return null;
	}

	private void deleteRequirementsForProfile(ExecutionEvent event, String profileId) {
		Fabric fabricNode = getSelectedFabric();
		
		RequirementsDTO reqs = fabricNode.getFabricService().getRequirements();
		
		try {
			if (reqs.findProfileRequirements(profileId) != null) {
				reqs.removeProfileRequirements(profileId);
				return;
			}
		} finally {
			try {
				fabricNode.getFabricService().setRequirements(reqs);
			} finally {
				IWorkbenchPart wp = HandlerUtil.getActivePart(event);
				if (wp instanceof PropertySheet) {
					PropertySheet ps = (PropertySheet)wp;
					if (ps.getCurrentPage() instanceof FabricTabViewPage) {
						FabricTabViewPage ftvp = (FabricTabViewPage)ps.getCurrentPage();
						ftvp.refresh();		
					}
				}
			}
		}
	}		
	
	private Fabric getSelectedFabric() {
		Fabric f = null;
		
		Object selectedFabricObject = Selections.getFirstSelection(FabricPlugin.getFabricNavigator().getCommonViewer().getSelection());
		if (selectedFabricObject != null && selectedFabricObject instanceof Fabric) {
			f = (Fabric)selectedFabricObject;
		}
		
		return f;
	}
}
