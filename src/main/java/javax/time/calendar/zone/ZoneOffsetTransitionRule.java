/*
 * Copyright (c) 2009-2010, Stephen Colebourne & Michael Nascimento Santos
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
import javax.time.calendar.ISOChronology;
import javax.time.calendar.LocalDate;
import javax.time.calendar.LocalDateTime;
import javax.time.calendar.LocalTime;
import javax.time.calendar.OffsetDateTime;
import javax.time.calendar.ZoneOffset;
import javax.time.calendar.field.DayOfWeek;
import javax.time.calendar.field.MonthOfYear;
import javax.time.calendar.zone.ZoneRulesBuilder.TimeDefinition;

/**
 * A rule expressing how to create transitions.
 * <p>
 * This class allows rules for identifying future transitions to be expressed.
 * A rule might be of n forms:
 * <ul>
 * <li>the 16th March
 * <li>the Sunday on or after the 16th March
 * <li>the Sunday on or before the 16th March
 * <li>the last Sunday in March
 * </ul>
 * These different rule types can be expressed and queried.
 * <p>
 * ZoneOffsetTransitionRule is immutable and thread-safe.
 *
 * @author Stephen Colebourne
 */
public final class ZoneOffsetTransitionRule implements Serializable {

    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = -32352886665458L;

