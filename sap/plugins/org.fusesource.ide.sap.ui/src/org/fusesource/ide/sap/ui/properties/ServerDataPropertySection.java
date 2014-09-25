package org.fusesource.ide.sap.ui.properties;

import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.fusesource.camel.component.sap.model.rfc.impl.ServerDataStoreEntryImpl;

@SuppressWarnings("restriction")
public abstract class ServerDataPropertySection extends BasePropertySection {

	protected ServerDataStoreEntryImpl serverDataStoreEntry;
	protected EditingDomain editingDomain;

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection)selection).getFirstElement();
		Assert.isTrue(input instanceof ServerDataStoreEntryImpl);
		serverDataStoreEntry = (ServerDataStoreEntryImpl) input;
		editingDomain = AdapterFactoryEditingDomain.getEditingDomainFor(serverDataStoreEntry);
		initDataBindings();
}

}
