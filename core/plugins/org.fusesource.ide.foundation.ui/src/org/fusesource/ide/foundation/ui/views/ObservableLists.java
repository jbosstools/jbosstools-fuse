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

package org.fusesource.ide.foundation.ui.views;

import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.list.IObservableList;

public class ObservableLists {

	public static void addListener(Object input, IChangeListener changeListener) {
		IObservableList observable = toObservableList(input);
		if (observable != null) {
			observable.addChangeListener(changeListener);
		}
	}

	public static void removeListener(Object input, IChangeListener changeListener) {
		IObservableList observable = toObservableList(input);
		if (observable != null) {
			observable.removeChangeListener(changeListener);
		}
	}

	public static IObservableList toObservableList(Object input) {
		IObservableList observable = null;
		if (input instanceof IObservableList) {
			observable = (IObservableList) input;
		}
		return observable;
	}

}
