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
import javax.time.scales.TimeScaleInstant.Validity;

/**
 * The simplified time-scale based on true UTC that ignores leap seconds.
 * <p>
 * The {@link TrueUTC true UTC} time-scale is a time-scale that introduces
 * "leap seconds" to ensure that the instant represented stays in line with
 * the earths rotational changes. These leap seconds are hard to follow and
 * calculate with, thus many computer systems simply pretend they do not exist.
 * Thus, for this time-scale, a day always has 86400 "seconds", but the second
 * may differ from the standard scientific definition of a second.
 * <p>
 * The result of ignoring leap seconds is that real leap seconds cannot be represented.
 * This is not a problem for most applications, but may be a problem for some.
 * If the application needs to handle leap seconds then the {@code TrueUTC} or
 * {@code TAI} time-scale should be used.
 * <p>
 * This class does correctly implement the relationship between TAI and UTC in
 * the period 1961 to 1972 (and also extends that to the period 1958 - 1961).
 * Prior to 1958 UTC, TrueUTC and TAI are defined to be identical.
 *
 * @author Mark Thornton
 */
class UTC implements TimeScale, Serializable {

    /**
     * Singleton instance.
     */
    static final UTC INSTANCE = new UTC();
    /**
     * Name of the singleton, used by other classes to avoid class-loading.
     */
    static final String NAME = "UTC";
    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 1L;

    //-----------------------------------------------------------------------
    /**
     * Private constructor.
     */
    private UTC() {
    }

    /**
     * Resolves singleton.
     *
     * @return the resolved singleton, never null
     */
    private Object readResolve() {
        return INSTANCE;
    }

    //-----------------------------------------------------------------------
    /** {@inheritDoc} */
    public String getName() {
        return NAME;
    }

    /** {@inheritDoc} */
    public boolean supportsLeapSecond() {
        return false;
    }

    //-----------------------------------------------------------------------
    /** {@inheritDoc} */
    public Instant toInstant(TimeScaleInstant tsInstant) {
        if (tsInstant.getTimeScale() != this) {
            return tsInstant.getTimeScale().toInstant(tsInstant);
        }
        return Instant.ofSeconds(tsInstant.getEpochSeconds(), tsInstant.getNanoOfSecond());
    }

    /** {@inheritDoc} */
    public TimeScaleInstant toTAI(TimeScaleInstant src) {
        if (src.getTimeScale() != this) {
            return src.getTimeScale().toTAI(src);
        }
        return ScaleUtil.tai(src.getEpochSeconds(), src.getNanoOfSecond());
    }

    /** {@inheritDoc} */
    public TimeScaleInstant toTimeScaleInstant(InstantProvider instantProvider) {
        Instant t = Instant.from(instantProvider);
        return TimeScaleInstant.seconds(this, t.getEpochSeconds(), t.getNanoOfSecond());
    }

    /** {@inheritDoc} */
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
        if (tsInstant.compareTo(TAI.START_LEAP_SECONDS) >= 0) {
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

    //-----------------------------------------------------------------------
    /** {@inheritDoc} */
    public Validity getValidity(TimeScaleInstant instant) {
        // ambiguous for instants which have two corresponding TAI instant's (as occurs with leap seconds)
        // invalid for times which never happened (due to small jumps between 1961 and 1972)
        // otherwise valid
        if (instant.getTimeScale() != this) {
            return instant.getTimeScale().getValidity(instant);
        }
        long s = instant.getEpochSeconds();
        if (s >= ScaleUtil.START_LEAP_SECONDS) {
            return checkLeapAmbiguity(instant);
        }
        if (s <= ScaleUtil.START_TAI) {
            return Validity.VALID;
        }
        return ScaleUtil.checkEarlyValidity(instant);
    }

    private Validity checkLeapAmbiguity(TimeScaleInstant instant) {
        LeapSeconds.Entry e = LeapSeconds.list().entryFromUTC(instant.getEpochSeconds());
        return e.getNext() != null && instant.getEpochSeconds() == e.getNext().getStartEpochSeconds()-1 ?
            Validity.AMBIGUOUS : Validity.VALID;
    }

    //-----------------------------------------------------------------------
    /** {@inheritDoc} */
    public TimeScaleInstant add(TimeScaleInstant t, Duration d) {
        if (t.getTimeScale() != this) {
            return t.getTimeScale().add(t, d);
        }
        long seconds = d.getSeconds();
        int nanos = d.getNanosInSecond();
        if (seconds == 0 && nanos == 0) {
            return t;
        }
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

    /** {@inheritDoc} */
    public TimeScaleInstant subtract(TimeScaleInstant t, Duration d) {
        if (t.getTimeScale() != this) {
            return t.getTimeScale().subtract(t, d);
        }
        long seconds = d.getSeconds();
        int nanos = d.getNanosInSecond();
        if (seconds == 0 && nanos == 0) {
            return t;
        }
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

    //-----------------------------------------------------------------------
    /** {@inheritDoc} */
    public Duration durationBetween(TimeScaleInstant a, TimeScaleInstant b) {
        if (a.getTimeScale() != this) {
            a = toTimeScaleInstant(a);
        }
        if (b.getTimeScale() != this) {
            b = toTimeScaleInstant(b);
        }
        return ScaleUtil.durationBetween(a, b);
    }

}
