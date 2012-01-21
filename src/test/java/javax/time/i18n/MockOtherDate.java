/*
 * Copyright (c) 2009-2011, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.i18n;

import java.io.Serializable;

import javax.time.LocalDate;
import javax.time.calendrical.Calendrical;
import javax.time.calendrical.CalendricalEngine;
import javax.time.calendrical.CalendricalRule;
import javax.time.calendrical.ISOChronology;

/**
 * A mock date.
 */
public final class MockOtherDate
        implements Calendrical, Comparable<MockOtherDate>, Serializable {

    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = 27545623L;

    /**
     * The underlying date.
     */
    private final LocalDate baseDate;

    //-----------------------------------------------------------------------
    public MockOtherDate(LocalDate date) {
        baseDate = date;
    }

    //-----------------------------------------------------------------------
    public <T> T get(CalendricalRule<T> rule) {
        if (rule == rule()) {
            return rule.reify(this);
        }
        return CalendricalEngine.derive(rule, rule(), toLocalDate(), null, null, null, ISOChronology.INSTANCE, null);
    }

    //-----------------------------------------------------------------------
    public LocalDate toLocalDate() {
        return baseDate;
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this instance to another.
     *
     * @param otherDate  the other date instance to compare to, not null
     * @return the comparator value, negative if less, positive if greater
     * @throws NullPointerException if otherDay is null
     */
    public int compareTo(MockOtherDate otherDate) {
        return baseDate.compareTo(otherDate.baseDate);
    }

    @Override
    public boolean equals(Object otherDate) {
        if (this == otherDate) {
            return true;
        }
        if (otherDate instanceof MockOtherDate) {
            MockOtherDate other = (MockOtherDate) otherDate;
            return baseDate.equals(other.baseDate);
        }
        return false;
    }

    /**
     * A hash code for this object.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return baseDate.hashCode();
    }

    @Override
    public String toString() {
        return baseDate.toString() + " (MockOtherDate)";
    }

    //-----------------------------------------------------------------------
    public static CalendricalRule<MockOtherDate> rule() {
        return Rule.INSTANCE;
    }

    /**
     * Rule implementation.
     */
    static final class Rule extends CalendricalRule<MockOtherDate> implements Serializable {
        private static final CalendricalRule<MockOtherDate> INSTANCE = new Rule();
        private static final long serialVersionUID = 1L;
        private Rule() {
            super(MockOtherDate.class, "MockOtherDate");
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        protected MockOtherDate deriveFrom(CalendricalEngine engine) {
            LocalDate date = engine.getDate(true);
            return date != null ? new MockOtherDate(date) : null;
        }
    }

}
