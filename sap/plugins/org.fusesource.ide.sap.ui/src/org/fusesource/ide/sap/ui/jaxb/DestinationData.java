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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="bean")
@XmlAccessorType(XmlAccessType.FIELD)
public class DestinationData extends Data {
	
	@XmlAttribute(name="class")
	private final static String clazz ="org.fusesource.camel.component.sap.model.rfc.impl.DestinationDataImpl";
	
	
	/*
	 * Alias User 
	 */

	@XmlElement(name="property")
	private AliasUser aliasUser;

	public String getAliasUser() {
		return aliasUser == null ? null : aliasUser.value;
	}
	
	public void setAliasUser(String aliasUser) {
		if (aliasUser == null) {
			this.aliasUser = null;
			return;
		}
		if (this.aliasUser == null) {
			this.aliasUser = new AliasUser();
		}
		this.aliasUser.value = aliasUser;
	}
	
	/*
	 * Ashost 
	 */

	@XmlElement(name="property")
	private Ashost ashost;

	public String getAshost() {
		return ashost == null ? null : ashost.value;
	}
	
	public void setAshost(String ashost) {
		if (ashost == null) {
			this.ashost = null;
			return;
		}
		if (this.ashost == null) {
			this.ashost = new Ashost();
		}
		this.ashost.value = ashost;
	}
	
	/*
	 * Auth Type 
	 */

	@XmlElement(name="property")
	private AuthType authType;

	public String getAuthType() {
		return authType == null ? null : authType.value;
	}
	
	public void setAuthType(String authType) {
		if (authType == null) {
			this.authType = null;
			return;
		}
		if (this.authType == null) {
			this.authType = new AuthType();
		}
		this.authType.value = authType;
	}
	
	/*
	 * Client
	 */

	@XmlElement(name = "property")
	private Client client;

	public String getClient() {
		return client == null ? null : client.value;
	}

	public void setClient(String client) {
		if (client == null) {
			this.client = null;
			return;
		}
		if (this.client == null) {
			this.client = new Client();
		}
		this.client.value = client;
	}	

	/*
	 * Codepage
	 */

	@XmlElement(name="property")
	private Codepage codepage;

	public String getCodepage() {
		return codepage == null ? null : codepage.value;
	}
	
	public void setCodepage(String codepage) {
		if (codepage == null) {
			this.codepage = null;
			return;
		}
		if (this.codepage == null) {
			this.codepage = new Codepage();
		}
		this.codepage.value = codepage;
	}
	
	/*
	 * Cpic Trace
	 */

	@XmlElement(name="property")
	private CpicTrace cpicTrace;

	public String getCpicTrace() {
		return cpicTrace == null ? null : cpicTrace.value;
	}
	
	public void setCpicTrace(String cpicTrace) {
		if (cpicTrace == null) {
			this.cpicTrace = null;
			return;
		}
		if (this.cpicTrace == null) {
			this.cpicTrace = new CpicTrace();
		}
		this.cpicTrace.value = cpicTrace;
	}
	
	/*
	 * Deny Initial Password
	 */

	@XmlElement(name="property")
	private DenyInitialPassword denyInitialPassword;

	public String getDenyInitialPassword() {
		return denyInitialPassword == null ? null : denyInitialPassword.value;
	}
	
	public void setDenyInitialPassword(String denyInitialPassword) {
		if (denyInitialPassword == null) {
			this.denyInitialPassword = null;
			return;
		}
		if (this.denyInitialPassword == null) {
			this.denyInitialPassword = new DenyInitialPassword();
		}
		this.denyInitialPassword.value = denyInitialPassword;
	}
	
	/*
	 * Expiration Period
	 */

	@XmlElement(name="property")
	private ExpirationPeriod expirationPeriod;

	public String getExpirationPeriod() {
		return expirationPeriod == null ? null : expirationPeriod.value;
	}
	
	public void setExpirationPeriod(String expirationPeriod) {
		if (expirationPeriod == null) {
			this.expirationPeriod = null;
			return;
		}
		if (this.expirationPeriod == null) {
			this.expirationPeriod = new ExpirationPeriod();
		}
		this.expirationPeriod.value = expirationPeriod;
	}
	
	/*
	 * Expiration Time
	 */

	@XmlElement(name="property")
	private ExpirationTime expirationTime;

	public String getExpirationTime() {
		return expirationTime == null ? null : expirationTime.value;
	}
	
