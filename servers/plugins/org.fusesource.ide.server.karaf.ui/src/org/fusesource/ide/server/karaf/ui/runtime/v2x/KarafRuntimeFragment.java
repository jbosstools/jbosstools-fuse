/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.server.karaf.ui.runtime.v2x;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.environments.IExecutionEnvironment;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.TaskModel;
import org.fusesource.ide.server.karaf.core.runtime.IKarafRuntime;
import org.fusesource.ide.server.karaf.core.runtime.IKarafRuntimeWorkingCopy;
import org.fusesource.ide.server.karaf.core.runtime.KarafRuntimeDelegate;
import org.fusesource.ide.server.karaf.ui.KarafUIPlugin;
import org.fusesource.ide.server.karaf.ui.Messages;
import org.jboss.ide.eclipse.as.core.server.bean.ServerBeanLoader;
import org.jboss.ide.eclipse.as.wtp.ui.composites.AbstractJREComposite;
import org.jboss.ide.eclipse.as.wtp.ui.composites.RuntimeHomeComposite;
import org.jboss.ide.eclipse.as.wtp.ui.wizard.RuntimeWizardFragment;
import org.jboss.tools.as.runtimes.integration.ui.composites.DownloadRuntimeHomeComposite;

/**
 * @author Stryker
 */
public class KarafRuntimeFragment extends RuntimeWizardFragment {

	@Override
	protected void updateWizardHandle(Composite parent) {
		// make modifications to parentComposite
		IRuntime r = getRuntimeFromTaskModel();
		handle.setTitle(Messages.AbstractKarafRuntimeComposite_wizard_tite);
		String descript = r.getRuntimeType().getDescription();
		handle.setDescription(descript);
		handle.setImageDescriptor(getImageDescriptor());
		initiateHelp(parent);
	}
	
	protected ImageDescriptor getImageDescriptor() {
		return KarafUIPlugin.getDefault().getImageRegistry().getDescriptor(KarafUIPlugin.IMG_KARAF_LOGO_LARGE);
	}
	
	@Override
	protected AbstractJREComposite createJRECompositeWidget(Composite main) {
		// Create our composite
		return new KarafJREComposite(main, SWT.NONE, getTaskModel());
	}
	
	@Override
	protected String getExplanationText() {
		return "Please point to a Karaf installation.";
	}

	protected void initiateHelp(Composite parent) {
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, "org.jboss.ide.eclipse.as.doc.user.new_server_runtime"); //$NON-NLS-1$		
	}

	@Override
	protected String getHomeVersionWarning() {
		String homeDir = homeDirComposite.getHomeDirectory();
		if (homeDir != null) {
			File loc = new File(homeDir);
			String serverId = new ServerBeanLoader(loc).getServerAdapterId();
			String rtId = serverId == null ? null : 
				ServerCore.findServerType(serverId).getRuntimeType().getId();
			IRuntime adapterRt = getRuntimeFromTaskModel();
			String adapterRuntimeId = adapterRt.getRuntimeType().getId();
			if( !adapterRuntimeId.equals(rtId)) {
				return NLS.bind("Incorrect Version Error {0} {1}", 
						adapterRt.getRuntimeType().getVersion(), 
						getVersionString(loc));
			}
		}
		return null;
	}
	
	@Override
	protected String getErrorString() {
		String superError = getErrorFromParent();
		String homeVersionWarning = getHomeVersionWarning();
		
		if (superError != null && homeVersionWarning != null) {
			return superError +"\n"+ homeVersionWarning;
		} else if (superError == null) {
			return homeVersionWarning;
		} else {
			return superError;
		}
	}

	String getErrorFromParent() {
		return super.getErrorString();
	}

	@Override
	protected void saveJreInRuntime(IRuntimeWorkingCopy wc) {
		IKarafRuntimeWorkingCopy srt = (IKarafRuntimeWorkingCopy) wc.loadAdapter(
				IKarafRuntimeWorkingCopy.class, new NullProgressMonitor());
		if( srt != null ) {
			IExecutionEnvironment selectedEnv = jreComposite.getSelectedExecutionEnvironment();
			IVMInstall selectedVM = jreComposite.getSelectedVM();
			srt.setVM(selectedVM);
			srt.setExecutionEnvironment(selectedEnv);
		}
	}

	@Override
	protected RuntimeHomeComposite createHomeCompositeWidget(Composite main) {
		return new DownloadRuntimeHomeComposite(main, SWT.NONE, handle, getTaskModel());
	}

	@Override
	protected void saveRuntimeLocationInPreferences(IRuntime runtime) {
		// Do nothing
	}
	
	protected class KarafJREComposite extends AbstractJREComposite {

		public KarafJREComposite(Composite parent, int style, TaskModel tm) {
			super(parent, style, tm);
		}		
		protected boolean isUsingDefaultJRE(IRuntime rt) {
			IRuntime r = getRuntimeFromTaskModel();
			IKarafRuntime jbsrt = (IKarafRuntime)r.loadAdapter(IKarafRuntime.class, null);
			return jbsrt.isUsingDefaultJRE();
		}
		
		protected IVMInstall getStoredJRE(IRuntime rt) {
			IRuntime r = getRuntimeFromTaskModel();
			IKarafRuntime jbsrt = (IKarafRuntime)r.loadAdapter(IKarafRuntime.class, null);
			return jbsrt.isUsingDefaultJRE() ? null : jbsrt.getVM();
		}

		@Override
		public List<IVMInstall> getValidJREs() {
			IRuntime r = getRuntimeFromTaskModel();
			IKarafRuntime jbsrt = (IKarafRuntime)r.loadAdapter(IKarafRuntime.class, null);
			return Arrays.asList(jbsrt.getValidJREs());
		}
		@Override
		protected IRuntime getRuntimeFromTaskModel() {
			return (IRuntime) getTaskModel().getObject(TaskModel.TASK_RUNTIME);
		}
		protected boolean isUsingDefaultJRE() {
			IRuntime r = getRuntimeFromTaskModel();
			IKarafRuntime jbsrt = (IKarafRuntime)r.loadAdapter(IKarafRuntime.class, null);
			return jbsrt.isUsingDefaultJRE();
		}
		
		@Override
		protected IVMInstall getStoredJRE() {
			IRuntime r = getRuntimeFromTaskModel();
			IKarafRuntime jbsrt = (IKarafRuntime)r.loadAdapter(IKarafRuntime.class, null);
			return ((KarafRuntimeDelegate)jbsrt).getHardVM();
		}
		@Override
		public IExecutionEnvironment getMinimumExecutionEnvironment() {
			IRuntime r = getRuntimeFromTaskModel();
			IKarafRuntime jbsrt = (IKarafRuntime)r.loadAdapter(IKarafRuntime.class, null);
			return jbsrt.getMinimumExecutionEnvironment();
		}
		@Override
		public IExecutionEnvironment getStoredExecutionEnvironment() {
			IRuntime r = getRuntimeFromTaskModel();
			IKarafRuntime jbsrt = (IKarafRuntime)r.loadAdapter(IKarafRuntime.class, null);
			return jbsrt.getExecutionEnvironment();
		}
	}
}
