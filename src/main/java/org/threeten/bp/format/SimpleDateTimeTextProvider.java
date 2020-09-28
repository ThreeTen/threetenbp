/*
 * Copyright (c) 2007-present, Stephen Colebourne & Michael Nascimento Santos
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
package org.threeten.bp.format;

import static org.threeten.bp.temporal.ChronoField.AMPM_OF_DAY;
import static org.threeten.bp.temporal.ChronoField.DAY_OF_WEEK;
import static org.threeten.bp.temporal.ChronoField.ERA;
import static org.threeten.bp.temporal.ChronoField.MONTH_OF_YEAR;

import java.text.DateFormatSymbols;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.threeten.bp.temporal.IsoFields;
import org.threeten.bp.temporal.TemporalField;

/**
 * The Service Provider Implementation to obtain date-time text for a field.
 * <p>
 * This implementation is based on extraction of data from a {@link DateFormatSymbols}.
 *
 * <h3>Specification for implementors</h3>
 * This class is immutable and thread-safe.
 */
final class SimpleDateTimeTextProvider extends DateTimeTextProvider {
     // TODO: Better implementation based on CLDR

    /** Comparator. */
    private static final Comparator<Entry<String, Long>> COMPARATOR = new Comparator<Entry<String, Long>>() {
        @Override
        public int compare(Entry<String, Long> obj1, Entry<String, Long> obj2) {
            return obj2.getKey().length() - obj1.getKey().length();  // longest to shortest
        }
    };

    /** Cache. */
    private final ConcurrentMap<Entry<TemporalField, Locale>, Object> cache =
            new ConcurrentHashMap<Entry<TemporalField, Locale>, Object>(16, 0.75f, 2);

    //-----------------------------------------------------------------------
    @Override
    public String getText(TemporalField field, long value, TextStyle style, Locale locale) {
        Object store = findStore(field, locale);
        if (store instanceof LocaleStore) {
            return ((LocaleStore) store).getText(value, style);
        }
        return null;
    }

    @Override
    public Iterator<Entry<String, Long>> getTextIterator(TemporalField field, TextStyle style, Locale locale) {
        Object store = findStore(field, locale);
        if (store instanceof LocaleStore) {
            return ((LocaleStore) store).getTextIterator(style);
        }
        return null;
    }

    //-----------------------------------------------------------------------
    private Object findStore(TemporalField field, Locale locale) {
        Entry<TemporalField, Locale> key = createEntry(field, locale);
        Object store = cache.get(key);
        if (store == null) {
            store = createStore(field, locale);
            cache.putIfAbsent(key, store);
            store = cache.get(key);
        }
        return store;
    }

    private Object createStore(TemporalField field, Locale locale) {
        if (field == MONTH_OF_YEAR) {
            DateFormatSymbols oldSymbols = DateFormatSymbols.getInstance(locale);
            Map<TextStyle, Map<Long, String>> styleMap = new HashMap<TextStyle, Map<Long,String>>();
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
            
            map = new HashMap<Long, String>();
            map.put(f1, narrowMonth(1, array[Calendar.JANUARY], locale));
            map.put(f2, narrowMonth(2, array[Calendar.FEBRUARY], locale));
            map.put(f3, narrowMonth(3, array[Calendar.MARCH], locale));
            map.put(f4, narrowMonth(4, array[Calendar.APRIL], locale));
            map.put(f5, narrowMonth(5, array[Calendar.MAY], locale));
            map.put(f6, narrowMonth(6, array[Calendar.JUNE], locale));
            map.put(f7, narrowMonth(7, array[Calendar.JULY], locale));
            map.put(f8, narrowMonth(8, array[Calendar.AUGUST], locale));
            map.put(f9, narrowMonth(9, array[Calendar.SEPTEMBER], locale));
            map.put(f10, narrowMonth(10, array[Calendar.OCTOBER], locale));
            map.put(f11, narrowMonth(11, array[Calendar.NOVEMBER], locale));
            map.put(f12, narrowMonth(12, array[Calendar.DECEMBER], locale));
            styleMap.put(TextStyle.NARROW, map);
            
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
            return createLocaleStore(styleMap);
        }
        if (field == DAY_OF_WEEK) {
            DateFormatSymbols oldSymbols = DateFormatSymbols.getInstance(locale);
            Map<TextStyle, Map<Long, String>> styleMap = new HashMap<TextStyle, Map<Long,String>>();
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
            
            map = new HashMap<Long, String>();
            map.put(f1, narrowDayOfWeek(1, array[Calendar.MONDAY], locale));
            map.put(f2, narrowDayOfWeek(2, array[Calendar.TUESDAY], locale));
            map.put(f3, narrowDayOfWeek(3, array[Calendar.WEDNESDAY], locale));
            map.put(f4, narrowDayOfWeek(4, array[Calendar.THURSDAY], locale));
            map.put(f5, narrowDayOfWeek(5, array[Calendar.FRIDAY], locale));
            map.put(f6, narrowDayOfWeek(6, array[Calendar.SATURDAY], locale));
            map.put(f7, narrowDayOfWeek(7, array[Calendar.SUNDAY], locale));
            styleMap.put(TextStyle.NARROW, map);
            
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
            return createLocaleStore(styleMap);
        }
        if (field == AMPM_OF_DAY) {
            DateFormatSymbols oldSymbols = DateFormatSymbols.getInstance(locale);
            Map<TextStyle, Map<Long, String>> styleMap = new HashMap<TextStyle, Map<Long,String>>();
            String[] array = oldSymbols.getAmPmStrings();
            Map<Long, String> map = new HashMap<Long, String>();
            map.put(0L, array[Calendar.AM]);
            map.put(1L, array[Calendar.PM]);
            styleMap.put(TextStyle.FULL, map);
            styleMap.put(TextStyle.SHORT, map);  // re-use, as we don't have different data
            return createLocaleStore(styleMap);
        }
        if (field == ERA) {
            DateFormatSymbols oldSymbols = DateFormatSymbols.getInstance(locale);
            Map<TextStyle, Map<Long, String>> styleMap = new HashMap<TextStyle, Map<Long,String>>();
            String[] array = oldSymbols.getEras();
            Map<Long, String> map = new HashMap<Long, String>();
            map.put(0L, array[GregorianCalendar.BC]);
            map.put(1L, array[GregorianCalendar.AD]);
            styleMap.put(TextStyle.SHORT, map);
            if (locale.getLanguage().equals(Locale.ENGLISH.getLanguage())) {
                map = new HashMap<Long, String>();
                map.put(0L, "Before Christ");
                map.put(1L, "Anno Domini");
                styleMap.put(TextStyle.FULL, map);
            } else {
                // re-use, as we don't have different data
                styleMap.put(TextStyle.FULL, map);
            }
            map = new HashMap<Long, String>();
            map.put(0L, array[GregorianCalendar.BC].substring(0, 1));
            map.put(1L, array[GregorianCalendar.AD].substring(0, 1));
            styleMap.put(TextStyle.NARROW, map);
            return createLocaleStore(styleMap);
        }
        // hard code English quarter text
        if (field == IsoFields.QUARTER_OF_YEAR) {
            Map<TextStyle, Map<Long, String>> styleMap = new HashMap<TextStyle, Map<Long,String>>();
            Map<Long, String> map = new HashMap<Long, String>();
            map.put(1L, "Q1");
            map.put(2L, "Q2");
            map.put(3L, "Q3");
            map.put(4L, "Q4");
            styleMap.put(TextStyle.SHORT, map);
            map = new HashMap<Long, String>();
            map.put(1L, "1st quarter");
            map.put(2L, "2nd quarter");
            map.put(3L, "3rd quarter");
            map.put(4L, "4th quarter");
            styleMap.put(TextStyle.FULL, map);
            return createLocaleStore(styleMap);
        }
        return "";  // null marker for map
    }

