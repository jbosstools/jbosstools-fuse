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

/**
 * @author lheinema
 */
public interface ICamelSupport {
	
	/**
	 * returns the camel version to be used
	 * 
	 * @return	the camel version
	 */
	String getCamelVersion();
	
	/**
	 * sets the camel version to be used
	 * 
	 * @param camelVersion
	 */
	void setCamelVersion(String camelVersion);
}
