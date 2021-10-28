/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.launcher.debug.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anySetOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;

import java.util.HashSet;
import java.util.Set;

import javax.management.ObjectInstance;
import javax.management.ObjectName;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CamelDebugFacadeTest {
	
	@Mock
	private CamelDebugFacade cdf;
	
	@Before
	public void setup() {
		doCallRealMethod().when(cdf).findDebugger(anyString(), anySetOf(ObjectInstance.class));
	}

	@Test
	public void testRetrieveDebuggerWith2CamelContexts() throws Exception {
		Set<ObjectInstance> mbeans = new HashSet<>();
		ObjectInstance o1 = new ObjectInstance("org.apache.camel:context=cbr-example-context,type=tracer,name=BacklogDebugger", null);
		mbeans.add(o1);
		ObjectInstance o2 = new ObjectInstance("org.apache.camel:context=cbr-example-context-2,type=tracer,name=BacklogDebugger", null);
		mbeans.add(o2);
		ObjectName found = cdf.findDebugger("cbr-example-context-2", mbeans);
		assertThat(found.getKeyProperty(CamelDebugFacade.KEY_PROPERTY_CONTEXT_FOR_DEBUGGER_MBEAN)).isEqualTo("cbr-example-context-2");
	}
	
	@Test
	public void testRetrieveDebuggerWith2CamelContextsFromKaraf() throws Exception {
		Set<ObjectInstance> mbeans = new HashSet<>();
		ObjectInstance o1 = new ObjectInstance("org.apache.camel:context=com.mycompany.camel-cbr-example-context,type=tracer,name=BacklogDebugger", null);
		mbeans.add(o1);
		ObjectInstance o2 = new ObjectInstance("org.apache.camel:context=com.mycompany.camel-cbr-example-context-2,type=tracer,name=BacklogDebugger", null);
		mbeans.add(o2);
		ObjectName found = cdf.findDebugger("cbr-example-context-2", mbeans);
		assertThat(found.getKeyProperty(CamelDebugFacade.KEY_PROPERTY_CONTEXT_FOR_DEBUGGER_MBEAN)).contains("cbr-example-context-2");
	}
	
}
