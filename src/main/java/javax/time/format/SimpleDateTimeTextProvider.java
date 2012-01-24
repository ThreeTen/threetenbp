/*
 * Copyright (c) 2011, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.format;

import static javax.time.calendrical.ISODateTimeRule.AMPM_OF_DAY;
import static javax.time.calendrical.ISODateTimeRule.DAY_OF_WEEK;
import static javax.time.calendrical.ISODateTimeRule.MONTH_OF_YEAR;
import static javax.time.calendrical.ISODateTimeRule.QUARTER_OF_YEAR;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.time.calendrical.DateTimeField;
import javax.time.calendrical.DateTimeRule;

/**
 * The Service Provider Implementation to obtain date-time text for a rule.
 * <p>
 * This implementation is based on extraction of data from a {@link DateFormatSymbols}.
 * <p>
 * This class is thread-safe.
 *
 * @author Stephen Colebourne
 */
public final class SimpleDateTimeTextProvider extends DateTimeTextProvider {
     // TODO: Better implementation based on CLDR

    /** Cache. */
    private static final ConcurrentMap<Entry<DateTimeRule, Locale>, Object> CACHE = new ConcurrentHashMap<>(16, 0.75f, 2);
    /** Comparator. */
    private static final Comparator<Entry<String, DateTimeField>> COMPARATOR = new Comparator<Entry<String, DateTimeField>>() {
        @Override
        public int compare(Entry<String, DateTimeField> obj1, Entry<String, DateTimeField> obj2) {
            return obj2.getKey().length() - obj1.getKey().length();  // longest to shortest
        }
    };

    //-----------------------------------------------------------------------
    /** {@inheritDoc} */
    @Override
    public Locale[] getAvailableLocales() {
        return DateFormatSymbols.getAvailableLocales();
    }

    //-----------------------------------------------------------------------
    @Override
    public String getText(DateTimeField field, TextStyle style, Locale locale) {
        Object store = findStore(field.getRule(), locale);
        if (store instanceof LocaleStore) {
            return ((LocaleStore) store).getText(field, style);
        }
        return null;
    }

    @Override
    public Iterator<Entry<String, DateTimeField>> getTextIterator(DateTimeRule rule, TextStyle style, Locale locale) {
        Object store = findStore(rule, locale);
        if (store instanceof LocaleStore) {
            return ((LocaleStore) store).getTextIterator(style);
        }
        return null;
    }

    //-----------------------------------------------------------------------
    private Object findStore(DateTimeRule rule, Locale locale) {
        Entry<DateTimeRule, Locale> key = createEntry(rule, locale);
        Object store = CACHE.get(key);
        if (store == null) {
            store = createStore(rule, locale);
            CACHE.putIfAbsent(key, store);
            store = CACHE.get(key);
        }
        return store;
    }

    private Object createStore(DateTimeRule rule, Locale locale) {
        if (rule.equals(QUARTER_OF_YEAR)) {
            Map<TextStyle, Map<DateTimeField, String>> styleMap = new HashMap<>();
            Map<DateTimeField, String> map = new HashMap<>();
            map.put(rule.field(1), "Q1");
            map.put(rule.field(2), "Q2");
            map.put(rule.field(3), "Q3");
            map.put(rule.field(4), "Q4");
            styleMap.put(TextStyle.FULL, map);
            styleMap.put(TextStyle.SHORT, map);
            return new LocaleStore(styleMap);
        }
        if (rule.equals(MONTH_OF_YEAR)) {
            DateFormatSymbols oldSymbols = DateFormatSymbols.getInstance(locale);
            Map<TextStyle, Map<DateTimeField, String>> styleMap = new HashMap<>();
            DateTimeField f1 = rule.field(1);
            DateTimeField f2 = rule.field(2);
            DateTimeField f3 = rule.field(3);
            DateTimeField f4 = rule.field(4);
            DateTimeField f5 = rule.field(5);
            DateTimeField f6 = rule.field(6);
            DateTimeField f7 = rule.field(7);
            DateTimeField f8 = rule.field(8);
            DateTimeField f9 = rule.field(9);
            DateTimeField f10 = rule.field(10);
            DateTimeField f11 = rule.field(11);
            DateTimeField f12 = rule.field(12);
            String[] array = oldSymbols.getMonths();
            Map<DateTimeField, String> map = new HashMap<>();
            map.put(f1, array[Calendar.JANUARY]);
            map.put(f2, array[Calendar.FEBRUARY]);
            map.put(f3, array[Calendar.MARCH]);
            map.put(f4, array[Calendar.APRIL]);
            map.put(f5, array[Calendar.MAY]);
            map.put(f6, array[Calendar.JUNE]);
            map.put(f7, array[Calendar.JULY]);
            map.put(f8, array[Calendar.AUGUST]);
            map.put(f9, array[Calendar.SEPTEMBER]);
            map.put(f10, array[Calendar.OCTOBER]);
            map.put(f11, array[Calendar.NOVEMBER]);
            map.put(f12, array[Calendar.DECEMBER]);
            styleMap.put(TextStyle.FULL, map);
            array = oldSymbols.getShortMonths();
            map = new HashMap<>();
            map.put(f1, array[Calendar.JANUARY]);
            map.put(f2, array[Calendar.FEBRUARY]);
            map.put(f3, array[Calendar.MARCH]);
            map.put(f4, array[Calendar.APRIL]);
            map.put(f5, array[Calendar.MAY]);
            map.put(f6, array[Calendar.JUNE]);
            map.put(f7, array[Calendar.JULY]);
            map.put(f8, array[Calendar.AUGUST]);
            map.put(f9, array[Calendar.SEPTEMBER]);
            map.put(f10, array[Calendar.OCTOBER]);
            map.put(f11, array[Calendar.NOVEMBER]);
            map.put(f12, array[Calendar.DECEMBER]);
            styleMap.put(TextStyle.SHORT, map);
            return new LocaleStore(styleMap);
        }
        if (rule.equals(DAY_OF_WEEK)) {
            DateFormatSymbols oldSymbols = DateFormatSymbols.getInstance(locale);
            Map<TextStyle, Map<DateTimeField, String>> styleMap = new HashMap<>();
            DateTimeField f1 = rule.field(1);
            DateTimeField f2 = rule.field(2);
            DateTimeField f3 = rule.field(3);
            DateTimeField f4 = rule.field(4);
            DateTimeField f5 = rule.field(5);
            DateTimeField f6 = rule.field(6);
            DateTimeField f7 = rule.field(7);
            String[] array = oldSymbols.getWeekdays();
            Map<DateTimeField, String> map = new HashMap<>();
            map.put(f1, array[Calendar.MONDAY]);
            map.put(f2, array[Calendar.TUESDAY]);
            map.put(f3, array[Calendar.WEDNESDAY]);
            map.put(f4, array[Calendar.THURSDAY]);
            map.put(f5, array[Calendar.FRIDAY]);
            map.put(f6, array[Calendar.SATURDAY]);
            map.put(f7, array[Calendar.SUNDAY]);
            styleMap.put(TextStyle.FULL, map);
            array = oldSymbols.getShortWeekdays();
            map = new HashMap<>();
            map.put(f1, array[Calendar.MONDAY]);
            map.put(f2, array[Calendar.TUESDAY]);
            map.put(f3, array[Calendar.WEDNESDAY]);
            map.put(f4, array[Calendar.THURSDAY]);
            map.put(f5, array[Calendar.FRIDAY]);
            map.put(f6, array[Calendar.SATURDAY]);
            map.put(f7, array[Calendar.SUNDAY]);
            styleMap.put(TextStyle.SHORT, map);
            return new LocaleStore(styleMap);
        }
        if (rule.equals(AMPM_OF_DAY)) {
            DateFormatSymbols oldSymbols = DateFormatSymbols.getInstance(locale);
            Map<TextStyle, Map<DateTimeField, String>> styleMap = new HashMap<>();
            String[] array = oldSymbols.getAmPmStrings();
            Map<DateTimeField, String> map = new HashMap<>();
            map.put(rule.field(0), array[Calendar.AM]);
            map.put(rule.field(1), array[Calendar.PM]);
            styleMap.put(TextStyle.FULL, map);
            styleMap.put(TextStyle.SHORT, map);  // re-use, as we don't have different data
            return new LocaleStore(styleMap);
        }
        return "";  // null marker for map
    }