	public void setExpirationTime(String expirationTime) {
		if (expirationTime == null) {
			this.expirationTime = null;
			return;
		}
		if (this.expirationTime == null) {
			this.expirationTime = new ExpirationTime();
		}
		this.expirationTime.value = expirationTime;
	}
	
	/*
	 * Getsso2
	 */

	@XmlElement(name="property")
	private Getsso2 getsso2;

	public String getGetsso2() {
		return getsso2 == null ? null : getsso2.value;
	}
	
	public void setGetsso2(String getsso2) {
		if (getsso2 == null) {
			this.getsso2 = null;
			return;
		}
		if (this.getsso2 == null) {
			this.getsso2 = new Getsso2();
		}
		this.getsso2.value = getsso2;
	}
	
	/*
	 * Group
	 */

	@XmlElement(name="property")
	private Group group;

	public String getGroup() {
		return group == null ? null : group.value;
	}
	
	public void setGroup(String group) {
		if (group == null) {
			this.group = null;
			return;
		}
		if (this.group == null) {
			this.group = new Group();
		}
		this.group.value = group;
	}
	
	/*
	 * Gwhost
	 */

	@XmlElement(name="property")
	private Gwhost gwhost;

	public String getGwhost() {
		return gwhost == null ? null : gwhost.value;
	}
	
	public void setGwhost(String gwhost) {
		if (gwhost == null) {
			this.gwhost = null;
			return;
		}
		if (this.gwhost == null) {
			this.gwhost = new Gwhost();
		}
		this.gwhost.value = gwhost;
	}
	
	/*
	 * Gwserv
	 */

	@XmlElement(name="property")
	private Gwserv gwserv;

	public String getGwserv() {
		return gwserv == null ? null : gwserv.value;
	}
	
	public void setGwserv(String gwserv) {
		if (gwserv == null) {
			this.gwserv = null;
			return;
		}
		if (this.gwserv == null) {
			this.gwserv = new Gwserv();
		}
		this.gwserv.value = gwserv;
	}
	
	/*
	 * Lang
	 */

	@XmlElement(name="property")
	private Lang lang;

	public String getLang() {
		return lang == null ? null : lang.value;
	}
	
	public void setLang(String lang) {
		if (lang == null) {
			this.lang = null;
			return;
		}
		if (this.lang == null) {
			this.lang = new Lang();
		}
		this.lang.value = lang;
	}
	
	/*
	 * Lcheck
	 */

	@XmlElement(name="property")
	private Lcheck lcheck;

	public String getLcheck() {
		return lcheck == null ? null : lcheck.value;
	}
	
	public void setLcheck(String lcheck) {
		if (lcheck == null) {
			this.lcheck = null;
			return;
		}
		if (this.lcheck == null) {
			this.lcheck = new Lcheck();
		}
		this.lcheck.value = lcheck;
	}
	
	/*
	 * Max Get Time
	 */

	@XmlElement(name="property")
	private MaxGetTime maxGetTime;

	public String getMaxGetTime() {
		return maxGetTime == null ? null : maxGetTime.value;
	}
	
	public void setMaxGetTime(String maxGetTime) {
		if (maxGetTime == null) {
			this.maxGetTime = null;
			return;
		}
		if (this.maxGetTime == null) {
			this.maxGetTime = new MaxGetTime();
		}
		this.maxGetTime.value = maxGetTime;
	}
	
	/*
	 * Mshost
	 */

	@XmlElement(name="property")
	private Mshost mshost;

	public String getMshost() {
		return mshost == null ? null : mshost.value;
	}
	
	public void setMshost(String mshost) {
		if (mshost == null) {
			this.mshost = null;
			return;
		}
		if (this.mshost == null) {
			this.mshost = new Mshost();
		}
		this.mshost.value = mshost;
	}
	
	/*
	 * Msserv
	 */

	@XmlElement(name="property")
	private Msserv msserv;

	public String getMsserv() {
		return msserv == null ? null : msserv.value;
	}
	
	public void setMsserv(String msserv) {
		if (msserv == null) {
			this.msserv = null;
			return;
		}
		if (this.msserv == null) {
			this.msserv = new Msserv();
		}
		this.msserv.value = msserv;
	}
	
	/*
	 * Mysapsso2
	 */

	@XmlElement(name="property")
	private Mysapsso2 mysapsso2;

	public String getMysapsso2() {
		return mysapsso2 == null ? null : mysapsso2.value;
	}
	
