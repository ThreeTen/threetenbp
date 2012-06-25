/*
 * Copyright (c) 2008-2012, Stephen Colebourne & Michael Nascimento Santos
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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.time.DateTimes;
import javax.time.calendrical.CalendricalObject;
import javax.time.calendrical.DateTimeBuilder;
import javax.time.calendrical.DateTimeField;

/**
 * Context object used during date and time parsing.
 * <p>
 * This class represents the current state of the parse.
 * It has the ability to store and retrieve the parsed values and manage optional segments.
 * It also provides key information to the parsing methods.
 * <p>
 * Once parsing is complete, the {@link #toBuilder()} is typically used
 * to obtain a builder that can combine the separate parsed fields into meaningful values.
 * 
 * <h4>Implementation notes</h4>
 * This class is a mutable context intended for use from a single thread.
 * Usage of the class is thread-safe within standard parsing as a new instance of this class
 * is automatically created for each parse and parsing is single-threaded
 */
final class DateTimeParseContext {

    /**
     * The locale, not null.
     */
    private Locale locale;
    /**
     * The date time format symbols, not null.
     */
    private DateTimeFormatSymbols symbols;
    /**
     * Whether to parse using case sensitively.
     */
    private boolean caseSensitive = true;
    /**
     * Whether to parse using strict rules.
     */
    private boolean strict = true;
    /**
     * The list of parsed data.
     */
    private final ArrayList<Parsed> calendricals = new ArrayList<Parsed>();

