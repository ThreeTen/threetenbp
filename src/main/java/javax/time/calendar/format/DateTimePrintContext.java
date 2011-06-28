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

import java.util.Locale;

import javax.time.CalendricalException;
import javax.time.calendar.Calendrical;
import javax.time.calendar.CalendricalRule;

/**
 * Context object used during date and time printing.
 * <p>
 * This class provides a single wrapper to items used in the print.
 * <p>
 * This class is mutable and thus not thread-safe.
 * Usage of the class is thread-safe within the Time Framework for Java as the
 * framework creates a new instance of the class for each parse.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public final class DateTimePrintContext {

    /**
     * The calendrical being output.
     */
    private Calendrical calendrical;
    /**
     * The date time format symbols, not null.
     */
    private DateTimeFormatSymbols symbols;

    /**
     * Constructor.
     *
     * @param calendrical  the calendrical being output, not null
     * @param symbols  the symbols to use during parsing, not null
     */
    public DateTimePrintContext(Calendrical calendrical, DateTimeFormatSymbols symbols) {
        super();
        setCalendrical(calendrical);
        setSymbols(symbols);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the calendrical being output.
     *
     * @return the calendrical, not null
     */
    public Calendrical getCalendrical() {
        return calendrical;
    }

    /**
     * Sets the calendrical being output.
     *
     * @param calendrical  the calendrical, not null
     */
    public void setCalendrical(Calendrical calendrical) {
        DateTimeFormatter.checkNotNull(calendrical, "Calendrical must not be null");
        this.calendrical = calendrical;
    }

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
     * Gets the value of the specified rule checking it is non-null.
     *
     * @param rule  the rue to find, not null
     * @return the value, not null
     * @throws CalendricalException if the rule is not available
     */
    public <T> T getValueChecked(CalendricalRule<T> rule) {
        return rule.getValueChecked(calendrical);
    }

    /**
     * Gets the locale to use for printing and parsing text.
     *
     * @return the locale, not null
     */
    public Locale getLocale() {
        return symbols.getLocale();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a string version of the context for debugging.
     *
     * @return a string representation of the context, not null
     */
    @Override
    public String toString() {
        return calendrical.toString();
    }

}
