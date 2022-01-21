/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.ui.bot.tests;

import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizard.SHELL_NAME;
import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardDeploymentType.OPENSHIFT;
import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardDeploymentType.STANDALONE;
import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardRuntimeType.EAP;
import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardRuntimeType.KARAF;
import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardRuntimeType.SPRINGBOOT;

import java.io.File;
import java.io.IOException;

import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.swt.condition.ShellIsAvailable;
import org.eclipse.reddeer.swt.impl.button.FinishButton;
import org.jboss.tools.fuse.reddeer.FileUtils;
import org.jboss.tools.fuse.reddeer.ResourceHelper;
import org.jboss.tools.fuse.reddeer.SupportedCamelVersions;
import org.jboss.tools.fuse.reddeer.editor.CamelEditor;
import org.jboss.tools.fuse.reddeer.editor.DataTransformationEditor;
import org.jboss.tools.fuse.reddeer.editor.SourceEditor;
import org.jboss.tools.fuse.reddeer.projectexplorer.CamelProject;
import org.jboss.tools.fuse.reddeer.utils.JDKCheck;
import org.jboss.tools.fuse.reddeer.utils.JDKTemplateCompatibleChecker;
import org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizard;
import org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardAdvancedPage;
import org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardDeploymentType;
import org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardFirstPage;
import org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardRuntimePage;
import org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardRuntimeType;
import org.jboss.tools.fuse.reddeer.wizard.NewFuseTransformationWizard;
import org.jboss.tools.fuse.reddeer.wizard.NewFuseTransformationWizard.TransformationType;
import org.jboss.tools.fuse.reddeer.wizard.NewFuseTransformationWizard.TypeDefinition;

/**
 * Gathers all mutual functionality for all tests around Data Transformation feature
 * 
 * @author tsedmik
 */
public class DataTransformationDefaultTest extends DefaultTest {

	public static final String DEPLOYMENT_TYPE = System.getProperty("fuseDeploymentType", "OpenShift");
	public static final String RUNTIME_TYPE = System.getProperty("fuseRuntimeType", "SpringBoot");
	public static final String CAMEL_VERSION = System.getProperty("fuseCamelVersion", "2.17.0.redhat-630377");
	public static final String DSL = System.getProperty("fuseDSL", "Blueprint");
	public static final String STAGING_REPOS = System.getProperty("staging.repositories", "false");

	protected static final String PROJECT_NAME = "data-transformation-test";

	/**
	 * This method prepares the camel route for data transformation tests. It assumes that the Camel editor is opened.
	 * It performs the following steps:
	 * <ul>
	 * <li>removes all comments</li>
	 * <li>replaces the camel route definition</li>
	 * </ul>
	 * 
	 * @throws IOException
	 *                         "resources/datatransformation/route-snippet.txt" is not available
	 */
	protected void configureRoute() throws IOException {
		new CamelEditor();
		CamelEditor.switchTab("Source");
		SourceEditor editor = new SourceEditor();

		// remove comments
		int from = editor.getPosition("<!--");
		int to;
		while (from != -1) {
			to = editor.getPosition("-->") + 3;
			editor.selectText(from, to);
			editor.insertText("");
			from = editor.getPosition("<!--");
		}

		// remove defined Camel route
		from = editor.getPosition("<route");
		to = editor.getPosition("</route");
		if (to == -1) {
			to = editor.getPosition("/>") + 2;
		} else {
			to = to + 8;
		}
		editor.selectText(from, to);
		editor.insertText(FileUtils.getFileContent(new File(ResourceHelper.getResourceAbsolutePath(Activator.PLUGIN_ID,
				"resources/datatransformation/route-snippet.txt")).getAbsolutePath()));
		CamelEditor.switchTab("Design");
		editor.save();
	}

	/**
	 * This method copies required files for data transformation tests into the given project
	 * 
	 * @param name
	 *                 Fuse Integration Project name
	 * @throws IOException
	 *                         Resources are not available
	 */
	protected void copyResources(String name) throws IOException {
		File from = new File(
				ResourceHelper.getResourceAbsolutePath(Activator.PLUGIN_ID, "resources/datatransformation/data"));
		File to = new File(new CamelProject(name).getFile(), "src/data");
		FileUtils.copyDirectory(from.getAbsolutePath(), to.getAbsolutePath());
	}

