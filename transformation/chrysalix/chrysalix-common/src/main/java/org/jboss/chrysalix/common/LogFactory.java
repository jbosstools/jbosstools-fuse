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

import org.jboss.chrysalix.common.logging.JdkLoggerFactory;
import org.jboss.chrysalix.common.logging.Log4jLoggerFactory;
import org.jboss.chrysalix.common.logging.SLF4JLoggerFactory;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.NotFoundException;

/**
 * The abstract class for the LogFactory, which is called to create a specific implementation of the {@link Logger}.
 * <p>
 * Several LogFactory implementations are provided out-of-the-box that work with common log frameworks:
 * <ol>
 * <li>SLF4J (which sits atop several logging frameworks)</li>
 * <li>Log4J</li>
 * <li>JDK Util Logging</li>
 * </ol>
 * The static initializer for this class checks the classpath for the availability of these frameworks, and as soon as one is found
 * the LogFactory implementation for that framework is instantiated and used for all logging.
 * </p>
 * <p>
 * However, since this facility can be embedded into any application, it is possible that applications use a logging framework other
 * than those listed above. So before falling back to the JDK logging, this facility looks for the
 * <code>org.jboss.chrysalix.common.logging.CustomLoggerFactory</code> class, and if found attempts to instantiate and use it. But this
 * facility does not provide this class out of the box; rather an application that is embedding this facility can provide its own
 * version of that class that should extend {@link LogFactory} and create an appropriate implementation of {@link Logger} that
 * forwards log messages to the application's logging framework.
 * </p>
 */
public abstract class LogFactory {

    /**
     * The name of the {@link LogFactory} implementation that is not provided out of the box but can be created, implemented, and
     * placed on the classpath to have this facility send log messages to a custom framework.
     */
    public static final String CUSTOM_LOG_FACTORY_CLASSNAME = "org.jboss.chrysalix.common.logging.CustomLoggerFactory";

    private static LogFactory LOGFACTORY;

    static {
        if ( customLoggerAvailable() ) try {
            @SuppressWarnings( "unchecked" ) final Class< LogFactory > customClass =
                ( Class< LogFactory > ) Class.forName( CUSTOM_LOG_FACTORY_CLASSNAME );
            LOGFACTORY = customClass.newInstance();
        } catch ( final Throwable e ) {
            // We're going to fallback to the JDK logger anyway, so use it and log this problem ...
            LOGFACTORY = new JdkLoggerFactory();
            final java.util.logging.Logger jdkLogger = java.util.logging.Logger.getLogger( LogFactory.class.getName() );
            final String msg = CommonI18n.localize( "Error loading and/or instantiating the \"%s\" implementation, which is used "
                                                    + "to tie into a custom logging framework (other than SLF4J, Log4J or the JDK "
                                                    + "Logging). Falling back to JDK logging.",
                                                    CUSTOM_LOG_FACTORY_CLASSNAME );
            jdkLogger.log( java.util.logging.Level.WARNING, msg, e );
        }
        else if ( slf4jAvailable() ) LOGFACTORY = new SLF4JLoggerFactory();
        else if ( log4jAvailable() ) LOGFACTORY = new Log4jLoggerFactory();
        else LOGFACTORY = new JdkLoggerFactory();
    }

    private static boolean customLoggerAvailable() {
        try {
            // Check if a custom log factory implementation is in the classpath and initialize the class
            Class.forName( CUSTOM_LOG_FACTORY_CLASSNAME );
            return true;
        } catch ( final ClassNotFoundException e ) {
            return false;
        }
    }

    private static boolean log4jAvailable() {
        try {
            // Check if the Log4J main interface is in the classpath and initialize the class
            Class.forName( "org.apache.log4j.Logger" );
            return true;
        } catch ( final ClassNotFoundException e ) {
            return false;
        }
    }

    static LogFactory logFactory() {
        return LOGFACTORY;
    }

    private static boolean slf4jAvailable() {
        try {
            // check if the api is in the classpath and initialize the classes
            Class.forName( "org.slf4j.Logger" );
            Class.forName( "org.slf4j.LoggerFactory" );

            // check if there's at least one implementation and initialize the classes
            Class.forName( "org.slf4j.impl.StaticLoggerBinder" );
            return true;
        } catch ( final ClassNotFoundException e ) {
            return false;
        }
    }

    String context() {
        try {
            final StackTraceElement trace = Thread.currentThread().getStackTrace()[ 3 ];
            final CtClass ctClass = ClassPool.getDefault().get( trace.getClassName() );
            final CtConstructor classInitializer = ctClass.getClassInitializer();
            if ( classInitializer != null && trace.getMethodName().equals( classInitializer.getName() ) )
                return classInitializer.getLongName();
            CtMethod ctMethod = null;
            int delta = Short.MAX_VALUE;
            // // jpav: remove
            // System.out.println( trace.getMethodName() );
            // // jpav: remove
            // System.out.println( ctClass.getClassInitializer().getName() );
            for ( final CtMethod method : ctClass.getDeclaredMethods() ) {
                final int methodDelta = trace.getLineNumber() - method.getMethodInfo().getLineNumber( 0 );
                if ( methodDelta >= 0 && methodDelta < delta ) {
                    delta = methodDelta;
                    ctMethod = method;
                }
            }
            if ( ctMethod == null )
                throw new RuntimeException();
            return ctMethod.getLongName();
        } catch ( final NotFoundException e ) {
            throw new RuntimeException( e );
        }
    }

    /**
     * Return a logger for the context identified by the calling method
     *
     * @param i18nClass
     *        the internationalization class used to localize logged messages
     * @return logger
     */
    Logger logger( final Class< ? > i18nClass ) {
        return logger( i18nClass, context() );
    }

    /**
     * Return a logger for the supplied context
     *
     * @param i18nClass
     *        the internationalization class used to localize logged messages
     * @param context
     *        The context of the logger.
     * @return logger
     */
    protected abstract Logger logger( Class< ? > i18nClass,
                                      String context );

}
