/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.fabric8.core.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.fusesource.ide.fabric8.core.connector.Fabric8Facade;

/**
 * DTO class for handling Fabric8 versions
 * 
 * @author lhein
 */
public class VersionDTO extends BaseDTO {

	private static final String JSON_FIELD_ATTRIBUTES 		= "attributes";
	private static final String JSON_FIELD_PROFILES   		= "profiles";
	private static final String JSON_FIELD_PROFILE_IDS		= "profileIds";
	private static final String JSON_FIELD_DEFAULT_VERSION	= "defaultVersion";
	private static final String JSON_FIELD_REVISION			= "revision";
	
	private String revision;
	private final VersionSequenceDTO sequence;
	
	/**
	 * creates a version
	 * 
	 * @param fabric8		the fabric8 ref
	 * @param jsonAttribs	the attributes
	 */
	public VersionDTO(Fabric8Facade fabric8, Map<String, Object> jsonAttribs) {
		super(fabric8, jsonAttribs);
		this.revision = "HEAD";
		this.sequence = new VersionSequenceDTO(getId());
	}
	
	/**
	 * returns true if this is the default version
	 * 
	 * @return
	 */
	public Boolean isDefaultVersion() {
		return getFieldValue(JSON_FIELD_DEFAULT_VERSION);
	}
	
	/**
	 * returns the list of profile ids 
	 * 
	 * @return
	 */
	public List<String> getProfileIds() {
		List<String> profileIds = getFieldValue(JSON_FIELD_PROFILE_IDS);
		if (profileIds == null) profileIds = getFieldValue(JSON_FIELD_PROFILES);
		return profileIds == null ? new ArrayList<String>() : profileIds;
	}
	
	/**
	 * returns the profiles
	 * 
	 * @return
	 */
	public List<ProfileDTO> getProfiles() {
		List<ProfileDTO> profiles = new ArrayList<ProfileDTO>();
		
		for (String pid : getProfileIds()) {
			ProfileDTO p = fabric8.getProfile(getId(), pid);
			if (p != null) profiles.add(p);
		}
		
		return profiles;
	}
	
	/**
	 * returns the attributes
	 * 
	 * @return
	 */
	public Map<String, Object> getAttributes() {
		return getFieldValue(JSON_FIELD_ATTRIBUTES);
	}
	
	public VersionSequenceDTO getVersionSequence() {
		return this.sequence;
	}
	
	/**
	 * returns the revision
	 * 
	 * @return
	 */
	public String getRevision() {
        return this.revision;
    }

