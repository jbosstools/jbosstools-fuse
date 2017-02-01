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
 * Utility class that checks arguments to methods. This class is to be used only in API methods, where failure to supply correct
 * arguments should result in a useful error message. In all cases, use the <code>assert</code> statement.
 */
public final class Arg {

    // public static I18n argumentDidNotContainKey = new I18n( "" );
    // public static I18n argumentDidNotContainObject = new I18n( "" );
    private static String argumentMustNotBeEmpty = "The \"%s\" argument must not be empty";

    // public static I18n argumentMayNotBeGreaterThan = new I18n( "" );
    // public static I18n argumentMayNotBeLessThan = new I18n( "" );
    private static String argumentMustNotBeNegative = "The \"%s\" argument's value, \"%s\", must not be negative";
    private static String argumentMustNotBeNull = "The \"%s\" argument must not be null";

    // public static I18n argumentMayNotBePositive = new I18n( "" );
    private static String argumentMustNotBeZeroLength = "The \"%s\" argument must not be zero-length";

    // public static I18n argumentMayNotContainNullValue = new I18n( "" );
    // public static I18n argumentMustBeEmpty = new I18n( "" );
    // public static I18n argumentMustBeEquals = new I18n( "" );
    // public static I18n argumentMustBeGreaterThan = new I18n( "" );
    // public static I18n argumentMustBeGreaterThanOrEqualTo = new I18n( "" );
    // public static I18n argumentMustBeInstanceOf = new I18n( "" );
    // public static I18n argumentMustBeLessThan = new I18n( "" );
    // public static I18n argumentMustBeLessThanOrEqualTo = new I18n( "" );
    // public static I18n argumentMustBeNegative = new I18n( "" );
    // public static I18n argumentMustBeNull = new I18n( "" );
    // public static I18n argumentMustBeNumber = new I18n( "" );
    // public static I18n argumentMustBeOfMaximumSize = new I18n( "" );
    // public static I18n argumentMustBeOfMinimumSize = new I18n( "" );
    // public static I18n argumentMustBePositive = new I18n( "" );
    // public static I18n argumentMustBeSameAs = new I18n( "" );
    // public static I18n argumentMustNotBeEquals = new I18n( "" );
    // public static I18n argumentMustNotBeSameAs = new I18n( "" );
    // argumentDidNotContainKey = "The {0} argument did not contain the expected key {1}
    // argumentDidNotContainObject = "The {0} argument did not contain the expected object {1}
    // argumentMayNotBeGreaterThan = The {0} argument value, {1}, may not be greater than {2}
    // argumentMayNotBeLessThan = The {0} argument value, {1}, may not be less than {2}
    // argumentMustNotBeNegative = The {0} argument value, {1}, may not be negative
    // argumentMayNotBePositive = The {0} argument value, {1}, may not be positive
    // argumentMayNotContainNullValue = The {0} argument may not contain a null value (first null found at position {1})
    // argumentMustBeEmpty = The {0} argument must be empty.
    // argumentMustBeEquals = The {0} argument is not equal to {1}
    // argumentMustBeGreaterThan = The {0} argument value, {1}, must be greater than {2}
    // argumentMustBeGreaterThanOrEqualTo = The {0} argument value, {1}, must be greater than or equal to {2}
    // argumentMustBeInstanceOf = The {0} argument was an instance of {1} but was expected to be an instance of {2}
    // argumentMustBeLessThan = The {0} argument value, {1}, must be less than {2}
    // argumentMustBeLessThanOrEqualTo = The {0} argument value, {1}, must be less than or equal to {2}
    // argumentMustBeNegative = The {0} argument value, {1}, must be negative
    // argumentMustBeNull = The {0} argument must be null
    // argumentMustBeNumber = The {0} argument value must be a number
    // argumentMustBeOfMaximumSize = The {0} argument is a {1} with {2} elements but must have no more than {3}
    // argumentMustBeOfMinimumSize = The {0} argument is a {1} with {2} elements but must have at least {3}
    // argumentMustBePositive = The {0} argument value, {1}, must be positive
    // argumentMustBeSameAs = The {0} argument is not the same as "{1}"
    // argumentMustNotBeEquals = The {0} argument is equal to {1}
    // argumentMustNotBeSameAs = The {0} argument is the same as "{1}"

