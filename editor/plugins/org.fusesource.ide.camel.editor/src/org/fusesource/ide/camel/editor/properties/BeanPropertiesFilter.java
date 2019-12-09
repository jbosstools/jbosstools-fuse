/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.editor.properties;

import org.eclipse.jface.viewers.IFilter;
import org.fusesource.ide.camel.editor.utils.GlobalConfigUtils;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelBean;

/**
 * @author bfitzpat
 */
public class BeanPropertiesFilter implements IFilter {

	private GlobalConfigUtils globalConfigUtils = null;

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IFilter#select(java.lang.Object)
	 */
	@Override
	public boolean select(Object toTest) {
		AbstractCamelModelElement ep = getSelectedObject(toTest);
		return ep != null && CamelBean.BEAN_NODE.equalsIgnoreCase(ep.getNodeTypeId());
	}

	protected AbstractCamelModelElement getSelectedObject(Object toTest) {
		if (globalConfigUtils == null) {
			globalConfigUtils = new GlobalConfigUtils();
		}
		if (toTest instanceof CamelBean) {
			CamelBean testBean = (CamelBean) toTest;
			boolean isSAPClass = "org.fusesource.camel.component.sap.SapConnectionConfiguration".equals(testBean.getClassName()); //$NON-NLS-1$
			if (!globalConfigUtils.isSAPExtInstalled() || !isSAPClass) {
				return testBean;
			}
		}
		return null;
	}
}
