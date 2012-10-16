package org.fusesource.ide.fabric.camel.navigator;

import java.util.List;

import org.eclipse.ui.part.IPage;
import org.eclipse.ui.views.properties.IPropertySource;
import org.fusesource.fabric.camel.facade.mbean.CamelProcessorMBean;
import org.fusesource.ide.commons.ui.propsrc.PropertySourceTableView;
import org.fusesource.ide.commons.ui.views.PageTabDescriptor;


public class ProcessorsPageTabDescriptor extends PageTabDescriptor {
	private final List<IPropertySource> propertySourceList;

	public ProcessorsPageTabDescriptor(String label, List<IPropertySource> propertySourceList) {
		super(label);
		this.propertySourceList = propertySourceList;
	}

	@Override
	protected IPage createPage() {
		PropertySourceTableView processorsTableView = new PropertySourceTableView(CamelProcessorMBean.class.getName());
		processorsTableView.setPropertySources(propertySourceList);
		return processorsTableView;
	}
}