    /**
     * Creates a new instance of the context.
     * <p>
     * This should normally only be created by the parser.
     *
     * @param locale  the locale to use, not null
     * @param symbols  the symbols to use during parsing, not null
     */
    DateTimeParseContext(Locale locale, DateTimeFormatSymbols symbols) {
        super();
        setLocale(locale);
        setSymbols(symbols);
        calendricals.add(new Parsed());
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the locale.
     * <p>
     * This locale is used to control localization in the parse except
     * where localization is controlled by the symbols.
     *
     * @return the locale, not null
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Sets the locale.
     * <p>
     * This locale is used to control localization in the parse except
     * where localization is controlled by the symbols.
     *
     * @param locale  the locale, not null
     */
    public void setLocale(Locale locale) {
        DateTimes.checkNotNull(locale, "Locale must not be null");
        this.locale = locale;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the formatting symbols.
     * <p>
     * The symbols control the localization of numeric parsing.
     *
     * @return the formatting symbols, not null
     */
    public DateTimeFormatSymbols getSymbols() {
        return symbols;
    }

    /**
     * Sets the formatting symbols.
     * <p>
     * The symbols control the localization of numeric parsing.
     *
     * @param symbols  the formatting symbols, not null
     */
    public void setSymbols(DateTimeFormatSymbols symbols) {
        DateTimes.checkNotNull(symbols, "DateTimeFormatSymbols must not be null");
        this.symbols = symbols;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if parsing is case sensitive.
     *
     * @return true if parsing is case sensitive, false if case insensitive
     */
    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    /**
     * Sets whether the parsing is case sensitive or not.
     *
     * @param caseSensitive  changes the parsing to be case sensitive or not from now on
     */
    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    /**
     * Helper to compare two {@code CharSequence} instances.
     * This uses {@link #isCaseSensitive()}.
     * 
     * @param cs1  the first character sequence, not null
     * @param offset1  the offset into the first sequence, valid
     * @param cs2  the second character sequence, not null
     * @param offset2  the offset into the second sequence, valid
     * @param length  the length to check, valid
     * @return true if equal
     */
    public boolean subSequenceEquals(CharSequence cs1, int offset1, CharSequence cs2, int offset2, int length) {
        if (offset1 + length > cs1.length() || offset2 + length > cs2.length()) {
            return false;
        }
        if (isCaseSensitive()) {
            for (int i = 0; i < length; i++) {
                char ch1 = cs1.charAt(offset1 + i);
                char ch2 = cs2.charAt(offset2 + i);
                if (ch1 != ch2) {
                    return false;
                }
            }
        } else {
            for (int i = 0; i < length; i++) {
                char ch1 = cs1.charAt(offset1 + i);
                char ch2 = cs2.charAt(offset2 + i);
                if (ch1 != ch2 && Character.toUpperCase(ch1) != Character.toUpperCase(ch2) &&
                        Character.toLowerCase(ch1) != Character.toLowerCase(ch2)) {
                    return false;
                }
            }
        }
        return true;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if parsing is strict.
     * <p>
     * Strict parsing requires exact matching of the text and sign styles.
     *
     * @return true if parsing is strict, false if lenient
     */
    public boolean isStrict() {
        return strict;
    }

    /**
     * Sets whether parsing is strict or lenient.
     *
     * @param strict  changes the parsing to be strict or lenient from now on
     */
    public void setStrict(boolean strict) {
        this.strict = strict;
    }

    //-----------------------------------------------------------------------
    /**
     * Starts the parsing of an optional segment of the input.
     */
    void startOptional() {
        calendricals.add(currentCalendrical().clone());
    }

    /**
     * Ends the parsing of an optional segment of the input.
     *
     * @param successful  whether the optional segment was successfully parsed
     */
    void endOptional(boolean successful) {
        if (successful) {
            calendricals.remove(calendricals.size() - 2);
        } else {
            calendricals.remove(calendricals.size() - 1);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the currently active calendrical.
     *
     * @return the current calendrical, not null
     */
    private Parsed currentCalendrical() {
        return calendricals.get(calendricals.size() - 1);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the first value that was parsed for the specified field.
     * <p>
     * This searches the results of the parse, returning the first value found
     * for the specified field. No attempt is made to derive a value.
     * The field may have an out of range value.
     * For example, the day-of-month might be set to 50, or the hour to 1000.
     *
     * @param field  the field to query from the map, null returns null
     * @return the value mapped to the specified field, null if field was not parsed
     */
    public Long getParsed(DateTimeField field) {
        for (Object obj : currentCalendrical().calendricals) {
            if (obj instanceof FieldValue) {
                FieldValue fv = (FieldValue) obj;
                if (fv.field.equals(field)) {
                    return fv.value;
                }
            }
        }
        return null;
    }

    /**
     * Gets the first value that was parsed for the specified type.
     * <p>
     * This searches the results of the parse, returning the first calendrical found
     * of the specified type. No attempt is made to derive a value.
     *
     * @param clazz  the type to query from the map, not null
     * @return the calendrical object, null if it was not parsed
     */
    @SuppressWarnings("unchecked")
    public <T> T getParsed(Class<T> clazz) {
        for (Object obj : currentCalendrical().calendricals) {
            if (clazz.isInstance(obj)) {
                return (T) obj;
            }
        }
        return null;
    }

    /**
     * Gets the list of parsed calendrical information.
     *
     * @return the list of parsed calendricals, not null, no nulls
     */
    List<Object> getParsed() {
        // package scoped for testing
        return currentCalendrical().calendricals;
    }

    /**
     * Stores the parsed field.
     * <p>
     * This stores a field-value pair that has been parsed.
     * The value stored may be out of range for the field - no checks are performed.
     *
     * @param field  the field to set in the field-value map, not null
     * @param value  the value to set in the field-value map
     */
    public void setParsedField(DateTimeField field, long value) {
        DateTimes.checkNotNull(field, "DateTimeField must not be null");
        currentCalendrical().calendricals.add(new FieldValue(field, value));
    }

    /**
     * Stores the parsed calendrical.
     * <p>
     * This stores a calendrical object that has been parsed.
     * No validation is performed on the calendrical other than ensuring it is not null.
     *
     * @param calendrical  the parsed calendrical, not null
     */
    public <T> void setParsed(CalendricalObject calendrical) {
        DateTimes.checkNotNull(calendrical, "Calendrical must not be null");
        currentCalendrical().calendricals.add(calendrical);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a {@code DateTimeBuilder} that can be used to interpret
     * the results of the parse.
     * <p>
     * This method is typically used once parsing is complete to obtain the parsed data.
     * Parsing will typically result in separate fields, such as year, month and day.
     * The returned builder can be used to combine the parsed data into meaningful
     * objects such as {@code LocalDate}, potentially applying complex processing
     * to handle invalid parsed data.
     *
     * @return a new builder with the results of the parse, not null
     */
    public DateTimeBuilder toBuilder() {
        List<Object> cals = currentCalendrical().calendricals;
        DateTimeBuilder builder = new DateTimeBuilder();
        for (Object obj : cals) {
            if (obj instanceof CalendricalObject) {
                builder.addCalendrical((CalendricalObject) obj);
            } else {
                FieldValue fv = (FieldValue) obj;
                builder.addFieldValue(fv.field, fv.value);
            }
        }
        return builder;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a string version of the context for debugging.
     *
     * @return a string representation of the context data, not null
     */
    @Override
    public String toString() {
        return currentCalendrical().toString();
    }

    //-----------------------------------------------------------------------
    /**
     * Temporary store of parsed data.
     */
    private static final class Parsed {
        final List<Object> calendricals = new ArrayList<Object>();
        private Parsed() {
        }
        @Override
        protected Parsed clone() {
            Parsed cloned = new Parsed();
            cloned.calendricals.addAll(this.calendricals);
            return cloned;
        }
        @Override
        public String toString() {
            return calendricals.toString();
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Temporary store of a field-value pair.
     */
    private static final class FieldValue {
        final DateTimeField field;
        final long value;
        private FieldValue(DateTimeField field, long value) {
            this.field = field;
            this.value = value;
        }
        @Override
        public String toString() {
            return field.getName() + ' ' + value;
        }
    }

}
