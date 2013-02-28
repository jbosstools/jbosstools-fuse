/*******************************************************************************
 * Copyright (c) 2006 Jeff Mesnil
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

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

package org.fusesource.ide.jmx.core;

import javax.management.MBeanOperationInfo;

import org.eclipse.core.runtime.Assert;
import org.fusesource.ide.commons.tree.HasName;


public class MBeanOperationInfoWrapper extends MBeanFeatureInfoWrapper implements HasName {

    private MBeanOperationInfo info;

    public MBeanOperationInfoWrapper(MBeanOperationInfo info,
            MBeanInfoWrapper wrapper) {
        super(wrapper);
        Assert.isNotNull(info);
        this.info = info;
    }

    public MBeanOperationInfo getMBeanOperationInfo() {
        return info;
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((info == null) ? 0 : info.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof MBeanOperationInfoWrapper))
            return false;
        final MBeanOperationInfoWrapper other = (MBeanOperationInfoWrapper) obj;
        if (info == null) {
            if (other.info != null)
                return false;
        } else if (!info.equals(other.info))
            return false;
        return true;
    }

	@Override
	public String getName() {
		return MBeanUtils.prettySignature(getMBeanOperationInfo());
	}
    
    
}
