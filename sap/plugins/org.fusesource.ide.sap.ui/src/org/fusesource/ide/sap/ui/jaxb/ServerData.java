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
public class ServerData  extends Data {
	
	@XmlAttribute(name="class")
	private final static String clazz ="org.fusesource.camel.component.sap.model.rfc.impl.ServerDataImpl";
	
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
	 * Progid
	 */

	@XmlElement(name="property")
	private Progid progid;

	public String getProgid() {
		return progid == null ? null : progid.value;
	}
	
	public void setProgid(String progid) {
		if (progid == null) {
			this.progid = null;
			return;
		}
		if (this.progid == null) {
			this.progid = new Progid();
		}
		this.progid.value = progid;
	}
	
	/*
	 * Connection Count
	 */

	@XmlElement(name="property")
	private ConnectionCount connectionCount;

	public String getConnectionCount() {
		return connectionCount == null ? null : connectionCount.value;
	}
	
	public void setConnectionCount(String connectionCount) {
		if (connectionCount == null) {
			this.connectionCount = null;
			return;
		}
		if (this.connectionCount == null) {
			this.connectionCount = new ConnectionCount();
		}
		this.connectionCount.value = connectionCount;
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
	 * Max Start Up Delay
	 */

	@XmlElement(name="property")
	private MaxStartUpDelay maxStartUpDelay;

	public String getMaxStartUpDelay() {
		return maxStartUpDelay == null ? null : maxStartUpDelay.value;
	}
	
	public void setMaxStartUpDelay(String maxStartUpDelay) {
		if (maxStartUpDelay == null) {
			this.maxStartUpDelay = null;
			return;
		}
		if (this.maxStartUpDelay == null) {
			this.maxStartUpDelay = new MaxStartUpDelay();
		}
		this.maxStartUpDelay.value = maxStartUpDelay;
	}
	
	/*
	 * Repository Destination
	 */

	@XmlElement(name="property")
	private RepositoryDestination repositoryDestination;

	public String getRepositoryDestination() {
		return repositoryDestination == null ? null : repositoryDestination.value;
	}
	
	public void setRepositoryDestination(String repositoryDestination) {
		if (repositoryDestination == null) {
			this.repositoryDestination = null;
			return;
		}
		if (this.repositoryDestination == null) {
			this.repositoryDestination = new RepositoryDestination();
		}
		this.repositoryDestination.value = repositoryDestination;
	}
	
	/*
	 * Repository Map
	 */

	@XmlElement(name="property")
	private RepositoryMap repositoryMap;

	public String getRepositoryMap() {
		return repositoryMap == null ? null : repositoryMap.value;
	}
	
	public void setRepositoryMap(String repositoryMap) {
		if (repositoryMap == null) {
			this.repositoryMap = null;
			return;
		}
		if (this.repositoryMap == null) {
			this.repositoryMap = new RepositoryMap();
		}
		this.repositoryMap.value = repositoryMap;
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
	 * Worker Thread Count
	 */

	@XmlElement(name="property")
	private WorkerThreadCount workerThreadCount;

	public String getWorkerThreadCount() {
		return workerThreadCount == null ? null : workerThreadCount.value;
	}
	
	public void setWorkerThreadCount(String workerThreadCount) {
		if (workerThreadCount == null) {
			this.workerThreadCount = null;
			return;
		}
		if (this.workerThreadCount == null) {
			this.workerThreadCount = new WorkerThreadCount();
		}
		this.workerThreadCount.value = workerThreadCount;
	}
	
	/*
	 * Worker Thread Min Count
	 */

	@XmlElement(name="property")
	private WorkerThreadMinCount workerThreadMinCount;

	public String getWorkerThreadMinCount() {
		return workerThreadMinCount == null ? null : workerThreadMinCount.value;
	}
	
	public void setWorkerThreadMinCount(String workerThreadMinCount) {
		if (workerThreadMinCount == null) {
			this.workerThreadMinCount = null;
			return;
		}
		if (this.workerThreadMinCount == null) {
			this.workerThreadMinCount = new WorkerThreadMinCount();
		}
		this.workerThreadMinCount.value = workerThreadMinCount;
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
	 * Snc Lib
	 */

	@XmlElement(name="property")
	private SncLib sncLib;

	public String getSncLib() {
		return sncLib == null ? null : sncLib.value;
	}
	
	public void setSncLib(String sncLib) {
		if (sncLib == null) {
			this.sncLib = null;
			return;
		}
		if (this.sncLib == null) {
			this.sncLib = new SncLib();
		}
		this.sncLib.value = sncLib;
	}
	
}
