/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.server.karaf.core.runtime.classpath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jst.common.project.facet.core.IClasspathProvider;
import org.eclipse.jst.common.project.facet.core.JavaFacet;
import org.eclipse.jst.server.ui.internal.RuntimeLabelProvider;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponent;
import org.eclipse.wst.common.project.facet.ui.IRuntimeComponentLabelProvider;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.ServerCore;
import org.fusesource.ide.server.karaf.core.runtime.IKarafRuntime;

/**
 * This class is the entry point when WTP is attempting to adapt a
 * IRuntimeComponent into an IClasspathProvider. This is the first step in
 * turning a project / facet / runtime combination into classpath entries that
 * should be added to a project.
 */
public final class KarafClasspathProviderAdapterFactory implements IAdapterFactory {
	private static final Class<?>[] ADAPTER_TYPES = { IClasspathProvider.class, IRuntimeComponentLabelProvider.class };

	@Override
	public Object getAdapter(final Object adaptable, final Class adapterType) {
		IRuntimeComponent rc = (IRuntimeComponent) adaptable;
		if (IRuntimeComponentLabelProvider.class.equals(adapterType)) {
			return new RuntimeLabelProvider(rc);
		}
		return new RuntimeFacetClasspathProvider(rc);
	}

	@Override
	public Class<?>[] getAdapterList() {
		return ADAPTER_TYPES;
	}

	public class RuntimeFacetClasspathProvider implements IClasspathProvider {
		protected IRuntimeComponent rc;

		public RuntimeFacetClasspathProvider() {
		}

		public RuntimeFacetClasspathProvider(final IRuntimeComponent rc) {
			this.rc = rc;
		}

		/**
		 * This method lets you add custom jars to the classpath depending on
		 * what facet + version is being requested. So you can customize the
		 * jars from your server for utility, java, web, ejb, etc.
		 */
		@Override
		public List<IClasspathEntry> getClasspathEntries(final IProjectFacetVersion fv) {
			List<IClasspathEntry> ret = new ArrayList<>();
			if( fv.getProjectFacet().equals(JavaFacet.FACET)) {
				String runtimeId = rc.getProperty("id"); //$NON-NLS-1$
				ret.addAll(JREClasspathUtil.getJavaClasspathEntries(runtimeId));
			} else
				// If we would prefer to handle on a per-facet
			// basis, this is the place to do it.
				// Until we decide to do that, this will
				// simply delegate to the "client-all" container.
			{
				ret.addAll(getProjectRuntimeEntry());
			}
			return ret;
		}

		/*
		 * We simply return one entry, which is a classpath container The
		 * container will have a visible display name equivilent to
		 * {runtimeTypeName}/{runtimeName}, for example:
		 * 
		 * WildFly 8.x Runtime [My Wildfly 2nd runtime]
		 * 
		 * The returned container knows nothing of facets, and returns default
		 * lists.
		 */
		private List<IClasspathEntry> getProjectRuntimeEntry() {
			String id = rc.getProperty("id"); //$NON-NLS-1$
			IPath containerPath = KarafProjectRuntimeClasspathProvider.CONTAINER_PATH;
			IClasspathEntry cpentry = JavaCore.newContainerEntry(containerPath.append(id));
			return Collections.singletonList(cpentry);
		}
	}

	/**
	 * A utility class for getting a classpath entry
	 * relating to the vm/jre in use by the runtime.
	 */
	public static class JREClasspathUtil {
		// Get a classpath container for the VM
		public static List<IClasspathEntry> getJavaClasspathEntries(String runtimeId) {
			if( runtimeId != null ) {
				IRuntime runtime = ServerCore.findRuntime(runtimeId);
				if( runtime != null ) {
					IKarafRuntime  jbsRuntime = (IKarafRuntime)runtime.loadAdapter(IKarafRuntime.class, null);
					IVMInstall vmInstall = jbsRuntime.getVM();
					if (vmInstall != null) {
						String name = vmInstall.getName();
						String typeId = vmInstall.getVMInstallType().getId();
						IClasspathEntry[] entries = new IClasspathEntry[] {
								JavaCore.newContainerEntry(new Path(JavaRuntime.JRE_CONTAINER)
.append(typeId).append(name))
						};
						return Arrays.asList(entries);
					}
				}
			}
			return new ArrayList<>();
		}
	}
}