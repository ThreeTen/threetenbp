/*
 * Copyright (c) 2011-2012, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.extra;

import static javax.time.calendrical.LocalDateTimeField.DAY_OF_WEEK;
import static javax.time.calendrical.LocalPeriodUnit.DAYS;

import javax.time.calendrical.DateTimeAdjuster;
import javax.time.calendrical.AdjustableDateTime;

/**
 * A helper class for rules around weekends.
 * 
 * <h4>Implementation notes</h4>
 * This is a thread-safe utility class.
 * All returned classes are immutable and thread-safe.
 */
public final class WeekendRules {

    /**
     * Restricted constructor.
     */
    private WeekendRules() {
    }

    /**
     * Returns the next non weekend day adjuster, which adjusts the date one day
     * forward skipping Saturday and Sunday.
     * <p>
     * Some territories have weekends that do not consist of Saturday and Sunday.
     * No implementation is supplied to support this, however an adjuster
     * can be easily written to do so.
     *
     * @return the next working day adjuster, not null
     */
    public static DateTimeAdjuster nextNonWeekendDay() {
        return Adjuster.NEXT_NON_WEEKEND;
    }

    //-----------------------------------------------------------------------
    /**
     * Enum implementing the adjusters.
     */
    private static enum Adjuster implements DateTimeAdjuster {
        /** Next non weekend day adjuster. */
        NEXT_NON_WEEKEND {
            @Override
            public AdjustableDateTime doAdjustment(AdjustableDateTime calendrical) {
                int dow = (int) calendrical.get(DAY_OF_WEEK);
                switch (dow) {
                    case 6:  // Saturday
                        return calendrical.plus(2, DAYS);
                    case 5:  // Friday
                        return calendrical.plus(3, DAYS);
                    default:
                        return calendrical.plus(1, DAYS);
                }
            }
        },
    }

}
