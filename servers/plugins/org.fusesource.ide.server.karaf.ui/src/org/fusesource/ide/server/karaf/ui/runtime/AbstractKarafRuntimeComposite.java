/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.server.karaf.ui.runtime;

import java.io.File;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.core.internal.IInstallableRuntime;
import org.eclipse.wst.server.core.internal.ServerPlugin;
import org.eclipse.wst.server.ui.internal.ImageResource;
import org.eclipse.wst.server.ui.internal.wizard.TaskWizard;
import org.eclipse.wst.server.ui.internal.wizard.fragment.LicenseWizardFragment;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.eclipse.wst.server.ui.wizard.WizardFragment;
import org.fusesource.ide.server.karaf.core.Activator;
import org.fusesource.ide.server.karaf.ui.Messages;

@SuppressWarnings("restriction")
public abstract class AbstractKarafRuntimeComposite extends Composite implements
		Listener {

	public static final String SEPARATOR = File.separator;

	protected final Composite parentComposite;
	protected final IWizardHandle wizardHandle;
	protected Text txtKarafDir;
	protected final KarafWizardDataModel model;
	protected boolean valid = false;
	protected IInstallableRuntime ir;
	protected Job installRuntimeJob;
	protected IJobChangeListener jobListener;
	protected Button btnBrowseButton;
	protected Button btnDownloadAndInstallButton;
	protected Label installLabel;
	protected IRuntimeWorkingCopy runtimeWC;

	public AbstractKarafRuntimeComposite(Composite parent,
			IWizardHandle wizardHandle, KarafWizardDataModel model) {
		super(parent, SWT.NONE);
		this.parentComposite = parent;
		this.wizardHandle = wizardHandle;
		this.model = model;
		wizardHandle
				.setTitle(Messages.AbstractKarafRuntimeComposite_wizard_tite);
		wizardHandle
				.setDescription(Messages.AbstractKarafRuntimeComposite_wizard_desc);
		wizardHandle.setImageDescriptor(ImageResource
				.getImageDescriptor(ImageResource.IMG_WIZBAN_NEW_RUNTIME));
	}

	@Override
	public void handleEvent(Event event) {
		if (event.type == SWT.FocusIn) {
			handleFocusEvent(event);
		} else {
			if (event.widget == txtKarafDir && validate()) {
				String installDir = txtKarafDir.getText();
				model.setKarafInstallDir(installDir);
			}
		}

		wizardHandle.update();
	}

	protected void cancel() {
		if (this.installRuntimeJob != null)
			this.installRuntimeJob.cancel();
	}

	public void handleFocusEvent(Event event) {
		if (event.widget == txtKarafDir) {
			wizardHandle.setMessage(
					Messages.AbstractKarafRuntimeComposite_txt_info_msg,
					IMessageProvider.NONE);
		}

	}

	protected abstract boolean doClassPathEntiresExist(
			final String karafInstallDir);

	protected abstract String getKarafPropFileLocation(String karafInstallDir);

	public boolean validate() {
		valid = false;
		String dirLocation = txtKarafDir.getText().trim();
		if (dirLocation != null && !"".equals(dirLocation)) {
			File file = new File(dirLocation);
			if (!file.exists()) {
				wizardHandle.setMessage(
						Messages.AbstractKarafRuntimeComposite_no_dir,
						IMessageProvider.ERROR);
			} else if (!file.isDirectory()) {
				wizardHandle.setMessage(
						Messages.AbstractKarafRuntimeComposite_not_a_dir,
						IMessageProvider.ERROR);
			} else {
				File binKaraf = new File(dirLocation + SEPARATOR
						+ Messages.AbstractKarafRuntimeComposite_bin_karaf);
				File binKarafBat = new File(dirLocation + SEPARATOR
						+ Messages.AbstractKarafRuntimeComposite_bin_karaf_bat);
				File confFile = new File(getKarafPropFileLocation(dirLocation));
				if ((binKaraf.exists() || binKarafBat.exists())
						&& confFile.exists()
						&& doClassPathEntiresExist(dirLocation)) {
					valid = true;
					wizardHandle.setMessage("", IMessageProvider.NONE); //$NON-NLS-1$
				} else {
					wizardHandle.setMessage(
							Messages.AbstractKarafRuntimeComposite_invalid_dir,
							IMessageProvider.ERROR); //$NON-NLS-1$
				}
			}
		} else {
			wizardHandle.setMessage(
					Messages.AbstractKarafRuntimeComposite_wizard_help_msg,
					IMessageProvider.NONE); //$NON-NLS-1$
		}
		return valid;
	}

	void createContents() {
		setLayout(new GridLayout(3, false));
		Label lblKarafInstallDir = new Label(this, SWT.NONE);
		lblKarafInstallDir
				.setText(Messages.AbstractKarafRuntimeComposite_install_dir_label);
		txtKarafDir = new Text(this, SWT.BORDER);
		txtKarafDir.addListener(SWT.Modify, this);
		txtKarafDir.setText(model.getKarafInstallDir());
		GridData txtKarafDirGridData = new GridData();
		txtKarafDirGridData.grabExcessHorizontalSpace = true;
		txtKarafDirGridData.horizontalAlignment = SWT.FILL;
		txtKarafDir.setLayoutData(txtKarafDirGridData);

		btnBrowseButton = new Button(this, SWT.PUSH);
		btnBrowseButton
				.setText(Messages.AbstractKarafRuntimeComposite_browse_text);
		btnBrowseButton.addMouseListener(new MouseListener() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {
			}

			@Override
			public void mouseDown(MouseEvent e) {

			}

			@Override
			public void mouseUp(MouseEvent e) {
				DirectoryDialog dd = new DirectoryDialog(Display.getDefault()
						.getActiveShell(), SWT.OPEN);
				dd.setFilterPath(txtKarafDir.getText());
				String dir = dd.open();
				if (dir != null) {
					txtKarafDir.setText(dd.getFilterPath());
				}
			}

		});

		installLabel = new Label(this, SWT.NONE);
		installLabel
				.setText(Messages.AbstractKarafRuntimeComposite_runtimeinstall_label);
		GridData installLabelGridData = new GridData();
		installLabelGridData.grabExcessHorizontalSpace = true;
		installLabelGridData.horizontalAlignment = SWT.FILL;
		installLabelGridData.horizontalSpan = 2;
		installLabel.setLayoutData(installLabelGridData);

		btnDownloadAndInstallButton = new Button(this, SWT.PUSH);
		btnDownloadAndInstallButton
				.setText(Messages.AbstractKarafRuntimeComposite_downloadAndInstall_text);
		btnDownloadAndInstallButton
				.setToolTipText(Messages.AbstractKarafRuntimeComposite_downloadAndInstall_description);
		btnDownloadAndInstallButton.addMouseListener(new MouseListener() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {
			}

			@Override
			public void mouseDown(MouseEvent e) {

			}

			@Override
			public void mouseUp(MouseEvent e) {
				ir = ServerPlugin.findInstallableRuntime(runtimeWC
						.getRuntimeType().getId());
				btnDownloadAndInstallButton.setEnabled(ir != null);

				if (ir == null)
					return;

				String license = null;
				try {
					license = ir.getLicense(new NullProgressMonitor());
				} catch (CoreException ex) {
					Activator.getLogger().error(ex);
				}
				TaskModel taskModel = new TaskModel();
				taskModel.putObject(LicenseWizardFragment.LICENSE, license);
				TaskWizard wizard2 = new TaskWizard(
						Messages.AbstractKarafRuntimeComposite_jboss_fuse_rt_label,
						new WizardFragment() {
							@Override
							protected void createChildFragments(
									List<WizardFragment> list) {
								list.add(new LicenseWizardFragment());
								list.add(new RTITargetFolderWizardFragment());
							}
						}, taskModel);

				WizardDialog dialog2 = new WizardDialog(getShell(), wizard2);
				if (dialog2.open() == Window.CANCEL)
					return;

				final String selectedDirectory = (String) taskModel
						.getObject(RTITargetFolderWizardFragment.FUSE_RT_LOC);

				if (selectedDirectory != null) {
					// ir.install(new Path(selectedDirectory));
					final IPath installPath = new Path(selectedDirectory);
					installRuntimeJob = new Job("Installing server runtime "
							+ ir.getName() + "...") {

						@Override
						public boolean belongsTo(Object family) {
							return ServerPlugin.PLUGIN_ID.equals(family);
						}

						@Override
						protected IStatus run(IProgressMonitor monitor) {
							try {
								Display.getDefault().asyncExec(new Runnable() {
									@Override
									public void run() {
										installLabel.setText(getName());
										btnBrowseButton.setEnabled(false);
										btnDownloadAndInstallButton
												.setEnabled(false);
										txtKarafDir.setEnabled(false);
										parentComposite.update();
									}
								});
								ir.install(installPath, monitor);
							} catch (CoreException ce) {
								return ce.getStatus();
							}

							return Status.OK_STATUS;
						}
					};
					jobListener = new JobChangeAdapter() {
						@Override
						public void done(IJobChangeEvent event) {
							Display.getDefault().asyncExec(new Runnable() {
								@Override
								public void run() {
									if (!AbstractKarafRuntimeComposite.this
											.isDisposed()) {
										installLabel.setText("Installation of "
												+ ir.getName() + " completed.");
										btnBrowseButton.setEnabled(true);
										btnDownloadAndInstallButton
												.setEnabled(true);
										txtKarafDir.setEnabled(true);
										txtKarafDir.setText(selectedDirectory);
										parentComposite.update();
									}
								}
							});
							installRuntimeJob.removeJobChangeListener(this);
							installRuntimeJob = null;
							Display.getDefault().asyncExec(new Runnable() {
								@Override
								public void run() {
									if (!isDisposed()) {
										validate();
									}
								}
							});
						}
					};
					installRuntimeJob.addJobChangeListener(jobListener);
					installRuntimeJob.schedule();
				}
			}

		});
		wizardHandle.update();
	}

	protected void setRuntime(IRuntimeWorkingCopy newRuntime) {
		if (newRuntime == null) {
			runtimeWC = null;
		} else {
			runtimeWC = newRuntime;
		}

		if (runtimeWC == null) {
			ir = null;
			btnDownloadAndInstallButton.setEnabled(false);
			installLabel.setText("");
		} else {
			ir = ServerPlugin.findInstallableRuntime(runtimeWC.getRuntimeType()
					.getId());
			if (ir != null) {
				btnDownloadAndInstallButton.setEnabled(true);
				// installLabel.setText(ir.getName());
			}
		}
		init();
		validate();
	}

	protected void init() {
		if (runtimeWC.getLocation() != null)
			txtKarafDir.setText(runtimeWC.getLocation().toOSString());
		else
			txtKarafDir.setText("");
	}

	void performFinish() {
	}

	protected boolean isValid() {
		return valid;
	}
}