    // for China/Japan we need special behaviour
    private String narrowMonth(int month, String text, Locale locale) {
        if (locale.getLanguage().equals("zh") && locale.getCountry().equals("CN")) {
            switch (month) {
                case 1:
                    return "\u4e00";
                case 2:
                    return "\u4e8c";
                case 3:
                    return "\u4e09";
                case 4:
                    return "\u56db";
                case 5:
                    return "\u4e94";
                case 6:
                    return "\u516d";
                case 7:
                    return "\u4e03";
                case 8:
                    return "\u516b";
                case 9:
                    return "\u4e5d";
                case 10:
                    return "\u5341";
                case 11:
                    return "\u5341\u4e00";
                case 12:
                    return "\u5341\u4e8c";
            }
        }
        if (locale.getLanguage().equals("ja") && locale.getCountry().equals("JP")) {
            return Integer.toString(month);
        }
        return text.substring(0, 1);
    }

    // for China we need to select the last character
    private String narrowDayOfWeek(int dow, String text, Locale locale) {
        if (locale.getLanguage().equals("zh") && locale.getCountry().equals("CN")) {
            switch (dow) {
                case 1:
                    return "\u4e00";
                case 2:
                    return "\u4e8c";
                case 3:
                    return "\u4e09";
                case 4:
                    return "\u56db";
                case 5:
                    return "\u4e94";
                case 6:
                    return "\u516d";
                case 7:
                    return "\u65e5";
            }
        }
        return text.substring(0, 1);
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
    private static LocaleStore createLocaleStore(Map<TextStyle, Map<Long, String>> valueTextMap) {
        valueTextMap.put(TextStyle.FULL_STANDALONE, valueTextMap.get(TextStyle.FULL));
        valueTextMap.put(TextStyle.SHORT_STANDALONE, valueTextMap.get(TextStyle.SHORT));
        if (valueTextMap.containsKey(TextStyle.NARROW) && valueTextMap.containsKey(TextStyle.NARROW_STANDALONE) == false) {
            valueTextMap.put(TextStyle.NARROW_STANDALONE, valueTextMap.get(TextStyle.NARROW));
        }
        return new LocaleStore(valueTextMap);
    }

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
            Map<TextStyle, List<Entry<String, Long>>> map = new HashMap<TextStyle, List<Entry<String,Long>>>();
            List<Entry<String, Long>> allList = new ArrayList<Map.Entry<String,Long>>();
            for (TextStyle style : valueTextMap.keySet()) {
                Map<String, Entry<String, Long>> reverse = new HashMap<String, Map.Entry<String,Long>>();
                for (Map.Entry<Long, String> entry : valueTextMap.get(style).entrySet()) {
                    if (reverse.put(entry.getValue(), createEntry(entry.getValue(), entry.getKey())) != null) {
                        continue;  // not parsable, try next style
                    }
                }
                List<Entry<String, Long>> list = new ArrayList<Map.Entry<String,Long>>(reverse.values());
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
