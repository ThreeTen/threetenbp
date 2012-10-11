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

/**
 * A date and/or time object that is complete enough to be adjusted.
 * <p>
 * This interface extends the query-only {@link DateTimeAccessor} interface.
 * Additional methods allow the date-time object to be adjusted, primarily through
 * set, added to and subtracted from.
 * While {@code DateTimeAccessor} should be implemented by all date-time objects,
 * this interface should only be implemented where it makes sense to allow adjustment.
 * 
 * <h4>Implementation notes</h4>
 * This interface places no restrictions on implementations and makes no guarantees
 * about their thread-safety.
 * All implementations must be {@link Comparable}.
 * 
 * @param <T> the implementing subclass
 */
public interface DateTime<T extends DateTime<T>> extends DateTimeAccessor {

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
     * <h4>Implementation notes</h4>
     * Implementations must check and handle any fields defined in {@link LocalDateTimeField} before
     * delegating on to the {@link DateTimeField#doSet(DateTimeAccessor, long) doSet method} on the specified field.
     * If the implementing class is immutable, then this method must return an updated copy of the original.
     * If the class is mutable, then this method must update the original and return it.
     *
     * @param field  the field to set in the returned date, not null
     * @param newValue  the new value of the field in the returned date, not null
     * @return an object of the same type with the specified field set, not null
     * @throws DateTimeException if the specified value is invalid
     * @throws DateTimeException if the field cannot be set on this type
     * @throws RuntimeException if the result exceeds the supported range
     */
    T with(DateTimeField field, long newValue);

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
     * @throws DateTimeException if the unit cannot be added to this type
     * @throws RuntimeException if the result exceeds the supported range
     */
    T plus(long periodAmount, PeriodUnit unit);

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
     * @throws DateTimeException if the unit cannot be subtracted to this type
     * @throws RuntimeException if the result exceeds the supported range
     */
    T minus(long periodAmount, PeriodUnit unit);
    // JAVA8
    // default {
    //     return plus(DateTimes.safeNegate(period), unit);
    // }

}