	public void setMysapsso2(String mysapsso2) {
		if (mysapsso2 == null) {
			this.mysapsso2 = null;
			return;
		}
		if (this.mysapsso2 == null) {
			this.mysapsso2 = new Mysapsso2();
		}
		this.mysapsso2.value = mysapsso2;
	}
	
	/*
	 * Passwd
	 */

	@XmlElement(name="property")
	private Passwd passwd;

	public String getPasswd() {
		return passwd == null ? null : passwd.value;
	}
	
	public void setPasswd(String passwd) {
		if (passwd == null) {
			this.passwd = null;
			return;
		}
		if (this.passwd == null) {
			this.passwd = new Passwd();
		}
		this.passwd.value = passwd;
	}
	
	/*
	 * Password
	 */

	@XmlElement(name="property")
	private Password password;

	public String getPassword() {
		return password == null ? null : password.value;
	}
	
	public void setPassword(String password) {
		if (password == null) {
			this.password = null;
			return;
		}
		if (this.password == null) {
			this.password = new Password();
		}
		this.password.value = password;
	}
	
	/*
	 * Pcs
	 */

	@XmlElement(name="property")
	private Pcs pcs;

	public String getPcs() {
		return pcs == null ? null : pcs.value;
	}
	
	public void setPcs(String pcs) {
		if (pcs == null) {
			this.pcs = null;
			return;
		}
		if (this.pcs == null) {
			this.pcs = new Pcs();
		}
		this.pcs.value = pcs;
	}
	
	/*
	 * Peak Limit
	 */

	@XmlElement(name="property")
	private PeakLimit peakLimit;

	public String getPeakLimit() {
		return peakLimit == null ? null : peakLimit.value;
	}
	
	public void setPeakLimit(String peakLimit) {
		if (peakLimit == null) {
			this.peakLimit = null;
			return;
		}
		if (this.peakLimit == null) {
			this.peakLimit = new PeakLimit();
		}
		this.peakLimit.value = peakLimit;
	}
	
	/*
	 * Ping On Create
	 */

	@XmlElement(name="property")
	private PingOnCreate pingOnCreate;

	public String getPingOnCreate() {
		return pingOnCreate == null ? null : pingOnCreate.value;
	}
	
	public void setPingOnCreate(String pingOnCreate) {
		if (pingOnCreate == null) {
			this.pingOnCreate = null;
			return;
		}
		if (this.pingOnCreate == null) {
			this.pingOnCreate = new PingOnCreate();
		}
		this.pingOnCreate.value = pingOnCreate;
	}
	
	/*
	 * Pool Capacity
	 */

	@XmlElement(name="property")
	private PoolCapacity poolCapacity;

	public String getPoolCapacity() {
		return poolCapacity == null ? null : poolCapacity.value;
	}
	
	public void setPoolCapacity(String poolCapacity) {
		if (poolCapacity == null) {
			this.poolCapacity = null;
			return;
		}
		if (this.poolCapacity == null) {
			this.poolCapacity = new PoolCapacity();
		}
		this.poolCapacity.value = poolCapacity;
	}
	
	/*
	 * R3name
	 */

	@XmlElement(name="property")
	private R3name r3name;

	public String getR3name() {
		return r3name == null ? null : r3name.value;
	}
	
	public void setR3name(String r3name) {
		if (r3name == null) {
			this.r3name = null;
			return;
		}
		if (this.r3name == null) {
			this.r3name = new R3name();
		}
		this.r3name.value = r3name;
	}
	
	/*
	 * Repository Dest
	 */

	@XmlElement(name="property")
	private RepositoryDest repositoryDest;

	public String getRepositoryDest() {
		return repositoryDest == null ? null : repositoryDest.value;
	}
	
	public void setRepositoryDest(String repositoryDest) {
		if (repositoryDest == null) {
			this.repositoryDest = null;
			return;
		}
		if (this.repositoryDest == null) {
			this.repositoryDest = new RepositoryDest();
		}
		this.repositoryDest.value = repositoryDest;
	}
	
	/*
	 * Repository Passwd
	 */

	@XmlElement(name="property")
	private RepositoryPasswd repositoryPasswd;

	public String getRepositoryPasswd() {
		return repositoryPasswd == null ? null : repositoryPasswd.value;
	}
	
	public void setRepositoryPasswd(String repositoryPasswd) {
		if (repositoryPasswd == null) {
			this.repositoryPasswd = null;
			return;
		}
		if (this.repositoryPasswd == null) {
			this.repositoryPasswd = new RepositoryPasswd();
		}
		this.repositoryPasswd.value = repositoryPasswd;
	}
	
