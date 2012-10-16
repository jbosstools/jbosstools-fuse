package org.fusesource.ide.fabric.navigator.maven;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.maven.cli.MavenCli;
import org.eclipse.core.resources.IContainer;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.m2e.actions.MavenLaunchConstants;
import org.eclipse.m2e.core.MavenPlugin;
import org.fusesource.ide.fabric.FabricPlugin;
import org.fusesource.ide.fabric.navigator.ProfileNode;
import org.fusesource.ide.launcher.ui.ExecutePomActionSupport;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;



/**
 * Deploys the build to the Fabric's maven proxy
 */
public class FabricDeployAction extends ExecutePomActionSupport {
	private static final String SERVERS_TAG  = "servers";
	private static final String SERVER_TAG   = "server";
	private static final String ID_TAG       = "id";
	private static final String USERNAME_TAG = "username";
	private static final String PASSWORD_TAG = "password";

	private ProfileNode node;
	private String deployURI;

	public FabricDeployAction(ProfileNode node) {
		super(FabricInstallAction.CONFIG_TAB_GROUP, FabricInstallAction.CONFIG_TYPE_ID, "deploy");
		this.node = node;
		this.deployURI = this.node.getMavenDeployParameter();
	}

	@Override
	protected void appendAttributes(IContainer basedir,
			ILaunchConfigurationWorkingCopy workingCopy, String goal) {

		List<String> properties = new ArrayList<String>();
		properties.add("altDeploymentRepository=" + deployURI);
		properties.add("retryFailedDeploymentCount=2");

		workingCopy.setAttribute(MavenLaunchConstants.ATTR_PROPERTIES, properties);
		workingCopy.setAttribute(MavenLaunchConstants.ATTR_USER_SETTINGS, prepareNewSettingsFile());
		//		workingCopy.setAttribute(MavenLaunchConstants.ATTR_DEBUG_OUTPUT, true);
	}

	private String prepareNewSettingsFile() {
		// check the users maven settings
		String userSettings = MavenPlugin.getMavenConfiguration().getUserSettingsFile();
		if(userSettings == null || userSettings.length() == 0) {
			userSettings = MavenCli.DEFAULT_USER_SETTINGS_FILE.getAbsolutePath();
		}

		if (userSettings != null && userSettings.trim().length()>0) {
			File mvnConfig = new File(userSettings);
			// now we have to parse the xml config file and check for
			// a servers/server entry named like our fabric
			Document document = readFileToDocument(userSettings);
			String fabServerName = this.node.getFabricNameWithoutSpaces();

			boolean entryExists = checkForFabricServer(document, fabServerName);

			// if yes - no more to do...user knows what he is doing
			if (entryExists) {
				// the entry is already there...use it
			} else {
				// if no - add the server entry for the fabric with auth
				// information and save config to temp folder - then reference
				// to this config file for the next mvn deploy command
				String fabServerUser = this.node.getFabric().getUserName();
				String fabServerPass = this.node.getFabric().getPassword();

				// create a server entry
				NodeList serversList = document.getElementsByTagName(SERVERS_TAG);
				Element serversElement = null;
				if (serversList.getLength() < 1) {
					// no servers element - create one
					serversElement = document.createElement(SERVERS_TAG);
					document.getDocumentElement().appendChild(serversElement);
				} else {
					serversElement = (Element)serversList.item(0);
				}

				Element server = document.createElement(SERVER_TAG);
				serversElement.appendChild(server);

				Element sId = document.createElement(ID_TAG);
				server.appendChild(sId);
				Text textId = document.createTextNode(fabServerName);
				sId.appendChild(textId);

				Element sUser = document.createElement(USERNAME_TAG);
				server.appendChild(sUser);
				Text textUser = document.createTextNode(fabServerUser);
				sUser.appendChild(textUser);

				Element sPass = document.createElement(PASSWORD_TAG);
				server.appendChild(sPass);
				Text textPass = document.createTextNode(fabServerPass);
				sPass.appendChild(textPass);

				// now save the config to a new file
				File newSettings = null;
				try {
					// create the temp file
					newSettings = File.createTempFile("mvnSettings_", "_ide.xml");

					// prepare dom document for writing
					Source source = new DOMSource(document);

					// prepare to write
					Result result = new StreamResult(newSettings);

					// Write the DOM document to the file
					Transformer xformer = TransformerFactory.newInstance().newTransformer();
					xformer.transform(source, result);

					// switch to new maven config
					userSettings = newSettings.getPath();
				} catch (Exception ex) {
					FabricPlugin.getLogger().error(ex);
				} finally {
					// mark file for deletion
					newSettings.deleteOnExit();
				}
			}
		}

		return userSettings;
	}

	private boolean checkForFabricServer(Document document, String fabServerName) {
		boolean exists = false;

		NodeList listOfServers = document.getElementsByTagName(SERVER_TAG);
		int numServers = listOfServers.getLength();
		for (int i=0; i<numServers; i++) {
			Node serverNode = listOfServers.item(i);
			if(serverNode.getNodeType() == Node.ELEMENT_NODE){
				Element serverElement = (Element)serverNode;
				NodeList idList = serverElement.getElementsByTagName(ID_TAG);
				Element idElement = (Element)idList.item(0);
				NodeList textFNList = idElement.getChildNodes();
				String id = textFNList.item(0).getNodeValue().trim();
				if (id.equals(fabServerName)) {
					exists = true;
					break;
				}
			}
		}

		return exists;
	}

	private Document readFileToDocument(String filePath) {
		Document doc = null;
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			File file = new File(filePath);
			if (file.exists() && file.isFile()) {
				doc = docBuilder.parse (file);
			} else {
				doc = docBuilder.newDocument();
				Element root = doc.createElement("settings");
				doc.appendChild(root);
			}
			doc.getDocumentElement().normalize();
		} catch (Exception ex) {
			FabricPlugin.getLogger().error(ex);
		}

		return doc;
	}
}