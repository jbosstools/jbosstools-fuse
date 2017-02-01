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

import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A simple logging interface that is fully compatible with multiple logging implementations. If no specific logging implementation
 * is found, then it defaults to the JDK Logger implementation.
 */
public abstract class Logger {

    private static final AtomicReference< Locale > LOCALE = new AtomicReference<>( null );

    /**
     * Get the locale used for the logs. If null, the {@link Locale#getDefault() default locale} is used.
     *
     * @return the current locale used for logging, or null if the system locale is used
     * @see #setLocale(Locale)
     */
    public static Locale locale() {
        return LOCALE.get();
    }

    /**
     * Return a logger named corresponding to the class passed as argument.
     *
     * @param i18nClass
     *        the internationalization class used to localize logged messages
     * @return logger
     */
    public static Logger logger( final Class< ? > i18nClass ) {
        return LogFactory.logFactory().logger( i18nClass, LogFactory.logFactory().context() );
    }

    /**
     * Return a logger named according to the name argument.
     *
     * @param i18nClass
     *        the internationalization class used to localize logged messages
     * @param context
     *        The context of the logger.
     * @return logger
     */
    public static Logger logger( final Class< ? > i18nClass,
                                 final String context ) {
        return LogFactory.logFactory().logger( i18nClass, context );
    }

    /**
     * Set the locale used for the logs. This should be used when the logs are to be written is a specific locale, independent of
     * the {@link Locale#getDefault() default locale}. To use the default locale, call this method with a null value.
     *
     * @param locale
     *        the desired locale to use for the logs, or null if the system locale should be used
     * @return the previous locale
     * @see #locale()
     */
    public static Locale setLocale( final Locale locale ) {
        return LOCALE.getAndSet( locale != null ? locale : Locale.getDefault() );
    }

    /**
     * Log a message at the DEBUG level according to the specified format and (optional) arguments. The message should contain a
     * pair of empty curly braces for each of the argument, which should be passed in the correct order. This method is efficient
     * and avoids superfluous object creation when the logger is disabled for the DEBUG level.
     *
     * @param message
     *        the message string
     * @param arguments
     *        the argument values that are to replace the variables in the format string
     */
    public abstract void debug( String message,
                                Object... arguments );

    /**
     * Log an exception (throwable) at the DEBUG level with an accompanying message. If the exception is null, then this method
     * calls {@link #debug(String, Object...)}.
     *
     * @param t
     *        the exception (throwable) to log
     * @param message
     *        the message accompanying the exception
     * @param arguments
     *        the argument values that are to replace the variables in the format string
     */
    public abstract void debug( Throwable t,
                                String message,
                                Object... arguments );

    /**
     * Return whether messages at the DEBUG level are being logged.
     *
     * @return true if DEBUG log messages are currently being logged, or false otherwise.
     */
    public abstract boolean debugEnabled();

    /**
     * Log the supplied localizable message as an {@link Level#ERROR error}, after first localizing the message and replacing any
     * variables in the message with the optional arguments in the same manner as described by
     * {@link String#format(String, Object...)}.
     *
     * @param message
     *        the message string
     * @param arguments
     *        the argument values that are to replace the variables in the format string
     */
    public abstract void error( I18n message,
                                Object... arguments );

    /**
     * Log the supplied localizable message as an {@link Level#ERROR error}, after first localizing the message and replacing any
     * variables in the message with the optional arguments in the same manner as described by
     * {@link String#format(String, Object...)}.
     *
     * @param message
     *        the message string
     * @param arguments
     *        the argument values that are to replace the variables in the format string
     */
    public abstract void error( String message,
                                Object... arguments );

    /**
     * Log the supplied throwable and localizable message as an {@link Level#ERROR error}, after first localizing the message and
     * replacing any variables in the message with the optional arguments in the same manner as described by
     * {@link String#format(String, Object...)}.
     *
     * @param t
     *        the exception (throwable) to log
     * @param message
     *        the message accompanying the exception
     * @param arguments
     *        the argument values that are to replace the variables in the format string
     */
    public abstract void error( Throwable t,
                                I18n message,
                                Object... arguments );