    // /**
    // * Check that the collection contains the value
    // *
    // * @param argument
    // * Collection to check
    // * @param value
    // * Value to check for, may be null
    // * @param name
    // * The name of the argument
    // * @throws IllegalArgumentException
    // * If collection is null or doesn't contain value
    // */
    // public static void contains( final Collection< ? > argument,
    // final Object value,
    // final String name ) {
    // notNull( argument, name );
    // if ( !argument.contains( value ) )
    // throw new IllegalArgumentException( CommonI18n.argumentDidNotContainObject.text( name, getObjectName( value ) ) );
    // }
    //
    // /**
    // * Check that the map contains the key
    // *
    // * @param argument
    // * Map to check
    // * @param key
    // * Key to check for, may be null
    // * @param name
    // * The name of the argument
    // * @throws IllegalArgumentException
    // * If map is null or doesn't contain key
    // */
    // public static void containsKey( final Map< ?, ? > argument,
    // final Object key,
    // final String name ) {
    // notNull( argument, name );
    // if ( !argument.containsKey( key ) )
    // throw new IllegalArgumentException( CommonI18n.argumentDidNotContainKey.text( name, getObjectName( key ) ) );
    // }
    //
    // /**
    // * Check that the collection is not null and contains no nulls
    // *
    // * @param argument
    // * Array
    // * @param name
    // * The name of the argument
    // * @throws IllegalArgumentException
    // * If array is null or has null values
    // */
    // public static void containsNoNulls( final Iterable< ? > argument,
    // final String name ) {
    // notNull( argument, name );
    // int i = 0;
    // for ( final Object object : argument ) {
    // if ( object == null ) throw new IllegalArgumentException( CommonI18n.argumentMayNotContainNullValue.text( name, i ) );
    // ++i;
    // }
    // }
    //
    // /**
    // * Check that the array is not null and contains no nulls
    // *
    // * @param argument
    // * Array
    // * @param name
    // * The name of the argument
    // * @throws IllegalArgumentException
    // * If array is null or has null values
    // */
    // public static void containsNoNulls( final Object[] argument,
    // final String name ) {
    // notNull( argument, name );
    // int i = 0;
    // for ( final Object object : argument ) {
    // if ( object == null ) throw new IllegalArgumentException( CommonI18n.argumentMayNotContainNullValue.text( name, i ) );
    // ++i;
    // }
    // }
    //
    // /**
    // * Checks that the object is an instance of the specified Class and then returns the object cast to the specified Class
    // *
    // * @param <C>
    // * the class type
    // * @param argument
    // * Value
    // * @param expectedClass
    // * Class
    // * @param name
    // * The name of the argument
    // * @return value cast to the specified Class
    // * @throws IllegalArgumentException
    // * If value is not an instance of theClass.
    // */
    // // due to cast in return
    // public static < C > C getInstanceOf( final Object argument,
    // final Class< C > expectedClass,
    // final String name ) {
    // isInstanceOf( argument, expectedClass, name );
    // return expectedClass.cast( argument );
    // }
    //
    // /**
    // * Returns the specified argument if it is not <code>null</code>.
    // *
    // * @param <T>
    // * any type
    // * @param argument
    // * The argument
    // * @param name
    // * The name of the argument
    // * @return The argument
    // * @throws IllegalArgumentException
    // * If argument is <code>null</code>
    // */
    // public static < T > T getNotNull( final T argument,
    // final String name ) {
    // notNull( argument, name );
    // return argument;
    // }
    //
    // /**
    // * @param object
    // * any object
    // * @return the text form of the supplied object
    // */
    // protected static String getObjectName( final Object object ) {
    // return object == null ? null : "'" + object.toString() + "'";
    // }
    //
    // /**
    // * Check that the collection contains at least the supplied number of elements
    // *
    // * @param argument
    // * Collection
    // * @param minimumSize
    // * the minimum size
    // * @param name
    // * The name of the argument
    // * @throws IllegalArgumentException
    // * If collection has a size smaller than the supplied value
    // */
    // public static void hasSizeOfAtLeast( final Collection< ? > argument,
    // final int minimumSize,
    // final String name ) {
    // notNull( argument, name );
    // if ( argument.size() < minimumSize )
    // throw new IllegalArgumentException( CommonI18n.argumentMustBeOfMinimumSize.text( name,
    // Collection.class.getSimpleName(),
    // argument.size(),
    // minimumSize ) );
    // }
    //
    // /**
    // * Check that the map contains at least the supplied number of entries
    // *
    // * @param argument
    // * the map
    // * @param minimumSize
    // * the minimum size
    // * @param name
    // * The name of the argument
    // * @throws IllegalArgumentException
    // * If the map has a size smaller than the supplied value
    // */
    // public static void hasSizeOfAtLeast( final Map< ?, ? > argument,
    // final int minimumSize,
    // final String name ) {
    // notNull( argument, name );
    // if ( argument.size() < minimumSize )
    // throw new IllegalArgumentException( CommonI18n.argumentMustBeOfMinimumSize.text( name,
    // Map.class.getSimpleName(),
    // argument.size(),
    // minimumSize ) );
    // }
    //
    // /**
    // * Check that the array contains at least the supplied number of elements
    // *
    // * @param argument
    // * the array
    // * @param minimumSize
    // * the minimum size
    // * @param name
    // * The name of the argument
    // * @throws IllegalArgumentException
    // * If the array has a size smaller than the supplied value
    // */
    // public static void hasSizeOfAtLeast( final Object[] argument,
    // final int minimumSize,
    // final String name ) {
    // notNull( argument, name );
    // if ( argument.length < minimumSize )
    // throw new IllegalArgumentException( CommonI18n.argumentMustBeOfMinimumSize.text( name,
    // Object[].class.getSimpleName(),
    // argument.length,
    // minimumSize ) );
    // }
    //
    // /**
    // * Check that the collection contains no more than the supplied number of elements
    // *
    // * @param argument
    // * Collection
    // * @param maximumSize
    // * the maximum size
    // * @param name
    // * The name of the argument
    // * @throws IllegalArgumentException
    // * If collection has a size smaller than the supplied value
    // */
    // public static void hasSizeOfAtMost( final Collection< ? > argument,
    // final int maximumSize,
    // final String name ) {
    // notNull( argument, name );
    // if ( argument.size() > maximumSize )
    // throw new IllegalArgumentException( CommonI18n.argumentMustBeOfMinimumSize.text( name,
    // Collection.class.getSimpleName(),
    // argument.size(),
    // maximumSize ) );
    // }
    //
    // /**
    // * Check that the map contains no more than the supplied number of entries
    // *
    // * @param argument
    // * the map
    // * @param maximumSize
    // * the maximum size
    // * @param name
    // * The name of the argument
    // * @throws IllegalArgumentException
    // * If the map has a size smaller than the supplied value
    // */
    // public static void hasSizeOfAtMost( final Map< ?, ? > argument,
    // final int maximumSize,
    // final String name ) {
    // notNull( argument, name );
    // if ( argument.size() > maximumSize )
    // throw new IllegalArgumentException( CommonI18n.argumentMustBeOfMinimumSize.text( name,
    // Map.class.getSimpleName(),
    // argument.size(),
    // maximumSize ) );
    // }
    //
    // /**
    // * Check that the array contains no more than the supplied number of elements
    // *
    // * @param argument
    // * the array
    // * @param maximumSize
    // * the maximum size
    // * @param name
    // * The name of the argument
    // * @throws IllegalArgumentException
    // * If the array has a size smaller than the supplied value
    // */
    // public static void hasSizeOfAtMost( final Object[] argument,
    // final int maximumSize,
    // final String name ) {
    // notNull( argument, name );
    // if ( argument.length > maximumSize )
    // throw new IllegalArgumentException( CommonI18n.argumentMustBeOfMinimumSize.text( name,
    // Object[].class.getSimpleName(),
    // argument.length,
    // maximumSize ) );
    // }
    //
    // /**
    // * Check that the array is empty
    // *
    // * @param argument
    // * Array
    // * @param name
    // * The name of the argument
    // * @throws IllegalArgumentException
    // * If array is not empty
    // */
    // public static void isEmpty( final Object[] argument,
    // final String name ) {
    // notNull( argument, name );
    // if ( argument.length > 0 ) throw new IllegalArgumentException( CommonI18n.argumentMustBeEmpty.text( name ) );
    // }
    //
    // /**
    // * Asserts that the specified first object is {@link Object#equals(Object) equal to} the specified second object. This method
    // * does take null references into consideration.
    // *
    // * @param <T>
    // * any type
    // * @param argument
    // * The argument to assert equal to <code>object</code>.
    // * @param argumentName
    // * The name that will be used within the exception message for the argument should an exception be thrown
    // * @param object
    // * The object to assert as equal to <code>argument</code>.
    // * @param objectName
    // * The name that will be used within the exception message for <code>object</code> should an exception be thrown; if
    // * <code>null</code> and <code>object</code> is not <code>null</code>, <code>object.toString()</code> will be used.
    // * @throws IllegalArgumentException
    // * If the specified objects are not equal.
    // */
    // public static < T > void isEquals( final T argument,
    // final String argumentName,
    // final T object,
    // String objectName ) {
    // if ( argument == null ) {
    // if ( object == null ) return;
    // } else if ( argument.equals( object ) ) return;
    // if ( objectName == null ) objectName = getObjectName( object );
    // throw new IllegalArgumentException( CommonI18n.argumentMustBeEquals.text( argumentName, objectName ) );
    // }
    //
    // /**
    // * Check that the argument is greater than the supplied value
    // *
    // * @param argument
    // * The argument
    // * @param greaterThanValue
    // * the value that is to be used to check the value
    // * @param name
    // * The name of the argument
    // * @throws IllegalArgumentException
    // * If argument is not greater than the supplied value
    // */
    // public static void isGreaterThan( final double argument,
    // final double greaterThanValue,
    // final String name ) {
    // if ( argument <= greaterThanValue )
    // throw new IllegalArgumentException( CommonI18n.argumentMustBeGreaterThan.text( name, argument, greaterThanValue ) );
    // }
    //
    // /**
    // * Check that the argument is greater than the supplied value
    // *
    // * @param argument
    // * The argument
    // * @param greaterThanValue
    // * the value that is to be used to check the value
    // * @param name
    // * The name of the argument
    // * @throws IllegalArgumentException
    // * If argument is not greater than the supplied value
    // */
    // public static void isGreaterThan( final int argument,
    // final int greaterThanValue,
    // final String name ) {
    // if ( argument <= greaterThanValue )
    // throw new IllegalArgumentException( CommonI18n.argumentMustBeGreaterThan.text( name, argument, greaterThanValue ) );
    // }
    //
    // /**
    // * Check that the argument is greater than or equal to the supplied value
    // *
    // * @param argument
    // * The argument
    // * @param greaterThanOrEqualToValue
    // * the value that is to be used to check the value
    // * @param name
    // * The name of the argument
    // * @throws IllegalArgumentException
    // * If argument is not greater than or equal to the supplied value
    // */
    // public static void isGreaterThanOrEqualTo( final int argument,
    // final int greaterThanOrEqualToValue,
    // final String name ) {
    // if ( argument < greaterThanOrEqualToValue )
    // throw new IllegalArgumentException( CommonI18n.argumentMustBeGreaterThanOrEqualTo.text( name,
    // argument,
    // greaterThanOrEqualToValue ) );
    // }
    //
    // /**
    // * Check that the object is an instance of the specified Class
    // *
    // * @param argument
    // * Value
    // * @param expectedClass
    // * Class
    // * @param name
    // * The name of the argument
    // * @throws IllegalArgumentException
    // * If value is null
    // */
    // public static void isInstanceOf( final Object argument,
    // final Class< ? > expectedClass,
    // final String name ) {
    // notNull( argument, name );
    // if ( !expectedClass.isInstance( argument ) )
    // throw new IllegalArgumentException( CommonI18n.argumentMustBeInstanceOf.text( name,
    // argument.getClass(),
    // expectedClass.getName() ) );
    // }
    //
    // /**
    // * Check that the argument is less than the supplied value
    // *
    // * @param argument
    // * The argument
    // * @param lessThanValue
    // * the value that is to be used to check the value
    // * @param name
    // * The name of the argument
    // * @throws IllegalArgumentException
    // * If argument is not less than the supplied value
    // */
    // public static void isLessThan( final int argument,
    // final int lessThanValue,
    // final String name ) {
    // if ( argument >= lessThanValue )
    // throw new IllegalArgumentException( CommonI18n.argumentMustBeLessThan.text( name, argument, lessThanValue ) );
    // }
    //
    // /**
    // * Check that the argument is less than or equal to the supplied value
    // *
    // * @param argument
    // * The argument
    // * @param lessThanOrEqualToValue
    // * the value that is to be used to check the value
    // * @param name
    // * The name of the argument
    // * @throws IllegalArgumentException
    // * If argument is not less than or equal to the supplied value
    // */
    // public static void isLessThanOrEqualTo( final int argument,
    // final int lessThanOrEqualToValue,
    // final String name ) {
    // if ( argument > lessThanOrEqualToValue )
    // throw new IllegalArgumentException( CommonI18n.argumentMustBeLessThanOrEqualTo.text( name,
    // argument,
    // lessThanOrEqualToValue ) );
    // }
    //
    // /**
    // * Check that the argument is negative (<0).
    // *
    // * @param argument
    // * The argument
    // * @param name
    // * The name of the argument
    // * @throws IllegalArgumentException
    // * If argument is non-negative (>=0)
    // */
    // public static void isNegative( final double argument,
    // final String name ) {
    // if ( argument >= 0.0 ) throw new IllegalArgumentException( CommonI18n.argumentMustBeNegative.text( name, argument ) );
    // }
    //
    // /**
    // * Check that the argument is negative (<0).
    // *
    // * @param argument
    // * The argument
    // * @param name
    // * The name of the argument
    // * @throws IllegalArgumentException
    // * If argument is non-negative (>=0)
    // */
    // public static void isNegative( final int argument,
    // final String name ) {
    // if ( argument >= 0 ) throw new IllegalArgumentException( CommonI18n.argumentMustBeNegative.text( name, argument ) );
    // }
    //
    // /**
    // * Check that the argument is negative (<0).
    // *
    // * @param argument
    // * The argument
    // * @param name
    // * The name of the argument
    // * @throws IllegalArgumentException
    // * If argument is non-negative (>=0)
    // */
    // public static void isNegative( final long argument,
    // final String name ) {
    // if ( argument >= 0 ) throw new IllegalArgumentException( CommonI18n.argumentMustBeNegative.text( name, argument ) );
    // }
    //
    // /**
    // * Check that the argument is non-negative (>=0).
    // *
    // * @param argument
    // * The argument
    // * @param name
    // * The name of the argument
    // * @throws IllegalArgumentException
    // * If argument is negative (<0)
    // */
    // public static void isNonNegative( final double argument,
    // final String name ) {
    // if ( argument < 0.0 ) throw new IllegalArgumentException( CommonI18n.argumentMayNotBeNegative.text( name, argument ) );
    // }

