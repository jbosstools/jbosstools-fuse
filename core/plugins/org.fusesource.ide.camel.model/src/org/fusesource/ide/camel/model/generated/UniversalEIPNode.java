package org.fusesource.ide.camel.model.generated;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.camel.ExchangePattern;
import org.apache.camel.LoggingLevel;
import org.apache.camel.ShutdownRoute;
import org.apache.camel.ShutdownRunningTask;
import org.apache.camel.ThreadPoolRejectedPolicy;
import org.apache.camel.model.DataFormatDefinition;
import org.apache.camel.model.LoadBalancerDefinition;
import org.apache.camel.model.OnCompletionMode;
import org.apache.camel.model.OptimisticLockRetryPolicyDefinition;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.RedeliveryPolicyDefinition;
import org.apache.camel.model.config.ResequencerConfig;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.Activator;
import org.fusesource.ide.camel.model.ExpressionPropertyDescriptor;
import org.fusesource.ide.camel.model.RouteContainer;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.util.Objects;
import org.fusesource.ide.commons.properties.BooleanPropertyDescriptor;
import org.fusesource.ide.commons.properties.ComplexUnionPropertyDescriptor;
import org.fusesource.ide.commons.properties.EnumPropertyDescriptor;
import org.fusesource.ide.commons.properties.ListPropertyDescriptor;
import org.fusesource.ide.commons.properties.UnionTypeValue;

public class UniversalEIPNode extends AbstractNode {
	
	
	private Eip eip;
	private HashMap<String, Object> propertyValues;
	private ProcessorDefinition definition;
	
    public UniversalEIPNode(Eip eip) {
    	super(null, true);
    	this.eip = eip;
    	propertyValues = new HashMap<String, Object>();
    }
    
    public UniversalEIPNode(ProcessorDefinition definition, RouteContainer parent, Eip eip) {
        super(true);
        this.definition = definition;
        this.eip = eip;
        addCustomProperties();
        propertyValues = new HashMap<String, Object>();
        loadPropertiesFromCamelDefinition(definition);
        loadChildrenFromCamelDefinition(definition);
    }


    @Override
    public String getIconName() {
    	return UniversalEIPUtility.getIconName(eip.getName());
    }

	/**
	 * Return the typeid of this node, if applicable. 
	 * This should match the parameter name from the eip.xml model, so for example, 
	 * doTry, resequence, etc
	 * @return
	 */
	public String getNodeTypeId() {
		return eip.getName();
	}
    
    @Override
    public String getDocumentationFileName() {
    	return UniversalEIPUtility.getDocumentationFileName(eip.getName());
    }

    @Override
    public String getCategoryName() {
    	return UniversalEIPUtility.getCategoryName(eip.getName());
    }

    
    
