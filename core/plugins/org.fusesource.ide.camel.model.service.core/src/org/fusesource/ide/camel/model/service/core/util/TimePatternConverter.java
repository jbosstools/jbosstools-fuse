/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.model.service.core.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is a copy from camel-core so we can use it independent to validate uris with time patterns
 */
public final class TimePatternConverter {
    private static final Pattern NUMBERS_ONLY_STRING_PATTERN = Pattern.compile("^[-]?(\\d)+$", Pattern.CASE_INSENSITIVE);
    private static final Pattern HOUR_REGEX_PATTERN = Pattern.compile("((\\d)*(\\d))h(our(s)?)?", Pattern.CASE_INSENSITIVE);
    private static final Pattern MINUTES_REGEX_PATTERN = Pattern.compile("((\\d)*(\\d))m(in(ute(s)?)?)?", Pattern.CASE_INSENSITIVE);
    private static final Pattern SECONDS_REGEX_PATTERN = Pattern.compile("((\\d)*(\\d))s(ec(ond)?(s)?)?", Pattern.CASE_INSENSITIVE);

    /**
     * Utility classes should not have a public constructor.
     */
    private TimePatternConverter() {
    }

    public static long toMilliSeconds(String source) throws IllegalArgumentException {
        long milliseconds = 0;
        boolean foundFlag = false;

        checkCorrectnessOfPattern(source);
        Matcher matcher;

        matcher = createMatcher(NUMBERS_ONLY_STRING_PATTERN, source);
        if (matcher.find()) {
            // Note: This will also be used for regular numeric strings.
            //       This String -> long converter will be used for all strings.
            milliseconds = Long.valueOf(source);
        } else {
            matcher = createMatcher(HOUR_REGEX_PATTERN, source);
            if (matcher.find()) {
                milliseconds = milliseconds + (3600000 * Long.valueOf(matcher.group(1)));
                foundFlag = true;
            }

            matcher = createMatcher(MINUTES_REGEX_PATTERN, source);
            if (matcher.find()) {
                long minutes = Long.valueOf(matcher.group(1));
                if ((minutes > 59) && foundFlag) {
                    throw new IllegalArgumentException("Minutes should contain a valid value between 0 and 59: " + source);
                }
                foundFlag = true;
                milliseconds = milliseconds + (60000 * minutes);
            }

            matcher = createMatcher(SECONDS_REGEX_PATTERN, source);
            if (matcher.find()) {
                long seconds = Long.valueOf(matcher.group(1));
                if ((seconds > 59) && foundFlag) {
                    throw new IllegalArgumentException("Seconds should contain a valid value between 0 and 59: " + source);
                }
                foundFlag = true;
                milliseconds = milliseconds + (1000 * seconds);
            }

            // No pattern matched... initiating fallback check and conversion (if required).
            // The source at this point may contain illegal values or special characters
            if (!foundFlag) {
                milliseconds = Long.valueOf(source);
            }
        }

        return milliseconds;
    }

    private static void checkCorrectnessOfPattern(String source) {
        //replace only numbers once
        Matcher matcher = createMatcher(NUMBERS_ONLY_STRING_PATTERN, source);
        String replaceSource = matcher.replaceFirst("");

        //replace hour string once
        matcher = createMatcher(HOUR_REGEX_PATTERN, replaceSource);
        if (matcher.find() && matcher.find()) {
            throw new IllegalArgumentException("Hours should not be specified more then once: " + source);
        }
        replaceSource = matcher.replaceFirst("");

        //replace minutes once
        matcher = createMatcher(MINUTES_REGEX_PATTERN, replaceSource);
        if (matcher.find() && matcher.find()) {
            throw new IllegalArgumentException("Minutes should not be specified more then once: " + source);
        }
        replaceSource = matcher.replaceFirst("");

        //replace seconds once
        matcher = createMatcher(SECONDS_REGEX_PATTERN, replaceSource);
        if (matcher.find() && matcher.find()) {
            throw new IllegalArgumentException("Seconds should not be specified more then once: " + source);
        }
        replaceSource = matcher.replaceFirst("");

        if (replaceSource.length() > 0) {
            throw new IllegalArgumentException("Illegal characters: " + source);
        }
    }

    private static Matcher createMatcher(Pattern pattern, String source) {
        return pattern.matcher(source);
    }
}