    /**
     * Check that the argument is non-negative (>=0).
     *
     * @param argument
     *        The argument
     * @param name
     *        The name of the argument
     * @throws IllegalArgumentException
     *         If argument is negative (<0)
     */
    public static void nonNegative(final int argument,
                                   final String name) {
        if (argument < 0) throw new IllegalArgumentException(CommonI18n.localize(argumentMustNotBeNegative, name, argument));
    }

    //
    // /**
    // * Check that the argument is non-negative (>=0).
    // *
    // * @param argument
    // * The argument
    // * @param name
    // * The name of the argument
    // * @throws IllegalArgumentException
    // * If argument is negative (<0)
    // */
    // public static void isNonNegative( final long argument,
    // final String name ) {
    // if ( argument < 0 ) throw new IllegalArgumentException( CommonI18n.argumentMayNotBeNegative.text( name, argument ) );
    // }
    //
    // /**
    // * Check that the argument is non-positive (<=0).
    // *
    // * @param argument
    // * The argument
    // * @param name
    // * The name of the argument
    // * @throws IllegalArgumentException
    // * If argument is positive (>0)
    // */
    // public static void isNonPositive( final double argument,
    // final String name ) {
    // if ( argument > 0.0 ) throw new IllegalArgumentException( CommonI18n.argumentMayNotBePositive.text( name, argument ) );
    // }
    //
    // /**
    // * Check that the argument is non-positive (<=0).
    // *
    // * @param argument
    // * The argument
    // * @param name
    // * The name of the argument
    // * @throws IllegalArgumentException
    // * If argument is positive (>0)
    // */
    // public static void isNonPositive( final int argument,
    // final String name ) {
    // if ( argument > 0 ) throw new IllegalArgumentException( CommonI18n.argumentMayNotBePositive.text( name, argument ) );
    // }
    //
    // /**
    // * Check that the argument is non-positive (<=0).
    // *
    // * @param argument
    // * The argument
    // * @param name
    // * The name of the argument
    // * @throws IllegalArgumentException
    // * If argument is positive (>0)
    // */
    // public static void isNonPositive( final long argument,
    // final String name ) {
    // if ( argument > 0 ) throw new IllegalArgumentException( CommonI18n.argumentMayNotBePositive.text( name, argument ) );
    // }
    //
    // /**
    // * Check that the collection is not empty
    // *
    // * @param argument
    // * Collection
    // * @param name
    // * The name of the argument
    // * @throws IllegalArgumentException
    // * If collection is null or empty
    // */
    // public static void isNotEmpty( final Collection< ? > argument,
    // final String name ) {
    // notNull( argument, name );
    // if ( argument.isEmpty() ) throw new IllegalArgumentException( CommonI18n.argumentMayNotBeEmpty.text( name ) );
    // }
    //
    // /**
    // * Checks that the iterator is not empty, and throws an exception if it is.
    // *
    // * @param argument
    // * the iterator to check
    // * @param name
    // * The name of the argument
    // * @throws IllegalArgumentException
    // * If iterator is empty (i.e., iterator.hasNext() returns false)
    // */
    // public static void isNotEmpty( final Iterator< ? > argument,
    // final String name ) {
    // notNull( argument, name );
    // if ( !argument.hasNext() ) throw new IllegalArgumentException( CommonI18n.argumentMayNotBeEmpty.text( name ) );
    // }
    //
    // /**
    // * Check that the map is not empty
    // *
    // * @param argument
    // * Map
    // * @param name
    // * The name of the argument
    // * @throws IllegalArgumentException
    // * If map is null or empty
    // */
    // public static void isNotEmpty( final Map< ?, ? > argument,
    // final String name ) {
    // notNull( argument, name );
    // if ( argument.isEmpty() ) throw new IllegalArgumentException( CommonI18n.argumentMayNotBeEmpty.text( name ) );
    // }

