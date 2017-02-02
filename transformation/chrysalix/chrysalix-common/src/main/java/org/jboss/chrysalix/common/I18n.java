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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.IllegalFormatException;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;
import org.jboss.chrysalix.common.i18n.ClasspathLocalizationRepository;

/**
 * An internalized string object, which manages the initialization of internationalization (i18n) files, substitution of values
 * within i18n message placeholders, and dynamically reading properties from i18n property files.
 */
public final class I18n {

    private static final Logger LOGGER = Logger.logger( CommonI18n.class );

    private static String i18nBundleNotFoundInClasspath =
        "None of the bundle variants for %s in locale \"%s\" can be located in the classpath: %s";
    private static String i18nUsingUsLocale = "Using default U.S. localization for %s";

    /**
     * The first level of this map indicates whether an i18n class has been localized to a particular locale. The second level
     * contains any problems encountered during localization.
     */
    static final ConcurrentMap< Locale, Map< Class< ? >, Set< String >>> LOCALE_TO_CLASS_TO_PROBLEMS_MAP =
        new ConcurrentHashMap<>();

    private static final ConcurrentMap< String, I18n > I18NS_BY_TEXT = new ConcurrentHashMap<>();

    /**
     * Note, calling this method will <em>not</em> trigger localization of the supplied internationalization class.
     *
     * @param i18nClass
     *        The internalization class for which localization problem locales should be returned.
     * @return The locales for which localization problems were encountered while localizing the supplied internationalization
     *         class; never <code>null</code>.
     */
    public static Set< Locale > localizationProblemLocales( final Class< ? > i18nClass ) {
        Arg.notNull( i18nClass, "i18nClass" );
        final Set< Locale > locales = new HashSet<>( LOCALE_TO_CLASS_TO_PROBLEMS_MAP.size() );
        for ( final Entry< Locale, Map< Class< ? >, Set< String >>> localeEntry : LOCALE_TO_CLASS_TO_PROBLEMS_MAP.entrySet() ) {
            for ( final Entry< Class< ? >, Set< String >> classEntry : localeEntry.getValue().entrySet() ) {
                if ( !classEntry.getValue().isEmpty() ) {
                    locales.add( localeEntry.getKey() );
                    break;
                }
            }
        }
        return locales;
    }

    /**
     * Note, calling this method will <em>not</em> trigger localization of the supplied internationalization class.
     *
     * @param i18nClass
     *        The internalization class for which localization problems should be returned.
     * @return The localization problems encountered while localizing the supplied internationalization class to the default locale;
     *         never <code>null</code>.
     */
    public static Set< String > localizationProblems( final Class< ? > i18nClass ) {
        return localizationProblems( i18nClass, Locale.getDefault() );
    }

    /**
     * Note, calling this method will <em>not</em> trigger localization of the supplied internationalization class.
     *
     * @param i18nClass
     *        The internalization class for which localization problems should be returned.
     * @param locale
     *        The locale for which localization problems should be returned. If <code>null</code>, the default locale will be used.
     * @return The localization problems encountered while localizing the supplied internationalization class to the supplied
     *         locale; never <code>null</code>.
     */
    public static Set< String > localizationProblems( final Class< ? > i18nClass,
                                                      final Locale locale ) {
        Arg.notNull( i18nClass, "i18nClass" );
        final Map< Class< ? >, Set< String >> classToProblemsMap =
            LOCALE_TO_CLASS_TO_PROBLEMS_MAP.get( locale == null ? Locale.getDefault() : locale );
        if ( classToProblemsMap == null ) {
            return Collections.emptySet();
        }
        final Set< String > problems = classToProblemsMap.get( i18nClass );
        if ( problems == null ) {
            return Collections.emptySet();
        }
        return problems;
    }

