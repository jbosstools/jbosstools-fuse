package org.fusesource.ide.commons.jobs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;

public abstract class LoadListJobSupport extends Job {
	private final WritableList writableList;

	public LoadListJobSupport(String name, WritableList writableList) {
		super(name);
		this.writableList = writableList;
	}

	protected abstract List<?> loadList();

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		System.out.println("Starting load of list of type: " + writableList.getElementType());
		// lets use a copy to make sure we're not going to be concurrently changing it later on
		final List<?> value = new ArrayList(loadList());

		// now lets do this asynchronously...
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				writableList.clear();
				writableList.addAll(value);

				System.out.println("Starting added " + value.size() + " element(s) of type: " + writableList.getElementType());
			}
		});
		return Status.OK_STATUS;
	}

}
