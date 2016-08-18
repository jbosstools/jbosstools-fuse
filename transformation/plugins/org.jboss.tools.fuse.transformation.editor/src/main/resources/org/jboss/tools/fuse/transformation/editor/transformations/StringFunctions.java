/******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: JBoss by Red Hat - Initial implementation.
 *****************************************************************************/
package org.jboss.tools.fuse.transformation.editor.transformations;

import org.jboss.tools.fuse.transformation.editor.transformations.Function.Arg;

/**
 * Deployments of this source code to design-time Fuse projects must not be altered by users!
 */
public class StringFunctions {

    @Function(description = "Appends the supplied suffix to the end of the source string.",
              format = "Append %2$s to %1$s",
              args = @Arg(name = "suffix",
                          description = "The string to be appended to the end of the source string"))
    public String append(String source,
                         String suffix) {
        return source + suffix;
    }

    @Function(description = "Returns true if the source string contains the supplied substring.",
              format = "%1$s contains %2$s if ignoring case is %3$s?",
              args = {
                  @Arg(name = "substring",
                       description = "The substring to find in the source string"),
                  @Arg(name = "ignore case",
                       description = "Indicates to ignore differences in case (defaults to false)")
              })
    public boolean contains(String source,
                            String substring,
                            Boolean ignoreCase) {
        return ignoreCase ? source.toLowerCase().contains(substring.toLowerCase()) : source.contains(substring);
    }

    @Function(description = "Returns true if the source string ends with the supplied string.",
              format = "%1$s ends with %2$s if ignoring case is %3$s?",
              args = {
                  @Arg(name = "substring",
                       description = "The string to compare to the end of the source string"),
                  @Arg(name = "ignore case",
                       description = "Indicates to ignore differences in case (defaults to false)")
              })
    public boolean endsWith(String source,
                            String substring,
                            Boolean ignoreCase) {
        return ignoreCase ? source.toLowerCase().endsWith(substring.toLowerCase()) : source.endsWith(substring);
    }

    @Function(description = "Returns true if the source string exactly matches the supplied string.",
              format = "%1$s = %2$s if ignoring case is %3$s?",
              args = {
                  @Arg(name = "string",
                       description = "The string to compare to the source string"),
                  @Arg(name = "ignore case",
                       description = "Indicates to ignore differences in case (defaults to false)")
              })
    public boolean equals(String sourceString,
                          String otherString,
                          Boolean ignoreCase) {
        return ignoreCase ? sourceString.equalsIgnoreCase(otherString) : sourceString.equals(otherString);
    }

    @Function(description = "Formats the source string to match the supplied pattern.  "
                            + "The source string can be specified in the pattern using <code>%s</code>."
                            + "<p style='text-decoration: underline; font-weight: bold;'>Example</p>"
                            + "<code>format(\"text\", \"[%s]\")</code> transforms source string <code>\"text\"</code> to <code>\"[text]\"</code>",
              format = "Format %1$s using pattern %2$s?",
              args = @Arg(name = "pattern",
                          description = "The pattern to use to format the source string"))
    public String format(String source,
                         String pattern) {
        return String.format(pattern, source);
    }

    @Function(description = "Returns the index of the source string where the supplied substring begins, or -1 if not found.",
              format = "Index of %2$s in %1$s if ignoring case is %3$s?",
              args = {
                  @Arg(name = "substring",
                       description = "The substring to find in the source string"),
                  @Arg(name = "ignore case",
                       description = "Indicates to ignore differences in case (defaults to false)")
              })
    public int indexOf(String source,
                       String substring,
                       Boolean ignoreCase) {
        return ignoreCase ? source.toLowerCase().indexOf(substring.toLowerCase()) : source.indexOf(substring);
    }

    @Function(description = "Returns true if the source string contains no characters (i.e., its length is zero)",
              format = "%1$s is empty?")
    public boolean isEmpty(String source) {
        return source.isEmpty();
    }

    @Function(description = "Returns the last index of the source string where the supplied substring begins, or -1 if not found.",
              format = "Last index of %2$s in %1$s if ignoring case is %3$s?",
              args = {
                  @Arg(name = "substring",
                       description = "The substring to find in the source string"),
                  @Arg(name = "ignore case",
                       description = "Indicates to ignore differences in case (defaults to false)")
              })
    public int lastIndexOf(String source,
                           String substring,
                           Boolean ignoreCase) {
        return ignoreCase ? source.toLowerCase().lastIndexOf(substring.toLowerCase()) : source.lastIndexOf(substring);
    }

    @Function(description = "Returns number of characters in the source string.",
              format = "Length of %1$s")
    public int length(String source) {
        return source.length();
    }

    @Function(description = "Converts the source string to lower case.",
              format = "%1$s to lowercase")
    public String lowerCase(String source) {
        return source.toLowerCase();
    }

    @Function(description = "Prepends the supplied prefix to the beginning of the source string.",
              format = "Prepend %2$s to %1$s",
              args = @Arg(name = "prefix",
                          description = "The string to be prepended to the beginning of the source string"))
    public String prepend(String source,
                          String prefix) {
        return prefix + source;
    }

    @Function(description = "Converts the source string to proper case (i.e., its first character upper case, and the remaining lower case).",
              format = "%1$s to proper case")
    public String properCase(String source) {
        if (source.isEmpty()) return source;
        return Character.toUpperCase(source.charAt(0)) + source.substring(1).toLowerCase();
    }

