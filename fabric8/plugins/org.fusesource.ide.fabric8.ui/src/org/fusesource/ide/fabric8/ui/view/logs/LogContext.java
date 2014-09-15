/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.fabric8.ui.view.logs;

import io.fabric8.insight.log.LogFilter;

import java.util.List;

import org.fusesource.ide.fabric8.core.dto.LogEventDTO;

public interface LogContext {

	public void addLogResults(List<LogEventDTO> events);

	public abstract LogFilter getLogFilter();

}
