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
import org.fusesource.ide.fabric8.core.connector.JolokiaHelpers;

/**
 * DTO class for handling Fabric8 containers
 * 
 * @author lhein
 */
public class ContainerDTO extends BaseDTO {
	
	private static final String JSON_FIELD_ROOT					= "root";					// boolean
	private static final String JSON_FIELD_PROVISION_STATUS		= "provisionStatus";		// string
	private static final String JSON_FIELD_PARENT_ID		  	= "parentId";				// string
	private static final String JSON_FIELD_CHILDREN_IDS		  	= "childrenIds";			// list
	private static final String JSON_FIELD_PROCESS_ID 			= "processId";				// long
	private static final String JSON_FIELD_VERSION_ID		 	= "versionId";				// string
	private static final String JSON_FIELD_PROFILE_IDS		  	= "profileIds";				// list
	private static final String JSON_FIELD_ENSEMBLE_SERVER		= "ensembleServer";			// boolean
	private static final String JSON_FIELD_JMX_URL				= "jmxUrl";					// string
	private static final String JSON_FIELD_HTTP_URL		 		= "httpUrl";				// string
	private static final String JSON_FIELD_DEBUG_PORT			= "debugPort";				// int
	private static final String JSON_FIELD_MANAGED		  		= "managed";				// boolean
	private static final String JSON_FIELD_TYPE					= "type";					// string	
	private static final String JSON_FIELD_ALIVE				= "alive";					// boolean
	private static final String JSON_FIELD_SSH_URL				= "sshUrl";					// string
	private static final String JSON_FIELD_JOLOKIA_URL			= "jolokiaUrl";				// string
	private static final String JSON_FIELD_PROVISIONING_COMPLETE = "provisioningComplete";	// boolean
	private static final String JSON_FIELD_JMX_DOMAINS			= "jmxDomains";				// list
	private static final String JSON_FIELD_LOCATION				= "location";				// string
	private static final String JSON_FIELD_PROVISIONING_PENDING = "provisioningPending";	// boolean
	
	private static final String JSON_FIELD_PROFILES				= "profiles";				// list
	private static final String JSON_FIELD_CHILDREN				= "children";				// list
	private static final String JSON_FIELD_PROVISION_CHECKSUMS 	= "provisionChecksums";		// list
	private static final String JSON_FIELD_LOCAL_HOSTNAME		= "localHostname";			// string
	private static final String JSON_FIELD_GEO_LOCATION			= "geoLocation";			// string
	private static final String JSON_FIELD_VERSION				= "version";				// string
	private static final String JSON_FIELD_ALIVE_AND_OK		  	= "aliveAndOK";				// boolean
	private static final String JSON_FIELD_RESOLVER				= "resolver";				// string
	private static final String JSON_FIELD_PROVISION_EXCEPTION  = "provisionException";		// string
	private static final String JSON_FIELD_PROVISION_LIST		= "provisionList";			// list
	private static final String JSON_FIELD_LOCAL_IP				= "localIp";				// string
	private static final String JSON_FIELD_METADATA				= "metadata";				// list
	private static final String JSON_FIELD_PROVISION_STATUS_MAP = "provisionStatusMap";		// list
	private static final String JSON_FIELD_MAX_PORT			  	= "maximumPort";			// int
	private static final String JSON_FIELD_PARENT				= "parent";					// string
	private static final String JSON_FIELD_MANUAL_IP	  		= "manualIp";				// string
	private static final String JSON_FIELD_PUBLIC_IP	  		= "publicIp";				// string
	private static final String JSON_FIELD_PUBLIC_HOSTNAME		= "publicHostname";			// string
	private static final String JSON_FIELD_IP		  			= "ip";						// string
	private static final String JSON_FIELD_OVERLAY_PROFILE		= "overlayProfile";			// list
	private static final String JSON_FIELD_MIN_PORT				= "minimumPort";			// integer
	private static final String JSON_FIELD_PROVISION_RESULT		= "provisionResult";		// string
	
	/**
	 * creates a container facade
	 * 
	 * @param fabric8		the ref to fabric8 facade
	 * @param jsonAttribs	the attributes
	 */
	public ContainerDTO(Fabric8Facade fabric8, Map<String, Object> jsonAttribs) {
		super(fabric8, jsonAttribs);
	}
	
	/**
	 * returns if the provisioning is still pending
	 * 
	 * @return
	 */
	public Boolean isProvisioningPending() {
		return getFieldValue(JSON_FIELD_PROVISIONING_PENDING);
	}
	
	/**
	 * returns the location
	 * 
	 * @return
	 */
	public String getLocation() {
		return getFieldValue(JSON_FIELD_LOCATION);
	}
	