    @Function(description = "Removes the first occurrence of the supplied substring from the source string.",
              format = "Remove %2$s from %1$s",
              args = @Arg(name = "substring",
                          description = "The substring that will be removed from the source string"))
    public String remove(String source,
                         String substring) {
        return source.replaceFirst(substring, ""); //$NON-NLS-1$
    }

    @Function(description = "Removes all occurrences of the supplied substring from the source string.",
              format = "Remove all %2$s from %1$s",
              args = @Arg(name = "substring",
                          description = "The substring for which all occurrences will be removed from the source string"))
    public String removeAll(String source,
                            String substring) {
        return source.replaceAll(substring, ""); //$NON-NLS-1$
    }

    @Function(description = "Replaces the first occurrence of the supplied old substring with the supplied new substring in the source string.",
              format = "Replace %2$s with %3$s in %1$s",
              args = {
                  @Arg(name = "old substring",
                       description = "The old substring to be replaced in the source string"),
                  @Arg(name = "new substring",
                       description = "The new substring that will replace the old substring within the source string")
              })
    public String replace(String source,
                          String oldSubstring,
                          String newSubstring) {
        return source.replaceFirst(oldSubstring, newSubstring);
    }

    @Function(description = "Replaces all occurrences of the supplied old substring with the supplied new substring in the source string.",
              format = "Replace all %2$s with %3$s in %1$s",
              args = {
                  @Arg(name = "old substring",
                       description = "The old substring to be replaced whereever it occurs in the source string"),
                  @Arg(name = "new substring",
                       description = "The new substring that will replace all occurrences of the old substring within the source string")
              })
    public String replaceAll(String source,
                             String oldSubstring,
                             String newSubstring) {
        return source.replaceAll(oldSubstring, newSubstring);
    }

    @Function(description = "Returns true if the source string starts with the supplied string.",
              format = "%1$s starts with %2$s if ignoring case is %3$s?",
              args = {
                  @Arg(name = "substring",
                       description = "The string to compare to the beginning of the source string"),
                  @Arg(name = "ignore case",
                       description = "Indicates to ignore differences in case (defaults to false)")
              })
    public boolean startsWith(String source,
                              String substring,
                              Boolean ignoreCase) {
        return ignoreCase ? source.toLowerCase().startsWith(substring.toLowerCase()) : source.startsWith(substring);
    }

    @Function(description = "Returns the substring of the source string beginning at the supplied index and having the supplied length.",
              format = "Substring of %1$s from index %2$s with length %3$s",
              args = {
                  @Arg(name = "index",
                       description = "The index within the source string where the substring begins (defaults to 0)",
                       defaultValue = "0"),
                  @Arg(name = "length",
                       description = "The length of the substring (defaults to the remaining length of the string)",
                       defaultValue = Short.MAX_VALUE + "",
                       hideDefault = true)
              })
    public String substring(String source,
                            Integer index,
                            Integer length) {
        int start = Math.min(Math.max(0, index), source.length() - 1);
        return source.substring(start, Math.min(Math.max(index, start + length), source.length()));
    }

    @Function(description = "Transforms the source string to a Boolean value.",
              format = "Transform %1$s to Boolean value")
    public Boolean toBoolean(String source) {
        return Boolean.valueOf(source);
    }

    @Function(description = "Transforms the source string to a double value.",
              format = "Transform %1$s to a double value")
    public Double toDouble(String source) {
        return Double.valueOf(source);
    }

    @Function(description = "Transforms the source string to a floating-point value.",
              format = "Transform %1$s to a floating-point value")
    public Float toFloat(String source) {
        return Float.valueOf(source);
    }

    @Function(description = "Transforms the source string to an integer value.",
              format = "Transform %1$s to integer value")
    public Integer toInteger(String source) {
        return Integer.valueOf(source);
    }

    @Function(description = "Parses the source string into tokens using the supplied delimiter regular expression, "
                            + "then returns the token at the supplied index.",
              format = "Token with index %3$s from %1$s delimited by %2$s",
              args = {
                  @Arg(name = "delimiter",
                       description = "The regular expression used to delimit tokens in the source string (defaults to \"\\s\")",
                       defaultValue = "\\s"),
                  @Arg(name = "index",
                       description = "The index of the token to return from the source string (defaults to 0, the index for the the first token)",
                       defaultValue = "0")
              })
    public String token(String source,
                        String delimiter,
                        int index) {
        return source.split(delimiter)[index];
    }

    @Function(description = "Transforms the source string to a long value.",
              format = "Transform %1$s to long value")
    public Long toLong(String source) {
        return Long.valueOf(source);
    }

    @Function(description = "Transforms the source string to a short value.",
              format = "Transform %1$s to short value")
    public Short toShort(String source) {
        return Short.valueOf(source);
    }

    @Function(description = "Removes all leading and trailing whitespace from the source string.",
              format = "Remove surrounding whitespace from %1$s")
    public String trim(String source) {
        return source.trim();
    }

    @Function(description = "Truncates the source string to be no greater than the supplied length.",
              format = "Truncate %1$s to length %2$s",
              args = @Arg(name = "length",
                          description = "The maximum length of the source string"))
    public String truncate(String source,
                           Integer length) {
        return substring(source, 0, length);
    }

    @Function(description = "Converts the source string to upper case.",
              format = "%1$s to uppercase")
    public String upperCase(String source) {
        return source.toUpperCase();
    }
}
