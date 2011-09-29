/*
 * Copyright (c) 2007-2011, Stephen Colebourne & Michael Nascimento Santos
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

import static javax.time.calendar.ISODateTimeRule.QUARTER_OF_YEAR;

import java.util.Locale;

import javax.time.CalendricalException;
import javax.time.calendar.format.TextStyle;

/**
 * A quarter-of-year, such as 'Q2'.
 * <p>
 * {@code QuarterOfYear} is an enum representing the 4 quarters of the year -
 * Q1, Q2, Q3 and Q4. These are defined as January to March, April to June,
 * July to September and October to December.
 * <p>
 * The calendrical framework requires date-time fields to have an {@code int} value.
 * The {@code int} value follows the quarter, from 1 (Q1) to 4 (Q4).
 * It is recommended that applications use the enum rather than the {@code int} value
 * to ensure code clarity.
 * <p>
 * <b>Do not use {@code ordinal()} to obtain the numeric representation of {@code QuarterOfYear}.
 * Use {@code getValue()} instead.</b>
 * <p>
 * This enum represents a common concept that is found in many calendar systems.
 * As such, this enum may be used by any calendar system that has the quarter-of-year
 * concept defined exactly equivalent to the ISO calendar system.
 * <p>
 * This is an immutable and thread-safe enum.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public enum QuarterOfYear implements Calendrical {

    /**
     * The singleton instance for the first quarter-of-year, from January to March.
     * This has the numeric value of {@code 1}.
     */
    Q1,
    /**
     * The singleton instance for the second quarter-of-year, from April to June.
     * This has the numeric value of {@code 2}.
     */
    Q2,
    /**
     * The singleton instance for the third quarter-of-year, from July to September.
     * This has the numeric value of {@code 3}.
     */
    Q3,
    /**
     * The singleton instance for the fourth quarter-of-year, from October to December.
     * This has the numeric value of {@code 4}.
     */
    Q4;

    //-----------------------------------------------------------------------
    /**
     * Gets the rule for {@code QuarterOfYear}.
     * <p>
     * This rule is a calendrical rule based on {@code QuarterOfYear}.
     * The equivalent date-time rule is {@link ISODateTimeRule#QUARTER_OF_YEAR}.
     *
     * @return the rule for the quarter-of-year, not null
     */
    public static CalendricalRule<QuarterOfYear> rule() {
        return ExtendedCalendricalRule.QUARTER_OF_YEAR;
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code QuarterOfYear} from an {@code int} value.
     * <p>
     * {@code QuarterOfYear} is an enum representing the 4 quarters of the year.
     * This factory allows the enum to be obtained from the {@code int} value.
     * The {@code int} value follows the quarter, from 1 (Q1) to 4 (Q4).
     * <p>
     * An exception is thrown if the value is invalid. The exception uses the
     * {@link ISOChronology} quarter-of-year rule to indicate the failed rule.
     *
     * @param quarterOfYear  the quarter-of-year to represent, from 1 (Q1) to 4 (Q4)
     * @return the QuarterOfYear singleton, not null
     * @throws IllegalCalendarFieldValueException if the quarter-of-year is invalid
     */
    public static QuarterOfYear of(int quarterOfYear) {
        switch (quarterOfYear) {
            case 1:
                return Q1;
            case 2:
                return Q2;
            case 3:
                return Q3;
            case 4:
                return Q4;
            default:
                throw new IllegalCalendarFieldValueException(QUARTER_OF_YEAR, quarterOfYear);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code QuarterOfYear} from a set of calendricals.
     * <p>
     * A calendrical represents some form of date and time information.
     * This method combines the input calendricals into a quarter-of-year.
     *
     * @param calendricals  the calendricals to create a quarter-of-year from, no nulls, not null
     * @return the quarter-of-year, not null
     * @throws CalendricalException if unable to merge to a quarter-of-year
     */
    public static QuarterOfYear from(Calendrical... calendricals) {
        return CalendricalEngine.merge(calendricals).deriveChecked(rule());
    }

    /**
     * Obtains an instance of {@code QuarterOfYear} from the engine.
     * <p>
     * This internal method is used by the associated rule.
     *
     * @param engine  the engine to derive from, not null
     * @return the QuarterOfYear singleton, null if unable to obtain
     */
    static QuarterOfYear deriveFrom(CalendricalEngine engine) {
        DateTimeField field = engine.getFieldDerived(QUARTER_OF_YEAR, true);
        if (field == null) {
            return null;
        }
        return of(field.getValidIntValue());
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the value of the specified calendrical rule.
     * <p>
     * This will only return a value for the {@link ISODateTimeRule#QUARTER_OF_YEAR}
     * rule, or something derivable from it.
     *
     * @param ruleToDerive  the rule to derive, not null
     * @return the value for the rule, null if the value cannot be returned
     */
    @SuppressWarnings("unchecked")
    public <T> T get(CalendricalRule<T> ruleToDerive) {
        if (ruleToDerive == rule()) {
            return (T) this;
        }
        return CalendricalEngine.derive(ruleToDerive, rule(), null, toField());
    }

    /**
     * Gets the quarter-of-year {@code int} value.
     * <p>
     * The values are numbered following the ISO-8601 standard,
     * from 1 (Q1) to 4 (Q4).
     *
     * @return the quarter-of-year, from 1 (Q1) to 4 (Q4)
     */
    public int getValue() {
        return ordinal() + 1;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the textual representation, such as 'Q1' or '4th quarter'.
     * <p>
     * This method is notionally specific to {@link ISOChronology} as it uses
     * the quarter-of-year rule to obtain the text. However, it is expected that
     * the text will be equivalent for all quarter-of-year rules, thus this aspect
     * of the implementation should be irrelevant to applications.
     * <p>
     * If no textual mapping is found then the {@link #getValue() numeric value} is returned.
     *
     * @param locale  the locale to use, not null
     * @return the short text value of the month-of-year, not null
     */
    public String getText(TextStyle style, Locale locale) {
        return QUARTER_OF_YEAR.getText(getValue(), style, locale);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the next quarter-of-year.
     * <p>
     * This calculates based on the time-line, thus it rolls around the end of
     * the week. The next quarter after Q4 is Q1.
     *
     * @return the next quarter-of-year, not null
     */
    public QuarterOfYear next() {
        return roll(1);
    }

    /**
     * Gets the previous quarter-of-year.
     * <p>
     * This calculates based on the time-line, thus it rolls around the end of
     * the year. The previous quarter before Q1 is Q4.
     *
     * @return the previous quarter-of-year, not null
     */
    public QuarterOfYear previous() {
        return roll(-1);
    }

    /**
     * Rolls the quarter-of-year, adding the specified number of quarters.
     * <p>
     * This calculates based on the time-line, thus it rolls around the end of
     * the year from Q4 to Q1. The quarters to roll by may be negative.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param quarters  the quarters to roll by, positive or negative
     * @return the resulting quarter-of-year, not null
     */
    public QuarterOfYear roll(int quarters) {
        return values()[(ordinal() + (quarters % 4 + 4)) % 4];
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the first of the three months that this quarter refers to.
     * <p>
     * Q1 will return January.<br />
     * Q2 will return April.<br />
     * Q3 will return July.<br />
     * Q4 will return October.
     * <p>
     * To obtain the other two months of the quarter, simply use {@link MonthOfYear#next()}
     * on the returned month.
     *
     * @return the first month in the quarter, not null
     */
    public MonthOfYear getFirstMonthOfQuarter() {
        switch (this) {
            case Q1:
                return MonthOfYear.JANUARY;
            case Q2:
                return MonthOfYear.APRIL;
            case Q3:
                return MonthOfYear.JULY;
            case Q4:
                return MonthOfYear.OCTOBER;
            default:
                throw new IllegalStateException("Unreachable");
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this quarter-of-year to an equivalent field.
     * <p>
     * The field is based on {@link ISODateTimeRule#QUARTER_OF_YEAR}.
     *
     * @return the equivalent quarter-of-year field, not null
     */
    public DateTimeField toField() {
        return QUARTER_OF_YEAR.field(getValue());
    }

}
