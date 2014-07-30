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
package org.fusesource.ide.server.fabric8.core.runtime;

import org.fusesource.ide.server.fabric8.core.util.IFabric8ToolingConstants;
import org.fusesource.ide.server.karaf.core.runtime.IKarafRuntime;

/**
 * @author lhein
 */
public interface IFabric8Runtime extends IKarafRuntime{
	
	static final String[] FABRIC8_RUNTIME_TYPES_SUPPORTED = new String[] {
		IFabric8ToolingConstants.RUNTIME_FABRIC8_11
	};
}
