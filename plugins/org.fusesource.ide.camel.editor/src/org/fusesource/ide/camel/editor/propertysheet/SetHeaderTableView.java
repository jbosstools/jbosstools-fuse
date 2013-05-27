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

package org.fusesource.ide.camel.editor.propertysheet;

import java.util.List;

import org.apache.camel.model.SetHeaderDefinition;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.fusesource.ide.camel.model.LanguageExpressionBean;
import org.fusesource.ide.commons.ui.views.TableViewSupport;
import org.fusesource.ide.commons.util.Function1;


/**
 * Table of SetHeaderDefinition objects
 */
public class SetHeaderTableView extends TableViewSupport {
	public static final String ID = SetHeaderTableView.class.getName();

	private final List<SetHeaderDefinition> tableData;
	private final WritableList input;


	public SetHeaderTableView(List<SetHeaderDefinition> tableData) {
		this.tableData = tableData;
		input = new WritableList(tableData, SetHeaderDefinition.class);
		setShowSearchBox(false);
	}

	@Override
	protected String getHelpID() {
		return ID;
	}

	public WritableList getInput() {
		return input;
	}

	@Override
	protected void warnNoSite() {
		// we're not gonna be used in a Site
	}

	@Override
	public void init(IViewSite site) throws PartInitException {
		super.init(site);
		makeActions();
		aboutToBeShown();
	}

	@Override
	protected void createColumns() {
		int bounds = 100;
		int column = 0;
		clearColumns();

		Function1 function = new Function1() {
			@Override
			public Object apply(Object element) {
				SetHeaderDefinition sh = DetailsSection.toSetHeaderDefinition(element);
				if (sh != null) {
					return sh.getHeaderName();
				}
				return null;
			}
		};
		column = addColumnFunction(bounds, column, function, "Header");

		function = new Function1() {
			@Override
			public Object apply(Object element) {
				SetHeaderDefinition sh = DetailsSection.toSetHeaderDefinition(element);
				if (sh != null) {
					LanguageExpressionBean expression = LanguageExpressionBean.toLanguageExpressionBean(sh.getExpression());
					if (expression != null) {
						return expression.getExpression();
					}
				}
				return null;
			}
		};
		column = addColumnFunction(bounds, column, function, "Expression");

		function = new Function1() {
			@Override
			public Object apply(Object element) {
				SetHeaderDefinition sh = DetailsSection.toSetHeaderDefinition(element);
				if (sh != null) {
					LanguageExpressionBean expression = LanguageExpressionBean.toLanguageExpressionBean(sh.getExpression());
					if (expression != null) {
						return expression.getLanguage();
					}
				}
				return null;
			}
		};
		column = addColumnFunction(bounds, column, function, "Language");
	}


	@Override
	protected void configureViewer() {
		viewer.setInput(input);
	}

	@Override
	protected IStructuredContentProvider createContentProvider() {
		return new ObservableListContentProvider();
	}
}