package org.fusesource.ide.commons.util;

/**
 * Represents a function which takes a single argument
 */
public interface Function1<T,R> {

	public R apply(T argument);
}
