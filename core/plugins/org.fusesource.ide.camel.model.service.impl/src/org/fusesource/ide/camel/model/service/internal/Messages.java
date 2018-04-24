/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.model.service.internal;

import org.eclipse.osgi.util.NLS;

/**
 * @author lheinema
 *
 */
public class Messages extends NLS {
	static {
		NLS.initializeMessages("org.fusesource.ide.camel.model.service.internal.messages", Messages.class);
	}
	
	public static String loadingCamelModel;
}
