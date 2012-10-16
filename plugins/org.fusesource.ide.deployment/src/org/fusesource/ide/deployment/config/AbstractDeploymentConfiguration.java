package org.fusesource.ide.deployment.config;

/**
 * @author lhein
 */
public abstract class AbstractDeploymentConfiguration {
	
	protected String name;
	protected String description;
	protected boolean defaultConfig;
	
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
	}
	
	/**
	 * @return the defaultConfig
	 */
	public boolean isDefaultConfig() {
		return this.defaultConfig;
	}
	
	/**
	 * @param defaultConfig the defaultConfig to set
	 */
	public void setDefaultConfig(boolean defaultConfig) {
		this.defaultConfig = defaultConfig;
	}
}
