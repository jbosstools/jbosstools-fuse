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
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.fusesource.ide.server.karaf.core.runtime.IKarafRuntime;
import org.fusesource.ide.server.karaf.core.runtime.IKarafRuntimeWorkingCopy;
import org.jboss.ide.eclipse.as.core.server.bean.ServerBeanLoader;
import org.jboss.ide.eclipse.as.ui.wizards.AbstractJBTRuntimeWizardFragment;
import org.jboss.ide.eclipse.as.ui.wizards.composite.JREComposite;
import org.jboss.ide.eclipse.as.ui.wizards.composite.JREComposite.IJRECompositeListener;

/**
 * @author Stryker
 */
public class KarafRuntimeFragment extends AbstractJBTRuntimeWizardFragment {

	protected void updateWizardHandle(Composite parent) {
		// make modifications to parent
		IRuntime r = getRuntimeFromTaskModel();
		handle.setTitle( "Karaf Runtime" );
		String descript = r.getRuntimeType().getDescription();
		handle.setDescription(descript);
		handle.setImageDescriptor(getImageDescriptor());
		initiateHelp(parent);
	}
	
	protected void createJREComposite(Composite main) {
		// Create our composite
		jreComposite = new JREComposite(main, SWT.NONE, getTaskModel()) {
			public IExecutionEnvironment getExecutionEnvironment() {
				IRuntime r = getRuntimeFromTaskModel();
				IKarafRuntime jbsrt = (IKarafRuntime)r.loadAdapter(IKarafRuntime.class, null);
				return jbsrt.getExecutionEnvironment();
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

			public List<IVMInstall> getValidJREs() {
				IRuntime r = getRuntimeFromTaskModel();
				IKarafRuntime jbsrt = (IKarafRuntime)r.loadAdapter(IKarafRuntime.class, null);
				return Arrays.asList(jbsrt.getValidJREs());
			}
		};
		FormData cData = new FormData();
		cData.left = new FormAttachment(0, 5);
		cData.right = new FormAttachment(100, -5);
		cData.top = new FormAttachment(homeDirComposite, 10);
		jreComposite.setLayoutData(cData);
		jreComposite.setListener(new IJRECompositeListener(){
			public void vmChanged(JREComposite comp) {
				updatePage();
			}
		});
	}
	
	protected String getExplanationText() {
		return "Please point to a karaf installation";
	}

	protected void initiateHelp(Composite parent) {
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, "org.jboss.ide.eclipse.as.doc.user.new_server_runtime"); //$NON-NLS-1$		
	}
	
	protected String getHomeVersionWarning() {
		String homeDir = homeDirComposite.getHomeDirectory();
		File loc = new File(homeDir);
		String serverId = new ServerBeanLoader(loc).getServerAdapterId();
		String rtId = serverId == null ? null : 
				ServerCore.findServerType(serverId).getRuntimeType().getId();
		IRuntime adapterRt = getRuntimeFromTaskModel();
		String adapterRuntimeId = adapterRt.getRuntimeType().getId();
		if( !adapterRuntimeId.equals(rtId)) {
			return NLS.bind("Incorrect Version Error{0} {1}", 
					adapterRt.getRuntimeType().getVersion(), 
					getVersionString(loc));
		}
		return null;
	}

	@Override
	protected void saveJreInRuntime(IRuntimeWorkingCopy wc) {
		IKarafRuntimeWorkingCopy srt = (IKarafRuntimeWorkingCopy) wc.loadAdapter(
				IKarafRuntimeWorkingCopy.class, new NullProgressMonitor());
		if( srt != null ) {
			if( jreComposite.getSelectedVM() != null )
				srt.setVM(jreComposite.getSelectedVM());
			else
				srt.setVM(null);
		}
	}
	
	protected ImageDescriptor getImageDescriptor() {
		// TODO: return an icon for the runtime
		return null;
	}
}
