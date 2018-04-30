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
package org.jboss.tools.fuse.reddeer.editor;

import org.eclipse.reddeer.eclipse.jdt.ui.wizards.NewClassCreationWizard;
import org.eclipse.reddeer.jface.wizard.WizardDialog;
import org.eclipse.reddeer.swt.impl.button.PushButton;
import org.eclipse.reddeer.swt.impl.combo.LabeledCombo;
import org.eclipse.reddeer.swt.impl.group.DefaultGroup;
import org.eclipse.reddeer.swt.impl.shell.DefaultShell;
import org.eclipse.reddeer.swt.impl.text.LabeledText;

/**
 * 
 * @author apodhrad
 *
 */
public class AddBeanWizard extends WizardDialog {

	public static final String TITLE = "Add Bean";

	public AddBeanWizard() {
		super(TITLE);
	}

	protected AddBeanWizard(String title) {
		super(title);
	}

	public void activate() {
		setShell(new DefaultShell(TITLE));
	}

	public void setId(String id) {
		new LabeledText(this, "Id").setText(id);
	}

	public String getId() {
		return new LabeledText(this, "Id").getText();
	}

	public void setClazz(String id) {
		new LabeledText(this, "Class").setText(id);
	}

	public String getClazz() {
		return new LabeledText(this, "Class").getText();
	}

	public FilteredSelectionDialog browseClass() {
		new PushButton(this, "...").click();
		return new FilteredSelectionDialog();
	}

	public NewClassCreationWizard addClass() {
		new PushButton(this, "+").click();
		NewClassCreationWizard wizard = new NewClassCreationWizard();
		wizard.isOpen();
		wizard.setShell(new DefaultShell("New Java Class"));
		return wizard;
	}

	public void selectFactoryBean(String beanName) {
		new LabeledCombo(this, "Factory Bean").setSelection(beanName);
	}

	public BeanArgumentDialog addArgument() {
		new PushButton(new DefaultGroup(this, "Constructor Arguments"), "Add").click();
		return new BeanArgumentDialog();
	}

	public BeanPropertyDialog addProperty() {
		new PushButton(new DefaultGroup(this, "Bean Properties"), "Add").click();
		return new BeanPropertyDialog();
	}
}
