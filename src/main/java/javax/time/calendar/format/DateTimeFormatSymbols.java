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

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.time.calendar.DateTimeFieldRule;
import javax.time.calendar.ISOChronology;
import javax.time.calendar.field.DayOfWeek;
import javax.time.calendar.field.AmPmOfDay;
import javax.time.calendar.field.MonthOfYear;
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
     * The decimal point character.
     */
    private final char decimalPointChar = '.';
    /**
     * The date format symbols.
     */
    private final Map<String, Map<TextStyle, TextStore>> textMap;

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
//        return new Locale[] {Locale.US};
        //TODO: check
        return Locale.getAvailableLocales();
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
        FormatUtil.checkNotNull(locale, "locale");
        DateFormatSymbols symbols = new DateFormatSymbols(locale);
        return new DateTimeFormatSymbols(locale, symbols);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param locale  the locale, not null
     */
    private DateTimeFormatSymbols(Locale locale) {
        FormatUtil.checkNotNull(locale, "locale");
        this.locale = locale;
        textMap = new HashMap<String, Map<TextStyle, TextStore>>();
    }

    /**
     * Constructor.
     *
     * @param locale  the locale, not null
     * @param oldSymbols  the old symbols, not null
     */
    private DateTimeFormatSymbols(Locale locale, DateFormatSymbols oldSymbols) {
        this(locale);
        FormatUtil.checkNotNull(oldSymbols, "symbols to convert");
        
        Map<Integer, String> map = new HashMap<Integer, String>();
        String[] array = null;
        String id = null;
        
//        // eras
//        id = ISOChronology.INSTANCE.era().getID();
//        textMap.put(id, new HashMap<TextStyle, TextStore>());
//        map.clear();
//        array = oldSymbols.getEras();
//        // TODO
//        textMap.get(id).put(TextStyle.FULL, new TextStore(map));
        
        // months
        id = ISOChronology.INSTANCE.monthOfYear().getID();
        textMap.put(id, new HashMap<TextStyle, TextStore>());
        map.clear();
        array = oldSymbols.getMonths();
        map.put(MonthOfYear.JANUARY.getValue(), array[Calendar.JANUARY]);
        map.put(MonthOfYear.FEBRUARY.getValue(), array[Calendar.FEBRUARY]);
        map.put(MonthOfYear.MARCH.getValue(), array[Calendar.MARCH]);
        map.put(MonthOfYear.APRIL.getValue(), array[Calendar.APRIL]);
        map.put(MonthOfYear.MAY.getValue(), array[Calendar.MAY]);
        map.put(MonthOfYear.JUNE.getValue(), array[Calendar.JUNE]);
        map.put(MonthOfYear.JULY.getValue(), array[Calendar.JULY]);
        map.put(MonthOfYear.AUGUST.getValue(), array[Calendar.AUGUST]);
        map.put(MonthOfYear.SEPTEMBER.getValue(), array[Calendar.SEPTEMBER]);
        map.put(MonthOfYear.OCTOBER.getValue(), array[Calendar.OCTOBER]);
        map.put(MonthOfYear.NOVEMBER.getValue(), array[Calendar.NOVEMBER]);
        map.put(MonthOfYear.DECEMBER.getValue(), array[Calendar.DECEMBER]);
        textMap.get(id).put(TextStyle.FULL, new TextStore(map));
        map.clear();
        array = oldSymbols.getShortMonths();
        map.put(MonthOfYear.JANUARY.getValue(), array[Calendar.JANUARY]);
        map.put(MonthOfYear.FEBRUARY.getValue(), array[Calendar.FEBRUARY]);
        map.put(MonthOfYear.MARCH.getValue(), array[Calendar.MARCH]);
        map.put(MonthOfYear.APRIL.getValue(), array[Calendar.APRIL]);
        map.put(MonthOfYear.MAY.getValue(), array[Calendar.MAY]);
        map.put(MonthOfYear.JUNE.getValue(), array[Calendar.JUNE]);
        map.put(MonthOfYear.JULY.getValue(), array[Calendar.JULY]);
        map.put(MonthOfYear.AUGUST.getValue(), array[Calendar.AUGUST]);
        map.put(MonthOfYear.SEPTEMBER.getValue(), array[Calendar.SEPTEMBER]);
        map.put(MonthOfYear.OCTOBER.getValue(), array[Calendar.OCTOBER]);
        map.put(MonthOfYear.NOVEMBER.getValue(), array[Calendar.NOVEMBER]);
        map.put(MonthOfYear.DECEMBER.getValue(), array[Calendar.DECEMBER]);
        textMap.get(id).put(TextStyle.SHORT, new TextStore(map));
        
        // day of week
        id = ISOChronology.INSTANCE.dayOfWeek().getID();
        textMap.put(id, new HashMap<TextStyle, TextStore>());
        map.clear();
        array = oldSymbols.getWeekdays();
        map.put(DayOfWeek.MONDAY.getValue(), array[Calendar.MONDAY]);
        map.put(DayOfWeek.TUESDAY.getValue(), array[Calendar.TUESDAY]);
        map.put(DayOfWeek.WEDNESDAY.getValue(), array[Calendar.WEDNESDAY]);
        map.put(DayOfWeek.THURSDAY.getValue(), array[Calendar.THURSDAY]);
        map.put(DayOfWeek.FRIDAY.getValue(), array[Calendar.FRIDAY]);
        map.put(DayOfWeek.SATURDAY.getValue(), array[Calendar.SATURDAY]);
        map.put(DayOfWeek.SUNDAY.getValue(), array[Calendar.SUNDAY]);
        textMap.get(id).put(TextStyle.FULL, new TextStore(map));
        map.clear();
        array = oldSymbols.getShortWeekdays();
        map.put(DayOfWeek.MONDAY.getValue(), array[Calendar.MONDAY]);
        map.put(DayOfWeek.TUESDAY.getValue(), array[Calendar.TUESDAY]);
        map.put(DayOfWeek.WEDNESDAY.getValue(), array[Calendar.WEDNESDAY]);
        map.put(DayOfWeek.THURSDAY.getValue(), array[Calendar.THURSDAY]);
        map.put(DayOfWeek.FRIDAY.getValue(), array[Calendar.FRIDAY]);
        map.put(DayOfWeek.SATURDAY.getValue(), array[Calendar.SATURDAY]);
        map.put(DayOfWeek.SUNDAY.getValue(), array[Calendar.SUNDAY]);
        textMap.get(id).put(TextStyle.SHORT, new TextStore(map));
        
        // am pm
        id = ISOChronology.INSTANCE.amPmOfDay().getID();
        textMap.put(id, new HashMap<TextStyle, TextStore>());
        map.clear();
        array = oldSymbols.getAmPmStrings();
        map.put(AmPmOfDay.AM.getValue(), array[Calendar.AM]);
        map.put(AmPmOfDay.PM.getValue(), array[Calendar.PM]);
        textMap.get(id).put(TextStyle.FULL, new TextStore(map));
        textMap.get(id).put(TextStyle.SHORT, new TextStore(map));  // no data for short so reuse full
    }

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
     * Gets the character that represents the decimal point.
     * <p>
     * This character can vary by locale.
     *
     * @return the character for the decimal point
     */
    public char getDecimalPointChar() {
        return decimalPointChar;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the map of text for each value of the specified field.
     * <p>
     * The map of integer value to text is returned for the specified field
     * and text style. There may be no text defined for the given input, in
     * which case <code>null</code> is returned. The returned map is immutable.
     *
     * @param fieldRule  the field to get text for, not null
     * @param textStyle  the text style, not null
     * @return the map of value to text for the field rule and style, null if no text defined
     */
    public Map<Integer, String> getFieldValueTextMap(DateTimeFieldRule fieldRule, TextStyle textStyle) {
        TextStore store = getTextStore(fieldRule, textStyle);
        return store == null ? null : store.valueTextMap;
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
     * @param value  the value to get text for
     * @return the text for the field value, null if no text found
     */
    public String getFieldValueText(DateTimeFieldRule fieldRule, TextStyle textStyle, int value) {
        Map<Integer, String> valueTextMap = getFieldValueTextMap(fieldRule, textStyle);
        return valueTextMap == null ? null : valueTextMap.get(value);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the map of the value for the text representation of the specified field.
     * <p>
     * The map of text to integer value is returned for the specified field
     * and text style. There may be no text defined for the given input, in
     * which case <code>null</code> is returned. The returned map is immutable.
     *
     * @param fieldRule  the field to get text for, not null
     * @param textStyle  the text style, not null
     * @return the map of value to text for the field rule and style, null if
     *  no text defined or parsing is not supported
     */
    public Map<String, Integer> getFieldTextValueMap(DateTimeFieldRule fieldRule, TextStyle textStyle) {
        TextStore store = getTextStore(fieldRule, textStyle);
        return store == null ? null : store.textValueMap;
    }

    /**
     * Matches the specified text against the known symbols returning the
     * matching text length and value.
     * <p>
     * This method is intended for use during parsing, and matches the start of
     * specified search text against the known symbols, optionally ignoring case.
     *
     * @param fieldRule  the field to get text for, not null
     * @param textStyle  the text style, not null
     * @param ignoreCase  true to ignore case during the matching
     * @param searchText  the text to match against
     * @return an array of size two consisting of the matched length and the matched value,
     *  null if there are no text symbols for the field and text style
     */
    public int[] matchFieldText(DateTimeFieldRule fieldRule, TextStyle textStyle, boolean ignoreCase, String searchText) {
        TextStore store = getTextStore(fieldRule, textStyle);
        FormatUtil.checkNotNull(searchText, "search text");
        if (store == null) {
            return null;
        }
        if (ignoreCase) {
            int maxLength = store.insensitiveMaxLength;
            Map<String, Integer> textValueMap = store.insensitiveTextValueMap;
            searchText = searchText.toUpperCase(locale);
            for (int i = Math.min(maxLength, searchText.length()); i > 0; i--) {
                Integer value = textValueMap.get(searchText.substring(0, i));
                if (value != null) {
                    return new int[] {i, value};
                }
            }
            searchText = searchText.toLowerCase(locale);
            for (int i = Math.min(maxLength, searchText.length()); i > 0; i--) {
                Integer value = textValueMap.get(searchText.substring(0, i));
                if (value != null) {
                    return new int[] {i, value};
                }
            }
        } else {
            int maxLength = store.maxLength;
            Map<String, Integer> textValueMap = store.textValueMap;
            for (int i = Math.min(maxLength, searchText.length()); i > 0; i--) {
                Integer value = textValueMap.get(searchText.substring(0, i));
                if (value != null) {
                    return new int[] {i, value};
                }
            }
        }
        return new int[] {0, 0};
    }

    /**
     * Gets the text store.
     *
     * @param fieldRule  the field to get text for, not null
     * @param textStyle  the text style, not null
     * @return the text store, null if no text defined
     */
    private TextStore getTextStore(DateTimeFieldRule fieldRule, TextStyle textStyle) {
        FormatUtil.checkNotNull(fieldRule, "field rule");
        FormatUtil.checkNotNull(textStyle, "text style");
        String id = fieldRule.getID();
        Map<TextStyle, TextStore> styleMap = textMap.get(id);
        return styleMap == null ? null : styleMap.get(textStyle);
    }

    //-----------------------------------------------------------------------
    /**
     * The internal store of text/value.
     */
    private class TextStore {
        /** Map of value to text. */
        private final Map<Integer, String> valueTextMap;
        /** Map of text to value. */
        private final Map<String, Integer> textValueMap;
        /** The maximum length of any text item. */
        private final int maxLength;
        /** Map of case insensitive text to value. */
        private final Map<String, Integer> insensitiveTextValueMap;
        /** The maximum length of any text item in the insensitive map. */
        private final int insensitiveMaxLength;
        
        /**
         * Constructor.
         *
         * @param map  the map of value to text, not null
         * @throws IllegalArgumentException if the map contains null or empty text
         */
        private TextStore(Map<Integer, String> map) {
            FormatUtil.checkNotNull(map, "text map");
            if (map.containsKey(null) || map.containsValue(null) || map.containsValue("")) {
                throw new IllegalArgumentException("The map must not contain null or empty text");
            }
            Map<Integer, String> copy = new HashMap<Integer, String>(map);
            Map<String, Integer> reverse = new HashMap<String, Integer>();
            int maxLength = 0;
            Map<String, Integer> insensitive = new HashMap<String, Integer>();
            int insensitiveMaxLength = 0;
            for (Entry<Integer, String> entry : copy.entrySet()) {
                String text = entry.getValue();
                Integer value = entry.getKey();
                reverse.put(text, value);
                maxLength = Math.max(maxLength, text.length());
                insensitive.put(text.toLowerCase(locale), value);
                insensitive.put(text.toUpperCase(locale), value);
                insensitiveMaxLength = Math.max(maxLength, text.length());
            }
            if (reverse.size() < copy.size()) {
                // duplicate text for a given value, so parsing is not supported
                this.textValueMap = null;
                maxLength = 0;
                this.insensitiveTextValueMap = null;
                insensitiveMaxLength = 0;
            } else {
                textValueMap = Collections.unmodifiableMap(reverse);
                insensitiveTextValueMap = Collections.unmodifiableMap(insensitive);
            }
            this.valueTextMap = Collections.unmodifiableMap(copy);
            this.maxLength = maxLength;
            this.insensitiveMaxLength = insensitiveMaxLength;
        }
    }

}