	/*
	 * Repository Roundtrip Optimization
	 */

	@XmlElement(name="property")
	private RepositoryRoundtripOptimization repositoryRoundtripOptimization;

	public String getRepositoryRoundtripOptimization() {
		return repositoryRoundtripOptimization == null ? null : repositoryRoundtripOptimization.value;
	}
	
	public void setRepositoryRoundtripOptimization(String repositoryRoundtripOptimization) {
		if (repositoryRoundtripOptimization == null) {
			this.repositoryRoundtripOptimization = null;
			return;
		}
		if (this.repositoryRoundtripOptimization == null) {
			this.repositoryRoundtripOptimization = new RepositoryRoundtripOptimization();
		}
		this.repositoryRoundtripOptimization.value = repositoryRoundtripOptimization;
	}
	
	/*
	 * Repository Snc
	 */

	@XmlElement(name="property")
	private RepositorySnc repositorySnc;

	public String getRepositorySnc() {
		return repositorySnc == null ? null : repositorySnc.value;
	}
	
	public void setRepositorySnc(String repositorySnc) {
		if (repositorySnc == null) {
			this.repositorySnc = null;
			return;
		}
		if (this.repositorySnc == null) {
			this.repositorySnc = new RepositorySnc();
		}
		this.repositorySnc.value = repositorySnc;
	}
	
	/*
	 * Repository User
	 */

	@XmlElement(name="property")
	private RepositoryUser repositoryUser;

	public String getRepositoryUser() {
		return repositoryUser == null ? null : repositoryUser.value;
	}
	
	public void setRepositoryUser(String repositoryUser) {
		if (repositoryUser == null) {
			this.repositoryUser = null;
			return;
		}
		if (this.repositoryUser == null) {
			this.repositoryUser = new RepositoryUser();
		}
		this.repositoryUser.value = repositoryUser;
	}
	
	/*
	 * Saprouter
	 */

	@XmlElement(name="property")
	private Saprouter saprouter;

	public String getSaprouter() {
		return saprouter == null ? null : saprouter.value;
	}
	
	public void setSaprouter(String saprouter) {
		if (saprouter == null) {
			this.saprouter = null;
			return;
		}
		if (this.saprouter == null) {
			this.saprouter = new Saprouter();
		}
		this.saprouter.value = saprouter;
	}
	
	/*
	 * Snc Library
	 */

	@XmlElement(name="property")
	private SncLibrary sncLibrary;

	public String getSncLibrary() {
		return sncLibrary == null ? null : sncLibrary.value;
	}
	
	public void setSncLibrary(String sncLibrary) {
		if (sncLibrary == null) {
			this.sncLibrary = null;
			return;
		}
		if (this.sncLibrary == null) {
			this.sncLibrary = new SncLibrary();
		}
		this.sncLibrary.value = sncLibrary;
	}
	
	/*
	 * Snc Mode
	 */

	@XmlElement(name="property")
	private SncMode sncMode;

	public String getSncMode() {
		return sncMode == null ? null : sncMode.value;
	}
	
	public void setSncMode(String sncMode) {
		if (sncMode == null) {
			this.sncMode = null;
			return;
		}
		if (this.sncMode == null) {
			this.sncMode = new SncMode();
		}
		this.sncMode.value = sncMode;
	}
	
	/*
	 * Snc Myname
	 */

	@XmlElement(name="property")
	private SncMyname sncMyname;

	public String getSncMyname() {
		return sncMyname == null ? null : sncMyname.value;
	}
	
	public void setSncMyname(String sncMyname) {
		if (sncMyname == null) {
			this.sncMyname = null;
			return;
		}
		if (this.sncMyname == null) {
			this.sncMyname = new SncMyname();
		}
		this.sncMyname.value = sncMyname;
	}
	
	/*
	 * Snc Partnername
	 */

	@XmlElement(name="property")
	private SncPartnername sncPartnername;

	public String getSncPartnername() {
		return sncPartnername == null ? null : sncPartnername.value;
	}
	
	public void setSncPartnername(String sncPartnername) {
		if (sncPartnername == null) {
			this.sncPartnername = null;
			return;
		}
		if (this.sncPartnername == null) {
			this.sncPartnername = new SncPartnername();
		}
		this.sncPartnername.value = sncPartnername;
	}
	
	/*
	 * Snc Qop
	 */

