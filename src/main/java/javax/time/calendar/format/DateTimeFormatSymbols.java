/*
 * Copyright (c) 2008, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.calendar.format;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.time.calendar.DateTimeFieldRule;
import javax.time.calendar.format.DateTimeFormatterBuilder.TextStyle;

/**
 * Symbols used for date and time formatting.
 *
 * @author Stephen Colebourne
 */
public final class DateTimeFormatSymbols {

    /**
     * The locale for printing and parsing text.
     */
    private final Locale locale;
    /**
     * The zero character.
     */
    private final char zeroChar = '0';
    /**
     * The positive sign character.
     */
    private final char positiveSignChar = '+';
    /**
     * The negative sign character.
     */
    private final char negativeSignChar = '-';
    /**
     * The date format symbols.
     */
    private final Map<String, Map<TextStyle, Map<Integer, String>>> textMap;

    //-----------------------------------------------------------------------
    /**
     * Returns an array of all locales for which the <code>getInstance</code>
     * methods of this class can return localized instances. The returned array
     * represents the union of locales supported by the Java runtime and by
     * installed
     * {@link java.text.spi.DateTimeFormatSymbolsProvider DateTimeFormatSymbolsProvider}
     * implementations. It must contain at least a <code>Locale</code>
     * instance equal to {@link java.util.Locale#US Locale.US}.
     *
     * @return an array of locales for which localized
     *         <code>DateTimeFormatSymbols</code> instances are available
     */
    public static Locale[] getAvailableLocales() {
//        LocaleServiceProviderPool pool = LocaleServiceProviderPool
//                .getPool(DateFormatSymbolsProvider.class);
//        return pool.getAvailableLocales();
        return new Locale[] {Locale.US};
    }

    /**
     * Gets the <code>DateFormatSymbols</code> instance for the default
     * locale. This method provides access to <code>DateFormatSymbols</code>
     * instances for locales supported by the Java runtime itself as well as for
     * those supported by installed
     * {@link java.text.spi.DateFormatSymbolsProvider DateFormatSymbolsProvider}
     * implementations.
     *
     * @return a <code>DateTimeFormatSymbols</code> instance.
     */
    public static DateTimeFormatSymbols getInstance() {
        return getInstance(Locale.getDefault());
    }

