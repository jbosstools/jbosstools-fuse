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

import java.util.Map;

public class SapConnectionConfigurationBuilder {

	public static void populateSapConnectionConfiguration(org.fusesource.camel.component.sap.model.rfc.SapConnectionConfiguration sapConnectionConfigurationModel, SapConnectionConfiguration sapConnectionConfiguration) {
		populateDestinationDataStore(sapConnectionConfigurationModel.getDestinationDataStore(), sapConnectionConfiguration.getDestinationDataStore());
		populateServerDataStore(sapConnectionConfigurationModel.getServerDataStore(), sapConnectionConfiguration.getServerDataStore());
	}
	
	public static void  populateDestinationDataStore(org.fusesource.camel.component.sap.model.rfc.DestinationDataStore destinationDataStoreModel, DestinationDataStore destinationDataStore) {
		for (Map.Entry<String, org.fusesource.camel.component.sap.model.rfc.DestinationData> entry: destinationDataStoreModel.getEntries()) {
			DestinationData destinationData = destinationDataStore.add(entry.getKey());
			populateDestinationData(entry.getValue(), destinationData);
		}
	}

	public static void populateServerDataStore(org.fusesource.camel.component.sap.model.rfc.ServerDataStore serverDataStoreModel, ServerDataStore serverDataStore) {
		for (Map.Entry<String, org.fusesource.camel.component.sap.model.rfc.ServerData> entry: serverDataStoreModel.getEntries()) {
			ServerData serverData = serverDataStore.add(entry.getKey());
			populateServerData(entry.getValue(), serverData);
		}
	}
	
	public static void populateDestinationData(org.fusesource.camel.component.sap.model.rfc.DestinationData destinationDataModel, DestinationData destinationData) {
		destinationData.setAliasUser(destinationDataModel.getAliasUser());
		destinationData.setAshost(destinationDataModel.getAshost());
		destinationData.setAuthType(destinationDataModel.getAuthType());
		destinationData.setClient(destinationDataModel.getClient());
		destinationData.setCodepage(destinationDataModel.getCodepage());
		destinationData.setCpicTrace(destinationDataModel.getCpicTrace());
		destinationData.setDenyInitialPassword(destinationDataModel.getDenyInitialPassword());
		destinationData.setExpirationPeriod(destinationDataModel.getExpirationPeriod());
		destinationData.setExpirationTime(destinationDataModel.getExpirationTime());
		destinationData.setGetsso2(destinationDataModel.getGetsso2());
		destinationData.setGroup(destinationDataModel.getGroup());
		destinationData.setGwhost(destinationDataModel.getGwhost());
		destinationData.setGwserv(destinationDataModel.getGwserv());
		destinationData.setLang(destinationDataModel.getLang());
		destinationData.setLcheck(destinationDataModel.getLcheck());
		destinationData.setMaxGetTime(destinationDataModel.getMaxGetTime());
		destinationData.setMshost(destinationDataModel.getMshost());
		destinationData.setMsserv(destinationDataModel.getMsserv());
		destinationData.setMysapsso2(destinationDataModel.getMysapsso2());
		destinationData.setPassword(destinationDataModel.getPassword());
		destinationData.setPasswd(destinationDataModel.getPasswd());
		destinationData.setPcs(destinationDataModel.getPcs());
		destinationData.setPeakLimit(destinationDataModel.getPeakLimit());
		destinationData.setPingOnCreate(destinationDataModel.getPingOnCreate());
		destinationData.setPoolCapacity(destinationDataModel.getPoolCapacity());
		destinationData.setR3name(destinationDataModel.getR3name());
		destinationData.setRepositoryDest(destinationDataModel.getRepositoryDest());
		destinationData.setRepositoryPasswd(destinationDataModel.getRepositoryPasswd());
		destinationData.setRepositoryRoundtripOptimization(destinationDataModel.getRepositoryRoundtripOptimization());
		destinationData.setRepositorySnc(destinationDataModel.getRepositorySnc());
		destinationData.setRepositoryUser(destinationDataModel.getRepositoryUser());
		destinationData.setSaprouter(destinationDataModel.getSaprouter());
		destinationData.setSncLibrary(destinationDataModel.getSncLibrary());
		destinationData.setSncMode(destinationDataModel.getSncMode());
		destinationData.setSncMyname(destinationDataModel.getSncMyname());
		destinationData.setSncPartnername(destinationDataModel.getSncPartnername());
		destinationData.setSncQop(destinationDataModel.getSncQop());
		destinationData.setSysnr(destinationDataModel.getSysnr());
		destinationData.setTphost(destinationDataModel.getTphost());
		destinationData.setTpname(destinationDataModel.getTpname());
		destinationData.setTrace(destinationDataModel.getTrace());
		destinationData.setType(destinationDataModel.getType());
		destinationData.setUserName(destinationDataModel.getUserName());
		destinationData.setUser(destinationDataModel.getUser());
		destinationData.setUserId(destinationDataModel.getUserId());
		destinationData.setUseSapgui(destinationDataModel.getUseSapgui());
		destinationData.setX509cert(destinationDataModel.getX509cert());
	}

	public static void populateServerData(org.fusesource.camel.component.sap.model.rfc.ServerData serverDataModel, ServerData serverData) {
		serverData.setConnectionCount(serverDataModel.getConnectionCount());
		serverData.setGwhost(serverDataModel.getGwhost());
		serverData.setGwserv(serverDataModel.getGwserv());
		serverData.setMaxStartUpDelay(serverDataModel.getMaxStartUpDelay());
		serverData.setProgid(serverDataModel.getProgid());
		serverData.setRepositoryDestination(serverDataModel.getRepositoryDestination());
		serverData.setRepositoryMap(serverDataModel.getRepositoryMap());
		serverData.setSaprouter(serverDataModel.getSaprouter());
		serverData.setSncLib(serverDataModel.getSncLib());
		serverData.setSncMode(serverDataModel.getSncMode());
		serverData.setSncMyname(serverDataModel.getSncMyname());
		serverData.setSncQop(serverDataModel.getSncQop());
		serverData.setTrace(serverDataModel.getTrace());
		serverData.setWorkerThreadCount(serverDataModel.getWorkerThreadCount());
		serverData.setWorkerThreadMinCount(serverDataModel.getWorkerThreadMinCount());
	}

}
