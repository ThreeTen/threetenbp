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

import java.io.ObjectStreamException;
import java.io.Serializable;
import javax.time.Instant;
import javax.time.InstantProvider;
import javax.time.MathUtils;
import javax.time.TimeScale;
import javax.time.TimeScaleInstant;
import javax.time.TimeScaleInstant.Validity;

/** International Atomic Time.
 *
 * @author Mark Thornton
 */
public class TAI implements TimeScale, Serializable {
    private static final long serialVersionUID = 1;
    public static final String NAME = "TAI";
    public static final TAI INSTANCE = new TAI();
    public static final TimeScaleInstant START_TAI = TimeScaleInstant.seconds(INSTANCE, ScaleUtil.START_TAI, 0);
    public static final TimeScaleInstant START_LEAPSECONDS = TimeScaleInstant.seconds(INSTANCE, ScaleUtil.START_LEAP_SECONDS+10, 0);

    private TAI() {
        // ensure single instance
    }

    private Object readResolve()
    		throws ObjectStreamException {
        return INSTANCE;
    }

    public Instant toInstant(TimeScaleInstant tsInstant) {
        if (tsInstant.getTimeScale() != this) {
            return tsInstant.getTimeScale().toInstant(tsInstant);
        }
        if (tsInstant.compareTo(START_LEAPSECONDS) >= 0) {
            return toModernInstant(tsInstant);
        }
        if (tsInstant.compareTo(START_TAI) > 0) {
            return toEarlyInstant(tsInstant);
        }
        // finally ancient history
        return Instant.seconds(tsInstant.getEpochSeconds(), tsInstant.getNanoOfSecond());
    }

    private Instant toModernInstant(TimeScaleInstant tsInstant) {
        LeapSeconds.Entry e = LeapSeconds.list().entryFromTAI(tsInstant);
        long s = MathUtils.safeSubtract(tsInstant.getEpochSeconds(), e.getDeltaSeconds());
        if (e.getNext() != null && s == e.getNext().getStartEpochSeconds()) {
            // repeat the last second
            s--;
        }
        return Instant.seconds(s, tsInstant.getNanoOfSecond());
    }

    private Instant toEarlyInstant(TimeScaleInstant tsInstant) {
        EarlyUTC_TAI.Entry e = EarlyUTC_TAI.list().entryFromTAI(tsInstant);
        long nanos = tsInstant.getNanoOfSecond() - e.getTAIDeltaNanoseconds(tsInstant.getEpochSeconds(), tsInstant.getNanoOfSecond());
        long s = MathUtils.safeAdd(tsInstant.getEpochSeconds(), nanos/ScaleUtil.NANOS_PER_SECOND);
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
        return Instant.seconds(s, (int)nanos);
    }

    public TimeScaleInstant toTimeScaleInstant(InstantProvider instantProvider) {
        Instant t = instantProvider.toInstant();
        return ScaleUtil.tai(t.getEpochSeconds(), t.getNanoOfSecond());
    }

    public TimeScaleInstant toTAI(TimeScaleInstant src) {
        if (src.getTimeScale() == this) {
            return src;
        }
        return src.getTimeScale().toTAI(src);
    }

    public TimeScaleInstant toTimeScaleInstant(TimeScaleInstant src) {
        return src.getTimeScale() == this ? src : src.getTimeScale().toTAI(src);
    }

    public String getName() {
        return NAME;
    }

    /** TAI times are always valid.
     * The TAI timeline has no discontinuities or ambiguities.
     * @param instant if the instant is not on the TAI time scale it will be forwarded to its time scale
     * to evaluate validity
     * @return valid for TAI
     */
    public Validity getValidity(TimeScaleInstant instant) {
        return instant.getTimeScale() == this ? Validity.valid : instant.getTimeScale().getValidity(instant);
    }

    public boolean supportsLeapSecond() {
        return false;
    }
}