    /**
     * Gets the <code>DateFormatSymbols</code> instance for the specified
     * locale. This method provides access to <code>DateFormatSymbols</code>
     * instances for locales supported by the Java runtime itself as well as for
     * those supported by installed
     * {@link java.text.spi.DateFormatSymbolsProvider DateFormatSymbolsProvider}
     * implementations.
     *
     * @param locale  the given locale, not null
     * @return a <code>DateTimeFormatSymbols</code> instance
     * @throws NullPointerException if <code>locale</code> is null
     */
    public static DateTimeFormatSymbols getInstance(Locale locale) {
//        // Check whether a provider can provide an implementation that's closer
//        // to the requested locale than what the Java runtime itself can provide
//        LocaleServiceProviderPool pool = LocaleServiceProviderPool.getPool(DateFormatSymbolsProvider.class);
//        if (pool.hasProviders()) {
//            DateTimeFormatSymbols providersInstance = pool.getLocalizedObject(
//                    DateTimeFormatSymbolsGetter.INSTANCE, locale);
//            if (providersInstance != null) {
//                return providersInstance;
//            }
//        }
//
        return new DateTimeFormatSymbols(locale);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param locale  the locale, not null
     */
    public DateTimeFormatSymbols(Locale locale) {
        this.locale = locale;
        textMap = new HashMap<String, Map<TextStyle, Map<Integer, String>>>();
    }

//    /**
//     * Constructor.
//     *
//     * @param locale  the locale, not null
//     * @param oldSymbols  the old symbols
//     */
//    private DateTimeFormatSymbols(Locale locale, DateFormatSymbols oldSymbols) {
//        this(locale);
//        // TODO
//    }

    //-----------------------------------------------------------------------
    /**
     * Gets the locale to use for printing and parsing text.
     * <p>
     * The locale information for printing and parsing numbers is defined in
     * the zero, negative and positive characters.
     *
     * @return the character for zero
     */
    public Locale getLocale() {
        return locale;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the character that represents zero.
     * <p>
     * This character can vary by locale.
     *
     * @return the character for zero
     */
    public char getZeroChar() {
        return zeroChar;
    }

    /**
     * Gets the character that represents the specified digit.
     * <p>
     * This character can vary by locale.
     * The characters for one to nine are based on the stored zero character.
     *
     * @param digit  the single digit to convert to a character, from 0 to 9
     * @return the character for the digit
     */
    public char getDigitChar(int digit) {
        return (char) (digit + zeroChar);
    }

    /**
     * Checks whether the character is a digit, based on the currently set zero character.
     *
     * @param ch  the character to check
     * @return the value, 0 to 9, of the character, or -1 if not a digit
     */
    public int convertToDigit(char ch) {
        int val = ch - zeroChar;
        return (val >= 0 && val <= 9) ? val : -1;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the character that represents the positive sign.
     * <p>
     * This character can vary by locale.
     *
     * @return the character for the positive sign
     */
    public char getPositiveSignChar() {
        return positiveSignChar;
    }

    /**
     * Gets the character that represents the negative sign.
     * <p>
     * This character can vary by locale.
     *
     * @return the character for the negative sign
     */
    public char getNegativeSignChar() {
        return negativeSignChar;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the text for the specified field.
     * <p>
     * The text associated with the style, field and value is returned. For example,
     * the full text for the month field value 3 in English is 'March'.
     * <p>
     * The value returned should be valid, however no exception will be thrown
     * if it is invalid.
     *
     * @param fieldRule  the field to get text for, not null
     * @param textStyle  the text style, not null
     * @param value  the value to get text for
     * @return the text for the field value, null if no text found
     */
    public String getFieldValueText(DateTimeFieldRule fieldRule, TextStyle textStyle, int value) {
        if (fieldRule == null) {
            throw new NullPointerException("The field rule must not be null");
        }
        if (textStyle == null) {
            throw new NullPointerException("The text style must not be null");
        }
        String id = fieldRule.getID();
        Map<TextStyle, Map<Integer, String>> styleMap = textMap.get(id);
        if (styleMap != null) {
            Map<Integer, String> valueTextMap = styleMap.get(textStyle);
            if (valueTextMap != null) {
                return valueTextMap.get(value);
            }
        }
        return null;
    }

    /**
     * Gets the text for the specified field.
     * <p>
     * The text associated with the style, field and value is returned. For example,
     * the full text for the month field value 3 in English is 'March'.
     * <p>
     * The value returned should be valid, however no exception will be thrown
     * if it is invalid.
     *
     * @param fieldRule  the field to get text for, not null
     * @param textStyle  the text style, not null
     * @param text  the text to match against
     * @return an array of size two consisting of the matched length and the matched value, never null
     */
    public int[] matchFieldValueText(DateTimeFieldRule fieldRule, TextStyle textStyle, String text) {
        if (fieldRule == null) {
            throw new NullPointerException("The field rule must not be null");
        }
        if (textStyle == null) {
            throw new NullPointerException("The text style must not be null");
        }
        if (text == null) {
            throw new NullPointerException("The text must not be null");
        }
        String id = fieldRule.getID();
        Map<TextStyle, Map<Integer, String>> styleMap = textMap.get(id);
        if (styleMap != null) {
            Map<Integer, String> valueTextMap = styleMap.get(textStyle);
            if (valueTextMap != null) {
                // TODO cache reversed map and maxlength
                int maxLength = 0;
                Map<String, Integer> reverse = new HashMap<String, Integer>();
                for (Entry<Integer, String> entry : valueTextMap.entrySet()) {
                    reverse.put(entry.getValue(), entry.getKey());
                    maxLength = Math.max(maxLength, entry.getKey());
                }
                for (int i = maxLength; i > 0; i--) {
                    Integer value = reverse.get(text.substring(0, i));
                    if (value != null) {
                        return new int[] {i, value};
                    }
                }
                return new int[] {0, 0};
            }
        }
        return null;
    }

//    //-----------------------------------------------------------------------
//    /**
//     * Outputs the flexi date-time as a <code>String</code>.
//     * <p>
//     * The output will use the following format:
//     * <ul>
//     * <li>Field-Value map, followed by space if non-empty</li>
//     * <li>Date</li>
//     * <li>Time, prefixed by 'T' if non-null</li>
//     * <li>Offset</li>
//     * <li>Zone, prefixed by a space if non-null</li>
//     * </ul>
//     * If an instance of LocalDate, LocalTime, LocalDateTime, OffsetDate, OffsetTime,
//     * OffsetDateTime or ZonedDateTime is converted to a FlexiDateTime then the
//     * toString output will remain the same.
//     *
//     * @return the formatted date-time string, never null
//     */
//    @Override
//    public String toString() {
//        StringBuilder buf = new StringBuilder();
//        if (getFieldValueMap().size() > 0) {
//            buf.append(getFieldValueMap());
//            if (date != null || time != null || offset != null) {
//                buf.append(' ');
//            }
//        }
//        if (date != null) {
//            buf.append(date);
//        }
//        if (time != null) {
//            buf.append('T').append(time);
//        }
//        if (offset != null) {
//            buf.append(offset);
//        }
//        if (zone != null) {
//            if (date != null || time != null || offset != null) {
//                buf.append(' ');
//            }
//            buf.append(zone);
//        }
//        return buf.toString();
//    }

//    /**
//     * Obtains an instance of the symbols from the provider.
//     */
//    private static class DateTimeFormatSymbolsGetter
//            implements LocaleServiceProviderPool.LocalizedObjectGetter<DateFormatSymbolsProvider, DateTimeFormatSymbols> {
//        /** Singleton instance. */
//        private static final DateTimeFormatSymbolsGetter INSTANCE = new DateTimeFormatSymbolsGetter();
//
//        public DateTimeFormatSymbols getObject(
//                DateFormatSymbolsProvider provider, Locale locale, String key, Object... params) {
//            assert params.length == 0;
//            DateFormatSymbols oldSymbols = provider.getInstance(locale);
//            return new DateTimeFormatSymbols(locale, oldSymbols);
//        }
//    }
}
