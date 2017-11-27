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
package org.fusesource.ide.projecttemplates.impl.simple;

import org.fusesource.ide.projecttemplates.adopters.creators.TemplateCreatorSupport;
import org.fusesource.ide.projecttemplates.util.NewProjectMetaData;

/**
 * this template is used for creating a blank project (not template based)
 * 
 * @author lhein
 */
public class EmptyProjectTemplateForFuse6 extends AbstractEmptyProjectTemplate {
	
	@Override
	public TemplateCreatorSupport getCreator(NewProjectMetaData projectMetaData) {
		return new BlankProjectCreator("6");
	}
	
	@Override
	public boolean isCompatible(String camelVersion) {
		return isStrictlyLowerThan2200(camelVersion);
	}
}
