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
 * A date and/or time object that is complete enough to be adjusted.
 * <p>
 * There are two types of date-time class modeled in the API.
 * The first, {@link DateTime}, expresses the date-time only as a map of field to value.
 * The second, this interface, extends that to also support addition and subtraction.
 * <p>
 * For example, a class representing the combination of day-of-week and day-of-month,
 * suitable for storing "Friday the 13th", would implement only the former.
 * By contrast, a {@link LocalDate} or {@link LocalTime} implements this interface.
 * <p>
 * See {@link DateTimeAdjuster} to see one way that this interface is used.
 * 
 * <h4>Formal definition</h4>
 * <p>
 * Formally, a class should implement this interface if it meets three criteria:
 * <ul>
 * <li>it represents a map of date-time fields to values (as per {@code DateTime})
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
public interface AdjustableDateTime extends DateTime {

    // override to restrict return type
    @Override
    AdjustableDateTime with(DateTimeField field, long newValue);

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
     * delegating on to the {@link PeriodUnit#doAdd(AdjustableDateTime, long) doAdd method} on the specified unit.
     * If the implementing class is immutable, then this method must return an updated copy of the original.
     * If the class is mutable, then this method must update the original and return it.
     *
     * @param periodAmount  the amount of the specified unit to add, not null
     * @param unit  the unit of the period to add, not null
     * @return an object of the same type with the specified period added, not null
     * @throws DateTimeException if the unit cannot be added to this type
     * @throws RuntimeException if the result exceeds the supported range
     */
    AdjustableDateTime plus(long periodAmount, PeriodUnit unit);

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
     * delegating on to the {@link PeriodUnit#doAdd(AdjustableDateTime, long) doAdd method} on the specified unit.
     * If the implementing class is immutable, then this method must return an updated copy of the original.
     * If the class is mutable, then this method must update the original and return it.
     * This method is normally implemented by delegating to {@link #plus(long, PeriodUnit)} with
     * the amount negated.
     *
     * @param periodAmount  the amount of the specified unit to subtract, not null
     * @param unit  the unit of the period to subtract, not null
     * @return an object of the same type with the specified period subtracted, not null
     * @throws DateTimeException if the unit cannot be subtracted to this type
     * @throws RuntimeException if the result exceeds the supported range
     */
    AdjustableDateTime minus(long periodAmount, PeriodUnit unit);
    // JAVA8, but still face self type problem
    // default {
    //     return plus(DateTimes.safeNegate(period), unit);
    // }

    // TODO JAVA8 - could implement these
    // BUT without a self return type it still needs to be implemented in each subclass
    // AdjustableDateTime plus(Period period);
    // AdjustableDateTime minus(Period period);

}
