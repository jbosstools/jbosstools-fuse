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

import java.util.ArrayList;
import java.util.List;

import org.fusesource.ide.camel.model.service.core.jmx.camel.IBacklogTracerHeader;
import org.fusesource.ide.jmx.commons.backlogtracermessage.Header;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CamelHeadersValueTest {

	@Test
	public void testGetValueString() throws Exception {
		List<IBacklogTracerHeader> headers = new ArrayList<>();
		IBacklogTracerHeader header = new Header();
		header.setKey("headerkey1");
		header.setValue("headervalue1");
		headers.add(header);

		IBacklogTracerHeader header2 = new Header();
		header2.setKey("headerkey2");
		header2.setValue("headervalue2");
		headers.add(header2);
		assertThat(new CamelHeadersValue(null, headers, String.class, null).getValueString()).isEqualTo("headerkey1 = headervalue1\nheaderkey2 = headervalue2");
	}

}
