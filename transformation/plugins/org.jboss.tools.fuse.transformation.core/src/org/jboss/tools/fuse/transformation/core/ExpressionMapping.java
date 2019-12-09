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
 * An ExpressionMapping represents a mapping where the source is a Camel 
 * language expression which is evaluated at runtime and mapped to a target
 * field.
 */
public interface ExpressionMapping extends MappingOperation<Expression, Model> {

}
