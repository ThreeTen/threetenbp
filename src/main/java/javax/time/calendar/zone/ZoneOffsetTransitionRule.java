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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

import javax.time.calendar.DateAdjusters;
import javax.time.calendar.DayOfWeek;
import javax.time.calendar.ISOChronology;
import javax.time.calendar.LocalDate;
import javax.time.calendar.LocalDateTime;
import javax.time.calendar.LocalTime;
import javax.time.calendar.MonthOfYear;
import javax.time.calendar.OffsetDateTime;
import javax.time.calendar.ZoneOffset;
import javax.time.calendar.zone.ZoneRulesBuilder.TimeDefinition;

/**
 * A rule expressing how to create a transition.
 * <p>
 * This class allows rules for identifying future transitions to be expressed.
 * A rule might be written in many forms:
 * <ul>
 * <li>the 16th March
 * <li>the Sunday on or after the 16th March
 * <li>the Sunday on or before the 16th March
 * <li>the last Sunday in February
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
     * {@code -1} is the last day of the month, {@code -2} is the second
     * to last day, and so on.
     */
    private final byte dom;
    /**
     * The cutover day-of-week, null to retain the day-of-month.
     */
    private final DayOfWeek dow;
    /**
     * The cutover time in the 'before' offset.
     */
    private final LocalTime time;
    /**
     * Whether the cutover time is midnight at the end of day.
     */
    private boolean timeEndOfDay;
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
     * Creates an instance defining the yearly rule to create transitions between two offsets.
     * <p>
     * Applications should normally obtain an instance from {@link ZoneRules}.
     * This constructor is intended for use by implementors of {@code ZoneRules}.
     *
     * @param month  the month of the month-day of the first day of the cutover week, not null
     * @param dayOfMonthIndicator  the day of the month-day of the cutover week, positive if the week is that
     *  day or later, negative if the week is that day or earlier, counting from the last day of the month,
     *  from -28 to 31 excluding 0
     * @param dayOfWeek  the required day-of-week, null if the month-day should not be changed
     * @param time  the cutover time in the 'before' offset, not null
     * @param timeEndOfDay  whether the time is midnight at the end of day
     * @param timeDefnition  how to interpret the cutover
     * @param standardOffset  the standard offset in force at the cutover, not null
     * @param offsetBefore  the offset before the cutover, not null
     * @param offsetAfter  the offset after the cutover, not null
     * @throws IllegalArgumentException if the day of month indicator is invalid
     * @throws IllegalArgumentException if the end of day flag is true when the time is not midnight
     */
    public static ZoneOffsetTransitionRule of(
            MonthOfYear month,
            int dayOfMonthIndicator,
            DayOfWeek dayOfWeek,
            LocalTime time,
            boolean timeEndOfDay,
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
        if (dayOfMonthIndicator < -28 || dayOfMonthIndicator > 31 || dayOfMonthIndicator == 0) {
            throw new IllegalArgumentException("Day of month indicator must be between -28 and 31 inclusive excluding zero");
        }
        if (timeEndOfDay && time.equals(LocalTime.MIDNIGHT) == false) {
            throw new IllegalArgumentException("Time must be midnight when end of day flag is true");
        }
        return new ZoneOffsetTransitionRule(month, dayOfMonthIndicator, dayOfWeek, time, timeEndOfDay, timeDefnition, standardOffset, offsetBefore, offsetAfter);
    }

    /**
     * Creates an instance defining the yearly rule to create transitions between two offsets.
     *
     * @param month  the month of the month-day of the first day of the cutover week, not null
     * @param dayOfMonthIndicator  the day of the month-day of the cutover week, positive if the week is that
     *  day or later, negative if the week is that day or earlier, counting from the last day of the month,
     *  from -28 to 31 excluding 0
     * @param dayOfWeek  the required day-of-week, null if the month-day should not be changed
     * @param time  the cutover time in the 'before' offset, not null
     * @param timeEndOfDay  whether the time is midnight at the end of day
     * @param timeDefnition  how to interpret the cutover
     * @param standardOffset  the standard offset in force at the cutover, not null
     * @param offsetBefore  the offset before the cutover, not null
     * @param offsetAfter  the offset after the cutover, not null
     * @throws IllegalArgumentException if the day of month indicator is invalid
     * @throws IllegalArgumentException if the end of day flag is true when the time is not midnight
     */
    public ZoneOffsetTransitionRule(
            MonthOfYear month,
            int dayOfMonthIndicator,
            DayOfWeek dayOfWeek,
            LocalTime time,
            boolean timeEndOfDay,
            TimeDefinition timeDefnition,
            ZoneOffset standardOffset,
            ZoneOffset offsetBefore,
            ZoneOffset offsetAfter) {
        this.month = month;
        this.dom = (byte) dayOfMonthIndicator;
        this.dow = dayOfWeek;
        this.time = time;
        this.timeEndOfDay = timeEndOfDay;
        this.timeDefinition = timeDefnition;
        this.standardOffset = standardOffset;
        this.offsetBefore = offsetBefore;
        this.offsetAfter = offsetAfter;
    }

    //-------------------------------------------------------------------------
    /**
     * Uses a serialization delegate.
     *
     * @return the replacing object, never null
     */
    private Object writeReplace() {
        return new Ser(Ser.ZOTRULE, this);
    }

    /**
     * Writes the state to the stream.
     * @param out  the output stream, not null
     * @throws IOException if an error occurs
     */
    void writeExternal(DataOutput out) throws IOException {
        final int timeSecs = (timeEndOfDay ? 86400 : time.toSecondOfDay());
        final int stdOffset = standardOffset.getAmountSeconds();
        final int beforeDiff = offsetBefore.getAmountSeconds() - stdOffset;
        final int afterDiff = offsetAfter.getAmountSeconds() - stdOffset;
        final int timeByte = (timeSecs % 3600 == 0 ? (timeEndOfDay ? 24 : time.getHourOfDay()) : 31);
        final int stdOffsetByte = (stdOffset % 900 == 0 ? stdOffset / 900 + 128 : 255);
        final int beforeByte = (beforeDiff == 0 || beforeDiff == 1800 || beforeDiff == 3600 ? beforeDiff / 1800 : 3);
        final int afterByte = (afterDiff == 0 || afterDiff == 1800 || afterDiff == 3600 ? afterDiff / 1800 : 3);
        final int dowByte = (dow == null ? 0 : dow.getValue());
        int b = (month.getValue() << 28) +          // 4 bytes
                ((dom + 32) << 22) +                // 6 bytes
                (dowByte << 19) +                   // 3 bytes
                (timeByte << 14) +                  // 5 bytes
                (timeDefinition.ordinal() << 12) +  // 2 bytes
                (stdOffsetByte << 4) +              // 8 bytes
                (beforeByte << 2) +                 // 2 bytes
                afterByte;                          // 2 bytes
        out.writeInt(b);
        if (timeByte == 31) {
            out.writeInt(timeSecs);
        }
        if (stdOffsetByte == 255) {
            out.writeInt(stdOffset);
        }
        if (beforeByte == 3) {
            out.writeInt(offsetBefore.getAmountSeconds());
        }
        if (afterByte == 3) {
            out.writeInt(offsetAfter.getAmountSeconds());
        }
    }

    /**
     * Reads the state from the stream.
     * @param in  the input stream, not null
     * @return the created object, never null
     * @throws IOException if an error occurs
     */
    static ZoneOffsetTransitionRule readExternal(DataInput in) throws IOException {
        int data = in.readInt();
        MonthOfYear month = MonthOfYear.of(data >>> 28);
        int dom = ((data & (63 << 22)) >>> 22) -32;
        int dowByte = (data & (7 << 19)) >>> 19;
        DayOfWeek dow = dowByte == 0 ? null : DayOfWeek.of(dowByte);
        int timeByte = (data & (31 << 14)) >>> 14;
        TimeDefinition defn = TimeDefinition.values()[(data & (3 << 12)) >>> 12];
        int stdByte = (data & (255 << 4)) >>> 4;
        int beforeByte = (data & (3 << 2)) >>> 2;
        int afterByte = (data & 3);
        LocalTime time = (timeByte == 31 ? LocalTime.ofSecondOfDay(in.readInt()) : LocalTime.of(timeByte % 24, 0));
        ZoneOffset std = (stdByte == 255 ? ZoneOffset.ofTotalSeconds(in.readInt()) : ZoneOffset.ofTotalSeconds((stdByte - 128) * 900));
        ZoneOffset before = (beforeByte == 3 ? ZoneOffset.ofTotalSeconds(in.readInt()) : ZoneOffset.ofTotalSeconds(std.getAmountSeconds() + beforeByte * 1800));
        ZoneOffset after = (afterByte == 3 ? ZoneOffset.ofTotalSeconds(in.readInt()) : ZoneOffset.ofTotalSeconds(std.getAmountSeconds() + afterByte * 1800));
        return ZoneOffsetTransitionRule.of(month, dom, dow, time, timeByte == 24, defn, std, before, after);
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
     * <p>
     * If the value is positive, then it represents a normal day-of-month, and is the
     * earliest possible date that the transition can be.
     * The date may refer to 29th February which should be treated as 1st March in non-leap years.
     * <p>
     * If the value is negative, then it represents the number of days back from the
     * end of the month where {@code -1} is the last day of the month.
     * In this case, the day identified is the latest possible date that the transition can be.
     *
     * @return the day-of-month indicator, from -28 to 31 excluding 0
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
     * Gets the local time of day of the transition which must be checked with
     * {@link #isMidnightEndOfDay()}.
     * <p>
     * The time is converted into an instant using the time definition.
     *
     * @return the local time of day of the transition, never null
     */
    public LocalTime getLocalTime() {
        return time;
    }

    /**
     * Is the transition local time midnight at the end of day.
     * <p>
     * The transition may be represented as occurring at 24:00.
     *
     * @return whether a local time of midnight is at the start or end of the day
     */
    public boolean isMidnightEndOfDay() {
        return timeEndOfDay;
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
     * <p>
     * Calculations are performed using the ISO-8601 chronology.
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
        if (timeEndOfDay) {
            date = date.plusDays(1);
        }
        LocalDateTime localDT = LocalDateTime.of(date, time);
        OffsetDateTime transition = timeDefinition.createDateTime(localDT, standardOffset, offsetBefore);
        return new ZoneOffsetTransition(transition, offsetAfter);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this object equals another.
     * <p>
     * The entire state of the object is compared.
     *
     * @param other  the other object to compare to, null returns false
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
                timeEndOfDay == other.timeEndOfDay &&
                standardOffset.equals(other.standardOffset) &&
                offsetBefore.equals(other.offsetBefore) &&
                offsetAfter.equals(other.offsetAfter);
        }
        return false;
    }

    /**
     * Returns a suitable hash code.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        int hash = ((time.toSecondOfDay() + (timeEndOfDay ? 1 : 0)) << 15) +
                (month.ordinal() << 11) + ((dom + 32) << 5) +
                ((dow == null ? 7 : dow.ordinal()) << 2) + (timeDefinition.ordinal());
        return hash ^ standardOffset.hashCode() ^
                offsetBefore.hashCode() ^ offsetAfter.hashCode();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a string describing this object.
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
            if (dom == -1) {
                buf.append(dow.name()).append(" on or before last day of ").append(month.name());
            } else if (dom < 0) {
                buf.append(dow.name()).append(" on or before last day minus ").append(-dom - 1).append(" of ").append(month.name());
            } else {
                buf.append(dow.name()).append(" on or after ").append(month.name()).append(' ').append(dom);
            }
        } else {
            buf.append(month.name()).append(' ').append(dom);
        }
        buf.append(" at ").append(timeEndOfDay ? "24:00" : time.toString())
            .append(" ").append(timeDefinition)
            .append(", standard offset ").append(standardOffset)
            .append(']');
        return buf.toString();
    }

}
