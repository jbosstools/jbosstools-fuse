package org.fusesource.ide.jmx.commons.messages;

public interface PreMarshalHook {
	
	/**
	 * When this object is about to be marshaled to XML we need to make sure the JAXB replica is up to date
	 */
	public void preMarshal();
	
}
