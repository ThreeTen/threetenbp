/*
 * Copyright (c) 2008-2010, Stephen Colebourne & Michael Nascimento Santos
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

import java.io.Serializable;

/**
 * A calendar system, consisting of rules controlling the passage of human-scale time.
 * <p>
 * Calendar systems describe a set of fields that can be used to describe time
 * in a human-scale. Typical fields include year, month-of-year and day-of-month.
 * <p>
 * This abstract base class provides a common mechanism to access the standard
 * fields which are supported in the vast majority of calendar systems.
 * Subclasses will provide the full set of fields for that calendar system.
 * <p>
 * The default chronology is {@link ISOChronology ISO8601} which is the
 * <i>de facto</i> world calendar today.
 * <p>
 * This is an abstract class and must be implemented with care to
 * ensure other classes in the framework operate correctly.
 * All instantiable subclasses must be final, immutable and thread-safe.
 * Wherever possible subclasses should be singletons with no public constructor.
 * It is recommended that subclasses implement {@code Serializable}
 *
 * @author Stephen Colebourne
 */
public abstract class Chronology implements Calendrical {

    /**
     * Restrictive constructor.
     */
    protected Chronology() {
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the value of the specified calendrical rule.
     * <p>
     * This method queries the value of the specified calendrical rule.
     * If the value cannot be returned for the rule from this offset then
     * {@code null} will be returned.
     *
     * @param rule  the rule to use, not null
     * @return the value for the rule, null if the value cannot be returned
     */
    @SuppressWarnings("unchecked")
    public <T> T get(CalendricalRule<T> rule) {
        if (rule instanceof CalendricalObjectRule<?>) {
            if (((CalendricalObjectRule<?>) rule).code == CalendricalObjectRule.CHRONO) {
                return (T) this;
            }
            return null;
        }
        return rule.derive(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the name of the chronology.
     * <p>
     * The name should not have the suffix 'Chronology'.
     * For example, the name of {@link ISOChronology} is 'ISO'.
     *
     * @return the name of the chronology, not null
     */
    public abstract String getName();

    //-----------------------------------------------------------------------
    /**
     * Returns a textual description of the chronology.
     *
     * @return a string form for debugging, not null
     */
    @Override
    public String toString() {
        return getName();
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the rule for {@code Chronology}.
     *
     * @return the rule for the chronology, not null
     */
    public static CalendricalRule<Chronology> rule() {
        return Rule.INSTANCE;
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    static final class Rule extends CalendricalObjectRule<Chronology> implements Serializable {
        private static final CalendricalRule<Chronology> INSTANCE = new Rule();
        private static final long serialVersionUID = 1L;
        private Rule() {
            super(Chronology.class, CHRONO);
        }
        private Object readResolve() {
            return INSTANCE;
        }
    }

}
