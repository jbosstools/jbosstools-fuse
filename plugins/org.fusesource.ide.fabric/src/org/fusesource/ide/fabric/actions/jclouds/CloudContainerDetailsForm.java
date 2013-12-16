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

package org.fusesource.ide.fabric.actions.jclouds;

import java.util.List;

import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import io.fabric8.api.Profile;
import io.fabric8.service.jclouds.CreateJCloudsContainerOptions;
import io.fabric8.service.jclouds.JCloudsInstanceType;
import org.fusesource.ide.commons.Viewers;
import org.fusesource.ide.commons.ui.ICanValidate;
import org.fusesource.ide.fabric.FabricPlugin;
import org.fusesource.ide.fabric.actions.CreateContainerFormSupport;
import org.fusesource.ide.fabric.actions.Messages;
import org.fusesource.ide.fabric.navigator.ContainerNode;
import org.fusesource.ide.fabric.navigator.Fabric;
import org.fusesource.ide.fabric.navigator.ProfileNode;
import org.fusesource.ide.fabric.navigator.VersionNode;
import org.jclouds.compute.domain.Image;


/**
 * The form for creating agents via jclouds
 */
public class CloudContainerDetailsForm extends CreateContainerFormSupport {

	private static final String PROP_OS_VERSION = "osVersion";
	private static final String PROP_OS_FAMILY = "family";
	private static final String PROP_IMAGE = "image";
	private static final String PROP_HARDWARE = "hardware";
	private static final String PROP_LOCATION = "location";
	private static final String PROP_USER = "user";
	private static final String PROP_PASSWORD = "password";
	private static final String PROP_ZKPASSWORD = "zookeeperPassword";
	private static final String PROP_GROUP = "group";

	private final ContainerNode selectedAgent;
	private final ProfileNode selectedProfile;
	private final Fabric fabric;
	private CreateJCloudsContainerArgumentsBean args = new CreateJCloudsContainerArgumentsBean();
	private ComboViewer imageField;
	private ComboViewer hardwareField;
	private ComboViewer locationField;
	private ComboViewer osFamilyField;
	private ComboViewer osVersionField;
	private CloudDetails selectedCloud;
	private Text userField;
	private Text passwordField;
	private Text zkPasswordField;
	private Text groupField;
	private CloudDetailsCachedData cloudCacheData = new CloudDetailsCachedData(null);
	private ImageViewerFilter imageFilter = new ImageViewerFilter(null);
	private boolean isAWSEC2;
	
	private class ImageViewerFilter extends ViewerFilter {
		
		private String selectedLocation;
		
