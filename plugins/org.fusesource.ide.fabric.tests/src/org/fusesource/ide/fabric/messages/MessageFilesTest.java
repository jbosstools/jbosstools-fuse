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

package org.fusesource.ide.fabric.messages;

import org.junit.Test;

public class MessageFilesTest extends MessagesFilesTestSupport {

	@Test
	public void testTextFile() throws Exception {
		assertFile("sample.txt", true);
	}

	@Test
	public void testBinaryFile() throws Exception {
		assertFile("sample.gif", false);
	}

}
