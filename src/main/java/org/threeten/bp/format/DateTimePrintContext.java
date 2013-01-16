/*
 * Copyright (c) 2011-2013, Stephen Colebourne & Michael Nascimento Santos
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

import java.util.Locale;
import java.util.Objects;

import org.threeten.bp.DateTimeException;
import org.threeten.bp.temporal.DateTimeAccessor;
import org.threeten.bp.temporal.DateTimeAccessor.Query;
import org.threeten.bp.temporal.DateTimeField;

/**
 * Context object used during date and time printing.
 * <p>
 * This class provides a single wrapper to items used in the print.
 *
 * <h4>Implementation notes</h4>
 * This class is a mutable context intended for use from a single thread.
 * Usage of the class is thread-safe within standard printing as the framework creates
 * a new instance of the class for each print and printing is single-threaded.
 */
final class DateTimePrintContext {

    /**
     * The date-time being output.
     */
    private DateTimeAccessor dateTime;
    /**
     * The locale, not null.
     */
    private Locale locale;
    /**
     * The date time format symbols, not null.
     */
    private DateTimeFormatSymbols symbols;
    /**
     * Whether the current formatter is optional.
     */
    private int optional;

    /**
     * Creates a new instance of the context.
     * <p>
     * This should normally only be created by the printer.
     *
     * @param dateTime  the date-time being output, not null
     * @param locale  the locale to use, not null
     * @param symbols  the symbols to use during parsing, not null
     */
    DateTimePrintContext(DateTimeAccessor dateTime, Locale locale, DateTimeFormatSymbols symbols) {
        super();
        setDateTime(dateTime);
        setLocale(locale);
        setSymbols(symbols);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the date-time being output.
     *
     * @return the date-time, not null
     */
    DateTimeAccessor getDateTime() {
        return dateTime;
    }

    /**
     * Sets the date-time being output.
     *
     * @param dateTime  the date-time object, not null
     */
    void setDateTime(DateTimeAccessor dateTime) {
        Objects.requireNonNull(dateTime, "dateTime");
        this.dateTime = dateTime;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the locale.
     * <p>
     * This locale is used to control localization in the print output except
     * where localization is controlled by the symbols.
     *
     * @return the locale, not null
     */
    Locale getLocale() {
        return locale;
    }

    /**
     * Sets the locale.
     * <p>
     * This locale is used to control localization in the print output except
     * where localization is controlled by the symbols.
     *
     * @param locale  the locale, not null
     */
    void setLocale(Locale locale) {
        Objects.requireNonNull(locale, "locale");
        this.locale = locale;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the formatting symbols.
     * <p>
     * The symbols control the localization of numeric output.
     *
     * @return the formatting symbols, not null
     */
    DateTimeFormatSymbols getSymbols() {
        return symbols;
    }

    /**
     * Sets the formatting symbols.
     * <p>
     * The symbols control the localization of numeric output.
     *
     * @param symbols  the formatting symbols, not null
     */
    void setSymbols(DateTimeFormatSymbols symbols) {
        Objects.requireNonNull(symbols, "symbols");
        this.symbols = symbols;
    }

    //-----------------------------------------------------------------------
    /**
     * Starts the printing of an optional segment of the input.
     */
    void startOptional() {
        this.optional++;
    }

    /**
     * Ends the printing of an optional segment of the input.
     */
    void endOptional() {
        this.optional--;
    }

    /**
     * Gets a value using a query.
     *
     * @param query  the query to use, not null
     * @return the result, null if not found and optional is true
     * @throws DateTimeException if the type is not available and the section is not optional
     */
    <R> R getValue(Query<R> query) {
        R result = dateTime.query(query);
        if (result == null && optional == 0) {
            throw new DateTimeException("Unable to extract value: " + dateTime.getClass());
        }
        return result;
    }

    /**
     * Gets the value of the specified field.
     * <p>
     * This will return the value for the specified field.
     *
     * @param field  the field to find, not null
     * @return the value, null if not found and optional is true
     * @throws DateTimeException if the field is not available and the section is not optional
     */
    Long getValue(DateTimeField field) {
        try {
            return dateTime.getLong(field);
        } catch (DateTimeException ex) {
            if (optional > 0) {
                return null;
            }
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a string version of the context for debugging.
     *
     * @return a string representation of the context, not null
     */
    @Override
    public String toString() {
        return dateTime.toString();
    }

}
