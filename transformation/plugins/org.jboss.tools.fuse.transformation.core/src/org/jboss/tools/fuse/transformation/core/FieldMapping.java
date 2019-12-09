/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.transformation.core;

import org.jboss.tools.fuse.transformation.core.model.Model;

/**
 * A basic mapping operation where one field is assigned to another field. The
 * source and target types are Models that represent nodes used in the mapping.
 */
public interface FieldMapping extends MappingOperation<Model, Model> {

}
