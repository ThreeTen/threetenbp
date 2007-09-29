/*
 * Copyright (c) 2007, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.duration;

import java.io.Serializable;
import java.util.HashMap;

import javax.time.duration.field.Days;
import javax.time.duration.field.Hours;
import javax.time.duration.field.Minutes;
import javax.time.duration.field.Months;
import javax.time.duration.field.Seconds;
import javax.time.duration.field.Years;

/**
 * An immutable duration consisting of a number of duration fields.
 * <p>
 * As an example, the duration "3 months, 4 days and 7 hours" can be stored
 * in a Duration.
 * <p>
 * Static factory methods allow you to constuct instances.
 * <p>
 * Duration is thread-safe and immutable.
 *
 * @author Stephen Colebourne
 */
public final class Duration implements Durational, Serializable {

    /**
     * A constant for a duration of zero.
     */
    public static final Duration ZERO = new Duration(new HashMap<DurationUnit, DurationField>(0));

    /**
     * A serialization identifier for this instance.
     */
    private static final long serialVersionUID = 986187548716897689L;

    /**
     * The map of duration fields.
     */
    private final HashMap<DurationUnit, DurationField> durationMap;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>Duration</code> from years, months and days.
     *
     * @param years  the years to represent
     * @param months  the months to represent
     * @param days  the days to represent
     * @return the created Duration
     */
    public static Duration yearsMonthsDays(int years, int months, int days) {
        return null;
    }

