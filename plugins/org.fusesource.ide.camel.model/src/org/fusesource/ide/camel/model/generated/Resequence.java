
/**
 * NOTE - this file is auto-generated using Scalate. 
 * 
 * DO NOT EDIT!
 */
package org.fusesource.ide.camel.model.generated;

import java.util.Map;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.ResequenceDefinition;
import org.apache.camel.model.config.ResequencerConfig;
import org.apache.camel.model.language.ExpressionDefinition;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.ExpressionPropertyDescriptor;
import org.fusesource.ide.camel.model.RouteContainer;
import org.fusesource.ide.camel.model.util.Objects;
import org.fusesource.ide.commons.properties.BooleanPropertyDescriptor;
import org.fusesource.ide.commons.properties.ComplexPropertyDescriptor;
import org.fusesource.ide.commons.properties.ComplexUnionPropertyDescriptor;
import org.fusesource.ide.commons.properties.EnumPropertyDescriptor;
import org.fusesource.ide.commons.properties.ListPropertyDescriptor;
import org.fusesource.ide.commons.properties.UnionTypeValue;

/**
 * The Node class for Camel's ResequenceDefinition
 */
public class Resequence extends AbstractNode {

	public static final String PROPERTY_EXPRESSION = "Resequence.Expression";
	public static final String PROPERTY_RESEQUENCERCONFIG = "Resequence.ResequencerConfig";
	
	private ExpressionDefinition expression;
	private ResequencerConfig resequencerConfig;
	
    public Resequence() {
    }		
	
    public Resequence(ResequenceDefinition definition, RouteContainer parent) {

      super(parent);
    	loadPropertiesFromCamelDefinition(definition);
    	loadChildrenFromCamelDefinition(definition);
    }


    /* (non-Javadoc)
     * @see org.fusesource.ide.camel.model.AbstractNode#getIconName()
     */
    @Override
    public String getIconName() {
    	return "resequence.png";
    }
    
  	@Override
  	public String getDocumentationFileName() {
  		return "resequenceEIP";
  	}
  	
  	@Override
  	public String getCategoryName() {
  		return "Routing";
  	}


	

	/**
	 * @return the expression
	 */
	public ExpressionDefinition getExpression() {
		return this.expression;
	}
	
	/**
	 * @param expression the expression to set
	 */
	public void setExpression(ExpressionDefinition expression) {
		ExpressionDefinition oldValue = this.expression;
		this.expression = expression;
		firePropertyChange(PROPERTY_EXPRESSION, oldValue, expression);
	}

	/**
	 * @return the resequencerConfig
	 */
	public ResequencerConfig getResequencerConfig() {
		return this.resequencerConfig;
	}
	
	/**
	 * @param resequencerConfig the resequencerConfig to set
	 */
	public void setResequencerConfig(ResequencerConfig resequencerConfig) {
		ResequencerConfig oldValue = this.resequencerConfig;
		this.resequencerConfig = resequencerConfig;
		firePropertyChange(PROPERTY_RESEQUENCERCONFIG, oldValue, resequencerConfig);
	}


	
	/*
	 * (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.AbstractNode#addCustomProperties(java.util.Map)
	 */
	@Override
	protected void addCustomProperties(Map<String, PropertyDescriptor> descriptors) {
		super.addCustomProperties(descriptors);
		
  
  	PropertyDescriptor descExpression = new ExpressionPropertyDescriptor(PROPERTY_EXPRESSION, Messages.propertyLabelResequenceExpression);
    
      
		PropertyDescriptor descResequencerConfig = new ComplexUnionPropertyDescriptor(PROPERTY_RESEQUENCERCONFIG, Messages.propertyLabelResequenceResequencerConfig, ResequencerConfig.class, new UnionTypeValue[]{
		        new UnionTypeValue("batch-config", org.apache.camel.model.config.BatchResequencerConfig.class),
		        new UnionTypeValue("stream-config", org.apache.camel.model.config.StreamResequencerConfig.class),
		  		});
  	  		descriptors.put(PROPERTY_EXPRESSION, descExpression);
		descriptors.put(PROPERTY_RESEQUENCERCONFIG, descResequencerConfig);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setPropertyValue(Object id, Object value) {
		if (PROPERTY_EXPRESSION.equals(id)) {
			setExpression(Objects.convertTo(value, ExpressionDefinition.class));
		}		else if (PROPERTY_RESEQUENCERCONFIG.equals(id)) {
			setResequencerConfig(Objects.convertTo(value, ResequencerConfig.class));
		}    else {
			super.setPropertyValue(id, value);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.AbstractNode#getPropertyValue(java.lang.Object)
	 */
	@Override
	public Object getPropertyValue(Object id) {
		if (PROPERTY_EXPRESSION.equals(id)) {
			return this.getExpression();
		}		else if (PROPERTY_RESEQUENCERCONFIG.equals(id)) {
			return this.getResequencerConfig();
		}    else {
			return super.getPropertyValue(id);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public ProcessorDefinition createCamelDefinition() {
		ResequenceDefinition answer = new ResequenceDefinition();
    answer.setExpression(toXmlPropertyValue(PROPERTY_EXPRESSION, this.getExpression()));
    answer.setResequencerConfig(toXmlPropertyValue(PROPERTY_RESEQUENCERCONFIG, this.getResequencerConfig()));
        super.savePropertiesToCamelDefinition(answer);
		return answer;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class<?> getCamelDefinitionClass() {
	  return ResequenceDefinition.class;
  }

	@SuppressWarnings("rawtypes")
	@Override
	protected void loadPropertiesFromCamelDefinition(ProcessorDefinition processor) {
    super.loadPropertiesFromCamelDefinition(processor);
    
    if (processor instanceof ResequenceDefinition) {
      ResequenceDefinition node = (ResequenceDefinition) processor;
      this.setExpression(node.getExpression());
      this.setResequencerConfig(node.getResequencerConfig());
    } else {
      throw new IllegalArgumentException("ProcessorDefinition not an instanceof ResequenceDefinition. Was " + processor.getClass().getName());
    }
	}
}
 
      
