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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
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
	private static final Pattern MAVEN_VERSION_PATTERN = Pattern.compile("^(\\d+){1}(\\.\\d+){1}(\\.\\d+){1}?((\\.|\\-).*)?$");

	private ComboViewer runtimeComboViewer;
	private Map<String, IRuntime> serverRuntimes;
	private String lastSelectedRuntime;
	private Combo camelVersionCombo;
	private boolean activeCamelVersionValidation;

	public FuseIntegrationProjectWizardRuntimeAndCamelPage() {
		super(Messages.newProjectWizardRuntimePageName);
		setTitle(Messages.newProjectWizardRuntimePageTitle);
		setDescription(Messages.newProjectWizardRuntimePageDescription);
		setImageDescriptor(ProjectTemplatesActivator.imageDescriptorFromPlugin(ProjectTemplatesActivator.PLUGIN_ID, ProjectTemplatesActivator.IMAGE_CAMEL_PROJECT_ICON));
		setPageComplete(false);
	}

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
		runtimeComboViewer.setComparator(new ViewerComparator((o1, o2) -> {
			if (Messages.newProjectWizardRuntimePageNoRuntimeSelectedLabel.equals(o1)){
				return -1;
			}
			return o1.compareTo(o2);
		}));

		GridData runtimeComboData = new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1);
		runtimeComboViewer.getCombo().setLayoutData(runtimeComboData);
		runtimeComboViewer.getCombo().setToolTipText(Messages.newProjectWizardRuntimePageRuntimeDescription);
		runtimeComboViewer.setContentProvider(ArrayContentProvider.getInstance());
		runtimeComboViewer.getCombo().addModifyListener(e -> {
			lastSelectedRuntime = getSelectedRuntimeAsString();
			preselectCamelVersionForRuntime(determineRuntimeCamelVersion(getSelectedRuntime()));
			validate();
		});

		configureRuntimeCombo();

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
		GridData camelGrpData = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
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
		camelVersionCombo.setItems(CamelCatalogUtils.getAllCamelCatalogVersions().stream().sorted( (String o1, String o2) -> o2.compareToIgnoreCase(o1)).toArray(String[]::new));
		camelVersionCombo.setText(CamelCatalogUtils.getLatestCamelVersion());
		camelVersionCombo.setToolTipText(Messages.newProjectWizardRuntimePageCamelDescription);
		camelVersionCombo.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				validate();
			}
		});
		camelVersionCombo.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				setMessage(null);
			}
		});

		camelVersionCombo.addModifyListener(e -> {
			validate();
			((FuseIntegrationProjectWizardTemplatePage)getWizard().getPage(Messages.newProjectWizardTemplatePageName)).refresh(camelVersionCombo.getText());
		});
		
		Button camelVersionValidationBtn = new Button(camelGrp, SWT.PUSH);
		GridData camelButtonData = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		camelVersionValidationBtn.setLayoutData(camelButtonData);
		camelVersionValidationBtn.setText(Messages.newProjectWizardRuntimePageCamelVersionValidationLabel);
		camelVersionValidationBtn.setToolTipText(Messages.newProjectWizardRuntimePageCamelVersionValidationDescription);
		camelVersionValidationBtn.addSelectionListener(new VersionValidationHandler());
				
		new Label(camelGrp, SWT.None);
				
		setControl(container);

		ServerCore.addRuntimeLifecycleListener(new FuseRuntimeLifecycleListener());
		validate();
	}	

	private void configureRuntimeCombo() {
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
	
	public void preselectCamelVersionForRuntime(String runtimeCamelVersion) {
		if (Widgets.isDisposed(camelVersionCombo)){
			return;
		}

		if (UNKNOWN_CAMEL_VERSION.equals(runtimeCamelVersion)) {
			camelVersionCombo.setEnabled(true);
		} else {
			camelVersionCombo.setText(runtimeCamelVersion);
		}		
	}

	/**
	 * /!\ Public for test purpose
	 * @param runtime
	 * @return the runtime camel version applicable to the supplied runtime
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
		boolean productizedRuntimeCamelVersion = runtimeCamelVersion.indexOf(".redhat-") != -1 || runtimeCamelVersion.indexOf(".fuse-") != -1;  //$NON-NLS-1$ //$NON-NLS-2$
		boolean productizedSelectedCamelVersion = selectedCamelVersion.indexOf(".redhat-") != -1 || selectedCamelVersion.indexOf(".fuse-") != -1; //$NON-NLS-1$ //$NON-NLS-2$

		return runtimeVersionParts.length>1 && camelVersionParts.length>1 &&
			   runtimeVersionParts[0].equals(camelVersionParts[0]) &&
			   runtimeVersionParts[1].equals(camelVersionParts[1]) &&
			   productizedRuntimeCamelVersion == productizedSelectedCamelVersion;
	}

	/**
	 * @return the selected runtime
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

	public String getSelectedRuntimeAsString(){
		IStructuredSelection structuredSelection = runtimeComboViewer.getStructuredSelection();
		if(!structuredSelection.isEmpty()){
			return (String) structuredSelection.getFirstElement();
		}
		return null;
	}

	/**
	 * @return the selected camel version
	 */
	public String getSelectedCamelVersion() {
		if (!Widgets.isDisposed(camelVersionCombo)) {
			return camelVersionCombo.getText();
		}
		return null;
	}

	/**
	 * /!\ Public for test purpose 
	 */
	public void setCamelVersionCombo(Combo camelVersionCombo) {
		this.camelVersionCombo = camelVersionCombo;
	}
	
	/**
	 * /!\ Public for test purpose
	 */
	public void validate() {
		setErrorMessage(null);
		
		// if runtime is selected other than NO RUNTIME
		if (getSelectedRuntime() != null) {
			validateForRuntimeCamelVersion(determineRuntimeCamelVersion(getSelectedRuntime()));
		} else {
			if (!Widgets.isDisposed(camelVersionCombo)){
				camelVersionCombo.setEnabled(true);	
			}			
			String selectedCamelVersion = getSelectedCamelVersion();
			if (!isValidCamelVersionSyntax(selectedCamelVersion)) {
				setErrorMessage(NLS.bind(Messages.newProjectWizardRuntimePageCamelVersionInvalidSyntaxWarning, selectedCamelVersion)); //$NON-NLS-1$
			} 		
		}

		if (Strings.isBlank(getSelectedRuntimeAsString())) {
			setErrorMessage(Messages.newProjectWizardRuntimePageNoRuntimeSelectedLabel);			
		}
		
		if (Strings.isBlank(getSelectedCamelVersion())) {
			setErrorMessage(Messages.newProjectWizardRuntimePageNoCamelVersionSelectedLabel);
		}
		setPageComplete(getErrorMessage() == null);
	}

	private void validateForRuntimeCamelVersion(String runtimeCamelVersion) {
		if (UNKNOWN_CAMEL_VERSION.equals(runtimeCamelVersion)) {
			if (!Widgets.isDisposed(camelVersionCombo)){
				camelVersionCombo.setEnabled(true);
			}
			setErrorMessage(Messages.fuseIntegrationProjectWizardRuntimeAndCamelPageWarningMessageWhenCamelVersionCannotBeDeterminedInRuntime);
		} else {
			// and compare if selected camel version fits that version
			if (!isCompatible(runtimeCamelVersion, getSelectedCamelVersion())) {
				// Display warning and suggest the correct version
				setErrorMessage(NLS.bind(Messages.newProjectWizardRuntimePageCamelVersionsDontMatchWarning, runtimeCamelVersion));
			}
			if (!Widgets.isDisposed(camelVersionCombo)){
				camelVersionCombo.setEnabled(false);
			}
		}
	}
	
	public boolean isValidCamelVersionSyntax(String camelVersion) {
		if (camelVersion != null) {
			Matcher m = MAVEN_VERSION_PATTERN.matcher(camelVersion);
			return m.matches();
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#canFlipToNextPage()
	 */
	@Override
	public boolean canFlipToNextPage() {
		return super.canFlipToNextPage() && !activeCamelVersionValidation && isPageComplete();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
	 */
	@Override
	public boolean isPageComplete() {
		return super.isPageComplete() && getErrorMessage() == null;
	}
	
	public synchronized boolean isCamelVersionValid(String camelVersion) {
		boolean valid = false;
		if (camelVersion != null) {
			try {
				activeCamelVersionValidation = true;
				valid = CamelServiceManagerUtil.getManagerService().isCamelVersionExisting(camelVersion);
			} finally {
				activeCamelVersionValidation = false;
			}
		}
		return valid;
	}
	
	class FuseRuntimeLifecycleListener implements IRuntimeLifecycleListener {
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
			Display.getDefault().asyncExec(FuseIntegrationProjectWizardRuntimeAndCamelPage.this::configureRuntimeCombo);
		}
	}
	
	class CamelVersionChecker implements IRunnableWithProgress {
		
		private String camelVersionToValidate;
		private Thread thread;
		private IProgressMonitor monitor;
		private boolean valid;
		private boolean done;
		
		public CamelVersionChecker(String camelVersionToValidate) {
			this.camelVersionToValidate = camelVersionToValidate;
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
		 */
		@Override
		public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
			this.monitor = monitor;
			monitor.beginTask(Messages.newProjectWizardRuntimePageResolveDependencyStatus, IProgressMonitor.UNKNOWN);
			this.thread = createThread();
			this.thread.start();
			while (this.thread.isAlive() && !this.thread.isInterrupted() && !monitor.isCanceled()) {
				// wait
				Thread.sleep(100);
			}
			done = true;
		}
		
		public void cancel() {
			this.thread.interrupt();
			monitor.setCanceled(true);
		}
		
		private Thread createThread() {
			return new Thread( () -> {
					valid = isCamelVersionValid(camelVersionToValidate);
					if (!monitor.isCanceled()) {
						monitor.done();
					}
			});
		}
		
		public boolean isDone() {
			return done;
		}
		
		public boolean isValid() {
			return valid;
		}
	}
	
	class VersionValidationHandler extends SelectionAdapter {
		
		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		@Override
		public void widgetSelected(SelectionEvent e) {
			String camelVersion = getSelectedCamelVersion();
			CamelVersionChecker versionChecker = new CamelVersionChecker(camelVersion);
			try {					
				getWizard().getContainer().run(true, true, versionChecker);
			} catch (InterruptedException iex) {
				versionChecker.cancel();
				Thread.currentThread().interrupt();
			} catch (Exception ex) {
				ProjectTemplatesActivator.pluginLog().logError(ex);
			}
			while (!versionChecker.isDone()) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException ex) {
					Thread.currentThread().interrupt();
				}
			}
			updateCamelValidation(camelVersion, versionChecker.isValid());
		}
		
		private void updateCamelValidation(String camelVersion, boolean valid) {
			if (!valid) {
				setMessage(null);
				setErrorMessage(NLS.bind(Messages.newProjectWizardRuntimePageCamelVersionInvalidWarning, camelVersion));
			} else {
				setErrorMessage(null);
				setMessage(Messages.newProjectWizardRuntimePageCamelVersionValidInfo, INFORMATION);
			}
			setPageComplete(valid);
		}
	}
}
