/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.reddeer.wizard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.reddeer.core.reference.ReferencedComposite;
import org.eclipse.reddeer.jface.wizard.WizardPage;
import org.eclipse.reddeer.swt.api.TreeItem;
import org.eclipse.reddeer.swt.impl.link.DefaultLink;
import org.eclipse.reddeer.swt.impl.tree.DefaultTree;
import org.eclipse.reddeer.swt.impl.tree.DefaultTreeItem;

/**
 * Represents the last page of "New Fuse Integration Project" wizard
 * 
 * @author tsedmik
 */
public class NewFuseIntegrationProjectWizardAdvancedPage extends WizardPage {

	private static final String MORE_EXAMPLES_LINK = "Where can I find more examples?";

	public NewFuseIntegrationProjectWizardAdvancedPage(ReferencedComposite referencedComposite) {
		super(referencedComposite);
	}

	public List<String[]> getAllAvailableTemplates() {
		List<String[]> templates = new ArrayList<>();
		for (TreeItem item : new DefaultTree(this).getAllItems()) {
			if (item.getItems().isEmpty()) {
				templates.add(item.getPath());
			}
		}
		return templates;
	}

	public void selectTemplate(String... path) {
		new DefaultTreeItem(new DefaultTree(this), path).select();
	}
	
	public void selectMoreExamplesLink() {
		new DefaultLink(this, MORE_EXAMPLES_LINK).click();
	}
}
