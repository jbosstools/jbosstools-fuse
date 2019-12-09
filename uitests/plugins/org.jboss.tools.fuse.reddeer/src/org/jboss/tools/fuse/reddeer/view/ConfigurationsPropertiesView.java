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
package org.jboss.tools.fuse.reddeer.view;

import org.eclipse.reddeer.common.matcher.RegexMatcher;
import org.eclipse.reddeer.core.matcher.WithLabelMatcher;
import org.eclipse.reddeer.core.matcher.WithMnemonicTextMatcher;
import org.eclipse.reddeer.eclipse.jdt.ui.wizards.NewClassCreationWizard;
import org.eclipse.reddeer.eclipse.ui.views.properties.PropertySheet;
import org.eclipse.reddeer.swt.impl.button.PushButton;
import org.eclipse.reddeer.swt.impl.button.RadioButton;
import org.eclipse.reddeer.swt.impl.combo.DefaultCombo;
import org.eclipse.reddeer.swt.impl.combo.LabeledCombo;
import org.eclipse.reddeer.swt.impl.group.DefaultGroup;
import org.eclipse.reddeer.swt.impl.shell.DefaultShell;
import org.eclipse.reddeer.swt.impl.text.LabeledText;
import org.jboss.tools.fuse.reddeer.editor.FilteredSelectionDialog;
import org.jboss.tools.fuse.reddeer.editor.MethodSelectionDialog;

public class ConfigurationsPropertiesView extends PropertySheet {

	public static final String BEAN_SECTION_LABEL = "Bean Class or Global Bean Reference";
	public static final String SCOPE_PROTOTYPE = "prototype";
	public static final String SCOPE_SINGLETON = "singleton";

	public ConfigurationsPropertiesView() {
		super();
	}

	public String getId() {
		return new LabeledText(this, "Id").getText();
	}

	public void setId(String id) {
		new LabeledText(this, "Id").setText(id);
	}

	public void toggleBeanReference() {
		new RadioButton(new DefaultGroup(this, BEAN_SECTION_LABEL), "Bean Reference").toggle(true);
	}

	public void toggleBeanClass() {
		new RadioButton(new DefaultGroup(this, BEAN_SECTION_LABEL), "Bean Class").toggle(true);
	}

	public FilteredSelectionDialog browseClass() {
		toggleBeanClass();
		new PushButton(new DefaultGroup(this, BEAN_SECTION_LABEL), "...").click();
		return new FilteredSelectionDialog();
	}

	public NewClassCreationWizard addClass() {
		toggleBeanClass();
		new PushButton(new DefaultGroup(this, BEAN_SECTION_LABEL), "+").click();
		NewClassCreationWizard wizard = new NewClassCreationWizard();
		wizard.isOpen();
		wizard.setShell(new DefaultShell("New Java Class"));
		return wizard;
	}

	public void selectBeanReference(String beanName) {
		toggleBeanReference();
		new DefaultCombo(this, 0, new WithLabelMatcher(new RegexMatcher("Factory-.*"))).setSelection(beanName);
	}

	public MethodSelectionDialog browseFactoryMethod() {
		new PushButton(this, 1, new WithMnemonicTextMatcher("...")).click();
		return new MethodSelectionDialog();
	}

	public MethodSelectionDialog browseInitMethod() {
		new PushButton(this, 2, new WithMnemonicTextMatcher("...")).click();
		return new MethodSelectionDialog();
	}

	public MethodSelectionDialog browseDestroyMethod() {
		new PushButton(this, 3, new WithMnemonicTextMatcher("...")).click();
		return new MethodSelectionDialog();
	}

	public void selectScope(String scope) {
		new LabeledCombo(this, "Scope").setSelection(scope);
	}

	public void selectScopePrototype() {
		selectScope(SCOPE_PROTOTYPE);
	}

	public void selectScopeSingleton() {
		selectScope(SCOPE_SINGLETON);
	}
}
