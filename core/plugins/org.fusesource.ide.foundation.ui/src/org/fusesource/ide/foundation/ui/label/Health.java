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

import java.util.List;

public class Health {
	private String text;
	private Double current;
	private Double minimum;
	private Double maximum;

	public Health() {
	}

	public static Health newInstance(String text, Number current, Number minimum, Number maximum) {
		return new Health(text, toDouble(current), toDouble(minimum), toDouble(maximum));
	}

	protected static Double toDouble(Number n) {
		if (n == null) {
			return null;
		} else if (n instanceof Double) {
			return (Double) n;
		} else {
			return n.doubleValue();
		}
	}

	public Health(String text, Double current, Double minimum, Double maximum) {
		this.text = text;
		this.current = current;
		this.minimum = minimum;
		this.maximum = maximum;
	}

	public void addBars(List<Bar> bars) {
		if (current == null)
			return;

		double scale = max(current, minimum, maximum);
		if (scale > 0) {
			if (minimum == null || minimum <= 0.0 || equal(current, minimum)) {
				// we assume single bar of green
				bars.add(new Bar(BarColour.Green, current / scale));
			} else {
				if (current > minimum) {
					bars.add(new Bar(BarColour.Green, minimum / scale));
					BarColour overMin = BarColour.Green;
					BarColour overMax = BarColour.DarkGreen;
					//BarColour overMin = BarColour.DarkGreen;
					//BarColour overMax = BarColour.Blue;
					if (maximum != null && current > maximum) {
						bars.add(new Bar(overMin, (maximum - minimum) / scale));
						bars.add(new Bar(overMax, (current - maximum) / scale));
					} else {
						bars.add(new Bar(overMin, (current - minimum) / scale));
					}
				} else {
					if (current / scale > 0.1) {
						bars.add(new Bar(BarColour.Yellow, current / scale));
					} else {
						bars.add(new Bar(BarColour.Red, 1.0));
					}
				}
			}
		}
		if (text != null && !bars.isEmpty()) {
			Bar bar = bars.get(0);
			bar.setText(text);
		}
	}

	/**
	 * Returns true if the 2 numbers are close enough :)
	 */
	private boolean equal(double d1, double d2) {
		double diff = Math.abs(d1 - d2);
		return diff < 0.0001;
	}

	private double max(Double... numbers) {
		double answer = Double.MIN_VALUE;
		for (Double d : numbers) {
			if (d != null) {
				double value = d.doubleValue();
				if (value > answer) answer = value;
			}
		}
		return answer;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Double getCurrent() {
		return current;
	}

	public void setCurrent(Double current) {
		this.current = current;
	}

	public Double getMinimum() {
		return minimum;
	}

	public void setMinimum(Double minimum) {
		this.minimum = minimum;
	}

	public Double getMaximum() {
		return maximum;
	}

	public void setMaximum(Double maximum) {
		this.maximum = maximum;
	}

	// Properties

}
