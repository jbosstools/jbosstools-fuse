/*******************************************************************************
* Copyright (c) 2014 Red Hat, Inc.
* Distributed under license by Red Hat, Inc. All rights reserved.
* This program is made available under the terms of the
* Eclipse Public License v1.0 which accompanies this distribution,
* and is available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
* Red Hat, Inc. - initial API and implementation
* William Collins punkhornsw@gmail.com
******************************************************************************/ 
package org.fusesource.ide.sap.imports;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class SAPPluginsBuilder implements IRunnableWithProgress {

	private JCo3ImportSettings jco3ImportSettings;
	private IDoc3ImportSettings idoc3ImportSettings;

	public SAPPluginsBuilder(JCo3ImportSettings jco3ImportSettings, IDoc3ImportSettings idoc3ImportSettings) {
		this.jco3ImportSettings = jco3ImportSettings;
		this.idoc3ImportSettings = idoc3ImportSettings;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		monitor.beginTask(Messages.SAPPluginsBuilder_ImportingSAPPLibraries, 3);
		try {
			// Deploy JCo3 Jar Bundle
			jco3ImportSettings.getJco3Archive().buildJCoPlugin(jco3ImportSettings);
			monitor.worked(1);
			if (monitor.isCanceled())
				throw new InterruptedException();

			// Deploy JCo3 Native Library Bundle
			jco3ImportSettings.getJco3Archive().buildJCoNativePlugin(jco3ImportSettings);
			monitor.worked(1);
			if (monitor.isCanceled())
				throw new InterruptedException();
			
			// Deploy IDoc3 Jar Bundle
			idoc3ImportSettings.getIdoc3Archive().buildIDoc3Plugin(idoc3ImportSettings);
			monitor.worked(1);
			if (monitor.isCanceled()) 
				throw new InterruptedException();
		} catch (IOException e) {
			throw new InvocationTargetException(e);
		} finally {
			monitor.done();
		}
	}

}
