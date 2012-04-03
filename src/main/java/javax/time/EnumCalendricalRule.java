/*
 * Copyright (c) 2011-2012 Stephen Colebourne & Michael Nascimento Santos
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
package javax.time;

import java.io.Serializable;

import javax.time.calendrical.CalendricalEngine;
import javax.time.calendrical.CalendricalRule;
import javax.time.calendrical.DateTimeField;
import javax.time.calendrical.ISODateTimeRule;

/**
 * Internal class supplying the rules for the additional date and time objects.
 * <p>
 * {@code EnumCalendricalRule} provides the rules for classes like {@code MonthOfYear}.
 * This class is package private. Rules should be accessed using the {@code rule()} metho
 * on each type, such as {@code MonthOfYear.rule()}.
 * <p>
 * Normally, a rule would be written as a small static nested class within the main class.
 * This class exists to avoid writing those separate classes, centralizing the singleton
 * pattern and enhancing performance via an {@code int} ordinal and package scope.
 * Thus, this design is an optimization and should not necessarily be considered best practice.
 * <p>
 * This class is final, immutable and thread-safe.
 *
 * @param <T> the rule type
 * @author Stephen Colebourne
 */
final class EnumCalendricalRule<T> extends CalendricalRule<T> implements Serializable {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Ordinal for performance and serialization.
     */
    final int ordinal;

    /**
     * Constructor used to create a rule.
     *
     * @param type  the type, not null
     * @param ordinal  the ordinal, not null
     */
    protected EnumCalendricalRule(Class<T> type, int ordinal) {
        super(type, type.getSimpleName());
        this.ordinal = ordinal;
    }

    /**
     * Deserialize singletons.
     * 
     * @return the resolved value, not null
     */
    private Object readResolve() {
        return RULE_CACHE[ordinal];
    }

    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    @Override
    protected T deriveFrom(CalendricalEngine engine) {
        switch (ordinal) {
            case MONTH_OF_YEAR_ORDINAL: return (T) deriveMoy(engine);
            case DAY_OF_WEEK_ORDINAL: return (T) deriveDow(engine);
            case AM_PM_OF_DAY_ORDINAL: return (T) deriveAmPm(engine);
        }
        return null;
    }

    /**
     * Obtains an instance of {@code MonthOfYear} from the engine.
     *
     * @param engine  the calendrical engine, not null
     * @return the derived object, null if unable to obtain
     */
    static MonthOfYear deriveMoy(CalendricalEngine engine) {
        DateTimeField field = engine.getFieldDerived(ISODateTimeRule.MONTH_OF_YEAR, true);
        return (field != null ? MonthOfYear.of(field.getValidIntValue()) : null);
    }

    /**
     * Obtains an instance of {@code AmPmOfDay} from the engine.
     *
     * @param engine  the calendrical engine, not null
     * @return the derived object, null if unable to obtain
     */
    static DayOfWeek deriveDow(CalendricalEngine engine) {
        DateTimeField field = engine.getFieldDerived(ISODateTimeRule.DAY_OF_WEEK, true);
        return (field != null ? DayOfWeek.of(field.getValidIntValue()) : null);
    }

    /**
     * Obtains an instance of {@code AmPmOfDay} from the engine.
     *
     * @param engine  the calendrical engine, not null
     * @return the derived object, null if unable to obtain
     */
    static AmPmOfDay deriveAmPm(CalendricalEngine engine) {
        DateTimeField field = engine.getFieldDerived(ISODateTimeRule.AMPM_OF_DAY, true);
        return (field != null ? AmPmOfDay.of(field.getValidIntValue()) : null);
    }

    //-----------------------------------------------------------------------
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EnumCalendricalRule<?>) {
            return ordinal == ((EnumCalendricalRule<?>) obj).ordinal;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return EnumCalendricalRule.class.hashCode() + ordinal;
    }

    //-----------------------------------------------------------------------
    private static final int MONTH_OF_YEAR_ORDINAL = 0;
    private static final int DAY_OF_WEEK_ORDINAL = 1;
    private static final int AM_PM_OF_DAY_ORDINAL = 2;

    //-----------------------------------------------------------------------
    /**
     * The rule for {@code MonthOfYear}.
     */
    static final CalendricalRule<MonthOfYear> MONTH_OF_YEAR = new EnumCalendricalRule<MonthOfYear>(MonthOfYear.class, MONTH_OF_YEAR_ORDINAL);
    /**
     * The rule for {@code DayOfWeek}.
     */
    static final CalendricalRule<DayOfWeek> DAY_OF_WEEK = new EnumCalendricalRule<DayOfWeek>(DayOfWeek.class, DAY_OF_WEEK_ORDINAL);
    /**
     * The rule for {@code AmPmOfDay}.
     */
    static final CalendricalRule<AmPmOfDay> AM_PM_OF_DAY = new EnumCalendricalRule<AmPmOfDay>(AmPmOfDay.class, AM_PM_OF_DAY_ORDINAL);

    /**
     * Cache of rules for deserialization.
     * Indices must match ordinal passed to rule constructor.
     */
    private static final CalendricalRule<?>[] RULE_CACHE = new CalendricalRule<?>[] {
        MONTH_OF_YEAR, DAY_OF_WEEK, AM_PM_OF_DAY,
    };

}
