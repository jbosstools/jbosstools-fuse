/*******************************************************************************
* Copyright (c) 2014 Red Hat, Inc.
* Distributed under license by Red Hat, Inc. All rights reserved.
* This program is made available under the terms of the
* Eclipse Public License v1.0 which accompanies this distribution,
* and is available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
* Red Hat, Inc. - initial API and implementation
* William Collins punkhornsw@gmail.com
******************************************************************************/ 
package org.fusesource.ide.sap.ui.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

public class Data {

	/*
	 * Alias User 
	 */

	@XmlRootElement(name="property")
	@XmlAccessorType(XmlAccessType.FIELD)
	static class AliasUser {
		
		@XmlAttribute(name="name")
		public final static String name = "aliasUser";
		
		@XmlAttribute(name="value")
		public String value;
		
	}
	
	
	
	/*
	 * Ashost 
	 */

	@XmlRootElement(name="property")
	@XmlAccessorType(XmlAccessType.FIELD)
	static class Ashost {
		
		@XmlAttribute(name="name")
		public final static String name = "ashost";
		
		@XmlAttribute(name="value")
		public String value;
		
	}
	
	
	
	/*
	 * Auth Type 
	 */

	@XmlRootElement(name="property")
	@XmlAccessorType(XmlAccessType.FIELD)
	static class AuthType {
		
		@XmlAttribute(name="name")
		public final static String name = "authType";
		
		@XmlAttribute(name="value")
		public String value;
		
	}
	
	
	
	/*
	 * Client
	 */

	@XmlRootElement(name = "property")
	@XmlAccessorType(XmlAccessType.FIELD)
	static class Client {

		@XmlAttribute(name = "name")
		public final static String name = "client";

		@XmlAttribute(name = "value")
		public String value;

	}

		

	/*
	 * Codepage
	 */

	@XmlRootElement(name="property")
	@XmlAccessorType(XmlAccessType.FIELD)
	static class Codepage {
		
		@XmlAttribute(name="name")
		public final static String name = "codepage";
		
		@XmlAttribute(name="value")
		public String value;
		
	}
	
	
	
	/*
	 * Connection Count
	 */

	@XmlRootElement(name="property")
	@XmlAccessorType(XmlAccessType.FIELD)
	static class ConnectionCount {
		
		@XmlAttribute(name="name")
		public final static String name = "connectionCount";
		
		@XmlAttribute(name="value")
		public String value;
		
	}
	
	/*
	 * Cpic Trace
	 */

	@XmlRootElement(name="property")
	@XmlAccessorType(XmlAccessType.FIELD)
	static class CpicTrace {
		
		@XmlAttribute(name="name")
		public final static String name = "cpicTrace";
		
		@XmlAttribute(name="value")
		public String value;
		
	}
	
	
	
	/*
	 * Deny Initial Password
	 */

	@XmlRootElement(name="property")
	@XmlAccessorType(XmlAccessType.FIELD)
	static class DenyInitialPassword {
		
		@XmlAttribute(name="name")
		public final static String name = "denyInitialPassword";
		
		@XmlAttribute(name="value")
		public String value;
		
	}
	
	
	
	/*
	 * Expiration Period
	 */

	@XmlRootElement(name="property")
	@XmlAccessorType(XmlAccessType.FIELD)
	static class ExpirationPeriod {
		
		@XmlAttribute(name="name")
		public final static String name = "expirationPeriod";
		
		@XmlAttribute(name="value")
		public String value;
		
	}
	
	
	
	/*
	 * Expiration Time
	 */

	@XmlRootElement(name="property")
	@XmlAccessorType(XmlAccessType.FIELD)
	static class ExpirationTime {
		
		@XmlAttribute(name="name")
		public final static String name = "expirationTime";
		
		@XmlAttribute(name="value")
		public String value;
		
	}
	
	
	
	/*
	 * Getsso2
	 */

	@XmlRootElement(name="property")
	@XmlAccessorType(XmlAccessType.FIELD)
	static class Getsso2 {
		
		@XmlAttribute(name="name")
		public final static String name = "getsso2";
		
		@XmlAttribute(name="value")
		public String value;
		
	}
	
	
	
	/*
	 * Group
	 */

	@XmlRootElement(name="property")
	@XmlAccessorType(XmlAccessType.FIELD)
	static class Group {
		
		@XmlAttribute(name="name")
		public final static String name = "group";
		
		@XmlAttribute(name="value")
		public String value;
		
	}
	
	
	
	/*
	 * Gwhost
	 */

	@XmlRootElement(name="property")
	@XmlAccessorType(XmlAccessType.FIELD)
	static class Gwhost {
		
		@XmlAttribute(name="name")
		public final static String name = "gwhost";
		