    //-----------------------------------------------------------------------
    /**
     * Stores the text for a single locale.
     * <p>
     * Some fields have a textual representation, such as day-of-week or month-of-year.
     * These textual representations can be captured in this class for printing
     * and parsing.
     * <p>
     * This class is immutable and thread-safe.
     *
     * @author Stephen Colebourne
     */
    static final class LocaleStore {
        /**
         * Map of value to text.
         */
        private final Map<TextStyle, Map<DateTimeField, String>> valueTextMap;
        /**
         * Parsable data.
         */
        private final Map<TextStyle, List<Entry<String, DateTimeField>>> parsable;

        //-----------------------------------------------------------------------
        /**
         * Constructor.
         *
         * @param valueTextMap  the map of values to text to store, assigned and not altered, not null
         */
        LocaleStore(Map<TextStyle, Map<DateTimeField, String>> valueTextMap) {
            this.valueTextMap = valueTextMap;
            Map<TextStyle, List<Entry<String, DateTimeField>>> map = new HashMap<>();
            List<Entry<String, DateTimeField>> allList = new ArrayList<Entry<String, DateTimeField>>();
            for (TextStyle style : valueTextMap.keySet()) {
                Map<String, Entry<String, DateTimeField>> reverse = new HashMap<>();
                for (Map.Entry<DateTimeField, String> entry : valueTextMap.get(style).entrySet()) {
                    if (reverse.put(entry.getValue(), createEntry(entry.getValue(), entry.getKey())) != null) {
                        continue;  // not parsable, try next style
                    }
                }
                List<Entry<String, DateTimeField>> list = new ArrayList<>(reverse.values());
                Collections.sort(list, COMPARATOR);
                map.put(style, list);
                allList.addAll(list);
                map.put(null, allList);
            }
            Collections.sort(allList, COMPARATOR);
            this.parsable = map;
        }

        //-----------------------------------------------------------------------
        /**
         * Gets the text for the specified field, locale and style
         * for the purpose of printing.
         *
         * @param field  the field to get text for, not null
         * @param style  the style to get text for, not null
         * @return the text for the field value, null if no text found
         */
        String getText(DateTimeField field, TextStyle style) {
            Map<DateTimeField, String> map = valueTextMap.get(style);
            return map != null ? map.get(field) : null;
        }

        /**
         * Gets an iterator of text to field for the specified style for the purpose of parsing.
         * <p>
         * The iterator must be returned in order from the longest text to the shortest.
         *
         * @param style  the style to get text for, null for all parsable text
         * @return the iterator of text to field pairs, in order from longest text to shortest text,
         *  null if the style is not parsable
         */
        Iterator<Entry<String, DateTimeField>> getTextIterator(TextStyle style) {
            List<Entry<String, DateTimeField>> list = parsable.get(style);
            return list != null ? list.iterator() : null;
        }
    }

}
