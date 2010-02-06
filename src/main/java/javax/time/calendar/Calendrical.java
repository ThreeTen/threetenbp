/*
 * Copyright (c) 2009-2010, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.calendar;

/**
 * Provides access in a uniform way to calendrical values.
 * <p>
 * All classes that can provide calendrical information should implement this interface.
 * The single method allows a calendrical to be queried to find the value of a rule.
 * A calendrical might be a single field, such as month-of-year or hour-of-day, or a
 * whole object, such as {@code LocalDate} or {@code ZoneOffset}.
 * <p>
 * Calendrical makes no guarantees about the thread-safety or immutability
 * of implementations.
 * 
 * @author Stephen Colebourne
 */
public interface Calendrical {

    /**
     * Gets the value of the specified calendrical rule.
     * <p>
     * This method queries the value of the specified rule.
     * If the rule is not supported then {@code null} will be returned.
     * <p>
     * Where this method returns numeric values, Integer is the preferred type.
     * The values should be within the range of the field wherever possible.
     * For example, minute-of-hour should be from 0 to 59 inclusive.
     * The calling code must validate the value however and not trust it to be valid.
     *
     * @param rule  the rule to query, not null
     * @return the value for the rule, null if no value for the rule
     */
    <T> T get(CalendricalRule<T> rule);

}
