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

import org.threeten.bp.DateTimeException;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;

/**
 * General access to a date and/or time object that is complete enough to be manipulated.
 * <p>
 * There are two types of date-time class modeled in the API.
 * The first, {@link TemporalAccessor}, expresses the date-time only as a map of field to value.
 * The second, this interface, extends that to also support addition and subtraction.
 * <p>
 * For example, a class representing the combination of day-of-week and day-of-month,
 * suitable for storing "Friday the 13th", would implement only the former.
 * By contrast, a {@link LocalDate} or {@link LocalTime} implements this interface.
 *
 * <h4>Formal definition</h4>
 * <p>
 * Formally, a class should implement this interface if it meets three criteria:
 * <p><ul>
 * <li>it represents a map of date-time fields to values (as per {@code DateTimeAccessor})
 * <li>the set of fields are contiguous from the largest to the smallest
 * <li>the set of fields are complete, such that no other field is needed to define the
 *  valid range of values for the fields that are represented
 * </ul><p>
 * <p>
 * Four examples make this clear:
 * <p><ul>
 * <li>{@code LocalDate} should implement this interface as it represents a set of fields
 *  that are contiguous from days to forever and require no external information to determine
 *  the validity of each date. It is therefore able to implement plus/minus correctly.
 * <li>{@code LocalTime} should implement this interface as it represents a set of fields
 *  that are contiguous from nanos to within days and require no external information to determine
 *  validity. It is able to implement plus/minus correctly, by wrapping around the day.
 * <li>The combination of month-of-year and day-of-month should not implement this interface.
 *  While the combination is contiguous, from days to months within years, the combination does
 *  not have sufficient information to define the valid range of values for day-of-month.
 *  As such, it is unable to implement plus/minus correctly.
 * <li>The combination day-of-week and day-of-month ("Friday the 13th") should not implement
 *  this interface. It does not represent a contiguous set of fields, as days to weeks overlaps
 *  days to months.
 * </ul><p>
 *
 * <h4>Implementation notes</h4>
 * This interface places no restrictions on implementations and makes no guarantees
 * about their thread-safety.
 * All implementations must be {@link Comparable}.
 */
public interface Temporal extends TemporalAccessor {

    /**
     * Returns an adjusted object of the same type as this object with the adjustment made.
     * <p>
     * This adjusts this date-time according to the rules of the specified adjuster.
     * A simple adjuster might simply set the one of the fields, such as the year field.
     * A more complex adjuster might set the date to the last day of the month.
     * A selection of common adjustments is provided in {@link TemporalAdjusters}.
     * These include finding the "last day of the month" and "next Wednesday".
     * The adjuster is responsible for handling special cases, such as the varying
     * lengths of month and leap years.
     * <p>
     * Some example code indicating how and why this method is used:
     * <pre>
     *  date = date.with(Month.JULY);        // most key classes implement TemporalAdjuster
     *  date = date.with(lastDayOfMonth());  // static import from DateTimeAdjusters
     *  date = date.with(next(WEDNESDAY));   // static import from DateTimeAdjusters and DayOfWeek
     * </pre>
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param adjuster the adjuster to use, not null
     * @return an object of the same type with the specified adjustment made, not null
     * @throws DateTimeException if the adjustment cannot be made
     * @throws ArithmeticException if numeric overflow occurs
     */
    Temporal with(TemporalAdjuster adjuster);

    /**
     * Returns an object of the same type as this object with the specified field altered.
     * <p>
     * This returns a new object based on this one with the value for the specified field changed.
     * For example, on a {@code LocalDate}, this could be used to set the year, month or day-of-month.
     * The returned object will have the same observable type as this object.
     * <p>
     * In some cases, changing a field is not fully defined. For example, if the target object is
     * a date representing the 31st January, then changing the month to February would be unclear.
     * In cases like this, the field is responsible for resolving the result. Typically it will choose
     * the previous valid date, which would be the last valid day of February in this example.
     *
     * <h5>Implementation notes</h5>
     * Implementations must check and handle any fields defined in {@link ChronoField} before
     * delegating on to the {@link TemporalField#doWith(Temporal, long) doWith method} on the specified field.
     * If the implementing class is immutable, then this method must return an updated copy of the original.
     * If the class is mutable, then this method must update the original and return it.
     *
     * @param field  the field to set in the result, not null
     * @param newValue  the new value of the field in the result
     * @return an object of the same type with the specified field set, not null
     * @throws DateTimeException if the field cannot be set
     * @throws ArithmeticException if numeric overflow occurs
     */
    Temporal with(TemporalField field, long newValue);

    //-----------------------------------------------------------------------
    /**
     * Returns an adjusted object of the same type as this object with the adjustment added.
     * <p>
     * This adjusts this date-time, adding according to the rules of the specified adjuster.
     * The adjuster is typically a {@link org.threeten.bp.Period} but may be any other type implementing
     * the {@link TemporalAdder} interface, such as {@link org.threeten.bp.Duration}.
     * <p>
     * Some example code indicating how and why this method is used:
     * <pre>
     *  date = date.plus(period);                      // add a Period instance
     *  date = date.plus(duration);                    // add a Duration instance
     *  date = date.plus(MONTHS.between(start, end));  // static import of MONTHS field
     *  date = date.plus(workingDays(6));              // example user-written workingDays method
     * </pre>
     * <p>
     * Note that calling {@code plus} followed by {@code minus} is not guaranteed to
     * return the same date-time.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param adjuster  the adjuster to use, not null
     * @return an object of the same type with the specified adjustment made, not null
     * @throws DateTimeException if the addition cannot be made
     * @throws ArithmeticException if numeric overflow occurs
     */
    Temporal plus(TemporalAdder adjuster);