    /**
     * The month of the month-day of the first day of the cutover week.
     * The actual date will be adjusted by the dowChange field.
     */
    private final MonthOfYear month;
    /**
     * The day-of-month of the month-day of the cutover week.
     * If positive, it is the start of the week where the cutover can occur.
     * If negative, it represents the end of the week where cutover can occur.
     * The value is the number of days from the end of the month, such that
     * <code>-1</code> is the last day of the month, <code>-2</code> is the second
     * to last day, and so on.
     */
    private final int dom;
    /**
     * The cutover day-of-week, null to retain the day-of-month.
     */
    private final DayOfWeek dow;
    /**
     * The cutover time in the 'before' offset.
     */
    private final LocalTime time;
    /**
     * The definition of how the local time should be interpreted.
     */
    private final TimeDefinition timeDefinition;
    /**
     * The standard offset at the cutover.
     */
    private final ZoneOffset standardOffset;
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
     * @param month  the month of the month-day of the first day of the cutover week, not null
     * @param dayOfMonthIndicator  the day of the month-day of the cutover week, positive if the week is that
     *  day or later, negative if the week is that day or earlier, counting from the last day of the month
     * @param dayOfWeek  the required day-of-week, null if the month-day should not be changed
     * @param time  the cutover time in the 'before' offset, not null
     * @param timeDefnition  how to interpret the cutover
     * @param standardOffset  the standard offset in force at the cutover, not null
     * @param offsetBefore  the offset before the cutover, not null
     * @param offsetAfter  the offset after the cutover, not null
     */
    ZoneOffsetTransitionRule(
            MonthOfYear month,
            int dayOfMonthIndicator,
            DayOfWeek dayOfWeek,
            LocalTime time,
            TimeDefinition timeDefnition,
            ZoneOffset standardOffset,
            ZoneOffset offsetBefore,
            ZoneOffset offsetAfter) {
        ZoneRules.checkNotNull(month, "MonthOfYear must not be null");
        ZoneRules.checkNotNull(time, "LocalTime must not be null");
        ZoneRules.checkNotNull(timeDefnition, "TimeDefinition must not be null");
        ZoneRules.checkNotNull(standardOffset, "Standard offset must not be null");
        ZoneRules.checkNotNull(offsetBefore, "Offset before must not be null");
        ZoneRules.checkNotNull(offsetAfter, "Offset after must not be null");
        this.month = month;
        this.dom = dayOfMonthIndicator;
        this.dow = dayOfWeek;
        this.time = time;
        this.timeDefinition = timeDefnition;
        this.standardOffset = standardOffset;
        this.offsetBefore = offsetBefore;
        this.offsetAfter = offsetAfter;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the month of the transition.
     * <p>
     * If the rule defines an exact date then the month is the month of that date.
     * <p>
     * If the rule defines a week where the transition might occur, then the month
     * if the month of either the earliest or latest possible date of the cutover.
     *
     * @return the month of the transition, never null
     */
    public MonthOfYear getMonthOfYear() {
        return month;
    }

    /**
     * Gets the indicator of the day-of-month of the transition.
     * <p>
     * If the rule defines an exact date then the day is the month of that date.
     * <p>
     * If the rule defines a week where the transition might occur, then the day
     * defines either the start of the end of the transition week.
     * If the value is positive, then it represents a normal day-of-month, and is the
     * earliest possible date that the transition can be.
     * If the value is negative, then it represents the number of days back from the
     * end of the month where <code>-1</code> is the last day of the month.
     * In this case, the day identified is the latest possible date that the transition can be.
     *
     * @return the day-of-month indicator, from -31 to 31 excluding 0
     */
    public int getDayOfMonthIndicator() {
        return dom;
    }

    /**
     * Gets the day-of-week of the transition.
     * <p>
     * If the rule defines an exact date then this returns null.
     * <p>
     * If the rule defines a week where the cutover might occur, then this method
     * returns the day-of-week that the month-day will be adjusted to.
     * If the day is positive then the adjustment is later.
     * If the day is negative then the adjustment is earlier.
     *
     * @return the day-of-week that the transition occurs, null if the rule defines an exact date
     */
    public DayOfWeek getDayOfWeek() {
        return dow;
    }

    /**
     * Gets the local time of day of the transition.
     * <p>
     * The time is converted into an instant using the time definition.
     *
     * @return the local time of day of the transition, never null
     */
    public LocalTime getLocalTime() {
        return time;
    }

    /**
     * Gets the time definition, specifying how to convert the time to an instant.
     * <p>
     * The local time can be converted to an instant using the standard offset,
     * the wall offset or UTC.
     *
     * @return the time definition, never null
     */
    public TimeDefinition getTimeDefinition() {
        return timeDefinition;
    }

    /**
     * Gets the standard offset in force at the transition.
     *
     * @return the standard offset, never null
     */
    public ZoneOffset getStandardOffset() {
        return standardOffset;
    }

    /**
     * Gets the offset before the transition.
     *
     * @return the offset before, never null
     */
    public ZoneOffset getOffsetBefore() {
        return offsetBefore;
    }

    /**
     * Gets the offset after the transition.
     *
     * @return the offset after, never null
     */
    public ZoneOffset getOffsetAfter() {
        return offsetAfter;
    }

    //-----------------------------------------------------------------------
    /**
     * Creates a transition instance for the specified year.
     *
     * @param year  the year to create a transition for, not null
     * @return the transition instance, never null
     */
    public ZoneOffsetTransition createTransition(int year) {
        LocalDate date;
        if (dom < 0) {
            date = LocalDate.of(year, month, month.getLastDayOfMonth(ISOChronology.isLeapYear(year)) + 1 + dom);
            if (dow != null) {
                date = date.with(DateAdjusters.previousOrCurrent(dow));
            }
        } else {
            date = LocalDate.of(year, month, dom);
            if (dow != null) {
                date = date.with(DateAdjusters.nextOrCurrent(dow));
            }
        }
        LocalDateTime localDT = LocalDateTime.from(date, time);
        OffsetDateTime transition = timeDefinition.createDateTime(localDT, standardOffset, offsetBefore);
        return new ZoneOffsetTransition(transition, offsetAfter);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this instance equals another, comparing the entire set of rules.
     *
     * @param otherRule  the other object to compare to, null returns false
     * @return true if equal
     */
    @Override
    public boolean equals(Object otherRule) {
        if (otherRule == this) {
            return true;
        }
        if (otherRule instanceof ZoneOffsetTransitionRule) {
            ZoneOffsetTransitionRule other = (ZoneOffsetTransitionRule) otherRule;
            return month == other.month && dom == other.dom && dow == other.dow &&
                timeDefinition == other.timeDefinition &&
                time.equals(other.time) &&
                standardOffset.equals(other.standardOffset) &&
                offsetBefore.equals(other.offsetBefore) &&
                offsetBefore.equals(other.offsetBefore);
        }
        return false;
    }

    /**
     * Gets the hash code, based on all the rules.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        int hash = (time.toSecondOfDay() << 15) + (month.ordinal() << 11) + ((dom + 32) << 5) +
                (dow.ordinal() << 2) + (timeDefinition.ordinal());
        return hash ^ standardOffset.hashCode() ^
                offsetBefore.hashCode() ^ offsetAfter.hashCode();
    }

    //-----------------------------------------------------------------------
    /**
     * Gets a string describing this object.
     *
     * @return a string for debugging, never null
     */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("TransitionRule[")
            .append(offsetBefore.compareTo(offsetAfter) > 0 ? "Gap " : "Overlap ")
            .append(offsetBefore).append(" to ").append(offsetAfter).append(", ");
        if (dow != null) {
            if (dom < 0) {
                buf.append(dow.name()).append(" on or before ").append(month.name()).append(' ').append(dom);
            } else {
                buf.append(dow.name()).append(" on or after ").append(month.name()).append(' ').append(dom);
            }
        } else {
            buf.append(month.name()).append(' ').append(dom);
        }
        buf.append(" at ").append(time)
            .append(" ").append(timeDefinition)
            .append(", standard offset ").append(standardOffset)
            .append(']');
        return buf.toString();
    }

}
