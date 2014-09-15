/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.fabric8.core.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * @author lhein
 */
public class VersionSequenceDTO implements Comparable<VersionSequenceDTO> {
	
	private final String name;
	private final int[] numbers;

	public static Comparator<String> getComparator() {
		return new Comparator<String>() {
			@Override
			public int compare(String thisId, String thatId) {
				VersionSequenceDTO thisSeq = new VersionSequenceDTO(thisId);
				VersionSequenceDTO thatSeq = new VersionSequenceDTO(thatId);
				return thisSeq.compareTo(thatSeq);
			}
		};
	}

	public VersionSequenceDTO(String name) {
		this.name = name;
		List<Integer> list = new ArrayList<Integer>();
		String[] split = name.split("\\.");
		for (String text : split) {
			text = text.trim();
			if (text.length() > 0) {
				try {
					int number = Integer.parseInt(text);
					list.add(number);
				} catch (NumberFormatException e) {
					// ignore bad number
				}
			}
		}
		int size = list.size();
		numbers = new int[size];
		for (int i = 0; i < size; i++) {
			numbers[i] = list.get(i);
		}
	}

	/**
	 * Creates the next sequence
	 */
	protected VersionSequenceDTO(VersionSequenceDTO previous) {
		int[] pn = previous.numbers;
		int size = pn.length;
		if (size <= 0) {
			this.numbers = new int[] { 1 };
		} else {
			this.numbers = Arrays.copyOf(pn, size);
			this.numbers[size - 1] += 1;
		}
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < size; i++) {
			if (i > 0) {
				builder.append(".");
			}
			builder.append(numbers[i]);
		}
		this.name = builder.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		VersionSequenceDTO that = (VersionSequenceDTO) o;
		if (!name.equals(that.name))
			return false;
		if (!Arrays.equals(numbers, that.numbers))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		int result = name.hashCode();
		result = 31 * result + Arrays.hashCode(numbers);
		return result;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int compareTo(VersionSequenceDTO that) {
		if (equals(that))
			return 0;
		int[] n1 = this.numbers;
		int[] n2 = that.numbers;
		for (int i = 0; i < n1.length; i++) {
			if (i >= n2.length) {
				// we must be greater as 1.1 is greater than 1
				// though we treat 1.0 as greater than 1 too but no biggie
				return 1;
			} else {
				int diff = n1[i] - n2[i];
				if (diff != 0) {
					return diff;
				}
			}
		}
		return n1.length - n2.length;
	}

	public String getName() {
		return name;
	}

	/**
	 * Returns the array of version numbers for this sequence
	 */
	public int[] getNumbers() {
		return numbers;
	}

	/**
	 * Returns a new version number sequence
	 */
	public VersionSequenceDTO next() {
		return new VersionSequenceDTO(this);
	}
}
