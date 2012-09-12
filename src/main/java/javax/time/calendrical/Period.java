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

import java.util.Set;

import javax.time.DateTimeException;
import javax.time.SimplePeriod;

/**
 * A period of time.
 * <p>
 * This interface is implemented by all period classes, such as
 * {@link javax.time.SimplePeriod SimplePeriod} and {@link javax.time.ISOPeriod ISOPeriod}.
 * <p>
 * This interface models a period as a map from unit to amount.
 * Methods provides access to the {@link #supportedUnits() keys} and {@link #get(PeriodUnit) values}.
 * This definition means that there are no dynamic conversions of periods.
 * Thus, a period of '2 Decades' cannot be queried for 'Years'.
 * 
 * <h4>Implementation notes</h4>
 * This interface places no restrictions on implementations and makes no guarantees
 * about their thread-safety.
 * However, it is strongly recommended that implementations are immutable.
 */
public interface Period {

    /**
     * Gets the units that this period supports.
     * <p>
     * This returns the period units that this period supports.
     * A query for a unit returned from this method will return a value.
     * A query for any other unit will return an exception.
     * <p>
     * The simplest period, {@link SimplePeriod}, has one supported unit.
     * Other periods have more than one. The total size of the period is the
     * combination of all the unit-amount pairs.
     *
     * @return the units that this period supports, not null
     */
    Set<PeriodUnit> supportedUnits();

    /**
     * Gets the amount of the specified period unit.
     * <p>
     * This queries the period for the amount of the specified unit.
     * <p>
     * All the supported units can be queried.
     * While implementations may allow other units to be queried this is not recommended.
     *
     * @param unit  the unit to get, not null
     * @return the value for the unit
     * @throws DateTimeException if the unit is not a supported unit
     */
    long get(PeriodUnit unit);

    /**
     * Returns an object of the same type as this object with the specified unit altered.
     * <p>
     * This returns an object based on this one with the amount for the specified unit changed.
     * <p>
     * All the supported units can be changed.
     * While implementations may allow other units to be changed this is not recommended.
     * <p>
     * There is no guarantee that calling {@code get} after calling {@code with} will return
     * the same amount. This is because implementations are permitted to normalize after this method.
     * 
     * <h4>Implementation notes</h4>
     * If the implementing class is immutable, then this method must return an updated copy of the original.
     * If the class is mutable, then this method must update the original and return it.
     *
     * @param newAmount  the new amount of the unit, not null
     * @param unit  the unit to set, not null
     * @return an object of the same type with the specified unit set, not null
     * @throws DateTimeException if the unit is not a supported unit
     * @throws RuntimeException if the amount cannot be stored, such as if it is too large
     */
    Period with(long newAmount, PeriodUnit unit);

    /**
     * Returns an object of the same type as this object with the specified period added.
     * <p>
     * This method returns an object based on this one with the specified period added.
     * The returned object will have the same observable type as this object.
     * <p>
     * All the supported units can be added to. Implementations may also allow other units to be added.
     * If other units are permitted to be added then this may be achieved by conversion
     * to a supported unit, or by increasing the set of supported units.
     * In all cases, the total size of the resulting period is larger than the original by
     * the size of the period specified in the two arguments to this method.
     * 
     * <h4>Implementation notes</h4>
     * If the implementing class is immutable, then this method must return an updated copy of the original.
     * If the class is mutable, then this method must update the original and return it.
     *
     * @param amountToAdd  the amount of the specified unit to add, not null
     * @param unit  the unit of the period to add, not null
     * @return an object of the same type with the specified period added, not null
     * @throws DateTimeException if the unit cannot be added to this type
     * @throws RuntimeException if the addition exceeds the supported range
     */
    Period plus(long amountToAdd, PeriodUnit unit);

    /**
     * Returns an object of the same type as this object with the specified period subtracted.
     * <p>
     * This method returns an object based on this one with the specified period subtracted.
     * The returned object will have the same observable type as this object.
     * <p>
     * All the supported units can be subtracted from. Implementations may also allow other units to be subtracted.
     * If other units are permitted to be subtracted then this may be achieved by conversion
     * to a supported unit, or by increasing the set of supported units.
     * In all cases, the total size of the resulting period is smaller than the original by
     * the size of the period specified in the two arguments to this method.
     * 
     * <h4>Implementation notes</h4>
     * If the implementing class is immutable, then this method must return an updated copy of the original.
     * If the class is mutable, then this method must update the original and return it.
     *
     * @param amountToSubtract  the amount of the specified unit to subtract, not null
     * @param unit  the unit of the period to subtract, not null
     * @return an object of the same type with the specified period subtracted, not null
     * @throws DateTimeException if the unit cannot be subtracted to this type
     * @throws RuntimeException if the subtraction exceeds the supported range
     */
    Period minus(long amountToSubtract, PeriodUnit unit);

    /**
     * Returns an object of the same type as the specified object with this period added.
     * <p>
     * This adds this period to the specified date-time.
     * The returned object will have the same observable type as the specified object.
     *
     * @param dateTime  the dateTime to add this period to, not null
     * @return a date-time of the same type with the specified period added, not null
     * @throws DateTimeException if the period cannot be added
     * @throws RuntimeException if addition exceeds the supported range of the date-time
     */
    AdjustableDateTime addTo(AdjustableDateTime dateTime);

    /**
     * Returns an object of the same type as the specified object with this period subtracted.
     * <p>
     * This subtracts this period from the specified date-time.
     * The returned object will have the same observable type as the specified object.
     *
     * @param dateTime  the dateTime to subtract this period from, not null
     * @return a date-time of the same type with the specified period subtracted, not null
     * @throws DateTimeException if the period cannot be subtracted
     * @throws RuntimeException if subtraction exceeds the supported range of the date-time
     */
    AdjustableDateTime subtractFrom(AdjustableDateTime dateTime);

}
