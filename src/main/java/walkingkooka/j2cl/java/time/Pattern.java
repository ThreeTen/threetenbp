/*
 * Copyright 2019 Miroslav Pokorny (github.com/mP1)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package walkingkooka.j2cl.java.time;

/**
 * Holds functionality equivalent code for {@link Pattern usage}.
 */
public final class Pattern {

    /**
     * Replaces the j2cl unsupported {@link java.util.regex.Pattern} used to validate the {@link String zoneId}
     */
    // Pattern.compile("[A-Za-z][A-Za-z0-9~/._+-]+");
    public static boolean isZoneId(final String zoneId) {
        boolean valid = isAlpha(zoneId.charAt(0));
        final int length = zoneId.length();
        int i = 1;

        while (valid && i < length) {
            final char c = zoneId.charAt(i);
            valid &= isAlpha(c) || "0123456789~/._+-".indexOf(c) != -1;
            i++;
        }

        return valid;
    }

    private static boolean isAlpha(final char c) {
        return (c >= 'A' && c <= 'Z') ||
                (c >= 'a' && c <= 'z');
    }
    
    /**
     * Obtains a {@code Period} from a text string such as {@code PnYnMnD}.
     * <p>
     * This will parse the string produced by {@code toString()} which is
     * based on the ISO-8601 period formats {@code PnYnMnD} and {@code PnW}.
     * <p>
     * The string starts with an optional sign, denoted by the ASCII negative
     * or positive symbol. If negative, the whole period is negated.
     * The ASCII letter "P" is next in upper or lower case.
     * There are then four sections, each consisting of a number and a suffix.
     * At least one of the four sections must be present.
     * The sections have suffixes in ASCII of "Y", "M", "W" and "D" for
     * years, months, weeks and days, accepted in upper or lower case.
     * The suffixes must occur in order.
     * The number part of each section must consist of ASCII digits.
     * The number may be prefixed by the ASCII negative or positive symbol.
     * The number must parse to an {@code int}.
     * <p>
     * The leading plus/minus sign, and negative values for other units are
     * not part of the ISO-8601 standard. In addition, ISO-8601 does not
     * permit mixing between the {@code PnYnMnD} and {@code PnW} formats.
     * Any week-based input is multiplied by 7 and treated as a number of days.
     * <p>
     * For example, the following are valid inputs:
     * <pre>
     *   "P2Y"             -- Period.ofYears(2)
     *   "P3M"             -- Period.ofMonths(3)
     *   "P4W"             -- Period.ofWeeks(4)
     *   "P5D"             -- Period.ofDays(5)
     *   "P1Y2M3D"         -- Period.of(1, 2, 3)
     *   "P1Y2M3W4D"       -- Period.of(1, 2, 25)
     *   "P-1Y2M"          -- Period.of(-1, 2, 0)
     *   "-P1Y2M"          -- Period.of(-1, -2, 0)
     * </pre>
     *
     * @param text  the text to parse, not null
     * @return the parsed period, not null
     * @throws DateTimeParseException if the text cannot be parsed to a period
     */
    public static String[] periodParse(final CharSequence text) {
        final int length = text.length();
        int i = 0;

        int mode = PERIOD_PARSE_SIGN;
        int startIndex = 0;
        int groupIndex = PERIOD_PARSE_GROUPS_YEAR;

        String[] groups = new String[PERIOD_PARSE_GROUPS_SIZE];

        Exit:
        while (i < length) {
            final char c = text.charAt(i);
            switch (mode) {
                case PERIOD_PARSE_SIGN:
                    switch (c) {
                        case '-':
                            groups[PERIOD_PARSE_GROUPS_SIGN] = PERIOD_PARSE_NEGATIVE_STRING;
                            i++;
                            break;
                        case '+':
                            groups[PERIOD_PARSE_GROUPS_SIGN] = PERIOD_PARSE_POSITIVE_STRING;
                            i++;
                            break;
                        default:
                            break;
                    }

                    mode = PERIOD_PARSE_P;
                    break;
                case PERIOD_PARSE_P:
                    switch (c) {
                        case 'P':
                        case 'p':
                            i++;
                            mode = PERIOD_PARSE_NUMBER_SIGN;
                            startIndex = i;
                            break;
                        default:
                            groups = null;
                            break Exit;
                    }
                    break;
                case PERIOD_PARSE_NUMBER_SIGN:
                    startIndex = i;
                    switch (c) {
                        case '-':
                        case '+':
                            i++;
                            mode = PERIOD_PARSE_NUMBER_FIRST_DIGIT;
                            break;
                        default:
                            mode = PERIOD_PARSE_NUMBER_DIGIT;
                            break;
                    }
                    break;

                case PERIOD_PARSE_NUMBER_FIRST_DIGIT:
                    if (false == isDigit(c)) {
                        groups = null;
                        break Exit;
                    }
                    i++;
                    mode = PERIOD_PARSE_NUMBER_DIGIT;
                    break;
                case PERIOD_PARSE_NUMBER_DIGIT:
                    if (false == isDigit(c)) {
                        mode = PERIOD_PARSE_YEAR_MONTH_WEEK_DAY;
                        break;
                    }
                    i++;
                    break;
                case PERIOD_PARSE_YEAR_MONTH_WEEK_DAY:
                    switch (c) {
                        case 'y':
                        case 'Y':
                            if (groupIndex > PERIOD_PARSE_GROUPS_YEAR) {
                                groups = null;
                                break Exit;
                            }
                            groups[PERIOD_PARSE_GROUPS_YEAR] = text.subSequence(startIndex, i).toString();
                            groupIndex = PERIOD_PARSE_GROUPS_MONTH;
                            i++;
                            mode = PERIOD_PARSE_NUMBER_SIGN;
                            break;
                        case 'm':
                        case 'M':
                            if (groupIndex > PERIOD_PARSE_GROUPS_MONTH) {
                                groups = null;
                                break Exit;
                            }
                            groups[PERIOD_PARSE_GROUPS_MONTH] = text.subSequence(startIndex, i).toString();
                            groupIndex = PERIOD_PARSE_GROUPS_WEEK;
                            i++;
                            mode = PERIOD_PARSE_NUMBER_SIGN;
                            break;
                        case 'w':
                        case 'W':
                            if (groupIndex > PERIOD_PARSE_GROUPS_WEEK) {
                                groups = null;
                                break Exit;
                            }
                            groups[PERIOD_PARSE_GROUPS_WEEK] = text.subSequence(startIndex, i).toString();
                            groupIndex = PERIOD_PARSE_GROUPS_DAY;
                            i++;
                            mode = PERIOD_PARSE_NUMBER_SIGN;
                            break;
                        case 'd':
                        case 'D':
                            if (groupIndex > PERIOD_PARSE_GROUPS_DAY) {
                                groups = null;
                                break Exit;
                            }
                            groups[PERIOD_PARSE_GROUPS_DAY] = text.subSequence(startIndex, i).toString();
                            i++;
                            mode = PERIOD_PARSE_END;
                            break;
                        default: // unexpected character -> ERROR
                            groups = null;
                            break Exit;
                    }
                    break;
                case PERIOD_PARSE_END:
                    groups = null;
                    break Exit;
            }
        }

        switch (mode) {
            case PERIOD_PARSE_SIGN: // must be empty text
            case PERIOD_PARSE_P: // pattern sign but no 'P'
            case PERIOD_PARSE_NUMBER_DIGIT: // string of digits but missing 'Y', 'M', 'W' or 'D'
            case PERIOD_PARSE_YEAR_MONTH_WEEK_DAY: // expecting 'Y', 'M', 'W' or 'D' shouldnt really happen
                groups = null; // enough to report an error
                break;
            case PERIOD_PARSE_NUMBER_SIGN: // must be just after a letter, but no number
            case PERIOD_PARSE_END:
                break;
        }

        return groups;
    }

