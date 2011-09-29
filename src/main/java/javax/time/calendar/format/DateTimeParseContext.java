/*
 * Copyright (c) 2008-2011, Stephen Colebourne & Michael Nascimento Santos
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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.time.calendar.Calendrical;
import javax.time.calendar.CalendricalEngine;
import javax.time.calendar.DateTimeField;
import javax.time.calendar.DateTimeRule;

/**
 * Context object used during date and time parsing.
 * <p>
 * This class represents the current state of the parse.
 * It has the ability to store and retrieve the parsed values and manage optional segments.
 * It also provides key information to the parsing methods.
 * <p>
 * Once parsing is complete, the {@link #toCalendricalEngine()} is typically used
 * to obtain a merger that will merge the separate parsed fields into meaningful values.
 * <p>
 * This class is a mutable context intended for use from a single thread.
 * Usage of the class is thread-safe within standard parsing as the framework creates
 * a new instance of the class for each parse and parsing is single-threaded
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public final class DateTimeParseContext {

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
     * @param symbols  the symbols to use during parsing, not null
     */
    public DateTimeParseContext(DateTimeFormatSymbols symbols) {
        super();
        setSymbols(symbols);
        calendricals.add(new Parsed());
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the formatting symbols.
     *
     * @return the formatting symbols, not null
     */
    public DateTimeFormatSymbols getSymbols() {
        return symbols;
    }

    /**
     * Sets the formatting symbols.
     *
     * @param symbols  the formatting symbols, not null
     */
    public void setSymbols(DateTimeFormatSymbols symbols) {
        DateTimeFormatter.checkNotNull(symbols, "DateTimeFormatSymbols must not be null");
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
     * Gets the locale to use for printing and parsing text.
     *
     * @return the locale, not null
     */
    public Locale getLocale() {
        return symbols.getLocale();
    }

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
     * Gets the first field matching the specified rule.
     * <p>
     * This searches the list of parsed calendricals, returning the first field
     * that has the specified rule. No attempt is made to derive a value.
     * The field may have an out of range value.
     * For example, the day-of-month might be set to 50, or the hour to 1000.
     *
     * @param rule  the rule to query from the map, null returns null
     * @return the value mapped to the specified rule, null if rule not in the map
     */
    public DateTimeField getParsed(DateTimeRule rule) {
        for (Calendrical cal : currentCalendrical().calendricals) {
            if (cal instanceof DateTimeField) {
                DateTimeField field = (DateTimeField) cal;
                if (field.getRule().equals(rule)) {
                    return field;
                }
            }
        }
        return null;
    }

    /**
     * Gets the first calendrical of the specified type.
     * <p>
     * This searches the list of parsed calendricals, returning the first calendrical
     * that is of the specified type. No attempt is made to derive a value.
     * The calendricals are not validated, so a field may have an out of range value.
     * For example, the day-of-month might be set to 50, or the hour to 1000.
     *
     * @param clazz  the type to query from the map, not null
     * @return the value mapped to the specified rule, null if rule not in the map
     */
    @SuppressWarnings("unchecked")
    public <T> T getParsed(Class<T> clazz) {
        for (Calendrical cal : currentCalendrical().calendricals) {
            if (clazz.isInstance(cal)) {
                return (T) cal;
            }
        }
        return null;
    }

    /**
     * Gets the list of parsed calendricals.
     * <p>
     * The list is modifiable, but modification is discouraged.
     * <p>
     * The calendricals are not validated, so a field may have an out of range value.
     * For example, the day-of-month might be set to 50, or the hour to 1000.
     *
     * @return the list of previously parsed calendricals, not null, no nulls
     */
    public List<Calendrical> getParsed() {
        return currentCalendrical().calendricals;
    }

    /**
     * Stores the parsed calendrical.
     * <p>
     * No validation is performed on the calendrical other than ensuring it is not null.
     *
     * @param calendrical  the parsed calendrical, not null
     */
    public <T> void setParsed(Calendrical calendrical) {
        DateTimeFormatter.checkNotNull(calendrical, "Calendrical must not be null");
        currentCalendrical().calendricals.add(calendrical);
    }

    /**
     * Stores the parsed field.
     * <p>
     * The value stored may be out of range for the rule - no checks are performed.
     *
     * @param rule  the rule to set in the rule-value map, not null
     * @param value  the value to set in the rule-value map
     */
    public void setParsedField(DateTimeRule rule, long value) {
        DateTimeField field = DateTimeField.of(rule, value);
        currentCalendrical().calendricals.add(field);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a {@code CalendricalMerger} that can be used to interpret
     * the results of the parse.
     * <p>
     * This method is typically used once parsing is complete to obtain the parsed data.
     * Parsing will typically result in separate fields, such as year, month and day.
     * The returned engine can be used to combine the parsed data into meaningful
     * objects such as {@code LocalDate}, potentially applying complex processing
     * to handle invalid parsed data.
     *
     * @return a new independent engine with the parsed calendricals, not null
     */
    public CalendricalEngine toCalendricalEngine() {
        List<Calendrical> cals = getParsed();
        return CalendricalEngine.merge(cals.toArray(new Calendrical[cals.size()]));
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
    static class Parsed {
        final List<Calendrical> calendricals = new ArrayList<Calendrical>();
        
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

}
