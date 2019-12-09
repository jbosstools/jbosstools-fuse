/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.foundation.ui.label;

public class Bar {
	private final BarColour colour;
	private final double rate;
	private String text;

	/**
	 * The rate should be <= 1.0
	 */
	public Bar(BarColour colour, double rate) {
		this(colour, rate, null);
	}

	public Bar(BarColour colour, double rate, String text) {
		this.colour = colour;
		this.rate = rate;
		this.text = text;
	}

	@Override
	public String toString() {
		return "Bar[" + colour + ", " + rate + (text != null ? ", " + text : "") + "]";
	}

	public BarColour getColour() {
		return colour;
	}

	public String getText() {
		return text;
	}

	/**
	 * The rate should be >= 0.0 and <= 1.0
	 */
	public double getRate() {
		return rate;
	}

	public void setText(String text) {
		this.text = text;
	}


}
