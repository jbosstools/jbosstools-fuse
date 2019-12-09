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
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.eclipse.wst.server.ui.wizard.WizardFragment;
import org.fusesource.ide.server.karaf.core.Activator;
import org.fusesource.ide.server.karaf.core.runtime.IKarafRuntime;
import org.fusesource.ide.server.karaf.core.server.IKarafServerDelegate;
import org.fusesource.ide.server.karaf.core.server.IKarafServerDelegateWorkingCopy;
import org.fusesource.ide.server.karaf.core.util.KarafUtils;


public abstract class AbstractKarafServerWizardFragment extends WizardFragment {

	private KarafServerPorpertiesComposite composite = null;
	protected KarafWizardDataModel model = null;

	public AbstractKarafServerWizardFragment() {
	}
 
	@Override
	public Composite createComposite(Composite parent, IWizardHandle handle) {
		getWizardModel();
		composite = new KarafServerPorpertiesComposite(parent, handle,
				model);
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
		composite.performFinish();
		updateServer();
	}

	private IRuntime getRuntimeWorkingCopy() {
		return (IRuntime) getTaskModel().getObject(TaskModel.TASK_RUNTIME);	}
	
	private IServerWorkingCopy getServerWorkingCopy() {
		return (IServerWorkingCopy) getTaskModel().getObject(TaskModel.TASK_SERVER);
	}

	private void getWizardModel() {
		Object objModel = getTaskModel().getObject(KarafWizardDataModel.KARAF_MODEL);
		if (objModel instanceof KarafWizardDataModel){
			model = (KarafWizardDataModel)objModel;
		} else{
			model = new KarafWizardDataModel();
		}
		populateModel();
	}
	
	/**
	 * updates the model from runtime.
	 */
	private void populateModel() {
		IServerWorkingCopy workingCopy = getServerWorkingCopy();
		if (model != null && workingCopy != null) {
			// workCopy will be instance of ServerDelegate classs.
			// We need to get the params, so IFuseESBRuntime will be enough.
			IKarafServerDelegate karafServerWorkingCopy = (IKarafServerDelegate) workingCopy.loadAdapter(IKarafServerDelegate.class, new NullProgressMonitor());
			if (karafServerWorkingCopy != null) {
				model.setUserName(karafServerWorkingCopy.getUserName());
				model.setPassword(karafServerWorkingCopy.getPassword());
			}
			IRuntime runtime = getRuntimeWorkingCopy();
			if (runtime != null){
				IKarafRuntime karafRuntime = (IKarafRuntime)runtime.loadAdapter(IKarafRuntime.class, null);
				if (karafRuntime != null
						&& ("".equals(model.getKarafInstallDir()) || model.getKarafInstallDir() == null)) {
					model.setKarafInstallDir(karafRuntime.getLocation().toOSString());
				}
			}
			if ( karafServerWorkingCopy != null) {
				model.setPortNumber(karafServerWorkingCopy.getPortNumber());
			}
		}
	}

	/**
	 * This updates the runtime.
	 */
	private void updateServer() {
		IServerWorkingCopy workingCopy = getServerWorkingCopy();
		if (workingCopy != null) {
			// workCopy will be instance of ServerDelegate classs.
			// We need to get the params, so IFuseESBRuntime will be enough.
			IKarafServerDelegateWorkingCopy karafServerWorkingCopy = (IKarafServerDelegateWorkingCopy) workingCopy
					.loadAdapter(IKarafServerDelegateWorkingCopy.class,
							new NullProgressMonitor());
			if (karafServerWorkingCopy != null) {
				karafServerWorkingCopy.setPortNumber(model.getPortNumber());
				karafServerWorkingCopy.setUserName(model.getUserName());
				karafServerWorkingCopy.setPassword(model.getPassword());
				workingCopy.setRuntime(getRuntimeWorkingCopy());
				try {
					workingCopy.save(true, new NullProgressMonitor());
				} catch (CoreException ex) {
					Activator.getLogger().error(ex);
				}
			}
		}
	}
	

	protected abstract void readFromPropertiesFile(File confFile) throws FileNotFoundException, IOException,NumberFormatException ;

	/**
	 * determines the version of the karaf installation from the manifest of the main bundle
	 * 
	 * @param runtime	the runtime to use for grabbing the install location
	 * @return	the version as string or null on errors
	 */
	protected String determineVersion(IKarafRuntime runtime) {
		String version = null;
		if (runtime != null && runtime.getLocation() != null) {
			File folder = runtime.getLocation().toFile();
			version = KarafUtils.getVersion(folder);
		}
		return version;
	}
	
	@Override
	public boolean isComplete() {
		if (composite == null || composite.isDisposed())
			return false;
		else
			return composite.isValid();
	}
}
