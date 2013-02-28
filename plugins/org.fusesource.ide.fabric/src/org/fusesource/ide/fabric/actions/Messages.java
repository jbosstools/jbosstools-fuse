/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved.
 *
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.fabric.actions;

import org.eclipse.osgi.util.NLS;

/**
 * The messages.
 */
public final class Messages extends NLS {

	/** The bundle name. */
	private static final String BUNDLE_NAME = "org.fusesource.ide.fabric.actions.messages";//$NON-NLS-1$

	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	/**
	 * The constructor.
	 */
	private Messages() {
		// do not instantiate
	}

	public static String createChildAgentTitle;

	public static String agentFormHeader;
	public static String agentFieldsHeader;
	public static String selectedProfiles;

	public static String selectAllLabel;
	public static String deselectAllLabel;

	public static String createChildAgentMenuLabel;
	public static String createChildAgentToolTip;

	public static String createSshAgentMenuLabel;
	public static String createSshAgentToolTip;

	public static String createJCloudsAgentMenuLabel;
	public static String createJCloudsAgentToolTip;

	public static String openTerminalLabel;
	public static String openTerminalToolTip;

	public static String StopAgentAction;
	public static String StopAgentActionToolTip;
	public static String StartAgentAction;
	public static String StartAgentActionToolTip;

	public static String createSshAgentTitle;
	public static String createJCloudsAgentTitle;

	public static String agentVersionLabel;
	public static String agentVersionTooltip;

	public static String agentNameLabel;
	public static String agentNameTooltip;

	public static String agentPathLabel;
	public static String agentPathTooltip;

	public static String agentHostLabel;
	public static String agentHostTooltip;

	public static String agentPortLabel;
	public static String agentPortTooltip;

	public static String agentUserLabel;
	public static String agentUserTooltip;

	public static String agentPasswordLabel;
	public static String agentPasswordTooltip;

	public static String noProfileSelected;

	public static String agentSshRetriesLabel;
	public static String agentSshRetriesTooltip;

	public static String agentRetryDelayLabel;
	public static String agentRetryDelayTooltip;

	public static String jclouds_providerNameLabel;
	public static String jclouds_providerNameTooltip;

    public static String jclouds_apiNameLabel;
    public static String jclouds_apiNameTooltip;

    public static String jclouds_endpointLabel;
    public static String jclouds_endpointTooltip;

	public static String jclouds_imageIdLabel;
	public static String jclouds_imageIdTooltip;

	public static String jclouds_groupLabel;
	public static String jclouds_groupTooltip;

	public static String jclouds_hardwareIdLabel;
	public static String jclouds_hardwareIdTooltip;

	public static String jclouds_locationIdLabel;
	public static String jclouds_locationIdTooltip;

	public static String jclouds_userLabel;
	public static String jclouds_userTooltip;

	public static String jclouds_osFamilyLabel;
	public static String jclouds_osFamilyTooltip;

	public static String jclouds_osVersionLabel;
	public static String jclouds_osVersionTooltip;

	public static String jclouds_identityLabel;
	public static String jclouds_identityTooltip;

	public static String jclouds_credentialLabel;
	public static String jclouds_credentialTooltip;

	public static String jclouds_ownerLabel;
	public static String jclouds_ownerTooltip;

	public static String jclouds_chooseCloudTitle;
	public static String jclouds_chooseCloudDescription;

	public static String jclouds_chooseAgentDetailsTitle;
	public static String jclouds_chooseAgentDetailsDescription;

	public static String jclouds_selectCloud;
	public static String jclouds_cloudDetails;

	public static String jclouds_nameLabel;
	public static String jclouds_nameTooltip;

	public static String jcloud_addCloudButton;
	public static String jcloud_addCloudButtonTooktip;

	public static String jcloud_editCloudButton;
	public static String jcloud_editCloudButtonTooktip;

	public static String jcloud_deleteCloudButton;
	public static String jcloud_deleteCloudButtonTooktip;


	public static String jcloud_addNodeButton;
	public static String jcloud_addNodeButtonTooktip;

	public static String jcloud_rebootNodeButton;
	public static String jcloud_rebootNodeButtonTooktip;
	public static String jcloud_destroyNodeButton;
	public static String jcloud_destroyNodeButtonTooktip;
	public static String jcloud_suspendNodeButton;
	public static String jcloud_suspendNodeButtonTooktip;
	public static String jcloud_resumeNodeButton;
	public static String jcloud_resumeNodeButtonTooktip;
	public static String jcloud_openTerminalLabel;
	public static String jcloud_openTerminalTooltip;

	public static String profileDeleteLabel;
	public static String profileDeleteTooltip;

	public static String profileAddLabel;
	public static String profileAddTooltip;

	public static String profileAddDialogTitle;
	public static String profileAddDialogMessage;

	public static String deleteProfileDialogTitle;
	public static String deleteProfileMessage;

	public static String profileParentsLabel;

	public static String createJCloudsFabricMenuLabel;
	public static String createJCloudsFabricTitle;
	public static String createJCloudsFabricToolTip;


	public static String jclouds_fabricCloudFormHeader;
	public static String jclouds_fabricCloudSectionHeader;

	public static String jclouds_fabricDetailsTitle;
	public static String jclouds_fabricDetailsDescription;
	public static String jclouds_fabricNameLabel;
	public static String jclouds_fabricNameTooltip;
	public static String jclouds_fabricProxyUriLabel;
	public static String jclouds_fabricProxyUriTooltip;

	public static String fabricAddLabel;
	public static String fabricAddTooltip;
	public static String fabricAddDialogTitle;
	public static String fabricAddDialogMessage;

	public static String fabricDetailsSection;
	public static String fabricNameLabel;
	public static String fabricNameTooltip;
	public static String fabricUrlsLabel;
	public static String fabricUrlsTooltip;
	public static String fabricDetailsDialog;

	public static String fabricDeleteButton;
	public static String fabricDeleteButtonTooktip;
	public static String fabricEditButton;
	public static String fabricEditButtonTooktip;
	public static String fabricAddButton;
	public static String fabricAddButtonTooktip;

	public static String shellViewLabel;

	public static String fabricUserNameLabel;
	public static String fabricUserNameTooltip;
	public static String fabricPasswordLabel;
	public static String fabricPasswordTooltip;
	public static String zkPasswordLabel;
	public static String zkPasswordTooltip;

	public static String createVersionMenuLabel;
	public static String createVersionDescription;

	public static String createVersionDialogTitle;
	public static String createVersionDialogMessage;

	public static String fabricConnectButton;
	public static String fabricConnectButtonTooltip;
	public static String fabricDisconnectButton;
	public static String fabricDisconnectButtonTooltip;

	public static String StopBundleAction;
	public static String StopBundleActionToolTip;
	public static String StartBundleAction;
	public static String StartBundleActionToolTip;
	public static String UpdateBundleAction;
	public static String UpdateBundleActionToolTip;
	public static String UninstallBundleAction;
	public static String UninstallBundleActionToolTip;

	public static String createCloudContainerDetailsFormHeaderLabel;
	public static String createCloudContainerDetailsFormSectionHeaderLabel;

	public static String DestroyContainerAction;
	public static String DestroyContainerActionToolTip;

	public static String defaultsLabel;
	public static String defaultsTooltip;

	public static String clearLabel;
	public static String clearTooltip;
}