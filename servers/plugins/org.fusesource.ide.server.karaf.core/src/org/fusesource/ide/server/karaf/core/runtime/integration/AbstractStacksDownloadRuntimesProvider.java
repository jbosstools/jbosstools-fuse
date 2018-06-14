/*************************************************************************************
 * Copyright (c) 2013 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat - Initial implementation.
 ************************************************************************************/
package org.fusesource.ide.server.karaf.core.runtime.integration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.fusesource.ide.server.karaf.core.Messages;
import org.jboss.ide.eclipse.as.core.util.IJBossToolingConstants;
import org.jboss.jdf.stacks.model.Stacks;
import org.jboss.tools.runtime.core.model.DownloadRuntime;
import org.jboss.tools.runtime.core.model.IDownloadRuntimesProvider;

/**
 * Pull runtimes from a stacks file and return them to runtimes framework
 */
public abstract class AbstractStacksDownloadRuntimesProvider implements IDownloadRuntimesProvider {

	/* The following constants are marked public but are in an internal package. */
	public static final String LABEL_FILE_SIZE = "runtime-size";
	public static final String LABEL_WTP_RUNTIME = "wtp-runtime-type";
	public static final String LABEL_RUNTIME_CATEGORY = "runtime-category";
	public static final String LABEL_RUNTIME_TYPE = "runtime-type";
	public static final String PROP_WTP_RUNTIME = LABEL_WTP_RUNTIME;
	
	private ArrayList<DownloadRuntime> downloads = null;
	
	public AbstractStacksDownloadRuntimesProvider() {
	}

	protected abstract Stacks[] getStacks(IProgressMonitor monitor);

	
	@Override
	public DownloadRuntime[] getDownloadableRuntimes(String requestType, IProgressMonitor monitor) {
		if( downloads == null ) {
			ArrayList<DownloadRuntime> tmp = loadDownloadableRuntimes(monitor);
			if( monitor.isCanceled()) {
				// Return the incomplete list, but do not cache it
				return tmp.toArray(new DownloadRuntime[tmp.size()]);
			}
			// Cache this, as its assumed to be complete
			downloads = tmp;
		}
		return downloads.toArray(new DownloadRuntime[downloads.size()]);
	}
	
	/*
	 * Return an arraylist of downloadruntime objects
	 */
	private synchronized ArrayList<DownloadRuntime> loadDownloadableRuntimes(IProgressMonitor monitor) {
		monitor.beginTask(Messages.LoadRemoteRuntimes, 200);
		Stacks[] stacksArr = getStacks(new SubProgressMonitor(monitor, 100));
		ArrayList<DownloadRuntime> all = new ArrayList<DownloadRuntime>();
		monitor.beginTask(Messages.CreateDownloadRuntimes, stacksArr.length * 100);		
		for( int i = 0; i < stacksArr.length && !monitor.isCanceled(); i++ ) {
			IProgressMonitor inner = new SubProgressMonitor(monitor, 100);
			if( stacksArr[i] != null ) {
				traverseStacks(stacksArr[i], all, inner);
			}
		}
		monitor.done();
		return all;
	}
	
	protected abstract void traverseStacks(Stacks stacks, ArrayList<DownloadRuntime> list, IProgressMonitor monitor);
	
	protected void traverseStacks(Stacks stacks, ArrayList<DownloadRuntime> list, String category, IProgressMonitor monitor) {
		List<org.jboss.jdf.stacks.model.Runtime> runtimes = stacks.getAvailableRuntimes();
		Iterator<org.jboss.jdf.stacks.model.Runtime> i = runtimes.iterator();
		org.jboss.jdf.stacks.model.Runtime workingRT;
		monitor.beginTask(Messages.CreateDownloadRuntimes, runtimes.size() * 100);
		while(i.hasNext()) {
			workingRT = i.next();
			String categoryFromStacks = workingRT.getLabels().getProperty(LABEL_RUNTIME_CATEGORY);
			if( category.equals(categoryFromStacks)) {
				String wtpRT = workingRT.getLabels().getProperty(LABEL_WTP_RUNTIME);
				if( wtpRT != null ) {
					// We can make a DL out of this
					String fileSize = workingRT.getLabels().getProperty(LABEL_FILE_SIZE);
					String license = workingRT.getLicense();
					String dlUrl = getDownloadURL(workingRT);
					String id = workingRT.getId();
					String legacyId = getLegacyId(id);
					String effectiveId = legacyId == null ? id : legacyId;
					
					String name = workingRT.getName();
					String version = workingRT.getVersion();
					DownloadRuntime dr = new DownloadRuntime(effectiveId, name, version, dlUrl);
					dr.setDisclaimer(!wtpRT.startsWith(IJBossToolingConstants.EAP_RUNTIME_PREFIX));
					dr.setHumanUrl(workingRT.getUrl());
					dr.setLicenseURL(license);
					dr.setSize(fileSize);
					dr.setProperty(PROP_WTP_RUNTIME, wtpRT);
					dr.setProperty(LABEL_RUNTIME_CATEGORY, category);
					dr.setProperty(LABEL_RUNTIME_TYPE, workingRT.getLabels().getProperty(LABEL_RUNTIME_TYPE));
					if(workingRT.getLabels().get(DownloadRuntime.PROPERTY_REQUIRES_CREDENTIALS) != null ) 
						dr.setProperty(DownloadRuntime.PROPERTY_REQUIRES_CREDENTIALS, workingRT.getLabels().get(DownloadRuntime.PROPERTY_REQUIRES_CREDENTIALS));
					if( legacyId != null )
						dr.setProperty(DownloadRuntime.PROPERTY_ALTERNATE_ID, id);
					list.add(dr);
				}
			}
			monitor.worked(100);
		}
		monitor.done();
	}
	
	
	/**
	 * The following supposes a yaml runtime that has no property "downloadURL", or
	 * has a downloadURL set but also has a label that indicates windows has a 
	 * different url.
	 * 
	 * Approved os types are:
	 *
	 *		"win32";
	 *  	"linux";
	 *  	"aix";
	 *  	"solaris";
	 *  	"hpux";
	 *  	"qnx";
	 *  	"macosx";
	 *  
	 *  These values are also the same as those in {@link Platform}
	 * 
	 * The label "additionalDownloadURLs" will return a Map.
	 * The map will have the key of one of the above constants,
	 * and a value of a url. 
	 */
	protected String getDownloadURL(org.jboss.jdf.stacks.model.Runtime workingRT) {
		// First look for an override for this specific OS
		String os = Platform.getOS();
		Object o = workingRT.getLabels().get("additionalDownloadURLs");
		String firstPossibleDL = null;
		if( o instanceof Map ) {
			Map m = (Map)o;
			Iterator i = m.keySet().iterator();
			while(i.hasNext()) {
				Object iNext = i.next();
				if( iNext.equals(os)) {
					// current impl looks for an exact match... we may need to update this
					// to a soft match, like users running on hpux should match linux
					return (String)m.get(iNext);
				} else if( firstPossibleDL == null ) {
					firstPossibleDL = (String)m.get(iNext);
				}
			}
		}
		String dlUrl = workingRT.getDownloadUrl();
		if( dlUrl == null ) {
			// So that at least something can get downloaded if its not an exact match
			return firstPossibleDL;
		}
		return dlUrl;
	}
	
	
	protected abstract String getLegacyId(String id);
}
