/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.projecttemplates.wizards.pages;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.jst.server.core.FacetUtil;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.runtime.RuntimeManager;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeLifecycleListener;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.ui.ServerUIUtil;
import org.fusesource.ide.camel.model.service.core.CamelServiceManagerUtil;
import org.fusesource.ide.camel.model.service.core.util.CamelCatalogUtils;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.foundation.ui.util.Widgets;
import org.fusesource.ide.projecttemplates.internal.Messages;
import org.fusesource.ide.projecttemplates.internal.ProjectTemplatesActivator;


/**
 * @author lhein
 */
public class FuseIntegrationProjectWizardRuntimeAndCamelPage extends WizardPage {

	static final String UNKNOWN_CAMEL_VERSION = "unknown"; //$NON-NLS-1$

	private ComboViewer runtimeComboViewer;
	private Map<String, IRuntime> serverRuntimes;
	private String lastSelectedRuntime;
	private Combo camelVersionCombo;
	private Button camelVersionValidationBtn;
	private StyledText camelInfoText;
	private Label warningIconLabel;
	
	public FuseIntegrationProjectWizardRuntimeAndCamelPage() {
		super(Messages.newProjectWizardRuntimePageName);
		setTitle(Messages.newProjectWizardRuntimePageTitle);
		setDescription(Messages.newProjectWizardRuntimePageDescription);
		setImageDescriptor(ProjectTemplatesActivator.imageDescriptorFromPlugin(ProjectTemplatesActivator.PLUGIN_ID, ProjectTemplatesActivator.IMAGE_CAMEL_PROJECT_ICON));
		setPageComplete(false);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new GridLayout(3, false));

		Group runtimeGrp = new Group(container, SWT.NONE);
		GridData runtimeGrpData = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
		runtimeGrp.setLayout(new GridLayout(3, false));
		runtimeGrp.setLayoutData(runtimeGrpData);
		runtimeGrp.setText(Messages.newProjectWizardRuntimePageRuntimeGroupLabel);
		
		Label runtimeLabel = new Label(runtimeGrp, SWT.NONE);
		GridData runtimeLabelData = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
		runtimeLabel.setLayoutData(runtimeLabelData);
		runtimeLabel.setText(Messages.newProjectWizardRuntimePageRuntimeLabel);

