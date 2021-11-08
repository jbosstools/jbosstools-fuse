/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.launcher.debug.model.values;

import org.eclipse.debug.core.DebugException;
import org.fusesource.ide.launcher.debug.model.CamelDebugFacade;
import org.fusesource.ide.launcher.debug.model.CamelStackFrame;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class CamelDebuggerValueTest {

	@Mock
	private CamelStackFrame stackFrame;
	@Mock
	private CamelDebugFacade debugger;

	@Test
	public void testCreation() throws DebugException {
		doReturn(debugger).when(stackFrame).getDebugger();

		CamelDebuggerValue camelDebuggerValue = new CamelDebuggerValue(null, stackFrame, String.class);

		assertThat(camelDebuggerValue.hasVariables()).isTrue();
		assertThat(camelDebuggerValue.getVariables()).hasSize(5);
	}

}
