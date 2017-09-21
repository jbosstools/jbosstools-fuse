/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.qe.reddeer.editor;

import java.util.List;

import org.jboss.reddeer.common.wait.WaitUntil;
import org.jboss.reddeer.core.condition.ShellWithTextIsAvailable;
import org.jboss.reddeer.jface.wizard.WizardDialog;
import org.jboss.reddeer.swt.impl.button.PushButton;
import org.jboss.reddeer.swt.impl.combo.LabeledCombo;
import org.jboss.reddeer.swt.impl.shell.DefaultShell;
import org.jboss.reddeer.swt.impl.text.LabeledText;
import org.jboss.reddeer.swt.impl.tree.DefaultTreeItem;

/**
 * Manipulates with dialog (Wizard) for adding Global Camel Data Formats
 * 
 * @author djelinek
 */
public class CamelDataFormatDialog extends WizardDialog {
	
	private static final String TYPE = "JBoss Fuse";
	
	public void activate() {
		new WaitUntil(new ShellWithTextIsAvailable("Create a new Data Format..."));
		new DefaultShell("Create a new Data Format...");		
	}
	
	public LabeledText getId() {
		return new LabeledText("Id: *");
	}
	
	/**
	 * Return name of data format element<br/>
	 */
	public String getIdText() {
		return new LabeledText("Id: *").getText();
	}
	
	/**
	 * Set name of Camel data format element<br/>
	 * 
	 * @param id
	 * 	 	   String id that will be set as name of data format element
	 */
	public void setIdText(String id) {
		new LabeledText("Id: *").setText(id);
	}
	
	public LabeledCombo getDataFormat() {
		return new LabeledCombo("Data Format:");
	}
	
	/**
	 * Method for select a Data Format global element in the Camel Data Format dialog<br/>
	 * 
	 * @param title
	 *            Name of a data format component that will be select
	 */
	public void chooseDataFormat(String title) {
		new LabeledCombo("Data Format:").setSelection(title);
	}
	
	/**
	 * Static method for gets an list of available data format global elements from the Camel Data Format dialog<br/>
	 */
	public static List<String> getDataFormats() {

		CamelEditor.switchTab("Configurations");
		new PushButton("Add").click();
		new WaitUntil(new ShellWithTextIsAvailable("Create new global element..."));
		new DefaultShell("Create new global element...");
		new DefaultTreeItem(new String[] { TYPE, "Data Format" }).select();
		new PushButton("OK").click();		
		new WaitUntil(new ShellWithTextIsAvailable("Create a new Data Format..."));
		new DefaultShell("Create a new Data Format...");				
		CamelDataFormatDialog formatDialog = new CamelDataFormatDialog();
		formatDialog.activate();			
		List<String> items = new LabeledCombo("Data Format:").getItems();	
		formatDialog.cancel();
		return items;
	}
}
