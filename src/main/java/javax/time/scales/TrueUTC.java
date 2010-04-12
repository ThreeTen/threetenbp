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
 * The Universal Coordinated Time (UTC) time-scale, with leap seconds.
 * <p>
 * The true UTC time-scale differs from the TAI time-scale by an integral
 * number of seconds (although see below for a historical note).
 * These "leap seconds" are introduced to ensure that the instant represented
 * by UTC stays in line with the earths rotational changes.
 * <p>
 * Unlike many software implementations of UTC, this time-scale fully supports leap seconds.
 * It correctly implements the relationship between TAI and UTC between 1961 and 1972
 * where the UTC second did not correspond to an SI second and there were
 * a number of small discontinuities (of 50ms or 100ms). This has been further
 * extended back to 1958 where UT and TAI where equal. In the period 1958 to
 * 1961 adjustments where not coordinated internationally, hence there is no
 * single definitive record of the relationship between TAI and UT.
 *
 * @author Mark Thornton
 */
class TrueUTC implements TimeScale, Serializable {

    /**
     * Singleton instance.
     */
    static final TrueUTC INSTANCE = new TrueUTC();
    /**
     * Name of the singleton, used by other classes to avoid class-loading.
     */
    static final String NAME = "TrueUTC";
    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 1L;

    //-----------------------------------------------------------------------
    /**
     * Private constructor.
     */
    private TrueUTC() {
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
        return true;
    }

    //-----------------------------------------------------------------------
    /** {@inheritDoc} */
    public Instant toInstant(TimeScaleInstant tsi) {
        if (tsi.getTimeScale() != this) {
            return tsi.getTimeScale().toInstant(tsi);
        }
        // just lose the leap second (if any)
        return Instant.ofSeconds(tsi.getEpochSeconds(), tsi.getNanoOfSecond());
    }

    /** {@inheritDoc} */
    public TimeScaleInstant toTAI(TimeScaleInstant tsi) {
        if (tsi.getTimeScale() != this) {
            return tsi.getTimeScale().toTAI(tsi);
        }
        return ScaleUtil.tai(tsi.getEpochSeconds(), tsi.getLeapSecond(), tsi.getNanoOfSecond());
    }

    /** {@inheritDoc} */
    public TimeScaleInstant toTimeScaleInstant(InstantProvider provider) {
        Instant t = Instant.from(provider);
        return TimeScaleInstant.seconds(this, t.getEpochSeconds(), t.getNanoOfSecond());
    }

    /** {@inheritDoc} */
    public TimeScaleInstant toTimeScaleInstant(TimeScaleInstant tsi) {
        if (tsi.getTimeScale() == this) {
            return tsi;
        }
        String name = tsi.getTimeScale().getName();
        // the following tests do not cause the classes to be loaded
        // the idea is to allow conversions between UTC and TrueUTC without loading TAI.
        // the use of == is also valid as the constants are interned by the JVM
        if (name == UTC.NAME) {
            return TimeScaleInstant.seconds(this, tsi.getEpochSeconds(), tsi.getNanoOfSecond());
        }
        if (name != TAI.NAME) {
            tsi = tsi.getTimeScale().toTAI(tsi);
        }
        return fromTAI(tsi);
    }

    private TimeScaleInstant fromTAI(TimeScaleInstant tsi) {
        if (tsi.compareTo(TAI.START_LEAP_SECONDS) >= 0) {
            return fromModernTAI(tsi);
        }
        if (tsi.compareTo(TAI.START_TAI) > 0) {
            return fromEarlyTAI(tsi);
        }
        // ancient, identical to TAI
        return TimeScaleInstant.seconds(this, tsi.getEpochSeconds(), tsi.getNanoOfSecond());
    }

    private TimeScaleInstant fromModernTAI(TimeScaleInstant tsi) {
        LeapSeconds.Entry e = LeapSeconds.list().entryFromTAI(tsi);
        long s = tsi.getEpochSeconds() - e.getDeltaSeconds();
        int leapSecond;
        if (e.getNext() != null && s >= e.getNext().getStartEpochSeconds()) {
            leapSecond = 1 + (int)(s-e.getNext().getStartEpochSeconds());
            s -= leapSecond;
        } else {
            leapSecond = 0;
        }
        return TimeScaleInstant.seconds(this, s, leapSecond, tsi.getNanoOfSecond());
    }