    /*
     * This method necessarily actually resolves classes in camel. 
     * If this is ever abstracted out, so that model.core or commons doesn't depend on one version
     * of camel, this will need to be isolated
     */
    protected PropertyDescriptor createPropertyDescriptor(String javaType, String propertyKey, String display) {
    	int parametizedStart = javaType.indexOf("<");
    	String cleanType = (parametizedStart == -1 ? javaType : javaType.substring(0, parametizedStart));

    	
        if( cleanType.equals("java.lang.Boolean")) {
        	return new BooleanPropertyDescriptor(propertyKey, display);
        }
        if( cleanType.equals("java.util.List")) {
        	return new ListPropertyDescriptor(propertyKey, display);
        }
        if( cleanType.equals("org.apache.camel.model.language.ExpressionDefinition")) {
            return new ExpressionPropertyDescriptor(propertyKey, display);
        }
        if( cleanType.equals("org.apache.camel.model.OptimisticLockRetryPolicyDefinition")) {
        	return new ComplexUnionPropertyDescriptor(propertyKey, display, OptimisticLockRetryPolicyDefinition.class, 
        			new UnionTypeValue[] {});
        }
        if( cleanType.equals("org.apache.camel.model.RedeliveryPolicyDefinition")) {
        	return new ComplexUnionPropertyDescriptor(propertyKey, display,
        			RedeliveryPolicyDefinition.class, new UnionTypeValue[] {});
        }
        if( cleanType.equals("org.apache.camel.ExchangePattern")) {
        	return new EnumPropertyDescriptor(propertyKey, display,ExchangePattern.class);
        }
        
        if( cleanType.equals("java.util.concurrent.TimeUnit")) {
        	return new EnumPropertyDescriptor(propertyKey, display,TimeUnit.class);
        }
        if( cleanType.equals("org.apache.camel.ThreadPoolRejectedPolicy")) {
        	return new EnumPropertyDescriptor(propertyKey, display, ThreadPoolRejectedPolicy.class);
        }
        if( cleanType.equals("org.apache.camel.LoggingLevel")) {
        	return new EnumPropertyDescriptor(propertyKey,display, LoggingLevel.class);
        }
        if( cleanType.equals("org.apache.camel.model.OnCompletionMode")) {
        	return new EnumPropertyDescriptor(propertyKey, display,  OnCompletionMode.class);
        }
        if( cleanType.equals("org.apache.camel.model.WhenDefinition")) {
        	return null;  // TODO?  Not in current file, seems left out
        }
        if( cleanType.equals("org.apache.camel.ShutdownRoute")) {
        	return new EnumPropertyDescriptor(propertyKey, display, ShutdownRoute.class);
        }
        if( cleanType.equals("org.apache.camel.ShutdownRunningTask")) {
        	return new EnumPropertyDescriptor(propertyKey, display, ShutdownRunningTask.class);
        }
        if( cleanType.equals("org.apache.camel.model.DataFormatDefinition")) {
            PropertyDescriptor descDataFormatType = new ComplexUnionPropertyDescriptor(propertyKey, display, DataFormatDefinition.class, new UnionTypeValue[] {
                    new UnionTypeValue("avro", org.apache.camel.model.dataformat.AvroDataFormat.class),
                    new UnionTypeValue("base64", org.apache.camel.model.dataformat.Base64DataFormat.class),
                    new UnionTypeValue("beanio", org.apache.camel.model.dataformat.BeanioDataFormat.class),
                    new UnionTypeValue("bindy", org.apache.camel.model.dataformat.BindyDataFormat.class),
                    new UnionTypeValue("castor", org.apache.camel.model.dataformat.CastorDataFormat.class),
                    new UnionTypeValue("crypto", org.apache.camel.model.dataformat.CryptoDataFormat.class),
                    new UnionTypeValue("csv", org.apache.camel.model.dataformat.CsvDataFormat.class),
                    new UnionTypeValue("custom", org.apache.camel.model.dataformat.CustomDataFormat.class),
                    new UnionTypeValue("flatpack", org.apache.camel.model.dataformat.FlatpackDataFormat.class),
                    new UnionTypeValue("gzip", org.apache.camel.model.dataformat.GzipDataFormat.class),
                    new UnionTypeValue("hl7", org.apache.camel.model.dataformat.HL7DataFormat.class),
                    new UnionTypeValue("ical", org.apache.camel.model.dataformat.IcalDataFormat.class),
                    new UnionTypeValue("jaxb", org.apache.camel.model.dataformat.JaxbDataFormat.class),
                    new UnionTypeValue("jibx", org.apache.camel.model.dataformat.JibxDataFormat.class),
                    new UnionTypeValue("json", org.apache.camel.model.dataformat.JsonDataFormat.class),
                    new UnionTypeValue("protobuf", org.apache.camel.model.dataformat.ProtobufDataFormat.class),
                    new UnionTypeValue("rss", org.apache.camel.model.dataformat.RssDataFormat.class),
                    new UnionTypeValue("secureXML", org.apache.camel.model.dataformat.XMLSecurityDataFormat.class),
                    new UnionTypeValue("serialization", org.apache.camel.model.dataformat.SerializationDataFormat.class),
                    new UnionTypeValue("soapjaxb", org.apache.camel.model.dataformat.SoapJaxbDataFormat.class),
                    new UnionTypeValue("string", org.apache.camel.model.dataformat.StringDataFormat.class),
                    new UnionTypeValue("syslog", org.apache.camel.model.dataformat.SyslogDataFormat.class),
                    new UnionTypeValue("tidyMarkup", org.apache.camel.model.dataformat.TidyMarkupDataFormat.class),
                    new UnionTypeValue("univocity-csv", org.apache.camel.model.dataformat.UniVocityCsvDataFormat.class),
                    new UnionTypeValue("univocity-fixed", org.apache.camel.model.dataformat.UniVocityFixedWidthDataFormat.class),
                    new UnionTypeValue("univocity-tsv", org.apache.camel.model.dataformat.UniVocityTsvDataFormat.class),
                    new UnionTypeValue("xmlBeans", org.apache.camel.model.dataformat.XMLBeansDataFormat.class),
                    new UnionTypeValue("xmljson", org.apache.camel.model.dataformat.XmlJsonDataFormat.class),
                    new UnionTypeValue("xmlrpc", org.apache.camel.model.dataformat.XmlRpcDataFormat.class),
                    new UnionTypeValue("xstream", org.apache.camel.model.dataformat.XStreamDataFormat.class),
                    new UnionTypeValue("pgp", org.apache.camel.model.dataformat.PGPDataFormat.class),
                    new UnionTypeValue("zip", org.apache.camel.model.dataformat.ZipDataFormat.class),
                    new UnionTypeValue("zipFile", org.apache.camel.model.dataformat.ZipFileDataFormat.class),
            });
        }
        if( cleanType.equals("org.apache.camel.model.LoadBalancerDefinition")) {
        	return new ComplexUnionPropertyDescriptor(propertyKey, display, LoadBalancerDefinition.class, new UnionTypeValue[] {
                    new UnionTypeValue("failover", org.apache.camel.model.loadbalancer.FailoverLoadBalancerDefinition.class),
                    new UnionTypeValue("random", org.apache.camel.model.loadbalancer.RandomLoadBalancerDefinition.class),
                    new UnionTypeValue("custom", org.apache.camel.model.loadbalancer.CustomLoadBalancerDefinition.class),
                    new UnionTypeValue("roundRobin", org.apache.camel.model.loadbalancer.RoundRobinLoadBalancerDefinition.class),
                    new UnionTypeValue("sticky", org.apache.camel.model.loadbalancer.StickyLoadBalancerDefinition.class),
                    new UnionTypeValue("topic", org.apache.camel.model.loadbalancer.TopicLoadBalancerDefinition.class),
                    new UnionTypeValue("weighted", org.apache.camel.model.loadbalancer.WeightedLoadBalancerDefinition.class),
                    new UnionTypeValue("circuitBreaker", org.apache.camel.model.loadbalancer.CircuitBreakerLoadBalancerDefinition.class),
            });
        }
        if( cleanType.equals("org.apache.camel.model.config.ResequencerConfig")) {
            return new ComplexUnionPropertyDescriptor(propertyKey, display, ResequencerConfig.class, new UnionTypeValue[] {
                    new UnionTypeValue("batch-config", org.apache.camel.model.config.BatchResequencerConfig.class),
                    new UnionTypeValue("stream-config", org.apache.camel.model.config.StreamResequencerConfig.class),
            });
        }
        // TODO add more here as I discover them
        
        // by default just return a text descriptor
        return new TextPropertyDescriptor(propertyKey, display);
    }
    
