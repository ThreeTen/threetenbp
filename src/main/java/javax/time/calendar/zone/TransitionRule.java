/*
 * Copyright (c) 2009, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.calendar.zone;

import java.io.Serializable;

import javax.time.calendar.DateAdjusters;
import javax.time.calendar.LocalDate;
import javax.time.calendar.LocalTime;
import javax.time.calendar.OffsetDateTime;
import javax.time.calendar.ZoneOffset;
import javax.time.calendar.field.DayOfMonth;
import javax.time.calendar.field.DayOfWeek;
import javax.time.calendar.field.MonthOfYear;
import javax.time.calendar.field.Year;

/**
 * A rule expressing how to create transitions.
 * <p>
 * TransitionRule is immutable and thread-safe.
 *
 * @author Stephen Colebourne
 */
class TransitionRule implements Serializable {

    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = -32352886665458L;

    /**
     * The month of the month-day of the first day of the cutover week.
     * The actual date will be adjusted by the dowChange field.
     */
    private final MonthOfYear cutoverMonth;
    /**
     * The day of month of the month-day of the first day of the cutover week
     * within the date range. The actual date will be adjusted by the dowChange field.
     * If null, then it means the last day of month and adjust earlier.
     */
    private final DayOfMonth cutoverWeekStart;
    /**
     * The cutover day of week, null to retain the day of month.
     */
    private final DayOfWeek dowChange;
    /**
     * The cutover time in the 'before' offset.
     */
    private final LocalTime cutoverTime;
    /**
     * The offset before the cutover.
     */
    private final ZoneOffset offsetBefore;
    /**
     * The offset after the cutover.
     */
    private final ZoneOffset offsetAfter;

    /**
     * Constructor.
     *
     * @param cutoverMonth  the month of the month-day of the first day of the cutover week, not null
     * @param cutoverWeekStart  the day of the month-day of the first day of the cutover week, not null
     * @param dowChange  the required day of week, null if the month-day should not be changed
     * @param cutoverTime  the cutover time in the 'before' offset, not null
     * @param offsetBefore  the offset before the cutover, not null
     * @param offsetAfter  the offset after the cutover, not null
     */
    public TransitionRule(
            MonthOfYear cutoverMonth,
            DayOfMonth cutoverWeekStart,
            DayOfWeek dowChange,
            LocalTime cutoverTime,
            ZoneOffset offsetBefore,
            ZoneOffset offsetAfter) {
        this.cutoverMonth = cutoverMonth;
        this.cutoverWeekStart = cutoverWeekStart;
        this.dowChange = dowChange;
        this.cutoverTime = cutoverTime;
        this.offsetBefore = offsetBefore;
        this.offsetAfter = offsetAfter;
    }

    //-----------------------------------------------------------------------
    /**
     * Creates a transition instance for the specified year.
     *
     * @param year  the year to create a transition for, not null
     * @return the transition instance, never null
     */
    public Transition createTransition(Year year) {
        LocalDate date;
        if (cutoverWeekStart == null) {
            date = LocalDate.date(year, cutoverMonth, cutoverMonth.getLastDayOfMonth(year));
            if (dowChange != null) {
                date = date.with(DateAdjusters.previousOrCurrent(dowChange));
            }
        } else {
            date = LocalDate.date(year, cutoverMonth, cutoverWeekStart);
            if (dowChange != null) {
                date = date.with(DateAdjusters.nextOrCurrent(dowChange));
            }
        }
        OffsetDateTime cutover = OffsetDateTime.dateTime(date, cutoverTime, offsetBefore);
        return new Transition(cutover, offsetAfter);
    }

}
