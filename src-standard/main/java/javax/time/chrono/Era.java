/*
 * Copyright (c) 2012, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.chrono;

/**
 * An era of the time-line.
 * <p>
 * Most calendar systems have a single epoch dividing the time-line into two eras.
 * However, some calendar systems, have multiple eras, such as one for the reign
 * of each leader.
 * In all cases, the era is conceptually the largest division of the time-line.
 * Each Chronology defines the Era's that are known Eras and a 
 * {@link Chronology#eras Chronology.eras} to get the valid eras.
 * <p>
 * For example, the Gregorian calendar system divides time into AD and BC.
 * By contrast, the Japanese imperial calendar system has one modern era per Emperor's reign.
 * <p>
 * Instances of {@code Era} may be compared using the {@code ==} operator.
 *
 * <h4>Implementation notes</h4>
 * This interface must be implemented with care to ensure other classes operate correctly.
 * All implementations must be singletons - final, immutable and thread-safe.
 * It is recommended to use an enum whenever possible.
 * An implementation of {@code Era} may be shared between different calendar systems
 * if appropriate.
 * @param C the Chronology of the Era
 */
public interface Era<C extends Chronology<C>>  {

    /**
     * Gets the numeric value associated with the era as defined by the chronology.
     * Each chronology defines the predefined Eras and methods to list the Eras
     * of the chronology.
     * <p>
     * All fields, including eras, must have an associated numeric value.
     * The meaning of the numeric value for era is determined by the chronology
     * according to these principles:
     * <p>
     * The era in use at the epoch 1970-01-01 (ISO) has the value 1.
     * Later eras have sequentially higher values.
     * Earlier eras have sequentially lower values, which may be negative.
     * <p>
     * For example, the Gregorian chronology uses AD/BC, with AD being 1 and BC being 0.
     *
     * @return the numeric era value
     */
    int getValue();

    /**
     * Returns a new ChronoLocalDate in this Era from the year, month, and day.
     * @param year the year of eara
     * @param month the month of year
     * @param day the day of month
     * @return a new ChronoLocalDate of the Era, year, month, day using the Chronology of the era.
     */
    ChronoLocalDate<C> date(int year, int month, int day);

    /**
     * Creates a new ChronoLocalDate in this Era from year and day-of-year fields.
     *
     * @param yearOfEra  the calendar system year-of-era
     * @param dayOfYear  the calendar system day-of-year
     * @return the date in this calendar system, not null
     */
    ChronoLocalDate<C> dateFromYearDay(int yearOfEra, int dayOfYear);


    // NOTE: methods to convert year/proleptic-year cannot be here as they may depend on month/day
}
