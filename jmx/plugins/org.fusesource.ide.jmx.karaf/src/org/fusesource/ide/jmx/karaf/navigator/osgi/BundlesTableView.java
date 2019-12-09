/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.jmx.karaf.navigator.osgi;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.fusesource.ide.foundation.core.functions.Function1;
import org.fusesource.ide.foundation.ui.propsrc.PropertySourceTableView;


public class BundlesTableView extends PropertySourceTableView {
	private BundlesNode bundlesNode;

	public BundlesTableView(String viewId, BundlesNode bundlesNode) {
		super(viewId);
		this.bundlesNode = bundlesNode;
	}


	@Override
	protected String getInitialSearchText() {
		if (bundlesNode == null)
			return "";
		return bundlesNode.getBundlefilterText();
	}


	@Override
	protected void setFilterText(String text) {
		super.setFilterText(text);
		if (bundlesNode == null)
			bundlesNode = ((BundlesTabSection)this).getCurrent();
		bundlesNode.setBundleFilterText(text);
	}

	@Override
	protected CellLabelProvider createColumnLabelProvider(String header, Function1<?,?> function) {
		if (header.equals("State")) {
			return new BundleStatusLabelProvider();
		} else if (header.equals("Last Modified")) {
			return new LastModifiedLabelProvider();
		} else {
			return super.createColumnLabelProvider(header, function);
		}
	}


}