	/**
	 * sets the location
	 * 
	 * @param newLocation
	 */
	public void setLocation(String newLocation) {
		setFieldValue(JSON_FIELD_LOCATION, newLocation);
		// TODO: update the location feld remotely
	}
	
	/**
	 * returns the list of jmx domains
	 * 
	 * @return
	 */
	public List<String> getJmxDomains() {
		return getFieldValue(JSON_FIELD_JMX_DOMAINS);
	}
	
	/**
	 * returns true if provisioning is complete
	 * 
	 * @return
	 */
	public Boolean isProvisioningComplete() {
		return getFieldValue(JSON_FIELD_PROVISIONING_COMPLETE);
	}
	
	/**
	 * returns true if the container is the root container of the fabric
	 * 
	 * @return
	 */
	public Boolean isRoot() {
		return getFieldValue(JSON_FIELD_ROOT);
	}
	
	/**
	 * returns the provision status
	 * 
	 * @return	"success" on success
	 */
	public String getProvisionStatus() {
		return getFieldValue(JSON_FIELD_PROVISION_STATUS);
	}
	
	/**
	 * returns the parent container id or null if no parent
	 * 
	 * @return
	 */
	public String getParentId() {
		return getFieldValue(JSON_FIELD_PARENT_ID);
	}
	
	/**
	 * returns the parent
	 * 
	 * @return
	 */
	public ContainerDTO getParent() {
		return fabric8.getContainer(getParentId());
	}
	
	/**
	 * returns all child ids for the container
	 * 
	 * @return	a possibly empty list of child ids 
	 */
	public List<String> getChildrenIds() {
		return getFieldValue(JSON_FIELD_CHILDREN_IDS);
	}
	
	/**
	 * returns the children
	 * 
	 * @return
	 */
	public List<ContainerDTO> getChildren() {
		List<ContainerDTO> children = new ArrayList<ContainerDTO>();
		
		for (String cid : getChildrenIds()) {
			ContainerDTO child = fabric8.getContainer(cid);
			if (child != null) children.add(child);
		}
		
		return children;
	}
	
	/**
	 * returns the pid of the fabric8 container process
	 * 
	 * @return	the process id
	 */
	public Long getProcessId() {
		return getFieldValue(JSON_FIELD_PROCESS_ID);
	}
	
	/**
	 * returns the version id running on this container
	 * 
	 * @return
	 */
	public String getVersionId() {
		return getFieldValue(JSON_FIELD_VERSION_ID);
	}
	
	/**
	 * returns the version
	 * 
	 * @return
	 */
	public VersionDTO getVersion() {
		for (VersionDTO version : fabric8.getVersions()) {
			if (version.getId().equals(getVersionId())) {
				return version;
			}
		}
		return null;
	}
	
	/**
	 * returns the profile ids installed in that container
	 * 
	 * @return
	 */
	public List<String> getProfileIDs() {
		return getFieldValue(JSON_FIELD_PROFILE_IDS);
	}
	
	/**
	 * returns the profiles
	 * 
	 * @return
	 */
	public List<ProfileDTO> getProfiles() {
		List<ProfileDTO> profiles = new ArrayList<ProfileDTO>();
		
		for (String pid : getProfileIDs()) {
			ProfileDTO prof = fabric8.getProfile(getVersionId(), pid);
			if (prof != null) profiles.add(prof);
		}
		
		return profiles;
	}
	
	/**
	 * returns true if this container is an ensemble server
	 * 
	 * @return
	 */
	public Boolean isEnsembleServer() {
		return getFieldValue(JSON_FIELD_ENSEMBLE_SERVER);
	}
	
	/**
	 * returns the JMX connection URL
	 * 
	 * @return
	 */
	public String getJMXUrl() {
		return getFieldValue(JSON_FIELD_JMX_URL);
	}
	
	/**
	 * returns the url to the containers web app
	 * 
	 * @return
	 */
	public String getContainerWebURL() {
		return getFieldValue(JSON_FIELD_HTTP_URL);
	}
	
	/**
	 * returns the containers debug port
	 * 
	 * @return
	 */
	public Integer getDebugPort() {
		return getFieldValue(JSON_FIELD_DEBUG_PORT);
	}
	
	/**
	 * returns true if this container is managed 
	 * 
	 * @return
	 */
	public Boolean isManaged() {
		return getFieldValue(JSON_FIELD_MANAGED);
	}

	/**
	 * returns the container type
	 * 
	 * @return
	 */
	public String getType() {
		return getFieldValue(JSON_FIELD_TYPE);
	}
	
	/**
	 * returns true if the container is alive
	 * 
	 * @return
	 */
	public Boolean isAlive() {
		return getFieldValue(JSON_FIELD_ALIVE);
	}
	
