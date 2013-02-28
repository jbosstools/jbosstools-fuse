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

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.jface.viewers.ViewerCell;
import org.fusesource.fabric.api.Container;
import org.fusesource.ide.fabric.FabricPlugin;



public class ContainerStatusLabelProvider extends StyledCellLabelProvider {

	@Override
	public void update(ViewerCell cell) {
		Object element = cell.getElement();
		ContainerViewBean bean = ContainerViewBean.toContainerViewBean(element);
		if (bean != null) {
			Container container = bean.container();
			boolean managed = container.isManaged();
			String image = "yellow-dot.png";
			String status = bean.getStatus();

			System.out.println("Container: " + container.getId() + " alive: " + container.isAlive() + " managed: " + container.isManaged()
					+ " pending: " + container.isProvisioningPending() + " complete: " + container.isProvisioningComplete() + " status: " + container.getProvisionStatus());

			if (!bean.isAlive()) {
				image = "gray-dot.png";
			}
			if (container.isProvisioningPending()) {
				//image = "pending.gif";
				image = "yellow-dot.png";
				managed = true;
			} else if (status != null) {
				String lowerStatus = status.toLowerCase();
				if (lowerStatus.startsWith("success") || lowerStatus.length() == 0) {
					if (bean.isAlive()) {
						image = "green-dot.png";
					} else {
						status = "stopped";
					}
					if (lowerStatus.length() == 0 && !managed) {
						if (bean.isRoot()) {
							status = "root";
						}
					}
				} else if (lowerStatus.startsWith("error")) {
					image = "red-dot.png";
				}
			}
			if (status != null) {
				Styler style = null;
				StyledString styledString = new StyledString(status, style);

				/*
				String fileName = bean.getFileName();
				if (fileName != null) {
					styledString.append(fileName, StyledString.COUNTER_STYLER);
				}
				 */
				cell.setText(styledString.toString());
				cell.setStyleRanges(styledString.getStyleRanges());
			}
			if (image != null) {
				cell.setImage(FabricPlugin.getDefault().getImage(image));
			}

		}
		super.update(cell);
	}





}