	@XmlElement(name="property")
	private SncQop sncQop;

	public String getSncQop() {
		return sncQop == null ? null : sncQop.value;
	}
	
	public void setSncQop(String sncQop) {
		if (sncQop == null) {
			this.sncQop = null;
			return;
		}
		if (this.sncQop == null) {
			this.sncQop = new SncQop();
		}
		this.sncQop.value = sncQop;
	}
	
	/*
	 * Sysnr
	 */

	@XmlElement(name="property")
	private Sysnr sysnr;

	public String getSysnr() {
		return sysnr == null ? null : sysnr.value;
	}
	
	public void setSysnr(String sysnr) {
		if (sysnr == null) {
			this.sysnr = null;
			return;
		}
		if (this.sysnr == null) {
			this.sysnr = new Sysnr();
		}
		this.sysnr.value = sysnr;
	}
	
	/*
	 * Tphost
	 */

	@XmlElement(name="property")
	private Tphost tphost;

	public String getTphost() {
		return tphost == null ? null : tphost.value;
	}
	
	public void setTphost(String tphost) {
		if (tphost == null) {
			this.tphost = null;
			return;
		}
		if (this.tphost == null) {
			this.tphost = new Tphost();
		}
		this.tphost.value = tphost;
	}
	
	/*
	 * Tpname
	 */

	@XmlElement(name="property")
	private Tpname tpname;

	public String getTpname() {
		return tpname == null ? null : tpname.value;
	}
	
	public void setTpname(String tpname) {
		if (tpname == null) {
			this.tpname = null;
			return;
		}
		if (this.tpname == null) {
			this.tpname = new Tpname();
		}
		this.tpname.value = tpname;
	}
	
	/*
	 * Trace
	 */

	@XmlElement(name="property")
	private Trace trace;

	public String getTrace() {
		return trace == null ? null : trace.value;
	}
	
	public void setTrace(String trace) {
		if (trace == null) {
			this.trace = null;
			return;
		}
		if (this.trace == null) {
			this.trace = new Trace();
		}
		this.trace.value = trace;
	}
	
	/*
	 * Type
	 */

	@XmlElement(name="property")
	private Type type;

	public String getType() {
		return type == null ? null : type.value;
	}
	
	public void setType(String type) {
		if (type == null) {
			this.type = null;
			return;
		}
		if (this.type == null) {
			this.type = new Type();
		}
		this.type.value = type;
	}
	
	/*
	 * User Name
	 */

	@XmlElement(name="property")
	private UserName userName;

	public String getUserName() {
		return userName == null ? null : userName.value;
	}
	
	public void setUserName(String userName) {
		if (userName == null) {
			this.userName = null;
			return;
		}
		if (this.userName == null) {
			this.userName = new UserName();
		}
		this.userName.value = userName;
	}
	
	/*
	 * User
	 */

	@XmlElement(name="property")
	private User user;

	public String getUser() {
		return user == null ? null : user.value;
	}
	
	public void setUser(String user) {
		if (user == null) {
			this.user = null;
			return;
		}
		if (this.user == null) {
			this.user = new User();
		}
		this.user.value = user;
	}
	
	/*
	 * User Id
	 */

	@XmlElement(name="property")
	private UserId userId;

	public String getUserId() {
		return userId == null ? null : userId.value;
	}
	
	public void setUserId(String userId) {
		if (userId == null) {
			this.userId = null;
			return;
		}
		if (this.userId == null) {
			this.userId = new UserId();
		}
		this.userId.value = userId;
	}
	
	/*
	 * Use Sapgui
	 */

	@XmlElement(name="property")
	private UseSapgui useSapgui;

	public String getUseSapgui() {
		return useSapgui == null ? null : useSapgui.value;
	}
	
	public void setUseSapgui(String useSapgui) {
		if (useSapgui == null) {
			this.useSapgui = null;
			return;
		}
		if (this.useSapgui == null) {
			this.useSapgui = new UseSapgui();
		}
		this.useSapgui.value = useSapgui;
	}
	
	/*
	 * X509cert
	 */

	@XmlElement(name="property")
	private X509cert x509cert;

	public String getX509cert() {
		return x509cert == null ? null : x509cert.value;
	}
	
	public void setX509cert(String x509cert) {
		if (x509cert == null) {
			this.x509cert = null;
			return;
		}
		if (this.x509cert == null) {
			this.x509cert = new X509cert();
		}
		this.x509cert.value = x509cert;
	}
}
