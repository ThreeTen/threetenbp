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

import static javax.time.calendar.ISODateTimeRule.ALIGNED_WEEK_OF_MONTH;
import static javax.time.calendar.ISODateTimeRule.ALIGNED_WEEK_OF_YEAR;
import static javax.time.calendar.ISODateTimeRule.AMPM_OF_DAY;
import static javax.time.calendar.ISODateTimeRule.DAY_OF_MONTH;
import static javax.time.calendar.ISODateTimeRule.DAY_OF_YEAR;
import static javax.time.calendar.ISODateTimeRule.HOUR_OF_AMPM;
import static javax.time.calendar.ISODateTimeRule.HOUR_OF_DAY;
import static javax.time.calendar.ISODateTimeRule.MINUTE_OF_DAY;
import static javax.time.calendar.ISODateTimeRule.MINUTE_OF_HOUR;
import static javax.time.calendar.ISODateTimeRule.MONTH_OF_QUARTER;
import static javax.time.calendar.ISODateTimeRule.MONTH_OF_YEAR;
import static javax.time.calendar.ISODateTimeRule.PACKED_EPOCH_MONTH_DAY;
import static javax.time.calendar.ISODateTimeRule.PACKED_YEAR_DAY;
import static javax.time.calendar.ISODateTimeRule.QUARTER_OF_YEAR;
import static javax.time.calendar.ISODateTimeRule.SECOND_OF_DAY;
import static javax.time.calendar.ISODateTimeRule.SECOND_OF_HOUR;
import static javax.time.calendar.ISODateTimeRule.SECOND_OF_MINUTE;
import static javax.time.calendar.ISODateTimeRule.YEAR;
import static javax.time.calendar.ISODateTimeRule.ZERO_EPOCH_MONTH;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

class DTChrono {
    // problem with chronologies like Historic, where the cutover date possibilities are semi-infinite
    // any registration based approach naturally leads to an unbound map
    
    // could only register concept calculation, with details like week-rules or historic cutover read
    // from passed in rule, but then how would you look up the calc in the map?
    
    // pushes design back to just focusing on rules or chronologies
    
    // same problem would apply to triple-based approach, rather than pair-based

    static ConcurrentMap<DateTimeRule, DTCalc> map = new ConcurrentHashMap<DateTimeRule, DTCalc>();
    static {
        registerDivMod(SECOND_OF_HOUR, SECOND_OF_MINUTE, 1, 60);
        registerDivMod(SECOND_OF_HOUR, MINUTE_OF_HOUR, 60, 60);
        registerDivMod(SECOND_OF_DAY, SECOND_OF_MINUTE, 1, 60);
        registerDivMod(SECOND_OF_DAY, SECOND_OF_HOUR, 1, 60 * 60);
        registerDivMod(SECOND_OF_DAY, MINUTE_OF_HOUR, 60, 60);
        registerDivMod(SECOND_OF_DAY, MINUTE_OF_DAY, 60, 60 * 24);
        registerDivMod(SECOND_OF_DAY, HOUR_OF_DAY, 60 * 60, 24);
        registerDivMod(MINUTE_OF_DAY, MINUTE_OF_HOUR, 1, 60);
        registerDivMod(MINUTE_OF_DAY, HOUR_OF_DAY, 60, 24);
        registerDivMod(HOUR_OF_DAY, HOUR_OF_AMPM, 1, 12);
        registerDivMod(HOUR_OF_DAY, AMPM_OF_DAY, 12, 2);
        registerPacked(PACKED_EPOCH_MONTH_DAY, ZERO_EPOCH_MONTH, DAY_OF_MONTH, 31);
        registerPacked(PACKED_YEAR_DAY, YEAR, DAY_OF_YEAR, 511);
        registerDivMod(DAY_OF_MONTH, ALIGNED_WEEK_OF_MONTH, 7, 5);
        registerDivMod(DAY_OF_YEAR, ALIGNED_WEEK_OF_YEAR, 7, 53);
        registerDivMod(ZERO_EPOCH_MONTH, MONTH_OF_QUARTER, 1, 3);
        registerDivMod(ZERO_EPOCH_MONTH, MONTH_OF_YEAR, 1, 12);
        registerDivMod(ZERO_EPOCH_MONTH, QUARTER_OF_YEAR, 3, 4);
        registerDivMod(ZERO_EPOCH_MONTH, YEAR, 12, Long.MAX_VALUE);
    }

