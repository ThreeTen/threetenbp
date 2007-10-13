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
package javax.time.period;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.time.MathUtils;
import javax.time.period.field.Days;
import javax.time.period.field.Hours;
import javax.time.period.field.Minutes;
import javax.time.period.field.Months;
import javax.time.period.field.Seconds;
import javax.time.period.field.Weeks;
import javax.time.period.field.Years;

/**
 * An immutable period consisting of a number of period fields.
 * <p>
 * As an example, the period "3 months, 4 days and 7 hours" can be stored
 * in a Period.
 * <p>
 * Static factory methods allow you to constuct instances.
 * <p>
 * Period is thread-safe and immutable.
 *
 * @author Stephen Colebourne
 */
public final class Period implements PeriodView, Serializable {

    /**
     * A constant for a period of zero.
     */
    public static final Period ZERO = new Period(new TreeMap<PeriodUnit, Integer>());

    /**
     * A serialization identifier for this instance.
     */
    private static final long serialVersionUID = 986187548716897689L;

    /**
     * The map of period fields.
     */
    private final TreeMap<PeriodUnit, Integer> periodMap;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>Period</code> from a set of periods.
     * <p>
     * This method is typically used to pass in one instance of each of the
     * period fields in the period. However, it is possible to pass in
     * any PeriodView instance and the resulting period will be a simple
     * field addition of each.
     *
     * @param periodMap  a map of periods that will be used to create this
     *  period, not null, contains no nulls, may be immutable
     * @return the created Period, never null
     * @throws NullPointerException if the map is null or contains nulls
     */
    public static Period periodOf(Map<PeriodUnit, Integer> periodMap) {
        if (periodMap == null) {
            throw new NullPointerException("Period map must not be null");
        }
        if (periodMap.containsKey(null) || periodMap.containsValue(null)) {
            throw new NullPointerException("Period map must not contain null");
        }
        TreeMap<PeriodUnit, Integer> internalMap = new TreeMap<PeriodUnit, Integer>(Collections.reverseOrder());
        internalMap.putAll(periodMap);
        return new Period(internalMap);
    }

