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
package javax.time.i18n;

import javax.time.CalendricalException;
import javax.time.calendar.CalendricalRule;
import javax.time.calendar.Chronology;
import javax.time.calendar.LocalDate;

/**
 * A standard calendar system formed of eras, years, months and days.
 * <p>
 * Most calendar systems are formed of common elements - eras, years, months, weeks and days.
 * This class provides a common way to access this in association with {@link ChronologyDate}.
 * Note that not all calendar systems are standard.
 * For example, the Mayan calendar has a completely different way of counting.
 * <p>
 * <b>era</b> The largest division of the time-line.
 * Most calendar systems divide into two eras, such as CE/AD and BCE/BC on the Gregorian calendar.
 * This value may be positive or negative.
 * The era in use at 1970-01-01 must have the value 1.
 * Later eras must have sequentially higher values.
 * Earlier eras must have sequentially lower values.
 * Each chronology should have constants providing meaning to the era value.
 * <p>
 * <b>year-of-era</b> The year counted within the era.
 * This value must be positive and is a sequential count within the era.
 * The count may run backwards, for example, 2 BCE/BC is before 1 BCE/BC in the Gregorian calendar
 * despite being a larger value.
 * <p>
 * <b>proleptic-year</b> The year counted in a proleptic manner across the entire time-line.
 * This value may be positive or negative and is a sequential count.
 * Earlier years must have a smaller value than later years.
 * The proleptic-year numbering scheme should be the same as year-of-era for the era in use at 1970-01-01.
 * <p>
 * <b>month-of-year</b> The month counted within the year.
 * This value must be positive.
 * Earlier months must have a smaller value than later months.
 * There may be gaps in the sequence of values.
 * <p>
 * <b>day-of-month</b> The day counted within the month.
 * This value must be positive.
 * Earlier days must have a smaller value than later days.
 * There may be gaps in the sequence of values.
 * <p>
 * <b>day-of-year</b> The day counted within the year.
 * This value must be positive and is a sequential count from 1.
 * <p>
 * This is an abstract class and must be implemented with care to
 * ensure other classes in the framework operate correctly.
 * All instantiable subclasses must be final, immutable and thread-safe.
 * Wherever possible subclasses should be singletons with no public constructor.
 * It is recommended that subclasses implement {@code Serializable}
 *
 * @author Stephen Colebourne
 */
public abstract class StandardChronology extends Chronology {

    /**
     * Restrictive constructor.
     */
    protected StandardChronology() {
    }

    //-----------------------------------------------------------------------
    /**
     * Creates a Coptic date from a standard ISO-8601 date.
     *
     * @return the Coptic date, not null
     */
    public abstract ChronologyDate createChronologyDate(LocalDate date);

    /**
     * Merges the era, year-of-era, month-of-year and day-of-month fields to form a date.
     * 
     * @param era  the calendar system era
     * @param yearOfEra  the calendar system year-of-era
     * @param monthOfYear  the calendar system month-of-year
     * @param dayOfMonth  the calendar system day-of-month
     * @return the date, not null
     * @throws CalendricalException if unable to create a date
     */
    public abstract ChronologyDate createChronologyDate(int era, int yearOfEra, int monthOfYear, int dayOfMonth);

    /**
     * Checks if the specified date is in a leap year.
     * <p>
     * The date will always have the correct chronology.
     *
     * @param date  the date to check, not null
     * @return true if the date is in a leap year
     */
    protected abstract boolean isLeapYear(ChronologyDate date);

    /**
     * Gets a calendrical rule for the {@code ChronologyDate}.
     * 
     * @return the rule, not null
     */
    public abstract CalendricalRule<ChronologyDate> dateRule();

}