    /**
     * Log the supplied throwable and localizable message as an {@link Level#ERROR error}, after first localizing the message and
     * replacing any variables in the message with the optional arguments in the same manner as described by
     * {@link String#format(String, Object...)}.
     *
     * @param t
     *        the throwable to log
     * @param message
     *        the <em>unlocalized</em> message accompanying the exception
     * @param arguments
     *        optional argument values that are to replace variables in the message
     */
    public abstract void error( Throwable t,
                                String message,
                                Object... arguments );

    /**
     * Return whether messages at the ERROR level are being logged.
     *
     * @return true if ERROR log messages are currently being logged, or false otherwise.
     */
    public abstract boolean errorEnabled();

    /**
     * Return the name of this logger instance.
     *
     * @return the logger's name
     */
    public abstract String getName();

    /**
     * Log the supplied localizable message as {@link Level#INFO informational}, after first localizing the message and replacing
     * any variables in the message with the optional arguments in the same manner as described by
     * {@link String#format(String, Object...)}.
     *
     * @param message
     *        the message string
     * @param arguments
     *        the argument values that are to replace the variables in the format string
     */
    public abstract void info( I18n message,
                               Object... arguments );

    /**
     * Log the supplied localizable message as {@link Level#INFO informational}, after first localizing the message and replacing
     * any variables in the message with the optional arguments in the same manner as described by
     * {@link String#format(String, Object...)}.
     *
     * @param message
     *        the message string
     * @param arguments
     *        the argument values that are to replace the variables in the format string
     */
    public abstract void info( String message,
                               Object... arguments );

    /**
     * Log the supplied throwable and localizable message as {@link Level#INFO informational}, after first localizing the message
     * and replacing any variables in the message with the optional arguments in the same manner as described by
     * {@link String#format(String, Object...)}.
     *
     * @param t
     *        the exception (throwable) to log
     * @param message
     *        the message accompanying the exception
     * @param arguments
     *        the argument values that are to replace the variables in the format string
     */
    public abstract void info( Throwable t,
                               I18n message,
                               Object... arguments );

    /**
     * Log the supplied throwable and localizable message as {@link Level#INFO informational}, after first localizing the message
     * and replacing any variables in the message with the optional arguments in the same manner as described by
     * {@link String#format(String, Object...)}.
     *
     * @param t
     *        the exception (throwable) to log
     * @param message
     *        the message accompanying the exception
     * @param arguments
     *        the argument values that are to replace the variables in the format string
     */
    public abstract void info( Throwable t,
                               String message,
                               Object... arguments );

    /**
     * Return whether messages at the INFORMATION level are being logged.
     *
     * @return true if INFORMATION log messages are currently being logged, or false otherwise.
     */
    public abstract boolean infoEnabled();

    /**
     * Log a message at the supplied level according to the specified format and (optional) arguments. The message should contain a
     * pair of empty curly braces for each of the argument, which should be passed in the correct order. This method is efficient
     * and avoids superfluous object creation when the logger is disabled for the desired level.
     *
     * @param level
     *        the level at which to log
     * @param message
     *        the (localized) message string
     * @param arguments
     *        the argument values that are to replace the variables in the format string
     */
    public void log( final Level level,
                     final I18n message,
                     final Object... arguments ) {
        if ( message == null ) return;
        switch ( level ) {
            case DEBUG:
                debug( message.text( LOCALE.get(), arguments ) );
                break;
            case ERROR:
                error( message, arguments );
                break;
            case INFO:
                info( message, arguments );
                break;
            case TRACE:
                trace( message.text( LOCALE.get(), arguments ) );
                break;
            case WARN:
                warn( message, arguments );
                break;
            case OFF:
                break;
        }
    }