    /**
     * Optional sign before the 'P'.
     */
    private final static int PERIOD_PARSE_SIGN = 1;

    /**
     * Required 'P'
     */
    private final static int PERIOD_PARSE_P = PERIOD_PARSE_SIGN + 1;

    /**\
     * Optional sign before a number
     */
    private final static int PERIOD_PARSE_NUMBER_SIGN = PERIOD_PARSE_P + 1;

    /**
     * Required first digit after a number sign.
     */
    private final static int PERIOD_PARSE_NUMBER_FIRST_DIGIT = PERIOD_PARSE_NUMBER_SIGN + 1;

    /**
     * Optional digits in a number.
     */
    private final static int PERIOD_PARSE_NUMBER_DIGIT = PERIOD_PARSE_NUMBER_FIRST_DIGIT + 1;

    /**
     * Required letter after a series of digits.
     */
    private final static int PERIOD_PARSE_YEAR_MONTH_WEEK_DAY = PERIOD_PARSE_NUMBER_DIGIT + 1;

    /**
     * Required letter after a series of digits.
     */
    private final static int PERIOD_PARSE_END = PERIOD_PARSE_YEAR_MONTH_WEEK_DAY + 1;

    private final static int PERIOD_PARSE_GROUPS_SIGN = 1;
    private final static int PERIOD_PARSE_GROUPS_YEAR = PERIOD_PARSE_GROUPS_SIGN + 1;
    private final static int PERIOD_PARSE_GROUPS_MONTH = PERIOD_PARSE_GROUPS_YEAR + 1;
    private final static int PERIOD_PARSE_GROUPS_WEEK = PERIOD_PARSE_GROUPS_MONTH + 1;
    private final static int PERIOD_PARSE_GROUPS_DAY = PERIOD_PARSE_GROUPS_WEEK + 1;
    private final static int PERIOD_PARSE_GROUPS_SIZE = PERIOD_PARSE_GROUPS_DAY + 1;

