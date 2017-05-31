/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.qe.reddeer.runtime.impl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.jboss.tools.fuse.qe.reddeer.runtime.Namespaces;

@XmlRootElement(name = "destination", namespace = Namespaces.SOA_REQ)
@XmlAccessorType(XmlAccessType.FIELD)
public class SAPDestination {

	@XmlAttribute(name = "name")
	private String name;

	@XmlElement(name = "ashost", namespace = Namespaces.SOA_REQ)
	private String ashost;

	@XmlElement(name = "sysnr", namespace = Namespaces.SOA_REQ)
	private String sysnr;

	@XmlElement(name = "client", namespace = Namespaces.SOA_REQ)
	private String client;

	@XmlElement(name = "passwd", namespace = Namespaces.SOA_REQ)
	private String passwd;

	@XmlElement(name = "password", namespace = Namespaces.SOA_REQ)
	private String password;

	@XmlElement(name = "user", namespace = Namespaces.SOA_REQ)
	private String user;

	@XmlElement(name = "userName", namespace = Namespaces.SOA_REQ)
	private String userName;

	@XmlElement(name = "lang", namespace = Namespaces.SOA_REQ)
	private String lang;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAshost() {
		return ashost;
	}

	public void setAshost(String ashost) {
		this.ashost = ashost;
	}

	public String getSysnr() {
		return sysnr;
	}

	public void setSysnr(String sysnr) {
		this.sysnr = sysnr;
	}

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

}
