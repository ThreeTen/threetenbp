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

import javax.time.CalendricalException;

/**
 * A calendrical that represents a continuous and modifiable date and time.
 * <p>
 * This interface builds on {@link DateTimeCalendrical} specifying the ability to
 * add and subtract periods from the implementation.
 * For example, it is possible to add or subtract years, months and days from a date.
 * <p>
 * Not all date-time classes can implement the plus/minus methods specified here.
 * For example, a class representing a month and a day-of-month is effectively just
 * a collection of two independent fields. Without the year, it is not possible to
 * implement addition or subtraction of either months or days.
 * 
 * <h4>Implementation notes</h4>
 * This interface places no restrictions on implementations and makes no guarantees
 * about their thread-safety.
 * All implementations must be {@link Comparable}.
 */
public interface DateTimeObject extends DateTimeCalendrical {

    /**
     * Returns an object of the same type as this object with the specified field altered.
     * <p>
     * This method returns a new object based on this one with the value for the specified field changed.
     * For example, on a {@code LocalDate}, this could be used to set the year, month of day-of-month.
     * The returned object will have the same observable type as this object.
     * <p>
     * In some cases, changing a field is not fully defined. For example, if the target object is
     * a date representing the 31st January, then changing the month to February would be unclear.
     * In cases like this, the field is responsible for resolving the result. Typically it will choose
     * the previous valid date, which would be the last valid day of February in this example.
     * <p>
     * Implementations must check and return any fields defined in {@code LocalDateTimeField} before
     * delegating on to the method on the specified field.
     * If the implementing class is immutable, then this method must return an updated copy of the original.
     * If the class is mutable, then this method must update the original.
     *
     * @param field  the field to set in the returned date, not null
     * @param newValue  the new value of the field in the returned date, not null
     * @return an object of the same type with the specified field set, not null
     * @throws CalendricalException if the specified value is invalid
     * @throws CalendricalException if the field cannot be set on this type
     * @throws RuntimeException if the result exceeds the supported range
     */
    @Override
    DateTimeObject with(DateTimeField field, long newValue);

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
     * Implementations must check and return any fields defined in {@code LocalDateTimeUnit} before
     * delegating on to the method on the specified unit.
     * If the implementing class is immutable, then this method must return an updated copy of the original.
     * If the class is mutable, then this method must update the original.
     *
     * @param period  the amount of the specified unit to add, not null
     * @param unit  the unit of the period to add, not null
     * @return an object of the same type with the specified period added, not null
     * @throws CalendricalException if the unit cannot be added to this type
     * @throws RuntimeException if the result exceeds the supported range
     */
    DateTimeObject plus(long period, PeriodUnit unit);

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
     * Implementations must check and return any fields defined in {@code LocalDateTimeUnit} before
     * delegating on to the method on the specified unit.
     * If the implementing class is immutable, then this method must return an updated copy of the original.
     * If the class is mutable, then this method must update the original.
     *
     * @param period  the amount of the specified unit to subtract, not null
     * @param unit  the unit of the period to subtract, not null
     * @return an object of the same type with the specified period subtracted, not null
     * @throws CalendricalException if the unit cannot be subtracted to this type
     * @throws RuntimeException if the result exceeds the supported range
     */
    DateTimeObject minus(long period, PeriodUnit unit);
//      return plus(DateTimes.safeNegate(period), unit); // JAVA8 default interface

}
