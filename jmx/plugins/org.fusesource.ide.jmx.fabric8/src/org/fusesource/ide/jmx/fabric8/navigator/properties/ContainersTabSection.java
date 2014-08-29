/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.jmx.fabric8.navigator.properties;

import java.util.Collections;
import java.util.List;

import org.fusesource.ide.jmx.fabric8.navigator.ContainersNode;
import org.fusesource.ide.jmx.fabric8.navigator.Fabric8Node;

/**
 * ContainersTabSection
 */
public class ContainersTabSection extends AbstractContainersTabSection {
	
	protected Fabric8Node getFabricForNode(Object node) {
		return ((ContainersNode) node).getFabric();
	}

	protected List<?> getPropertySourcesForNode(Object node) {
		return node == null ? Collections.emptyList() : ((ContainersNode) node)
				.getPropertySourceList();
	}

//	protected CreateSshContainerAction createSshContainerAction(Object current) {
//		return new CreateSshContainerAction(getFabric());
//	}
//
//	protected CreateChildContainerAction createChildContainerAction(
//			Object current) {
//		return new CreateChildContainerAction(getFabric());
//	}
}