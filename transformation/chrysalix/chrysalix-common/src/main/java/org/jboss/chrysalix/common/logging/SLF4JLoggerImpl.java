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
import org.slf4j.LoggerFactory;

/**
 * Logger that delivers messages to a Log4J logger
 *
 * @since 2.5
 */
final class SLF4JLoggerImpl extends Logger {

    private static Boolean debugEnabled;
    private static Boolean errorEnabled;
    private static Boolean infoEnabled;
    private static Boolean traceEnabled;
    private static Boolean warnEnabled;

    private final org.slf4j.Logger logger;
    private final Class< ? > i18nClass;

    public SLF4JLoggerImpl( final Class< ? > i18nClass,
                            final String context ) {
        logger = LoggerFactory.getLogger( context );
        this.i18nClass = i18nClass;
    }

    @Override
    public void debug( final String message,
                       final Object... params ) {
        if ( !debugEnabled() ) return;
        if ( message == null ) return;
        logger.debug( String.format( message, params ) );
    }

    @Override
    public void debug( final Throwable t,
                       final String message,
                       final Object... params ) {
        if ( !debugEnabled() ) return;
        if ( t == null ) {
            debug( message, params );
            return;
        }
        if ( message == null ) {
            logger.debug( null, t );
            return;
        }
        logger.debug( String.format( message, params ), t );
    }

    @Override
    public boolean debugEnabled() {
        return debugEnabled == null ? logger.isDebugEnabled() : debugEnabled;
    }

    @Override
    public void error( final I18n message,
                       final Object... params ) {
        if ( !errorEnabled() || message == null ) return;
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
        if ( !errorEnabled() ) return;
        if ( t == null ) {
            error( message, params );
            return;
        }
        if ( message == null ) {
            logger.error( null, t );
            return;
        }
        logger.error( message.text( locale(), params ), t );
    }

    @Override
    public void error( final Throwable t,
                       final String message,
                       final Object... params ) {
        if ( !errorEnabled() ) return;
        if ( t == null ) {
            error( message, params );
            return;
        }
        if ( message == null ) {
            logger.error( null, t );
            return;
        }
        logger.error( I18n.localize( i18nClass, locale(), message, params ), t );
    }

    @Override
    public boolean errorEnabled() {
        return errorEnabled == null ? logger.isErrorEnabled() : errorEnabled;
    }

    @Override
    public String getName() {
        return logger.getName();
    }

    @Override
    public void info( final I18n message,
                      final Object... params ) {
        if ( !infoEnabled() || message == null ) return;
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
        if ( !infoEnabled() ) return;
        if ( t == null ) {
            info( message, params );
            return;
        }
        if ( message == null ) {
            logger.info( null, t );
            return;
        }
        logger.info( message.text( locale(), params ), t );
    }

    @Override
    public void info( final Throwable t,
                      final String message,
                      final Object... arguments ) {
        if ( !infoEnabled() ) return;
        if ( t == null ) {
            info( message, arguments );
            return;
        }
        if ( message == null ) {
            logger.info( null, t );
            return;
        }
        logger.info( I18n.localize( i18nClass, locale(), message, arguments ), t );
    }

    @Override
    public boolean infoEnabled() {
        return infoEnabled == null ? logger.isInfoEnabled() : infoEnabled;
    }

    @Override
    public void setLevel( final Level level ) {
        errorEnabled = level.ordinal() >= Level.ERROR.ordinal();
        warnEnabled = level.ordinal() >= Level.WARN.ordinal();
        infoEnabled = level.ordinal() >= Level.INFO.ordinal();
        debugEnabled = level.ordinal() >= Level.DEBUG.ordinal();
        traceEnabled = level.ordinal() >= Level.TRACE.ordinal();
    }

    @Override
    public void trace( final String message,
                       final Object... params ) {
        if ( !traceEnabled() ) return;
        if ( message == null ) return;
        logger.trace( String.format( message, params ) );
    }

    @Override
    public void trace( final Throwable t,
                       final String message,
                       final Object... params ) {
        if ( !traceEnabled() ) return;
        if ( t == null ) {
            this.trace( message, params );
            return;
        }
        if ( message == null ) {
            logger.trace( null, t );
            return;
        }
        logger.trace( String.format( message, params ), t );
    }

    @Override
    public boolean traceEnabled() {
        return traceEnabled == null ? logger.isTraceEnabled() : traceEnabled;
    }

    @Override
    public void warn( final I18n message,
                      final Object... params ) {
        if ( !warnEnabled() || message == null ) return;
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
        if ( !warnEnabled() ) return;
        if ( t == null ) {
            warn( message, params );
            return;
        }
        if ( message == null ) {
            logger.warn( null, t );
            return;
        }
        logger.warn( message.text( locale(), params ), t );
    }

    @Override
    public void warn( final Throwable t,
                      final String message,
                      final Object... arguments ) {
        if ( !warnEnabled() ) return;
        if ( t == null ) {
            warn( message, arguments );
            return;
        }
        if ( message == null ) {
            logger.warn( null, t );
            return;
        }
        logger.warn( I18n.localize( i18nClass, locale(), message, arguments ), t );
    }

    @Override
    public boolean warnEnabled() {
        return warnEnabled == null ? logger.isWarnEnabled() : warnEnabled;
    }

}
