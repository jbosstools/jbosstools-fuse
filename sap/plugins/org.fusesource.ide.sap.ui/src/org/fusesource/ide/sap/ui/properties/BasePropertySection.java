package org.fusesource.ide.sap.ui.properties;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;

@SuppressWarnings("restriction")
public abstract class BasePropertySection extends AbstractPropertySection {

	private DataBindingContext bindingContext;

	protected Composite createFlatFormComposite(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE) {
			// This is overridden to prevent issues with JFace bindings in property sections.
			@Override
			public boolean setFocus() {
				return true;
			}
		};
		composite.setBackground(getWidgetFactory().getColors().getBackground());
		getWidgetFactory().paintBordersFor(composite);
        FormLayout layout = new FormLayout();
        layout.marginWidth = ITabbedPropertyConstants.HSPACE + 2;
        layout.marginHeight = ITabbedPropertyConstants.VSPACE;
        layout.spacing = ITabbedPropertyConstants.VMARGIN + 1;
        composite.setLayout(layout);
        return composite;
	}

	protected DataBindingContext initDataBindings() {
		if (bindingContext != null) {
			bindingContext.dispose();
			bindingContext = null;
		}
		
		return bindingContext = new DataBindingContext();
	}
		
	@Override
	public void refresh() {
		bindingContext.updateTargets();
	}
	
}
