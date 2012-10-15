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
package javax.time.calendrical;

import javax.time.DateTimeException;
import javax.time.LocalDate;
import javax.time.LocalTime;

/**
 * General access to a date and/or time object that is complete enough to be manipulated.
 * <p>
 * There are two types of date-time class modeled in the API.
 * The first, {@link DateTimeAccessor}, expresses the date-time only as a map of field to value.
 * The second, this interface, extends that to also support addition and subtraction.
 * <p>
 * For example, a class representing the combination of day-of-week and day-of-month,
 * suitable for storing "Friday the 13th", would implement only the former.
 * By contrast, a {@link LocalDate} or {@link LocalTime} implements this interface.
 * 
 * <h4>Formal definition</h4>
 * <p>
 * Formally, a class should implement this interface if it meets three criteria:
 * <ul>
 * <li>it represents a map of date-time fields to values (as per {@code DateTimeAccessor})
 * <li>the set of fields are contiguous from the largest to the smallest
 * <li>the set of fields are complete, such that no other field is needed to define the
 *  valid range of values for the fields that are represented
 * </ul>
 * <p>
 * Four examples make this clear:
 * <ul>
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
 * </ul>
 * 
 * <h4>Implementation notes</h4>
 * This interface places no restrictions on implementations and makes no guarantees
 * about their thread-safety.
 * All implementations must be {@link Comparable}.
 */
public interface DateTime extends DateTimeAccessor {

    //-----------------------------------------------------------------------
    /**
     * Returns an adjusted object of the same type as this object with the adjustment made.
     * <p>
     * This adjusts this date-time according to the rules of the specified adjuster.
     * A simple adjuster might simply set the one of the fields, such as the year field.
     * A more complex adjuster might set the date to the last day of the month.
     * A selection of common adjustments is provided in {@link DateTimeAdjusters}.
     * These include finding the "last day of the month" and "next Wednesday".
     * The adjuster is responsible for handling special cases, such as the varying
     * lengths of month and leap years.
     * <p>
     * Some example code indicating how and why this method is used:
     * <pre>
     *  date = date.with(Month.JULY);        // most key classes implement WithAdjuster
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
    DateTime with(WithAdjuster adjuster);
    // JAVA8
    // default {
    //     return adjuster.doAdjustment(this);
    // }

    // override to restrict return type
    @Override
    DateTime with(DateTimeField field, long newValue);

    //-----------------------------------------------------------------------
    /**
     * Returns an adjusted object of the same type as this object with the adjustment added.
     * <p>
     * This adjusts this date-time, adding according to the rules of the specified adjuster.
     * The adjuster is typically a {@link Period} but may be any other type implementing
     * the {@link PlusAdjuster} interface, such as {@link Duration}.
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
    DateTime plus(PlusAdjuster adjuster);
    // JAVA8
    // default {
    //     return adjuster.doAdd(this);
    // }

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
     * <h4>Implementation notes</h4>
     * Implementations must check and handle any fields defined in {@link LocalDateTimeField} before
     * delegating on to the {@link PeriodUnit#doAdd(DateTime, long) doAdd method} on the specified unit.
     * If the implementing class is immutable, then this method must return an updated copy of the original.
     * If the class is mutable, then this method must update the original and return it.
     *
     * @param periodAmount  the amount of the specified unit to add, not null
     * @param unit  the unit of the period to add, not null
     * @return an object of the same type with the specified period added, not null
     * @throws DateTimeException if the unit cannot be added
     * @throws ArithmeticException if numeric overflow occurs
     */
    DateTime plus(long periodAmount, PeriodUnit unit);

    //-----------------------------------------------------------------------
    /**
     * Returns an adjusted object of the same type as this object with the adjustment subtracted.
     * <p>
     * This adjusts this date-time, subtracting according to the rules of the specified adjuster.
     * The adjuster is typically a {@link Period} but may be any other type implementing
     * the {@link MinusAdjuster} interface, such as {@link Duration}.
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
    DateTime minus(MinusAdjuster adjuster);
    // JAVA8
    // default {
    //     return adjuster.doAdd(this);
    // }

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
     * <h4>Implementation notes</h4>
     * Implementations must check and handle any fields defined in {@link LocalDateTimeField} before
     * delegating on to the {@link PeriodUnit#doAdd(DateTime, long) doAdd method} on the specified unit.
     * If the implementing class is immutable, then this method must return an updated copy of the original.
     * If the class is mutable, then this method must update the original and return it.
     * This method is normally implemented by delegating to {@link #plus(long, PeriodUnit)} with
     * the amount negated.
     *
     * @param periodAmount  the amount of the specified unit to subtract, not null
     * @param unit  the unit of the period to subtract, not null
     * @return an object of the same type with the specified period subtracted, not null
     * @throws DateTimeException if the unit cannot be subtracted
     * @throws ArithmeticException if numeric overflow occurs
     */
    DateTime minus(long periodAmount, PeriodUnit unit);
    // JAVA8, but still face self type problem
    // default {
    //     return plus(DateTimes.safeNegate(period), unit);
    // }

