package org.fusesource.ide.commons.util;

/**
 * Allows a {@link Function1} to be annotated with a return type
 */
public interface ReturnType<T> {

	Class<T> getReturnType();
}
