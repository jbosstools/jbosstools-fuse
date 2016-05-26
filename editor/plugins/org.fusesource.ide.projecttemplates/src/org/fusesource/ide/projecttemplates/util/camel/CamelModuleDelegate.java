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
package org.fusesource.ide.projecttemplates.util.camel;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jst.common.internal.modulecore.AddMappedOutputFoldersParticipant;
import org.eclipse.jst.common.internal.modulecore.IgnoreJavaInSourceFolderParticipant;
import org.eclipse.wst.common.componentcore.internal.flat.GlobalHeirarchyParticipant;
import org.eclipse.wst.common.componentcore.internal.flat.IFlattenParticipant;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.jboss.ide.eclipse.as.wtp.core.modules.IJBTModule;
import org.jboss.ide.eclipse.as.wtp.core.modules.JBTFlatModuleDelegate;
import org.jboss.ide.eclipse.as.wtp.core.modules.JBTFlatProjectModuleFactory;

/**
 * @author lhein
 */
public class CamelModuleDelegate extends JBTFlatModuleDelegate implements IJBTModule {

	public CamelModuleDelegate(IProject project,
			IVirtualComponent aComponent, JBTFlatProjectModuleFactory myFactory) {
		super(project, aComponent, myFactory);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.wst.web.internal.deployables.FlatComponentDeployable#getParticipants()
	 */
	@Override
	public IFlattenParticipant[] getParticipants() {
		List<IFlattenParticipant> participants = new ArrayList<IFlattenParticipant>();
		participants.add(new GlobalHeirarchyParticipant());
		participants.add(new AddMappedOutputFoldersParticipant());
		participants.add(new IgnoreJavaInSourceFolderParticipant());
		return participants.toArray(new IFlattenParticipant[participants.size()]);
	}
}
