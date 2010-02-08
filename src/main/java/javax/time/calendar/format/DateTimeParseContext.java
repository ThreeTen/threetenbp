/*
 * Copyright (c) 2008-2010, Stephen Colebourne & Michael Nascimento Santos
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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.time.calendar.CalendricalContext;
import javax.time.calendar.CalendricalMerger;
import javax.time.calendar.CalendricalRule;
import javax.time.calendar.DateTimeFieldRule;

/**
 * Context object used during date and time parsing.
 * <p>
 * This class represents the current state of the parse.
 * It has the ability to store and retrieve the parsed values and manage optional segments.
 * It also provides key information to the parsing methods.
 * <p>
 * Once parsing is complete, the {@link #toCalendricalMerger()} is typically used
 * to obtain a merger that will merge the separate parsed fields into meaningful values.
 * <p>
 * This class is mutable and thus not thread-safe.
 * Usage of the class is thread-safe within the Time Framework for Java as the
 * framework creates a new instance of the class for each parse.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public final class DateTimeParseContext {

    /**
     * The date time format symbols, not null.
     */
    private final DateTimeFormatSymbols symbols;
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
     * Constructor.
     *
     * @param symbols  the symbols to use during parsing, not null
     */
    public DateTimeParseContext(DateTimeFormatSymbols symbols) {
        super();
        DateTimeFormatter.checkNotNull(symbols, "DateTimeFormatSymbols must not be null");
        this.symbols = symbols;
        calendricals.add(new Parsed());
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the locale to use for printing and parsing text.
     *
     * @return the locale, never null
     */
    public Locale getLocale() {
        return symbols.getLocale();
    }

    /**
     * Gets the formatting symbols.
     *
     * @return the formatting symbols, never null
     */
    public DateTimeFormatSymbols getSymbols() {
        return symbols;
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
     * Gets the currently active calendrical.
     *
     * @return the current calendrical, never null
     */
    private Parsed currentCalendrical() {
        return calendricals.get(calendricals.size() - 1);
    }

    /**
     * Gets the parsed value for the specified rule.
     * <p>
     * The value returned is directly obtained from the stored map of values.
     * It may be of any type and any value.
     * For example, the day-of-month might be set to 50, or the hour to 1000.
     *
     * @param rule  the rule to query from the map, not null
     * @return the value mapped to the specified rule, null if rule not in the map
     */
    public Object getParsed(CalendricalRule<?> rule) {
        DateTimeFormatter.checkNotNull(rule, "CalendricalRule must not be null");
        return currentCalendrical().values.get(rule);
    }

    /**
     * Gets the set of parsed rules.
     * <p>
     * The set can be read and have elements removed, but nothing can be added.
     *
     * @return the set of rules previously parsed, never null
     */
    public Set<CalendricalRule<?>> getParsedRules() {
        return currentCalendrical().values.keySet();
    }

    /**
     * Sets the parsed value associated with the specified rule.
     * <p>
     * The value stored may be out of range for the rule and of any type -
     * no checks are performed.
     *
     * @param rule  the rule to set in the rule-value map, not null
     * @param value  the value to set in the rule-value map, not null
     */
    public void setParsed(CalendricalRule<?> rule, Object value) {
        DateTimeFormatter.checkNotNull(rule, "CalendricalRule must not be null");
        DateTimeFormatter.checkNotNull(value, "Value must not be null");
        currentCalendrical().values.put(rule, value);
    }

    /**
     * Sets the parsed value associated with the specified rule.
     * <p>
     * The value stored may be out of range for the rule - no checks are performed.
     *
     * @param rule  the rule to set in the rule-value map, not null
     * @param value  the value to set in the rule-value map
     */
    public void setParsed(DateTimeFieldRule<?> rule, int value) {
        DateTimeFormatter.checkNotNull(rule, "DateTimeFieldRule must not be null");
        currentCalendrical().values.put(rule, value);
    }

    //-----------------------------------------------------------------------
    /**
     * Starts the parsing of an optional segment of the input.
     */
    public void startOptional() {
        calendricals.add(currentCalendrical().clone());
    }

    /**
     * Ends the parsing of an optional segment of the input.
     *
     * @param successful  whether the optional segment was successfully parsed
     */
    public void endOptional(boolean successful) {
        if (successful) {
            calendricals.remove(calendricals.size() - 2);
        } else {
            calendricals.remove(calendricals.size() - 1);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a {@code CalendricalMerger} that can be used to interpret
     * the results of the parse.
     * <p>
     * This method is typically used once parsing is complete to obtain the parsed data.
     * Parsing will typically result in separate fields, such as year, month and day.
     * The returned merger can be used to combine the parsed data into meaningful
     * objects such as {@code LocalDate}, potentially applying complex processing
     * to handle invalid parsed data.
     *
     * @return a new independent merger with the parsed rule-value map, never null
     */
    public CalendricalMerger toCalendricalMerger() {
        return new CalendricalMerger(new CalendricalContext(true, true), currentCalendrical().values);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a string version of the context for debugging.
     *
     * @return a string representation of the context date, never null
     */
    @Override
    public String toString() {
        return currentCalendrical().toString();
    }

    static class Parsed {
        Map<CalendricalRule<?>, Object> values = new HashMap<CalendricalRule<?>, Object>();
        
        @Override
        protected Parsed clone() {
            Parsed cloned = new Parsed();
            cloned.values.putAll(this.values);
            return cloned;
        }
        @Override
        public String toString() {
            return new TreeMap<CalendricalRule<?>, Object>(values).toString();
        }
    }

}