    //-----------------------------------------------------------------------
    /**
     * Validates that the input value is not null.
     *
     * @param object  the object to check
     * @param errorMessage  the error to throw
     * @throws NullPointerException if the object is null
     */
    static void checkNotNull(Object object, String errorMessage) {
        if (object == null) {
            throw new NullPointerException(errorMessage);
        }
    }

    static long get(DateTimeRule rule, long value) {
        DTCalc calc = map.get(rule);
        return (calc == null ? Long.MIN_VALUE : calc.getChild(value));
    }

    public static void registerDivMod(DateTimeRule parentRule, DateTimeRule childRule, long div, long mod) {
        checkNotNull(parentRule, "DateTimeRule must not be null");
        checkNotNull(childRule, "DateTimeRule must not be null");
        registerCalc(new DivModCalc(parentRule, childRule, div, mod));
    }

    public static void registerPacked(DateTimeRule parentRule, DateTimeRule midRule, DateTimeRule childRule, long packAmount) {
        checkNotNull(parentRule, "DateTimeRule must not be null");
        checkNotNull(midRule, "DateTimeRule must not be null");
        checkNotNull(childRule, "DateTimeRule must not be null");
        registerCalc(new PackHighCalc(parentRule, midRule, Long.bitCount(packAmount)));
        registerCalc(new PackLowCalc(midRule, childRule, packAmount));
    }

    public static void registerCalc(DTCalc calc) {
        checkNotNull(calc, "DTCalc must not be null");
        map.putIfAbsent(calc.getParentRule(), calc);
    }

    //-----------------------------------------------------------------------
    static class DivModCalc implements DTCalc {
        private final DateTimeRule parentRule;
        private final DateTimeRule childRule;
        private final long div;
        private final long mod;

        DivModCalc(DateTimeRule parentRule, DateTimeRule childRule, long div, long mod) {
            this.parentRule = parentRule;
            this.childRule = parentRule;
            this.div = div;
            this.mod = mod;
        }

        @Override
        public DateTimeRule getParentRule() {
            return parentRule;
        }

        @Override
        public DateTimeRule getChildRule() {
            return childRule;
        }

        @Override
        public long getChild(long parentValue) {
            return (parentValue / div) % mod;
        }

        @Override
        public long setChild(long parentValue, long childValue) {
            return parentValue + (childValue - getChild(parentValue)) * div;
        }
    }

    //-----------------------------------------------------------------------
    static class PackHighCalc implements DTCalc {
        private final DateTimeRule parentRule;
        private final DateTimeRule childRule;
        private final long packBits;

        PackHighCalc(DateTimeRule parentRule, DateTimeRule childRule, long packBits) {
            this.parentRule = parentRule;
            this.childRule = parentRule;
            this.packBits = packBits;
        }

        @Override
        public DateTimeRule getParentRule() {
            return parentRule;
        }

        @Override
        public DateTimeRule getChildRule() {
            return childRule;
        }

        @Override
        public long getChild(long parentValue) {
            return parentValue >> packBits;
        }

        @Override
        public long setChild(long parentValue, long childValue) {
            return parentValue + ((childValue - getChild(parentValue)) << packBits);
        }
    }

    static class PackLowCalc implements DTCalc {
        private final DateTimeRule parentRule;
        private final DateTimeRule childRule;
        private final long packAmount;

        PackLowCalc(DateTimeRule parentRule, DateTimeRule childRule, long packAmount) {
            this.parentRule = parentRule;
            this.childRule = parentRule;
            this.packAmount = packAmount;
        }

        @Override
        public DateTimeRule getParentRule() {
            return parentRule;
        }

        @Override
        public DateTimeRule getChildRule() {
            return childRule;
        }

        @Override
        public long getChild(long parentValue) {
            return parentValue & packAmount;
        }

        @Override
        public long setChild(long parentValue, long childValue) {
            return parentValue + (childValue - getChild(parentValue));
        }
    }

}
