package org.fusesource.ide.deployment.config;

/**
 * @author lhein
 */
public class HotfolderDeploymentConfiguration extends
		AbstractDeploymentConfiguration {
	
	protected String hotDeployPath;
		
	/**
	 * @return the hotDeployPath
	 */
	public String getHotDeployPath() {
		return this.hotDeployPath;
	}
	
	/**
	 * @param hotDeployPath the hotDeployPath to set
	 */
	public void setHotDeployPath(String hotDeployPath) {
		this.hotDeployPath = hotDeployPath;
	}	
}