		/**
		 * 
		 */
		public ImageViewerFilter(String filter) {
			this.selectedLocation = filter;
		}
		
		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			if (selectedLocation == null || selectedLocation.trim().length()<1) {
				return true;
			}
			return ((Image)element).getId().startsWith(selectedLocation);
		}
	};

	private ISelectionChangedListener selectionListener = new ISelectionChangedListener() {
		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			validate();
		}
	};

	public CloudContainerDetailsForm(ICanValidate validator, VersionNode versionNode, ContainerNode selectedAgent, String defaultAgentName, ProfileNode selectedProfile) {
		super(validator, versionNode, defaultAgentName);
		this.selectedAgent = selectedAgent;
		this.selectedProfile = selectedProfile;
		this.fabric = (versionNode != null) ? versionNode.getFabric() : (selectedProfile != null) ? selectedProfile.getFabric() : (selectedAgent != null) ? selectedAgent.getFabric() : null;
		if (fabric != null) {
			args.setUser(fabric.getUserName());
		} else {
			args.setUser("admin");
		}
		args.setGroup("fabric");
	}

	private void presetCloudDetails() {
		if (selectedCloud != null) args.setProperties(selectedCloud);
		args.setInstanceType(JCloudsInstanceType.Smallest);
	}

	@Override
	protected boolean isMandatory(Object bean, String propertyName) {
		if (propertyName.equals(PROP_IMAGE) ||
				propertyName.equals(PROP_OS_FAMILY) ||
				propertyName.equals(PROP_OS_VERSION)) {
			// we will handle those in the isValid method
			return false;
		}
		return true;
	}

	public CreateJCloudsContainerOptions.Builder getCreateCloudArguments() {
		CreateJCloudsContainerOptions.Builder answer = args.getBuilder();
		return answer;
	}

	@Override
	public void setFocus() {
		groupField.setFocus();
	}

	public CloudDetails getSelectedCloud() {
		return selectedCloud;
	}

	public void setSelectedCloud(CloudDetails selectedCloud) {
		if (cloudCacheData != null) {
			cloudCacheData.cancelLoadingJobs();
		}

		this.selectedCloud = selectedCloud;

		cloudCacheData = CloudDetailsCachedData.getInstance(selectedCloud);
		setCloudDataInputs();
		cloudCacheData.startLoadingDataJobs();
		presetCloudDetails();
		restoreAll();
	}

	protected void setInput(final ComboViewer viewer, final WritableList list) {
		list.addChangeListener(new IChangeListener() {

			@Override
			public void handleChange(ChangeEvent event) {
//				FabricPlugin.getLogger().debug("Updating viewer " + viewer + " with list of " + list.size() + " of type: " + list.getElementType());
				restoreSettings(viewer);
				Viewers.refresh(viewer);				
			}
		});
		viewer.setInput(list);
	}

	public WritableList getImageList() {
		return cloudCacheData.getImageList();
	}

	public List getOSFamilyList() {
		return cloudCacheData.getOsFamilyList();
	}

	public List getOSVersionList() {
		return cloudCacheData.getOsVersionList();
	}

	public WritableList getHardwareList() {
		return cloudCacheData.getHardwareList();
	}

	public WritableList getLocationList() {
		return cloudCacheData.getLocationList();
	}

	@Override
	protected void loadPreference() {
		if (selectedProfile != null) {
			Profile profile = selectedProfile.getProfile();
			if (profile != null) {
				setCheckedProfiles(profile);
			}
		}
		super.loadPreference();
	}

	@Override
	protected void createTextFields(Composite inner) {
		super.createTextFields(inner);

		groupField = createBeanPropertyTextField(inner, args, PROP_GROUP, Messages.jclouds_groupLabel, Messages.jclouds_groupTooltip);
		userField = createBeanPropertyTextField(inner, args, PROP_USER, Messages.jclouds_userLabel, Messages.jclouds_userTooltip);
		passwordField = createBeanPropertyPasswordField(inner, args, PROP_PASSWORD, Messages.jclouds_passwordLabel, Messages.jclouds_passwordTooltip);
		zkPasswordField = createBeanPropertyPasswordField(inner, args, PROP_ZKPASSWORD, Messages.jclouds_zkPasswordLabel, Messages.jclouds_zkPasswordTooltip);
		
		// TODO how to bind the ID value from the selection lists?
		locationField = createBeanPropertyCombo(inner, args, PROP_LOCATION, Messages.jclouds_locationIdLabel, Messages.jclouds_locationIdTooltip, getLocationList());
		locationField.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (!isAWSEC2) return;
				imageField.setFilters(new ViewerFilter[0]);
				imageFilter = new ImageViewerFilter(locationField.getCombo().getText());
				imageField.setFilters(new ViewerFilter[] {imageFilter});
			}
		});
		hardwareField = createBeanPropertyCombo(inner, args, PROP_HARDWARE, Messages.jclouds_hardwareIdLabel, Messages.jclouds_hardwareIdTooltip, getHardwareList());

		// os fields
		osFamilyField = createBeanPropertyCombo(inner, args, PROP_OS_FAMILY, Messages.jclouds_osFamilyLabel, Messages.jclouds_osFamilyTooltip, getOSFamilyList());
		osFamilyField.addSelectionChangedListener(selectionListener);
		osVersionField = createBeanPropertyCombo(inner, args, PROP_OS_VERSION, Messages.jclouds_osVersionLabel, Messages.jclouds_osVersionTooltip, getOSVersionList());
		osVersionField.addSelectionChangedListener(selectionListener);

		imageField = createBeanPropertyCombo(inner, args, PROP_IMAGE, Messages.jclouds_imageIdLabel, Messages.jclouds_imageIdTooltip, getImageList());
		imageField.addSelectionChangedListener(selectionListener);
		
		Composite btn_bar = new Composite(inner, SWT.NULL);
		btn_bar.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 2, 1));
		btn_bar.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Button btn_Clear = new Button(btn_bar, SWT.PUSH);
		btn_Clear.setText(Messages.clearLabel);
		btn_Clear.setToolTipText(Messages.clearTooltip);
		btn_Clear.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				reset();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		
		Button btn_Reset = new Button(btn_bar, SWT.PUSH);
		btn_Reset.setText(Messages.defaultsLabel);
		btn_Reset.setToolTipText(Messages.defaultsTooltip);
		btn_Reset.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				reset();
				restoreAll();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		
		setCloudDataInputs();
	}

	protected void setCloudDataInputs() {
		setInput(imageField, getImageList());
		setInput(hardwareField, getHardwareList());
		setInput(locationField, getLocationList());
		osFamilyField.setInput(getOSFamilyList());
		osVersionField.setInput(getOSVersionList());
	}
	
	private void reset() {
		imageField.setSelection(null);
		hardwareField.setSelection(null);
		locationField.setSelection(null);
		osFamilyField.setSelection(null);
		osVersionField.setSelection(null);
		if (fabric != null) {
			args.setUser(fabric.getUserName());
		} else {
			args.setUser("admin");
		}
		groupField.setText("");
		userField.setText("");
		passwordField.setText("");
		zkPasswordField.setText("");
	}

	private void restoreAll() {
		restoreSettings();
		restoreSettings(hardwareField);
		restoreSettings(locationField);
		restoreSettings(imageField);
	}
	
	protected ComboViewer createBeanPropertyCombo(Composite parent, Object bean, String propertyName, String labelText, String tooltip, List<?> input) {
		ComboViewer answer = createBeanPropertyCombo(parent, bean, propertyName, labelText, tooltip, SWT.READ_ONLY | SWT.BORDER);
		answer.setInput(input);
		answer.setLabelProvider(JCloudsLabelProvider.getInstance());
		return answer;
	}

	@Override
	public void okPressed() {
	}
	
	

	/* (non-Javadoc)
	 * @see org.fusesource.ide.fabric.actions.ProfileTreeSelectionFormSupport#isValid()
	 */
	@Override
	public boolean isValid() {
		boolean valid = super.isValid();

		if (!valid) return valid;

		String imgTxt = imageField.getCombo().getText();
		String osFamTxt = osFamilyField.getCombo().getText();

		if (imgTxt.trim().length() > 0 ||
				(osFamTxt.trim().length() > 0) ) {
			// image or osFamily have to be selected
			return valid;
		} else {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.fabric.actions.CreateContainerFormSupport#getFormHeader()
	 */
	@Override
	protected String getFormHeader() {
		return Messages.createCloudContainerDetailsFormHeaderLabel;
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.fabric.actions.CreateContainerFormSupport#getSectionHeader()
	 */
	@Override
	protected String getSectionHeader() {
		return Messages.createCloudContainerDetailsFormSectionHeaderLabel;
	}
	
	public void saveSettings() {
		if (!userField.isDisposed()) cloudCacheData.getDetails().addSetting("user", userField.getText());
		if (!passwordField.isDisposed()) cloudCacheData.getDetails().addSetting("password", passwordField.getText());
		if (!zkPasswordField.isDisposed()) cloudCacheData.getDetails().addSetting("zookeeperPassword", zkPasswordField.getText());
		if (!groupField.isDisposed()) cloudCacheData.getDetails().addSetting("group", groupField.getText());
		if (!imageField.getCombo().isDisposed()) cloudCacheData.getDetails().addSetting("image", imageField.getCombo().getText());
		if (!hardwareField.getCombo().isDisposed()) cloudCacheData.getDetails().addSetting("hardware", hardwareField.getCombo().getText());
		if (!locationField.getCombo().isDisposed()) cloudCacheData.getDetails().addSetting("location", locationField.getCombo().getText());
		if (!osFamilyField.getCombo().isDisposed()) cloudCacheData.getDetails().addSetting("osfamily", osFamilyField.getCombo().getText());
		if (!osVersionField.getCombo().isDisposed()) cloudCacheData.getDetails().addSetting("osversion", osVersionField.getCombo().getText());
		cloudCacheData.getDetails().flush();
	}
	
	public void restoreSettings(final ComboViewer viewer) {
		if (cloudCacheData == null || cloudCacheData.getDetails() == null) return;
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				if (viewer.getCombo().isDisposed()) return;
				if (viewer.equals(hardwareField)) {
					preSelectItem(hardwareField, cloudCacheData.getDetails().getSetting("hardware", hardwareField.getCombo().getText()));
				} else if (viewer.equals(locationField)) {
					preSelectItem(locationField, cloudCacheData.getDetails().getSetting("location", locationField.getCombo().getText()));
				} else if (viewer.equals(hardwareField)) {
					preSelectItem(imageField, cloudCacheData.getDetails().getSetting("image", imageField.getCombo().getText()));
				}
				// revalidate
				validate();
			}
		});
	}
	
	public void restoreSettings() {
		if (cloudCacheData.getDetails() == null) return;
		
		// check if we work with Amazon EC2 provider...if not we disable the image filtering by location
		this.isAWSEC2 = cloudCacheData.getDetails().getProviderId().equals("aws-ec2");
		
		Display.getDefault().asyncExec(new Runnable() {
			/* (non-Javadoc)
			 * @see java.lang.Runnable#run()
			 */
			@Override
			public void run() {
				if (!userField.isDisposed())  userField.setText(cloudCacheData.getDetails().getSetting("user", userField.getText()));
				if (!passwordField.isDisposed())  passwordField.setText(cloudCacheData.getDetails().getSetting("password", passwordField.getText()));
				if (!zkPasswordField.isDisposed())  zkPasswordField.setText(cloudCacheData.getDetails().getSetting("zookeeperPassword", zkPasswordField.getText()));
				if (!groupField.isDisposed()) groupField.setText(cloudCacheData.getDetails().getSetting("group", groupField.getText()));
				if (!osFamilyField.getCombo().isDisposed()) preSelectItem(osFamilyField, cloudCacheData.getDetails().getSetting("osfamily", osFamilyField.getCombo().getText()));
				if (!osVersionField.getCombo().isDisposed()) preSelectItem(osVersionField, cloudCacheData.getDetails().getSetting("osversion", osVersionField.getCombo().getText()));
			}
		});
	}
	
	private void preSelectItem(ComboViewer viewer, String itemToSelect) {
		for (Object o : (List)viewer.getInput()) {
			String oName = JCloudsLabelProvider.getInstance().getText(o);
			if (oName.equals(itemToSelect)) {
				viewer.setSelection(new StructuredSelection(o), true);
			}
		}
	}
}