    private final static String PERIOD_PARSE_NEGATIVE_STRING = "-";
    private final static String PERIOD_PARSE_POSITIVE_STRING = "+";

    // Duration.parse ..................................................................................................

//    private final static Pattern PATTERN =
//            Pattern.compile("([-+]?)P(?:([-+]?[0-9]+)D)?" +
//                    "(T(?:([-+]?[0-9]+)H)?(?:([-+]?[0-9]+)M)?(?:([-+]?[0-9]+)(?:[.,]([0-9]{0,9}))?S)?)?",
//                    Pattern.CASE_INSENSITIVE);

    /**
     * Obtains a {@code Duration} from a text string such as {@code PnDTnHnMn.nS}.
     * <p>
     * This will parse a textual representation of a duration, including the
     * string produced by {@code toString()}. The formats accepted are based
     * on the ISO-8601 duration format {@code PnDTnHnMn.nS} with days
     * considered to be exactly 24 hours.
     * <p>
     * The string starts with an optional sign, denoted by the ASCII negative
     * or positive symbol. If negative, the whole period is negated.
     * The ASCII letter "P" is next in upper or lower case.
     * There are then four sections, each consisting of a number and a suffix.
     * The sections have suffixes in ASCII of "D", "H", "M" and "S" for
     * days, hours, minutes and seconds, accepted in upper or lower case.
     * The suffixes must occur in order. The ASCII letter "T" must occur before
     * the first occurrence, if any, of an hour, minute or second section.
     * At least one of the four sections must be present, and if "T" is present
     * there must be at least one section after the "T".
     * The number part of each section must consist of one or more ASCII digits.
     * The number may be prefixed by the ASCII negative or positive symbol.
     * The number of days, hours and minutes must parse to a {@code long}.
     * The number of seconds must parse to a {@code long} with optional fraction.
     * The decimal point may be either a dot or a comma.
     * The fractional part may have from zero to 9 digits.
     * <p>
     * The leading plus/minus sign, and negative values for other units are
     * not part of the ISO-8601 standard.
     * <p>
     * Examples:
     * <pre>
     *    "PT20.345S" -> parses as "20.345 seconds"
     *    "PT15M"     -> parses as "15 minutes" (where a minute is 60 seconds)
     *    "PT10H"     -> parses as "10 hours" (where an hour is 3600 seconds)
     *    "P2D"       -> parses as "2 days" (where a day is 24 hours or 86400 seconds)
     *    "P2DT3H4M"  -> parses as "2 days, 3 hours and 4 minutes"
     *    "P-6H3M"    -> parses as "-6 hours and +3 minutes"
     *    "-P6H3M"    -> parses as "-6 hours and -3 minutes"
     *    "-P-6H+3M"  -> parses as "+6 hours and -3 minutes"
     * </pre>
     *
     * @param text the text to parse, not null
     * @return the parsed duration, not null
     * @throws DateTimeParseException if the text cannot be parsed to a duration
     */
    public static String[] durationParse(final CharSequence text) {
        final int length = text.length();
        int i = 0;

        int mode = DURATION_PARSE_SIGN;
        int startIndex = 0;
        int groupIndex = DURATION_PARSE_GROUPS_DAY;

        String[] groups = new String[DURATION_PARSE_GROUPS_SIZE];

        Exit:
        while (i < length) {
            final char c = text.charAt(i);
            switch (mode) {
                case DURATION_PARSE_SIGN:
                    switch (c) {
                        case '-':
                            groups[DURATION_PARSE_GROUPS_SIGN] = DURATION_PARSE_NEGATIVE_STRING;
                            i++;
                            break;
                        case '+':
                            groups[DURATION_PARSE_GROUPS_SIGN] = DURATION_PARSE_POSITIVE_STRING;
                            i++;
                            break;
                        default:
                            break;
                    }

                    mode = DURATION_PARSE_P;
                    break;
                case DURATION_PARSE_P:
                    switch (c) {
                        case 'P':
                        case 'p':
                            i++;
                            mode = DURATION_PARSE_NUMBER_SIGN;
                            break;
                        default:
                            groups = null;
                            break Exit;
                    }
                    break;
                case DURATION_PARSE_NUMBER_SIGN:
                    startIndex = i;
                    switch (c) {
                        case '-':
                        case '+':
                            i++;
                            mode = DURATION_PARSE_NUMBER_FIRST_DIGIT;
                            break;
                        default:
                            mode = DURATION_PARSE_NUMBER_DIGIT;
                            break;
                    }
                    break;

                case DURATION_PARSE_NUMBER_FIRST_DIGIT:
                    if (false == isDigit(c)) {
                        groups = null;
                        break Exit;
                    }
                    i++;
                    mode = DURATION_PARSE_NUMBER_DIGIT;
                    break;
                case DURATION_PARSE_NUMBER_DIGIT:
                    if (false == isDigit(c)) {
                        mode = DURATION_PARSE_DAY_HOUR_MINUTE_SECOND;
                        break;
                    }
                    i++;
                    break;
                case DURATION_PARSE_DAY_HOUR_MINUTE_SECOND:
                    switch (c) {
                        case 'd':
                        case 'D':
                            if (groupIndex > DURATION_PARSE_GROUPS_DAY) {
                                groups = null;
                                break Exit;
                            }
                            groups[DURATION_PARSE_GROUPS_DAY] = text.subSequence(startIndex, i).toString();
                            groupIndex = DURATION_PARSE_GROUPS_T;
                            i++;
                            mode = DURATION_PARSE_T;
                            break;
                        case 'h':
                        case 'H':
                            if (groupIndex > DURATION_PARSE_GROUPS_HOUR) {
                                groups = null;
                                break Exit;
                            }
                            groups[DURATION_PARSE_GROUPS_HOUR] = text.subSequence(startIndex, i).toString();
                            groupIndex = DURATION_PARSE_GROUPS_MINUTE;
                            i++;
                            mode = DURATION_PARSE_NUMBER_SIGN;
                            break;
                        case 'm':
                        case 'M':
                            if (groupIndex > DURATION_PARSE_GROUPS_MINUTE) {
                                groups = null;
                                break Exit;
                            }
                            groups[DURATION_PARSE_GROUPS_MINUTE] = text.subSequence(startIndex, i).toString();
                            groupIndex = DURATION_PARSE_GROUPS_SECOND;
                            i++;
                            mode = DURATION_PARSE_NUMBER_SIGN;
                            break;
                        case '.':
                        case ',':
                            mode = DURATION_PARSE_DECIMAL_SEPARATOR;
                            break;
                        case 's':
                        case 'S':
                            switch (groupIndex) {
                                case DURATION_PARSE_GROUPS_DAY:
                                case DURATION_PARSE_GROUPS_T:
                                case DURATION_PARSE_GROUPS_HOUR:
                                case DURATION_PARSE_GROUPS_MINUTE:
                                case DURATION_PARSE_GROUPS_SECOND:
                                    groupIndex = DURATION_PARSE_GROUPS_SECOND;
                                    break;
                                case DURATION_PARSE_GROUPS_FRACTION:
                                    if (i - startIndex >= 10) {
                                        groups = null;
                                        break Exit; // too long -> error
                                    }
                                    groupIndex = DURATION_PARSE_GROUPS_FRACTION;
                                    break;
                                case DURATION_PARSE_END:
                                    groups = null;
                                    break Exit;
                                default:
                                    break;
                            }

                            groups[groupIndex] = text.subSequence(startIndex, i).toString();
                            i++;
                            mode = DURATION_PARSE_END;
                            break;
                        case 'T':
                        case 't':
                            mode = DURATION_PARSE_T;
                            break;
                        default: // unexpected character -> ERROR
                            groups = null;
                            break Exit;
                    }
                    break;
                case DURATION_PARSE_T:
                    startIndex = i;
                    switch (c) {
                        case 'T':
                        case 't':
                            if (groupIndex > DURATION_PARSE_GROUPS_HOUR) {
                                groups = null;
                                break Exit;
                            }
                            groups[DURATION_PARSE_GROUPS_T] = String.valueOf(c);
                            i++;
                            startIndex = i;
                            mode = DURATION_PARSE_NUMBER_SIGN;
                            groupIndex = DURATION_PARSE_GROUPS_HOUR;
                            break;
                        default:
                            startIndex = i;
                            mode = DURATION_PARSE_NUMBER_FIRST_DIGIT;
                            groupIndex = DURATION_PARSE_GROUPS_HOUR;
                            break;
                    }
                    break;
                case DURATION_PARSE_DECIMAL_SEPARATOR:
                    if (groupIndex > DURATION_PARSE_GROUPS_SECOND) {
                        groups = null; // decimal point in the wrong number/place
                        break Exit;
                    }

                    switch (c) {
                        case '.':
                        case ',':
                            groups[DURATION_PARSE_GROUPS_SECOND] = text.subSequence(startIndex, i).toString();
                            i++;
                            startIndex = i;
                            groupIndex = DURATION_PARSE_GROUPS_FRACTION;
                            mode = DURATION_PARSE_NUMBER_DIGIT;
                            break;
                        default:
                            break;
                    }
                    break;
                case DURATION_PARSE_END:
                    groups = null;
                    break Exit;
                default:
                    break Exit;
            }
        }

        switch (mode) {
            case DURATION_PARSE_SIGN: // must be empty text
            case DURATION_PARSE_P: // pattern sign but no 'P'
            case DURATION_PARSE_NUMBER_DIGIT: // string of digits but missing 'D', 'H', 'M' or 'S'
            case DURATION_PARSE_DAY_HOUR_MINUTE_SECOND: // expecting 'D', 'H', 'M' or 'S' shouldnt really happen
            case DURATION_PARSE_DECIMAL_SEPARATOR: // seconds includes decimal point but missing S
                groups = null; // enough to report an error
                break;
            case DURATION_PARSE_T: // must have just done days
            case DURATION_PARSE_NUMBER_SIGN: // must be just after a letter, but no number
            case DURATION_PARSE_END:
                break;
        }

        return groups;
    }

