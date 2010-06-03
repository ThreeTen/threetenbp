/*
 * Copyright (c) 2009-2010, Stephen Colebourne & Michael Nascimento Santos
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of JSR-310 nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package javax.time.calendar;

import javax.time.calendar.format.CalendricalParseException;

/**
 * An period parser that creates an instance of {@code Period} from a string
 * using the ISO8601 period format {@code PnYnMnDTnHnMn.nS}.
 *
 * @author Darryl West
 * @author Stephen Colebourne
 */
class PeriodParser {

    /**
     * The singleton instance.
     */
    private static final PeriodParser INSTANCE = new PeriodParser();
    /**
     * Used to validate the correct sequence of tokens.
     */
    private static final String TOKEN_SEQUENCE = "PYMDTHMS";
    /**
     * The standard string representing a zero period.
     */
    private static final String ZERO = "PT0S";

    /**
     * Restricted constructor.
     */
    private PeriodParser() { }

    /**
     * Gets the singleton instance of the parser.
     *
     * @return the instance of the parser
     */
    static PeriodParser getInstance() {
        return INSTANCE;
    }

    /**
     * Obtains an instance of {@code Period} from a string.
     * <p>
     * This will parse the string produced by {@code toString()} which is
     * a subset of the ISO8601 period format {@code PnYnMnDTnHnMn.nS}.
     * <p>
     * The string consists of a series of numbers with a suffix identifying their meaning.
     * The values, and suffixes, must be in the sequence year, month, day, hour, minute, second.
     * Any of the number/suffix pairs may be omitted providing at least one is present.
     * If the period is zero, the value is normally represented as {@code PT0S}.
     * The numbers must consist of ASCII digits.
     * Any of the numbers may be negative. Negative zero is not accepted.
     * The number of nanoseconds is expressed as an optional fraction of the seconds.
     * There must be at least one digit before any decimal point.
     * There must be between 1 and 9 inclusive digits after any decimal point.
     * The letters will all be accepted in upper or lower case.
     * The decimal point may be either a dot or a comma.
     *
     * @param text  the input string in the format PnYnMnDTnHnMn.nS, validated not null
     * @return the created Period, never null
     * @throws CalendricalParseException if the text cannot be parsed to a Period
     */
    Period parse(final String text) {
        // force to upper case and coerce the comma to dot
        String s = text.toUpperCase().replace(',', '.');
        
        // check for zero and skip parse
        if (ZERO.equals(s)) {
            return Period.ZERO;
        }
        if (s.length() < 3 || s.charAt(0) != 'P') {
            throw new CalendricalParseException("Period could not be parsed: " + text, text, 0);
        }
        validateCharactersAndOrdering(s, text);
        
        // strip off the leading P
        ParseValues values = new ParseValues(text);
        String[] datetime = s.substring(1).split("T");
        switch (datetime.length) {
            case 2:
                parseDate(values, datetime[0], 1);
                parseTime(values, datetime[1], datetime[0].length() + 2);
                break;
            case 1:
                parseDate(values, datetime[0], 1);
                break;
        }
        return values.toPeriod();
    }

    private void parseDate(ParseValues values, String s, int baseIndex) {
        values.index = 0;
        while (values.index < s.length()) {
            String value = parseNumber(values, s);
            if (values.index < s.length()) {
                char c = s.charAt(values.index);
                switch(c) {
                    case 'Y': values.years = parseInt(values, value, baseIndex) ; break;
                    case 'M': values.months = parseInt(values, value, baseIndex) ; break;
                    case 'D': values.days = parseInt(values, value, baseIndex) ; break;
                    default:
                        throw new CalendricalParseException("Period could not be parsed, unrecognized letter '" +
                                c + ": " + values.text, values.text, baseIndex + values.index);
                }
                values.index++;
            }
        }
    }

    private void parseTime(ParseValues values, String s, int baseIndex) {
        values.index = 0;
        s = prepareTime(values, s, baseIndex);
        while (values.index < s.length()) {
            String value = parseNumber(values, s);
            if (values.index < s.length()) {
                char c = s.charAt(values.index);
                switch(c) {
                    case 'H': values.hours = parseInt(values, value, baseIndex) ; break;
                    case 'M': values.minutes = parseInt(values, value, baseIndex) ; break;
                    case 'S': values.seconds = parseInt(values, value, baseIndex) ; break;
                    case 'N': values.nanos = parseNanos(values, value, baseIndex); break;
                    default:
                        throw new CalendricalParseException("Period could not be parsed, unrecognized letter '" +
                                c + "': " + values.text, values.text, baseIndex + values.index);
                }
                values.index++;
            }
        }
    }