		runtimeComboViewer = new ComboViewer(runtimeGrp, SWT.NONE | SWT.READ_ONLY);
		runtimeComboViewer.setComparator(new ViewerComparator(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				if(Messages.newProjectWizardRuntimePageNoRuntimeSelectedLabel.equals(o1)){
					return -1;
				}
				return o1.compareTo(o2);
			}
		}));
		
		GridData runtimeComboData = new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1);
		runtimeComboViewer.getCombo().setLayoutData(runtimeComboData);
		runtimeComboViewer.getCombo().setToolTipText(Messages.newProjectWizardRuntimePageRuntimeDescription);
		runtimeComboViewer.setContentProvider(ArrayContentProvider.getInstance());
		runtimeComboViewer.getCombo().addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				lastSelectedRuntime = getSelectedRuntimeAsString();
				preselectCamelVersionForRuntime(determineRuntimeCamelVersion(getSelectedRuntime()));
				validate();
			}
		});

		try {
			configureRuntimeCombo();
		} catch (CoreException ex) {
			ProjectTemplatesActivator.pluginLog().logError(ex);
		}

		Button runtimeNewButton = new Button(runtimeGrp, SWT.NONE);
		GridData runtimeNewButtonData = new GridData(SWT.FILL, SWT.CENTER, false, false);
		runtimeNewButton.setLayoutData(runtimeNewButtonData);
		runtimeNewButton.setText(Messages.newProjectWizardRuntimePageRuntimeNewButtonLabel);
		runtimeNewButton.setToolTipText(Messages.newProjectWizardRuntimePageRuntimeNewButtonDescription);
		runtimeNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String[] oldRuntimes = runtimeComboViewer.getCombo().getItems();
				boolean created = ServerUIUtil.showNewRuntimeWizard(getShell(), null, null);
				if (created) {
					String[] newRuntimes = runtimeComboViewer.getCombo().getItems();
					String newRuntime = getNewRuntime(oldRuntimes, newRuntimes);
					if(newRuntime != null){
						runtimeComboViewer.setSelection(new StructuredSelection(newRuntime));
					}
				}
			}
		});
		
		new Label(runtimeGrp, SWT.None);
		
		Group camelGrp = new Group(container, SWT.NONE);
		GridData camelGrpData = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 20);
		camelGrp.setLayout(new GridLayout(3, false));
		camelGrp.setLayoutData(camelGrpData);
		camelGrp.setText(Messages.newProjectWizardRuntimePageCamelGroupLabel);
		
		Label camelVersionLabel = new Label(camelGrp, SWT.NONE);
		GridData camelLabelData = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
		camelVersionLabel.setLayoutData(camelLabelData);
		camelVersionLabel.setText(Messages.newProjectWizardRuntimePageCamelLabel);

		camelVersionCombo = new Combo(camelGrp, SWT.RIGHT | SWT.DROP_DOWN);
		GridData camelComboData = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		camelVersionCombo.setLayoutData(camelComboData);
		camelVersionCombo.setItems(CamelCatalogUtils.getOfficialSupportedCamelCatalogVersions().toArray(new String[CamelCatalogUtils.getOfficialSupportedCamelCatalogVersions().size()]));
		camelVersionCombo.setText(CamelCatalogUtils.getLatestCamelVersion());
		camelVersionCombo.setToolTipText(Messages.newProjectWizardRuntimePageCamelDescription);
		camelVersionCombo.addSelectionListener(new SelectionAdapter() {
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				validate();
			}
		});
		camelVersionCombo.addFocusListener(new FocusAdapter() {
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.FocusAdapter#focusLost(org.eclipse.swt.events.FocusEvent)
			 */
			@Override
			public void focusLost(FocusEvent e) {
				super.focusLost(e);
				validate();
			}
			
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.FocusAdapter#focusGained(org.eclipse.swt.events.FocusEvent)
			 */
			@Override
			public void focusGained(FocusEvent e) {
				super.focusGained(e);
				setPageComplete(false);
			}
		});
		
		camelVersionValidationBtn = new Button(camelGrp, SWT.PUSH);
		GridData camelButtonData = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		camelVersionValidationBtn.setLayoutData(camelButtonData);
		camelVersionValidationBtn.setText(Messages.newProjectWizardRuntimePageCamelVersionValidationLabel);
		camelVersionValidationBtn.setToolTipText(Messages.newProjectWizardRuntimePageCamelVersionValidationDescription);
		camelVersionValidationBtn.addSelectionListener(new SelectionAdapter() {
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					getWizard().getContainer().run(false, false, new IRunnableWithProgress() {
						@Override
						public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
							monitor.beginTask(Messages.newProjectWizardRuntimePageResolveDependencyStatus, IProgressMonitor.UNKNOWN);
							validateCamelVersion();
							monitor.done();
						}
					});
				} catch (Exception ex) {
					ProjectTemplatesActivator.pluginLog().logError(ex);
				}
			}
		});
				
		new Label(camelGrp, SWT.None);
	
		warningIconLabel = new Label(camelGrp, SWT.None);
		GridData camelLblData = new GridData(SWT.FILL, SWT.TOP, false, true, 1, 20);
		camelLblData.verticalIndent = 20;
		warningIconLabel.setImage(getSWTImage(SWT.ICON_WARNING));
		warningIconLabel.setLayoutData(camelLblData);
		warningIconLabel.setVisible(false);
		
		camelInfoText = new StyledText(camelGrp, SWT.WRAP | SWT.MULTI);
		GridData camelInfoData = new GridData(SWT.FILL, SWT.TOP, true, true, 2, 20);
		camelInfoData.verticalIndent = 0;
		camelInfoData.heightHint = 150;
		camelInfoText.setLayoutData(camelInfoData);
		camelInfoText.setEnabled(false);
		camelInfoText.setEditable(false);
		camelInfoText.setBackground(container.getBackground());

		new Label(camelGrp, SWT.None);
		
		setControl(container);
		
		IRuntimeLifecycleListener listener = new IRuntimeLifecycleListener() {
			
			@Override
			public void runtimeRemoved(IRuntime runtime) {
				runInUIThread();
			}
			
			@Override
			public void runtimeChanged(IRuntime runtime) {
				runInUIThread();
			}
			
			@Override
			public void runtimeAdded(IRuntime runtime) {
				runInUIThread();
			}
			
			private void runInUIThread() {
				Display.getDefault().asyncExec(new Runnable() {
					
					@Override
					public void run() {
						try {
							configureRuntimeCombo();
						} catch (CoreException ex) {
							ProjectTemplatesActivator.pluginLog().logError("Unable to handle runtime change event", ex); //$NON-NLS-1$
						}
					}
				});
			}
			
		};
		ServerCore.addRuntimeLifecycleListener(listener);
		validate();
	}
	
	private void configureRuntimeCombo() throws CoreException {
		if (Widgets.isDisposed(runtimeComboViewer)) {
			return;
		}
		
		String lastUsedRuntime = lastSelectedRuntime;
		List<String> runtimesList = new ArrayList<>();
		String selectedRuntime = null;
	
		serverRuntimes = getServerRuntimes(null);
		runtimeComboViewer.setInput(ArrayContentProvider.getInstance());
		runtimesList.addAll(serverRuntimes.keySet());
		runtimesList.add(Messages.newProjectWizardRuntimePageNoRuntimeSelectedLabel);
		runtimeComboViewer.setInput(runtimesList);
		runtimeComboViewer.setSelection(new StructuredSelection(Messages.newProjectWizardRuntimePageNoRuntimeSelectedLabel));

		for (Map.Entry<String, IRuntime> entry : serverRuntimes.entrySet()) {
			IRuntime runtime = entry.getValue();
			if (lastUsedRuntime != null && lastUsedRuntime.equals(runtime.getId())) {
				selectedRuntime = lastUsedRuntime;
			}
		}
		
		if (selectedRuntime != null) {
			runtimeComboViewer.setSelection(new StructuredSelection(selectedRuntime));
		}
	}
	
	private Map<String, IRuntime> getServerRuntimes(IProjectFacetVersion facetVersion) {
		Set<org.eclipse.wst.common.project.facet.core.runtime.IRuntime> runtimesSet;
		if (facetVersion == null) {
			runtimesSet = RuntimeManager.getRuntimes();
		} else {
			runtimesSet = RuntimeManager.getRuntimes(Collections.singleton(facetVersion));
		}
		
		Map<String, IRuntime> runtimesMap = new LinkedHashMap<>();
		for (org.eclipse.wst.common.project.facet.core.runtime.IRuntime r : runtimesSet) {
			IRuntime serverRuntime = FacetUtil.getRuntime(r);
			if (serverRuntime != null) {
				runtimesMap.put(r.getLocalizedName(), serverRuntime);
			}
		}
		return runtimesMap;
	}
	
	private String getNewRuntime(String[] oldRuntimes, String[] newRuntimes) {
		for (String newRuntime : newRuntimes) {
			boolean found = false;
			for (String oldRuntime : oldRuntimes) {
				if (newRuntime.equals(oldRuntime)) {
					found = true;
					break;
				}
			}
			if (!found){
				return newRuntime;
			}
		}
		return Messages.newProjectWizardRuntimePageNoRuntimeSelectedLabel;
	}

	/**
	 * /!\ Public for test purpose 
	 */
	public void validate() {
		// if runtime is selected other than NO RUNTIME
		if (getSelectedRuntime() != null) {
			// determine the camel version of that runtime
			String runtimeCamelVersion = determineRuntimeCamelVersion(getSelectedRuntime());

			if (UNKNOWN_CAMEL_VERSION.equals(runtimeCamelVersion)) {
				if (!Widgets.isDisposed(camelVersionCombo)){
					camelVersionCombo.setEnabled(true);
				}
				camelInfoText.setText(Messages.FuseIntegrationProjectWizardRuntimeAndCamelPage_WarningMessageWhenCamelVersionCannotBeDeterminedInRuntime);
			} else {
				// and compare if selected camel version fits that version
				if (!isCompatible(runtimeCamelVersion, getSelectedCamelVersion())) {
					// Display warning and suggest the correct version
					camelInfoText.setText(NLS.bind(Messages.newProjectWizardRuntimePageCamelVersionsDontMatchWarning, runtimeCamelVersion));
				} else {
					camelInfoText.setText(""); //$NON-NLS-1$
				}
				if (!Widgets.isDisposed(camelVersionCombo)){
					camelVersionCombo.setEnabled(false);
				}
			}
		} else {
			if (!Widgets.isDisposed(camelVersionCombo)){
				camelVersionCombo.setEnabled(true);
			}

			if (!Widgets.isDisposed(camelInfoText)) {
				camelInfoText.setText(""); //$NON-NLS-1$
			}			
		}
		
		if (!Widgets.isDisposed(warningIconLabel) && !Widgets.isDisposed(camelInfoText)) { 
			warningIconLabel.setVisible(!camelInfoText.getText().isEmpty());
		}
		
		if (!Widgets.isDisposed(runtimeComboViewer) && !Widgets.isDisposed(camelVersionCombo)) {
			setPageComplete(!Strings.isBlank(getSelectedRuntimeAsString()) && 
							!Strings.isBlank(camelVersionCombo.getText()) &&
							!warningIconLabel.isVisible());
		}
	}
	
	private void validateCamelVersion() {
		if (getSelectedCamelVersion() != null && !isCamelVersionValid(getSelectedCamelVersion())) {
			if (!Widgets.isDisposed(camelInfoText)) {
				camelInfoText.setText(NLS.bind(Messages.newProjectWizardRuntimePageCamelVersionInvalidWarning, getSelectedCamelVersion()));
				setPageComplete(false);
			}
		} else {
			if (!Widgets.isDisposed(camelInfoText)) {
				camelInfoText.setText(""); //$NON-NLS-1$
			}	
		}
		if (!Widgets.isDisposed(warningIconLabel) && !Widgets.isDisposed(camelInfoText)) { 
			warningIconLabel.setVisible(camelInfoText.getText().length()>0);
		}
	}
	
	private boolean isCamelVersionValid(String camelVersion) {
		return CamelServiceManagerUtil.getManagerService().isCamelVersionExisting(camelVersion);
	}
	
	public void preselectCamelVersionForRuntime(String runtimeCamelVersion) {
		if (Widgets.isDisposed(camelVersionCombo)){
			return;
		}
		
		if (UNKNOWN_CAMEL_VERSION.equals(runtimeCamelVersion)) {
			camelVersionCombo.setEnabled(true);
		}		
	}
	
	/**
	 * /!\ Public for test purpose 
	 */
	public String determineRuntimeCamelVersion(IRuntime runtime) {
		return new RuntimeCamelVersionFinder().getVersion(runtime);
	}
	
	/**
	 * checks if the two versions are identical for major and minor version
	 * 
	 * @param runtimeCamelVersion	the camel version in the runtime
	 * @param selectedCamelVersion	the camel version selected in the wizard
	 * @return	true if compatible
	 */
	private boolean isCompatible(String runtimeCamelVersion, String selectedCamelVersion) {
		String[] runtimeVersionParts = runtimeCamelVersion.split("\\."); //$NON-NLS-1$
		String[] camelVersionParts = selectedCamelVersion.split("\\."); //$NON-NLS-1$
		boolean rh_branded_rcv = runtimeCamelVersion.indexOf(".redhat-") != -1 || runtimeCamelVersion.indexOf(".fuse-") != -1;  //$NON-NLS-1$
		boolean rh_branded_scv = selectedCamelVersion.indexOf(".redhat-") != -1 || selectedCamelVersion.indexOf(".fuse-") != -1; //$NON-NLS-1$
		
		return runtimeVersionParts.length>1 && camelVersionParts.length>1 &&
			   runtimeVersionParts[0].equals(camelVersionParts[0]) &&
			   runtimeVersionParts[1].equals(camelVersionParts[1]) && 
			   rh_branded_rcv == rh_branded_scv;
	}
	
	/**
	 * returns the selected runtime
	 * 
	 * @return
	 */
	public IRuntime getSelectedRuntime() {
		if (!Widgets.isDisposed(runtimeComboViewer)) {
			String runtimeId = getSelectedRuntimeAsString();
			if (!Messages.newProjectWizardRuntimePageNoRuntimeSelectedLabel.equalsIgnoreCase(runtimeId)) {
				return serverRuntimes.get(runtimeId);	
			}
		}
		return null;
	}
	
	private String getSelectedRuntimeAsString(){
		IStructuredSelection structuredSelection = runtimeComboViewer.getStructuredSelection();
		if(!structuredSelection.isEmpty()){
			return (String) structuredSelection.getFirstElement();
		}
		return null;
	}
	
	/**
	 * returns the selected camel version
	 * 
	 * @return
	 */
	public String getSelectedCamelVersion() {
		if (!Widgets.isDisposed(camelVersionCombo)) {
			return camelVersionCombo.getText();
		}
		return null;
	}
	
	/**
	 * Get an <code>Image</code> from the provide SWT image constant.
	 *
	 * @param imageID
	 *            the SWT image constant
	 * @return image the image
	 */
	private Image getSWTImage(final int imageID) {
		Shell shell = getShell();
		final Display display;
		if (!Widgets.isDisposed(shell)) {
			shell = shell.getParent().getShell();
		}
		if (Widgets.isDisposed(shell)) {
			display = Display.getCurrent();
			// The dialog should be always instantiated in UI thread.
			// However it was possible to instantiate it in other threads
			// (the code worked in most cases) so the assertion covers
			// only the failing scenario. See bug 107082 for details.
			Assert.isNotNull(display,
					"The dialog should be created in UI thread"); //$NON-NLS-1$
		} else {
			display = shell.getDisplay();
		}

		final Image[] image = new Image[1];
		display.syncExec(new Runnable() {
			@Override
			public void run() {
				image[0] = display.getSystemImage(imageID);
			}
		});

		return image[0];
	}
	
	/**
	 * /!\ Public for test purpose 
	 */
	public void setCamelInfoText(StyledText camelInfoText) {
		this.camelInfoText = camelInfoText;
	}
}
