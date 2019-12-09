/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.model.service.internal;

import org.eclipse.core.runtime.IStatus;
import org.jboss.tools.foundation.core.plugin.log.IPluginLog;

final class PluginLogAcceptingNoInteraction implements IPluginLog {
	
	private final class RuntimeShouldNotBeCalledException extends RuntimeException {

		private static final long serialVersionUID = 689485168674040821L;

		private RuntimeShouldNotBeCalledException(String message, Throwable cause) {
			super(message, cause);
		}

		public RuntimeShouldNotBeCalledException(Throwable t) {
			super(t);
		}

		public RuntimeShouldNotBeCalledException(String message) {
			super(message);
		}
	}

	@Override
	public void logWarning(String message, Throwable t) {
		throw new RuntimeShouldNotBeCalledException(message, t);
	}

	@Override
	public void logWarning(Throwable t) {
		throw new RuntimeShouldNotBeCalledException(t);
		
	}

	@Override
	public void logWarning(String message) {
		throw new RuntimeShouldNotBeCalledException(message);
	}

	@Override
	public void logStatus(IStatus status) {
		throw new RuntimeShouldNotBeCalledException(status.getMessage());
	}

	@Override
	public void logMessage(int code, String message, Throwable t) {
		throw new RuntimeShouldNotBeCalledException(message, t);
		
	}

	@Override
	public void logInfo(String message, Throwable t) {
		throw new RuntimeShouldNotBeCalledException(message, t);
	}

	@Override
	public void logInfo(String message) {
		throw new RuntimeShouldNotBeCalledException(message);
	}

	@Override
	public void logError(String message, Throwable t) {
		throw new RuntimeShouldNotBeCalledException(message, t);
	}

	@Override
	public void logError(Throwable t) {
		throw new RuntimeShouldNotBeCalledException(t);
	}

	@Override
	public void logError(String message) {
		throw new RuntimeShouldNotBeCalledException(message);
	}
}