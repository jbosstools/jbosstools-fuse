/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others.
 * All rights reserved.  See the COPYRIGHT.txt file distributed with this work
 * for information regarding copyright ownership.  Some portions may be
 * licensed to Red Hat, Inc. under one or more contributor license agreements.
 * See the AUTHORS.txt file in the distribution for a full listing of
 * individual contributors.
 *
 * Chrysalix is free software. Unless otherwise indicated, all code in
 * Chrysalix is licensed to you under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * Chrysalix is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.chrysalix.internal.transformer;

import java.util.Map;
import org.jboss.chrysalix.spi.AbstractTransformer;

public class StoreTransformer extends AbstractTransformer {

    public StoreTransformer() {}

    @Override
    public void transform(Map<String, Object> context,
                          String[] arguments) {
        // TODO support other values besides just value
        int ndx = 0;
        String var = arguments[ndx++];
        if ("in".equals(var)) var = arguments[ndx++];
        if ("variable".equals(var)) var = arguments[ndx++];
        if (!var.startsWith("$")) {
            var = '$' + var;
        }
        if (!var.endsWith("$")) {
            var = var + '$';
        }
        context.put(var, context.get(DATA));
    }
}