    protected Class findClass(String str) {
    	//  TODO currently errors on generics...   java.util.List<org.apache.camel.model.WhenDefinition>  fails
    	int parametizedStart = str.indexOf("<");
    	str = (parametizedStart == -1 ? str : str.substring(0, parametizedStart));
    	
    	try {
    		return Class.forName(str);
    	} catch(ClassNotFoundException cnfe) {
    		Activator.getDefault().getLog().log(new Status(IStatus.ERROR, "org.fusesource.ide.camel.model", cnfe.getMessage(), cnfe));
    	}
    	return null;
    }
    
    @Override
    protected void addCustomProperties(Map<String, PropertyDescriptor> descriptors) {
        super.addCustomProperties(descriptors);
        String eipName = eip.getName();
        String propertyPrefix = capitalizeFirstLetter(eipName) + ".";
        ArrayList<Parameter> params = eip.getParameters();
        Iterator<Parameter> it = params.iterator();
        while(it.hasNext()) {
        	Parameter p = it.next();
        	if( !isIgnored(p)) {
	        	String javaType = p.getJavaType();
	        	String paramName = p.getName();
	        	String propertyDescriptorId = propertyPrefix + capitalizeFirstLetter(paramName);
	        	
	        	// Replace all camel case with a space so it's human readable
	        	String display = convertCamelCase(paramName);
	        	PropertyDescriptor desc = createPropertyDescriptor(javaType, propertyDescriptorId, display);
	        	descriptors.put(propertyDescriptorId, desc);
        	}
        }
    }
    

