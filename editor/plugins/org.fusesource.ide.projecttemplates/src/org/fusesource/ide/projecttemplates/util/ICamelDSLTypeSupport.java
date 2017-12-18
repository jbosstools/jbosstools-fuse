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

import org.fusesource.ide.projecttemplates.adopters.util.CamelDSLType;

/**
 * @author lheinema
 */
public interface ICamelDSLTypeSupport {
	
	/**
	 * returns the used camel dsl type
	 * 
	 * @return
	 */
	CamelDSLType getDslType();
	
	/**
	 * sets the camel dsl type
	 * 
	 * @param dslType
	 */
	void setDslType(CamelDSLType dslType);
}