    private long parseNanos(ParseValues values, String s, int baseIndex) {
        if (s.length() > 9) {
            throw new CalendricalParseException("Period could not be parsed, nanosecond range exceeded: " +
                    values.text, values.text, baseIndex + values.index - s.length());
        }
        // pad to the right to create 10**9, then trim
        return Long.parseLong((s + "000000000").substring(0, 9));
    }

    private String prepareTime(ParseValues values, String s, int baseIndex) {
        if (s.contains(".")) {
            int i = s.indexOf(".") + 1;
            
            // verify that the first character after the dot is a digit
            if (Character.isDigit(s.charAt(i))) {
                i++;
            } else {
                throw new CalendricalParseException("Period could not be parsed, invalid decimal number: " +
                        values.text, values.text, baseIndex + values.index);
            }
            
            // verify that only digits follow the decimal point followed by an S
            while (i < s.length()) {
                // || !Character.isDigit(s.charAt(i))
                char c = s.charAt(i);
                if (Character.isDigit(c) || c == 'S') {
                    i++;
                } else {
                    throw new CalendricalParseException("Period could not be parsed, invalid decimal number: " +
                            values.text, values.text, baseIndex + values.index);
                }
            }
            s = s.replace('S', 'N').replace('.', 'S');
            if (s.contains("-0S")) {
                values.negativeSecs = true;
                s = s.replace("-0S", "0S");
            }
        }
        return s;
    }

    private int parseInt(ParseValues values, String s, int baseIndex) {
        try {
            int value = Integer.parseInt(s);
            if (s.charAt(0) == '-' && value == 0) {
                throw new CalendricalParseException("Period could not be parsed, invalid number '" +
                        s + "': " + values.text, values.text, baseIndex + values.index - s.length());
            }
            return value;
        } catch (NumberFormatException ex) {
            throw new CalendricalParseException("Period could not be parsed, invalid number '" +
                    s + "': " + values.text, values.text, baseIndex + values.index - s.length());
        }
    }

    private String parseNumber(ParseValues values, String s) {
        int start = values.index;
        while (values.index < s.length()) {
            char c = s.charAt(values.index);
            if ((c < '0' || c > '9') && c != '-') {
                break;
            }
            values.index++;
        }
        return s.substring(start, values.index);
    }

    private void validateCharactersAndOrdering(String s, String text) {
        char[] chars = s.toCharArray();
        int tokenPos = 0;
        boolean lastLetter = false;
        for (int i = 0; i < chars.length; i++) {
            if (tokenPos >= TOKEN_SEQUENCE.length()) {
                throw new CalendricalParseException("Period could not be parsed, characters after last 'S': " + text, text, i);
            }
            char c = chars[i];
            if ((c < '0' || c > '9') && c != '-' && c != '.') {
                tokenPos = TOKEN_SEQUENCE.indexOf(c, tokenPos);
                if (tokenPos < 0) {
                    throw new CalendricalParseException("Period could not be parsed, invalid character '" + c + "': " + text, text, i);
                }
                tokenPos++;
                lastLetter = true;
            } else {
                lastLetter = false;
            }
        }
        if (lastLetter == false) {
            throw new CalendricalParseException("Period could not be parsed, invalid last character: " + text, text, s.length() - 1);
        }
    }

    /**
     * Parse values container created for each parse
     */
    private static class ParseValues {
        /**
         * The number of years.
         */
        private int years;
        /**
         * The number of months.
         */
        private int months;
        /**
         * The number of days.
         */
        private int days;
        /**
         * The number of hours.
         */
        private int hours;
        /**
         * The number of minutes.
         */
        private int minutes;
        /**
         * The number of seconds.
         */
        private int seconds;
        /**
         * The number of nanoseconds.
         */
        private long nanos;
        /**
         * Whether the seconds were negative.
         */
        private boolean negativeSecs;
        /**
         * Parser position index.
         */
        private int index;
        /**
         * Original text.
         */
        private String text;
        
        ParseValues(String text) {
            this.text = text;
        }
        
        Period toPeriod() {
            return Period.of(years, months, days, hours, minutes, seconds, negativeSecs || seconds < 0 ? -nanos : nanos);
        }
    }

}
