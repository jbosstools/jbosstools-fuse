package org.fusesource.ide.fabric.actions.jclouds;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.fusesource.ide.fabric.actions.Messages;


public class CloudDetailsWizardPage extends WizardPage {

	private CloudDetailsTable table = new CloudDetailsTable();

	public CloudDetailsWizardPage() {
		super(Messages.jclouds_chooseCloudTitle);
		setDescription(Messages.jclouds_chooseCloudDescription);
	}

	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FillLayout());

		table.createPartControl(composite);

		setControl(composite);

		table.getViewer().addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				selectionUpdated();
			}
		});

	}

	@Override
	public boolean isPageComplete() {
		return getSelectedCloud() != null;
	}

	public CloudDetails getSelectedCloud() {
		return table.getSelectedCloud();
	}


	protected void selectionUpdated() {
		boolean selected = getSelectedCloud() != null;
		setPageComplete(selected);
	}


}
