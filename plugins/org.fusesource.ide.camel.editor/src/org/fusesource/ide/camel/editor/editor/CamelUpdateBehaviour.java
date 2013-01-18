package org.fusesource.ide.camel.editor.editor;

import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.ui.editor.DefaultUpdateBehavior;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.graphiti.ui.services.GraphitiUi;

/**
 * @author lhein
 */
public class CamelUpdateBehaviour extends DefaultUpdateBehavior {

	private TransactionalEditingDomain editingDomain;
	
	/**
	 * @param diagramEditor
	 */
	public CamelUpdateBehaviour(DiagramEditor diagramEditor) {
		super(diagramEditor);
		createEditingDomain();
	}

//	/* (non-Javadoc)
//	 * @see org.eclipse.graphiti.ui.editor.DefaultUpdateBehavior#createEditingDomain()
//	 */
//	@Override
//	protected void createEditingDomain() {
//		this.editingDomain = GraphitiUi.getEmfService().createResourceSetAndEditingDomain(); 
//		initializeEditingDomain(this.editingDomain);
//	}
//	
//	/* (non-Javadoc)
//	 * @see org.eclipse.graphiti.ui.editor.DefaultUpdateBehavior#getEditingDomain()
//	 */
//	@Override
//	public synchronized TransactionalEditingDomain getEditingDomain() {
//		if (this.editingDomain == null) {
//			createEditingDomain();
//		}
//		return this.editingDomain;
//	}
//
//	/* (non-Javadoc)
//	 * @see org.eclipse.graphiti.ui.editor.DefaultUpdateBehavior#disposeEditingDomain()
//	 */
//	@Override
//	protected void disposeEditingDomain() {
//		this.editingDomain.dispose();
//	}
}
