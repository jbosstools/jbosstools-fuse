/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.reddeer.wizard;

import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.jface.wizard.WizardDialog;
import org.eclipse.reddeer.swt.condition.ShellIsAvailable;
import org.eclipse.reddeer.swt.impl.combo.LabeledCombo;
import java.util.List;
import org.eclipse.reddeer.swt.impl.text.LabeledText;
import org.eclipse.reddeer.swt.impl.shell.DefaultShell;

/**
 * This class is intended for manipulations with 'Add REST Operation' wizard.
 * 
 * @author djelinek
 *
 */
public class AddRESTOperationWizard extends WizardDialog {

	public static final String TITLE = "Add REST Operation";
	public static final String OPERATION_TYPE = "Operation Type*";
	public static final String REFERENCED_ROUTE_ID = "Referenced Route ID";
	public static final String URI = "URI*";
	public static final String ID = "ID*";

	// Operation type combo-box values
	public static final String OPERATION_TYPE_CONNECT = "connect";
	public static final String OPERATION_TYPE_DELETE = "delete";
	public static final String OPERATION_TYPE_GET = "get";
	public static final String OPERATION_TYPE_HEAD = "head";
	public static final String OPERATION_TYPE_OPTIONS = "options";
	public static final String OPERATION_TYPE_PATCH = "patch";
	public static final String OPERATION_TYPE_POST = "post";
	public static final String OPERATION_TYPE_PUT = "put";
	public static final String OPERATION_TYPE_TRACE = "trace";

	public AddRESTOperationWizard() {
		super(TITLE);
		new WaitUntil(new ShellIsAvailable(TITLE), TimePeriod.DEFAULT);
	}

	public DefaultShell getShellAddRESTOperation() {
		return new DefaultShell(TITLE);
	}

	public String getTextReferencedRouteID() {
		return new LabeledCombo(this, REFERENCED_ROUTE_ID).getText();
	}

	public String getSelectionReferencedRouteID() {
		return new LabeledCombo(this, REFERENCED_ROUTE_ID).getSelection();
	}

	public List<String> getItemsReferencedRouteID() {
		return new LabeledCombo(this, REFERENCED_ROUTE_ID).getItems();
	}

	public String getTextOperationType() {
		return new LabeledCombo(this, OPERATION_TYPE).getText();
	}

	public String getSelectionOperationType() {
		return new LabeledCombo(this, OPERATION_TYPE).getSelection();
	}

	public List<String> getItemsOperationType() {
		return new LabeledCombo(this, OPERATION_TYPE).getItems();
	}

	public String getTextURI() {
		return new LabeledText(this, URI).getText();
	}

	public String getTextID() {
		return new LabeledText(this, ID).getText();
	}

	public void setSelectionReferencedRouteID(String str) {
		new LabeledCombo(this, REFERENCED_ROUTE_ID).setSelection(str);
	}

	public void setSelectionOperationType(String str) {
		new LabeledCombo(this, OPERATION_TYPE).setSelection(str);
	}

	public void setTextURI(String str) {
		new LabeledText(this, URI).setText(str);
	}

	public void setTextID(String str) {
		new LabeledText(this, ID).setText(str);
	}

	protected LabeledText getIDTXT() {
		return new LabeledText(this, ID);
	}

	protected LabeledText getURITXT() {
		return new LabeledText(this, URI);
	}

	protected LabeledCombo getOperationTypeCMB() {
		return new LabeledCombo(this, OPERATION_TYPE);
	}

	protected LabeledCombo getReferencedRouteIDCMB() {
		return new LabeledCombo(this, REFERENCED_ROUTE_ID);
	}

}