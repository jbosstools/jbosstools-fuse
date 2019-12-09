/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.launcher.debug.util;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.ui.IEditorInput;
import org.fusesource.ide.launcher.debug.model.CamelDebugTarget;

/**
 * @author lhein
 */
public class CamelDebugRegistryEntry {
	
	private String fileName;
	private CamelDebugTarget debugTarget;
	private IEditorInput editorInput;
	private ILaunchConfiguration launchConfig;
	
	/**
	 * 
	 * @param debugTarget
	 * @param fileName
	 * @param editorInput
	 * @param launchConfig
	 */
	public CamelDebugRegistryEntry(CamelDebugTarget debugTarget, String fileName, IEditorInput editorInput, ILaunchConfiguration launchConfig) {
		this.fileName = fileName;
		this.debugTarget = debugTarget;
		this.editorInput = editorInput;
		this.launchConfig = launchConfig;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return this.fileName;
	}
	
	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return the debugTarget
	 */
	public CamelDebugTarget getDebugTarget() {
		return this.debugTarget;
	}

	/**
	 * @param debugTarget the debugTarget to set
	 */
	public void setDebugTarget(CamelDebugTarget debugTarget) {
		this.debugTarget = debugTarget;
	}

	/**
	 * @return the editorInput
	 */
	public IEditorInput getEditorInput() {
		return this.editorInput;
	}

	/**
	 * @param editorInput the editorInput to set
	 */
	public void setEditorInput(IEditorInput editorInput) {
		this.editorInput = editorInput;
	}

	/**
	 * @return the launchConfig
	 */
	public ILaunchConfiguration getLaunchConfig() {
		return this.launchConfig;
	}

	/**
	 * @param launchConfig the launchConfig to set
	 */
	public void setLaunchConfig(ILaunchConfiguration launchConfig) {
		this.launchConfig = launchConfig;
	}
}
