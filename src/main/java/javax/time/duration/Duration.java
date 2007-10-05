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
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.time.MathUtils;
import javax.time.duration.field.Days;
import javax.time.duration.field.Hours;
import javax.time.duration.field.Minutes;
import javax.time.duration.field.Months;
import javax.time.duration.field.Seconds;
import javax.time.duration.field.Weeks;
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
    public static final Duration ZERO = new Duration(new TreeMap<DurationUnit, Integer>());

    /**
     * A serialization identifier for this instance.
     */
    private static final long serialVersionUID = 986187548716897689L;

    /**
     * The map of duration fields.
     */
    private final TreeMap<DurationUnit, Integer> durationMap;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>Duration</code> from a set of durations.
     * <p>
     * This method is typically used to pass in one instance of each of the
     * duration fields in the duration. However, it is possible to pass in
     * any Durational instance and the resulting duration will be a simple
     * field addition of each.
     *
     * @param durationMap  a map of durations that will be used to create this
     *  duration, not null, contains no nulls, may be immutable
     * @return the created Duration, never null
     * @throws NullPointerException if the map is null or contains nulls
     */
    public static Duration durationOf(Map<DurationUnit, Integer> durationMap) {
        if (durationMap == null) {
            throw new NullPointerException("Duration map must not be null");
        }
        if (durationMap.containsKey(null) || durationMap.containsValue(null)) {
            throw new NullPointerException("Duration map must not contain null");
        }
        TreeMap<DurationUnit, Integer> internalMap = new TreeMap<DurationUnit, Integer>(Collections.reverseOrder());
        internalMap.putAll(durationMap);
        return new Duration(internalMap);
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
    private Duration(TreeMap<DurationUnit, Integer> durationMap) {
        this.durationMap = durationMap;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks whether a given unit is supported -
     * <code>Duration</code> supports all units.
     *
     * @param unit  the unit to check for, null returns false
     * @return true, unless unit specified was null
     */
    public boolean isSupported(DurationUnit unit)  {
        return (unit != null);
    }

    /**
     * Gets the map of duration unit to amount which defines the duration.
     * The map iterators are sorted by duration unit, returning the largest first.
     *
     * @return the map of duration amounts, never null, never contains null
     */
    public Map<DurationUnit, Integer> getDurationalMap() {
        return Collections.unmodifiableMap(durationMap);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the amount of the duration for the specified unit, returning
     * zero if this duration does not define the unit.
     *
     * @param unit  the unit to query, not null
     * @return the duration amount, zero if the unit is not present
     */
    public Integer getAmount(DurationUnit unit) {
        Integer amount = durationMap.get(unit);
        if (amount == null) {
            return Integer.valueOf(0);
        }
        return amount;
    }

//    /**
//     * Extracts the fields from this duration into another.
//     *
//     * @param <T>  the type of the durational instance to be returned
//     * @param durationType  the duration type, not null
//     * @return the duration amount, returned using the specified type
//     */
//    public <T extends DurationField> T get(DurationalType<T> durationType) {
//        return durationType.extractFrom(this);
//    }

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
        Map<DurationUnit, Integer> durationalMap = duration.getDurationalMap();
        if (durationalMap.isEmpty()) {
            return this;
        }
        TreeMap<DurationUnit, Integer> copy = cloneMap();
        for (DurationUnit unit : durationalMap.keySet()) {
            Integer amount = durationalMap.get(unit);
            if (amount == 0) {
                copy.remove(unit);
            } else {
                copy.put(unit, amount);
            }
        }
        return new Duration(copy);
    }

    /**
     * Returns a copy of this Duration with the specified values altered.
     * The list of durations must contain no duplicate units.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param durations  the duration fields to update, not null
     * @return a new updated Duration
     * @throws NullPointerException if any duration is null
     * @throws IllegalArgumentException if any unit is duplicated
     */
    public Duration with(Durational... durations) {
        if (durations.length == 0) {
            return this;
        }
        TreeMap<DurationUnit, Integer> copy = null;
        Set<DurationUnit> set = new HashSet<DurationUnit>();
        for (Durational durational : durations) {
            Map<DurationUnit, Integer> durationalMap = durational.getDurationalMap();
            if (durationalMap.size() > 0) {
                if (copy == null) {
                    copy = cloneMap();
                }
                for (DurationUnit unit : durationalMap.keySet()) {
                    if (!set.add(unit)) {
                        throw new IllegalArgumentException("Input Durational array contains duplicate unit " + unit.getName());
                    }
                    Integer amount = durationalMap.get(unit);
                    if (amount == 0) {
                        copy.remove(unit);
                    } else {
                        copy.put(unit, amount);
                    }
                }
            }
        }
        if (copy == null) {
            return this;
        }
        return new Duration(copy);
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
    private Duration with(int amount, DurationUnit unit) {
        if (getAmount(unit) == amount) {
            return this;
        }
        TreeMap<DurationUnit, Integer> copy = cloneMap();
        if (amount == 0) {
            copy.remove(unit);
        } else {
            copy.put(unit, amount);
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
        Map<DurationUnit, Integer> durationalMap = duration.getDurationalMap();
        if (durationalMap.isEmpty()) {
            return this;
        }
        TreeMap<DurationUnit, Integer> copy = cloneMap();
        for (DurationUnit unit : durationalMap.keySet()) {
            int amount = durationalMap.get(unit);
            int current = copy.get(unit);
            copy.put(unit, MathUtils.safeAdd(amount, current));
        }
        return new Duration(copy);
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
     * @param years  the years to add, positive or negative
     * @return a new updated Duration
     */
    public Duration plusYears(int years) {
        int current = getAmount(Years.UNIT);
        return with(MathUtils.safeAdd(years, current), Years.UNIT);
    }

    /**
     * Returns a copy of this Duration with the specified number of months added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to add, positive or negative
     * @return a new updated Duration
     */
    public Duration plusMonths(int months) {
        int current = getAmount(Months.UNIT);
        return with(MathUtils.safeAdd(months, current), Months.UNIT);
    }

    /**
     * Returns a copy of this Duration with the specified number of weeks added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param weeks  the weeks to add, positive or negative
     * @return a new updated Duration
     */
    public Duration plusWeeks(int weeks) {
        int current = getAmount(Weeks.UNIT);
        return with(MathUtils.safeAdd(weeks, current), Weeks.UNIT);
    }

    /**
     * Returns a copy of this Duration with the specified number of days added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to add, positive or negative
     * @return a new updated Duration
     */
    public Duration plusDays(int days) {
        int current = getAmount(Days.UNIT);
        return with(MathUtils.safeAdd(days, current), Days.UNIT);
    }

    /**
     * Returns a copy of this Duration with the specified number of hours added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the hours to add, positive or negative
     * @return a new updated Duration
     */
    public Duration plusHours(int hours) {
        int current = getAmount(Hours.UNIT);
        return with(MathUtils.safeAdd(hours, current), Hours.UNIT);
    }

    /**
     * Returns a copy of this Duration with the specified number of minutes added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutes  the minutes to add, positive or negative
     * @return a new updated Duration
     */
    public Duration plusMinutes(int minutes) {
        int current = getAmount(Minutes.UNIT);
        return with(MathUtils.safeAdd(minutes, current), Minutes.UNIT);
    }

    /**
     * Returns a copy of this Duration with the specified number of seconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds  the seconds to add, positive or negative
     * @return a new updated Duration
     */
    public Duration plusSeconds(int seconds) {
        int current = getAmount(Seconds.UNIT);
        return with(MathUtils.safeAdd(seconds, current), Seconds.UNIT);
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
     * Returns a new instance with each element in this duration multiplied
     * by the specified scalar.
     *
     * @param scalar  the scalar to multiply by, not null
     * @return the new updated durational instance, never null
     * @throws ArithmeticException if the calculation result overflows
     */
    public Duration multipliedBy(int scalar) {
        if (durationMap.isEmpty()) {
            return this;
        }
        TreeMap<DurationUnit, Integer> copy = cloneMap();
        for (DurationUnit unit : durationMap.keySet()) {
            int amount = durationMap.get(unit);
            copy.put(unit, MathUtils.safeMultiply(amount, scalar));
        }
        return new Duration(copy);
    }

    /**
     * Returns a new instance with each element in this duration multiplied
     * by the specified scalar.
     *
     * @param scalar  the scalar to multiply by, not null
     * @return the new updated durational instance, never null
     * @throws ArithmeticException if the calculation result overflows
     */
    public Duration dividedBy(int scalar) {
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Clone the internal data storage map.
     *
     * @return the cloned map, never null
     */
    @SuppressWarnings("unchecked")
    private TreeMap<DurationUnit, Integer> cloneMap() {
        return (TreeMap) durationMap.clone();
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified, as defined by <code>Durational</code>.
     *
     * @param other  the other point instance to compare to, null returns false
     * @return true if this point is equal to the specified second
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof Durational) {
            Durational otherDuraton = (Durational) other;
            return getDurationalMap().equals(otherDuraton);
        }
        return false;
    }

    /**
     * Returns the hash code for this duration.
     *
     * @return the hash code defined by <code>Durational</code>
     */
    @Override
    public int hashCode() {
        return getDurationalMap().hashCode();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a string representation of the amount of time.
     *
     * @return the amount of time in ISO8601 string format
     */
    @Override
    public String toString() {
        return null;
    }

}
