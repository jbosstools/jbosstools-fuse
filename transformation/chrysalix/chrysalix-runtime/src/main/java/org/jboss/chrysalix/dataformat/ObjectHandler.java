/******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: JBoss by Red Hat - Initial implementation.
 *****************************************************************************/
package org.jboss.chrysalix.dataformat;

import org.jboss.chrysalix.DataFormatHandler;
import org.jboss.chrysalix.Node;


public class ObjectHandler implements DataFormatHandler {

	/**
	 * {@inheritDoc}
	 *
	 * @see org.jboss.chrysalix.DataFormatHandler#load(org.jboss.chrysalix.Node)
	 */
	@Override
	public void load(Node fileNode) {}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.jboss.chrysalix.DataFormatHandler#save(org.jboss.chrysalix.Node)
	 */
	@Override
	public void save(Node fileNode) {}
}
