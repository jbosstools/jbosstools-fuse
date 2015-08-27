package org.fusesource.ide.foundation.core.util;

public interface Filter<T> {
	public boolean matches(T object);
}