	/**
	 * returns the ssh url
	 * 
	 * @return
	 */
	public String getSshUrl() {
		return getFieldValue(JSON_FIELD_SSH_URL);
	}
	
	/**
	 * returns the jolokia url
	 * 
	 * @return
	 */
	public String getJolokiaUrl() {
		return getFieldValue(JSON_FIELD_JOLOKIA_URL);
	}
	
	/**
	 * sets the version of this container
	 * 
	 * @param version
	 */
	public void setVersion(VersionDTO version) {
		setVersion(version.getId());
	}
	
	/**
	 * sets the version of this container
	 * 
	 * @param versionId
	 */
	public void setVersion(String versionId) {
        setFieldValue(JSON_FIELD_VERSION_ID, versionId);
        fabric8.setVersionForContainer(this.getId(), versionId);
    }

	/**
	 * sets the profiles for the container
	 * 
	 * @param profileIds
	 */
	public void setProfileDTOs(List<ProfileDTO> profiles) {
		List<String> ids = new ArrayList<String>();
		for (ProfileDTO p : profiles) {
			ids.add(p.getId());
		}
		setProfiles(ids);
	}
	
	/**
	 * sets the profiles for the container
	 * 
	 * @param profileIds
	 */
    public void setProfiles(List<String> profileIds) {
        setFieldValue(JSON_FIELD_PROFILE_IDS, profileIds);
    	fabric8.setProfilesForContainer(getId(), getVersionId(), profileIds);
    }

    /**
     * adds profiles to the container
     * 
     * @param profileIds
     */
    public void addProfiles(String... profileIds) {
    	List<String> ids = getFieldValue(JSON_FIELD_PROFILE_IDS);
    	for (String pId : profileIds) {
    		if (ids.contains(pId) == false) {
    			ids.add(pId);
    		}
    	}
    	setFieldValue(JSON_FIELD_PROFILE_IDS, ids);
    	fabric8.addProfilesToContainer(getId(), profileIds);
    }

    /**
     * removes profiles from the container
     * 
     * @param profileIds
     */
    public void removeProfiles(String... profileIds) {
        List<String> ids = getFieldValue(JSON_FIELD_PROFILE_IDS);
        ids.removeAll(JolokiaHelpers.toList(profileIds));
        setFieldValue(JSON_FIELD_PROFILE_IDS, ids);
        fabric8.removeProfiles(getId(), profileIds);
    }
    
    /**
     * starts the container
     */
    public void start() {
    	fabric8.startContainer(this);
    }

    /**
     * stops the container
     */
    public void stop() {
    	fabric8.stopContainer(this);
    }