    /**
     * Log an exception (throwable) at the supplied level with an accompanying message. If the exception is null, then this method
     * calls {@link #debug(String, Object...)}.
     *
     * @param level
     *        the level at which to log
     * @param t
     *        the exception (throwable) to log
     * @param message
     *        the message accompanying the exception
     * @param arguments
     *        the argument values that are to replace the variables in the format string
     */
    public void log( final Level level,
                     final Throwable t,
                     final I18n message,
                     final Object... arguments ) {
        if ( message == null ) return;
        switch ( level ) {
            case DEBUG:
                debug( t, message.text( LOCALE.get(), arguments ) );
                break;
            case ERROR:
                error( t, message, arguments );
                break;
            case INFO:
                info( t, message, arguments );
                break;
            case TRACE:
                trace( t, message.text( LOCALE.get(), arguments ) );
                break;
            case WARN:
                warn( t, message, arguments );
                break;
            case OFF:
                break;
        }
    }

    /**
     * @param level
     *        a log level
     */
    public abstract void setLevel( Level level );

    /**
     * Log a message at the TRACE level according to the specified format and (optional) arguments. The message should contain a
     * pair of empty curly braces for each of the argument, which should be passed in the correct order. This method is efficient
     * and avoids superfluous object creation when the logger is disabled for the TRACE level.
     *
     * @param message
     *        the message string
     * @param arguments
     *        the argument values that are to replace the variables in the format string
     */
    public abstract void trace( String message,
                                Object... arguments );

    /**
     * Log an exception (throwable) at the TRACE level with an accompanying message. If the exception is null, then this method
     * calls {@link #trace(String, Object...)}.
     *
     * @param t
     *        the exception (throwable) to log
     * @param message
     *        the message accompanying the exception
     * @param arguments
     *        the argument values that are to replace the variables in the format string
     */
    public abstract void trace( Throwable t,
                                String message,
                                Object... arguments );

    /**
     * Return whether messages at the TRACE level are being logged.
     *
     * @return true if TRACE log messages are currently being logged, or false otherwise.
     */
    public abstract boolean traceEnabled();

    /**
     * Log the supplied localizable message as a {@link Level#WARN warning}, after first localizing the message and replacing any
     * variables in the message with the optional arguments in the same manner as described by
     * {@link String#format(String, Object...)}.
     *
     * @param message
     *        the message string
     * @param arguments
     *        the argument values that are to replace the variables in the format string
     */
    public abstract void warn( I18n message,
                               Object... arguments );

    /**
     * Log the supplied localizable message as a {@link Level#WARN warning}, after first localizing the message and replacing any
     * variables in the message with the optional arguments in the same manner as described by
     * {@link String#format(String, Object...)}.
     *
     * @param message
     *        the message string
     * @param arguments
     *        the argument values that are to replace the variables in the format string
     */
    public abstract void warn( String message,
                               Object... arguments );

    /**
     * Log the supplied throwable and localizable message as a {@link Level#WARN warning}, after first localizing the message and
     * replacing any variables in the message with the optional arguments in the same manner as described by
     * {@link String#format(String, Object...)}.
     *
     * @param t
     *        the exception (throwable) to log
     * @param message
     *        the message accompanying the exception
     * @param arguments
     *        the argument values that are to replace the variables in the format string
     */
    public abstract void warn( Throwable t,
                               I18n message,
                               Object... arguments );

    /**
     * Log the supplied throwable and localizable message as a {@link Level#WARN warning}, after first localizing the message and
     * replacing any variables in the message with the optional arguments in the same manner as described by
     * {@link String#format(String, Object...)}.
     *
     * @param t
     *        the exception (throwable) to log
     * @param message
     *        the message accompanying the exception
     * @param arguments
     *        the argument values that are to replace the variables in the format string
     */
    public abstract void warn( Throwable t,
                               String message,
                               Object... arguments );

    /**
     * Return whether messages at the WARN level are being logged.
     *
     * @return true if WARN log messages are currently being logged, or false otherwise.
     */
    public abstract boolean warnEnabled();

    /**
     *
     */
    public enum Level {

        /**
         *
         */
        OFF,

        /**
         *
         */
        ERROR,

        /**
         *
         */
        WARN,

        /**
         *
         */
        INFO,

        /**
         *
         */
        DEBUG,

        /**
         *
         */
        TRACE
    }
}