    /**
     * Returns an object of the same type as this object with the specified period added.
     * <p>
     * This method returns a new object based on this one with the specified period added.
     * For example, on a {@code LocalDate}, this could be used to add a number of years, months or days.
     * The returned object will have the same observable type as this object.
     * <p>
     * In some cases, changing a field is not fully defined. For example, if the target object is
     * a date representing the 31st January, then adding one month would be unclear.
     * In cases like this, the field is responsible for resolving the result. Typically it will choose
     * the previous valid date, which would be the last valid day of February in this example.
     * <p>
     * If the implementation represents a date-time that has boundaries, such as {@code LocalTime},
     * then the permitted units must include the boundary unit, but no multiples of the boundary unit.
     * For example, {@code LocalTime} must accept {@code DAYS} but not {@code WEEKS} or {@code MONTHS}.
     *
     * <h5>Implementation notes</h5>
     * Implementations must check and handle any fields defined in {@link ChronoField} before
     * delegating on to the {@link TemporalUnit#doPlus(Temporal, long) doPlus method} on the specified unit.
     * If the implementing class is immutable, then this method must return an updated copy of the original.
     * If the class is mutable, then this method must update the original and return it.
     *
     * @param amountToAdd  the amount of the specified unit to add, may be negative
     * @param unit  the unit of the period to add, not null
     * @return an object of the same type with the specified period added, not null
     * @throws DateTimeException if the unit cannot be added
     * @throws ArithmeticException if numeric overflow occurs
     */
    Temporal plus(long amountToAdd, TemporalUnit unit);

    //-----------------------------------------------------------------------
    /**
     * Returns an adjusted object of the same type as this object with the adjustment subtracted.
     * <p>
     * This adjusts this date-time, subtracting according to the rules of the specified adjuster.
     * The adjuster is typically a {@link org.threeten.bp.Period} but may be any other type implementing
     * the {@link TemporalSubtractor} interface, such as {@link org.threeten.bp.Duration}.
     * <p>
     * Some example code indicating how and why this method is used:
     * <pre>
     *  date = date.minus(period);                      // subtract a Period instance
     *  date = date.minus(duration);                    // subtract a Duration instance
     *  date = date.minus(MONTHS.between(start, end));  // static import of MONTHS field
     *  date = date.minus(workingDays(6));              // example user-written workingDays method
     * </pre>
     * <p>
     * Note that calling {@code plus} followed by {@code minus} is not guaranteed to
     * return the same date-time.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param adjuster  the adjuster to use, not null
     * @return an object of the same type with the specified adjustment made, not null
     * @throws DateTimeException if the subtraction cannot be made
     * @throws ArithmeticException if numeric overflow occurs
     */
    Temporal minus(TemporalSubtractor adjuster);

    /**
     * Returns an object of the same type as this object with the specified period subtracted.
     * <p>
     * This method returns a new object based on this one with the specified period subtracted.
     * For example, on a {@code LocalDate}, this could be used to subtract a number of years, months or days.
     * The returned object will have the same observable type as this object.
     * <p>
     * In some cases, changing a field is not fully defined. For example, if the target object is
     * a date representing the 31st March, then subtracting one month would be unclear.
     * In cases like this, the field is responsible for resolving the result. Typically it will choose
     * the previous valid date, which would be the last valid day of February in this example.
     * <p>
     * If the implementation represents a date-time that has boundaries, such as {@code LocalTime},
     * then the permitted units must include the boundary unit, but no multiples of the boundary unit.
     * For example, {@code LocalTime} must accept {@code DAYS} but not {@code WEEKS} or {@code MONTHS}.
     *
     * <h5>Implementation notes</h5>
     * Implementations must check and handle any fields defined in {@link ChronoField} before
     * delegating on to the {@link TemporalUnit#doPlus(Temporal, long) doPlus method} on the specified unit.
     * If the implementing class is immutable, then this method must return an updated copy of the original.
     * If the class is mutable, then this method must update the original and return it.
     * This method is normally implemented by delegating to {@link #plus(long, TemporalUnit)} with
     * the amount negated.
     *
     * @param amountToSubtract  the amount of the specified unit to subtract, may be negative
     * @param unit  the unit of the period to subtract, not null
     * @return an object of the same type with the specified period subtracted, not null
     * @throws DateTimeException if the unit cannot be subtracted
     * @throws ArithmeticException if numeric overflow occurs
     */
    Temporal minus(long amountToSubtract, TemporalUnit unit);

    //-----------------------------------------------------------------------
    /**
     * Calculates the period from this date-time until the given date-time in the specified unit.
     * <p>
     * This is used to calculate the period between two date-times.
     * This method operates in association with {@link TemporalUnit#between}.
     * That method returns an object which can be used directly in addition/subtraction
     * whereas this method returns the amount directly:
     * <pre>
     *   long period = start.periodUntil(end, MONTHS);          // this method
     *   long period = MONTHS.between(start, end).getAmount();  // same as above
     *   dateTime.plus(MONTHS.between(start, end));             // directly add
     * </pre>
     *
     * @param endDateTime  the end date-time, of the same type as this object, not null
     * @param unit  the unit to measure the period in, not null
     * @return the amount of the period between this and the end
     * @throws DateTimeException if the period cannot be calculated
     * @throws ArithmeticException if numeric overflow occurs
     */
    long periodUntil(Temporal endDateTime, TemporalUnit unit);

}
