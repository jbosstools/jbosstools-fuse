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

package org.fusesource.ide.server.tests.util;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Path;
import org.junit.Test;

public class ServerTest extends TestCase {

	@Test
	public void testKaraf2x() throws Exception {
		MockRuntimeCreationUtil.createRuntimeMock("org.fusesource.ide.karaf.runtime.2x", new Path("/var/tmp/karaf_latest_tst/"));
		System.out.println("DONE!");
	}
}
