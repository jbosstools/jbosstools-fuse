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

package org.fusesource.ide.camel.editor.utils;

/**
 * @author lhein
 */
public interface IPrefersPerspective {
	/**
     * @return the preferred perspective of this part or null if no perspective
     *         is preferred.
     */
    String getPreferredPerspectiveId();
}
