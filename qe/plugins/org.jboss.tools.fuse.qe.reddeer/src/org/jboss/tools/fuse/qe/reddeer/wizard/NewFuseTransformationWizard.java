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
package org.jboss.tools.fuse.qe.reddeer.wizard;

import org.jboss.reddeer.common.logging.Logger;
import org.jboss.reddeer.common.matcher.RegexMatcher;
import org.jboss.reddeer.common.wait.TimePeriod;
import org.jboss.reddeer.common.wait.WaitUntil;
import org.jboss.reddeer.common.wait.WaitWhile;
import org.jboss.reddeer.core.condition.ShellWithTextIsActive;
import org.jboss.reddeer.core.condition.ShellWithTextIsAvailable;
import org.jboss.reddeer.jface.wizard.WizardDialog;
import org.jboss.reddeer.swt.condition.WidgetIsEnabled;
import org.jboss.reddeer.swt.impl.button.PushButton;
import org.jboss.reddeer.swt.impl.button.RadioButton;
import org.jboss.reddeer.swt.impl.combo.DefaultCombo;
import org.jboss.reddeer.swt.impl.shell.DefaultShell;
import org.jboss.reddeer.swt.impl.table.DefaultTable;
import org.jboss.reddeer.swt.impl.text.LabeledText;
import org.jboss.tools.fuse.qe.reddeer.condition.TableHasRow;

/**
 * Represents 'New Fuse Transformation Wizard'
 * 
 * @author tsedmik
 */
public class NewFuseTransformationWizard extends WizardDialog {

	private static final String TITLE = "New Fuse Transformation";
	private Logger log = Logger.getLogger(NewFuseTransformationWizard.class);

	public NewFuseTransformationWizard() {
		new WaitUntil(new ShellWithTextIsAvailable(TITLE));
		new DefaultShell(TITLE);
	}

	@Override
	public void next() {
		new WaitUntil(new WidgetIsEnabled(new PushButton("Next >")));
		super.next();
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
		new WaitUntil(new ShellWithTextIsAvailable("Select XSD From Project"));
		new DefaultShell("Select XSD From Project");
		DefaultTable table = new DefaultTable();
		new WaitUntil(new TableHasRow(table, new RegexMatcher(name)));
		table.select(name);
		new PushButton("OK").click();
		new WaitUntil(new ShellWithTextIsActive("New Fuse Transformation"));
	}

	public void setJSONTypeDefinition(TypeDefinition type) {
		log.debug("Set 'JSON Type Definiton' to '" + type + "'");
		new RadioButton(type.equals(TypeDefinition.Schema) ? "JSON Schema" : "JSON Instance Document").toggle(true);
	}

	public void setJSONTargetFile(String name) {
		log.debug("Set 'JSON Target File' to '" + name + "'");
		new PushButton("...").click();
		new WaitUntil(new ShellWithTextIsAvailable("Select JSON From Project"));
		new DefaultShell("Select JSON From Project");
		DefaultTable table = new DefaultTable();
		new WaitUntil(new TableHasRow(table, new RegexMatcher(name)));
		table.select(name);
		new PushButton("OK").click();
		new WaitUntil(new ShellWithTextIsActive("New Fuse Transformation"));
	}

	@Override
	public void finish() {
		super.finish();
		if (new ShellWithTextIsAvailable("New Fuse Transformation").test()) {
			new WaitWhile(new ShellWithTextIsAvailable("New Fuse Transformation"), TimePeriod.LONG);
		}
	}


	public static enum TransformationType {
		Java, XML, JSON, Other
	}

	public static enum TypeDefinition {
		Schema, Document
	}
}