	/**
	 * Creates a project with parameters defined via system properties (see below).
	 * <ul>
	 * <li>-DfuseDeploymentType=... --- OpenShift / Standalone</li>
	 * <li>-DfuseRuntimeType=... --- SpringBoot / Karaf / EAP</li>
	 * <li>-DfuseCamelVersion=... --- e.g. 2.18.1.redhat-000012</li>
	 * <li>-DfuseDSL=... --- Blueprint / Spring</li> --> applicable only for Standalone / Karaf combination
	 * </ul>
	 * 
	 * @param name
	 *                 Name of the Fuse Integration Project
	 */
	protected void createProject(String name) {
		boolean hasJava8 = JDKCheck.isJava8Available();
		boolean hasJava11 = JDKCheck.isJava11Available();
		boolean hasJava17 = JDKCheck.isJava17Available();

		NewFuseIntegrationProjectWizardDeploymentType deploymentType = DEPLOYMENT_TYPE.equals("OpenShift") ? OPENSHIFT
				: STANDALONE;
		NewFuseIntegrationProjectWizardRuntimeType runtimeType;
		switch (RUNTIME_TYPE) {
		case "SpringBoot":
			runtimeType = SPRINGBOOT;
			break;
		case "Karaf":
			runtimeType = KARAF;
			break;
		case "EAP":
			runtimeType = EAP;
			break;
		default:
			runtimeType = KARAF;
		}

		NewFuseIntegrationProjectWizard wiz = new NewFuseIntegrationProjectWizard();
		wiz.open();
		NewFuseIntegrationProjectWizardFirstPage firstPage = new NewFuseIntegrationProjectWizardFirstPage(wiz);
		firstPage.setProjectName(name);
		wiz.next();
		NewFuseIntegrationProjectWizardRuntimePage secondPage = new NewFuseIntegrationProjectWizardRuntimePage(wiz);
		secondPage.setDeploymentType(deploymentType);
		secondPage.setRuntimeType(runtimeType);
		String camelVersion = SupportedCamelVersions.getCamelVersionsWithLabels().get(CAMEL_VERSION);
		if (camelVersion != null) {
			secondPage.selectCamelVersion(camelVersion);
		} else {
			camelVersion = secondPage.getSelectedCamelVersion();
		}
		wiz.next();
		NewFuseIntegrationProjectWizardAdvancedPage lastPage = new NewFuseIntegrationProjectWizardAdvancedPage(wiz);
		if ("Standalone".equals(DEPLOYMENT_TYPE) && "Karaf".equals(RUNTIME_TYPE)) {
			if ("Blueprint".equals(DSL)) {
				lastPage.selectTemplate("Empty", "Empty - Blueprint DSL");
			} else {
				lastPage.selectTemplate("Empty", "Empty - Spring DSL");
			}
		} else {
			lastPage.selectTemplate(lastPage.getAllAvailableTemplates().get(0));
		}
		new FinishButton(wiz).click();

		JDKTemplateCompatibleChecker jdkChecker = new JDKTemplateCompatibleChecker(runtimeType, camelVersion);
		jdkChecker.handleNoStrictlyCompliantJRETemplates(hasJava8, hasJava11, hasJava17, SHELL_NAME);
	}

	/**
	 * Creates data mapping for data transformation tests. It assumes that Data transformation editor is open -> Call
	 * this method after {@link DataTransformationDefaultTest#addDataTransformation()}
	 */
	protected void createMapping() {
		DataTransformationEditor transEditor = new DataTransformationEditor("transformation.xml");
		transEditor.createNewVariable("ORIGIN");
		transEditor.createVariableTransformation("ABCOrder", "ORIGIN", "XyzOrder",
				new String[] { "XyzOrder", "origin" });
		transEditor.createExpressionTransformation("ABCOrder", "Header", "approvalID", "XyzOrder",
				new String[] { "XyzOrder", "approvalCode" });
		transEditor.createTransformation("ABCOrder", new String[] { "ABCOrder", "header", "customerNum" }, "XyzOrder",
				new String[] { "XyzOrder", "custId" });
		transEditor.createTransformation("ABCOrder", new String[] { "ABCOrder", "header", "orderNum" }, "XyzOrder",
				new String[] { "XyzOrder", "orderId" });
		transEditor.createTransformation("ABCOrder", new String[] { "ABCOrder", "header", "status" }, "XyzOrder",
				new String[] { "XyzOrder", "priority" });
		transEditor.createTransformation("ABCOrder", new String[] { "ABCOrder", "orderItems", "item[ ]", "id" },
				"XyzOrder", new String[] { "XyzOrder", "lineItems[ ]", "itemId" });
		transEditor.createTransformation("ABCOrder", new String[] { "ABCOrder", "orderItems", "item[ ]", "price" },
				"XyzOrder", new String[] { "XyzOrder", "lineItems[ ]", "cost" });
		transEditor.createTransformation("ABCOrder", new String[] { "ABCOrder", "orderItems", "item[ ]", "quantity" },
				"XyzOrder", new String[] { "XyzOrder", "lineItems[ ]", "amount" });
		transEditor.close();
	}

	/**
	 * Adds a Data Transformation component into the Camel route.
	 */
	protected void addDataTransformation() {
		CamelEditor editor = new CamelEditor();
		editor.addCamelComponent("Data Transformation", "SetHeader _setHeader1");
		new WaitUntil(new ShellIsAvailable("New Fuse Transformation"), TimePeriod.VERY_LONG);
		NewFuseTransformationWizard wizard = new NewFuseTransformationWizard();
		wizard.setTransformationID("xml2json");
		wizard.setSourceType(TransformationType.XML);
		wizard.setTargetType(TransformationType.JSON);
		wizard.next();
		wizard.setXMLTypeDefinition(TypeDefinition.Schema);
		wizard.setXMLSourceFile("abc-order.xsd - data-transformation-test/src/data");
		wizard.next();
		wizard.setJSONTypeDefinition(TypeDefinition.Schema);
		wizard.setJSONTargetFile("xyz-order.json - data-transformation-test/src/data");
		wizard.finish(TimePeriod.VERY_LONG);
		editor.activate();
		editor.addCamelComponent("Log", "ref:xml2json");
		editor.setProperty("Log", "Message *", "${body}");
		editor.close(true);
	}
}