    /**
     * Obtains an instance of <code>Duration</code> from a set of durations.
     * <p>
     * This method is typically used to pass in one instance of each of the
     * duration fields in the duration. However, it is possible to pass in
     * any Durational instance and the resulting duration will be a simple
     * field addition of each.
     *
     * @param durations  a set of durations that will be added together to form the duration
     * @return the created Duration
     */
    public static Duration durationOf(Durational... durations) {
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance using a pre-built map.
     * The map must not be used by the calling code after calling the constructor.
     *
     * @param durationMap  the map of durations to represent, not null and safe to assign
     */
    private Duration(HashMap<DurationUnit, DurationField> durationMap) {
        this.durationMap = durationMap;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the durational state which provides internal access to this
     * instance.
     *
     * @return the duration state for this instance, never null
     */
    @Override
    public DurationalState getDurationalState() {
        return null;  // TODO
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the amount of the duration for the specified unit, returning
     * zero if this duration does not define the unit.
     *
     * @param unit  the unit to query, not null
     * @return the duration amount, zero if the unit is not present
     */
    public int getAmount(DurationUnit unit) {
        DurationField field = durationMap.get(unit);
        if (field == null) {
            return 0;
        }
        return field.getAmount();
    }

    /**
     * Gets the amount of the duration for the specified field, returning
     * zero if this duration does not define the field.
     *
     * @param <T>  the type of the durational field to be returned
     * @param durationType  the field to query, not null
     * @return the duration amount, returned using the specified type
     */
    public <T extends DurationField> T getAmount(Class<T> durationType) {
        return (T) durationMap.get(null);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the years field of the overall duration, if any.
     *
     * @return the years field of the overall duration
     */
    public int getYears() {
        return getAmount(Years.UNIT);
    }

    /**
     * Gets the months field of the overall duration, if any.
     *
     * @return the months field of the overall duration
     */
    public int getMonths() {
        return getAmount(Months.UNIT);
    }

    /**
     * Gets the days field of the overall duration, if any.
     *
     * @return the days field of the overall duration
     */
    public int getDays() {
        return getAmount(Days.UNIT);
    }

    /**
     * Gets the hours field of the overall duration, if any.
     *
     * @return the hours field of the overall duration
     */
    public int getHours() {
        return getAmount(Hours.UNIT);
    }

    /**
     * Gets the minutes field of the overall duration, if any.
     *
     * @return the minutes field of the overall duration
     */
    public int getMinutes() {
        return getAmount(Minutes.UNIT);
    }

    /**
     * Gets the seconds field of the overall duration, if any.
     *
     * @return the seconds field of the overall duration
     */
    public int getSeconds() {
        return getAmount(Seconds.UNIT);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this Duration with the specified values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param duration  the duration field to update, not null
     * @return a new updated Duration
     * @throws NullPointerException if the duration is null
     */
    public Duration with(Durational duration) {
        // TODO
        return null;
    }

    /**
     * Returns a copy of this Duration with the specified values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param durations  the duration fields to update, not null
     * @return a new updated Duration
     * @throws NullPointerException if any duration is null
     */
    public Duration with(Durational... durations) {
        // TODO
        return null;
    }

    /**
     * Returns a copy of this Duration with the specified years field value.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param amount  the amount to update the new instance with
     * @param unit  the unit to update, not null
     * @return a new updated Duration
     */
    @SuppressWarnings("unchecked")
    private Duration with(int amount, DurationUnit unit) {
        if (getAmount(unit) == amount) {
            return this;
        }
        HashMap<DurationUnit, DurationField> copy = (HashMap) durationMap.clone();
        if (amount == 0) {
            copy.remove(unit);
//        } else {  // TODO
//            copy.put(rule, rule.createInstance(amount));
        }
        return new Duration(copy);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this Duration with the specified years field value.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to represent
     * @return a new updated Duration
     */
    public Duration withYears(int years) {
        return with(years, Years.UNIT);
    }

    /**
     * Returns a copy of this Duration with the specified months field value.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to represent
     * @return a new updated Duration
     */
    public Duration withMonths(int months) {
        return with(months, Months.UNIT);
    }

    /**
     * Returns a copy of this Duration with the specified days field value.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to represent
     * @return a new updated Duration
     */
    public Duration withDays(int days) {
        return with(days, Days.UNIT);
    }

    /**
     * Returns a copy of this Duration with the specified days field value.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the hours to represent
     * @return a new updated Duration
     */
    public Duration withHours(int hours) {
        return with(hours, Hours.UNIT);
    }

    /**
     * Returns a copy of this Duration with the specified days field value.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutes  the minutes to represent
     * @return a new updated Duration
     */
    public Duration withMinutes(int minutes) {
        return with(minutes, Minutes.UNIT);
    }

    /**
     * Returns a copy of this Duration with the specified days field value.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds  the seconds to represent
     * @return a new updated Duration
     */
    public Duration withSeconds(int seconds) {
        return with(seconds, Seconds.UNIT);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this Duration with the specified duration added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param duration  the duration to add, not null
     * @return a new updated Duration
     * @throws NullPointerException if the duration is null
     */
    public Duration plus(Durational duration) {
        // TODO
        return null;
    }

    /**
     * Returns a copy of this Duration with the specified durations added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param durations  the durations to add, not null
     * @return a new updated Duration
     * @throws NullPointerException if any duration is null
     */
    public Duration plus(Durational... durations) {
        // TODO
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this Duration with the specified number of years added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to add
     * @return a new updated Duration
     */
    public Duration plusYears(int years) {
        return null;
    }

    /**
     * Returns a copy of this Duration with the specified number of months added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to add
     * @return a new updated Duration
     */
    public Duration plusMonths(int months) {
        return null;
    }

    /**
     * Returns a copy of this Duration with the specified number of weeks added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param weeks  the weeks to add
     * @return a new updated Duration
     */
    public Duration plusWeeks(int weeks) {
        return null;
    }

    /**
     * Returns a copy of this Duration with the specified number of days added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to add
     * @return a new updated Duration
     */
    public Duration plusDays(int days) {
        return null;
    }

    /**
     * Returns a copy of this Duration with the specified number of hours added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the hours to add
     * @return a new updated Duration
     */
    public Duration plusHours(int hours) {
        return null;
    }

    /**
     * Returns a copy of this Duration with the specified number of minutes added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutes  the minutes to add
     * @return a new updated Duration
     */
    public Duration plusMinutes(int minutes) {
        return null;
    }

    /**
     * Returns a copy of this Duration with the specified number of seconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds  the seconds to add
     * @return a new updated Duration
     */
    public Duration plusSeconds(int seconds) {
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this Duration with the specified duration subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param duration  the duration to subtract, not null
     * @return a new updated Duration
     * @throws NullPointerException if the duration is null
     */
    public Duration minus(Durational duration) {
        // TODO
        return null;
    }

    /**
     * Returns a copy of this Duration with the specified durations subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param durations  the durations to subtract, not null
     * @return a new updated Duration
     * @throws NullPointerException if any duration is null
     */
    public Duration minus(Durational... durations) {
        // TODO
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this Duration with the specified number of years subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to subtract
     * @return a new updated Duration
     */
    public Duration minusYears(int years) {
        return null;
    }

    /**
     * Returns a copy of this Duration with the specified number of months subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to subtract
     * @return a new updated Duration
     */
    public Duration minusMonths(int months) {
        return null;
    }

    /**
     * Returns a copy of this Duration with the specified number of weeks subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param weeks  the weeks to subtract
     * @return a new updated Duration
     */
    public Duration minusWeeks(int weeks) {
        return null;
    }

    /**
     * Returns a copy of this Duration with the specified number of days subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to subtract
     * @return a new updated Duration
     */
    public Duration minusDays(int days) {
        return null;
    }

    /**
     * Returns a copy of this Duration with the specified number of hours subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the hours to subtract
     * @return a new updated Duration
     */
    public Duration minusHours(int hours) {
        return null;
    }

    /**
     * Returns a copy of this Duration with the specified number of minutes subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutes  the minutes to subtract
     * @return a new updated Duration
     */
    public Duration minusMinutes(int minutes) {
        return null;
    }

    /**
     * Returns a copy of this Duration with the specified number of seconds subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds  the seconds to subtract
     * @return a new updated Duration
     */
    public Duration minusSeconds(int seconds) {
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified.
     *
     * @param other  the other point instance to compare to, null returns false
     * @return true if this point is equal to the specified second
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof Duration) {
//            Duration otherSecond = (Duration) other;
            return false;
        }
        return false;
    }

    /**
     * A suitable hashcode for this object.
     *
     * @return a suitable hashcode
     */
    @Override
    public int hashCode() {
        return getDurationalState().hashCode() + 1;
    }

}