	/* (non-Javadoc)
	 * @see org.fusesource.ide.fabric8.core.dto.BaseDTO#create()
	 */
	@Override
	public void create() {
		// TODO Auto-generated method stub
		
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.fabric8.core.dto.BaseDTO#delete()
	 */
	@Override
	public void delete() {
		// TODO Auto-generated method stub
		
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.fabric8.core.dto.BaseDTO#update()
	 */
	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * {
	 * attributes={}, 
	 * defaultVersion=false, 
	 * id=String, 
	 * profileIds=[fabric-ensemble-0000, fabric-ensemble-0000-1, default, karaf, hawtio, fabric, docker, gateway-mq, gateway-haproxy, gateway-http, containers-debug, containers-tomcat, quickstarts-war-camel.servlet, quickstarts-war-rest, feature-camel, feature-cxf, quickstarts-karaf-cxf-camel.cxf.code.first, quickstarts-karaf-cxf-rest, quickstarts-karaf-cxf-secure.rest, quickstarts-karaf-cxf-soap, quickstarts-karaf-cxf-camel.cxf.contract.first, quickstarts-karaf-cxf-secure.soap, quickstarts-karaf-beginner-camel.errorhandler, quickstarts-karaf-beginner-camel.log, quickstarts-karaf-beginner-camel.log.wiki, quickstarts-karaf-beginner-camel.cbr, quickstarts-karaf-beginner-camel.eips, quickstarts-karaf-camel.amq, containers-java, containers-java.pojosr, quickstarts-java-cxf.cdi, insight-log4j, containers-java.camel.spring, quickstarts-java-camel.spring, containers-java.weld, quickstarts-java-camel.cdi, containers-java.spring.boot, quickstarts-spring.boot-webmvc, quickstarts-spring.boot-activemq, quickstarts-spring.boot-camel, mq-client, feature-camel-jms, feature-fabric-web, feature-dosgi, example-camel-mq.bundle, example-camel-twitter, example-camel-cxf, example-camel-mq, example-camel-autotest, example-camel-cluster-cluster.server, example-camel-cluster-cluster.client, example-camel-loanbroker-mq.bank1, example-camel-loanbroker-mq.bank3, example-camel-loanbroker-mq.bank2, example-camel-loanbroker-mq.loanBroker, example-cxf-cxf.server, example-cxf-cxf.client, example-mq-base, example-mq-producer, example-mq-consumer, example-mq, example-dosgi-camel.consumer, example-dosgi-camel.provider, mq-base, mq-default, mq-amq, mq-client-base, mq-client-default, mq-client-local, mq-replicated, openshift, insight-metrics.base, insight-metrics.rhq, insight-core, insight-console, containers-wildfly, insight-metrics.rhq.console, insight-kibana, insight-camel, insight-jetty, insight-metrics.elasticsearch, unmanaged, containers-tomcat.fabric8, containers-keycloak, containers-mule.ce, containers-jdk-java8, containers-liveoak, containers-drools-workbench, containers-drools-execution.server, containers-tomee.fabric8, containers-karaf, containers-jbpm-designer, containers-jbpm-workbench, containers-tomee, containers-services-mongodb, containers-services-cassandra, containers-services-cassandra.local, containers-jetty, system-dns, autoscale],
	 * profiles=[fabric-ensemble-0000, fabric-ensemble-0000-1, default, karaf, hawtio, fabric, docker, gateway-mq, gateway-haproxy, gateway-http, containers-debug, containers-tomcat, quickstarts-war-camel.servlet, quickstarts-war-rest, feature-camel, feature-cxf, quickstarts-karaf-cxf-camel.cxf.code.first, quickstarts-karaf-cxf-rest, quickstarts-karaf-cxf-secure.rest, quickstarts-karaf-cxf-soap, quickstarts-karaf-cxf-camel.cxf.contract.first, quickstarts-karaf-cxf-secure.soap, quickstarts-karaf-beginner-camel.errorhandler, quickstarts-karaf-beginner-camel.log, quickstarts-karaf-beginner-camel.log.wiki, quickstarts-karaf-beginner-camel.cbr, quickstarts-karaf-beginner-camel.eips, quickstarts-karaf-camel.amq, containers-java, containers-java.pojosr, quickstarts-java-cxf.cdi, insight-log4j, containers-java.camel.spring, quickstarts-java-camel.spring, containers-java.weld, quickstarts-java-camel.cdi, containers-java.spring.boot, quickstarts-spring.boot-webmvc, quickstarts-spring.boot-activemq, quickstarts-spring.boot-camel, mq-client, feature-camel-jms, feature-fabric-web, feature-dosgi, example-camel-mq.bundle, example-camel-twitter, example-camel-cxf, example-camel-mq, example-camel-autotest, example-camel-cluster-cluster.server, example-camel-cluster-cluster.client, example-camel-loanbroker-mq.bank1, example-camel-loanbroker-mq.bank3, example-camel-loanbroker-mq.bank2, example-camel-loanbroker-mq.loanBroker, example-cxf-cxf.server, example-cxf-cxf.client, example-mq-base, example-mq-producer, example-mq-consumer, example-mq, example-dosgi-camel.consumer, example-dosgi-camel.provider, mq-base, mq-default, mq-amq, mq-client-base, mq-client-default, mq-client-local, mq-replicated, openshift, insight-metrics.base, insight-metrics.rhq, insight-core, insight-console, containers-wildfly, insight-metrics.rhq.console, insight-kibana, insight-camel, insight-jetty, insight-metrics.elasticsearch, unmanaged, containers-tomcat.fabric8, containers-keycloak, containers-mule.ce, containers-jdk-java8, containers-liveoak, containers-drools-workbench, containers-drools-execution.server, containers-tomee.fabric8, containers-karaf, containers-jbpm-designer, containers-jbpm-workbench, containers-tomee, containers-services-mongodb, containers-services-cassandra, containers-services-cassandra.local, containers-jetty, system-dns, autoscale]
	 * }
	 */
}
