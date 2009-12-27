/*
 * Copyright (c) 2009, Stephen Colebourne & Michael Nascimento Santos
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

import javax.time.calendar.Calendrical;
import javax.time.calendar.CalendricalRule;
import javax.time.calendar.DateTimeFieldRule;
import javax.time.calendar.IllegalCalendarFieldValueException;
import javax.time.calendar.UnsupportedRuleException;

/**
 * Defines the valid eras for the Minguo calendar system.
 * <p>
 * <b>Do not use ordinal() to obtain the numeric representation of a MinguoEra
 * instance. Use getValue() instead.</b>
 * <p>
 * MinguoEra is immutable and thread-safe.
 *
 * @author Stephen Colebourne
 */
public enum MinguoEra implements Calendrical {

    /**
     * The singleton instance for the era before the current one - Before Minguo -
     * which has the value 0.
     */
    BEFORE_MINGUO,
    /**
     * The singleton instance for the current era - Minguo - which has the value 1.
     */
    MINGUO;

    //-----------------------------------------------------------------------
    /**
     * Gets the rule that defines how the era field operates.
     * <p>
     * The rule provides access to the minimum and maximum values, and a
     * generic way to access values within a calendrical.
     *
     * @return the era rule, never null
     */
    public static DateTimeFieldRule<MinguoEra> rule() {
        return MinguoChronology.eraRule();
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>MinguoEra</code> from a value.
     * <p>
     * The current era (from ISO year 1912 onwards) has the value 1
     * The previous era has the value 0.
     *
     * @param minguoEra  the era to represent, from 0 to 1
     * @return the MinguoEra singleton, never null
     * @throws IllegalCalendarFieldValueException if the era is invalid
     */
    public static MinguoEra minguoEra(int minguoEra) {
        switch (minguoEra) {
            case 0:
                return BEFORE_MINGUO;
            case 1:
                return MINGUO;
            default:
                throw new IllegalCalendarFieldValueException(MinguoChronology.eraRule(), minguoEra, 0, 1);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>MinguoEra</code> from a calendrical.
     * <p>
     * This can be used extract the era directly from any implementation
     * of Calendrical, including those in other calendar systems.
     *
     * @param calendrical  the calendrical to extract from, not null
     * @return the MinguoEra enum instance, never null
     * @throws UnsupportedRuleException if the era cannot be obtained
     */
    public static MinguoEra minguoEra(Calendrical calendrical) {
        return rule().getValue(calendrical);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the value of the specified calendrical rule.
     * <p>
     * This method queries the value of the specified calendrical rule.
     * If the value cannot be returned for the rule from this instance then
     * <code>null</code> will be returned.
     *
     * @param rule  the rule to use, not null
     * @return the value for the rule, null if the value cannot be returned
     */
    public <T> T get(CalendricalRule<T> rule) {
        return rule().deriveValueFor(rule, this, this);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the era numeric value.
     * <p>
     * The current era (from ISO year 1912 onwards) has the value 1
     * The previous era has the value 0.
     *
     * @return the era value, from 0 (BEFORE_MINGUO) to 1 (MINGUO)
     */
    public int getValue() {
        return ordinal();
    }

}
