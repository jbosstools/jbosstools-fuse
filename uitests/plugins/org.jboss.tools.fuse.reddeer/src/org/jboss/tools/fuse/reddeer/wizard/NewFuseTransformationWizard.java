/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.reddeer.wizard;

import org.eclipse.reddeer.common.logging.Logger;
import org.eclipse.reddeer.common.matcher.RegexMatcher;
import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.common.wait.WaitWhile;
import org.eclipse.reddeer.jface.wizard.WizardDialog;
import org.eclipse.reddeer.swt.condition.ControlIsEnabled;
import org.eclipse.reddeer.swt.condition.ShellIsActive;
import org.eclipse.reddeer.swt.condition.ShellIsAvailable;
import org.eclipse.reddeer.swt.impl.button.PushButton;
import org.eclipse.reddeer.swt.impl.button.RadioButton;
import org.eclipse.reddeer.swt.impl.combo.DefaultCombo;
import org.eclipse.reddeer.swt.impl.shell.DefaultShell;
import org.eclipse.reddeer.swt.impl.table.DefaultTable;
import org.eclipse.reddeer.swt.impl.text.LabeledText;
import org.jboss.tools.fuse.reddeer.condition.TableHasRow;

/**
 * Represents 'New Fuse Transformation Wizard'
 * 
 * @author tsedmik
 */
public class NewFuseTransformationWizard extends WizardDialog {

	public static final String TITLE = "New Fuse Transformation";
	private Logger log = Logger.getLogger(NewFuseTransformationWizard.class);

	public NewFuseTransformationWizard() {
		super(TITLE);
	}

	@Override
	public NewFuseTransformationWizard next() {
		new WaitUntil(new ControlIsEnabled(new PushButton("Next >")));
		return (NewFuseTransformationWizard) super.next();
	}

	public void setTransformationID(String id) {
		log.debug("Set 'Transformation ID' to '" + id + "'");
		new LabeledText("Transformation ID:").setText(id);
	}

	public void setSourceType(TransformationType type) {
		log.debug("Set 'Source Type' to '" + type + "'");
		new DefaultCombo(0).setSelection(type.toString());
	}

	public void setTargetType(TransformationType type) {
		log.debug("Set 'Target Type' to '" + type + "'");
		new DefaultCombo(1).setSelection(type.toString());
	}

	public void setXMLTypeDefinition(TypeDefinition type) {
		log.debug("Set 'XML Type Definition' to '" + type + "'");
		new RadioButton(type.equals(TypeDefinition.Schema) ? "XML Schema" : "XML Instance Document").toggle(true);
	}

	public void setXMLSourceFile(String name) {
		log.debug("Set 'XML Source File' to '" + name + "'");
		new PushButton("...").click();
		new WaitUntil(new ShellIsAvailable("Select XSD From Project"));
		new DefaultShell("Select XSD From Project");
		DefaultTable table = new DefaultTable();
		new WaitUntil(new TableHasRow(table, new RegexMatcher(name)));
		table.select(name);
		new PushButton("OK").click();
		new WaitUntil(new ShellIsActive("New Fuse Transformation"));
	}

	public void setJSONTypeDefinition(TypeDefinition type) {
		log.debug("Set 'JSON Type Definiton' to '" + type + "'");
		new RadioButton(type.equals(TypeDefinition.Schema) ? "JSON Schema" : "JSON Instance Document").toggle(true);
	}

	public void setJSONTargetFile(String name) {
		log.debug("Set 'JSON Target File' to '" + name + "'");
		new PushButton("...").click();
		new WaitUntil(new ShellIsAvailable("Select JSON From Project"));
		new DefaultShell("Select JSON From Project");
		DefaultTable table = new DefaultTable();
		new WaitUntil(new TableHasRow(table, new RegexMatcher(name)));
		table.select(name);
		new PushButton("OK").click();
		new WaitUntil(new ShellIsActive("New Fuse Transformation"));
	}

	@Override
	public void finish() {
		super.finish();
		if (new ShellIsAvailable("New Fuse Transformation").test()) {
			new WaitWhile(new ShellIsAvailable("New Fuse Transformation"), TimePeriod.LONG);
		}
	}


	public static enum TransformationType {
		Java, XML, JSON, Other
	}

	public static enum TypeDefinition {
		Schema, Document
	}
}
