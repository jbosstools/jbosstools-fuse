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
package org.fusesource.ide.server.servicemix.core.runtime;

import org.fusesource.ide.server.karaf.core.runtime.IKarafRuntime;

/**
 * @author lhein
 */
public interface IServiceMixRuntime extends IKarafRuntime{
	
	static final String[] SMX_RUNTIME_TYPES_SUPPORTED = new String[] {
		  "org.fusesource.ide.smx.runtime.40"
		, "org.fusesource.ide.smx.runtime.42"
		, "org.fusesource.ide.smx.runtime.43"
		, "org.fusesource.ide.smx.runtime.44"
		, "org.fusesource.ide.smx.runtime.45"
	};
}
