/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.jmx.commons.backlogtracermessage;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.fusesource.ide.jmx.commons.Activator;

/**
 * @author Aurelien Pupier
 *
 */
public class BacklogTracerEventMessageParser {

	/**
	 * creates the backlog tracer event message for a given xml dump
	 * 
	 * @param xmlDump
	 *            the xml dump of the message
	 * @return the message object or null on errors
	 */
	public BacklogTracerEventMessage getBacklogTracerEventMessage(String xmlDump) {
		return (BacklogTracerEventMessage) getUnmarshalledObject(xmlDump);
	}

	/**
	 * creates the backlog tracer event message for a given xml dump
	 * 
	 * @param xmlDump
	 *            the xml dump of the message
	 * @return the messages object or null on errors
	 */
	public BacklogTracerEventMessages getBacklogTracerEventMessages(String xmlDump) {
		return (BacklogTracerEventMessages) getUnmarshalledObject(xmlDump);
	}

	private Object getUnmarshalledObject(String xmlDump) {
		try {
			JAXBContext context = JAXBContext.newInstance(BacklogTracerEventMessages.class, BacklogTracerEventMessage.class, Message.class, Header.class);
			Unmarshaller um = context.createUnmarshaller();
			return um.unmarshal(new StringReader(xmlDump));
		} catch (JAXBException ex) {
			Activator.getLogger().error(ex);
		}
		return null;
	}

}
