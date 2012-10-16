package org.fusesource.ide.fabric.navigator.osgi;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.fusesource.ide.commons.ui.propsrc.PropertySourceTableView;
import org.fusesource.ide.commons.util.Function1;


public class BundlesTableView extends PropertySourceTableView {
	private final BundlesNode bundlesNode;

	public BundlesTableView(String viewId, BundlesNode bundlesNode) {
		super(viewId);
		this.bundlesNode = bundlesNode;
	}


	@Override
	protected String getInitialSearchText() {
		return bundlesNode.getBundlefilterText();
	}


	@Override
	protected void setFilterText(String text) {
		super.setFilterText(text);
		bundlesNode.setBundlefilterText(text);
	}

	@Override
	protected CellLabelProvider createColumnLabelProvider(String header, Function1 function) {
		if (header.equals("State")) {
			return new BundleStatusLabelProvider();
		} else if (header.equals("Last Modified")) {
			return new LastModifiedLabelProvider();
		} else {
			return super.createColumnLabelProvider(header, function);
		}
	}


}
