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
package org.fusesource.ide.projecttemplates.adopters.creators;

import java.io.IOException;
import java.io.InputStream;

import org.fusesource.ide.projecttemplates.util.CommonNewProjectMetaData;

/**
 * this abstract super class can be used to write creators which use streams
 * to create the project contents. This can be URL stream or Jar / Zip Streams 
 * or combinations of streams
 * 
 * @author lhein
 */
public abstract class InputStreamCreator implements TemplateCreatorSupport {
	
	/**
	 * returns the inputstream which holds the template data
	 * 
	 * @param metadata		the project metadata
	 * @return
	 * @throws IOException
	 */
	public abstract InputStream getTemplateStream(CommonNewProjectMetaData metadata) throws IOException;
}
