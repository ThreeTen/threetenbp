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
 * Strategy for matching against a calendrical.
 * <p>
 * Matchers can be used to query the calendrical in unusual ways.
 * Examples might be a matcher that checks if the date is a weekend or holiday,
 * or Friday the Thirteenth.
 * <p>
 * CalendricalMatcher is an interface and must be implemented with care
 * to ensure other classes in the framework operate correctly.
 * All instantiable implementations must be final, immutable and thread-safe.
 *
 * @author Stephen Colebourne
 */
public interface CalendricalMatcher {

    /**
     * Checks if the input calendrical matches the rules of the implementation.
     * <p>
     * This is a strategy pattern that allows a range of matches to be made
     * against a calendrical. A typical implementation will query the calendrical
     * to extract one of more values, and compare or check them in some way.
     * <p>
     * For example, an implementation to check if the calendrical represents a
     * Saturday or Sunday:
     * <pre>
     *  public boolean matchesCalendrical(Calendrical calendrical) {
     *    DayOfWeek dow = calendrical.get(ISOChronology.dayOfWeekRule());
     *    return dow != null && (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY);
     *  }
     * </pre>
     *
     * @param calendrical  the calendrical to match against, not null
     * @return true if the date matches, false otherwise
     */
    boolean matchesCalendrical(Calendrical calendrical);

}
