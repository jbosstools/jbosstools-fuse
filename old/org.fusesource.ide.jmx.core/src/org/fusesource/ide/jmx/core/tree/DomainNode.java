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

package org.fusesource.ide.jmx.core.tree;

import org.eclipse.swt.graphics.Image;
import org.fusesource.ide.commons.tree.HasName;
import org.fusesource.ide.commons.tree.Node;
import org.fusesource.ide.commons.ui.ImageProvider;
import org.fusesource.ide.jmx.core.JMXActivator;


public class DomainNode extends Node implements HasName, ImageProvider {

    private String domain;

    DomainNode(Node root, String domain) {
        super(root);
        this.domain = domain;
    }

    public String getDomain() {
        return domain;
    }

    @Override
    public String toString() {
        return "DomainNode[domain=" + domain + "]"; //$NON-NLS-1$ //$NON-NLS-2$
    }

	public String getName() {
		return getDomain();
	}

	@Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((domain == null) ? 0 : domain.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final DomainNode other = (DomainNode) obj;
        if (domain == null) {
            if (other.domain != null)
                return false;
        } else if (!domain.equals(other.domain))
            return false;
        return true;
    }

    public int compareTo(Object o) {
    	String otherText = null;
    	if (o instanceof DomainNode) {
            DomainNode other = (DomainNode) o;
            otherText = other.domain;
    	} else {
    		otherText = o.toString();
    	}
        return domain.compareTo(otherText);
    }

	@Override
	public Image getImage() {
		return JMXActivator.getDefault().getImage("package.png");
	}

}
