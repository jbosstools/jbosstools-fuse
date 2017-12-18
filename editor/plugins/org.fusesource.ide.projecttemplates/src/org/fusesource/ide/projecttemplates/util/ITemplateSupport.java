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
package org.fusesource.ide.projecttemplates.util;

import org.fusesource.ide.projecttemplates.adopters.AbstractProjectTemplate;

/**
 * @author lheinema
 */
public interface ITemplateSupport {
	
	/**
	 * returns the template to be used in the project creation
	 * 
	 * @return	the template to use or null
	 */
	AbstractProjectTemplate getTemplate();
	
	/**
	 * sets the template to be used in the project creation
	 * 
	 * @param template	the template to use
	 */
	void setTemplate(AbstractProjectTemplate template);
}