    private TimeScaleInstant fromEarlyTAI(TimeScaleInstant tsi) {
        EarlyUTC_TAI.Entry e = EarlyUTC_TAI.list().entryFromTAI(tsi);
        long nanos = tsi.getNanoOfSecond() - e.getTAIDeltaNanoseconds(tsi.getEpochSeconds(), tsi.getNanoOfSecond());
        long s = MathUtils.safeAdd(tsi.getEpochSeconds(), nanos/ScaleUtil.NANOS_PER_SECOND);
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
    public Validity getValidity(TimeScaleInstant tsi) {
        if (tsi.getTimeScale() != this) {
            return tsi.getTimeScale().getValidity(tsi);
        }
        if (tsi.getLeapSecond() != 0) {
            return tsi.getEpochSeconds() <= ScaleUtil.START_LEAP_SECONDS ? Validity.INVALID :
                checkLeapSecondValidity(tsi);
        }
        return tsi.getEpochSeconds() < ScaleUtil.START_LEAP_SECONDS && tsi.getEpochSeconds() > ScaleUtil.START_TAI ?
            ScaleUtil.checkEarlyValidity(tsi) : Validity.VALID;
    }

    private Validity checkLeapSecondValidity(TimeScaleInstant tsi) {
        if (tsi.getEpochSeconds() >= LeapSeconds.getNextPossibleLeap()) {
            // should verify that leap is at end of June or December
            return Validity.POSSIBLE;
        }
        LeapSeconds.Entry e = LeapSeconds.list().entryFromUTC(tsi.getEpochSeconds());
        return (e.getNext() != null && tsi.getEpochSeconds() == e.getNext().getStartEpochSeconds()-1) ?
            Validity.VALID : Validity.INVALID;
    }

    //-----------------------------------------------------------------------
    /** {@inheritDoc} */
    public TimeScaleInstant add(TimeScaleInstant tsi, Duration dur) {
        if (tsi.getTimeScale() != this) {
            return tsi.getTimeScale().add(tsi, dur);
        }
        long seconds = dur.getSeconds();
        int nanos = dur.getNanosInSecond();
        if (seconds == 0 && nanos == 0) {
            return tsi;
        }
        seconds = MathUtils.safeAdd(tsi.getEpochSeconds(), seconds);
        if (tsi.getLeapSecond() != 0) {
            seconds = MathUtils.safeIncrement(seconds);
        }
        nanos += tsi.getNanoOfSecond();
        if (nanos >= ScaleUtil.NANOS_PER_SECOND) {
            nanos -= ScaleUtil.NANOS_PER_SECOND;
            seconds = MathUtils.safeDecrement(seconds);
        }
        if (seconds < ScaleUtil.START_TAI && tsi.getEpochSeconds() < ScaleUtil.START_TAI) {
            // no adjustments required in this interval
            return TimeScaleInstant.seconds(this, seconds, nanos);
        }
        return adjustResult(tsi, seconds, nanos);
    }

    private TimeScaleInstant adjustResult(TimeScaleInstant tsi, long resultEpochSeconds, int resultNanoOfSecond) {
        LeapSeconds.Entry et = tsi.getEpochSeconds() <= ScaleUtil.START_LEAP_SECONDS ?
            LeapSeconds.list().get(0) :
            LeapSeconds.list().entryFromUTC(tsi.getEpochSeconds());
        resultEpochSeconds += et.getDeltaSeconds(); // now in TAI
        LeapSeconds.Entry e = resultEpochSeconds <= ScaleUtil.TAI_START_LEAP_SECONDS ?
            LeapSeconds.list().get(0) :
            LeapSeconds.list().entryFromTAI(resultEpochSeconds, resultNanoOfSecond);
        // convert result back to UTC
        resultEpochSeconds -= e.getDeltaSeconds();
        int leapSecond;
        if (e.getNext() != null && resultEpochSeconds >= e.getNext().getStartEpochSeconds()) {
            resultEpochSeconds--;
            leapSecond = 1;
        } else {
            leapSecond = 0;
            if (resultEpochSeconds < ScaleUtil.START_LEAP_SECONDS && resultEpochSeconds > ScaleUtil.START_TAI) {
                return ScaleUtil.adjustUTCAroundGaps(tsi, resultEpochSeconds, resultNanoOfSecond);
            }
        }
        return TimeScaleInstant.seconds(this, resultEpochSeconds, leapSecond, resultNanoOfSecond);
    }

    //-----------------------------------------------------------------------
    /** {@inheritDoc} */
    public TimeScaleInstant subtract(TimeScaleInstant tsi, Duration dur) {
        if (tsi.getTimeScale() != this) {
            return tsi.getTimeScale().subtract(tsi, dur);
        }
        long seconds = dur.getSeconds();
        int nanos = dur.getNanosInSecond();
        if (seconds == 0 && nanos == 0) {
            return tsi;
        }
        seconds = MathUtils.safeSubtract(tsi.getEpochSeconds(), seconds);
        if (tsi.getLeapSecond() != 0) {
            seconds = MathUtils.safeIncrement(seconds);
        }
        nanos = tsi.getNanoOfSecond() - nanos;
        if (nanos < 0) {
            nanos += ScaleUtil.NANOS_PER_SECOND;
            seconds = MathUtils.safeDecrement(seconds);
        }
        if (seconds < ScaleUtil.START_TAI && tsi.getEpochSeconds() < ScaleUtil.START_TAI) {
            return TimeScaleInstant.seconds(tsi.getTimeScale(), seconds, nanos);
        }
        return adjustResult(tsi, seconds, nanos);
    }

    private int differenceAdjust(TimeScaleInstant tsi) {
        return (tsi.getEpochSeconds() <= ScaleUtil.START_LEAP_SECONDS ? 10 :
            LeapSeconds.list().entryFromUTC(tsi.getEpochSeconds()).getDeltaSeconds()) + tsi.getLeapSecond();
    }

    //-----------------------------------------------------------------------
    /** {@inheritDoc} */
    public Duration durationBetween(TimeScaleInstant start, TimeScaleInstant end) {
        if (start.getTimeScale() != this) {
            start = toTimeScaleInstant(start);
        }
        if (end.getTimeScale() != this) {
            end = toTimeScaleInstant(end);
        }
        long secs = MathUtils.safeSubtract(end.getEpochSeconds(), start.getEpochSeconds());
        int nanos = end.getNanoOfSecond() - start.getNanoOfSecond();
        if (nanos < 0) {
            nanos += ScaleUtil.NANOS_PER_SECOND;
            secs = MathUtils.safeDecrement(secs);
        }
        int adjust = differenceAdjust(end)- differenceAdjust(start);
        if (adjust != 0) {
            secs = MathUtils.safeAdd(secs, adjust);
        }
        return Duration.seconds(secs, nanos);
    }

}
