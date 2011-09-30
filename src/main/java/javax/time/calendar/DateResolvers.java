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


/**
 * Provides common implementations of {@code DateResolver}.
 * <p>
 * This is a thread-safe utility class.
 * All resolvers returned are immutable and thread-safe.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public final class DateResolvers {

    /**
     * Private constructor since this is a utility class.
     */
    private DateResolvers() {
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the strict resolver which does not manipulate the state
     * in any way, resulting in an exception for all invalid values.
     * <p>
     * The invalid input 2011-02-30 will throw an exception.<br />
     * The invalid input 2012-02-30 will throw an exception (leap year).<br />
     * The invalid input 2011-04-31 will throw an exception.
     *
     * @return the strict resolver, not null
     */
    public static DateResolver strict() {
        return Impl.STRICT;
    }

    /**
     * Returns the previous valid day resolver, which adjusts the date to be
     * valid by moving to the last valid day of the month.
     * <p>
     * The invalid input 2011-02-30 will return 2011-02-28.<br />
     * The invalid input 2012-02-30 will return 2012-02-29 (leap year).<br />
     * The invalid input 2011-04-31 will return 2011-04-30.
     *
     * @return the previous valid day resolver, not null
     */
    public static DateResolver previousValid() {
        return Impl.PREVIOUS_VALID;
    }

    /**
     * Returns the next valid day resolver, which adjusts the date to be
     * valid by moving to the first of the next month.
     * <p>
     * The invalid input 2011-02-30 will return 2011-03-01.<br />
     * The invalid input 2012-02-30 will return 2012-03-01 (leap year).<br />
     * The invalid input 2011-04-31 will return 2011-05-01.
     *
     * @return the next valid day resolver, not null
     */
    public static DateResolver nextValid() {
        return Impl.NEXT_VALID;
    }

    /**
     * Returns the part lenient resolver, which adjusts the date to be
     * valid by moving it to the next month by the number of days that
     * are invalid up to the 31st of the month.
     * <p>
     * The invalid input 2011-02-29 will return 2011-03-01.<br />
     * The invalid input 2011-02-30 will return 2011-03-02.<br />
     * The invalid input 2011-02-31 will return 2011-03-03.<br />
     * The invalid input 2012-02-30 will return 2012-03-01 (leap year).<br />
     * The invalid input 2012-02-31 will return 2012-03-02 (leap year).<br />
     * The invalid input 2011-04-31 will return 2011-05-01.
     *
     * @return the part lenient resolver, not null
     */
    public static DateResolver partLenient() {
        return Impl.PART_LENIENT;
    }

    //-----------------------------------------------------------------------
    /**
     * Enum implementing the resolvers.
     */
    private static enum Impl implements DateResolver {
        /** Strict resolver. */
        STRICT {
            /** {@inheritDoc} */
            public LocalDate resolveDate(int year, MonthOfYear monthOfYear, int dayOfMonth) {
                return LocalDate.of(year, monthOfYear, dayOfMonth);
            }
        },
        /** Previous valid resolver. */
        PREVIOUS_VALID {
            /** {@inheritDoc} */
            public LocalDate resolveDate(int year, MonthOfYear monthOfYear, int dayOfMonth) {
                int lastDay = monthOfYear.getLastDayOfMonth(ISOChronology.isLeapYear(year));
                if (dayOfMonth > lastDay) {
                    return LocalDate.of(year, monthOfYear, lastDay);
                }
                return LocalDate.of(year, monthOfYear, dayOfMonth);
            }
        },
        /** Next valid resolver. */
        NEXT_VALID {
            /** {@inheritDoc} */
            public LocalDate resolveDate(int year, MonthOfYear monthOfYear, int dayOfMonth) {
                int len = monthOfYear.lengthInDays(ISOChronology.isLeapYear(year));
                if (dayOfMonth > len) {
                    return LocalDate.of(year, monthOfYear.next(), 1);
                }
                return LocalDate.of(year, monthOfYear, dayOfMonth);
            }
        },
        /** Part lenient resolver. */
        PART_LENIENT {
            /** {@inheritDoc} */
            public LocalDate resolveDate(int year, MonthOfYear monthOfYear, int dayOfMonth) {
                int len = monthOfYear.lengthInDays(ISOChronology.isLeapYear(year));
                if (dayOfMonth > len) {
                    // this line works because December is never invalid assuming the input is from 1-31
                    return LocalDate.of(year, monthOfYear.next(), dayOfMonth - len);
                }
                return LocalDate.of(year, monthOfYear, dayOfMonth);
            }
        },
    }

}
