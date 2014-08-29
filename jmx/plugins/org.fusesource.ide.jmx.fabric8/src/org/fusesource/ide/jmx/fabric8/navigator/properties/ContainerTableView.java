/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.jmx.fabric8.navigator.properties;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.fusesource.ide.commons.ui.propsrc.PropertySourceTableView;
import org.fusesource.ide.commons.util.Function1;

/**
 * @author lhein
 */
public class ContainerTableView extends PropertySourceTableView {
	public ContainerTableView(String viewId) {
		super(viewId);
	}

	@Override
	protected CellLabelProvider createColumnLabelProvider(String header,
			Function1 function) {
		if (header.equals("Status")) {
			return new ContainerStatusLabelProvider();
		}
		return super.createColumnLabelProvider(header, function);
	}
}