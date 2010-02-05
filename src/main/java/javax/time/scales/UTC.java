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
import javax.time.Duration;
import javax.time.Instant;
import javax.time.InstantProvider;
import javax.time.MathUtils;
import javax.time.TimeScale;
import javax.time.TimeScaleInstant;
import javax.time.TimeScaleInstant.Validity;

/** Universal Coordinated Time without leap seconds.
 * This implementation of UTC pretends that leap seconds do not exist.
 * That is times within a leap second can't be represented. However it
 * does correctly implement the relationship between TAI and UTC in the period 1961 to 1972
 * (and also extends that to the period 1958 - 1961).
 * Prior to 1958 UTC, TrueUTC and TAI are defined to be identical.
 * @author Mark Thornton
 */
public class UTC implements TimeScale, Serializable {
    private static final long serialVersionUID = 1;
    public static final String NAME = "UTC";
    public static final UTC INSTANCE = new UTC();

    private UTC() {

    }

    private Object readResolve() {
        return INSTANCE;
    }

    public TimeScaleInstant add(TimeScaleInstant t, Duration d) {
        if (t.getTimeScale() != this)
            return t.getTimeScale().add(t, d);
        long seconds = d.getSeconds();
        int nanos = d.getNanosInSecond();
        if (seconds == 0 && nanos == 0)
            return t;
        seconds = MathUtils.safeAdd(t.getEpochSeconds(), seconds);
        nanos += t.getNanoOfSecond();
        if (nanos >= ScaleUtil.NANOS_PER_SECOND) {
            nanos -= ScaleUtil.NANOS_PER_SECOND;
            seconds = MathUtils.safeIncrement(seconds);
        }
        if (seconds < ScaleUtil.START_LEAP_SECONDS && seconds > ScaleUtil.START_TAI) {
            return ScaleUtil.adjustUTCAroundGaps(t, seconds, nanos);
        }
        return TimeScaleInstant.seconds(this, seconds, nanos);
    }

    public TimeScaleInstant subtract(TimeScaleInstant t, Duration d) {
        if (t.getTimeScale() != this)
            return t.getTimeScale().subtract(t, d);
        long seconds = d.getSeconds();
        int nanos = d.getNanosInSecond();
        if (seconds == 0 && nanos == 0)
            return t;
        seconds = MathUtils.safeSubtract(t.getEpochSeconds(), seconds);
        nanos = t.getNanoOfSecond() - nanos;
        if (nanos < 0) {
            nanos += ScaleUtil.NANOS_PER_SECOND;
            seconds = MathUtils.safeDecrement(seconds);
        }
        if (seconds < ScaleUtil.START_LEAP_SECONDS && seconds > ScaleUtil.START_TAI) {
            return ScaleUtil.adjustUTCAroundGaps(t, seconds, nanos);
        }
        return TimeScaleInstant.seconds(this, seconds, nanos);
    }

    public Duration durationBetween(TimeScaleInstant a, TimeScaleInstant b) {
        if (a.getTimeScale() != this)
            a = toTimeScaleInstant(a);
        if (b.getTimeScale() != this)
            b = toTimeScaleInstant(b);
        return ScaleUtil.durationBetween(a, b);
    }

    /** Check validity of UTC instants.
     *
     * @param instant
     * @return Validity.ambiguous for instants which have two corresponding TAI instant's
     * (as occurs with leap seconds), Validity.invalid for times which never happened (due to
     * small jumps between 1961 and 1972), otherwise Validity.valid.
     */
    public Validity getValidity(TimeScaleInstant instant) {
        if (instant.getTimeScale() != this) {
            return instant.getTimeScale().getValidity(instant);
        }
        long s = instant.getEpochSeconds();
        if (s >= ScaleUtil.START_LEAP_SECONDS)
            return checkLeapAmbiguity(instant);
        if (s <= ScaleUtil.START_TAI)
            return Validity.valid;
        return ScaleUtil.checkEarlyValidity(instant);
    }

    private Validity checkLeapAmbiguity(TimeScaleInstant instant) {
        LeapSeconds.Entry e = LeapSeconds.list().entryFromUTC(instant.getEpochSeconds());
        return e.getNext() != null && instant.getEpochSeconds() == e.getNext().getStartEpochSeconds()-1 ?
            Validity.ambiguous : Validity.valid;
    }

    public boolean supportsLeapSecond() {
        return false;
    }

    public Instant toInstant(TimeScaleInstant tsInstant) {
        if (tsInstant.getTimeScale() != this) {
            return tsInstant.getTimeScale().toInstant(tsInstant);
        }
        return Instant.seconds(tsInstant.getEpochSeconds(), tsInstant.getNanoOfSecond());
    }

    public TimeScaleInstant toTimeScaleInstant(InstantProvider instantProvider) {
        Instant t = instantProvider.toInstant();
        return TimeScaleInstant.seconds(this, t.getEpochSeconds(), t.getNanoOfSecond());
    }

    public TimeScaleInstant toTAI(TimeScaleInstant src) {
        return src.getTimeScale() == this ? ScaleUtil.tai(src.getEpochSeconds(), src.getNanoOfSecond()) : src.getTimeScale().toTAI(src);
    }

    public TimeScaleInstant toTimeScaleInstant(TimeScaleInstant src) {
        if (src.getTimeScale() == this) {
            return src;
        }
        // The following tests do not cause the classes to be loaded
        // The idea is to allow conversions between UTC and TrueUTC without loading TAI.
        // The use of == is also valid as the constants are interned by the JVM
        String srcName = src.getTimeScale().getName();
        if (srcName == TrueUTC.NAME) {
            // simply forget the leap second
            return TimeScaleInstant.seconds(this, src.getEpochSeconds(), src.getNanoOfSecond());
        }
        if (srcName != TAI.NAME) {
            src = src.getTimeScale().toTAI(src);
        }
        // final case is convert from TAI
        return fromTAI(src);
    }

    private TimeScaleInstant fromTAI(TimeScaleInstant tsInstant) {
        if (tsInstant.compareTo(TAI.START_LEAPSECONDS) >= 0) {
            return fromModernTAI(tsInstant);
        }
        if (tsInstant.compareTo(TAI.START_TAI) > 0) {
            return fromEarlyTAI(tsInstant);
        }
        // finally ancient history
        return TimeScaleInstant.seconds(this, tsInstant.getEpochSeconds(), tsInstant.getNanoOfSecond());
    }

    private TimeScaleInstant fromModernTAI(TimeScaleInstant tsInstant) {
        LeapSeconds.Entry e = LeapSeconds.list().entryFromTAI(tsInstant);
        long s = MathUtils.safeSubtract(tsInstant.getEpochSeconds(), e.getDeltaSeconds());
        if (e.getNext() != null && s == e.getNext().getStartEpochSeconds()) {
            // repeat the last second
            s--;
        }
        return TimeScaleInstant.seconds(this, s, tsInstant.getNanoOfSecond());
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
