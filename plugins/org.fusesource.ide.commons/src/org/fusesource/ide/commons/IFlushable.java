package org.fusesource.ide.commons;

/**
 * Represents an object like a Preferences object which can be flushed to its underlying persistent storage.
 */
public interface IFlushable {
	public void flush();
}