    //-----------------------------------------------------------------------
    /**
     * Calculates the period from this date-time until the given date-time in the specified unit.
     * <p>
     * This is used to calculate the period between two date-times.
     * This method operates in association with {@link PeriodUnit#between}.
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
    long periodUntil(DateTime endDateTime, PeriodUnit unit);

    //-----------------------------------------------------------------------
    /**
     * Strategy for adjusting a date-time object.
     * <p>
     * This interface allows different kinds of adjustment to be modeled.
     * Examples might be an adjuster that sets the date avoiding weekends, or one that
     * sets the date to the last day of the month.
     * <p>
     * Implementations should not normally be used directly.
     * Instead, the {@link DateTime#with(WithAdjuster)} method should be used:
     * <pre>
     *   dateTime = dateTime.with(adjuster);
     * </pre>
     * <p>
     * See {@link DateTimeAdjusters} for a standard set of adjusters, including finding the
     * last day of the month.
     * 
     * <h4>Implementation notes</h4>
     * This interface must be implemented with care to ensure other classes operate correctly.
     * All implementations that can be instantiated must be final, immutable and thread-safe.
     */
    public interface WithAdjuster {
        /**
         * Implementation of the strategy to make an adjustment to the specified date-time object.
         * <p>
         * This method is not intended to be called by application code directly.
         * Instead, the {@link DateTime#with(WithAdjuster)} method should be used:
         * 
         * <h4>Implementation notes</h4>
         * The implementation takes the input object and adjusts it according to an algorithm.
         * For example, it could be used to adjust a date to "next Wednesday".
         * <p>
         * Implementations must use the methods on {@code DateTime} to make the adjustment.
         * The returned object must have the same observable type as this object.
         * The input object will be mutated if it is mutable, or a new object returned if immutable.
         * <p>
         * This interface can be used by calendar systems other than ISO.
         * Implementations may choose to document compatibility with other calendar systems, or
         * validate for it by querying the calendar system from the input object.
         *
         * @param dateTime  the date-time object to adjust, not null
         * @return an object of the same type with the adjustment made, not null
         * @throws DateTimeException if unable to make the adjustment
         * @throws ArithmeticException if numeric overflow occurs
         */
        DateTime doAdjustment(DateTime dateTime);
    }

    //-----------------------------------------------------------------------
    /**
     * Strategy for adjusting a date-time object by addition.
     * <p>
     * This interface allows different types of addition to be modeled.
     * Implementations of this interface are used to add to a date-time.
     * <p>
     * Implementations should not normally be used directly.
     * Instead, the {@link DateTime#plus(PlusAdjuster)} method should be used:
     * <pre>
     *   dateTime = dateTime.plus(adjuster);
     * </pre>
     * 
     * <h4>Implementation notes</h4>
     * This interface must be implemented with care to ensure other classes operate correctly.
     * All implementations that can be instantiated must be final, immutable and thread-safe.
     */
    public interface PlusAdjuster {
        /**
         * Implementation of the strategy to add to the specified date-time object.
         * <p>
         * This method is not intended to be called by application code directly.
         * Instead, the {@link DateTime#plus(PlusAdjuster)} method should be used:
         * 
         * <h4>Implementation notes</h4>
         * The implementation takes the input object and adds to it.
         * For example, the implementation {@link Duration} will add the length of the duration.
         * <p>
         * Implementations must use the methods on {@code DateTime} to make the adjustment.
         * The returned object must have the same observable type as this object.
         * The input object will be mutated if it is mutable, or a new object returned if immutable.
         * <p>
         * This interface can be used by calendar systems other than ISO.
         * Typically this requires no extra work, because the algorithm for adding/subtraing in
         * the calendar system is part of the {@code DateTime} implementation.
         *
         * @param dateTime  the date-time object to adjust, not null
         * @return an object of the same type with the adjustment made, not null
         * @throws DateTimeException if unable to add
         * @throws ArithmeticException if numeric overflow occurs
         */
        DateTime doAdd(DateTime dateTime);
    }

    /**
     * Strategy for adjusting a date-time object by subtraction.
     * <p>
     * This interface allows different types of subtraction to be modeled.
     * Implementations of this interface are used to subtract from a date-time.
     * <p>
     * Implementations should not normally be used directly.
     * Instead, the {@link DateTime#minus(MinusAdjuster)} method should be used:
     * <pre>
     *   dateTime = dateTime.minus(adjuster);
     * </pre>
     * 
     * <h4>Implementation notes</h4>
     * This interface must be implemented with care to ensure other classes operate correctly.
     * All implementations that can be instantiated must be final, immutable and thread-safe.
     */
    public interface MinusAdjuster {
        /**
         * Implementation of the strategy to subtract from the specified date-time object.
         * <p>
         * This method is not intended to be called by application code directly.
         * Instead, the {@link DateTime#minus(MinusAdjuster)} method should be used:
         * 
         * <h4>Implementation notes</h4>
         * The implementation takes the input object and subtracts from it.
         * For example, the implementation {@link Duration} will subtract the length of the duration.
         * <p>
         * Implementations must use the methods on {@code DateTime} to make the adjustment.
         * The returned object must have the same observable type as this object.
         * The input object will be mutated if it is mutable, or a new object returned if immutable.
         * <p>
         * This interface can be used by calendar systems other than ISO.
         * Typically this requires no extra work, because the algorithm for subtracting in
         * the calendar system is part of the {@code DateTime} implementation.
         *
         * @param dateTime  the date-time object to adjust, not null
         * @return an object of the same type with the adjustment made, not null
         * @throws DateTimeException if unable to subtract
         * @throws ArithmeticException if numeric overflow occurs
         */
        DateTime doSubtract(DateTime dateTime);
    }

}
