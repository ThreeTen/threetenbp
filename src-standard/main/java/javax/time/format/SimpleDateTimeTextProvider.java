/*
 * Copyright (c) 2011-2012, Stephen Colebourne & Michael Nascimento Santos
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

import static javax.time.calendrical.LocalDateTimeField.AMPM_OF_DAY;
import static javax.time.calendrical.LocalDateTimeField.DAY_OF_WEEK;
import static javax.time.calendrical.LocalDateTimeField.MONTH_OF_YEAR;

import java.text.DateFormatSymbols;
import java.util.AbstractMap.SimpleImmutableEntry;
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
import javax.time.calendrical.QuarterYearField;

/**
 * The Service Provider Implementation to obtain date-time text for a field.
 * <p>
 * This implementation is based on extraction of data from a {@link DateFormatSymbols}.
 * 
 * <h4>Implementation notes</h4>
 * This class is immutable and thread-safe.
 */
final class SimpleDateTimeTextProvider extends DateTimeTextProvider {
     // TODO: Better implementation based on CLDR

    /** Cache. */
    private static final ConcurrentMap<Entry<DateTimeField, Locale>, Object> CACHE =
        new ConcurrentHashMap<Entry<DateTimeField, Locale>, Object>(16, 0.75f, 2);
    /** Comparator. */
    private static final Comparator<Entry<String, Long>> COMPARATOR = new Comparator<Entry<String, Long>>() {
        @Override
        public int compare(Entry<String, Long> obj1, Entry<String, Long> obj2) {
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
    public String getText(DateTimeField field, long value, TextStyle style, Locale locale) {
        Object store = findStore(field, locale);
        if (store instanceof LocaleStore) {
            return ((LocaleStore) store).getText(value, style);
        }
        return null;
    }

    @Override
    public Iterator<Entry<String, Long>> getTextIterator(DateTimeField field, TextStyle style, Locale locale) {
        Object store = findStore(field, locale);
        if (store instanceof LocaleStore) {
            return ((LocaleStore) store).getTextIterator(style);
        }
        return null;
    }

    //-----------------------------------------------------------------------
    private Object findStore(DateTimeField field, Locale locale) {
        Entry<DateTimeField, Locale> key = createEntry(field, locale);
        Object store = CACHE.get(key);
        if (store == null) {
            store = createStore(field, locale);
            CACHE.putIfAbsent(key, store);
            store = CACHE.get(key);
        }
        return store;
    }

    private Object createStore(DateTimeField field, Locale locale) {
        if (field == QuarterYearField.QUARTER_OF_YEAR) {
            Map<TextStyle, Map<Long, String>> styleMap = new HashMap<TextStyle, Map<Long, String>>();
            Map<Long, String> map = new HashMap<Long, String>();
            map.put(1L, "Q1");
            map.put(2L, "Q2");
            map.put(3L, "Q3");
            map.put(4L, "Q4");
            styleMap.put(TextStyle.FULL, map);
            styleMap.put(TextStyle.SHORT, map);
            return new LocaleStore(styleMap);
        }
        if (field == MONTH_OF_YEAR) {
            DateFormatSymbols oldSymbols = DateFormatSymbols.getInstance(locale);
            Map<TextStyle, Map<Long, String>> styleMap = new HashMap<TextStyle, Map<Long, String>>();
            Long f1 = 1L;
            Long f2 = 2L;
            Long f3 = 3L;
            Long f4 = 4L;
            Long f5 = 5L;
            Long f6 = 6L;
            Long f7 = 7L;
            Long f8 = 8L;
            Long f9 = 9L;
            Long f10 = 10L;
            Long f11 = 11L;
            Long f12 = 12L;
            String[] array = oldSymbols.getMonths();
            Map<Long, String> map = new HashMap<Long, String>();
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
            map = new HashMap<Long, String>();
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
        if (field == DAY_OF_WEEK) {
            DateFormatSymbols oldSymbols = DateFormatSymbols.getInstance(locale);
            Map<TextStyle, Map<Long, String>> styleMap = new HashMap<TextStyle, Map<Long, String>>();
            Long f1 = 1L;
            Long f2 = 2L;
            Long f3 = 3L;
            Long f4 = 4L;
            Long f5 = 5L;
            Long f6 = 6L;
            Long f7 = 7L;
            String[] array = oldSymbols.getWeekdays();
            Map<Long, String> map = new HashMap<Long, String>();
            map.put(f1, array[Calendar.MONDAY]);
            map.put(f2, array[Calendar.TUESDAY]);
            map.put(f3, array[Calendar.WEDNESDAY]);
            map.put(f4, array[Calendar.THURSDAY]);
            map.put(f5, array[Calendar.FRIDAY]);
            map.put(f6, array[Calendar.SATURDAY]);
            map.put(f7, array[Calendar.SUNDAY]);
            styleMap.put(TextStyle.FULL, map);
            array = oldSymbols.getShortWeekdays();
            map = new HashMap<Long, String>();
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
        if (field == AMPM_OF_DAY) {
            DateFormatSymbols oldSymbols = DateFormatSymbols.getInstance(locale);
            Map<TextStyle, Map<Long, String>> styleMap = new HashMap<TextStyle, Map<Long, String>>();
            String[] array = oldSymbols.getAmPmStrings();
            Map<Long, String> map = new HashMap<Long, String>();
            map.put(0L, array[Calendar.AM]);
            map.put(1L, array[Calendar.PM]);
            styleMap.put(TextStyle.FULL, map);
            styleMap.put(TextStyle.SHORT, map);  // re-use, as we don't have different data
            return new LocaleStore(styleMap);
        }
        return "";  // null marker for map
    }

    //-----------------------------------------------------------------------
    /**
     * Helper method to create an immutable entry.
     * 
     * @param text  the text, not null
     * @param field  the field, not null
     * @return the entry, not null
     */
    private static <A, B> Entry<A, B> createEntry(A text, B field) {
        return new SimpleImmutableEntry<A, B>(text, field);
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
     */
    static final class LocaleStore {
        /**
         * Map of value to text.
         */
        private final Map<TextStyle, Map<Long, String>> valueTextMap;
        /**
         * Parsable data.
         */
        private final Map<TextStyle, List<Entry<String, Long>>> parsable;

        //-----------------------------------------------------------------------
        /**
         * Constructor.
         *
         * @param valueTextMap  the map of values to text to store, assigned and not altered, not null
         */
        LocaleStore(Map<TextStyle, Map<Long, String>> valueTextMap) {
            this.valueTextMap = valueTextMap;
            Map<TextStyle, List<Entry<String, Long>>> map = new HashMap<TextStyle, List<Entry<String, Long>>>();
            List<Entry<String, Long>> allList = new ArrayList<Entry<String, Long>>();
            for (TextStyle style : valueTextMap.keySet()) {
                Map<String, Entry<String, Long>> reverse = new HashMap<String, Entry<String, Long>>();
                for (Map.Entry<Long, String> entry : valueTextMap.get(style).entrySet()) {
                    if (reverse.put(entry.getValue(), createEntry(entry.getValue(), entry.getKey())) != null) {
                        continue;  // not parsable, try next style
                    }
                }
                List<Entry<String, Long>> list = new ArrayList<Entry<String, Long>>(reverse.values());
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
         * Gets the text for the specified field value, locale and style
         * for the purpose of printing.
         *
         * @param value  the value to get text for, not null
         * @param style  the style to get text for, not null
         * @return the text for the field value, null if no text found
         */
        String getText(long value, TextStyle style) {
            Map<Long, String> map = valueTextMap.get(style);
            return map != null ? map.get(value) : null;
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
        Iterator<Entry<String, Long>> getTextIterator(TextStyle style) {
            List<Entry<String, Long>> list = parsable.get(style);
            return list != null ? list.iterator() : null;
        }
    }

}
