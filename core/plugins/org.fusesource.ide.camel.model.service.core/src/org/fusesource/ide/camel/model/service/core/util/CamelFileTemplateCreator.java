/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 

package org.fusesource.ide.camel.model.service.core.util;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.fusesource.ide.foundation.core.util.IOUtils;

/**
 * @author lhein
 */
public class CamelFileTemplateCreator {
	
	/**
	 * creates a camel spring context template file
	 * 
	 * @param f	the file to use / to write to
	 */
	public void createSpringTemplateFile(IFile f) {
		File file = f.getRawLocation().makeAbsolute().toFile();
		createSpringTemplateFile(file);
	}
	
	/**
	 * creates a camel blueprint context template file
	 * 
	 * @param f	the file to use / to write to
	 */
	public void createBlueprintTemplateFile(IFile f) {
		File file = f.getRawLocation().makeAbsolute().toFile();
		createBlueprintTemplateFile(file);
	}
	
	/**
	 * creates a camel routes context template file
	 * 
	 * @param f	the file to use / to write to
	 */
	public void createRoutesTemplateFile(IFile f) {
		File file = f.getRawLocation().makeAbsolute().toFile();
		createRoutesTemplateFile(file);
	}
	
	/**
	 * creates a camel spring context template file
	 * 
	 * @param f	the file to use / to write to
	 */
	public void createSpringTemplateFile(File f) {
		createTemplateFile(f, getSpringStubText());
	}
	
	/**
	 * creates a camel blueprint context template file
	 * 
	 * @param f	the file to use / to write to
	 */
	public void createBlueprintTemplateFile(File f) {
		createTemplateFile(f, getBlueprintStubText());
	}
	
	/**
	 * creates a camel routes context template file
	 * 
	 * @param f	the file to use / to write to
	 */
	public void createRoutesTemplateFile(File f) {
		createTemplateFile(f, getRoutesStubText());
	}
	
	private void createTemplateFile(File f, String content) {
		if (f == null) throw new IllegalArgumentException("The given file parameter can't be null.");
		if (f.isDirectory()) throw new IllegalArgumentException("The given file parameter can't be a folder.");
		
		if (f.exists() && f.isFile()) f.delete();
		try {
			if (f.createNewFile()) {
				IOUtils.writeText(f, content);
			}
		} catch (IOException ex) {
			throw new IllegalArgumentException("An error occured creating the template file..." ,ex);
		}
	}
	
	/**
	 * returns a stub template for a spring camel context
	 * 
	 * @return
	 */
	String getSpringStubText() {
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append("<beans xmlns=\"http://www.springframework.org/schema/beans\"\n");
		sb.append("       xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.springframework.org/schema/beans https://www.springframework.org/schema/beans/spring-beans.xsd        http://camel.apache.org/schema/spring https://camel.apache.org/schema/spring/camel-spring.xsd\">\n");
		sb.append("   <camelContext id=\"camelContext1\" xmlns=\"http://camel.apache.org/schema/spring\">\n");
		sb.append("   </camelContext>\n");
		sb.append("</beans>\n");
		return sb.toString();
	}
	
	/**
	 * returns a stub template for a blueprint camel context
	 * 
	 * @return
	 */
	String getBlueprintStubText() {
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append("<blueprint xmlns=\"http://www.osgi.org/xmlns/blueprint/v1.0.0\"\n");
		sb.append("		xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
		sb.append("		       xsi:schemaLocation=\"\n");
		sb.append("       http://www.osgi.org/xmlns/blueprint/v1.0.0 https://www.osgi.org/xmlns/blueprint/v1.0.0/blueprint.xsd\n");
		sb.append("       http://camel.apache.org/schema/blueprint https://camel.apache.org/schema/blueprint/camel-blueprint.xsd\">\n");
		sb.append("\n");
		sb.append("	<camelContext id=\"context1\" xmlns=\"http://camel.apache.org/schema/blueprint\">\n");
		sb.append("\n");
		sb.append("	</camelContext>\n");
		sb.append("\n");
		sb.append("</blueprint>\n");
		return sb.toString();
	}
	
	/**
	 * returns a stub template for a routes
	 * 
	 * @return
	 */
	String getRoutesStubText() {
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append("<routes xmlns=\"http://camel.apache.org/schema/spring\">\n");
		sb.append("</routes>\n");
		return sb.toString();
	}
}
