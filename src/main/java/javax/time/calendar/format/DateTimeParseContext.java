/*
 * Copyright (c) 2008-2009, Stephen Colebourne & Michael Nascimento Santos
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
import java.util.Locale;

import javax.time.calendar.Calendrical;
import javax.time.calendar.CalendricalProvider;
import javax.time.calendar.DateTimeFieldRule;
import javax.time.calendar.TimeZone;
import javax.time.calendar.UnsupportedCalendarFieldException;
import javax.time.calendar.ZoneOffset;

/**
 * Context object used during date and time parsing.
 * <p>
 * This context is essentially a wrapper around a {@link Calendrical} providing
 * methods and functionality to support parsing.
 * <p>
 * This class is mutable and thus not thread-safe.
 * Usage of the class is thread-safe within the Java Time Framework as the
 * framework creates a new instance of the class for each parse.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public final class DateTimeParseContext implements CalendricalProvider {

    /**
     * The date time format symbols, not null.
     */
    private final DateTimeFormatSymbols symbols;
    /**
     * The calendrical collecting the results.
     */
    private final ArrayList<Calendrical> calendricals = new ArrayList<Calendrical>();

    /**
     * Constructor.
     *
     * @param symbols  the symbols to use during parsing, not null
     */
    public DateTimeParseContext(DateTimeFormatSymbols symbols) {
        super();
        FormatUtil.checkNotNull(symbols, "symbols");
        this.symbols = symbols;
        calendricals.add(new Calendrical());
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
     * Gets the currently active calendrical.
     *
     * @return the current calendrical, never null
     */
    private Calendrical currentCalendrical() {
        return calendricals.get(calendricals.size() - 1);
    }

    /**
     * Gets the value for the specified field throwing an exception if the
     * field is not in the field-value map.
     * <p>
     * The value returned might contradict the date or time, or be out of
     * range for the rule.
     * <p>
     * For example, the day of month might be set to 50, or the hour to 1000.
     *
     * @param fieldRule  the rule to query from the map, not null
     * @return the value mapped to the specified field
     * @throws UnsupportedCalendarFieldException if the field is not in the map
     */
    public int getFieldValueMapValue(DateTimeFieldRule fieldRule) {
        return currentCalendrical().deriveValue(fieldRule);
    }

    /**
     * Sets the value associated with the specified field rule.
     *
     * @param fieldRule  the field to set in the field-value map, not null
     * @param value  the value to set in the field-value map
     */
    public void setFieldValue(DateTimeFieldRule fieldRule, int value) {
        currentCalendrical().getFieldMap().put(fieldRule, value);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the optional time zone offset, such as '+02:00'.
     * This method will return null if the offset is null.
     *
     * @return the offset, may be null
     */
    public ZoneOffset getOffset() {
        return currentCalendrical().getOffset();
    }

    /**
     * Sets the parsed offset, such as '+02:00'.
     *
     * @param offset  the zone offset to store, may be null
     */
    public void setOffset(ZoneOffset offset) {
        currentCalendrical().setOffset(offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the optional time zone rules, such as 'Europe/Paris'.
     * This method will return null if the zone is null.
     *
     * @return the zone, may be null
     */
    public TimeZone getZone() {
        return currentCalendrical().getZone();
    }

    /**
     * Sets the parsed zone, such as 'Europe/Paris'.
     *
     * @param zone  the zone to store, may be null
     */
    public void setZone(TimeZone zone) {
        currentCalendrical().setZone(zone);
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
     * Converts this object to a Calendrical with the same fields.
     * <p>
     * The returned calendrical is part of the internal state of this object.
     * This design is chosen for performance.
     *
     * @return the current Calendrical state with the parsed fields, never null
     */
    Calendrical asCalendrical() {
        return currentCalendrical();
    }

    /**
     * Converts this object to a Calendrical with the same fields.
     * <p>
     * The returned calendrical is independent of the internal state of this object.
     *
     * @return a new Calendrical with the parsed fields, never null
     */
    public Calendrical toCalendrical() {
        return currentCalendrical().clone();
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

}