    /**
     * destroys the container
     */
    public void destroy() {
    	fabric8.destroyContainer(this);
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
	 * [
	 * {
	 * "provisionChecksums":{"mvn:io.fabric8\/fabric-groovy\/1.1.0-SNAPSHOT":"1633973826","mvn:io.fabric8\/fabric-core-agent-ssh\/1.1.0-SNAPSHOT":"987017636","mvn:io.fabric8\/fabric-configadmin\/1.1.0-SNAPSHOT":"3723222791","mvn:io.fabric8\/fabric-commands\/1.1.0-SNAPSHOT":"3848777212","mvn:io.fabric8\/fabric-extender-listener\/1.1.0-SNAPSHOT":"1352503653","mvn:io.fabric8\/fabric-redirect\/1.1.0-SNAPSHOT":"2535566266","mvn:io.fabric8\/fabric-rest\/1.1.0-SNAPSHOT":"2471432393","mvn:io.fabric8\/common-util\/1.1.0-SNAPSHOT":"2588867722","mvn:io.fabric8\/fabric-process-container\/1.1.0-SNAPSHOT":"3965729124","mvn:io.fabric8\/fabric-zookeeper\/1.1.0-SNAPSHOT":"2570259931","mvn:io.fabric8\/fabric-cxf\/1.1.0-SNAPSHOT":"3678820623","mvn:io.fabric8\/archetype-builder\/1.1.0-SNAPSHOT":"54394926","mvn:io.fabric8\/fabric-agent\/1.1.0-SNAPSHOT":"1471238054","mvn:io.fabric8.insight\/insight-log\/1.1.0-SNAPSHOT":"2505058383","mvn:io.fabric8.fab\/fab-osgi\/1.1.0-SNAPSHOT":"3984067192","mvn:io.fabric8\/fabric-groups\/1.1.0-SNAPSHOT":"1572153865","mvn:io.fabric8\/fabric-git-server\/1.1.0-SNAPSHOT":"765162932","mvn:io.fabric8\/fabric-ssl\/1.1.0-SNAPSHOT":"1153413021","mvn:io.fabric8\/fabric-agent-commands\/1.1.0-SNAPSHOT":"1122286103","mvn:io.fabric8\/fabric-maven-proxy\/1.1.0-SNAPSHOT":"1950699871","mvn:io.fabric8\/fabric-git-hawtio\/1.1.0-SNAPSHOT":"1246524323","mvn:io.fabric8\/archetype-commands\/1.1.0-SNAPSHOT":"912341087","mvn:io.fabric8\/fabric-features-service\/1.1.0-SNAPSHOT":"2110378148","mvn:io.fabric8\/process-manager\/1.1.0-SNAPSHOT":"1945560218","mvn:io.fabric8\/fabric-boot-commands\/1.1.0-SNAPSHOT":"425471117","mvn:io.fabric8.runtime\/fabric-runtime-container-karaf-registration\/1.1.0-SNAPSHOT":"3980322997","mvn:io.fabric8\/fabric-api\/1.1.0-SNAPSHOT":"920836927","mvn:io.fabric8\/fabric-cxf-registry\/1.1.0-SNAPSHOT":"49458888","mvn:io.fabric8\/fabric-project-deployer\/1.1.0-SNAPSHOT":"2976665523","mvn:io.fabric8\/fabric-jaas\/1.1.0-SNAPSHOT":"771407167","mvn:io.fabric8\/fabric-core\/1.1.0-SNAPSHOT":"1493931539","mvn:io.fabric8\/fabric-git\/1.1.0-SNAPSHOT":"3385544324","mvn:io.fabric8\/fabric-jolokia\/1.1.0-SNAPSHOT":"2693850473","mvn:io.fabric8\/fabric-web\/1.1.0-SNAPSHOT":"4144179186"},
	 * "localHostname":"westeros",
	 * "sshUrl":"westeros:8101",
	 * "root":true,
	 * "processId":10732,
	 * "location":"",
	 * "geoLocation":"52.0333,11.25",
	 * "children":[],
	 * "type":"karaf",
	 * "alive":true,
	 * "version":"1.0",
	 * "id":"root",
	 * "parentId":null,
	 * "provisionStatus":"success",
	 * "aliveAndOK":true,
	 * "resolver":"localhostname",
	 * "provisionException":null,
	 * "childrenIds":[],
	 * "provisionList":["mvn:biz.aQute.bnd\/bndlib\/2.1.0","mvn:biz.aQute\/bndlib\/1.43.0","mvn:com.fasterxml.jackson.core\/jackson-annotations\/2.4.1","mvn:com.fasterxml.jackson.core\/jackson-core\/2.4.1","mvn:com.fasterxml.jackson.core\/jackson-databind\/2.4.1","mvn:com.fasterxml.jackson.jaxrs\/jackson-jaxrs-base\/2.4.1","mvn:com.fasterxml.jackson.jaxrs\/jackson-jaxrs-json-provider\/2.4.1","mvn:com.fasterxml.jackson.module\/jackson-module-jaxb-annotations\/2.4.1","mvn:com.google.guava\/guava\/15.0","mvn:commons-beanutils\/commons-beanutils\/1.8.3","mvn:commons-codec\/commons-codec\/1.6","mvn:commons-collections\/commons-collections\/3.2.1","mvn:commons-io\/commons-io\/2.4","mvn:io.fabric8.fab\/fab-osgi\/1.1.0-SNAPSHOT","mvn:io.fabric8.insight\/insight-log\/1.1.0-SNAPSHOT","mvn:io.fabric8.runtime\/fabric-runtime-container-karaf-registration\/1.1.0-SNAPSHOT","mvn:io.fabric8\/archetype-builder\/1.1.0-SNAPSHOT","mvn:io.fabric8\/archetype-commands\/1.1.0-SNAPSHOT","mvn:io.fabric8\/common-util\/1.1.0-SNAPSHOT","mvn:io.fabric8\/fabric-agent-commands\/1.1.0-SNAPSHOT","mvn:io.fabric8\/fabric-agent\/1.1.0-SNAPSHOT","mvn:io.fabric8\/fabric-api\/1.1.0-SNAPSHOT","mvn:io.fabric8\/fabric-boot-commands\/1.1.0-SNAPSHOT","mvn:io.fabric8\/fabric-commands\/1.1.0-SNAPSHOT","mvn:io.fabric8\/fabric-configadmin\/1.1.0-SNAPSHOT","mvn:io.fabric8\/fabric-core-agent-ssh\/1.1.0-SNAPSHOT","mvn:io.fabric8\/fabric-core\/1.1.0-SNAPSHOT","mvn:io.fabric8\/fabric-cxf-registry\/1.1.0-SNAPSHOT","mvn:io.fabric8\/fabric-cxf\/1.1.0-SNAPSHOT","mvn:io.fabric8\/fabric-extender-listener\/1.1.0-SNAPSHOT","mvn:io.fabric8\/fabric-features-service\/1.1.0-SNAPSHOT","mvn:io.fabric8\/fabric-git-hawtio\/1.1.0-SNAPSHOT","mvn:io.fabric8\/fabric-git-server\/1.1.0-SNAPSHOT","mvn:io.fabric8\/fabric-git\/1.1.0-SNAPSHOT","mvn:io.fabric8\/fabric-groovy\/1.1.0-SNAPSHOT","mvn:io.fabric8\/fabric-groups\/1.1.0-SNAPSHOT","mvn:io.fabric8\/fabric-jaas\/1.1.0-SNAPSHOT","mvn:io.fabric8\/fabric-jolokia\/1.1.0-SNAPSHOT","mvn:io.fabric8\/fabric-maven-proxy\/1.1.0-SNAPSHOT","mvn:io.fabric8\/fabric-process-container\/1.1.0-SNAPSHOT","mvn:io.fabric8\/fabric-project-deployer\/1.1.0-SNAPSHOT","mvn:io.fabric8\/fabric-redirect\/1.1.0-SNAPSHOT","mvn:io.fabric8\/fabric-rest\/1.1.0-SNAPSHOT","mvn:io.fabric8\/fabric-ssl\/1.1.0-SNAPSHOT","mvn:io.fabric8\/fabric-web\/1.1.0-SNAPSHOT","mvn:io.fabric8\/fabric-zookeeper\/1.1.0-SNAPSHOT","mvn:io.fabric8\/process-manager\/1.1.0-SNAPSHOT","mvn:io.hawt.swagger\/hawtio-swagger-ui\/1.0.2\/war","mvn:io.hawt\/hawtio-fabric8-branding\/1.4.17\/war","mvn:io.hawt\/hawtio-json-schema-mbean\/1.4.17","mvn:io.hawt\/hawtio-karaf-terminal\/1.4.17\/war","mvn:io.hawt\/hawtio-osgi-jmx\/1.4.17","mvn:io.hawt\/hawtio-web\/1.4.17\/war","mvn:javax.mail\/mail\/1.4.4","mvn:javax.validation\/validation-api\/1.1.0.Final","mvn:jline\/jline\/2.11","mvn:org.apache.aries.blueprint\/org.apache.aries.blueprint.api\/1.0.1","mvn:org.apache.aries.blueprint\/org.apache.aries.blueprint.cm\/1.0.4","mvn:org.apache.aries.blueprint\/org.apache.aries.blueprint.core\/1.4.1","mvn:org.apache.aries.jmx\/org.apache.aries.jmx.api\/1.1.1","mvn:org.apache.aries.jmx\/org.apache.aries.jmx.blueprint.api\/1.1.0","mvn:org.apache.aries.jmx\/org.apache.aries.jmx.blueprint.core\/1.1.0","mvn:org.apache.aries.jmx\/org.apache.aries.jmx.core\/1.1.2","mvn:org.apache.aries.proxy\/org.apache.aries.proxy.api\/1.0.1","mvn:org.apache.aries.proxy\/org.apache.aries.proxy.impl\/1.0.3","mvn:org.apache.aries\/org.apache.aries.util\/1.1.0","mvn:org.apache.camel.karaf\/camel-karaf-commands\/2.13.2","mvn:org.apache.camel\/camel-core\/2.13.2","mvn:org.apache.camel\/camel-mvel\/2.13.2","mvn:org.apache.cxf.karaf\/cxf-karaf-commands\/2.7.11","mvn:org.apache.cxf\/cxf-api\/2.7.11","mvn:org.apache.cxf\/cxf-rt-bindings-soap\/2.7.11","mvn:org.apache.cxf\/cxf-rt-bindings-xml\/2.7.11","mvn:org.apache.cxf\/cxf-rt-core\/2.7.11","mvn:org.apache.cxf\/cxf-rt-databinding-jaxb\/2.7.11","mvn:org.apache.cxf\/cxf-rt-frontend-jaxrs\/2.7.11","mvn:org.apache.cxf\/cxf-rt-frontend-jaxws\/2.7.11","mvn:org.apache.cxf\/cxf-rt-frontend-simple\/2.7.11","mvn:org.apache.cxf\/cxf-rt-management\/2.7.11","mvn:org.apache.cxf\/cxf-rt-rs-extension-providers\/2.7.11","mvn:org.apache.cxf\/cxf-rt-rs-extension-search\/2.7.11","mvn:org.apache.cxf\/cxf-rt-transports-http\/2.7.11","mvn:org.apache.felix\/org.apache.felix.configadmin\/1.6.0","mvn:org.apache.felix\/org.apache.felix.eventadmin\/1.3.2","mvn:org.apache.felix\/org.apache.felix.fileinstall\/3.4.0","mvn:org.apache.felix\/org.apache.felix.gogo.runtime\/0.12.1","mvn:org.apache.felix\/org.apache.felix.metatype\/1.0.10","mvn:org.apache.felix\/org.apache.felix.scr\/1.8.2","mvn:org.apache.geronimo.specs\/geronimo-annotation_1.0_spec\/1.1.1","mvn:org.apache.geronimo.specs\/geronimo-annotation_1.1_spec\/1.0.1","mvn:org.apache.geronimo.specs\/geronimo-osgi-registry\/1.1","mvn:org.apache.geronimo.specs\/geronimo-servlet_3.0_spec\/1.0","mvn:org.apache.karaf.admin\/org.apache.karaf.admin.core\/2.4.0.redhat-620004","mvn:org.apache.karaf.admin\/org.apache.karaf.admin.management\/2.4.0.redhat-620004","mvn:org.apache.karaf.deployer\/org.apache.karaf.deployer.blueprint\/2.4.0.redhat-620004","mvn:org.apache.karaf.deployer\/org.apache.karaf.deployer.spring\/2.4.0.redhat-620004","mvn:org.apache.karaf.deployer\/org.apache.karaf.deployer.wrap\/2.4.0.redhat-620004","mvn:org.apache.karaf.features\/org.apache.karaf.features.command\/2.4.0.redhat-620004","mvn:org.apache.karaf.features\/org.apache.karaf.features.core\/2.4.0.redhat-620004","mvn:org.apache.karaf.jaas\/org.apache.karaf.jaas.command\/2.4.0.redhat-620004","mvn:org.apache.karaf.jaas\/org.apache.karaf.jaas.config\/2.4.0.redhat-620004","mvn:org.apache.karaf.jaas\/org.apache.karaf.jaas.modules\/2.4.0.redhat-620004","mvn:org.apache.karaf.management.mbeans\/org.apache.karaf.management.mbeans.scr\/2.4.0.redhat-620004","mvn:org.apache.karaf.management\/org.apache.karaf.management.server\/2.4.0.redhat-620004","mvn:org.apache.karaf.service\/org.apache.karaf.service.guard\/2.4.0.redhat-620004","mvn:org.apache.karaf.shell\/org.apache.karaf.shell.commands\/2.4.0.redhat-620004","mvn:org.apache.karaf.shell\/org.apache.karaf.shell.config\/2.4.0.redhat-620004","mvn:org.apache.karaf.shell\/org.apache.karaf.shell.console\/2.4.0.redhat-620004","mvn:org.apache.karaf.shell\/org.apache.karaf.shell.dev\/2.4.0.redhat-620004","mvn:org.apache.karaf.shell\/org.apache.karaf.shell.log\/2.4.0.redhat-620004","mvn:org.apache.karaf.shell\/org.apache.karaf.shell.osgi\/2.4.0.redhat-620004","mvn:org.apache.karaf.shell\/org.apache.karaf.shell.packages\/2.4.0.redhat-620004","mvn:org.apache.karaf.shell\/org.apache.karaf.shell.scr\/2.4.0.redhat-620004","mvn:org.apache.karaf.shell\/org.apache.karaf.shell.ssh\/2.4.0.redhat-620004","mvn:org.apache.mina\/mina-core\/2.0.7","mvn:org.apache.servicemix.bundles\/org.apache.servicemix.bundles.jackson-module-scala\/2.1.5_2","mvn:org.apache.servicemix.bundles\/org.apache.servicemix.bundles.jasypt\/1.9.1_2","mvn:org.apache.servicemix.bundles\/org.apache.servicemix.bundles.javassist\/3.12.1.GA_3","mvn:org.apache.servicemix.bundles\/org.apache.servicemix.bundles.jaxb-impl\/2.2.1.1_2","mvn:org.apache.servicemix.bundles\/org.apache.servicemix.bundles.jaxb-xjc\/2.2.1.1_2","mvn:org.apache.servicemix.bundles\/org.apache.servicemix.bundles.jsch\/0.1.49_1","mvn:org.apache.servicemix.bundles\/org.apache.servicemix.bundles.json4s\/3.2.4_1","mvn:org.apache.servicemix.bundles\/org.apache.servicemix.bundles.reflections\/0.9.8_1","mvn:org.apache.servicemix.bundles\/org.apache.servicemix.bundles.swagger-annotations\/1.3.2_1","mvn:org.apache.servicemix.bundles\/org.apache.servicemix.bundles.swagger-core\/1.3.2_1","mvn:org.apache.servicemix.bundles\/org.apache.servicemix.bundles.swagger-jaxrs\/1.3.2_1","mvn:org.apache.servicemix.bundles\/org.apache.servicemix.bundles.wsdl4j\/1.6.3_1","mvn:org.apache.servicemix.specs\/org.apache.servicemix.specs.jsr339-api-m10\/2.2.0","mvn:org.apache.sshd\/sshd-core\/0.12.0","mvn:org.apache.ws.xmlschema\/xmlschema-core\/2.1.0","mvn:org.apache.xbean\/xbean-asm4-shaded\/3.16","mvn:org.apache.xbean\/xbean-finder-shaded\/3.16","mvn:org.bouncycastle\/bcprov-jdk15on\/1.49","mvn:org.codehaus.groovy\/groovy-all\/2.3.6","mvn:org.codehaus.woodstox\/stax2-api\/3.1.4","mvn:org.codehaus.woodstox\/woodstox-core-asl\/4.2.1","mvn:org.eclipse.jetty.aggregate\/jetty-all-server\/8.1.14.v20131031","mvn:org.jboss.gravia\/gravia-provision\/1.1.0.Beta38","mvn:org.jboss.gravia\/gravia-repository\/1.1.0.Beta38","mvn:org.jboss.gravia\/gravia-resolver\/1.1.0.Beta38","mvn:org.jboss.gravia\/gravia-resource\/1.1.0.Beta38","mvn:org.jboss.gravia\/gravia-runtime-api\/1.1.0.Beta38","mvn:org.jboss.gravia\/gravia-runtime-osgi\/1.1.0.Beta38","mvn:org.mvel\/mvel2\/2.2.1.Final","mvn:org.ops4j.base\/ops4j-base-lang\/1.4.0","mvn:org.ops4j.base\/ops4j-base-monitors\/1.4.0","mvn:org.ops4j.base\/ops4j-base-net\/1.4.0","mvn:org.ops4j.base\/ops4j-base-util-property\/1.4.0","mvn:org.ops4j.pax.logging\/pax-logging-api\/1.7.3","mvn:org.ops4j.pax.logging\/pax-logging-service\/1.7.3","mvn:org.ops4j.pax.swissbox\/pax-swissbox-bnd\/1.6.0","mvn:org.ops4j.pax.swissbox\/pax-swissbox-core\/1.6.0","mvn:org.ops4j.pax.swissbox\/pax-swissbox-optional-jcl\/1.7.0","mvn:org.ops4j.pax.swissbox\/pax-swissbox-property\/1.6.0","mvn:org.ops4j.pax.swissbox\/pax-swissbox-property\/1.7.0","mvn:org.ops4j.pax.url\/pax-url-aether\/2.1.0","mvn:org.ops4j.pax.url\/pax-url-commons\/1.4.2","mvn:org.ops4j.pax.url\/pax-url-war\/1.4.2","mvn:org.ops4j.pax.url\/pax-url-wrap\/2.1.0\/jar\/uber","mvn:org.ops4j.pax.web\/pax-web-api\/3.0.7","mvn:org.ops4j.pax.web\/pax-web-deployer\/3.0.7","mvn:org.ops4j.pax.web\/pax-web-extender-war\/3.0.7","mvn:org.ops4j.pax.web\/pax-web-extender-whiteboard\/3.0.7","mvn:org.ops4j.pax.web\/pax-web-jetty\/3.0.7","mvn:org.ops4j.pax.web\/pax-web-jsp\/3.0.7","mvn:org.ops4j.pax.web\/pax-web-runtime\/3.0.7","mvn:org.ops4j.pax.web\/pax-web-spi\/3.0.7","mvn:org.ow2.asm\/asm-all\/5.0.3","mvn:org.scala-lang\/scala-library\/2.10.2"],
	 * "localIp":"192.168.178.35",
	 * "metadata":null,
	 * "provisionStatusMap":{"ProvisionStatus":"success","BlueprintStatus":"STARTED","SpringStatus":"STARTED"},
	 * "jmxDomains":["JMImplementation","com.sun.management","connector","hawtio","io.fabric8","io.fabric8.cxf","io.fabric8.insight","java.lang","java.nio","java.util.logging","jmx4perl","jolokia","org.apache.aries.blueprint","org.apache.cxf","org.apache.karaf","org.apache.zookeeper","org.jboss.gravia","osgi.compendium","osgi.core"],
	 * "maximumPort":0,
	 * "provisioningPending":false,
	 * "jmxUrl":"service:jmx:rmi:\/\/westeros:44444\/jndi\/rmi:\/\/westeros:1099\/karaf-root",
	 * "parent":null,
	 * "manualIp":"null",
	 * "publicIp":"null",
	 * "publicHostname":"null",
	 * "ip":"westeros",
	 * "provisioningComplete":true,
	 * "managed":true,
	 * "profileIds":["fabric","fabric-ensemble-0000-1"],
	 * "httpUrl":"http:\/\/westeros:8181",
	 * "debugPort":null,
	 * "versionId":"1.0",
	 * "overlayProfile":{"libraries":[],"parents":[],"repositories":["mvn:org.apache.karaf.assemblies.features\/standard\/2.4.0.redhat-620004\/xml\/features","mvn:org.apache.camel.karaf\/apache-camel\/2.13.2\/xml\/features","mvn:org.apache.karaf.assemblies.features\/enterprise\/2.4.0.redhat-620004\/xml\/features","mvn:org.apache.cxf.karaf\/apache-cxf\/2.7.11\/xml\/features","mvn:io.fabric8\/fabric8-karaf\/1.1.0-SNAPSHOT\/xml\/features","mvn:io.hawt\/hawtio-karaf\/1.4.17\/xml\/features","mvn:org.apache.karaf.assemblies.features\/spring\/2.4.0.redhat-620004\/xml\/features","mvn:org.apache.activemq\/activemq-karaf\/5.10.0\/xml\/features"],"endorsedLibraries":[],"profileHash":null,"optionals":["mvn:org.ops4j.base\/ops4j-base-lang\/1.4.0"],"version":"1.0","id":"#container-root","fabs":[],"features":["hawtio-fabric8-branding","fabric-agent","fabric-maven-proxy","fabric-git","fabric-hawtio-swagger","fabric-commands","fabric-groovy","jolokia","fabric-git-server","fabric-core","fabric-web","fabric-hawtio","war","fabric-ssh","fabric-jaas","fabric-redirect","fabric-agent-commands","insight-log","fabric-process-container","fabric-archetype-commands","swagger","karaf"],"configurationFileNames":["org.apache.cxf.osgi.properties","io.fabric8.zookeeper.properties","org.ops4j.pax.logging.properties","jetty.xml","io.fabric8.jolokia.properties","io.fabric8.version.properties","io.fabric8.zookeeper.server-0000.properties","jmx.acl.whitelist.properties","io.fabric8.agent.properties","io.fabric8.maven.properties","icon.svg","jmx.acl.properties","org.ops4j.pax.web.properties","org.ops4j.pax.url.mvn.properties","io.fabric8.jaas.properties","io.fabric8.docker.provider.properties","io.fabric8.insight.metrics.json","welcome.dashboard"],"tags":["#container"],"abstract":true,"containers":[],"containerCount":0,"parentIds":[],"overrides":[],"childIds":[],"fileConfigurations":["org.apache.cxf.osgi.properties","io.fabric8.zookeeper.properties","org.ops4j.pax.logging.properties","jetty.xml","io.fabric8.jolokia.properties","io.fabric8.version.properties","io.fabric8.zookeeper.server-0000.properties","jmx.acl.whitelist.properties","io.fabric8.agent.properties","io.fabric8.maven.properties","icon.svg","jmx.acl.properties","org.ops4j.pax.web.properties","org.ops4j.pax.url.mvn.properties","io.fabric8.jaas.properties","io.fabric8.docker.provider.properties","io.fabric8.insight.metrics.json","welcome.dashboard"],"bundles":[],"overlay":false,"hidden":true,"summaryMarkdown":null,"configurations":["org.apache.cxf.osgi.properties","io.fabric8.zookeeper.properties","org.ops4j.pax.logging.properties","jetty.xml","io.fabric8.jolokia.properties","io.fabric8.version.properties","io.fabric8.zookeeper.server-0000.properties","jmx.acl.whitelist.properties","io.fabric8.agent.properties","io.fabric8.maven.properties","icon.svg","jmx.acl.properties","org.ops4j.pax.web.properties","org.ops4j.pax.url.mvn.properties","io.fabric8.jaas.properties","io.fabric8.docker.provider.properties","io.fabric8.insight.metrics.json","welcome.dashboard"],"locked":false,"extensionLibraries":[],"iconURL":"\/version\/1.0\/profile\/#container-root\/file\/icon.svg"},
	 * "profiles":["fabric","fabric-ensemble-0000-1"],
	 * "ensembleServer":true,
	 * "jolokiaUrl":"http:\/\/westeros:8181\/jolokia",
	 * "minimumPort":0,
	 * "provisionResult":"success"
	 * }]
	 */
}
