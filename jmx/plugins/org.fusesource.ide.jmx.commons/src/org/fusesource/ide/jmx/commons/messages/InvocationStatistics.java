/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.jmx.commons.messages;

import java.util.concurrent.atomic.AtomicLong;

public class InvocationStatistics implements IInvocationStatistics {

	private AtomicLong counter = new AtomicLong(0);
	private AtomicLong elapsedTime = new AtomicLong(0);
	private AtomicLong maxElapsedTime = new AtomicLong(0);
	private AtomicLong minElapsedTime = new AtomicLong(0);

	public InvocationStatistics() {
		super();
	}

	public InvocationStatistics(IInvocationStatistics that) {
		if (that != null) {
			counter.set(that.getCounter());
			elapsedTime.set(that.getTotalElapsedTime());
			maxElapsedTime.set(that.getMaxElapsedTime());
			minElapsedTime.set(that.getMinElapsedTime());
		}
	}

	/**
	 * Appends the given set of stats to this object
	 */
	public void append(NodeStatistics that) {
		counter.addAndGet(that.getCounter());
		elapsedTime.addAndGet(that.getTotalElapsedTime());

		synchronized (this) {
			long time = that.getMinElapsedTime();
			if (time < minElapsedTime.get()) {
				minElapsedTime.set(time);
			}
			time = that.getMaxElapsedTime();
			if (time > maxElapsedTime.get()) {
				maxElapsedTime.set(time);
			}
		}
	}

	/**
	 * Combines the child statistics in this statistic; so that we combine elapsed times but only
	 * take the maximum number of counts
	 */
	public void combineChild(INodeStatistics that) {
		long value = that.getCounter();
		if (value > counter.get()) {
			counter.set(value);
		}
		elapsedTime.addAndGet(that.getTotalElapsedTime());

		synchronized (this) {
			value = that.getMinElapsedTime();
			if (value < minElapsedTime.get()) {
				minElapsedTime.set(value);
			}
			value = that.getMaxElapsedTime();
			if (value > maxElapsedTime.get()) {
				maxElapsedTime.set(value);
			}
		}
	}

	/**
	 * Increments the counter with the given elapsed time
	 */
	public void increment(Long time) {
		counter.incrementAndGet();
		if (time != null) {
			elapsedTime.addAndGet(time);

			synchronized (this) {
				if (time < minElapsedTime.get()) {
					minElapsedTime.set(time);
				}
				if (time > maxElapsedTime.get()) {
					maxElapsedTime.set(time);
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.fusesource.fon.util.messages.IInvocationStatistics#getCounter()
	 */
	@Override
	public long getCounter() {
		return counter.get();
	}

	/* (non-Javadoc)
	 * @see org.fusesource.fon.util.messages.IInvocationStatistics#getTotalElapsedTime()
	 */
	@Override
	public long getTotalElapsedTime() {
		return elapsedTime.get();
	}

	/* (non-Javadoc)
	 * @see org.fusesource.fon.util.messages.IInvocationStatistics#getMinElapsedTime()
	 */
	@Override
	public long getMinElapsedTime() {
		return minElapsedTime.get();
	}

	/* (non-Javadoc)
	 * @see org.fusesource.fon.util.messages.IInvocationStatistics#getMaxElapsedTime()
	 */
	@Override
	public long getMaxElapsedTime() {
		return maxElapsedTime.get();
	}

	/* (non-Javadoc)
	 * @see org.fusesource.fon.util.messages.IInvocationStatistics#getAverageElasedTime()
	 */
	@Override
	public double getMeanElapsedTime() {
		long c = getCounter();
		if (c <= 0) {
			return 0;
		} else {
			return getTotalElapsedTime() / c;
		}
	}


}