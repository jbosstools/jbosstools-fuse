package org.fusesource.ide.fabric;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.jface.viewers.ISelection;
import org.fusesource.fabric.api.ProfileStatus;
import org.fusesource.ide.commons.ui.Selections;


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
			if(selection instanceof ProfileStatus){
				ProfileStatus status = (ProfileStatus)selection;
				if(property.equals("requirementsNotEmpty")) {
					return !status.requirements().isEmpty();
				}
			}			
		}
					
		return false;
	}
}