    /* (non-Javadoc)
     * @see org.eclipse.ui.views.properties.IPropertySource\#setPropertyValue(java.lang.Object, java.lang.Object)
     */
    @Override
    public void setPropertyValue(Object id, Object value) {
        Object oldValue = propertyValues.get(id);
        propertyValues.put((String)id,  value);
        if (!isSame(oldValue, value)) {
            firePropertyChange((String)id, oldValue, value);
        }
        super.setPropertyValue(id, value);
    }

    /* (non-Javadoc)
     * @see org.fusesource.ide.camel.model.AbstractNode\#getPropertyValue(java.lang.Object)
     */
    @Override
    public Object getPropertyValue(Object id) {
    	if( propertyValues.get(id) != null )
    		return propertyValues.get(id);
    	return super.getPropertyValue(id);
    }
    
	/**
	 * Pass in a short property, such as "uri" as opposed to "Enrich.Uri"
	 * @param id
	 * @return
	 */
    public <T> T getShortPropertyValue(String id, Class<T> c) {
        String eipName = eip.getName();
    	String propertyDescriptorId = getPropertyKey(eipName, id);
    	Object ret = getPropertyValue(propertyDescriptorId);
    	if( ret != null && c.isInstance(ret)) {
    		return c.cast(ret);
    	}
    	return null;
	}
    
    public Object getShortPropertyValue(String id) {
    	return getShortPropertyValue(id, Object.class);
    }
    
	public void setShortPropertyValue(String id, Object val) {
        String eipName = eip.getName();
    	String propertyDescriptorId = getPropertyKey(eipName, id);
    	setPropertyValue(propertyDescriptorId, val);
	}
    