    /**
     * Check that the array is not empty
     *
     * @param argument
     *        Array
     * @param name
     *        The name of the argument
     * @throws IllegalArgumentException
     *         If array is null or empty
     */
    public static void notEmpty(final Object[] argument,
                                final String name) {
        notNull(argument, name);
        if (argument.length == 0) throw new IllegalArgumentException(CommonI18n.localize(argumentMustNotBeEmpty, name));
    }

    //
    // /**
    // * Asserts that the specified first object is not {@link Object#equals(Object) equal to} the specified second object. This
    // * method does take null references into consideration.
    // *
    // * @param <T>
    // * any type
    // * @param argument
    // * The argument to assert equal to <code>object</code>.
    // * @param argumentName
    // * The name that will be used within the exception message for the argument should an exception be thrown
    // * @param object
    // * The object to assert as equal to <code>argument</code>.
    // * @param objectName
    // * The name that will be used within the exception message for <code>object</code> should an exception be thrown; if
    // * <code>null</code> and <code>object</code> is not <code>null</code>, <code>object.toString()</code> will be used.
    // * @throws IllegalArgumentException
    // * If the specified objects are equals.
    // */
    // public static < T > void isNotEquals( final T argument,
    // final String argumentName,
    // final T object,
    // String objectName ) {
    // if ( argument == null ) {
    // if ( object != null ) return;
    // } else if ( !argument.equals( object ) ) return;
    // if ( objectName == null ) objectName = getObjectName( object );
    // throw new IllegalArgumentException( CommonI18n.argumentMustNotBeEquals.text( argumentName, objectName ) );
    // }
    //
    // /**
    // * Check that the argument is not greater than the supplied value
    // *
    // * @param argument
    // * The argument
    // * @param notGreaterThanValue
    // * the value that is to be used to check the value
    // * @param name
    // * The name of the argument
    // * @throws IllegalArgumentException
    // * If argument is less than or equal to the supplied value
    // */
    // public static void isNotGreaterThan( final int argument,
    // final int notGreaterThanValue,
    // final String name ) {
    // if ( argument > notGreaterThanValue )
    // throw new IllegalArgumentException( CommonI18n.argumentMayNotBeGreaterThan.text( name, argument, notGreaterThanValue ) );
    // }
    //
    // /**
    // * Check that the argument is not less than the supplied value
    // *
    // * @param argument
    // * The argument
    // * @param notLessThanValue
    // * the value that is to be used to check the value
    // * @param name
    // * The name of the argument
    // * @throws IllegalArgumentException
    // * If argument greater than or equal to the supplied vlaue
    // */
    // public static void isNotLessThan( final int argument,
    // final int notLessThanValue,
    // final String name ) {
    // if ( argument < notLessThanValue )
    // throw new IllegalArgumentException( CommonI18n.argumentMayNotBeLessThan.text( name, argument, notLessThanValue ) );
    // }
    //
    // /**
    // * Check that the argument is not NaN.
    // *
    // * @param argument
    // * The argument
    // * @param name
    // * The name of the argument
    // * @throws IllegalArgumentException
    // * If argument is NaN
    // */
    // public static void isNotNan( final double argument,
    // final String name ) {
    // if ( Double.isNaN( argument ) ) throw new IllegalArgumentException( CommonI18n.argumentMustBeNumber.text( name ) );
    // }
    //
    // /**
    // * Asserts that the specified first object is not the same as (==) the specified second object.
    // *
    // * @param <T>
    // * any type
    // * @param argument
    // * The argument to assert as not the same as <code>object</code>.
    // * @param argumentName
    // * The name that will be used within the exception message for the argument should an exception be thrown
    // * @param object
    // * The object to assert as not the same as <code>argument</code>.
    // * @param objectName
    // * The name that will be used within the exception message for <code>object</code> should an exception be thrown; if
    // * <code>null</code> and <code>object</code> is not <code>null</code>, <code>object.toString()</code> will be used.
    // * @throws IllegalArgumentException
    // * If the specified objects are the same.
    // */
    // public static < T > void isNotSame( final T argument,
    // final String argumentName,
    // final T object,
    // String objectName ) {
    // if ( argument == object ) {
    // if ( objectName == null ) objectName = getObjectName( object );
    // throw new IllegalArgumentException( CommonI18n.argumentMustNotBeSameAs.text( argumentName, objectName ) );
    // }
    // }
    //
    // /**
    // * Check that the argument is null
    // *
    // * @param argument
    // * The argument
    // * @param name
    // * The name of the argument
    // * @throws IllegalArgumentException
    // * If value is non-null
    // */
    // public static void isNull( final Object argument,
    // final String name ) {
    // if ( argument != null ) throw new IllegalArgumentException( CommonI18n.argumentMustBeNull.text( name ) );
    // }
    //
    // /**
    // * Check that the argument is positive (>0).
    // *
    // * @param argument
    // * The argument
    // * @param name
    // * The name of the argument
    // * @throws IllegalArgumentException
    // * If argument is non-positive (<=0)
    // */
    // public static void isPositive( final double argument,
    // final String name ) {
    // if ( argument <= 0.0 ) throw new IllegalArgumentException( CommonI18n.argumentMustBePositive.text( name, argument ) );
    // }
    //
    // /**
    // * Check that the argument is positive (>0).
    // *
    // * @param argument
    // * The argument
    // * @param name
    // * The name of the argument
    // * @throws IllegalArgumentException
    // * If argument is non-positive (<=0)
    // */
    // public static void isPositive( final int argument,
    // final String name ) {
    // if ( argument <= 0 ) throw new IllegalArgumentException( CommonI18n.argumentMustBePositive.text( name, argument ) );
    // }
    //
    // /**
    // * Check that the argument is positive (>0).
    // *
    // * @param argument
    // * The argument
    // * @param name
    // * The name of the argument
    // * @throws IllegalArgumentException
    // * If argument is non-positive (<=0)
    // */
    // public static void isPositive( final long argument,
    // final String name ) {
    // if ( argument <= 0 ) throw new IllegalArgumentException( CommonI18n.argumentMustBePositive.text( name, argument ) );
    // }
    //
    // /**
    // * Asserts that the specified first object is the same as (==) the specified second object.
    // *
    // * @param <T>
    // * any type
    // * @param argument
    // * The argument to assert as the same as <code>object</code>.
    // * @param argumentName
    // * The name that will be used within the exception message for the argument should an exception be thrown
    // * @param object
    // * The object to assert as the same as <code>argument</code>.
    // * @param objectName
    // * The name that will be used within the exception message for <code>object</code> should an exception be thrown; if
    // * <code>null</code> and <code>object</code> is not <code>null</code>, <code>object.toString()</code> will be used.
    // * @throws IllegalArgumentException
    // * If the specified objects are not the same.
    // */
    // public static < T > void isSame( final T argument,
    // final String argumentName,
    // final T object,
    // String objectName ) {
    // if ( argument != object ) {
    // if ( objectName == null ) objectName = getObjectName( object );
    // throw new IllegalArgumentException( CommonI18n.argumentMustBeSameAs.text( argumentName, objectName ) );
    // }
    // }