    /**
     * Obtains an instance of <code>Period</code> from a set of periods.
     * <p>
     * This method is typically used to pass in one instance of each of the
     * period fields in the period. However, it is possible to pass in
     * any PeriodView instance and the resulting period will be a simple
     * field addition of each.
     *
     * @param periods  a set of periods that will be added together to form the period
     * @return the created Period
     */
    public static Period periodOf(PeriodView... periods) {
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance using a pre-built map.
     * The map must not be used by the calling code after calling the constructor.
     *
     * @param periodMap  the map of periods to represent, not null and safe to assign
     */
    private Period(TreeMap<PeriodUnit, Integer> periodMap) {
        this.periodMap = periodMap;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks whether a given unit is supported -
     * <code>Period</code> supports all units.
     *
     * @param unit  the unit to check for, null returns false
     * @return true, unless unit specified was null
     */
    public boolean isSupported(PeriodUnit unit)  {
        return (unit != null);
    }

    /**
     * Gets the map of period unit to amount which defines the period.
     * The map iterators are sorted by period unit, returning the largest first.
     *
     * @return the map of period amounts, never null, never contains null
     */
    public Map<PeriodUnit, Integer> getPeriodViewMap() {
        return Collections.unmodifiableMap(periodMap);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the amount of the period for the specified unit, returning
     * zero if this period does not define the unit.
     *
     * @param unit  the unit to query, not null
     * @return the period amount, zero if the unit is not present
     */
    public Integer getAmount(PeriodUnit unit) {
        Integer amount = periodMap.get(unit);
        if (amount == null) {
            return Integer.valueOf(0);
        }
        return amount;
    }

//    /**
//     * Extracts the fields from this period into another.
//     *
//     * @param <T>  the type of the period instance to be returned
//     * @param periodType  the period type, not null
//     * @return the period amount, returned using the specified type
//     */
//    public <T extends PeriodField> T get(PeriodViewType<T> periodType) {
//        return periodType.extractFrom(this);
//    }

    //-----------------------------------------------------------------------
    /**
     * Gets the years field of the overall period, if any.
     *
     * @return the years field of the overall period
     */
    public int getYears() {
        return getAmount(Years.UNIT);
    }

    /**
     * Gets the months field of the overall period, if any.
     *
     * @return the months field of the overall period
     */
    public int getMonths() {
        return getAmount(Months.UNIT);
    }

    /**
     * Gets the days field of the overall period, if any.
     *
     * @return the days field of the overall period
     */
    public int getDays() {
        return getAmount(Days.UNIT);
    }

    /**
     * Gets the hours field of the overall period, if any.
     *
     * @return the hours field of the overall period
     */
    public int getHours() {
        return getAmount(Hours.UNIT);
    }

    /**
     * Gets the minutes field of the overall period, if any.
     *
     * @return the minutes field of the overall period
     */
    public int getMinutes() {
        return getAmount(Minutes.UNIT);
    }

    /**
     * Gets the seconds field of the overall period, if any.
     *
     * @return the seconds field of the overall period
     */
    public int getSeconds() {
        return getAmount(Seconds.UNIT);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this Period with the specified values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param period  the period field to update, not null
     * @return a new updated Period
     * @throws NullPointerException if the period is null
     */
    public Period with(PeriodView period) {
        Map<PeriodUnit, Integer> periodViewMap = period.getPeriodViewMap();
        if (periodViewMap.isEmpty()) {
            return this;
        }
        TreeMap<PeriodUnit, Integer> copy = cloneMap();
        for (PeriodUnit unit : periodViewMap.keySet()) {
            Integer amount = periodViewMap.get(unit);
            if (amount == 0) {
                copy.remove(unit);
            } else {
                copy.put(unit, amount);
            }
        }
        return new Period(copy);
    }

    /**
     * Returns a copy of this Period with the specified values altered.
     * The list of periods must contain no duplicate units.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periods  the period fields to update, not null
     * @return a new updated Period
     * @throws NullPointerException if any period is null
     * @throws IllegalArgumentException if any unit is duplicated
     */
    public Period with(PeriodView... periods) {
        if (periods.length == 0) {
            return this;
        }
        TreeMap<PeriodUnit, Integer> copy = null;
        Set<PeriodUnit> set = new HashSet<PeriodUnit>();
        for (PeriodView periodView : periods) {
            Map<PeriodUnit, Integer> periodViewMap = periodView.getPeriodViewMap();
            if (periodViewMap.size() > 0) {
                if (copy == null) {
                    copy = cloneMap();
                }
                for (PeriodUnit unit : periodViewMap.keySet()) {
                    if (!set.add(unit)) {
                        throw new IllegalArgumentException("Input PeriodView array contains duplicate unit " + unit.getName());
                    }
                    Integer amount = periodViewMap.get(unit);
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
        return new Period(copy);
    }

    /**
     * Returns a copy of this Period with the specified years field value.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param amount  the amount to update the new instance with
     * @param unit  the unit to update, not null
     * @return a new updated Period
     */
    private Period with(int amount, PeriodUnit unit) {
        if (getAmount(unit) == amount) {
            return this;
        }
        TreeMap<PeriodUnit, Integer> copy = cloneMap();
        if (amount == 0) {
            copy.remove(unit);
        } else {
            copy.put(unit, amount);
        }
        return new Period(copy);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this Period with the specified years field value.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to represent
     * @return a new updated Period
     */
    public Period withYears(int years) {
        return with(years, Years.UNIT);
    }

    /**
     * Returns a copy of this Period with the specified months field value.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to represent
     * @return a new updated Period
     */
    public Period withMonths(int months) {
        return with(months, Months.UNIT);
    }

    /**
     * Returns a copy of this Period with the specified days field value.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to represent
     * @return a new updated Period
     */
    public Period withDays(int days) {
        return with(days, Days.UNIT);
    }

    /**
     * Returns a copy of this Period with the specified days field value.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the hours to represent
     * @return a new updated Period
     */
    public Period withHours(int hours) {
        return with(hours, Hours.UNIT);
    }

    /**
     * Returns a copy of this Period with the specified days field value.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutes  the minutes to represent
     * @return a new updated Period
     */
    public Period withMinutes(int minutes) {
        return with(minutes, Minutes.UNIT);
    }

    /**
     * Returns a copy of this Period with the specified days field value.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds  the seconds to represent
     * @return a new updated Period
     */
    public Period withSeconds(int seconds) {
        return with(seconds, Seconds.UNIT);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this Period with the specified period added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param period  the period to add, not null
     * @return a new updated Period
     * @throws NullPointerException if the period is null
     */
    public Period plus(PeriodView period) {
        Map<PeriodUnit, Integer> periodViewMap = period.getPeriodViewMap();
        if (periodViewMap.isEmpty()) {
            return this;
        }
        TreeMap<PeriodUnit, Integer> copy = cloneMap();
        for (PeriodUnit unit : periodViewMap.keySet()) {
            int amount = periodViewMap.get(unit);
            int current = copy.get(unit);
            copy.put(unit, MathUtils.safeAdd(amount, current));
        }
        return new Period(copy);
    }

    /**
     * Returns a copy of this Period with the specified periods added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periods  the periods to add, not null
     * @return a new updated Period
     * @throws NullPointerException if any period is null
     */
    public Period plus(PeriodView... periods) {
        // TODO
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this Period with the specified number of years added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to add, positive or negative
     * @return a new updated Period
     */
    public Period plusYears(int years) {
        int current = getAmount(Years.UNIT);
        return with(MathUtils.safeAdd(years, current), Years.UNIT);
    }

    /**
     * Returns a copy of this Period with the specified number of months added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to add, positive or negative
     * @return a new updated Period
     */
    public Period plusMonths(int months) {
        int current = getAmount(Months.UNIT);
        return with(MathUtils.safeAdd(months, current), Months.UNIT);
    }

    /**
     * Returns a copy of this Period with the specified number of weeks added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param weeks  the weeks to add, positive or negative
     * @return a new updated Period
     */
    public Period plusWeeks(int weeks) {
        int current = getAmount(Weeks.UNIT);
        return with(MathUtils.safeAdd(weeks, current), Weeks.UNIT);
    }

    /**
     * Returns a copy of this Period with the specified number of days added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to add, positive or negative
     * @return a new updated Period
     */
    public Period plusDays(int days) {
        int current = getAmount(Days.UNIT);
        return with(MathUtils.safeAdd(days, current), Days.UNIT);
    }

    /**
     * Returns a copy of this Period with the specified number of hours added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the hours to add, positive or negative
     * @return a new updated Period
     */
    public Period plusHours(int hours) {
        int current = getAmount(Hours.UNIT);
        return with(MathUtils.safeAdd(hours, current), Hours.UNIT);
    }

    /**
     * Returns a copy of this Period with the specified number of minutes added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutes  the minutes to add, positive or negative
     * @return a new updated Period
     */
    public Period plusMinutes(int minutes) {
        int current = getAmount(Minutes.UNIT);
        return with(MathUtils.safeAdd(minutes, current), Minutes.UNIT);
    }

    /**
     * Returns a copy of this Period with the specified number of seconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds  the seconds to add, positive or negative
     * @return a new updated Period
     */
    public Period plusSeconds(int seconds) {
        int current = getAmount(Seconds.UNIT);
        return with(MathUtils.safeAdd(seconds, current), Seconds.UNIT);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this Period with the specified period subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param period  the period to subtract, not null
     * @return a new updated Period
     * @throws NullPointerException if the period is null
     */
    public Period minus(PeriodView period) {
        // TODO
        return null;
    }

    /**
     * Returns a copy of this Period with the specified periods subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periods  the periods to subtract, not null
     * @return a new updated Period
     * @throws NullPointerException if any period is null
     */
    public Period minus(PeriodView... periods) {
        // TODO
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this Period with the specified number of years subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to subtract
     * @return a new updated Period
     */
    public Period minusYears(int years) {
        return null;
    }

    /**
     * Returns a copy of this Period with the specified number of months subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to subtract
     * @return a new updated Period
     */
    public Period minusMonths(int months) {
        return null;
    }

    /**
     * Returns a copy of this Period with the specified number of weeks subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param weeks  the weeks to subtract
     * @return a new updated Period
     */
    public Period minusWeeks(int weeks) {
        return null;
    }

    /**
     * Returns a copy of this Period with the specified number of days subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to subtract
     * @return a new updated Period
     */
    public Period minusDays(int days) {
        return null;
    }

    /**
     * Returns a copy of this Period with the specified number of hours subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the hours to subtract
     * @return a new updated Period
     */
    public Period minusHours(int hours) {
        return null;
    }

    /**
     * Returns a copy of this Period with the specified number of minutes subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutes  the minutes to subtract
     * @return a new updated Period
     */
    public Period minusMinutes(int minutes) {
        return null;
    }

    /**
     * Returns a copy of this Period with the specified number of seconds subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds  the seconds to subtract
     * @return a new updated Period
     */
    public Period minusSeconds(int seconds) {
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a new instance with each element in this period multiplied
     * by the specified scalar.
     *
     * @param scalar  the scalar to multiply by, not null
     * @return the new updated period instance, never null
     * @throws ArithmeticException if the calculation result overflows
     */
    public Period multipliedBy(int scalar) {
        if (periodMap.isEmpty()) {
            return this;
        }
        TreeMap<PeriodUnit, Integer> copy = cloneMap();
        for (PeriodUnit unit : periodMap.keySet()) {
            int amount = periodMap.get(unit);
            copy.put(unit, MathUtils.safeMultiply(amount, scalar));
        }
        return new Period(copy);
    }

    /**
     * Returns a new instance with each element in this period multiplied
     * by the specified scalar.
     *
     * @param scalar  the scalar to multiply by, not null
     * @return the new updated period instance, never null
     * @throws ArithmeticException if the calculation result overflows
     */
    public Period dividedBy(int scalar) {
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Clone the internal data storage map.
     *
     * @return the cloned map, never null
     */
    @SuppressWarnings("unchecked")
    private TreeMap<PeriodUnit, Integer> cloneMap() {
        return (TreeMap) periodMap.clone();
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified, as defined by <code>PeriodView</code>.
     *
     * @param other  the other point instance to compare to, null returns false
     * @return true if this point is equal to the specified second
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof PeriodView) {
            PeriodView otherDuraton = (PeriodView) other;
            return getPeriodViewMap().equals(otherDuraton);
        }
        return false;
    }

    /**
     * Returns the hash code for this period.
     *
     * @return the hash code defined by <code>PeriodView</code>
     */
    @Override
    public int hashCode() {
        return getPeriodViewMap().hashCode();
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
