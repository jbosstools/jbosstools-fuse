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
package org.fusesource.ide.project;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IProject;
import org.fusesource.ide.project.providers.CamelVirtualFolder;

/**
 * @author lhein
 */
public class CamelContextFilesCountTester extends PropertyTester {

	/**
	 * 
	 */
	public CamelContextFilesCountTester() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.expressions.IPropertyTester#test(java.lang.Object, java.lang.String, java.lang.Object[], java.lang.Object)
	 */
	@Override
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {

		if(receiver instanceof IProject){
			IProject project = (IProject)receiver;
			if(property.equals("camelContextFileCount")) {
				CamelVirtualFolder folder = new CamelVirtualFolder(project);
				folder.populateChildren();
				return folder.getCamelFiles().size()>0;
			}
		}		
		return false;
	}
}