    /**
     * Optional sign before the 'P'.
     */
    private final static int DURATION_PARSE_SIGN = 1;

    /**
     * Required 'P'
     */
    private final static int DURATION_PARSE_P = DURATION_PARSE_SIGN + 1;

    /**
     * \
     * Optional sign before a number
     */
    private final static int DURATION_PARSE_NUMBER_SIGN = DURATION_PARSE_P + 1;

    /**
     * Required first digit after a number sign.
     */
    private final static int DURATION_PARSE_NUMBER_FIRST_DIGIT = DURATION_PARSE_NUMBER_SIGN + 1;

    /**
     * Optional digits in a number.
     */
    private final static int DURATION_PARSE_NUMBER_DIGIT = DURATION_PARSE_NUMBER_FIRST_DIGIT + 1;

    /**
     * Required letter after a series of digits.
     */
    private final static int DURATION_PARSE_DAY_HOUR_MINUTE_SECOND = DURATION_PARSE_NUMBER_DIGIT + 1;

    /**
     * Required letter 'T' between days and hours
     */
    private final static int DURATION_PARSE_T = DURATION_PARSE_DAY_HOUR_MINUTE_SECOND + 1;

    /**
     * Required decimal point '.' expected only within a seconds.
     */
    private final static int DURATION_PARSE_DECIMAL_SEPARATOR = DURATION_PARSE_T + 1;

    /**
     * Indicates the seconds including the fraction has been consumed
     */
    private final static int DURATION_PARSE_END = DURATION_PARSE_DECIMAL_SEPARATOR + 1;

    private final static int DURATION_PARSE_GROUPS_SIGN = 1;
    private final static int DURATION_PARSE_GROUPS_DAY = 2;
    private final static int DURATION_PARSE_GROUPS_T = 3;
    private final static int DURATION_PARSE_GROUPS_HOUR = 4;
    private final static int DURATION_PARSE_GROUPS_MINUTE = 5;
    private final static int DURATION_PARSE_GROUPS_SECOND = 6;
    private final static int DURATION_PARSE_GROUPS_FRACTION = 7;
    private final static int DURATION_PARSE_GROUPS_SIZE = 8;

    private final static String DURATION_PARSE_NEGATIVE_STRING = "-";
    private final static String DURATION_PARSE_POSITIVE_STRING = "+";

    // helpers..........................................................................................................

    private static boolean isDigit(final char c) {
        return c >= '0' && c <= '9';
    }

    private Pattern() {
        throw new UnsupportedOperationException();
    }
}
