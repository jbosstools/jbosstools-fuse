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

package org.fusesource.ide.fabric8.ui;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.jface.viewers.ISelection;
import org.fusesource.ide.commons.ui.Selections;
import org.fusesource.ide.fabric8.core.dto.ProfileStatusDTO;


/**
 * @author lhein
 *
 */
public class RequirementsPropertyTester extends PropertyTester {
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.expressions.IPropertyTester#test(java.lang.Object, java.lang.String, java.lang.Object[], java.lang.Object)
	 */
	@Override
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		
		if (receiver instanceof ISelection) {
			Object selection = Selections.getFirstSelection((ISelection)receiver);
			if(selection instanceof ProfileStatusDTO){
				ProfileStatusDTO status = (ProfileStatusDTO)selection;
				if(property.equals("requirementsNotEmpty")) {
					return !status.getRequirements().checkIsEmpty();
				}
			}			
		}
					
		return false;
	}
}