    public static String getPropertyKey(String eip, String property) {
        String propertyPrefix = capitalizeFirstLetter(eip) + ".";
    	String propertyDescriptorId = propertyPrefix + capitalizeFirstLetter(property);
    	return propertyDescriptorId;
    	
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    public ProcessorDefinition createCamelDefinition() {
        String eipName = eip.getName();
        String propertyPrefix = capitalizeFirstLetter(eipName) + ".";
        try {
	    	ProcessorDefinition answer = definition.getClass().newInstance();
	        ArrayList<Parameter> params = eip.getParameters();
	        Iterator<Parameter> it = params.iterator();
	        while(it.hasNext()) {
	        	Parameter p = it.next();
	        	if(!isIgnored(p)) {
	        		runSetter(p, answer, propertyPrefix);
	        	}
	        }
	        super.savePropertiesToCamelDefinition(answer);
	        return answer;
        } catch(Exception e) {
        	Activator.getDefault().getLog().log(new Status(
        			IStatus.ERROR, "org.fusesource.ide.camel.model", e.getMessage(), e));
        }
        return null;
    }
    
    
    private void runSetter(Parameter p, ProcessorDefinition answer, String propertyPrefix) throws Exception {
		String paramName = p.getName();
    	String propertyDescriptorId = propertyPrefix + capitalizeFirstLetter(paramName);
    	Object val = propertyValues.get(propertyDescriptorId);
    	
    	String originalField = p.getOriginalFieldName();
    	if( originalField == null )
    		originalField = p.getName();
    	
		String setterMethod = "set" + capitalizeFirstLetter(originalField);
		Class paramClass = findClass(p.getJavaType());
		Method m = null;
		if( paramClass != null ) {
			// First try using a setter based on original field name
			try {
				m = answer.getClass().getMethod(setterMethod, paramClass);
				if( m != null ) {
					m.invoke(answer, val);
					return;
				}
			} catch(NoSuchMethodException nsme) {
				System.out.println("Method " + setterMethod + " does not exist for eip " + eip.getName() + " :: " + nsme.getMessage());
				//nsme.printStackTrace();
			} catch(IllegalAccessException iae) {
				System.out.println("Method " + setterMethod + " does not exist for eip " + eip.getName() + " :: " + iae.getMessage());
//				iae.printStackTrace();
			} catch(InvocationTargetException ite) {
				System.out.println("Method " + setterMethod + " does not exist for eip " + eip.getName() + " :: " + ite.getMessage());
//				ite.printStackTrace();
			}
			
			// Try to find a field with the parameter name?
			try {
				Objects.setField(answer, originalField, toXmlPropertyValue(propertyDescriptorId, val));
				return;
			} catch(Exception e) {
				System.out.println("Field name " + originalField + " does not exist for eip " + eip.getName() + " :: " + e.getMessage());
//				e.printStackTrace();
			}
			
			// Ok, no setter and field does not exist, try some other variations
			if( m == null ) {
				// It's possible the model is wrong and the setter doesn't match the param name
				// We may try adding an 's' at the end in case the param is 'exception' but the setter is setExceptions
				try {
					m = answer.getClass().getMethod(setterMethod + "s", paramClass);
					if( m != null ) {
			    		m.invoke(answer, val);
			    		return;
					}
				} catch(NoSuchMethodException nsme2) {
					System.out.println("Method name " + setterMethod + "s does not exist for eip " + eip.getName() + " :: " + nsme2.getMessage());
//					nsme2.printStackTrace();
				} catch(IllegalAccessException iae) {
					System.out.println("Method name " + setterMethod + "s does not exist for eip " + eip.getName() + " :: " + iae.getMessage());
//					iae.printStackTrace();
				} catch(InvocationTargetException ite) {
					System.out.println("Method name " + setterMethod + "s does not exist for eip " + eip.getName() + " :: " + ite.getMessage());
//					ite.printStackTrace();
				}
			}

			// No field, no setter,  no plural setter.  attempt plural field
			try {
				Objects.setField(answer, paramName + "s", toXmlPropertyValue(propertyDescriptorId, val));
				return;
			} catch(Exception e2) {
				System.out.println("Field name " + originalField + "s does not exist for eip " + eip.getName() + " :: " + e2.getMessage());
			}
			
			throw new Exception("All attempts at setting value have failed for parameter " + p.getName());
		}
    }
    
    protected boolean isIgnored(Parameter p) {
    	String name = p.getName();
    	if( "id".equals(name) || "outputs".equals(name) || "description".equals(name))
    		return true;
    	return false;
    }

    @SuppressWarnings("rawtypes")
    @Override
    protected void loadPropertiesFromCamelDefinition(ProcessorDefinition processor) {
        super.loadPropertiesFromCamelDefinition(processor);
        ArrayList<Parameter> params = eip.getParameters();
        Iterator<Parameter> it = params.iterator();
        String eipName = eip.getName();
        String propertyPrefix = capitalizeFirstLetter(eipName) + ".";
        while(it.hasNext()) {
        	Parameter p = it.next();
	        String propertyDescriptorId = propertyPrefix + capitalizeFirstLetter(p.getName());
        	if( !isIgnored(p)) {
        		Object result = runGetter(p, processor, propertyPrefix);
		        setPropertyValue(propertyDescriptorId, result);
        	}
        }
    }

    private Object runGetter(Parameter p, ProcessorDefinition processor, String propertyPrefix) {
    	// Check a properly-formed getter
		String getter = "get" + capitalizeFirstLetter(p.getName());
		Method m = null;
        try {
	        m = processor.getClass().getMethod(getter, null);
        } catch(NoSuchMethodException nsme) {
    		// TODO
    		nsme.printStackTrace();
        }
        if( m == null ) {
        	// Check a getter that's plural
        	try {
        		m = processor.getClass().getMethod(getter + "s", null);
        	} catch(NoSuchMethodException nsme) {
        		// TODO
        		nsme.printStackTrace();
        	}
        }
        if( m != null ) {
        	try {
		        String propertyDescriptorId = propertyPrefix + capitalizeFirstLetter(p.getName());
		        Object result = m.invoke(processor, null);
		        return result;
        	} catch(IllegalAccessException iae) {
        		iae.printStackTrace();
        	} catch(InvocationTargetException iae) {
        		iae.printStackTrace();
        	}
        } else {
        	System.out.println("Expected getter not found: " + getter);
        	// Try via field
        	try {
	        	return Objects.getField(processor, p.getName(), findClass(p.getJavaType()));
        	}  catch(NoSuchFieldException nsfe) {
        		nsfe.printStackTrace();
        	}  catch(IllegalAccessException nsfe) {
        		nsfe.printStackTrace();
        	}

        	try {
        		// Try field with an 's'
	        	return Objects.getField(processor, p.getName() + "s", findClass(p.getJavaType()));
        	}  catch(NoSuchFieldException nsfe) {
        		nsfe.printStackTrace();
        	}  catch(IllegalAccessException nsfe) {
        		nsfe.printStackTrace();
        	}

        	// Error that the expected getter wasn't found
        }
        return null;
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    public Class<?> getCamelDefinitionClass() {
        return definition.getClass();
    }

}
