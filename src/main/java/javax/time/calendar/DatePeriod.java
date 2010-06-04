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
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.time.CalendricalException;
import javax.time.Duration;
import javax.time.MathUtils;
import javax.time.calendar.format.CalendricalParseException;

/**
 * An immutable period consisting of the ISO-8601 year, month and day units,
 * such as '2 Years, 3 Months and 4 Days'.
 * <p>
 * A period is a human-scale description of an amount of time.
 * This class represents the 3 standard date-based period units from {@link ISOChronology} -
 * 'Years', 'Months' and 'Days'. There are defined to be 12 months in a year.
 * <p>
 * DatePeriod is immutable and thread-safe.
 *
 * @author Stephen Colebourne
 */
public final class DatePeriod
        implements PeriodProvider, Serializable {

    /**
     * A constant for a period of zero.
     */
    public static final DatePeriod ZERO = new DatePeriod(0, 0, 0);
    /**
     * The serialization version.
     */
    private static final long serialVersionUID = 1L;
    /**
     * The ISO period units, trusted to not be altered.
     */
    private static final PeriodUnit[] UNITS = new PeriodUnit[] {
        ISOChronology.periodYears(), ISOChronology.periodMonths(), ISOChronology.periodDays(),
    };

    /**
     * The number of years.
     */
    private final int years;
    /**
     * The number of months.
     */
    private final int months;
    /**
     * The number of days.
     */
    private final int days;
    /**
     * The cached PeriodFields.
     */
    private transient volatile PeriodFields periodFields;
    /**
     * The cached toString value.
     */
    private transient volatile String string;

    //-----------------------------------------------------------------------
    /**
     * Obtains a {@code DatePeriod} from an amount and unit.
     * <p>
     * The parameters represent the two parts of a phrase like '6 Days'.
     * <p>
     * A {@code DatePeriod} supports 3 units, ISO years, months and days.
     * The unit must be one of these, or be able to be converted to one of these.
     *
     * @param amount  the amount of the period, measured in terms of the unit, positive or negative
     * @param unit  the unit that the period is measured in, not null
     * @return the period, never null
     */
    public static DatePeriod of(int amount, PeriodUnit unit) {
        return of(PeriodFields.of(amount, unit));
    }

    /**
     * Obtains a {@code DatePeriod} from a provider of periods.
     * <p>
     * A {@code DatePeriod} supports 3 units, ISO years, months and days.
     * The period specified must only contain these units, or units that can be
     * {@link PeriodFields#toEquivalent converted} to these units.
     *
     * @param periodProvider  a provider of period information, not null
     * @return the period, never null
     * @throws CalendricalException if the provided period cannot be converted to the supported units
     * @throws ArithmeticException if any provided amount, except nanos, exceeds an {@code int}
     */
    public static DatePeriod of(PeriodProvider periodProvider) {
        PeriodFields.checkNotNull(periodProvider, "PeriodProvider must not be null");
        if (periodProvider instanceof DatePeriod) {
            return (DatePeriod) periodProvider;
        }
        PeriodFields periodFields = PeriodFields.of(periodProvider);
        periodFields = periodFields.toEquivalent(UNITS);
        int years = periodFields.getAmountInt(ISOChronology.periodYears());
        int months = periodFields.getAmountInt(ISOChronology.periodMonths());
        int days = periodFields.getAmountInt(ISOChronology.periodDays());
        return of(years, months, days);
    }

    /**
     * Obtains a {@code DatePeriod} from amounts from years to seconds.
     *
     * @param years  the amount of years, may be negative
     * @param months  the amount of months, may be negative
     * @param days  the amount of days, may be negative
     * @return the period, never null
     */
    public static DatePeriod of(int years, int months, int days) {
        if ((years | months | days) == 0) {
            return ZERO;
        }
        return new DatePeriod(years, months, days);
    }

    /**
     * Obtains a {@code DatePeriod} from years, months and days.
     *
     * @param years  the amount of years, may be negative
     * @param months  the amount of months, may be negative
     * @return the period, never null
     */
    public static DatePeriod ofYearsMonths(int years, int months) {
        if ((years | months) == 0) {
            return ZERO;
        }
        return new DatePeriod(years, months, 0);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains a {@code DatePeriod} from a number of years.
     *
     * @param years  the amount of years, may be negative
     * @return the period, never null
     */
    public static DatePeriod ofYears(int years) {
        if (years == 0) {
            return ZERO;
        }
        return new DatePeriod(years, 0, 0);
    }

    /**
     * Obtains a {@code DatePeriod} from a number of months.
     *
     * @param months  the amount of months, may be negative
     * @return the period, never null
     */
    public static DatePeriod ofMonths(int months) {
        if (months == 0) {
            return ZERO;
        }
        return new DatePeriod(0, months, 0);
    }

    /**
     * Obtains a {@code DatePeriod} from a number of days.
     *
     * @param days  the amount of days, may be negative
     * @return the period, never null
     */
    public static DatePeriod ofDays(int days) {
        if (days == 0) {
            return ZERO;
        }
        return new DatePeriod(0, 0, days);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains a {@code DatePeriod} from a string formatted as {@code PnYnMnDTnHnMn.nS}.
     * <p>
     * This will parse the string produced by {@code toString()} which is
     * a subset of the ISO8601 period format {@code PnYnMnDTnHnMn.nS}.
     * <p>
     * The string consists of a series of numbers with a suffix identifying their meaning.
     * The values, and suffixes, must be in the sequence year, month, day, hour, minute, second.
     * Any of the number/suffix pairs may be omitted providing at least one is present.
     * If the period is zero, the value is normally represented as {@code PT0S}.
     * The numbers must consist of ASCII digits.
     * Any of the numbers may be negative. Negative zero is not accepted.
     * The number of nanoseconds is expressed as an optional fraction of the seconds.
     * There must be at least one digit before any decimal point.
     * There must be between 1 and 9 inclusive digits after any decimal point.
     * The letters will all be accepted in upper or lower case.
     * The decimal point may be either a dot or a comma.
     *
     * @param text  the text to parse, not null
     * @return the parsed period, never null
     * @throws CalendricalParseException if the text cannot be parsed to a Period
     */
    public static DatePeriod parse(String text) {
        PeriodFields.checkNotNull(text, "Text to parse must not be null");
        Pattern pattern = Pattern.compile("P([-]?[0-9]+[Yy])?([-]?[0-9]+[Mm])?([-]?[0-9]+[Dd])?");
        Matcher matcher = pattern.matcher(text);
        if (text.length() < 3 || matcher.matches() == false) {
            throw new CalendricalParseException("Invalid period: " + text, text, 0);
        }
        int[] values = new int[3];
        for (int i = 0; i < matcher.groupCount(); i++) {
            String group = matcher.group(i + 1);
            if (group != null) {
                try {
                    values[i] = Integer.parseInt(group.substring(0, group.length() - 1));
                } catch (NumberFormatException ex) {
                    throw new CalendricalParseException("Invalid period: " + text, text, 0);
                }
            }
        }
        return of(values[0], values[1], values[2]);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param years  the amount
     * @param months  the amount
     * @param days  the amount
     */
    private DatePeriod(int years, int months, int days) {
        this.years = years;
        this.months = months;
        this.days = days;
    }

    /**
     * Resolves singletons.
     *
     * @return the resolved instance
     */
    private Object readResolve() {
        if ((years | months | days) == 0) {
            return ZERO;
        }
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this period is zero-length.
     *
     * @return true if this period is zero-length
     */
    public boolean isZero() {
        return (this == ZERO);
    }

    /**
     * Checks if this period is fully positive, excluding zero.
     * <p>
     * This checks whether all the amounts in the period are positive,
     * defined as greater than zero.
     *
     * @return true if this period is fully positive excluding zero
     */
    public boolean isPositive() {
        return ((years | months | days) > 0);
    }

    /**
     * Checks if this period is fully positive, including zero.
     * <p>
     * This checks whether all the amounts in the period are positive,
     * defined as greater than or equal to zero.
     *
     * @return true if this period is fully positive including zero
     */
    public boolean isPositiveOrZero() {
        return ((years | months | days) >= 0);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the amount of years of this period, if any.
     *
     * @return the amount of years of this period
     */
    public int getYears() {
        return years;
    }

    /**
     * Gets the amount of months of this period, if any.
     *
     * @return the amount of months of this period
     */
    public int getMonths() {
        return months;
    }

    /**
     * Gets the amount of days of this period, if any.
     *
     * @return the amount of days of this period
     */
    public int getDays() {
        return days;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this period with the specified amount of years.
     * <p>
     * This method will only affect the the years field.
     * All other fields are left untouched.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to represent
     * @return a period based on this period with the requested years, never null
     */
    public DatePeriod withYears(int years) {
        if (years == this.years) {
            return this;
        }
        return of(years, months, days);
    }

    /**
     * Returns a copy of this period with the specified amount of months.
     * <p>
     * This method will only affect the the months field.
     * All other fields are left untouched.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to represent
     * @return a period based on this period with the requested months, never null
     */
    public DatePeriod withMonths(int months) {
        if (months == this.months) {
            return this;
        }
        return of(years, months, days);
    }

    /**
     * Returns a copy of this period with the specified amount of days.
     * <p>
     * This method will only affect the the days field.
     * All other fields are left untouched.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to represent
     * @return a period based on this period with the requested days, never null
     */
    public DatePeriod withDays(int days) {
        if (days == this.days) {
            return this;
        }
        return of(years, months, days);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this period with the specified period added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periodProvider  the period to add, not null
     * @return a period based on this period with the requested period added, never null
     * @throws ArithmeticException if the capacity of any field is exceeded
     */
    public DatePeriod plus(PeriodProvider periodProvider) {
        DatePeriod other = of(periodProvider);
        return of(
                MathUtils.safeAdd(years, other.years),
                MathUtils.safeAdd(months, other.months),
                MathUtils.safeAdd(days, other.days));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this period with the specified number of years added.
     * <p>
     * This method will only affect the the years field.
     * All other fields are left untouched.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to add, positive or negative
     * @return a period based on this period with the requested years added, never null
     * @throws ArithmeticException if the capacity of an {@code int} is exceeded
     */
    public DatePeriod plusYears(int years) {
        return withYears(MathUtils.safeAdd(this.years, years));
    }

    /**
     * Returns a copy of this period with the specified number of months added.
     * <p>
     * This method will only affect the the months field.
     * All other fields are left untouched.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to add, positive or negative
     * @return a period based on this period with the requested months added, never null
     * @throws ArithmeticException if the capacity of an {@code int} is exceeded
     */
    public DatePeriod plusMonths(int months) {
        return withMonths(MathUtils.safeAdd(this.months, months));
    }

    /**
     * Returns a copy of this period with the specified number of days added.
     * <p>
     * This method will only affect the the days field.
     * All other fields are left untouched.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to add, positive or negative
     * @return a period based on this period with the requested days added, never null
     * @throws ArithmeticException if the capacity of an {@code int} is exceeded
     */
    public DatePeriod plusDays(int days) {
        return withDays(MathUtils.safeAdd(this.days, days));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this period with the specified period subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periodProvider  the period to subtract, not null
     * @return a period based on this period with the requested period subtracted, never null
     * @throws ArithmeticException if the capacity of any field is exceeded
     */
    public DatePeriod minus(PeriodProvider periodProvider) {
        DatePeriod other = of(periodProvider);
        return of(
                MathUtils.safeSubtract(years, other.years),
                MathUtils.safeSubtract(months, other.months),
                MathUtils.safeSubtract(days, other.days));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this period with the specified number of years subtracted.
     * <p>
     * This method will only affect the the years field.
     * All other fields are left untouched.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to subtract, positive or negative
     * @return a period based on this period with the requested years subtracted, never null
     * @throws ArithmeticException if the capacity of an {@code int} is exceeded
     */
    public DatePeriod minusYears(int years) {
        return withYears(MathUtils.safeSubtract(this.years, years));
    }

    /**
     * Returns a copy of this period with the specified number of months subtracted.
     * <p>
     * This method will only affect the the months field.
     * All other fields are left untouched.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to subtract, positive or negative
     * @return a period based on this period with the requested months subtracted, never null
     * @throws ArithmeticException if the capacity of an {@code int} is exceeded
     */
    public DatePeriod minusMonths(int months) {
        return withMonths(MathUtils.safeSubtract(this.months, months));
    }

    /**
     * Returns a copy of this period with the specified number of days subtracted.
     * <p>
     * This method will only affect the the days field.
     * All other fields are left untouched.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to subtract, positive or negative
     * @return a period based on this period with the requested days subtracted, never null
     * @throws ArithmeticException if the capacity of an {@code int} is exceeded
     */
    public DatePeriod minusDays(int days) {
        return withDays(MathUtils.safeSubtract(this.days, days));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a new instance with each element in this period multiplied
     * by the specified scalar.
     *
     * @param scalar  the scalar to multiply by, not null
     * @return a period based on this period with the amounts multiplied by the scalar, never null
     * @throws ArithmeticException if the capacity of any field is exceeded
     */
    public DatePeriod multipliedBy(int scalar) {
        if (this == ZERO || scalar == 1) {
            return this;
        }
        return of(
                MathUtils.safeMultiply(years, scalar),
                MathUtils.safeMultiply(months, scalar),
                MathUtils.safeMultiply(days, scalar));
    }

    /**
     * Returns a new instance with each element in this period divided
     * by the specified value.
     * <p>
     * The implementation simply divides each separate field by the divisor
     * using integer division.
     *
     * @param divisor  the value to divide by, not null
     * @return a period based on this period with the amounts divided by the divisor, never null
     * @throws ArithmeticException if dividing by zero
     */
    public DatePeriod dividedBy(int divisor) {
        if (divisor == 0) {
            throw new ArithmeticException("Cannot divide by zero");
        }
        if (this == ZERO || divisor == 1) {
            return this;
        }
        return of(
                years / divisor, months / divisor, days / divisor);
    }

    /**
     * Returns a new instance with each amount in this period negated.
     *
     * @return a period based on this period with the amounts negated, never null
     * @throws ArithmeticException if any field has the minimum value
     */
    public DatePeriod negated() {
        return multipliedBy(-1);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this period with the months field normalized.
     * <p>
     * The normalization adjusts the years and months fields ensuring that the
     * months field is between 0 and 11 inclusive.
     * Days are not normalized.
     * For example, a period of P1Y15M1D will be normalized to P2Y3M1D.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return a period based on this period with the amounts normalized, never null
     * @throws ArithmeticException if the capacity of any field is exceeded
     */
    public DatePeriod normalized() {
        if (this == ZERO) {
            return ZERO;
        }
        long totalMonths = totalMonths();
        int years = MathUtils.safeToInt(MathUtils.floorDiv(totalMonths, 12));
        int months = MathUtils.floorMod(totalMonths, 12);
        return of(years, months, days);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the total number of years represented by this period.
     * <p>
     * This method ignores the number of days and calculates using 12 months in a year.
     *
     * @return the total number of years
     */
    public long totalYears() {
        return years + months / 12L;
    }

    /**
     * Gets the total number of months represented by this period.
     * <p>
     * This method ignores the number of days and calculates using 12 months in a year.
     *
     * @return the total number of years
     */
    public long totalMonths() {
        return years * 12L + months;
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this period to a {@code PeriodFields}.
     * <p>
     * The returned {@code PeriodFields} will only contain the non-zero amounts.
     *
     * @return a {@code PeriodFields} equivalent to this period, never null
     */
    public PeriodFields toPeriodFields() {
        PeriodFields fields = periodFields;
        if (fields == null) {
            TreeMap<PeriodUnit, PeriodField> map = new TreeMap<PeriodUnit, PeriodField>();
            if (years != 0) {
                map.put(ISOChronology.periodYears(), PeriodField.of(years, ISOChronology.periodYears()));
            }
            if (months != 0) {
                map.put(ISOChronology.periodMonths(), PeriodField.of(months, ISOChronology.periodMonths()));
            }
            if (days != 0) {
                map.put(ISOChronology.periodDays(), PeriodField.of(days, ISOChronology.periodDays()));
            }
            periodFields = fields = PeriodFields.create(map);
        }
        return fields;
    }

    /**
     * Converts this object to an estimated {@code Duration} using the definitions on the units.
     * <p>
     * This uses the estimated duration definitions on the years, months and days units
     * to provide the estimate.
     *
     * @return a {@code Duration} with a length estimated from this period, never null
     */
    public Duration toEstimatedDuration() {
        return toPeriodFields().toEstimatedDuration();
    }

    //-----------------------------------------------------------------------
    /**
     * Is this period equal to the specified period.
     *
     * @param obj  the other period to compare to, null returns false
     * @return true if this instance is equal to the specified period
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof DatePeriod) {
            DatePeriod other = (DatePeriod) obj;
            return years == other.years && months == other.months && days == other.days;
        }
        return false;
    }

    /**
     * Returns the hash code for this period.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return ((years << 24) | (years >>> 8)) ^
                ((months << 16) | (months >>> 16)) ^
                days;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a string representation of the period.
     *
     * @return the period in ISO8601 string format
     */
    @Override
    public String toString() {
        String str = string;
        if (str == null) {
            if (this == ZERO) {
                str = "P0D";
            } else {
                StringBuilder buf = new StringBuilder();
                buf.append('P');
                if (years != 0) {
                    buf.append(years).append('Y');
                }
                if (months != 0) {
                    buf.append(months).append('M');
                }
                if (days != 0) {
                    buf.append(days).append('D');
                }
                str = buf.toString();
            }
            string = str;
        }
        return str;
    }

}
