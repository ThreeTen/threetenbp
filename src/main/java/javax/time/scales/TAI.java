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
 * The International Atomic Time (TAI) time-scale.
 * <p>
 * This time-scale is the primary international scientific time-scale and is completely continuous.
 *
 * @author Mark Thornton
 */
class TAI implements TimeScale, Serializable {

    /**
     * Singleton instance.
     */
    static final TAI INSTANCE = new TAI();
    /**
     * Name of the singleton, used by other classes to avoid class-loading.
     */
    static final String NAME = "TAI";
    /**
     * The start of TAI.
     */
    static final TimeScaleInstant START_TAI = TimeScaleInstant.seconds(INSTANCE, ScaleUtil.START_TAI, 0);
    /**
     * The start of leap seconds.
     */
    static final TimeScaleInstant START_LEAP_SECONDS = TimeScaleInstant.seconds(INSTANCE, ScaleUtil.START_LEAP_SECONDS + 10, 0);
    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 1L;

    //-----------------------------------------------------------------------
    /**
     * Private constructor.
     */
    private TAI() {
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
    public Instant toInstant(TimeScaleInstant tsi) {
        if (tsi.getTimeScale() != this) {
            return tsi.getTimeScale().toInstant(tsi);
        }
        if (tsi.compareTo(START_LEAP_SECONDS) >= 0) {
            return toModernInstant(tsi);
        }
        if (tsi.compareTo(START_TAI) > 0) {
            return toEarlyInstant(tsi);
        }
        // finally ancient history
        return Instant.ofSeconds(tsi.getEpochSeconds(), tsi.getNanoOfSecond());
    }

    private Instant toModernInstant(TimeScaleInstant tsi) {
        LeapSeconds.Entry e = LeapSeconds.list().entryFromTAI(tsi);
        long s = MathUtils.safeSubtract(tsi.getEpochSeconds(), e.getDeltaSeconds());
        if (e.getNext() != null && s == e.getNext().getStartEpochSeconds()) {
            // repeat the last second
            s--;
        }
        return Instant.ofSeconds(s, tsi.getNanoOfSecond());
    }

    private Instant toEarlyInstant(TimeScaleInstant tsi) {
        EarlyUTC_TAI.Entry e = EarlyUTC_TAI.list().entryFromTAI(tsi);
        long nanos = tsi.getNanoOfSecond() - e.getTAIDeltaNanoseconds(tsi.getEpochSeconds(), tsi.getNanoOfSecond());
        long s = MathUtils.safeAdd(tsi.getEpochSeconds(), nanos/ScaleUtil.NANOS_PER_SECOND);
        nanos = nanos % ScaleUtil.NANOS_PER_SECOND;
        if (nanos < 0) {
            s--;    // safe because the range is known
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
        return Instant.ofSeconds(s, (int)nanos);
    }

    /** {@inheritDoc} */
    public TimeScaleInstant toTAI(TimeScaleInstant tsi) {
        if (tsi.getTimeScale() != this) {
            return tsi.getTimeScale().toTAI(tsi);
        }
        return tsi;
    }

    /** {@inheritDoc} */
    public TimeScaleInstant toTimeScaleInstant(InstantProvider provider) {
        Instant t = Instant.from(provider);
        return ScaleUtil.tai(t.getEpochSeconds(), t.getNanoOfSecond());
    }

    /** {@inheritDoc} */
    public TimeScaleInstant toTimeScaleInstant(TimeScaleInstant tsi) {
        if (tsi.getTimeScale() != this) {
            return tsi.getTimeScale().toTAI(tsi);
        }
        return tsi;
    }

    //-----------------------------------------------------------------------
    /** {@inheritDoc} */
    public Validity getValidity(TimeScaleInstant tsi) {
        if (tsi.getTimeScale() != this) {
            return tsi.getTimeScale().getValidity(tsi);
        }
        return Validity.VALID;
    }

    //-----------------------------------------------------------------------
    /** {@inheritDoc} */
    public TimeScaleInstant add(TimeScaleInstant tsi, Duration dur) {
        if (tsi.getTimeScale() != this) {
            return tsi.getTimeScale().add(tsi, dur);
        }
        return ScaleUtil.simpleAdd(tsi, dur);
    }

    /** {@inheritDoc} */
    public TimeScaleInstant subtract(TimeScaleInstant tsi, Duration dur) {
        if (tsi.getTimeScale() != this) {
            return tsi.getTimeScale().subtract(tsi, dur);
        }
        return ScaleUtil.simpleSubtract(tsi, dur);
    }

    //-----------------------------------------------------------------------
    /** {@inheritDoc} */
    public Duration durationBetween(TimeScaleInstant start, TimeScaleInstant end) {
        if (start.getTimeScale() != this) {
            start = start.getTimeScale().toTAI(start);
        }
        if (end.getTimeScale() != this) {
            end = end.getTimeScale().toTAI(end);
        }
        return ScaleUtil.durationBetween(start, end);
    }

}
