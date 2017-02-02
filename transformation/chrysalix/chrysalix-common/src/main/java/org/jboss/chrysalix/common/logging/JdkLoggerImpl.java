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
package org.jboss.chrysalix.common.logging;

import org.jboss.chrysalix.common.I18n;
import org.jboss.chrysalix.common.Logger;

/**
 * Logger that delivers messages to a JDK logger
 */
final class JdkLoggerImpl extends Logger {

    private final java.util.logging.Logger logger;
    private final Class< ? > i18nClass;

    public JdkLoggerImpl( final Class< ? > i18nClass,
                          final String context ) {
        logger = java.util.logging.Logger.getLogger( context );
        this.i18nClass = i18nClass;
    }

    @Override
    public void debug( final String message,
                       final Object... params ) {
        log( java.util.logging.Level.FINE, String.format( message, params ), null );
    }

    @Override
    public void debug( final Throwable t,
                       final String message,
                       final Object... params ) {
        log( java.util.logging.Level.FINE, String.format( message, params ), t );
    }

    @Override
    public boolean debugEnabled() {
        return logger.isLoggable( java.util.logging.Level.FINE );
    }

    @Override
    public void error( final I18n message,
                       final Object... params ) {
        log( java.util.logging.Level.SEVERE, message.text( locale(), params ), null );
    }

    @Override
    public void error( final String message,
                       final Object... arguments ) {
        log( java.util.logging.Level.SEVERE, I18n.localize( i18nClass, locale(), message, arguments ), null );
    }

    @Override
    public void error( final Throwable t,
                       final I18n message,
                       final Object... params ) {
        log( java.util.logging.Level.SEVERE, message.text( locale(), params ), t );
    }

    @Override
    public void error( final Throwable t,
                       final String message,
                       final Object... params ) {
        log( java.util.logging.Level.SEVERE, I18n.localize( i18nClass, locale(), message, params ), t );
    }

    @Override
    public boolean errorEnabled() {
        return logger.isLoggable( java.util.logging.Level.SEVERE );
    }

    @Override
    public String getName() {
        return logger.getName();
    }

    @Override
    public void info( final I18n message,
                      final Object... params ) {
        log( java.util.logging.Level.INFO, message.text( locale(), params ), null );
    }

    @Override
    public void info( final String message,
                      final Object... arguments ) {
        log( java.util.logging.Level.INFO, I18n.localize( i18nClass, locale(), message, arguments ), null );
    }

    @Override
    public void info( final Throwable t,
                      final I18n message,
                      final Object... params ) {
        log( java.util.logging.Level.INFO, message.text( locale(), params ), t );
    }

    @Override
    public void info( final Throwable t,
                      final String message,
                      final Object... arguments ) {
        log( java.util.logging.Level.INFO, I18n.localize( i18nClass, locale(), message, arguments ), t );
    }

    @Override
    public boolean infoEnabled() {
        return logger.isLoggable( java.util.logging.Level.INFO );
    }

    private void log( final java.util.logging.Level level,
                      final String message,
                      final Throwable ex ) {
        if ( logger.isLoggable( level ) ) {
            final Throwable dummyException = new Throwable();
            final StackTraceElement locations[] = dummyException.getStackTrace();
            String className = "unknown";
            String methodName = "unknown";
            final int depth = 2;
            if ( locations != null && locations.length > depth ) {
                final StackTraceElement caller = locations[ depth ];
                className = caller.getClassName();
                methodName = caller.getMethodName();
            }
            if ( ex == null ) {
                logger.logp( level, className, methodName, message );
            } else {
                logger.logp( level, className, methodName, message, ex );
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.chrysalix.common.Logger#setLevel(org.jboss.chrysalix.common.Logger.Level)
     */
    @Override
    public void setLevel( final Level level ) {
        switch ( level ) {
            case OFF: {
                logger.setLevel( java.util.logging.Level.OFF );
                break;
            }
            case ERROR: {
                logger.setLevel( java.util.logging.Level.SEVERE );
                break;
            }
            case WARN: {
                logger.setLevel( java.util.logging.Level.WARNING );
                break;
            }
            case INFO: {
                logger.setLevel( java.util.logging.Level.INFO );
                break;
            }
            case DEBUG: {
                logger.setLevel( java.util.logging.Level.FINE );
                break;
            }
            case TRACE: {
                logger.setLevel( java.util.logging.Level.FINER );
                break;
            }
        }
    }

    @Override
    public void trace( final String message,
                       final Object... params ) {
        log( java.util.logging.Level.FINER, String.format( message, params ), null );
    }

    @Override
    public void trace( final Throwable t,
                       final String message,
                       final Object... params ) {
        log( java.util.logging.Level.FINER, String.format( message, params ), t );

    }

    @Override
    public boolean traceEnabled() {
        return logger.isLoggable( java.util.logging.Level.FINER );
    }

    @Override
    public void warn( final I18n message,
                      final Object... params ) {
        log( java.util.logging.Level.WARNING, message.text( locale(), params ), null );
    }

    @Override
    public void warn( final String message,
                      final Object... arguments ) {
        log( java.util.logging.Level.WARNING, I18n.localize( i18nClass, locale(), message, arguments ), null );
    }

    @Override
    public void warn( final Throwable t,
                      final I18n message,
                      final Object... params ) {
        log( java.util.logging.Level.WARNING, message.text( locale(), params ), t );

    }

    @Override
    public void warn( final Throwable t,
                      final String message,
                      final Object... arguments ) {
        log( java.util.logging.Level.WARNING, I18n.localize( i18nClass, locale(), message, arguments ), t );
    }

    @Override
    public boolean warnEnabled() {
        return logger.isLoggable( java.util.logging.Level.WARNING );
    }
}
