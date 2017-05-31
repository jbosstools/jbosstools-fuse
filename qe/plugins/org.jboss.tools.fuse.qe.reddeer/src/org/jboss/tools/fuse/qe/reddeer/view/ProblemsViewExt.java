/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.qe.reddeer.view;

import java.util.List;

import org.jboss.reddeer.eclipse.ui.problems.ProblemsView;
import org.jboss.reddeer.swt.api.TreeItem;
import org.jboss.reddeer.swt.impl.tree.DefaultTree;

public class ProblemsViewExt extends ProblemsView {

	public void doubleClickProblem (String name, ProblemType type) {
		activate();
		List<TreeItem> items = new DefaultTree().getItems();
		for (TreeItem item : items) {
			if (type.equals(ProblemType.ERROR) && item.getText().startsWith("Errors")) {
				List<TreeItem> tmpList = item.getItems();
				for (TreeItem tmp : tmpList) {
					if (tmp.getText().contains(name)) {
						tmp.doubleClick();
						break;
					}
				}
				break;
			}
		}
	}
}