    /**
     * Synchronized on the supplied internationalization class.
     *
     * @param i18nClass
     *        The internationalization class being localized
     * @param locale
     *        The locale to which the supplied internationalization class should be localized.
     * @return the resulting locale, which will be the {@link Locale#US U.S. Locale} even if the supplied locale is different, but
     *         no bundle is found
     */
    private static Locale localize( final Class< ? > i18nClass,
                                    final Locale locale ) {
        assert i18nClass != null;
        assert locale != null;
        // Create a class-to-problem map for this locale if one doesn't exist, else get the existing one.
        Map< Class< ? >, Set< String >> classToProblemsMap = new ConcurrentHashMap<>();
        final Map< Class< ? >, Set< String >> existingClassToProblemsMap =
            LOCALE_TO_CLASS_TO_PROBLEMS_MAP.putIfAbsent( locale,
                                                         classToProblemsMap );
        if ( existingClassToProblemsMap != null ) {
            classToProblemsMap = existingClassToProblemsMap;
        }
        // Check if already localized outside of synchronization block for 99% use-case
        if ( classToProblemsMap.get( i18nClass ) != null ) {
            return locale;
        }
        synchronized ( i18nClass ) {
            // Return if the supplied i18n class has already been localized to the supplied locale, despite the check outside of
            // the synchronization block (1% use-case), else create a class-to-problems map for the class.
            Set< String > problems = classToProblemsMap.get( i18nClass );
            if ( problems == null ) {
                problems = new CopyOnWriteArraySet<>();
                classToProblemsMap.put( i18nClass, problems );
            } else {
                return locale;
            }
            // Get the URL to the localization properties file ...
            final String localizationBaseName = i18nClass.getName();
            URL bundleUrl;
            if ( Locale.US.equals( locale ) )
            // If US English locale, then use values already defined in constants
            bundleUrl = null;
            else {
                bundleUrl =
                    ClasspathLocalizationRepository.getLocalizationBundle( i18nClass.getClassLoader(), localizationBaseName, locale );
                if ( bundleUrl == null ) {
                    LOGGER.warn( i18nBundleNotFoundInClasspath, i18nClass, locale,
                                 ClasspathLocalizationRepository.getPathsToSearchForBundle( localizationBaseName, locale ) );
                    // Nothing was found, so try the default locale
                    final Locale defaultLocale = Locale.getDefault();
                    if ( defaultLocale == Locale.US ) {
                        LOGGER.warn( i18nUsingUsLocale, i18nClass );
                        return Locale.US;
                    }
                    if ( !defaultLocale.equals( locale ) )
                        bundleUrl =
                            ClasspathLocalizationRepository.getLocalizationBundle( i18nClass.getClassLoader(), localizationBaseName, defaultLocale );
                    // Return if no applicable localization file could be found
                    if ( bundleUrl == null ) {
                        LOGGER.warn( i18nBundleNotFoundInClasspath, i18nClass, defaultLocale,
                                     ClasspathLocalizationRepository.getPathsToSearchForBundle( localizationBaseName,
                                                                                                defaultLocale ) );
                        LOGGER.warn( i18nUsingUsLocale, i18nClass );
                        problems.add( CommonI18n.localize( i18nUsingUsLocale, i18nClass ) );
                        return Locale.US;
                    }
                }
                // Initialize i18n map
                final Properties props = prepareBundleLoading( i18nClass, locale, bundleUrl, problems );

                try {
                    try ( InputStream propStream = bundleUrl.openStream() ) {
                        props.load( propStream );
                        // Check for uninitialized fields
                        for ( final Field fld : i18nClass.getDeclaredFields() )
                            if ( fld.getType() == I18n.class )
                                try {
                                    if ( ( ( I18n ) fld.get( null ) ).localeToTextMap.get( locale ) == null ) return Locale.US;
                                } catch ( final IllegalAccessException notPossible ) {
                                    // Would have already occurred in initialize method, but allowing for the impossible...
                                    problems.add( notPossible.getMessage() );
                                }
                    }
                } catch ( final IOException err ) {
                    problems.add( err.getMessage() );
                }
            }
        }
        return locale;
    }

    /**
     * @param i18nClass
     *        the internationalization class used to localize the supplied text.
     * @param locale
     *        the locale, or <code>null</code> if the {@link Locale#getDefault() current (default) locale} should be used
     * @param text
     *        the text to be localized
     * @param arguments
     *        optional arguments applied to the supplied text as described in {@link String#format(String, Object...)}
     * @return the localized form of the supplied text
     */
    public static String localize( final Class< ? > i18nClass,
                                   final Locale locale,
                                   final String text,
                                   final Object... arguments ) {
        I18n i18n = I18NS_BY_TEXT.get( text );
        if ( i18n == null ) {
            i18n = new I18n( text, i18nClass );
            I18NS_BY_TEXT.put( text, i18n );
        }
        return i18n.text( locale, arguments );
    }

    /**
     * @param i18nClass
     *        the internationalization class used to localize the supplied text.
     * @param text
     *        the text to be localized
     * @param arguments
     *        optional arguments applied to the supplied text as described in {@link String#format(String, Object...)}
     * @return the localized form of the supplied text
     */
    public static String localize( final Class< ? > i18nClass,
                                   final String text,
                                   final Object... arguments ) {
        return localize( i18nClass, null, text, arguments );
    }

    private static Properties prepareBundleLoading( final Class< ? > i18nClass,
                                                    final Locale locale,
                                                    final URL bundleUrl,
                                                    final Set< String > problems ) {
        return new Properties() {

            private static final long serialVersionUID = 3920620306881072843L;

            @Override
            public synchronized Object put( final Object key,
                                            final Object value ) {
                try {
                    ( ( I18n ) i18nClass.getDeclaredField( key.toString() ).get( null ) ).localeToTextMap.putIfAbsent( locale,
                                                                                                                       value.toString() );
                } catch ( final IllegalAccessException | NoSuchFieldException | SecurityException notPossible ) {
                    // Would have already occurred in initialize method, but allowing for the impossible...
                    problems.add( notPossible.getMessage() );
                }

                return null;
            }
        };
    }

