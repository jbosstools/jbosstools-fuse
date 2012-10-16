package org.fusesource.ide.commons.util;

public interface Predicate<T> {

	public boolean matches(T object);
}
