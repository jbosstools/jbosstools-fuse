package org.fusesource.ide.commons.ui.actions;

import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.jface.action.Separator;

/**
 * Creates {@link Separator} objects with unique IDs for a view
 */
public class SeparatorFactory {
	private final String prefix;
	private final AtomicInteger counter = new AtomicInteger();

	public SeparatorFactory(String ownerViewId) {
		this.prefix = ownerViewId + ".separator.";
	}

	@Override
	public String toString() {
		return "SeparatorFactory(" + prefix + "n)";
	}



	public Separator createSeparator() {
		Separator answer = new Separator();
		answer.setId(prefix + counter.incrementAndGet());
		return answer;
	}

}