		@XmlAttribute(name="value")
		public String value;
		
	}
	
	
	
	/*
	 * Gwserv
	 */

	@XmlRootElement(name="property")
	@XmlAccessorType(XmlAccessType.FIELD)
	static class Gwserv {
		
		@XmlAttribute(name="name")
		public final static String name = "gwserv";
		
		@XmlAttribute(name="value")
		public String value;
		
	}
	
	
	
	/*
	 * Lang
	 */

	@XmlRootElement(name="property")
	@XmlAccessorType(XmlAccessType.FIELD)
	static class Lang {
		
		@XmlAttribute(name="name")
		public final static String name = "lang";
		
		@XmlAttribute(name="value")
		public String value;
		
	}
	
	
	
	/*
	 * Lcheck
	 */

	@XmlRootElement(name="property")
	@XmlAccessorType(XmlAccessType.FIELD)
	static class Lcheck {
		
		@XmlAttribute(name="name")
		public final static String name = "lcheck";
		
		@XmlAttribute(name="value")
		public String value;
		
	}
	
	
	
	/*
	 * Max Get Time
	 */

	@XmlRootElement(name="property")
	@XmlAccessorType(XmlAccessType.FIELD)
	static class MaxGetTime {
		
		@XmlAttribute(name="name")
		public final static String name = "maxGetTime";
		
		@XmlAttribute(name="value")
		public String value;
		
	}
	
	
	
	/*
	 * Max Start Up Delay
	 */

	@XmlRootElement(name="property")
	@XmlAccessorType(XmlAccessType.FIELD)
	static class MaxStartUpDelay {
		
		@XmlAttribute(name="name")
		public final static String name = "maxStartUpDelay";
		
		@XmlAttribute(name="value")
		public String value;
		
	}
	
	/*
	 * Mshost
	 */

	@XmlRootElement(name="property")
	@XmlAccessorType(XmlAccessType.FIELD)
	static class Mshost {
		
		@XmlAttribute(name="name")
		public final static String name = "mshost";
		
		@XmlAttribute(name="value")
		public String value;
		
	}
	
	
	
	/*
	 * Msserv
	 */

	@XmlRootElement(name="property")
	@XmlAccessorType(XmlAccessType.FIELD)
	static class Msserv {
		
		@XmlAttribute(name="name")
		public final static String name = "msserv";
		
		@XmlAttribute(name="value")
		public String value;
		
	}
	
	
	
	/*
	 * Mysapsso2
	 */

	@XmlRootElement(name="property")
	@XmlAccessorType(XmlAccessType.FIELD)
	static class Mysapsso2 {
		
		@XmlAttribute(name="name")
		public final static String name = "mysapsso2";
		
		@XmlAttribute(name="value")
		public String value;
		
	}
	
	
	
	/*
	 * Passwd
	 */

	@XmlRootElement(name="property")
	@XmlAccessorType(XmlAccessType.FIELD)
	static class Passwd {
		
		@XmlAttribute(name="name")
		public final static String name = "passwd";
		
		@XmlAttribute(name="value")
		public String value;
		
	}
	
	
	
	/*
	 * Password
	 */

	@XmlRootElement(name="property")
	@XmlAccessorType(XmlAccessType.FIELD)
	static class Password {
		
		@XmlAttribute(name="name")
		public final static String name = "password";
		
		@XmlAttribute(name="value")
		public String value;
		
	}
	
	
	
	/*
	 * Pcs
	 */

	@XmlRootElement(name="property")
	@XmlAccessorType(XmlAccessType.FIELD)
	static class Pcs {
		
		@XmlAttribute(name="name")
		public final static String name = "pcs";
		
		@XmlAttribute(name="value")
		public String value;
		
	}
	
	
	
	/*
	 * Peak Limit
	 */

	@XmlRootElement(name="property")
	@XmlAccessorType(XmlAccessType.FIELD)
	static class PeakLimit {
		
		@XmlAttribute(name="name")
		public final static String name = "peakLimit";
		
		@XmlAttribute(name="value")
		public String value;
		
	}
	
	
	
	/*
	 * Ping On Create
	 */

	@XmlRootElement(name="property")
	@XmlAccessorType(XmlAccessType.FIELD)
	static class PingOnCreate {
		
		@XmlAttribute(name="name")
		public final static String name = "pingOnCreate";
		
		@XmlAttribute(name="value")
		public String value;
		
	}
	
	
	
	/*
	 * Pool Capacity
	 */

	@XmlRootElement(name="property")
	@XmlAccessorType(XmlAccessType.FIELD)
	static class PoolCapacity {
		
		@XmlAttribute(name="name")
		public final static String name = "poolCapacity";
		
