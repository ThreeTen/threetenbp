/*
 * Copyright (c) 2008-2009, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.period;

/**
 * An period parser that creates an instance of <code>Period</code> from a string using the
 * ISO8601 period format <code>PnYnMnDTnHnMn.nS</code>.
 *
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 * @author Darryl West
 */
public class PeriodParser {
    /**
     * used to validate the correct sequence of tokens
     */
    private static final String TOKEN_SEQUENCE = "PYMDTHMS";
    /**
     * the standard string representing a zero period
     */
    public static final String ZERO = "PT0S";
    
    /**
     * the singleton instance
     */
    private static final PeriodParser INSTANCE = new PeriodParser();
    
    /**
     * @param text string to be parsed
     */
    private PeriodParser() { }
    
    /**
     * @return - an instance of the parser.
     */
    public static PeriodParser getInstance() {
        return INSTANCE;
    }
    
    /**
     * Obtains an instance of <code>Period</code> from a string.
     * <p>
     * This will parse the string produced by <code>toString()</code> which is
     * a subset of the ISO8601 period format <code>PnYnMnDTnHnMn.nS</code>.
     * <p>
     * The string consists of a series of numbers with a suffix identifying their meaning.
     * The values, and suffixes, must be in the sequence year, month, day, hour, minute, second.
     * Any of the number/suffix pairs may be omitted providing at least one is present.
     * If the period is zero, the value is normally represented as <code>PT0S</code>.
     * The numbers must consist of ASCII digits.
     * Any of the numbers may be negative. Negative zero is not accepted.
     * The number of nanoseconds is expressed as an optional fraction of the seconds.
     * There must be at least one digit before any decimal point.
     * There must be between 1 and 9 inclusive digits after any decimal point.
     * The letters will all be accepted in upper or lower case.
     * The decimal point may be either a dot or a comma.
     *
     * @param text - the input string in the format PnYnMnDTnHnMn.nS
     * @return the created Period, never null
     * @throws IllegalArgumentException if the text cannot be parsed to a Period
     */
    public Period parse(final String text) {
        ParseValues values = new ParseValues(text);
        // force to upper case and coerce the comma to dot
        String s = text.toUpperCase().replace(",", ".");
        
        // check for zero and skip parse
        if (ZERO.equals(s)) {
            return Period.ZERO;
        }

        try {
            if (s.charAt(0) != 'P' || s.length() < 3 || s.contains("-0") || !validateOrdering(s)) {
                throw new Exception("Invalid Period format");
            }
            
            // strip off the leading P
            String[] datetime = s.substring(1).split("T");
            switch(datetime.length) {
                case 2:
                    parseDate(values, datetime[0]);
                    parseTime(values, datetime[1]);
                    break;
                case 1:
                    if (s.contains("T")) {
                        parseTime(values, datetime[0]);
                    } else {
                        parseDate(values, datetime[0]);
                    }
            }
            
        } catch (Throwable t) {
            throw new IllegalArgumentException("Text '" + text + "' cannot be parsed as a Period", t);
        }
        
        return values.toPeriod();
    }
    
    private void parseDate(ParseValues values, String s) throws Exception {
        values.index = 0;
        while (values.index < s.length()) {
            String value = parseNumber(values, s);
            if (values.index < s.length()) {
                char c = s.charAt(values.index);
                switch(c) {
                    case 'Y': values.years = parseInt(value) ; break;
                    case 'M': values.months = parseInt(value) ; break;
                    case 'D': values.days = parseInt(value) ; break;
                    default:
                        throw new Exception("Period date parse format does not recognize: " + s.charAt(values.index));
                }
                
                values.index++;
            }
        }
    }
    
    private void parseTime(ParseValues values, String s) throws Exception {
        values.index = 0;
        s = prepareTime(s);
        while (values.index < s.length()) {
            String value = parseNumber(values, s);
            if (values.index < s.length()) {
                char c = s.charAt(values.index);
                switch(c) {
                    case 'H': values.hours = parseInt(value) ; break;
                    case 'M': values.minutes = parseInt(value) ; break;
                    case 'S': values.seconds = parseInt(value) ; break;
                    case 'N': values.nanos = parseNanos(value); break;
                    default:
                        throw new Exception("Period time parse format does not recognize: " + s.charAt(values.index));
                }
                
                values.index++;
            }
        }
    }
    
    private long parseNanos(String s) throws Exception {
        if (s.length() > 9) {
            throw new Exception("Period parse nano second range exceeded for: " + s);
        }
        // pad to the right to create 10**9, then trim
        return Long.parseLong((s + "000000000").substring(0, 9));
    }
    
    private String prepareTime(String s) throws Exception {
        if (s.contains(".")) {
            if (s.contains("1.S")) {
                System.out.println("ok...");
            }
            int i = s.indexOf(".") + 1;
            
            // verify that the first character after the dot is a digit
            if (Character.isDigit(s.charAt(i))) {
                i++;
            } else {
                throw new Exception("Decimal point must be followed by a digit");
            }
            
            // verify that only digits follow the decimal point followed by an S
            while (i < s.length()) {
                // || !Character.isDigit(s.charAt(i))
                char c = s.charAt(i);
                if (Character.isDigit(c) || c == 'S') {
                    i++;
                } else {
                    throw new Exception("Decimal point must be followed by a digit");
                }
            }
            
            s = s.replace("S", "N").replace(".", "S");
        }

        return s;
    }
    
    private int parseInt(String s) throws Exception {
        return Integer.parseInt(s);
    }
    
    private String parseNumber(ParseValues values, String s) {
        
        StringBuilder sb = new StringBuilder();
        while (values.index < s.length()) {
            char c = s.charAt(values.index);
            if (Character.isDigit(c) || c == '-') {
                sb.append(c);
            } else {
                break;
            }
            
            values.index++;
        }
        
        return sb.toString();
    }
    
    private boolean validateOrdering(String s) {
        if (s == null) {
            return false;
        }

        int idx = 0;
        char[] chars = s.toCharArray();

        for (int i = 0; i < chars.length && idx < TOKEN_SEQUENCE.length(); i++) {
            char c = chars[i];
            if (Character.isLetter(c)) {
                int n = TOKEN_SEQUENCE.substring(idx).indexOf(c);
                if (n < 0) {
                    return false;
                    }

                idx += n;
                }
            }
    
        return true;
    }
    
    /**
     * Parse values container created for each parse
     */
    private class ParseValues {
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
         * parser position index
         */
        private int index;
        /**
         * the original text to be parsed
         */
        private final String text;
        
        ParseValues(final String text) {
            if (text == null) {
                throw new IllegalArgumentException("Parse text must not be null");
            }
            this.text = text;
        }
        
        Period toPeriod() {
            return Period.period(years, months, days, hours, minutes, seconds, nanos);
        }
    }
    
}
