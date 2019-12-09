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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Aurelien Pupier
 *
 */
@XmlRootElement(name = "backlogTracerEventMessages")
@XmlAccessorType(XmlAccessType.FIELD)
public class BacklogTracerEventMessages {

	@XmlElement(name = "backlogTracerEventMessage", required = false)
	private List<BacklogTracerEventMessage> backlogTracerEventMessages = new ArrayList<>();

	public BacklogTracerEventMessages() {
	}

	public List<BacklogTracerEventMessage> getBacklogTracerEventMessages() {
		return backlogTracerEventMessages;
	}

	public void setBacklogTracerEventMessages(List<BacklogTracerEventMessage> backlogTracerEventMessages) {
		this.backlogTracerEventMessages = backlogTracerEventMessages;
	}

}
