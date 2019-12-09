/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.projecttemplates.util;

import org.eclipse.wst.server.core.IRuntime;
import org.fusesource.ide.projecttemplates.adopters.util.CamelDSLType;

/**
 * @author lhein
 */
public class NewFuseIntegrationProjectMetaData extends CommonNewProjectMetaData implements ICamelDSLTypeSupport {

	private IRuntime targetRuntime;
	private CamelDSLType dslType;
	
	/**
	 * @return the dslType
	 */
	@Override
	public CamelDSLType getDslType() {
		return this.dslType;
	}
	
	/**
	 * @return the targetRuntime
	 */
	public IRuntime getTargetRuntime() {
		return this.targetRuntime;
	}
	
	/**
	 * @param dslType the dslType to set
	 */
	@Override
	public void setDslType(CamelDSLType dslType) {
		this.dslType = dslType;
	}
	
	/**
	 * @param targetRuntime the targetRuntime to set
	 */
	public void setTargetRuntime(IRuntime targetRuntime) {
		this.targetRuntime = targetRuntime;
	}
	
}