		@XmlAttribute(name="value")
		public String value;
		
	}
	
	
	
	/*
	 * Progid
	 */

	@XmlRootElement(name="property")
	@XmlAccessorType(XmlAccessType.FIELD)
	static class Progid {
		
		@XmlAttribute(name="name")
		public final static String name = "progid";
		
		@XmlAttribute(name="value")
		public String value;
		
	}
	
	/*
	 * R3name
	 */

	@XmlRootElement(name="property")
	@XmlAccessorType(XmlAccessType.FIELD)
	static class R3name {
		
		@XmlAttribute(name="name")
		public final static String name = "r3name";
		
		@XmlAttribute(name="value")
		public String value;
		
	}
	
	
	
	/*
	 * Repository Dest
	 */

	@XmlRootElement(name="property")
	@XmlAccessorType(XmlAccessType.FIELD)
	static class RepositoryDest {
		
		@XmlAttribute(name="name")
		public final static String name = "repositoryDest";
		
		@XmlAttribute(name="value")
		public String value;
		
	}
	
	
	
	/*
	 * Repository Destination
	 */

	@XmlRootElement(name="property")
	@XmlAccessorType(XmlAccessType.FIELD)
	static class RepositoryDestination {
		
		@XmlAttribute(name="name")
		public final static String name = "repositoryDestination";
		
		@XmlAttribute(name="value")
		public String value;
		
	}
	
	/*
	 * Repository Map
	 */

	@XmlRootElement(name="property")
	@XmlAccessorType(XmlAccessType.FIELD)
	static class RepositoryMap {
		
		@XmlAttribute(name="name")
		public final static String name = "repositoryMap";
		
		@XmlAttribute(name="value")
		public String value;
		
	}
	
	/*
	 * Repository Passwd
	 */

	@XmlRootElement(name="property")
	@XmlAccessorType(XmlAccessType.FIELD)
	static class RepositoryPasswd {
		
		@XmlAttribute(name="name")
		public final static String name = "repositoryPasswd";
		
		@XmlAttribute(name="value")
		public String value;
		
	}
	
	
	
	/*
	 * Repository Roundtrip Optimization
	 */

	@XmlRootElement(name="property")
	@XmlAccessorType(XmlAccessType.FIELD)
	static class RepositoryRoundtripOptimization {
		
		@XmlAttribute(name="name")
		public final static String name = "repositoryRoundtripOptimization";
		
		@XmlAttribute(name="value")
		public String value;
		
	}
	
	
	
	/*
	 * Repository Snc
	 */

	@XmlRootElement(name="property")
	@XmlAccessorType(XmlAccessType.FIELD)
	static class RepositorySnc {
		
		@XmlAttribute(name="name")
		public final static String name = "repositorySnc";
		
		@XmlAttribute(name="value")
		public String value;
		
	}
	
	
	
	/*
	 * Repository User
	 */

	@XmlRootElement(name="property")
	@XmlAccessorType(XmlAccessType.FIELD)
	static class RepositoryUser {
		
		@XmlAttribute(name="name")
		public final static String name = "repositoryUser";
		
		@XmlAttribute(name="value")
		public String value;
		
	}
	
	
	
	/*
	 * Saprouter
	 */

	@XmlRootElement(name="property")
	@XmlAccessorType(XmlAccessType.FIELD)
	static class Saprouter {
		
		@XmlAttribute(name="name")
		public final static String name = "saprouter";
		
		@XmlAttribute(name="value")
		public String value;
		
	}
	
	
	
	/*
	 * Snc Lib
	 */

	@XmlRootElement(name="property")
	@XmlAccessorType(XmlAccessType.FIELD)
	static class SncLib {
		
		@XmlAttribute(name="name")
		public final static String name = "sncLib";
		
		@XmlAttribute(name="value")
		public String value;
		
	}
	

	/*
	 * Snc Library
	 */

	@XmlRootElement(name="property")
	@XmlAccessorType(XmlAccessType.FIELD)
	static class SncLibrary {
		
		@XmlAttribute(name="name")
		public final static String name = "sncLibrary";
		
		@XmlAttribute(name="value")
		public String value;
		
	}
	
	
	
	/*
	 * Snc Mode
	 */

	@XmlRootElement(name="property")
	@XmlAccessorType(XmlAccessType.FIELD)
	static class SncMode {
		
		@XmlAttribute(name="name")
		public final static String name = "sncMode";
		
		@XmlAttribute(name="value")
		public String value;
		
	}
	
	
	
	/*
	 * Snc Myname
	 */

	@XmlRootElement(name="property")
	@XmlAccessorType(XmlAccessType.FIELD)
	static class SncMyname {
		
		@XmlAttribute(name="name")
		public final static String name = "sncMyname";
		
