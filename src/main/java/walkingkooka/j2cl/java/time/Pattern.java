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

import org.threeten.bp.format.DateTimeParseException;

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
    public static String[] parse(final CharSequence text) {
        final int length = text.length();
        int i = 0;

        int mode = PATTERN_SIGN;
        int startIndex = 0;
        int groupIndex = PATTERN_GROUPS_YEAR;

        String[] groups = new String[PATTERN_GROUPS_SIZE];

        Exit:
        while (i < length) {
            final char c = text.charAt(i);
            switch (mode) {
                case PATTERN_SIGN:
                    switch (c) {
                        case '-':
                            groups[PATTERN_GROUPS_SIGN] = PATTERN_NEGATIVE_STRING;
                            i++;
                            break;
                        case '+':
                            groups[PATTERN_GROUPS_SIGN] = PATTERN_POSITIVE_STRING;
                            i++;
                            break;
                        default:
                            break;
                    }

                    mode = PATTERN_P;
                    break;
                case PATTERN_P:
                    switch (c) {
                        case 'P':
                        case 'p':
                            i++;
                            mode = PATTERN_NUMBER_SIGN;
                            startIndex = i;
                            break;
                        default:
                            groups = null;
                            break Exit;
                    }
                    break;
                case PATTERN_NUMBER_SIGN:
                    startIndex = i;
                    switch (c) {
                        case '-':
                        case '+':
                            i++;
                            mode = PATTERN_NUMBER_FIRST_DIGIT;
                            break;
                        default:
                            mode = PATTERN_NUMBER_DIGIT;
                            break;
                    }
                    break;

                case PATTERN_NUMBER_FIRST_DIGIT:
                    if (false == isDigit(c)) {
                        groups = null;
                        break Exit;
                    }
                    i++;
                    mode = PATTERN_NUMBER_DIGIT;
                    break;
                case PATTERN_NUMBER_DIGIT:
                    if (false == isDigit(c)) {
                        mode = PATTERN_YEAR_MONTH_WEEK_DAY;
                        break;
                    }
                    i++;
                    break;
                case PATTERN_YEAR_MONTH_WEEK_DAY:
                    switch (c) {
                        case 'y':
                        case 'Y':
                            if (groupIndex > PATTERN_GROUPS_YEAR) {
                                groups = null;
                                break Exit;
                            }
                            groups[PATTERN_GROUPS_YEAR] = text.subSequence(startIndex, i).toString();
                            groupIndex = PATTERN_GROUPS_MONTH;
                            i++;
                            mode = PATTERN_NUMBER_SIGN;
                            break;
                        case 'm':
                        case 'M':
                            if (groupIndex > PATTERN_GROUPS_MONTH) {
                                groups = null;
                                break Exit;
                            }
                            groups[PATTERN_GROUPS_MONTH] = text.subSequence(startIndex, i).toString();
                            groupIndex = PATTERN_GROUPS_WEEK;
                            i++;
                            mode = PATTERN_NUMBER_SIGN;
                            break;
                        case 'w':
                        case 'W':
                            if (groupIndex > PATTERN_GROUPS_WEEK) {
                                groups = null;
                                break Exit;
                            }
                            groups[PATTERN_GROUPS_WEEK] = text.subSequence(startIndex, i).toString();
                            groupIndex = PATTERN_GROUPS_DAY;
                            i++;
                            mode = PATTERN_NUMBER_SIGN;
                            break;
                        case 'd':
                        case 'D':
                            if (groupIndex > PATTERN_GROUPS_DAY) {
                                groups = null;
                                break Exit;
                            }
                            groups[PATTERN_GROUPS_DAY] = text.subSequence(startIndex, i).toString();
                            i++;
                            mode = PATTERN_END;
                            break;
                        default: // unexpected character -> ERROR
                            groups = null;
                            break Exit;
                    }
                    break;
                case PATTERN_END:
                    groups = null;
                    break Exit;
            }
        }

        switch (mode) {
            case PATTERN_SIGN: // must be empty text
            case PATTERN_P: // pattern sign but no 'P'
            case PATTERN_NUMBER_DIGIT: // string of digits but missing 'Y', 'M', 'W' or 'D'
            case PATTERN_YEAR_MONTH_WEEK_DAY: // expecting 'Y', 'M', 'W' or 'D' shouldnt really happen
                groups = null; // enough to report an error
                break;
            case PATTERN_NUMBER_SIGN: // must be just after a letter, but no number
            case PATTERN_END:
                break;
        }

        return groups;
    }

    private static boolean isDigit(final char c) {
        return c >= '0' && c <= '9';
    }

    /**
     * Optional sign before the 'P'.
     */
    private final static int PATTERN_SIGN = 1;

    /**
     * Required 'P'
     */
    private final static int PATTERN_P = PATTERN_SIGN + 1;

    /**\
     * Optional sign before a number
     */
    private final static int PATTERN_NUMBER_SIGN = PATTERN_P + 1;

    /**
     * Required first digit after a number sign.
     */
    private final static int PATTERN_NUMBER_FIRST_DIGIT = PATTERN_NUMBER_SIGN + 1;

    /**
     * Optional digits in a number.
     */
    private final static int PATTERN_NUMBER_DIGIT = PATTERN_NUMBER_FIRST_DIGIT + 1;

    /**
     * Required letter after a series of digits.
     */
    private final static int PATTERN_YEAR_MONTH_WEEK_DAY = PATTERN_NUMBER_DIGIT + 1;

    /**
     * Required letter after a series of digits.
     */
    private final static int PATTERN_END = PATTERN_YEAR_MONTH_WEEK_DAY + 1;

    private final static int PATTERN_GROUPS_SIGN = 1;
    private final static int PATTERN_GROUPS_YEAR = PATTERN_GROUPS_SIGN + 1;
    private final static int PATTERN_GROUPS_MONTH = PATTERN_GROUPS_YEAR + 1;
    private final static int PATTERN_GROUPS_WEEK = PATTERN_GROUPS_MONTH + 1;
    private final static int PATTERN_GROUPS_DAY = PATTERN_GROUPS_WEEK + 1;
    private final static int PATTERN_GROUPS_SIZE = PATTERN_GROUPS_DAY + 1;

    private final static String PATTERN_NEGATIVE_STRING = "-";
    private final static String PATTERN_POSITIVE_STRING = "+";

    private Pattern() {
        throw new UnsupportedOperationException();
    }
}
