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
package javax.time.calendar.format;

import java.text.DateFormatSymbols;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;

import javax.time.calendar.DateTimeField;
import javax.time.calendar.DateTimeRule;
import javax.time.calendar.format.DateTimeFormatterBuilder.TextStyle;

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

//    DateTimeFormatterBuilder.checkNotNull(locale, "Locale must not be null");
//    DateTimeFormatterBuilder.checkNotNull(valueTextMap, "Map must not be null");
//    if (valueTextMap.containsKey(null) || valueTextMap.containsValue(null) || valueTextMap.containsValue("")) {
//        throw new IllegalArgumentException("The map must not contain null or empty text");
//    }
//    this.rule = rule;
//    this.locale = locale;
//    Map<DateTimeField, String> copy = new HashMap<DateTimeField, String>(valueTextMap);
//    Map<String, DateTimeField> reverse = new HashMap<String, DateTimeField>();
//    Map<String, DateTimeField> insensitive = new HashMap<String, DateTimeField>();
//    Set<Integer> lengthSet = new HashSet<Integer>();
//    for (Map.Entry<DateTimeField, String> entry : copy.entrySet()) {
//        String text = entry.getValue();
//        DateTimeField value = entry.getKey();
//        reverse.put(text, value);
//        lengthSet.add(text.length());
//        String lower = text.toLowerCase(locale);
//        insensitive.put(lower, value);
//        lengthSet.add(lower.length());
//        String upper = text.toUpperCase(locale);
//        insensitive.put(upper, value);
//        lengthSet.add(upper.length());
//    }
//    if (reverse.size() < copy.size()) {
//        // duplicate text for a given value, so parsing is not supported
//        this.textValueMap = Collections.emptyMap();
//        this.insensitiveTextValueMap = Collections.emptyMap();
//        this.lengths = null;
//    } else {
//        textValueMap = Collections.unmodifiableMap(reverse);
//        insensitiveTextValueMap = Collections.unmodifiableMap(insensitive);
//        this.lengths = new int[lengthSet.size()];
//        int i = 0;
//        for (Iterator<Integer> it = lengthSet.iterator(); it.hasNext(); ) {
//            lengths[i++] = it.next();
//        }
//        Arrays.sort(lengths);
//    }
//    this.valueTextMap = Collections.unmodifiableMap(copy);
    //-----------------------------------------------------------------------
    /** {@inheritDoc} */
    @Override
    public Locale[] getAvailableLocales() {
        return DateFormatSymbols.getAvailableLocales();
    }

    //-----------------------------------------------------------------------
    @Override
    public String getText(DateTimeField field, TextStyle style, Locale locale) {
        return null;  // TODO
    }

    @Override
    public Iterator<Entry<String, DateTimeField>> getTextIterator(DateTimeRule rule, TextStyle style, Locale locale) {
        return null;  // TODO
    }

//    //-----------------------------------------------------------------------
//    /**
//     * Gets the text for the specified field.
//     * <p>
//     * The text associated with the field is returned, or null if none found.
//     *
//     * @param field  the field to get text for
//     * @return the text for the field, null if no text found
//     */
//    public String getValueText(DateTimeField field) {
//        return valueTextMap.get(field);
//    }
//
//    /**
//     * Matches the specified text against the text-value map returning the
//     * matched length and value.
//     * <p>
//     * This method is intended for use during parsing, and matches the search text
//     * against the text-value map, optionally ignoring case.
//     *
//     * @param ignoreCase  true to ignore case during the matching
//     * @param parseText  the text to match against
//     * @return the parsed field, null if not parsed
//     */
//    public DateTimeField matchText(boolean ignoreCase, String parseText, int[] parsedPosition) {
//        DateTimeFormatterBuilder.checkNotNull(parseText, "Search text must not be null");
//        if (lengths == null) {
//            return null;
//        }
//        int lengthsStart = Arrays.binarySearch(lengths, parseText.length());
//        lengthsStart = (lengthsStart < 0 ? -lengthsStart - 2 : lengthsStart);
//        if (ignoreCase) {
//            parseText = parseText.toUpperCase(locale);
//            for (int i = lengthsStart; i >= 0; i--) {
//                DateTimeField field = insensitiveTextValueMap.get(parseText.substring(0, lengths[i]));
//                if (field != null) {
//                    parsedPosition[0] = lengths[i];
//                    return field;
//                }
//            }
//            parseText = parseText.toLowerCase(locale);
//            for (int i = lengthsStart; i >= 0; i--) {
//                DateTimeField field = insensitiveTextValueMap.get(parseText.substring(0, lengths[i]));
//                if (field != null) {
//                    parsedPosition[0] = lengths[i];
//                    return field;
//                }
//            }
//        } else {
//            for (int i = lengthsStart; i >= 0; i--) {
//                DateTimeField field = textValueMap.get(parseText.substring(0, lengths[i]));
//                if (field != null) {
//                    parsedPosition[0] = lengths[i];
//                    return field;
//                }
//            }
//        }
//        return null;
//    }

}
