/*
 * Copyright (c) 2007, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.part;

import java.util.HashMap;
import java.util.Map;

import javax.math.Fraction;

/**
 * Time part of an hour.
 *
 * @author Stephen Colebourne
 */
public class Hour extends TimePart {

    /** The singleton instance. */
    public static final Hour PART = new Hour();
    /** The map of part to seconds in that part. */
    private static final Map<TimePart, Integer> CONVERSION_MAP = new HashMap<TimePart, Integer>();
    static {
        CONVERSION_MAP.put(Second.PART, 1);
        CONVERSION_MAP.put(Minute.PART, 60);
        CONVERSION_MAP.put(Hour.PART, 60);
        CONVERSION_MAP.put(Day.PART, 60);
        CONVERSION_MAP.put(Week.PART, 60);
        CONVERSION_MAP.put(Month.PART, 60);
        CONVERSION_MAP.put(Year.PART, 60);
    }

    //-----------------------------------------------------------------------
    /**
     * Singleton constructor.
     */
    private Hour() {
    }

    // TODO: readResolve

    //-----------------------------------------------------------------------
    /** {@inheritDoc} */
    @Override
    protected long getComparisonDurationSeconds() {
        return 3600;
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return "Hour";
    }

    /** {@inheritDoc} */
    @Override
    public Fraction getDurationRatio(TimePart otherPart) {
        return null;
    }

}
