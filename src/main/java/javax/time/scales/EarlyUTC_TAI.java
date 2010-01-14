/*
 * Copyright (c) 2009-2010, Stephen Colebourne & Michael Nascimento Santos
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

package javax.time.scales;

/*
    From http://hpiers.obspm.fr/eoppc/bul/bulc/UTC-TAI.history
    A similar table can be found at http://maia.usno.navy.mil/ser7/tai-utc.dat
 ---------------
 UTC-TAI.history
 ---------------
 RELATIONSHIP BETWEEN TAI AND UTC, UNTIL 27 DECEMBER 2005
 -------------------------------------------------------------------------------
 Limits of validity(at 0h UTC)       TAI - UTC

 1961  Jan.  1 - 1961  Aug.  1     1.422 818 0s + (MJD - 37 300) x 0.001 296s
       Aug.  1 - 1962  Jan.  1     1.372 818 0s +        ""
 1962  Jan.  1 - 1963  Nov.  1     1.845 858 0s + (MJD - 37 665) x 0.001 123 2s
 1963  Nov.  1 - 1964  Jan.  1     1.945 858 0s +        ""
 1964  Jan.  1 -       April 1     3.240 130 0s + (MJD - 38 761) x 0.001 296s
       April 1 -       Sept. 1     3.340 130 0s +        ""
       Sept. 1 - 1965  Jan.  1     3.440 130 0s +        ""
 1965  Jan.  1 -       March 1     3.540 130 0s +        ""
       March 1 -       Jul.  1     3.640 130 0s +        ""
       Jul.  1 -       Sept. 1     3.740 130 0s +        ""
       Sept. 1 - 1966  Jan.  1     3.840 130 0s +        ""
 1966  Jan.  1 - 1968  Feb.  1     4.313 170 0s + (MJD - 39 126) x 0.002 592s
 1968  Feb.  1 - 1972  Jan.  1     4.213 170 0s +        ""
 * */

/** UTC-TAI for 1958 - 1972.
 * Officially UTC commenced at 1961-01-01T00:00 with UTC-TAI=1.422818s, but we have extended it back to
 * 1958-01-01T00:00 where UTC-TAI=0s.
 * @see <a href="http://hpiers.obspm.fr/eoppc/bul/bulc/UTC-TAI.history">UTC-TAI History</a>
 * @author Mark Thornton
 */
public class EarlyUTC_TAI {
    public static final long START_EPOCH_SECONDS = ScaleUtil.epochSeconds(1958, 1, 1);
    public static final long END_EPOCH_SECONDS = ScaleUtil.epochSeconds(1972, 1, 1);
    private static final long SECONDS_PER_DAY = 86400;
    private static final int NANOS_PER_SECOND = 1000000000;
    private static final UTC_TAI<Entry> entries = new UTC_TAI<Entry>(new Entry[] {
        new Entry(1958,  1,       0, 36204, 1296000),
        new Entry(1961,  1, 1422818, 37300, 1296000),
        new Entry(1961,  8, 1372818),
        new Entry(1962,  1, 1845858, 37665, 1123200),
        new Entry(1963, 11, 1945858),
        new Entry(1964,  1, 3240130, 38761, 1296000),
        new Entry(1964,  4, 3340130),
        new Entry(1964,  9, 3440130),
        new Entry(1965,  1, 3540130),
        new Entry(1965,  3, 3640130),
        new Entry(1965,  7, 3740130),
        new Entry(1965,  9, 3840130),
        new Entry(1966,  1, 4313170, 39126, 2592000),
        new Entry(1968,  2, 4213170)
    });

    public static class Entry extends UTCPeriod {
        private long endEpochSeconds;
        private long deltaNanoseconds;
        private int originMJD;
        private int rateNanoseconds;
        private long originEpochSeconds;
        private double utcRate;
        private double taiRate;

        Entry(int startYear, int startMonth, int deltaMicroseconds)
        {
            super(startYear, startMonth, 1);
            deltaNanoseconds = 1000L*deltaMicroseconds;
        }

        Entry(int startYear, int startMonth, int deltaMicroseconds, int originMJD, int rateNanoseconds)
        {
            this(startYear, startMonth, deltaMicroseconds);
            this.originMJD = originMJD;
            this.rateNanoseconds = rateNanoseconds;
        }

        @Override
        void initialise(UTCPeriod previous) {
            Entry p = (Entry)previous;
            if (p != null) {
                p.endEpochSeconds = getStartEpochSeconds();
            }
            if (originMJD == 0) {
                // copy from previous entry
                originMJD = p.originMJD;
                rateNanoseconds = p.rateNanoseconds;
                originEpochSeconds = p.originEpochSeconds;
                utcRate = p.utcRate;
                taiRate = p.taiRate;
            }
            else {
                originEpochSeconds = SECONDS_PER_DAY *(originMJD-ScaleUtil.MJD_EPOCH);
                utcRate = rateNanoseconds / (double)SECONDS_PER_DAY;
                taiRate = utcRate/(1+utcRate/NANOS_PER_SECOND);
            }
            super.initialise(previous);
        }

        @Override
        public Entry getNext() {
            return (Entry)super.getNext();
        }

        @Override
        public Entry getPrevious() {
            return (Entry)super.getPrevious();
        }

        /** UTC epoch seconds at end of period */
        public long getEndEpochSeconds() {
            return endEpochSeconds;
        }

        /** TAI-UTC at originMJD */
        public long getDeltaNanoseconds() {
            return deltaNanoseconds;
        }

        /** origin in modified julian days of adjustment */
        public int getOriginMJD() {
            return originMJD;
        }

        /** additional TAI-UTC for every day since originMJD */
        public int getRateNanoseconds() {
            return rateNanoseconds;
        }

        /** TAI-UTC at a UTC instant.
         */
        public long getUTCDeltaNanoseconds(long utcEpochSeconds, int nanoOfSecond) {
            return deltaNanoseconds + Math.round(utcRate*((utcEpochSeconds-originEpochSeconds)+1e-9*nanoOfSecond));
        }

        /** TAI-UTC at a TAI instant.*/
        public long getTAIDeltaNanoseconds(long taiEpochSeconds, int nanoOfSecond) {
            return deltaNanoseconds + Math.round(taiRate*((taiEpochSeconds-originEpochSeconds) + 1e-9*(nanoOfSecond-deltaNanoseconds)));
        }

    }

    static {
        entries.get(entries.size()-1).endEpochSeconds = END_EPOCH_SECONDS;
    }

    public static UTC_TAI<Entry> list() {
        return entries;
    }

}
