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
 *
 */
public abstract class BaseException extends Exception {

    /**
     * @param i18nClass
     *        the internationalization class used to localize the supplied message
     * @param message
     *        the localizable exception message
     * @param arguments
     *        optional arguments to the supplied message
     */
    public BaseException( final Class< ? > i18nClass,
                          final String message,
                          final Object... arguments ) {
        super( I18n.localize( i18nClass, null, message, arguments ) );
    }

    /**
     * @param i18nClass
     *        the internationalization class used to localize the supplied message
     * @param cause
     *        the underlying Throwable that caused of this exception
     * @param message
     *        the localizable exception message
     * @param arguments
     *        optional arguments to the supplied message
     */
    public BaseException( final Class< ? > i18nClass,
                          final Throwable cause,
                          final String message,
                          final Object... arguments ) {
        super( I18n.localize( i18nClass, null, message, arguments ), cause );
    }

    /**
     * @param message
     *        a localizable exception message
     * @param arguments
     *        optional arguments to the supplied message
     */
    public BaseException( final I18n message,
                          final Object... arguments ) {
        super( message.text( arguments ) );
    }

    /**
     * @param cause
     *        the underlying Throwable that caused of this exception
     * @param message
     *        the localizable exception message
     * @param arguments
     *        optional arguments to the supplied message
     */
    public BaseException( final Throwable cause,
                          final I18n message,
                          final Object... arguments ) {
        super( message.text( arguments ), cause );
    }
}
