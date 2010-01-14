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

import java.io.Serializable;
import javax.time.Instant;
import javax.time.InstantProvider;
import javax.time.MathUtils;
import javax.time.TimeScale;
import javax.time.TimeScaleInstant;
import javax.time.TimeScaleInstant.Validity;

/** Universal Coordinated Time (UTC) with leap seconds.
 * Unlike many software implementations of UTC, this TimeScale
 * fully supports leap seconds. It also correctly implements
 * the relationship between TAI and UTC between 1961 and 1972 where
 * the UTC second did not correspond to an SI second and there were
 * a number of small discontinuities (of 50ms or 100ms). This has been further
 * extended back to 1958 where UT and TAI where equal. In the period 1958 to
 * 1961 adjustments where not coordinated internationally, hence there is no
 * single definitive record of the relationship between TAI and UT.
 * @author Mark Thornton
 */
public class TrueUTC implements TimeScale, Serializable {
    private static final long serialVersionUID = 1;
    public static final String NAME = "TrueUTC";
    public static final TrueUTC INSTANCE = new TrueUTC();

    private TrueUTC() {

    }

    private Object readResolve() {
        return INSTANCE;
    }

    /** Check validity of TrueUTC instants.
     *
     * @param instant instant to verify
     * @return
     */
    public Validity getValidity(TimeScaleInstant instant) {
        if (instant.getTimeScale() != this) {
            return instant.getTimeScale().getValidity(instant);
        }
        if (instant.getLeapSecond() != 0) {
            return instant.getEpochSeconds() <= ScaleUtil.START_LEAP_SECONDS ? Validity.invalid :
                checkLeapSecondValidity(instant);
        }
        return instant.getEpochSeconds() < ScaleUtil.START_LEAP_SECONDS && instant.getEpochSeconds() > ScaleUtil.START_TAI ?
            ScaleUtil.checkEarlyValidity(instant) : Validity.valid;
    }

    private Validity checkLeapSecondValidity(TimeScaleInstant instant) {
        if (instant.getEpochSeconds() >= LeapSeconds.getNextPossibleLeap()) {
            // should verify that leap is at end of June or December
            return Validity.possible;
        }
        LeapSeconds.Entry e = LeapSeconds.list().entryFromUTC(instant.getEpochSeconds());
        return (e.getNext() != null && instant.getEpochSeconds() == e.getNext().getStartEpochSeconds()-1) ?
            Validity.valid : Validity.invalid;
    }

    public boolean supportsLeapSecond() {
        return true;
    }

    public Instant toInstant(TimeScaleInstant tsInstant) {
        if (tsInstant.getTimeScale() != this) {
            return tsInstant.getTimeScale().toInstant(tsInstant);
        }
        // just lose the leap second (if any)
        return Instant.seconds(tsInstant.getEpochSeconds(), tsInstant.getNanoOfSecond());
    }

    public TimeScaleInstant toTimeScaleInstant(InstantProvider instantProvider) {
        Instant t = instantProvider.toInstant();
        return TimeScaleInstant.seconds(this, t.getEpochSeconds(), t.getNanoOfSecond());
    }

    public TimeScaleInstant toTAI(TimeScaleInstant src) {
        return src.getTimeScale() == this ? ScaleUtil.tai(src.getEpochSeconds(), src.getLeapSecond(), src.getNanoOfSecond()) :
            src.getTimeScale().toTAI(src);
    }

    public TimeScaleInstant toTimeScaleInstant(TimeScaleInstant src) {
        if (src.getTimeScale() == this) {
            return src;
        }
        String name = src.getTimeScale().getName();
        // The following tests do not cause the classes to be loaded
        // The idea is to allow conversions between UTC and TrueUTC without loading TAI.
        // The use of == is also valid as the constants are interned by the JVM
        if (name == UTC.NAME) {
            return TimeScaleInstant.seconds(this, src.getEpochSeconds(), src.getNanoOfSecond());
        }
        if (name != TAI.NAME) {
            src = src.getTimeScale().toTAI(src);
        }
        return fromTAI(src);
    }

    private TimeScaleInstant fromTAI(TimeScaleInstant src) {
        if (src.compareTo(TAI.START_LEAPSECONDS) >= 0) {
            return fromModernTAI(src);
        }
        if (src.compareTo(TAI.START_TAI) > 0) {
            return fromEarlyTAI(src);
        }
        // ancient, identical to TAI
        return TimeScaleInstant.seconds(this, src.getEpochSeconds(), src.getNanoOfSecond());
    }

    private TimeScaleInstant fromModernTAI(TimeScaleInstant tsInstant) {
        LeapSeconds.Entry e = LeapSeconds.list().entryFromTAI(tsInstant);
        long s = tsInstant.getEpochSeconds() - e.getDeltaSeconds();
        int leapSecond;
        if (e.getNext() != null && s >= e.getNext().getStartEpochSeconds()) {
            leapSecond = 1 + (int)(s-e.getNext().getStartEpochSeconds());
            s -= leapSecond;
        }
        else {
            leapSecond = 0;
        }
        return TimeScaleInstant.seconds(this, s, leapSecond, tsInstant.getNanoOfSecond());
    }

    private TimeScaleInstant fromEarlyTAI(TimeScaleInstant tsInstant) {
        EarlyUTC_TAI.Entry e = EarlyUTC_TAI.list().entryFromTAI(tsInstant);
        long nanos = tsInstant.getNanoOfSecond() - e.getTAIDeltaNanoseconds(tsInstant.getEpochSeconds(), tsInstant.getNanoOfSecond());
        long s = MathUtils.safeAdd(tsInstant.getEpochSeconds(), nanos/ScaleUtil.NANOS_PER_SECOND);
        nanos = nanos % ScaleUtil.NANOS_PER_SECOND;
        if (nanos < 0) {
            s--;
            nanos += ScaleUtil.NANOS_PER_SECOND;
        }
        if (s == e.getEndEpochSeconds()) {
            // need to adjust result for step at end of period
            nanos -= e.getUTCGapNanoseconds();
            if (nanos < 0) {
                s--;
                nanos += ScaleUtil.NANOS_PER_SECOND;
            }
        }
        return TimeScaleInstant.seconds(this, s, (int)nanos);
    }

    public String getName() {
        return NAME;
    }

}
