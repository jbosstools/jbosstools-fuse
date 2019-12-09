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
package org.jboss.tools.fuse.transformation.core.dozer;

import java.util.List;

import org.jboss.tools.fuse.transformation.core.Expression;
import org.jboss.tools.fuse.transformation.core.ExpressionMapping;
import org.jboss.tools.fuse.transformation.core.MappingType;
import org.jboss.tools.fuse.transformation.core.dozer.config.Field;
import org.jboss.tools.fuse.transformation.core.dozer.config.Mapping;
import org.jboss.tools.fuse.transformation.core.model.Model;

/**
 * Dozer-specific implementation of ExpressionMapping.
 */
public class DozerExpressionMapping extends BaseDozerMapping implements ExpressionMapping {

    private Expression source;
    private Model target;

    /**
     * Create a new DozerExpressionMapping.
     * 
     * @param source source expression
     * @param target target field
     */
    public DozerExpressionMapping(Expression source, Model target, Mapping mapping, Field field) {
        super(mapping, field);
        this.source = source;
        this.target = target;
    }

    @Override
    public Expression getSource() {
        return source;
    }

    @Override
    public Model getTarget() {
        return target;
    }

    @Override
    public MappingType getType() {
        return MappingType.EXPRESSION;
    }
    
    @Override
    public void setTargetIndex(List<Integer> indexes) {
        setFieldIndex(getField().getB(), target, getMapping().getClassB().getContent(), indexes);
    }
}
