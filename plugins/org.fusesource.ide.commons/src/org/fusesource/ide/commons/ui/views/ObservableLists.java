package org.fusesource.ide.commons.ui.views;

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
