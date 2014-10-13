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
package org.fusesource.ide.fabric8.tests.cases;

import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;

import org.fusesource.ide.fabric8.core.connector.Fabric8Connector;
import org.fusesource.ide.fabric8.core.connector.Fabric8ConnectorType;
import org.fusesource.ide.fabric8.core.connector.Fabric8Facade;
import org.fusesource.ide.fabric8.core.dto.ContainerDTO;
import org.fusesource.ide.fabric8.core.dto.ProfileDTO;
import org.fusesource.ide.fabric8.core.dto.VersionDTO;
import org.fusesource.ide.fabric8.tests.utils.Fabric8TestHelpers;
import org.junit.Test;

/**
 * @author lhein
 */
public class Fabric8DTOTest extends TestCase {
	
    private boolean doTests = false;
    
    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        String s = System.getProperty("localFabric", "false");
        if (s != null || s.trim().length()>0) {
            this.doTests = Boolean.parseBoolean(s);
        }
    }
    
	@Test
	public void testGetContainers() throws Exception {
	    if (!this.doTests) return;
		Fabric8ConnectorType connectorType = Fabric8TestHelpers.getJolokiaConnector();
		Fabric8Connector con = new Fabric8Connector(connectorType);
		try {
			assertNotNull("Connector is null!", con);

			con.connect();
			assertNotNull("Connector Type is null!", con.getConnection());
			
			Fabric8Facade fabric8 = con.getConnection().getFabricFacade();
			assertNotNull("Fabric8 DTO is null!", fabric8);
			
			List<ContainerDTO> containers = fabric8.getContainers();
			assertNotNull("getContainers() returned not allowed value NULL!", containers);
			
			System.out.println("Found containers: " + containers.size());
			for (ContainerDTO container : containers) {
				 System.out.println("Found container: " + container.getId());
			}
		} catch (IOException ex) {
			fail(ex.getMessage());
		} finally {
			con.disconnect();
		}
	}	
	
	@Test
	public void testGetRootContainer() throws Exception {
	    if (!this.doTests) return;
		Fabric8ConnectorType connectorType = Fabric8TestHelpers.getJolokiaConnector();
		Fabric8Connector con = new Fabric8Connector(connectorType);
		try {
			assertNotNull("Connector is null!", con);

			con.connect();
			assertNotNull("Connector Type is null!", con.getConnection());
			
			Fabric8Facade fabric8 = con.getConnection().getFabricFacade();
			assertNotNull("Fabric8 DTO is null!", fabric8);
			
			ContainerDTO container = fabric8.getContainer("root");
			assertNotNull("getContainer('root') returned NULL!", container);
			
			assertTrue("Root container is not root!", container.isRoot());			
		} catch (IOException ex) {
			fail(ex.getMessage());
		} finally {
			con.disconnect();
		}
	}
	
	@Test
	public void testRootContainerProvisionStatus() throws Exception {
	    if (!this.doTests) return;
		Fabric8ConnectorType connectorType = Fabric8TestHelpers.getJolokiaConnector();
		Fabric8Connector con = new Fabric8Connector(connectorType);
		try {
			assertNotNull("Connector is null!", con);

			con.connect();
			assertNotNull("Connector Type is null!", con.getConnection());
			
			Fabric8Facade fabric8 = con.getConnection().getFabricFacade();
			assertNotNull("Fabric8 DTO is null!", fabric8);
			
			ContainerDTO container = fabric8.getContainer("root");
			assertNotNull("getContainer('root') returned NULL!", container);
			
			assertNotNull("Root container has no provision status!", container.getProvisionStatus());		
		} catch (IOException ex) {
			fail(ex.getMessage());
		} finally {
			con.disconnect();
		}
	}
	
	@Test
	public void testRootContainerParentId() throws Exception {
	    if (!this.doTests) return;
		Fabric8ConnectorType connectorType = Fabric8TestHelpers.getJolokiaConnector();
		Fabric8Connector con = new Fabric8Connector(connectorType);
		try {
			assertNotNull("Connector is null!", con);

			con.connect();
			assertNotNull("Connector Type is null!", con.getConnection());
			
			Fabric8Facade fabric8 = con.getConnection().getFabricFacade();
			assertNotNull("Fabric8 DTO is null!", fabric8);
			
			ContainerDTO container = fabric8.getContainer("root");
			assertNotNull("getContainer('root') returned NULL!", container);
			
			assertNull("Root container has a parent ?!", container.getParentId());		
		} catch (IOException ex) {
			fail(ex.getMessage());
		} finally {
			con.disconnect();
		}
	}
	
	@Test
	public void testRootContainerChildrenIds() throws Exception {
	    if (!this.doTests) return;
		Fabric8ConnectorType connectorType = Fabric8TestHelpers.getJolokiaConnector();
		Fabric8Connector con = new Fabric8Connector(connectorType);
		try {
			assertNotNull("Connector is null!", con);

			con.connect();
			assertNotNull("Connector Type is null!", con.getConnection());
			
			Fabric8Facade fabric8 = con.getConnection().getFabricFacade();
			assertNotNull("Fabric8 DTO is null!", fabric8);
			
			ContainerDTO container = fabric8.getContainer("root");
			assertNotNull("getContainer('root') returned NULL!", container);
			
			assertNotNull("Root container children ids returned null?!", container.getChildrenIds());		
		} catch (IOException ex) {
			fail(ex.getMessage());
		} finally {
			con.disconnect();
		}
	}
	
	@Test
	public void testRootContainerProcessId() throws Exception {
	    if (!this.doTests) return;
		Fabric8ConnectorType connectorType = Fabric8TestHelpers.getJolokiaConnector();
		Fabric8Connector con = new Fabric8Connector(connectorType);
		try {
			assertNotNull("Connector is null!", con);

			con.connect();
			assertNotNull("Connector Type is null!", con.getConnection());
			
			Fabric8Facade fabric8 = con.getConnection().getFabricFacade();
			assertNotNull("Fabric8 DTO is null!", fabric8);
			
			ContainerDTO container = fabric8.getContainer("root");
			assertNotNull("getContainer('root') returned NULL!", container);
			
			assertTrue("Root container process id  is null?!", container.getProcessId()>=0);		
			System.out.println("Container 'root' is running with PID: " + container.getProcessId());
		} catch (IOException ex) {
			fail(ex.getMessage());
		} finally {
			con.disconnect();
		}
	}
	
	@Test
	public void testRootContainerVersionId() throws Exception {
	    if (!this.doTests) return;
		Fabric8ConnectorType connectorType = Fabric8TestHelpers.getJolokiaConnector();
		Fabric8Connector con = new Fabric8Connector(connectorType);
		try {
			assertNotNull("Connector is null!", con);

			con.connect();
			assertNotNull("Connector Type is null!", con.getConnection());
			
			Fabric8Facade fabric8 = con.getConnection().getFabricFacade();
			assertNotNull("Fabric8 DTO is null!", fabric8);
			
			ContainerDTO container = fabric8.getContainer("root");
			assertNotNull("getContainer('root') returned NULL!", container);
			
			assertNotNull("Root container version id  is null?!", container.getVersionId());		
			System.out.println("Container 'root' is running with version: " + container.getVersionId());
		} catch (IOException ex) {
			fail(ex.getMessage());
		} finally {
			con.disconnect();
		}
	}
	
	@Test
	public void testRootContainerProfileIds() throws Exception {
	    if (!this.doTests) return;
	    Fabric8ConnectorType connectorType = Fabric8TestHelpers.getJolokiaConnector();
		Fabric8Connector con = new Fabric8Connector(connectorType);
		try {
			assertNotNull("Connector is null!", con);

			con.connect();
			assertNotNull("Connector Type is null!", con.getConnection());
			
			Fabric8Facade fabric8 = con.getConnection().getFabricFacade();
			assertNotNull("Fabric8 DTO is null!", fabric8);
			
			ContainerDTO container = fabric8.getContainer("root");
			assertNotNull("getContainer('root') returned NULL!", container);
			
			assertNotNull("Root container profile ids is null?!", container.getProfileIDs());		
		} catch (IOException ex) {
			fail(ex.getMessage());
		} finally {
			con.disconnect();
		}
	}
	
	@Test
	public void testRootContainerEnsembleServer() throws Exception {
	    if (!this.doTests) return;
		Fabric8ConnectorType connectorType = Fabric8TestHelpers.getJolokiaConnector();
		Fabric8Connector con = new Fabric8Connector(connectorType);
		try {
			assertNotNull("Connector is null!", con);

			con.connect();
			assertNotNull("Connector Type is null!", con.getConnection());
			
			Fabric8Facade fabric8 = con.getConnection().getFabricFacade();
			assertNotNull("Fabric8 DTO is null!", fabric8);
			
			ContainerDTO container = fabric8.getContainer("root");
			assertNotNull("getContainer('root') returned NULL!", container);
			
			assertNotNull("Root container ensemble server is null?!", container.isEnsembleServer());		
		} catch (IOException ex) {
			fail(ex.getMessage());
		} finally {
			con.disconnect();
		}
	}
	
	@Test
	public void testRootContainerJMXUrl() throws Exception {
	    if (!this.doTests) return;
		Fabric8ConnectorType connectorType = Fabric8TestHelpers.getJolokiaConnector();
		Fabric8Connector con = new Fabric8Connector(connectorType);
		try {
			assertNotNull("Connector is null!", con);

			con.connect();
			assertNotNull("Connector Type is null!", con.getConnection());
			
			Fabric8Facade fabric8 = con.getConnection().getFabricFacade();
			assertNotNull("Fabric8 DTO is null!", fabric8);
			
			ContainerDTO container = fabric8.getContainer("root");
			assertNotNull("getContainer('root') returned NULL!", container);
			
			assertNotNull("Root container has no JMX url (null)?!", container.getJMXUrl());		
			System.out.println("JMX URL for container 'root' : " + container.getJMXUrl());
		} catch (IOException ex) {
			fail(ex.getMessage());
		} finally {
			con.disconnect();
		}
	}
	
	@Test
	public void testRootContainerWebUrl() throws Exception {
	    if (!this.doTests) return;
		Fabric8ConnectorType connectorType = Fabric8TestHelpers.getJolokiaConnector();
		Fabric8Connector con = new Fabric8Connector(connectorType);
		try {
			assertNotNull("Connector is null!", con);

			con.connect();
			assertNotNull("Connector Type is null!", con.getConnection());
			
			Fabric8Facade fabric8 = con.getConnection().getFabricFacade();
			assertNotNull("Fabric8 DTO is null!", fabric8);
			
			ContainerDTO container = fabric8.getContainer("root");
			assertNotNull("getContainer('root') returned NULL!", container);
			
			assertNotNull("Root container has no web url (null)?!", container.getJMXUrl());		
			System.out.println("Web URL for container 'root' : " + container.getContainerWebURL());
		} catch (IOException ex) {
			fail(ex.getMessage());
		} finally {
			con.disconnect();
		}
	}
	
	@Test
	public void testRootContainerDebugPort() throws Exception {
	    if (!this.doTests) return;
		Fabric8ConnectorType connectorType = Fabric8TestHelpers.getJolokiaConnector();
		Fabric8Connector con = new Fabric8Connector(connectorType);
		try {
			assertNotNull("Connector is null!", con);

			con.connect();
			assertNotNull("Connector Type is null!", con.getConnection());
			
			Fabric8Facade fabric8 = con.getConnection().getFabricFacade();
			assertNotNull("Fabric8 DTO is null!", fabric8);
			
			ContainerDTO container = fabric8.getContainer("root");
			assertNotNull("getContainer('root') returned NULL!", container);
			
			System.out.println("Debug port for container 'root' : " + container.getDebugPort());
		} catch (IOException ex) {
			fail(ex.getMessage());
		} finally {
			con.disconnect();
		}
	}
	
	@Test
	public void testRootContainerManaged() throws Exception {
	    if (!this.doTests) return;
		Fabric8ConnectorType connectorType = Fabric8TestHelpers.getJolokiaConnector();
		Fabric8Connector con = new Fabric8Connector(connectorType);
		try {
			assertNotNull("Connector is null!", con);

			con.connect();
			assertNotNull("Connector Type is null!", con.getConnection());
			
			Fabric8Facade fabric8 = con.getConnection().getFabricFacade();
			assertNotNull("Fabric8 DTO is null!", fabric8);
			
			ContainerDTO container = fabric8.getContainer("root");
			assertNotNull("getContainer('root') returned NULL!", container);
			
			assertNotNull("Root container isManaged returns null?!", container.isManaged());		
		} catch (IOException ex) {
			fail(ex.getMessage());
		} finally {
			con.disconnect();
		}
	}
	
	@Test
	public void testGetCurrentContainer() throws Exception {
	    if (!this.doTests) return;
		Fabric8ConnectorType connectorType = Fabric8TestHelpers.getJolokiaConnector();
		Fabric8Connector con = new Fabric8Connector(connectorType);
		try {
			assertNotNull("Connector is null!", con);

			con.connect();
			assertNotNull("Connector Type is null!", con.getConnection());
			
			Fabric8Facade fabric8 = con.getConnection().getFabricFacade();
			assertNotNull("Fabric8 DTO is null!", fabric8);
			
			ContainerDTO container = fabric8.getCurrentContainer();
			assertNotNull("getCurrentContainer() returned NULL!", container);
		} catch (IOException ex) {
			fail(ex.getMessage());
		} finally {
			con.disconnect();
		}
	}
	
	@Test
	public void testStartContainer() throws Exception {
	    if (!this.doTests) return;
		Fabric8ConnectorType connectorType = Fabric8TestHelpers.getJolokiaConnector();
		Fabric8Connector con = new Fabric8Connector(connectorType);
		try {
			assertNotNull("Connector is null!", con);

			con.connect();
			assertNotNull("Connector Type is null!", con.getConnection());
			
			Fabric8Facade fabric8 = con.getConnection().getFabricFacade();
			assertNotNull("Fabric8 DTO is null!", fabric8);
			
			ContainerDTO container = fabric8.getCurrentContainer();
			assertNotNull("getCurrentContainer() returned NULL!", container);
			
			// TODO: create a new container, start it and check if state is started, delete container
			fabric8.startContainer(container);
		} catch (IOException ex) {
			fail(ex.getMessage());
		} finally {
			con.disconnect();
		}
	}

	@Test
	public void testStopContainer() throws Exception {
	    if (!this.doTests) return;
		Fabric8ConnectorType connectorType = Fabric8TestHelpers.getJolokiaConnector();
		Fabric8Connector con = new Fabric8Connector(connectorType);
		try {
			assertNotNull("Connector is null!", con);

			con.connect();
			assertNotNull("Connector Type is null!", con.getConnection());
			
			Fabric8Facade fabric8 = con.getConnection().getFabricFacade();
			assertNotNull("Fabric8 DTO is null!", fabric8);
			
			ContainerDTO container = fabric8.getCurrentContainer();
			assertNotNull("getCurrentContainer() returned NULL!", container);

			// TODO: create container, start it, check if started, stop it, check if stopped, delete container
			fabric8.stopContainer(container);
		} catch (IOException ex) {
			fail(ex.getMessage());
		} finally {
			con.disconnect();
		}
	}
	
	@Test
	public void testDestroyContainer() throws Exception {
	    if (!this.doTests) return;
		// TODO: create dummy container, check alive, destroy dummy container, try to get the container to check if its deleted
	}
	
	@Test
	public void testGetWebUrl() throws Exception {
	    if (!this.doTests) return;
		Fabric8ConnectorType connectorType = Fabric8TestHelpers.getJolokiaConnector();
		Fabric8Connector con = new Fabric8Connector(connectorType);
		try {
			assertNotNull("Connector is null!", con);

			con.connect();
			assertNotNull("Connector Type is null!", con.getConnection());
			
			Fabric8Facade fabric8 = con.getConnection().getFabricFacade();
			assertNotNull("Fabric8 DTO is null!", fabric8);
			
			String webUrl = fabric8.getWebUrl();
			assertNotNull("Web App Url of Fabric8 is null", webUrl);
		} catch (IOException ex) {
			fail(ex.getMessage());
		} finally {
			con.disconnect();
		}
	}

	@Test
	public void testGetGitUrl() {
	    if (!this.doTests) return;
		Fabric8ConnectorType connectorType = Fabric8TestHelpers.getJolokiaConnector();
		Fabric8Connector con = new Fabric8Connector(connectorType);
		try {
			assertNotNull("Connector is null!", con);

			con.connect();
			assertNotNull("Connector Type is null!", con.getConnection());
			
			Fabric8Facade fabric8 = con.getConnection().getFabricFacade();
			assertNotNull("Fabric8 DTO is null!", fabric8);
			
			String gitUrl = fabric8.getGitUrl();
			assertNotNull("Git Url of Fabric8 is null", gitUrl);
		} catch (IOException ex) {
			fail(ex.getMessage());
		} finally {
			con.disconnect();
		}
	}

	@Test
	public void testGetMavenUploadProxyUrl() {
	    if (!this.doTests) return;
		Fabric8ConnectorType connectorType = Fabric8TestHelpers.getJolokiaConnector();
		Fabric8Connector con = new Fabric8Connector(connectorType);
		try {
			assertNotNull("Connector is null!", con);

			con.connect();
			assertNotNull("Connector Type is null!", con.getConnection());
			
			Fabric8Facade fabric8 = con.getConnection().getFabricFacade();
			assertNotNull("Fabric8 DTO is null!", fabric8);
			
			String url = fabric8.getMavenUploadProxyUrl();
			assertNotNull("Maven Upload Proxy Url of Fabric8 is null", url);
		} catch (IOException ex) {
			fail(ex.getMessage());
		} finally {
			con.disconnect();
		}
	}

	@Test
	public void testGetMavenDownloadProxyUrl() {
	    if (!this.doTests) return;
		Fabric8ConnectorType connectorType = Fabric8TestHelpers.getJolokiaConnector();
		Fabric8Connector con = new Fabric8Connector(connectorType);
		try {
			assertNotNull("Connector is null!", con);

			con.connect();
			assertNotNull("Connector Type is null!", con.getConnection());
			
			Fabric8Facade fabric8 = con.getConnection().getFabricFacade();
			assertNotNull("Fabric8 DTO is null!", fabric8);
			
			String url = fabric8.getMavenDownloadProxyUrl();
			assertNotNull("Maven Download Proxy Url of Fabric8 is null", url);
		} catch (IOException ex) {
			fail(ex.getMessage());
		} finally {
			con.disconnect();
		}
	}
	
	@Test
	public void testGetDefaultVersion() throws Exception {
	    if (!this.doTests) return;
		Fabric8ConnectorType connectorType = Fabric8TestHelpers.getJolokiaConnector();
		Fabric8Connector con = new Fabric8Connector(connectorType);
		try {
			assertNotNull("Connector is null!", con);

			con.connect();
			assertNotNull("Connector Type is null!", con.getConnection());
			
			Fabric8Facade fabric8 = con.getConnection().getFabricFacade();
			assertNotNull("Fabric8 DTO is null!", fabric8);
			
			VersionDTO version = fabric8.getDefaultVersion();
			assertNotNull("Default version is null", version);
		} catch (IOException ex) {
			fail(ex.getMessage());
		} finally {
			con.disconnect();
		}
	}

	@Test
	public void testSetDefaultVersion() throws Exception {
	    if (!this.doTests) return;
		// TODO: FIX TEST
//		Fabric8ConnectorType connectorType = Fabric8TestHelpers.getJolokiaConnector();
//		Fabric8Connector con = new Fabric8Connector(connectorType);
//		try {
//			assertNotNull("Connector is null!", con);
//
//			con.connect();
//			assertNotNull("Connector Type is null!", con.getConnection());
//			
//			Fabric8Facade fabric8 = con.getConnection().getFabricFacade();
//			assertNotNull("Fabric8 DTO is null!", fabric8);
//			
//			VersionDTO oldDefaultVersion = fabric8.getDefaultVersion();
//			assertNotNull("Seems there is no default version set", oldDefaultVersion);
//			
//			VersionDTO newDefaultVersion = fabric8.createVersion("7.77");
//			List<VersionDTO> versions = fabric8.getVersions();
//			boolean found = false;
//			for (VersionDTO v : versions) {
//				if (v.getId().equals(newDefaultVersion.getId())) {
//					found = true;
//					break;
//				}
//			}
//			assertTrue("The created version was not found in backstore...", found == true);
//			
//			fabric8.setDefaultVersion(newDefaultVersion.getId());
//			VersionDTO tmpDefaultVersion = fabric8.getDefaultVersion();
//			assertNotNull("Seems the new default version was not set", tmpDefaultVersion);
//			assertEquals("New set default version doesn't match what we set before", newDefaultVersion.getId(), tmpDefaultVersion.getId());
//			
//			fabric8.setDefaultVersion(oldDefaultVersion.getId());
//			fabric8.deleteVersion(newDefaultVersion.getId());
//		} catch (IOException ex) {
//			fail(ex.getMessage());
//		} finally {
//			con.disconnect();
//		}
	}

	@Test
	public void testGetVersions() throws Exception {
	    if (!this.doTests) return;
		Fabric8ConnectorType connectorType = Fabric8TestHelpers.getJolokiaConnector();
		Fabric8Connector con = new Fabric8Connector(connectorType);
		try {
			assertNotNull("Connector is null!", con);

			con.connect();
			assertNotNull("Connector Type is null!", con.getConnection());
			
			Fabric8Facade fabric8 = con.getConnection().getFabricFacade();
			assertNotNull("Fabric8 DTO is null!", fabric8);
			
			List<VersionDTO> versions = fabric8.getVersions();
			assertNotNull("getVersions returns null", versions);
		} catch (IOException ex) {
			fail(ex.getMessage());
		} finally {
			con.disconnect();
		}
	}
	
	@Test
	public void testDeleteVersion() throws Exception {
	    if (!this.doTests) return;
		// TODO: FIX TEST
//		Fabric8ConnectorType connectorType = Fabric8TestHelpers.getJolokiaConnector();
//		Fabric8Connector con = new Fabric8Connector(connectorType);
//		try {
//			assertNotNull("Connector is null!", con);
//
//			con.connect();
//			assertNotNull("Connector Type is null!", con.getConnection());
//			
//			Fabric8Facade fabric8 = con.getConnection().getFabricFacade();
//			assertNotNull("Fabric8 DTO is null!", fabric8);
//			
//			VersionDTO dummy = fabric8.createVersion("7.77");
//			assertNotNull("createVersion returns null", dummy);
//			
//			List<VersionDTO> versions = fabric8.getVersions();
//			boolean found = false;
//			for (VersionDTO v : versions) {
//				if (v.getId().equals(dummy.getId())) {
//					found = true;
//					break;
//				}
//			}
//			assertTrue("The created version was not found in backstore...", found == true);
//			
//			fabric8.deleteVersion(dummy.getId());
//			
//			versions = fabric8.getVersions();
//			found = false;
//			for (VersionDTO v : versions) {
//				if (v.getId().equals(dummy.getId())) {
//					found = true;
//					break;
//				}
//			}
//			assertFalse("The created version was not deleted from backstore...", found == true);
//		} catch (IOException ex) {
//			fail(ex.getMessage());
//		} finally {
//			con.disconnect();
//		}
	}

	@Test
	public void testCreateVersion() throws Exception {
	    if (!this.doTests) return;
	    // TODO: FIX TEST
//		Fabric8ConnectorType connectorType = Fabric8TestHelpers.getJolokiaConnector();
//		Fabric8Connector con = new Fabric8Connector(connectorType);
//		try {
//			assertNotNull("Connector is null!", con);
//
//			con.connect();
//			assertNotNull("Connector Type is null!", con.getConnection());
//			
//			Fabric8Facade fabric8 = con.getConnection().getFabricFacade();
//			assertNotNull("Fabric8 DTO is null!", fabric8);
//			
//			VersionDTO dummy = fabric8.createVersion("7.77");
//					
//			List<VersionDTO> versions = fabric8.getVersions();
//			boolean found = false;
//			for (VersionDTO v : versions) {
//				if (v.getId().equals(dummy.getId())) {
//					found = true;
//					break;
//				}
//			}
//			assertTrue("The created version was not found in backstore...", found == true);
//			
//			fabric8.deleteVersion(dummy.getId());
//		} catch (IOException ex) {
//			fail(ex.getMessage());
//		} finally {
//			con.disconnect();
//		}
	}

	@Test
	public void testCreateSubVersion() throws Exception {
	    if (!this.doTests) return;
		// TODO: FIX TEST
//		Fabric8ConnectorType connectorType = Fabric8TestHelpers.getJolokiaConnector();
//		Fabric8Connector con = new Fabric8Connector(connectorType);
//		try {
//			assertNotNull("Connector is null!", con);
//
//			con.connect();
//			assertNotNull("Connector Type is null!", con.getConnection());
//			
//			Fabric8Facade fabric8 = con.getConnection().getFabricFacade();
//			assertNotNull("Fabric8 DTO is null!", fabric8);
//			
//			VersionDTO defaultVersion = fabric8.getDefaultVersion();
//			assertNotNull("Default version is null", defaultVersion);
//			
//			VersionDTO dummy = fabric8.createVersion(defaultVersion.getId(), "7.77");
//			assertNotNull("createVersion returns null", dummy);
//			
//			List<VersionDTO> versions = fabric8.getVersions();
//			boolean found = false;
//			for (VersionDTO v : versions) {
//				if (v.getId().equals(dummy.getId())) {
//					found = true;
//					break;
//				}
//			}
//			assertTrue("The created version was not found in backstore...", found == true);
//			
//			fabric8.deleteVersion(dummy.getId());
//		} catch (IOException ex) {
//			fail(ex.getMessage());
//		} finally {
//			con.disconnect();
//		}
	}

	@Test
	public void testGetProfiles() throws Exception {
	    if (!this.doTests) return;
		Fabric8ConnectorType connectorType = Fabric8TestHelpers.getJolokiaConnector();
		Fabric8Connector con = new Fabric8Connector(connectorType);
		try {
			assertNotNull("Connector is null!", con);

			con.connect();
			assertNotNull("Connector Type is null!", con.getConnection());
			
			Fabric8Facade fabric8 = con.getConnection().getFabricFacade();
			assertNotNull("Fabric8 DTO is null!", fabric8);
			
			VersionDTO version = fabric8.getDefaultVersion();
			assertNotNull("Default version is null", version);
			
			List<ProfileDTO> profiles = fabric8.getProfiles(version.getId());
			assertNotNull("getProfiles returns null", profiles);
		} catch (IOException ex) {
			fail(ex.getMessage());
		} finally {
			con.disconnect();
		}
	}

	@Test
	public void testGetProfile() throws Exception {
	    if (!this.doTests) return;
		Fabric8ConnectorType connectorType = Fabric8TestHelpers.getJolokiaConnector();
		Fabric8Connector con = new Fabric8Connector(connectorType);
		try {
			assertNotNull("Connector is null!", con);

			con.connect();
			assertNotNull("Connector Type is null!", con.getConnection());
			
			Fabric8Facade fabric8 = con.getConnection().getFabricFacade();
			assertNotNull("Fabric8 DTO is null!", fabric8);
			
			VersionDTO version = fabric8.getDefaultVersion();
			assertNotNull("Default version is null", version);
			
			ProfileDTO profile = fabric8.getProfile(version.getId(), "default");
			assertNotNull("getProfile returns null for default profile", profile);
		} catch (IOException ex) {
			fail(ex.getMessage());
		} finally {
			con.disconnect();
		}
	}

	@Test
	public void testCreateProfile() throws Exception {
	    if (!this.doTests) return;
		Fabric8ConnectorType connectorType = Fabric8TestHelpers.getJolokiaConnector();
		Fabric8Connector con = new Fabric8Connector(connectorType);
		try {
			assertNotNull("Connector is null!", con);

			con.connect();
			assertNotNull("Connector Type is null!", con.getConnection());
			
			Fabric8Facade fabric8 = con.getConnection().getFabricFacade();
			assertNotNull("Fabric8 DTO is null!", fabric8);
			
			VersionDTO version = fabric8.getDefaultVersion();
			assertNotNull("Default version is null", version);
			
			ProfileDTO profile = fabric8.createProfile(version.getId(), "unitTestProfileIDE");
			assertNotNull("createProfile returns null when created profile", profile);
			
			ProfileDTO profile2 = fabric8.getProfile(version.getId(), "unitTestProfileIDE");
			assertNotNull("getProfile returns null for created profile", profile2);
			
			fabric8.deleteProfile(version.getId(), profile2.getId());
		} catch (IOException ex) {
			fail(ex.getMessage());
		} finally {
			con.disconnect();
		}
	}

	@Test
	public void testDeleteProfile() throws Exception {
	    if (!this.doTests) return;
		Fabric8ConnectorType connectorType = Fabric8TestHelpers.getJolokiaConnector();
		Fabric8Connector con = new Fabric8Connector(connectorType);
		try {
			assertNotNull("Connector is null!", con);

			con.connect();
			assertNotNull("Connector Type is null!", con.getConnection());
			
			Fabric8Facade fabric8 = con.getConnection().getFabricFacade();
			assertNotNull("Fabric8 DTO is null!", fabric8);
			
			VersionDTO version = fabric8.getDefaultVersion();
			assertNotNull("Default version is null", version);
			
			ProfileDTO profile = fabric8.createProfile(version.getId(), "unitTestProfileIDE");
			assertNotNull("createProfile returns null when created profile", profile);
			
			ProfileDTO profile2 = fabric8.getProfile(version.getId(), "unitTestProfileIDE");
			assertNotNull("getProfile returns null for created profile", profile2);
			
			fabric8.deleteProfile(version.getId(), profile2.getId());
			
			profile2 = fabric8.getProfile(version.getId(), "unitTestProfileIDE");
			assertNull("The profile hasn't been deleted from backstore", profile2);			
		} catch (IOException ex) {
			fail(ex.getMessage());
		} finally {
			con.disconnect();
		}
	}
	
	@Test
    public void testSetProfilesForContainer() throws Exception {
	    if (!this.doTests) return;
		// TODO: implement
	}

	@Test
    public void testAddProfilesToContainer() throws Exception {
	    if (!this.doTests) return;
        // TODO: implement
    }

	@Test
    public void testRemoveProfiles() throws Exception {
	    if (!this.doTests) return;
        // TODO: implement
    }
    
	@Test
	public void testSetProfileBundles() throws Exception {
	    if (!this.doTests) return;
        // TODO: implement
	}
	
	@Test
	public void testSetProfileFabs() throws Exception {
	    if (!this.doTests) return;
        // TODO: implement
	}
	
	@Test
	public void testSetProfileFeatures() throws Exception {
	    if (!this.doTests) return;
        // TODO: implement
	}
	
	@Test
	public void testSetProfileOptionals() throws Exception {
	    if (!this.doTests) return;
        // TODO: implement
	}
	
	@Test
	public void testSetProfileOverrides() throws Exception {
	    if (!this.doTests) return;
        // TODO: implement
	}
	
	@Test
	public void testSetProfileParentIds() throws Exception {
	    if (!this.doTests) return;
        // TODO: implement
	}
	
	@Test
	public void testSetProfileRepositories() throws Exception {
	    if (!this.doTests) return;
        // TODO: implement
	}
}
