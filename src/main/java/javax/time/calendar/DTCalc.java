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

import static javax.time.calendar.ISODateTimeRule.EPOCH_DAY;
import static javax.time.calendar.ISODateTimeRule.PACKED_EPOCH_MONTH_DAY;

import java.util.HashMap;

import javax.time.MathUtils;

public interface DTCalc {

    DateTimeRule getParentRule();

    DateTimeRule getChildRule();

    long getChild(long parentValue);

    long setChild(long parentValue, long childValue);

    //-----------------------------------------------------------------------
    static class DivCalc implements DTCalc {
        private DateTimeRule parentRule;
        private DateTimeRule childRule;
        private long factor;

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
            return parentValue / factor;
        }

        @Override
        public long setChild(long parentValue, long childValue) {
            return parentValue + (childValue - getChild(parentValue)) * factor;
        }
    }

    static class ModCalc implements DTCalc {
        private DateTimeRule parentRule;
        private DateTimeRule childRule;
        private long factor;

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
            return parentValue % factor;
        }

        @Override
        public long setChild(long parentValue, long childValue) {
            return parentValue + (childValue - getChild(parentValue));
        }
    }

    //-----------------------------------------------------------------------
    static class PackHighCalc implements DTCalc {
        private DateTimeRule parentRule;
        private DateTimeRule childRule;
        private long packBits;

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
        private DateTimeRule parentRule;
        private DateTimeRule childRule;
        private long packAmount;

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

    static class PemdInEd implements DTCalc {
        @Override
        public DateTimeRule getParentRule() {
            return EPOCH_DAY;
        }

        @Override
        public DateTimeRule getChildRule() {
            return PACKED_EPOCH_MONTH_DAY;
        }

        @Override
        public long getChild(long ed) {
            // find the march-based year
            long zeroDay = ed + ISOChronology.DAYS_0000_TO_1970 - 60;  // adjust to 0000-03-01 so leap day is at end of four year cycle
            long adjust = 0;
            if (zeroDay < 0) {
                // adjust negative years to positive for calculation
                long adjustCycles = (zeroDay + 1) / ISOChronology.DAYS_PER_CYCLE - 1;
                adjust = adjustCycles * 400;
                zeroDay += -adjustCycles * ISOChronology.DAYS_PER_CYCLE;
            }
            long yearEst = (400 * zeroDay + 591) / ISOChronology.DAYS_PER_CYCLE;
            long doyEst = zeroDay - (365 * yearEst + yearEst / 4 - yearEst / 100 + yearEst / 400);
            if (doyEst < 0) {
                // fix estimate
                yearEst--;
                doyEst = zeroDay - (365 * yearEst + yearEst / 4 - yearEst / 100 + yearEst / 400);
            }
            yearEst += adjust;  // reset any negative year
            int marchDoy0 = (int) doyEst;
            
            // convert march-based values back to january-based
            int marchMonth0 = (marchDoy0 * 5 + 2) / 153;
            int month = (marchMonth0 + 2) % 12 + 1;
            int dom = marchDoy0 - (marchMonth0 * 306 + 5) / 10 + 1;
            long year = yearEst + marchMonth0 / 10;
            
            // pack
            return packPemd(year, month, dom);
        }

        @Override
        public long setChild(long parentValue, long pemd) {
            long em = (pemd >> 5);
            long year = MathUtils.floorDiv(em, 12) + 1970;
            long y = year;
            long m = MathUtils.floorMod(em, 12) + 1;
            long total = 0;
            total += 365 * y;
            if (y >= 0) {
                total += (y + 3) / 4 - (y + 99) / 100 + (y + 399) / 400;
            } else {
                total -= y / -4 - y / -100 + y / -400;
            }
            total += ((367 * m - 362) / 12);
            total += (pemd & 31) - 1;
            if (m > 2) {
                total--;
                if (ISOChronology.isLeapYear(year) == false) {
                    total--;
                }
            }
            return total - ISOChronology.DAYS_0000_TO_1970;
        }

        static long packPemd(long year, int month, int dom) {
            return packPemd((year - 1970) * 12 + (month - 1), dom);
        }

        static long packPemd(long em, int dom) {
            return (em << 5) + dom;
        }
    }

    static class EdInPemd implements DTCalc {
        @Override
        public DateTimeRule getParentRule() {
            return PACKED_EPOCH_MONTH_DAY;
        }

        @Override
        public DateTimeRule getChildRule() {
            return EPOCH_DAY;
        }

        @Override
        public long getChild(long pemd) {
            return new PemdInEd().setChild(0, pemd);
        }

        @Override
        public long setChild(long parentValue, long ed) {
            return new PemdInEd().getChild(ed);
        }
    }

    //-----------------------------------------------------------------------
    static class Combi implements DTCalc {
        private DTCalc[] calcs;

        @Override
        public DateTimeRule getParentRule() {
            return calcs[0].getParentRule();
        }

        @Override
        public DateTimeRule getChildRule() {
            return calcs[calcs.length - 1].getChildRule();
        }

        @Override
        public long getChild(long parentValue) {
            long value = parentValue;
            for (DTCalc calc : calcs) {
                value = calc.getChild(value);
            }
            return value;
        }

        @Override
        public long setChild(long parentValue, long childValue) {
            long value = parentValue;
            for (int i = 0; i < calcs.length - 1; i++) {
                value = calcs[i].getChild(value);
            }
            for (int i = calcs.length - 1; i >= 0; i--) {
                value = calcs[i].setChild(value, childValue);
            }
            return value;
        }
    }

    static class Cache {
        static HashMap<DateTimeRule, DTCalc> map = new HashMap<DateTimeRule, DTCalc>();
        static long get(DateTimeRule rule, long value) {
            DTCalc calc = map.get(rule);
            return (calc == null ? Long.MIN_VALUE : calc.getChild(value));
        }
    }

    // perhaps these calcs could only be loaded if user adds their own rules
    // with default hard coded rules used for ISO in standard case
}
