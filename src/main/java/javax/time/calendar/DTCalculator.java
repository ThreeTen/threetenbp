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

public interface DTCalculator {

    DateTimeRule getParentRule();

    DateTimeRule getLargeChildRule();
    long getLargeChild(long parentValue);
    long setLargeChild(long parentValue, long childValue);

    DateTimeRule getSmallChildRule();
    long getSmallChild(long parentValue);
    long setSmallChild(long parentValue, long childValue);

    long merge(long largeChildValue, long smallChildValue);

    //-----------------------------------------------------------------------
    static class DivCalc implements DTCalculator {
        private DateTimeRule parentRule;
        private DateTimeRule largeChildRule;
        private DateTimeRule smallChildRule;
        private long factor;

        @Override
        public DateTimeRule getParentRule() {
            return parentRule;
        }

        @Override
        public DateTimeRule getLargeChildRule() {
            return largeChildRule;
        }

        @Override
        public long getLargeChild(long parentValue) {
            return parentValue / factor;
        }

        @Override
        public long setLargeChild(long parentValue, long childValue) {
            return parentValue + (childValue - getLargeChild(parentValue)) * factor;
        }

        @Override
        public DateTimeRule getSmallChildRule() {
            return smallChildRule;
        }

        @Override
        public long getSmallChild(long parentValue) {
            return parentValue % factor;
        }

        @Override
        public long setSmallChild(long parentValue, long childValue) {
            return parentValue + (childValue - getSmallChild(parentValue));
        }

        @Override
        public long merge(long largeChildValue, long smallChildValue) {
            return largeChildValue * factor + smallChildValue;
        }
        
    }

    //-----------------------------------------------------------------------
    static class PackCalc implements DTCalculator {
        private DateTimeRule parentRule;
        private DateTimeRule largeChildRule;
        private DateTimeRule smallChildRule;
        private long packAmount;
        private long packBits;

        @Override
        public DateTimeRule getParentRule() {
            return parentRule;
        }

        @Override
        public DateTimeRule getLargeChildRule() {
            return largeChildRule;
        }

        @Override
        public long getLargeChild(long parentValue) {
            return parentValue >> packBits;
        }

        @Override
        public long setLargeChild(long parentValue, long childValue) {
            return parentValue + (childValue - getLargeChild(parentValue)) << packBits;
        }

        @Override
        public DateTimeRule getSmallChildRule() {
            return smallChildRule;
        }

        @Override
        public long getSmallChild(long parentValue) {
            return parentValue & packAmount;
        }

        @Override
        public long setSmallChild(long parentValue, long childValue) {
            return parentValue + (childValue - getSmallChild(parentValue));
        }

        @Override
        public long merge(long largeChildValue, long smallChildValue) {
            return largeChildValue << packBits + smallChildValue;
        }
    }

//    //-----------------------------------------------------------------------
//    static class CombinedCalc implements DTCalculator {
//        private DTCalculator top;
//        private DTCalculator bottom;
//
//        @Override
//        public DateTimeRule getParentRule() {
//            return top.getParentRule();
//        }
//
//        @Override
//        public DateTimeRule getLargeChildRule() {
//            return bottom.getLargeChildRule();
//        }
//
//        @Override
//        public long getLargeChild(long parentValue) {
//            return bottom.getLargeChild(top.getLargeChild(parentValue));
//        }
//
//        @Override
//        public long setLargeChild(long parentValue, long childValue) {
//            return top.setLargeChild(parentValue, bottom.setLargeChild(top.getLargeChild(parentValue), childValue));
//        }
//
//        @Override
//        public DateTimeRule getSmallChildRule() {
//            return bottom.getSmallChildRule();
//        }
//
//        @Override
//        public long getSmallChild(long parentValue) {
//            return bottom.getSmallChild(top.getLargeChild(parentValue));
//        }
//
//        @Override
//        public long setSmallChild(long parentValue, long childValue) {
//            return top.setLargeChild(parentValue, bottom.setSmallChild(top.getLargeChild(parentValue), childValue));
//        }
//
//        @Override
//        public long merge(long largeChildValue, long smallChildValue) {
//            return top.setLargeChild(parentValue, bottom.setSmallChild(top.getLargeChild(parentValue), childValue));
//        }
//    }

}
