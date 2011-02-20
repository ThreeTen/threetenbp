/*
 * Copyright (c) 2007-2011 Stephen Colebourne & Michael Nascimento Santos
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

import java.lang.ref.SoftReference;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.time.CalendricalException;
import javax.time.calendar.format.DateTimeFormatterBuilder.TextStyle;

/**
 * The rule defining how a measurable field of time operates.
 * <p>
 * Rule implementations define how a field like day-of-month operates.
 * This includes the field name and minimum/maximum values.
 * <p>
 * This class is abstract and must be implemented with care to
 * ensure other classes in the framework operate correctly.
 * All instantiable subclasses must be final, immutable and thread-safe and must
 * ensure serialization works correctly.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public abstract class DateTimeRule extends CalendricalRule<DateTimeField> {
    // TODO: broken serialization

    /** A serialization identifier for this class. */
    private static final long serialVersionUID = 1L;
    /** A Math context for calculating fractions from values. */
    private static final MathContext FRACTION_CONTEXT = new MathContext(9, RoundingMode.FLOOR);
    /** A Math context for calculating values from fractions. */
    private static final MathContext VALUE_CONTEXT = new MathContext(0, RoundingMode.FLOOR);

    /** The minimum value for the field. */
    private final long minimumValue;
    /** The maximum value for the field. */
    private final long maximumValue;
    /** The cached text for this rule. */
    private final transient ConcurrentMap<Locale, SoftReference<EnumMap<TextStyle, TextStore>>> textStores;

    /**
     * Constructor.
     *
     * @param chronology  the chronology, not null
     * @param name  the name of the type, not null
     * @param periodUnit  the period unit, not null
     * @param periodRange  the period range, not null
     * @param minimumValue  the minimum value
     * @param maximumValue  the minimum value
     */
    protected DateTimeRule(
            Chronology chronology,
            String name,
            PeriodUnit periodUnit,
            PeriodUnit periodRange,
            long minimumValue,
            long maximumValue) {
        this(chronology, name, periodUnit, periodRange, minimumValue, maximumValue, false);
    }

    /**
     * Constructor.
     *
     * @param chronology  the chronology, not null
     * @param name  the name of the type, not null
     * @param periodUnit  the period unit, not null
     * @param periodRange  the period range, not null
     * @param minimumValue  the minimum value
     * @param maximumValue  the minimum value
     * @param hasText  true if this field has a text representation
     */
    protected DateTimeRule(
            Chronology chronology,
            String name,
            PeriodUnit periodUnit,
            PeriodUnit periodRange,
            long minimumValue,
            long maximumValue,
            boolean hasText) {
        super(DateTimeField.class, chronology, name, periodUnit, periodRange);
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
        this.textStores = (hasText ? new ConcurrentHashMap<Locale, SoftReference<EnumMap<TextStyle, TextStore>>>() : null);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the rule defines values that fit in an {@code int}.
     * <p>
     * This checks that all valid values are within the bounds of an {@code int}.
     * <p>
     * For example, the 'MonthOfYear' rule has values from 1 to 12, which fits in an {@code int}.
     * By comparison, 'NanoOfDay' runs from 1 to 86,400,000,000,000 which does not fit in an {@code int}.
     * <p>
     * This implementation uses {@link #getMinimumValue()} and {@link #getMaximumValue()}.
     *
     * @return true if a valid value always fits in an {@code int}
     */
    public boolean isIntValue() {
        return getMinimumValue() >= Integer.MIN_VALUE && getMaximumValue() <= Integer.MAX_VALUE;
    }

    /**
     * Checks if the rule defines values that fit in an {@code int}, throwing an exception if not.
     * <p>
     * This checks that all valid values are within the bounds of an {@code int}.
     * <p>
     * For example, the 'MonthOfYear' rule has values from 1 to 12, which fits in an {@code int}.
     * By comparison, 'NanoOfDay' runs from 1 to 86,400,000,000,000 which does not fit in an {@code int}.
     * <p>
     * This implementation uses {@link #getMinimumValue()} and {@link #getMaximumValue()}.
     *
     * @return true if a valid value always fits in an {@code int}
     * @throws CalendricalException if the value does not fit in an {@code int}
     */
    public void checkIntValue() {
        if (isIntValue() == false) {
            throw new CalendricalRuleException("Rule does not specify an int value: " + getName(), this);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the value is valid for the rule.
     * <p>
     * This checks that the value is within the valid range of the rule.
     * This method considers the rule in isolation, thus only the
     * outer minimum and maximum range for the field is validated.
     * For example, 'DayOfMonth' has the outer value-range of 1 to 31.
     *
     * @param value  the value to check
     * @return true if the value is valid
     */
    public boolean isValidValue(long value) {
        return (value >= getMinimumValue() && value <= getMaximumValue());
    }

    /**
     * Checks if the value is valid for the rule, throwing an exception if invalid.
     * <p>
     * This checks that the value is within the valid range of the rule.
     * This method considers the rule in isolation, thus only the
     * outer minimum and maximum range for the field is validated.
     * For example, 'DayOfMonth' has the outer value-range of 1 to 31.
     * <p>
     * This implementation uses {@link #isValidValue(long)}.
     *
     * @param value  the value to check
     * @return the valid value
     * @throws IllegalCalendarFieldValueException if the value is invalid
     */
    public long checkValidValue(long value) {
        if (isValidValue(value) == false) {
            throw new IllegalCalendarFieldValueException(this, value, getMinimumValue(), getMaximumValue());
        }
        return value;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the value is valid for the rule and that the rule defines
     * values that fit in an {@code int}.
     * <p>
     * This checks that the value is within the valid range of the rule and
     * that all valid values are within the bounds of an {@code int}.
     * This method considers the rule in isolation, thus only the
     * outer minimum and maximum range for the field is validated.
     * For example, 'DayOfMonth' has the outer value-range of 1 to 31.
     * <p>
     * This implementation uses {@link #isIntValue()} and {@link #isValidValue(long)}.
     *
     * @param value  the value to check
     * @return true if the value is valid and fits in an {@code int}
     */
    public boolean isValidIntValue(long value) {
        return isIntValue() && isValidValue(value);
    }

    /**
     * Checks if the value is valid for the rule and that the rule defines
     * values that fit in an {@code int}, throwing an exception if not.
     * <p>
     * This checks that the value is within the valid range of the rule and
     * that all valid values are within the bounds of an {@code int}.
     * This method considers the rule in isolation, thus only the
     * outer minimum and maximum range for the field is validated.
     * For example, 'DayOfMonth' has the outer value-range of 1 to 31.
     * <p>
     * This implementation uses {@link #checkIntValue()} and {@link #checkValidValue(long)}.
     *
     * @param value  the value to check
     * @return the valid value as an {@code int}
     * @throws CalendricalException if the value does not fit in an {@code int}
     * @throws IllegalCalendarFieldValueException if the value is invalid
     */
    public int checkValidIntValue(long value) {
        checkIntValue();
        return (int) checkValidValue(value);
    }

    //-----------------------------------------------------------------------
    /**
     * Is the set of values, from the minimum value to the maximum, a fixed
     * set, or does it vary according to other fields.
     *
     * @return true if the set of values is fixed
     */
    public boolean isFixedValueSet() {
        return getMaximumValue() == getSmallestMaximumValue() &&
                getMinimumValue() == getLargestMinimumValue();
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the minimum value that the field can take.
     *
     * @return the minimum value for this field
     */
    public long getMinimumValue() {
        return minimumValue;
    }

    /**
     * Gets the largest possible minimum value that the field can take.
     * <p>
     * The default implementation returns {@link #getMinimumValue()}.
     * Subclasses must override this as necessary.
     *
     * @return the largest possible minimum value for this field
     */
    public long getLargestMinimumValue() {
        return getMinimumValue();
    }

    /**
     * Gets the minimum value that the field can take using the specified
     * calendrical information to refine the accuracy of the response.
     * <p>
     * The result of this method may still be inaccurate, if there is insufficient
     * information in the calendrical.
     * <p>
     * The default implementation returns {@link #getMinimumValue()}.
     * Subclasses must override this as necessary.
     *
     * @param calendrical  context calendrical, not null
     * @return the minimum value of the field given the context
     */
    public long getMinimumValue(Calendrical calendrical) {
        return getMinimumValue();
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the maximum value that the field can take.
     *
     * @return the maximum value for this field
     */
    public long getMaximumValue() {
        return maximumValue;
    }

    /**
     * Gets the smallest possible maximum value that the field can take.
     * <p>
     * The default implementation returns {@link #getMaximumValue()}.
     * Subclasses must override this as necessary.
     *
     * @return the smallest possible maximum value for this field
     */
    public long getSmallestMaximumValue() {
        return getMaximumValue();
    }

    /**
     * Gets the minimum value that the field can take using the specified
     * calendrical information to refine the accuracy of the response.
     * <p>
     * The result of this method will still be inaccurate if there is insufficient
     * information in the calendrical.
     * <p>
     * For example, if this field is the ISO day-of-month field, then the number
     * of days in the month varies depending on the month and year. If both the
     * month and year can be derived from the calendrical, then the maximum value
     * returned will be accurate. Otherwise the 'best guess' value from
     * {@link #getMaximumValue()} will be returned.
     * <p>
     * The default implementation returns {@link #getMaximumValue()}.
     * Subclasses must override this as necessary.
     *
     * @param calendrical  context calendrical, not null
     * @return the minimum value of the field given the context
     */
    public long getMaximumValue(Calendrical calendrical) {
        return getMaximumValue();
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the text for this field.
     * <p>
     * Some fields have a textual representation, such as day-of-week or
     * month-of-year. This method provides a convenient way to convert a value
     * to such a textual representation.
     * More control is available using {@link #getTextStore}.
     * <p>
     * If there is no textual mapping, then the value is returned as per
     * {@link Integer#toString()}. Note that this is different to what occurs
     * in printing /parsing, where a more advanced localized conversion from
     * int to String is used.
     *
     * @param value  the value to convert to text, must be valid for the rule
     * @param locale  the locale to use, not null
     * @param textStyle  the text style, not null
     * @return the text of the field, never null
     */
    public String getText(int value, Locale locale, TextStyle textStyle) {
        TextStore textStore = getTextStore(locale, textStyle);
        String text = (textStore != null ? textStore.getValueText(value) : null);
        return text == null ? Integer.toString(value) : text;
    }

    /**
     * Gets the text map for this field with the specified locale and style.
     * <p>
     * Some fields have a textual representation, such as day-of-week or
     * month-of-year. The text store provides details of those textual representations.
     * <p>
     * To supply text, subclasses should pass true in the constructor and
     * override {@link #createTextStores}. This method is not normally overridden.
     *
     * @param locale  the locale to use, not null
     * @param textStyle  the text style, not null
     * @return the text cache, null if no text available
     */
    public TextStore getTextStore(Locale locale, TextStyle textStyle) {
        if (textStores == null) {
            return null;
        }
        SoftReference<EnumMap<TextStyle, TextStore>> ref = textStores.get(locale);
        if (ref != null) {
            EnumMap<TextStyle, TextStore> textMapByStyle = ref.get();
            if (textMapByStyle != null) {
                return textMapByStyle.get(textStyle);
            }
        }
        EnumMap<TextStyle, TextStore> textStoreByStyle = new EnumMap<TextStyle, TextStore>(TextStyle.class);
        createTextStores(textStoreByStyle, locale);
        textStoreByStyle = new EnumMap<TextStyle, TextStore>(textStoreByStyle);
        textStores.put(locale, new SoftReference<EnumMap<TextStyle, TextStore>>(textStoreByStyle));
        return textStoreByStyle.get(textStyle);
    }

    /**
     * Creates the text store for each style for the specified locale.
     * <p>
     * It is intended that a new copy of the text store should be created in
     * response to calling this method as the result is cached by {@link #getTextStore}.
     *
     * @param textStores  the map to populate with TextStore instances, not null
     * @param locale  the locale to use, not null
     */
    protected void createTextStores(EnumMap<TextStyle, TextStore> textStores, Locale locale) {
        // do nothing - override if field provides text
    }

    //-----------------------------------------------------------------------
    /**
     * Converts a value for this field to a fraction between 0 and 1.
     * <p>
     * The fractional value is between 0 (inclusive) and 1 (exclusive).
     * It can only be returned if {@link #isFixedValueSet()} returns true and the
     * {@link #getMinimumValue()} returns zero.
     * The fraction is obtained by calculation from the field range using 9 decimal
     * places and a rounding mode of {@link RoundingMode#FLOOR FLOOR}.
     * <p>
     * For example, the second-of-minute value of 15 would be returned as 0.25,
     * assuming the standard definition of 60 seconds in a minute.
     *
     * @param value  the value to convert, must be valid for this rule
     * @return the value as a fraction within the range, from 0 to 1, not null
     * @throws CalendricalRuleException if the value cannot be converted to a fraction
     */
    public BigDecimal convertToFraction(long value) {
        if (isFixedValueSet() == false) {
            throw new CalendricalRuleException("The fractional value of " + getName() +
                    " cannot be obtained as the range is not fixed", this);
        }
        if (getMinimumValue() != 0) {
            throw new CalendricalRuleException("The fractional value of " + getName() +
                    " cannot be obtained as the minimum field value is not zero", this);
        }
        checkValidValue(value);
        long range = getMaximumValue();
        range++;
        BigDecimal decimal = new BigDecimal(value);
        return decimal.divide(new BigDecimal(range), FRACTION_CONTEXT);
    }

    /**
     * Converts a fraction from 0 to 1 for this field to a value.
     * <p>
     * The fractional value must be between 0 (inclusive) and 1 (exclusive).
     * It can only be returned if {@link #isFixedValueSet()} returns true and the
     * {@link #getMinimumValue()} returns zero.
     * The value is obtained by calculation from the field range and a rounding
     * mode of {@link RoundingMode#FLOOR FLOOR}.
     * <p>
     * For example, the fractional second-of-minute of 0.25 would be converted to 15,
     * assuming the standard definition of 60 seconds in a minute.
     *
     * @param fraction  the fraction to convert, not null
     * @return the value of the field, valid for this rule
     * @throws UnsupportedRuleException if the value cannot be converted
     * @throws IllegalCalendarFieldValueException if the value is invalid
     */
    public long convertFromFraction(BigDecimal fraction) {
        if (isFixedValueSet() == false) {
            throw new UnsupportedRuleException("The fractional value of " + getName() +
                    " cannot be converted as the range is not fixed", this);
        }
        if (getMinimumValue() != 0) {
            throw new UnsupportedRuleException("The fractional value of " + getName() +
                    " cannot be converted as the minimum field value is not zero", this);
        }
        long range = getMaximumValue();
        range++;
        BigDecimal decimal = fraction.multiply(new BigDecimal(range), VALUE_CONTEXT);
        try {
            long value = decimal.longValueExact();
            checkValidValue(value);
            return value;
        } catch (ArithmeticException ex) {
            throw new IllegalCalendarFieldValueException("The fractional value " + fraction + " of " + getName() +
                    " cannot be converted as it is not in the range 0 (inclusive) to 1 (exclusive)", this);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Creates a field for this rule.
     * 
     * @param value  the value to create the field for, may be outside the valid range for the rule
     * @return the created field, not null
     */
    public DateTimeField field(long value) {
       return DateTimeField.of(this, value); 
    }

    //-----------------------------------------------------------------------
    /**
     * The mapping between integer values and textual representations.
     * <p>
     * Some fields have a textual representation, such as day-of-week or month-of-year.
     * These textual representations can be captured in this class for printing
     * and parsing.
     * <p>
     * TextStore is immutable and thread-safe.
     *
     * @author Stephen Colebourne
     */
    public static final class TextStore {
        /**
         * The locale of the text.
         */
        private final Locale locale;
        /**
         * Map of value to text.
         */
        private final Map<Integer, String> valueTextMap;
        /**
         * Map of case sensitive text to value.
         */
        private final Map<String, Integer> textValueMap;
        /**
         * Map of case insensitive text to value.
         */
        private final Map<String, Integer> insensitiveTextValueMap;
        /**
         * The lengths of the text items.
         */
        private final int[] lengths;

        //-----------------------------------------------------------------------
        /**
         * Constructor.
         *
         * @param locale  the locale, not null
         * @param valueTextMap  the map of values to text to store, not null
         */
        public TextStore(Locale locale, Map<Integer, String> valueTextMap) {
            ISOChronology.checkNotNull(locale, "Locale must not be null");
            ISOChronology.checkNotNull(valueTextMap, "Map must not be null");
            if (valueTextMap.containsKey(null) || valueTextMap.containsValue(null) || valueTextMap.containsValue("")) {
                throw new IllegalArgumentException("The map must not contain null or empty text");
            }
            this.locale = locale;
            Map<Integer, String> copy = new HashMap<Integer, String>(valueTextMap);
            Map<String, Integer> reverse = new HashMap<String, Integer>();
            Map<String, Integer> insensitive = new HashMap<String, Integer>();
            Set<Integer> lengthSet = new HashSet<Integer>();
            for (Map.Entry<Integer, String> entry : copy.entrySet()) {
                String text = entry.getValue();
                Integer value = entry.getKey();
                reverse.put(text, value);
                lengthSet.add(text.length());
                String lower = text.toLowerCase(locale);
                insensitive.put(lower, value);
                lengthSet.add(lower.length());
                String upper = text.toUpperCase(locale);
                insensitive.put(upper, value);
                lengthSet.add(upper.length());
            }
            if (reverse.size() < copy.size()) {
                // duplicate text for a given value, so parsing is not supported
                this.textValueMap = Collections.emptyMap();
                this.insensitiveTextValueMap = Collections.emptyMap();
                this.lengths = null;
            } else {
                textValueMap = Collections.unmodifiableMap(reverse);
                insensitiveTextValueMap = Collections.unmodifiableMap(insensitive);
                this.lengths = new int[lengthSet.size()];
                int i = 0;
                for (Iterator<Integer> it = lengthSet.iterator(); it.hasNext(); ) {
                    lengths[i++] = it.next();
                }
                Arrays.sort(lengths);
            }
            this.valueTextMap = Collections.unmodifiableMap(copy);
        }

        //-----------------------------------------------------------------------
        /**
         * Gets the locale that the text relates to.
         *
         * @return the locale for the text, never null
         */
        public Locale getLocale() {
            return locale;
        }

        //-----------------------------------------------------------------------
        /**
         * Gets the map of text for each integer value.
         *
         * @return the unmodifiable map of value to text, never null
         */
        public Map<Integer, String> getValueTextMap() {
            return valueTextMap;
        }

        /**
         * Gets the text for the specified integer value.
         * <p>
         * The text associated with the value is returned, or null if none found.
         *
         * @param value  the value to get text for
         * @return the text for the field value, null if no text found
         */
        public String getValueText(int value) {
            return valueTextMap.get(value);
        }

        //-----------------------------------------------------------------------
        /**
         * Gets the derived map expressing the value for each text.
         * <p>
         * If the value-text map contains duplicate text elements then this map
         * will be empty.
         *
         * @return the unmodifiable map of text to value for the field rule and style, never null
         */
        public Map<String, Integer> getTextValueMap() {
            return textValueMap;
        }

        /**
         * Matches the specified text against the text-value map returning the
         * matched length and value.
         * <p>
         * This method is intended for use during parsing, and matches the search text
         * against the text-value map, optionally ignoring case.
         *
         * @param ignoreCase  true to ignore case during the matching
         * @param parseText  the text to match against
         * @return a long packed result of two int values (for performance in parsing).
         *  The value is <code>(parseLength << 32 + matchedValue)</code>.
         *  Zero is returned if there is no match.
         *  Minus one is returned if the text store cannot parse.
         *  The parse length can be obtained via (result >>> 32).
         *  The value can be obtained via ((int) result).
         */
        public long matchText(boolean ignoreCase, String parseText) {
            ISOChronology.checkNotNull(parseText, "Search text must not be null");
            if (lengths == null) {
                return -1;
            }
            int lengthsStart = Arrays.binarySearch(lengths, parseText.length());
            lengthsStart = (lengthsStart < 0 ? -lengthsStart - 2 : lengthsStart);
            if (ignoreCase) {
                parseText = parseText.toUpperCase(locale);
                for (int i = lengthsStart; i >= 0; i--) {
                    Integer value = insensitiveTextValueMap.get(parseText.substring(0, lengths[i]));
                    if (value != null) {
                        return (((long) lengths[i]) << 32) + value;
                    }
                }
                parseText = parseText.toLowerCase(locale);
                for (int i = lengthsStart; i >= 0; i--) {
                    Integer value = insensitiveTextValueMap.get(parseText.substring(0, lengths[i]));
                    if (value != null) {
                        return (((long) lengths[i]) << 32) + value;
                    }
                }
            } else {
                for (int i = lengthsStart; i >= 0; i--) {
                    Integer value = textValueMap.get(parseText.substring(0, lengths[i]));
                    if (value != null) {
                        return (((long) lengths[i]) << 32) + value;
                    }
                }
            }
            return 0;
        }

    }

}
