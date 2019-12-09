/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.jmx.camel.navigator.stats.model;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author lhein
 */
@XmlRootElement(name = "camelContextStat")
public class CamelContextStatistics {
	private String id;
	private ArrayList<RouteStatistics> routeStatisticsList;

	/**
	 * @return the id
	 */
	@XmlAttribute(name = "id")
	public String getId() {
		return this.id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the routeStatisticsList
	 */
	@XmlElementWrapper(name = "routeStats")
	@XmlElement(name = "routeStat")
	public ArrayList<RouteStatistics> getRouteStatisticsList() {
		return this.routeStatisticsList;
	}

	/**
	 * @param routeStatisticsList the routeStatisticsList to set
	 */
	public void setRouteStatisticsList(
			ArrayList<RouteStatistics> routeStatisticsList) {
		this.routeStatisticsList = routeStatisticsList;
	}
}
