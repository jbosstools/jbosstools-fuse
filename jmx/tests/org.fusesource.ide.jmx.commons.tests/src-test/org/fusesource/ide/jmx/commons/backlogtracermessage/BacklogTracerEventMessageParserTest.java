/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.fusesource.ide.jmx.commons.backlogtracermessage;

import java.io.InputStream;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BacklogTracerEventMessageParserTest {

	@Test
	public void testGetBacklogTracerEventMessage() throws Exception {
		InputStream stream = this.getClass().getResourceAsStream("backlogTracerEventMessage.xml");
		String xmlDump = org.apache.commons.io.IOUtils.toString(stream);

		BacklogTracerEventMessage backlogTracerEventMessage = new BacklogTracerEventMessageParser().getBacklogTracerEventMessage(xmlDump);

		assertThat(backlogTracerEventMessage.getMessage()).isNotNull();
	}

	@Test
	public void testGetBacklogTracerEventMessages() throws Exception {
		InputStream stream = this.getClass().getResourceAsStream("backlogTracerEventMessages.xml");
		String xmlDump = org.apache.commons.io.IOUtils.toString(stream);

		BacklogTracerEventMessages backlogTracerEventMessage = new BacklogTracerEventMessageParser().getBacklogTracerEventMessages(xmlDump);

		assertThat(backlogTracerEventMessage.getBacklogTracerEventMessages()).hasSize(4);
	}

}
