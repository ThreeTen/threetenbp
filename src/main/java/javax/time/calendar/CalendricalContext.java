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

import javax.time.CalendricalException;

/**
 * Context for aspects of date-time calculations that frequently change.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public final class CalendricalContext
        implements Serializable {

    /** Serialization version. */
    private static final long serialVersionUID = 1L;

    /**
     * Whether to use strict rules.
     */
    private final boolean strict;
    /**
     * Whether to use a date resolver for resolving dates.
     */
    private final DateResolver dateResolver;
    /**
     * Whether to check unused fields.
     */
    private final boolean checkUnusedFields;

    /**
     * Constructs an instance that can merge the specified calendrical.
     *
     * @param strict  whether to use strict rules
     * @param checkUnusedFields  whether to check unused fields
     */
    public CalendricalContext(boolean strict, boolean checkUnusedFields) {
        this.strict = strict;
        this.dateResolver = null;
        this.checkUnusedFields = checkUnusedFields;
    }

//    /**
//     * Validates that the input value is not null.
//     *
//     * @param object  the object to check
//     * @param errorMessage  the error to throw
//     * @throws NullPointerException if the object is null
//     */
//    private static void checkNotNull(Object object, String errorMessage) {
//        if (object == null) {
//            throw new NullPointerException(errorMessage);
//        }
//    }

    //-----------------------------------------------------------------------
    /**
     * Checks whether calculations will use strict rules or not.
     *
     * @return true if calculations will use strict rules
     */
    public boolean isStrict() {
        return strict;
    }

    /**
     * Checks whether to check unused fields.
     *
     * @return true if unused fields will be checked
     */
    public boolean isCheckUnusedFields() {
        return checkUnusedFields;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the merge is strict.
     *
     * @return true if the merge is strict
     */
    public DateResolver getDateResolver() {
        return dateResolver;
    }

    /**
     * Resolves the year, month and day-of-month to a date using ISO chronology rules.
     * <p>
     * The three input parameters are resolved to a date.
     * If the context specifies a date resolver, then that is used.
     * Otherwise, the strict/lenient flag is used.
     *
     * @param year  the year to resolve
     * @param month  the month to resolve
     * @param dayOfMonth  the day-of-month to resolve
     * @return the resolved date, never null
     * @throws IllegalCalendarFieldValueException if one of the fields has an invalid value
     * @throws CalendricalException if the input date does not match the stored date
     */
    public LocalDate resolveDate(int year, int month, int dayOfMonth) {
        if (dateResolver != null) {
            ISOChronology.yearRule().checkValue(year);  // TODO: make resolver handle this
            ISOChronology.dayOfMonthRule().checkValue(dayOfMonth);  // TODO: make resolver handle this
            return dateResolver.resolveDate(year, MonthOfYear.of(month), dayOfMonth);
        }
        if (strict) {
            return LocalDate.of(year, month, dayOfMonth);
        }
        if (month >= 1 && month <= 12) {
            if (dayOfMonth >= 1 && dayOfMonth <= 28) {  // range is valid for all months
                return LocalDate.of(year, month, dayOfMonth);
            }
            return LocalDate.of(year, month, 1).plusDays(((long) dayOfMonth) - 1);  // MIN/MAX handled ok
        }
        return LocalDate.of(year, 1, 1).plusMonths(month).plusMonths(-1)
                                .plusDays(((long) dayOfMonth) - 1);  // MIN/MAX handled ok
    }

    //-----------------------------------------------------------------------
    /**
     * Is this context equal to the specified context.
     *
     * @param obj  the other context to compare to, null returns false
     * @return true if this instance is equal to the specified context
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof CalendricalContext) {
            CalendricalContext other = (CalendricalContext) obj;
            return strict == other.strict &&
                (dateResolver == other.dateResolver || (dateResolver != null && dateResolver.equals(other.dateResolver))) &&
                checkUnusedFields == other.checkUnusedFields;
        }
        return false;
    }

    /**
     * A hashcode for this context.
     *
     * @return a suitable hashcode
     */
    @Override
    public int hashCode() {
        return (strict ? 1 : 0) + (dateResolver == null ? 0 : dateResolver.hashCode()) + (checkUnusedFields ? 1 : 0);
    }
//
//    //-----------------------------------------------------------------------
//    /**
//     * Outputs the context as a {@code String}.
//     *
//     * @return the string representation, never null
//     */
//    @Override
//    public String toString() {
//        return "CalendricalContext[" + (strict ? "strict" : "lenient") + "]";
//    }
}
