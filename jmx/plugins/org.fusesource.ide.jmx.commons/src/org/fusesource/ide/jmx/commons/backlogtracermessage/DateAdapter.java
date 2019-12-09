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

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author Aurelien Pupier
 *
 */
public class DateAdapter extends XmlAdapter<String, Date> {

	protected static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
	private SimpleDateFormat dateFormat;

	public DateAdapter() {
		this.dateFormat = new SimpleDateFormat(DATE_FORMAT);
	}

	@Override
	public Date unmarshal(String v) throws Exception {
		return dateFormat.parse(v);
	}

	@Override
	public String marshal(Date v) throws Exception {
		return dateFormat.format(v);
	}


}
