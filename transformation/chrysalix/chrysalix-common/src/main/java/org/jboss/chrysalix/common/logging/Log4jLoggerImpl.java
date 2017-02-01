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

final class Log4jLoggerImpl extends Logger {

    private final org.apache.log4j.Logger logger;
    private final Class< ? > i18nClass;

    public Log4jLoggerImpl( final Class< ? > i18nClass,
                            final String context ) {
        logger = org.apache.log4j.Logger.getLogger( context );
        this.i18nClass = i18nClass;
    }

    @Override
    public void debug( final String message,
                       final Object... params ) {
        if ( !debugEnabled() ) return;
        logger.debug( String.format( message, params ) );
    }

    @Override
    public void debug( final Throwable t,
                       final String message,
                       final Object... params ) {
        if ( empty( message ) ) {
            return;
        }
        if ( !debugEnabled() ) return;
        logger.debug( String.format( message, params ), t );
    }

    @Override
    public boolean debugEnabled() {
        return logger.isEnabledFor( org.apache.log4j.Level.DEBUG );
    }

    private boolean empty( final String str ) {
        return str == null || str.trim().isEmpty();
    }

    @Override
    public void error( final I18n message,
                       final Object... params ) {
        if ( message == null || !errorEnabled() ) return;
        logger.error( message.text( locale(), params ) );
    }

    @Override
    public void error( final String message,
                       final Object... arguments ) {
        if ( !errorEnabled() ) return;
        logger.error( I18n.localize( i18nClass, locale(), message, arguments ) );
    }

    @Override
    public void error( final Throwable t,
                       final I18n message,
                       final Object... params ) {
        if ( message == null || !errorEnabled() ) return;
        logger.error( message.text( locale(), params ), t );
    }

    @Override
    public void error( final Throwable t,
                       final String message,
                       final Object... params ) {
        if ( !errorEnabled() ) return;
        logger.error( I18n.localize( i18nClass, locale(), message, params ), t );
    }

    @Override
    public boolean errorEnabled() {
        return logger.isEnabledFor( org.apache.log4j.Level.ERROR );
    }

    @Override
    public String getName() {
        return logger.getName();
    }

    @Override
    public void info( final I18n message,
                      final Object... params ) {
        if ( message == null || !infoEnabled() ) return;
        logger.info( message.text( locale(), params ) );
    }

    @Override
    public void info( final String message,
                      final Object... arguments ) {
        if ( !infoEnabled() ) return;
        logger.info( I18n.localize( i18nClass, locale(), message, arguments ) );
    }

    @Override
    public void info( final Throwable t,
                      final I18n message,
                      final Object... params ) {
        if ( message == null || !infoEnabled() ) return;
        logger.info( message.text( locale(), params ), t );
    }

    @Override
    public void info( final Throwable t,
                      final String message,
                      final Object... arguments ) {
        if ( !infoEnabled() ) return;
        logger.info( I18n.localize( i18nClass, locale(), message, arguments ), t );
    }

    @Override
    public boolean infoEnabled() {
        return logger.isEnabledFor( org.apache.log4j.Level.INFO );
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
                logger.setLevel( org.apache.log4j.Level.OFF );
                break;
            }
            case ERROR: {
                logger.setLevel( org.apache.log4j.Level.ERROR );
                break;
            }
            case WARN: {
                logger.setLevel( org.apache.log4j.Level.WARN );
                break;
            }
            case INFO: {
                logger.setLevel( org.apache.log4j.Level.INFO );
                break;
            }
            case DEBUG: {
                logger.setLevel( org.apache.log4j.Level.DEBUG );
                break;
            }
            case TRACE: {
                logger.setLevel( org.apache.log4j.Level.TRACE );
                break;
            }
        }
    }

    @Override
    public void trace( final String message,
                       final Object... params ) {
        if ( empty( message ) ) {
            return;
        }
        if ( !traceEnabled() ) return;
        logger.trace( String.format( message, params ) );
    }

    @Override
    public void trace( final Throwable t,
                       final String message,
                       final Object... params ) {
        if ( empty( message ) ) {
            return;
        }
        if ( !traceEnabled() ) return;
        logger.trace( String.format( message, params ), t );
    }

    @Override
    public boolean traceEnabled() {
        return logger.isEnabledFor( org.apache.log4j.Level.TRACE );
    }

    @Override
    public void warn( final I18n message,
                      final Object... params ) {
        if ( message == null || !warnEnabled() ) return;
        logger.warn( message.text( locale(), params ) );
    }

    @Override
    public void warn( final String message,
                      final Object... arguments ) {
        if ( !warnEnabled() ) return;
        logger.warn( I18n.localize( i18nClass, locale(), message, arguments ) );
    }

    @Override
    public void warn( final Throwable t,
                      final I18n message,
                      final Object... params ) {
        if ( message == null || !warnEnabled() ) return;
        logger.warn( message.text( locale(), params ), t );
    }

    @Override
    public void warn( final Throwable t,
                      final String message,
                      final Object... arguments ) {
        if ( !warnEnabled() ) return;
        logger.warn( I18n.localize( i18nClass, locale(), message, arguments ), t );
    }

    @Override
    public boolean warnEnabled() {
        return logger.isEnabledFor( org.apache.log4j.Level.WARN );
    }
}
