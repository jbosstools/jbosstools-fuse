/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved.
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.fabric.actions.jclouds;


import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.fusesource.ide.commons.ui.ICanValidate;
import org.fusesource.ide.fabric.actions.Messages;
import org.fusesource.ide.fabric.navigator.Fabrics;


/**
 * The form for creating a new fabric in the jcloud
 */
public class CloudFabricDetailsForm extends CloudContainerDetailsForm {
	private Text nameField;
	private Text proxyUriField;
	private String fabricName = "Cloud Fabric";
	private String proxyUri = Fabrics.DEFAULT_MAVEN_PROXY_URI;

	public CloudFabricDetailsForm(ICanValidate validator, String defaultAgentName) {
		super(validator, null, null, defaultAgentName, null);
	}

	public String getFabricName() {
		return fabricName;
	}

	public void setFabricName(String fabricName) {
		this.fabricName = fabricName;
	}

	public String getProxyUri() {
		return proxyUri;
	}

	public void setProxyUri(String proxyUri) {
		this.proxyUri = proxyUri;
	}

	@Override
	public void setFocus() {
		nameField.setFocus();
	}


	@Override
	protected boolean isSelectProfile() {
		return false;
	}


	@Override
	protected boolean chooseVersion() {
		return false;
	}

	@Override
	protected String getFormHeader() {
		return Messages.jclouds_fabricCloudFormHeader;
	}

	@Override
	protected String getSectionHeader() {
		//return Messages.jclouds_fabricCloudSectionHeader;
		return super.getSectionHeader();
	}

	@Override
	protected void createTextFields(Composite inner) {
		nameField = createBeanPropertyTextField(inner, this, "fabricName", Messages.jclouds_fabricNameLabel, Messages.jclouds_fabricNameTooltip);

		super.createTextFields(inner);

		proxyUriField = createBeanPropertyTextField(inner, this, "proxyUri", Messages.jclouds_fabricProxyUriLabel, Messages.jclouds_fabricProxyUriTooltip);
	}



}