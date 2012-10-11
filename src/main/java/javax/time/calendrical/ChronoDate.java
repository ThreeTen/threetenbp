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

import javax.time.chrono.Chronology;
import javax.time.chrono.ISOChronology;

/**
 * A date without a time or time-zone.
 * <p>
 * Implementations of this interface represent a date, without time-of-day and
 * without offset or time-zone. The most common implementation is {@code LocalDate}
 * which should be used by the vast majority of applications.
 * Other implementations exist for non-ISO calendar systems.
 * <p>
 * When using this interface, bear in mind that two instances may represent dates
 * in two different calendar systems. Thus, application logic needs to handle
 * calendar systems, for example allowing for additional months, different years
 * and alternate leap systems.
 * 
 * <h4>Implementation notes</h4>
 * This class is immutable and thread-safe.
 * 
 * @param <T> the implementing subclass
 */
public interface ChronoDate<T extends ChronoDate<T>> extends DateTime<T>, DateTimeAdjuster, Comparable<T> {

    /**
     * Gets the chronology in use for this date-time.
     * <p>
     * The {@code Chronology} represents the calendar system, where the world civil calendar
     * system is referred to as the {@link ISOChronology ISO calendar system}.
     * All fields are expressed in terms of the calendar system.
     *
     * @return the calendar system chronology used by the date-time, not null
     */
    Chronology getChronology();

}
