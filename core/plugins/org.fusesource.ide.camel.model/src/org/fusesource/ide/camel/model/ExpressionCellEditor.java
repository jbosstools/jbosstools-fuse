/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.camel.model;

import org.apache.camel.model.language.ExpressionDefinition;
import org.apache.camel.model.language.LanguageExpression;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.fusesource.ide.foundation.core.util.Strings;

/**
 * @author jstrachan
 */
public class ExpressionCellEditor extends CellEditor {

	private Label labelExpression;
	private Button btnOpenExpressionDialog;
	
	private FileDialog expressionDialog;
	private String expression;
	private String language;
	
	private LanguageExpression value = new LanguageExpression();

	public static final String[] LANGUAGES = {
		"el",  "groovy", "js", "ruby", "simple", "xpath", "xquery"
	};
	                
	/**
	 * creates the cell editor
	 * 
	 * @param expressionPropertyDescriptor
	 * @param parent
	 */
	public ExpressionCellEditor(ExpressionPropertyDescriptor expressionPropertyDescriptor, Composite parent) {
		super(parent);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.CellEditor#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createControl(Composite parent) {
		
		RowLayout layout = new RowLayout(SWT.HORIZONTAL);
		layout.spacing = 10;
		layout.pack = false;
		layout.center = true;
		layout.justify = true;
		layout.fill = true;

		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(layout);

		labelExpression = new Label(composite, SWT.NULL);
		labelExpression.setText("");

		this.btnOpenExpressionDialog = new Button(composite, SWT.PUSH);
		this.btnOpenExpressionDialog.setText("...");

		this.btnOpenExpressionDialog.addSelectionListener(new SelectionListener() {
			
			/*
			 * (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				expressionDialog = new FileDialog(Display.getDefault().getActiveShell(), SWT.OPEN);
				expression = expressionDialog.open();
			}
			
			/*
			 * (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		return composite;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.CellEditor#doGetValue()
	 */
	@Override
	protected Object doGetValue() {
		value.setExpression(this.expression);
		value.setLanguage(this.language);
		return value;
	}

	/**
	 * 
	 * @param value
	 * @return
	 */
	protected String toString(Object value) {
		if (value != null) {
			return value.toString();
		}
		return "";
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.CellEditor#doSetFocus()
	 */
	@Override
	protected void doSetFocus() {
		this.btnOpenExpressionDialog.setFocus();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.CellEditor#doSetValue(java.lang.Object)
	 */
	@Override
	protected void doSetValue(Object value) {
		if (value instanceof ExpressionDefinition) {
			ExpressionDefinition expr = (ExpressionDefinition) value;
			this.value = new LanguageExpression(expr.getLanguage(), expr.getExpression());
			
//			textExpression.setText(getOrElse(this.value.getExpression()));
			String language = Strings.getOrElse(this.value.getLanguage());
//			comboLanguage.setText(language);
		} else {
			Activator.getLogger().warning("Bad value ignored: " + value);
		}
	}
}
