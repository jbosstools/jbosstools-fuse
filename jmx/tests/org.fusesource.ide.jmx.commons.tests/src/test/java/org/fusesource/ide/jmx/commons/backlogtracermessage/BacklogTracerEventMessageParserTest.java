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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Collectors;

import org.junit.Test;

public class BacklogTracerEventMessageParserTest {

	@Test
	public void testGetBacklogTracerEventMessage_messsageParsed() throws Exception {
		String xmlDump = backlogTracerEventMessageDump();

		BacklogTracerEventMessage backlogTracerEventMessage = new BacklogTracerEventMessageParser().getBacklogTracerEventMessage(xmlDump);

		assertThat(backlogTracerEventMessage.getMessage()).isNotNull();
	}

	@Test
	public void testGetBacklogTracerEventMessage_timestampParsed() throws Exception {
		String xmlDump = backlogTracerEventMessageDump();

		BacklogTracerEventMessage backlogTracerEventMessage = new BacklogTracerEventMessageParser().getBacklogTracerEventMessage(xmlDump);

		final Date dateToCheck = new SimpleDateFormat(DateAdapter.DATE_FORMAT).parse("2016-04-01T17:09:44.635+0200");
		assertThat(backlogTracerEventMessage.getTimestamp()).hasSameTimeAs(dateToCheck);
	}

	@Test
	public void testGetBacklogTracerEventMessage_uidParsed() throws Exception {
		String xmlDump = backlogTracerEventMessageDump();

		BacklogTracerEventMessage backlogTracerEventMessage = new BacklogTracerEventMessageParser().getBacklogTracerEventMessage(xmlDump);

		assertThat(backlogTracerEventMessage.getUid()).isEqualTo(5l);
	}

	@Test
	public void testGetBacklogTracerEventMessage_exchangeIdParsed() throws Exception {
		String xmlDump = backlogTracerEventMessageDump();

		BacklogTracerEventMessage backlogTracerEventMessage = new BacklogTracerEventMessageParser().getBacklogTracerEventMessage(xmlDump);

		assertThat(backlogTracerEventMessage.getExchangeId()).isEqualTo("ID-DESKTOP-9NT300B-61151-1459523299086-0-40");
	}

	/**
	 * @return
	 * @throws IOException
	 */
	private String backlogTracerEventMessageDump() throws IOException {
		InputStream stream = this.getClass().getResourceAsStream("backlogTracerEventMessage.xml");
		return new BufferedReader(new InputStreamReader(stream)).lines()
				   .parallel().collect(Collectors.joining("\n"));
	}


	@Test
	public void testGetBacklogTracerEventMessages() throws Exception {
		InputStream stream = this.getClass().getResourceAsStream("backlogTracerEventMessages.xml");
		String xmlDump = new BufferedReader(new InputStreamReader(stream)).lines()
				   .parallel().collect(Collectors.joining("\n"));

		BacklogTracerEventMessages backlogTracerEventMessage = new BacklogTracerEventMessageParser().getBacklogTracerEventMessages(xmlDump);

		assertThat(backlogTracerEventMessage.getBacklogTracerEventMessages()).hasSize(4);
	}

}
