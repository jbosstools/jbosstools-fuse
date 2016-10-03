/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 

package org.fusesource.ide.catalog.generator.model.component;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author lhein
 *
 */
public class UriParam {
	private String name;
	private String kind;
	private String required;
	private String type;
	private String javaType;
	private String group;
	private String deprecated;
	private String defaultValue;
    private String description;
	private String label;
	@JsonProperty("enum")
	private String[] choice;
	@JsonProperty(required=false)
	private String prefix;
	@JsonProperty(required=false)
	private String optionalPrefix;
	@JsonProperty(required=false)
	private String multiValue;
	private String secret;

	/**
	 * @return the optionalPrefix
	 */
	public String getOptionalPrefix() {
		return this.optionalPrefix;
	}
	
	/**
	 * @param optionalPrefix the optionalPrefix to set
	 */
	public void setOptionalPrefix(String optionalPrefix) {
		this.optionalPrefix = optionalPrefix;
	}
	
	/**
	 * @return the multiValue
	 */
	public String getMultiValue() {
		return this.multiValue;
	}
	
	/**
	 * @param multiValue the multiValue to set
	 */
	public void setMultiValue(String multiValue) {
		this.multiValue = multiValue;
	}
	
	/**
	 * @return the prefix
	 */
	public String getPrefix() {
		return this.prefix;
	}
	
	/**
	 * @param prefix the prefix to set
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	/**
	 * @return the group
	 */
	public String getGroup() {
		return this.group;
	}
	
	/**
	 * @param group the group to set
	 */
	public void setGroup(String group) {
		this.group = group;
	}
	
	/**
	 * @return the choice
	 */
	public String[] getChoice() {
		return this.choice;
	}
	
	/**
	 * @param choice the choice to set
	 */
	public void setChoice(String[] choice) {
		this.choice = choice;
	}
	
    /**
     * @return the name
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * @return the type
     */
    public String getType() {
        return this.type;
    }
    
    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }
    
    /**
     * @return the defaultValue
     */
    public String getDefaultValue() {
        return this.defaultValue;
    }
    
    /**
     * @param defaultValue the defaultValue to set
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
        this.defaultValue = this.defaultValue.replace("\"", "&quot;");
		this.defaultValue = this.defaultValue.replace("\r", "\\r");
		this.defaultValue = this.defaultValue.replace("\n", "\\n");
    }
    
    /**
     * @return the kind
     */
    public String getKind() {
        return this.kind;
    }
    
    /**
     * @param kind the kind to set
     */
    public void setKind(String kind) {
        this.kind = kind;
    }
    
    /**
     * @return the label
     */
    public String getLabel() {
        return this.label;
    }
    
    /**
     * @param label the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }
    
    /**
     * @return the description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
        this.description = this.description.replace("\"", "&quot;");
		this.description = this.description.replace("\r", "\\r");
		this.description = this.description.replace("\n", "\\n");
    }
    
    /**
	 * @return the required
	 */
	public String getRequired() {
		return this.required;
	}
	
	/**
	 * @param required the required to set
	 */
	public void setRequired(String required) {
		this.required = required;
	}
	
	/**
	 * @return the deprecated
	 */
	public String getDeprecated() {
		return this.deprecated;
	}
	
	/**
	 * @param deprecated the deprecated to set
	 */
	public void setDeprecated(String deprecated) {
		this.deprecated = deprecated;
	}
	
	/**
	 * @return the javaType
	 */
	public String getJavaType() {
		return this.javaType;
	}
	
	/**
	 * @param javaType the javaType to set
	 */
	public void setJavaType(String javaType) {
		this.javaType = javaType;
		this.javaType = this.javaType.replaceAll("<", "&lt;");
		this.javaType = this.javaType.replaceAll(">", "&gt;");
	}
	
	public String getChoiceString() {
		if (this.choice == null || this.choice.length<1) return null;
		String retVal = "";
		for (String c : this.choice) {
			if (retVal.length()>0) retVal += ","; 
			retVal += c;
		}
		return retVal;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}
}
