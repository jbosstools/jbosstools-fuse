package org.fusesource.ide.fabric.navigator.osgi;

import org.eclipse.ui.views.properties.IPropertySource;

public class BundleStateFacade {
	private final IPropertySource source;

	public BundleStateFacade(IPropertySource source) {
		this.source = source;
	}

	public Long getId() {
		return getProperty("Identifier", Long.class);
	}

	public String getState() {
		return getProperty("State", String.class);
	}

	protected <T> T getProperty(String name, Class<T> aClass) {
		Object value = source.getPropertyValue(name);
		if (value != null && aClass.isInstance(value)) {
			return aClass.cast(value);
		}
		return null;
	}

	public String getLocation() {
		return getProperty("Location", String.class);
	}
}
