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


public class PropertyNode extends Node implements HasName, ImageProvider {
	private String key;

	private String value;

	PropertyNode(Node parent, String key, String value) {
		super(parent);
		this.key = key;
		this.value = value;
	}

	public String getKey() {
	    return key;
	}

	public String getValue() {
	    return value;
	}

	@Override
	public String toString() {
		return "PropertyNode[" + key + "=" + value + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public int compareTo(Object o) {
		PropertyNode other = (PropertyNode) o;
		if (key.equals(other.key)) {
		    return value.compareTo(other.value);
		}
		return key.compareTo(other.key);
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((key == null) ? 0 : key.hashCode());
		result = PRIME * result + ((value == null) ? 0 : value.hashCode());
        result = PRIME * result + ((parent == null) ? 0 : parent.hashCode());
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
		final PropertyNode other = (PropertyNode) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
        if (parent == null) {
            if (other.parent != null)
                return false;
        } else if (!parent.equals(other.parent))
            return false;
		return true;
	}

	public String getName() {
		return getValue();
	}

	@Override
	public Image getImage() {
		return JMXActivator.getDefault().getImage("mbean_folder.png");
	}
}
