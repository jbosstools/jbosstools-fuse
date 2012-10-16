package org.fusesource.ide.fabric.navigator;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.fusesource.ide.commons.ui.propsrc.PropertySourceTableView;
import org.fusesource.ide.commons.util.Function1;


public class ContainerTableView extends PropertySourceTableView {

	public ContainerTableView(String viewId) {
		super(viewId);
	}

	@Override
	protected CellLabelProvider createColumnLabelProvider(String header, Function1 function) {
		if (header.equals("Status")) {
			return new ContainerStatusLabelProvider();
		}
		return super.createColumnLabelProvider(header, function);
	}


}