    /**
     * Check that the string is not empty, is not null, and does not contain only whitespace.
     *
     * @param argument
     *        String
     * @param name
     *        The name of the argument
     * @throws IllegalArgumentException
     *         If string is null or empty
     */
    public static void notEmpty(final String argument,
                                final String name) {
        notZeroLength(argument, name);
        if (argument.trim().length() == 0)
            throw new IllegalArgumentException(CommonI18n.localize(argumentMustNotBeEmpty, name));
    }

    /**
     * Check that the specified argument is non-null
     *
     * @param argument
     *        The argument
     * @param name
     *        The name of the argument
     * @throws IllegalArgumentException
     *         If argument is null
     */
    public static void notNull(final Object argument,
                               final String name) {
        if (argument == null) throw new IllegalArgumentException(CommonI18n.localize(argumentMustNotBeNull, name));
    }

    /**
     * Check that the string is non-null and has length > 0
     *
     * @param argument
     *        The argument
     * @param name
     *        The name of the argument
     * @throws IllegalArgumentException
     *         If value is null or length == 0
     */
    public static void notZeroLength(final String argument,
                                     final String name) {
        notNull(argument, name);
        if (argument.length() <= 0) throw new IllegalArgumentException(CommonI18n.localize(argumentMustNotBeZeroLength, name));
    }

    private Arg() {
        // prevent construction
    }
}
