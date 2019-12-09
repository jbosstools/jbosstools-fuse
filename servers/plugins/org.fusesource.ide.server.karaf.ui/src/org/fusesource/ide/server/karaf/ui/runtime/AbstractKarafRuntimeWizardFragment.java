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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.eclipse.wst.server.ui.wizard.WizardFragment;
import org.fusesource.ide.server.karaf.core.Activator;
import org.fusesource.ide.server.karaf.core.runtime.IKarafRuntime;
import org.fusesource.ide.server.karaf.core.runtime.IKarafRuntimeWorkingCopy;

public abstract class AbstractKarafRuntimeWizardFragment extends WizardFragment {

	protected AbstractKarafRuntimeComposite composite = null;
	protected final KarafWizardDataModel model = new KarafWizardDataModel();

	public AbstractKarafRuntimeWizardFragment() {
	}

	@Override
	public Composite createComposite(Composite parent, IWizardHandle handle) {
		getTaskModel().putObject(KarafWizardDataModel.KARAF_MODEL, model);
		populateModel();
		composite = getRuntimeComposite(parent, handle, model);
		composite.createContents();
		return composite;
	}

	@Override
	public boolean hasComposite() {
		return true;
	}

	@Override
	public void performFinish(IProgressMonitor monitor) throws CoreException {
		super.performFinish(monitor);
		if (composite != null)
			composite.performFinish();
		updateRuntime();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.wst.server.ui.wizard.WizardFragment#performCancel(org.eclipse
	 * .core.runtime.IProgressMonitor)
	 */
	@Override
	public void performCancel(IProgressMonitor monitor) throws CoreException {
		if (composite != null && !composite.isDisposed())
			composite.cancel();
		super.performCancel(monitor);
	}

	protected IRuntimeWorkingCopy getRuntimeWorkingCopy() {
		return (IRuntimeWorkingCopy) getTaskModel().getObject(
				TaskModel.TASK_RUNTIME);
	}

	/**
	 * updates the model from runtime.
	 */
	protected void populateModel() {
		IRuntimeWorkingCopy workingCopy = getRuntimeWorkingCopy();
		if (workingCopy != null) {
			// workCopy will be instance of ServerDelegate classs.
			// We need to get the params, so IFuseESBRuntime will be enough.
			IKarafRuntime karafRuntime = (IKarafRuntime) workingCopy
					.loadAdapter(IKarafRuntime.class, new NullProgressMonitor());
			if (karafRuntime != null) {
				IPath loc = karafRuntime.getLocation();
				model.setKarafInstallDir(loc == null ? null : loc.toOSString());
			}
		}
	}

	/**
	 * This updates the runtime.
	 */
	private void updateRuntime() {
		IRuntimeWorkingCopy workingCopy = getRuntimeWorkingCopy();
		if (workingCopy != null) {
			// workCopy will be instance of ServerDelegate classs.
			// We need to get the params, so IKarafRuntime will be enough.
			IKarafRuntimeWorkingCopy karafRuntimeWorkingCopy = (IKarafRuntimeWorkingCopy) workingCopy
					.loadAdapter(IKarafRuntimeWorkingCopy.class,
							new NullProgressMonitor());
			if (karafRuntimeWorkingCopy != null) {
				String installDir = model.getKarafInstallDir();
				IPath path = new Path(installDir);
				workingCopy.setLocation(path);
			}
		}
		try {
			workingCopy.save(true, new NullProgressMonitor());
		} catch (CoreException e) {
			Activator.getLogger().error(e);
		}
	}

	protected abstract AbstractKarafRuntimeComposite getRuntimeComposite(
			Composite parent, IWizardHandle handle, KarafWizardDataModel model);

	@Override
	public boolean isComplete() {
		return composite == null || composite.isDisposed() ? false : composite.isValid();
	}

	@Override
	public void enter() {
		if (composite != null && composite.isDisposed() == false) {
			IRuntimeWorkingCopy runtime = (IRuntimeWorkingCopy) getTaskModel()
					.getObject(TaskModel.TASK_RUNTIME);
			composite.setRuntime(runtime);
		}
	}
}