    private final String id;
    private final Class< ? > i18nClass;
    final ConcurrentHashMap< Locale, String > localeToTextMap = new ConcurrentHashMap<>();
    final ConcurrentHashMap< Locale, String > localeToProblemMap = new ConcurrentHashMap<>();

    /**
     * @param text
     *        the text to be localized
     * @deprecated Use {@link #localize(Class, String, Object...)}
     */
    @Deprecated
    public I18n( final String text ) {
        Arg.notEmpty( text, "text" );
        String id = null;
        Class< ? > i18nClass = null;
        final StackTraceElement elem = Thread.currentThread().getStackTrace()[ 2 ];
        try {
            i18nClass = Class.forName( elem.getClassName() );
            for ( final Field field : i18nClass.getDeclaredFields() ) {
                ClassUtil.makeAccessible( field );
                if ( Modifier.isStatic( field.getModifiers() ) ) {
                    final Object val = field.get( null );
                    if ( val == null ) {
                        id = field.getName();
                        break;
                    }
                }
            }
        } catch ( final ClassNotFoundException | IllegalArgumentException | IllegalAccessException e ) {
            throw new RuntimeException( e );
        }
        if ( id == null )
            throw new IllegalStateException( CommonI18n.localize( "Internationalization object is not assigned to a static member variable\n\tat %s",
                                                                  elem ) );
        this.id = id;
        this.i18nClass = i18nClass;
        localeToTextMap.put( Locale.US, text );
    }

    private I18n( final String text,
                  final Class< ? > i18nClass ) {
        Arg.notEmpty( text, "text" );
        this.id = text;
        this.i18nClass = i18nClass;
        localeToTextMap.put( Locale.US, text );
    }

    /**
     * @return <code>true</code> if a problem was encountered while localizing this internationalization object to the default
     *         locale.
     */
    public boolean hasProblem() {
        return ( problem() != null );
    }

    /**
     * @param locale
     *        The locale for which to check whether a problem was encountered.
     * @return <code>true</code> if a problem was encountered while localizing this internationalization object to the supplied
     *         locale.
     */
    public boolean hasProblem( final Locale locale ) {
        return ( problem( locale ) != null );
    }

    /**
     * @return The problem encountered while localizing this internationalization object to the default locale, or <code>null</code>
     *         if none was encountered.
     */
    public String problem() {
        return problem( null );
    }

    /**
     * @param locale
     *        The locale for which to return the problem.
     * @return The problem encountered while localizing this internationalization object to the supplied locale, or
     *         <code>null</code> if none was encountered.
     */
    public String problem( Locale locale ) {
        if ( locale == null ) {
            locale = Locale.getDefault();
        }
        locale = localize( i18nClass, locale );
        // Check for field/property error
        String problem = localeToProblemMap.get( locale );
        if ( problem != null ) {
            return problem;
        }
        // Check if text exists
        if ( localeToTextMap.get( locale ) != null ) {
            // If so, no problem exists
            return null;
        }
        // If we get here, which will be at most once, there was at least one global localization error, so just return a message
        // indicating to look them up.
        problem = CommonI18n.localize( locale,
                                       "Problems were encountered while localizing internationalization %s to locale \"%s\"",
                                       i18nClass, locale );
        localeToProblemMap.put( locale, problem );
        return problem;
    }

    private String rawText( Locale locale ) throws CommonException {
        assert locale != null;
        locale = localize( i18nClass, locale );
        // Check if text exists
        final String text = localeToTextMap.get( locale );
        if ( text != null ) {
            return text;
        }
        // If not, there was a problem, so throw it within an exception so upstream callers can tell the difference between normal
        // text and problem text.
        throw new CommonException( problem( locale ) );
    }

    /**
     * Get the localized text for the supplied locale, replacing the parameters in the text with those supplied.
     *
     * @param locale
     *        the locale, or <code>null</code> if the {@link Locale#getDefault() current (default) locale} should be used
     * @param arguments
     *        the arguments for the parameter replacement; may be <code>null</code> or empty
     * @return the localized text
     */
    public String text( final Locale locale,
                        final Object... arguments ) {
        try {
            final String rawText = rawText( locale == null ? Locale.getDefault() : locale );
            return String.format( rawText, arguments );
        } catch ( final IllegalFormatException err ) {
            throw new IllegalArgumentException( CommonI18n.localize( "Internationalization field \"%s\" in %s: %s",
                                                                     id, i18nClass, err.getMessage() ) );
        } catch ( final CommonException err ) {
            return '<' + err.getMessage() + '>';
        }
    }

    /**
     * Get the localized text for the {@link Locale#getDefault() current (default) locale}, replacing the parameters in the text
     * with those supplied.
     *
     * @param arguments
     *        the arguments for the parameter replacement; may be <code>null</code> or empty
     * @return the localized text
     */
    public String text( final Object... arguments ) {
        return text( null, arguments );
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        try {
            return rawText( Locale.getDefault() );
        } catch ( final CommonException err ) {
            return '<' + err.getMessage() + '>';
        }
    }
}