		@XmlAttribute(name="value")
		public String value;
		
	}
	
	
	
	/*
	 * Snc Partnername
	 */

	@XmlRootElement(name="property")
	@XmlAccessorType(XmlAccessType.FIELD)
	static class SncPartnername {
		
		@XmlAttribute(name="name")
		public final static String name = "sncPartnername";
		
		@XmlAttribute(name="value")
		public String value;
		
	}
	
	
	
	/*
	 * Snc Qop
	 */

	@XmlRootElement(name="property")
	@XmlAccessorType(XmlAccessType.FIELD)
	static class SncQop {
		
		@XmlAttribute(name="name")
		public final static String name = "sncQop";
		
		@XmlAttribute(name="value")
		public String value;
		
	}
	
	
	
	/*
	 * Sysnr
	 */

	@XmlRootElement(name="property")
	@XmlAccessorType(XmlAccessType.FIELD)
	static class Sysnr {
		
		@XmlAttribute(name="name")
		public final static String name = "sysnr";
		
		@XmlAttribute(name="value")
		public String value;
		
	}
	
	
	
	/*
	 * Tphost
	 */

	@XmlRootElement(name="property")
	@XmlAccessorType(XmlAccessType.FIELD)
	static class Tphost {
		
		@XmlAttribute(name="name")
		public final static String name = "tphost";
		
		@XmlAttribute(name="value")
		public String value;
		
	}
	
	
	
	/*
	 * Tpname
	 */

	@XmlRootElement(name="property")
	@XmlAccessorType(XmlAccessType.FIELD)
	static class Tpname {
		
		@XmlAttribute(name="name")
		public final static String name = "tpname";
		
		@XmlAttribute(name="value")
		public String value;
		
	}
	
	
	
	/*
	 * Trace
	 */

	@XmlRootElement(name="property")
	@XmlAccessorType(XmlAccessType.FIELD)
	static class Trace {
		
		@XmlAttribute(name="name")
		public final static String name = "trace";
		
		@XmlAttribute(name="value")
		public String value;
		
	}
	
	
	
	/*
	 * Type
	 */

	@XmlRootElement(name="property")
	@XmlAccessorType(XmlAccessType.FIELD)
	static class Type {
		
		@XmlAttribute(name="name")
		public final static String name = "type";
		
		@XmlAttribute(name="value")
		public String value;
		
	}
	
	
	
	/*
	 * User Name
	 */

	@XmlRootElement(name="property")
	@XmlAccessorType(XmlAccessType.FIELD)
	static class UserName {
		
		@XmlAttribute(name="name")
		public final static String name = "userName";
		
		@XmlAttribute(name="value")
		public String value;
		
	}
	
	
	
	/*
	 * User
	 */

	@XmlRootElement(name="property")
	@XmlAccessorType(XmlAccessType.FIELD)
	static class User {
		
		@XmlAttribute(name="name")
		public final static String name = "user";
		
		@XmlAttribute(name="value")
		public String value;
		
	}
	
	
	
	/*
	 * User Id
	 */

	@XmlRootElement(name="property")
	@XmlAccessorType(XmlAccessType.FIELD)
	static class UserId {
		
		@XmlAttribute(name="name")
		public final static String name = "userId";
		
		@XmlAttribute(name="value")
		public String value;
		
	}
	
	
	
	/*
	 * Use Sapgui
	 */

	@XmlRootElement(name="property")
	@XmlAccessorType(XmlAccessType.FIELD)
	static class UseSapgui {
		
		@XmlAttribute(name="name")
		public final static String name = "useSapgui";
		
		@XmlAttribute(name="value")
		public String value;
		
	}
	
	
	
	/*
	 * Worker Thread Count
	 */

	@XmlRootElement(name="property")
	@XmlAccessorType(XmlAccessType.FIELD)
	static class WorkerThreadCount {
		
		@XmlAttribute(name="name")
		public final static String name = "workerThreadCount";
		
		@XmlAttribute(name="value")
		public String value;
		
	}
	
	/*
	 * Worker Thread Min Count
	 */

	@XmlRootElement(name="property")
	@XmlAccessorType(XmlAccessType.FIELD)
	static class WorkerThreadMinCount {
		
		@XmlAttribute(name="name")
		public final static String name = "workerThreadMinCount";
		
		@XmlAttribute(name="value")
		public String value;
		
	}
	
	/*
	 * X509cert
	 */

	@XmlRootElement(name="property")
	@XmlAccessorType(XmlAccessType.FIELD)
	static class X509cert {
		
		@XmlAttribute(name="name")
		public final static String name = "x509cert";
		
		@XmlAttribute(name="value")
		public String value;
		
	}
}
