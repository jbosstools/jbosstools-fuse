package org.fusesource.ide.camel.editor.features.create;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.generated.Bean;


public class CreateBeanFigureFeature extends CreateFigureFeature<Bean> {
	private final Bean bean;

	public CreateBeanFigureFeature(IFeatureProvider fp, String name, String description, Bean endpoint) {
		super(fp, name, description, Bean.class);
		this.bean = endpoint;
	}

	@Override
	protected AbstractNode createNode() {
		Bean answer = new Bean();
		answer.setRef(bean.getRef());
		answer.setBeanType(bean.getBeanType());
		answer.setName(bean.getName());
		return answer;
	}


}
