/*
 * Copyright (c) 2007-2013, Stephen Colebourne & Michael Nascimento Santos
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
package org.threeten.bp.temporal;

import java.util.Locale;

import org.threeten.bp.format.TextStyle;

/**
 * An era of the time-line.
 * <p>
 * Most calendar systems have a single epoch dividing the time-line into two eras.
 * However, some calendar systems, have multiple eras, such as one for the reign
 * of each leader.
 * In all cases, the era is conceptually the largest division of the time-line.
 * Each chronology defines the Era's that are known Eras and a
 * {@link Chrono#eras Chrono.eras} to get the valid eras.
 * <p>
 * For example, the Thai Buddhist calendar system divides time into two eras,
 * before and after a single date. By contrast, the Japanese calendar system
 * has one era for the reign of each Emperor.
 * <p>
 * Instances of {@code Era} may be compared using the {@code ==} operator.
 *
 * <h3>Specification for implementors</h3>
 * This interface must be implemented with care to ensure other classes operate correctly.
 * All implementations must be singletons - final, immutable and thread-safe.
 * It is recommended to use an enum whenever possible.
 *
 * @param <C> the chronology of the era
 */
public interface Era<C extends Chrono<C>> extends TemporalAccessor, TemporalAdjuster {

    /**
     * Gets the numeric value associated with the era as defined by the chronology.
     * Each chronology defines the predefined Eras and methods to list the Eras
     * of the chronology.
     * <p>
     * All fields, including eras, have an associated numeric value.
     * The meaning of the numeric value for era is determined by the chronology
     * according to these principles:
     * <p><ul>
     * <li>The era in use at the epoch 1970-01-01 (ISO) has the value 1.
     * <li>Later eras have sequentially higher values.
     * <li>Earlier eras have sequentially lower values, which may be negative.
     * </ul><p>
     *
     * @return the numeric era value
     */
    int getValue();

    /**
     * Gets the chronology of this era.
     * <p>
     * The {@code Chrono} represents the calendar system in use.
     * This always returns the standard form of the chronology.
     *
     * @return the chronology, not null
     */
    C getChrono();

    //-----------------------------------------------------------------------
    /**
     * Obtains a date in this era given the year-of-era, month, and day.
     * <p>
     * This era is combined with the given date fields to form a date.
     * The year specified must be the year-of-era.
     * Methods to create a date from the proleptic-year are on {@code Chrono}.
     * This always uses the standard form of the chronology.
     *
     * @param yearOfEra  the calendar system year-of-era
     * @param month  the calendar system month-of-year
     * @param day  the calendar system day-of-month
     * @return a local date based on this era and the specified year-of-era, month and day
     */
    ChronoLocalDate<C> date(int yearOfEra, int month, int day);

    /**
     * Obtains a date in this era given year-of-era and day-of-year fields.
     * <p>
     * This era is combined with the given date fields to form a date.
     * The year specified must be the year-of-era.
     * Methods to create a date from the proleptic-year are on {@code Chrono}.
     * This always uses the standard form of the chronology.
     *
     * @param yearOfEra  the calendar system year-of-era
     * @param dayOfYear  the calendar system day-of-year
     * @return a local date based on this era and the specified year-of-era and day-of-year
     */
    ChronoLocalDate<C> dateYearDay(int yearOfEra, int dayOfYear);

    //-----------------------------------------------------------------------
    /**
     * Gets the textual representation of this era.
     * <p>
     * This returns the textual name used to identify the era.
     * The parameters control the style of the returned text and the locale.
     * <p>
     * If no textual mapping is found then the {@link #getValue() numeric value} is returned.
     *
     * @param style  the style of the text required, not null
     * @param locale  the locale to use, not null
     * @return the text value of the era, not null
     */
    String getText(TextStyle style, Locale locale);

    // NOTE: methods to convert year-of-era/proleptic-year cannot be here as they may depend on month/day (Japanese)
}
