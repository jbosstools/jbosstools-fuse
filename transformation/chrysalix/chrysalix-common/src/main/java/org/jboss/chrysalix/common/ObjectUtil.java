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
package org.jboss.chrysalix.common;

/**
 * Utilities for any {@link Object object}.
 */
public final class ObjectUtil {

    /**
     * An empty array of objects.
     */
    public static final Object[] EMPTY_ARRAY = new Object[ 0 ];

    /**
     * @param obj1
     *        the first object used
     * @param obj2
     *        the other object used
     * @return <code>true</code> if both objects are <code>null</code> or they are equal
     */
    public static boolean equals( final Object obj1,
                                  final Object obj2 ) {
        if ( obj1 == obj2 ) {
            return true;
        }

        if ( obj1 == null ) {
            return ( obj2 == null );
        }

        if ( obj2 == null ) {
            return false;
        }

        return obj1.equals( obj2 );
    }

    /**
     * Don't allow construction outside of this class.
     */
    private ObjectUtil() {
        // nothing to do
    }

}
