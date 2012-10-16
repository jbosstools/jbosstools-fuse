package org.fusesource.ide.camel.model;

import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.ui.views.properties.PropertyDescriptor;


/**
 * @author jstrachan
 */
public class ExpressionPropertyDescriptor extends PropertyDescriptor {

	private ExpressionLabelProvider labelProvider;
	
	/**
	 * creates a property descriptor for expression properties
	 * 
	 * @param id	the id
	 * @param displayName	the display name
	 */
	public ExpressionPropertyDescriptor(Object id, String displayName) {
		super(id, displayName);
		this.labelProvider = new ExpressionLabelProvider();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.PropertyDescriptor#createPropertyEditor(org.eclipse.swt.widgets.Composite)
	 */
	/*
	@Override
	public CellEditor createPropertyEditor(Composite parent) {
		return new ExpressionCellEditor(this, parent);
	}
	*/

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.PropertyDescriptor#getLabelProvider()
	 */
	@Override
	public ILabelProvider getLabelProvider() {
		return this.labelProvider;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.PropertyDescriptor#getValidator()
	 */
	@Override
	protected ICellEditorValidator getValidator() {
		return super.getValidator();
	}
}
