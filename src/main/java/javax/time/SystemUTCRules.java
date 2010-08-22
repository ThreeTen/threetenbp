/*
 * Copyright (c) 2010, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicReference;

import javax.time.calendar.LocalDate;

/**
 * System default UTC rules.
 * <p>
 * SystemUTCRules is immutable and thread-safe.
 *
 * @author Stephen Colebourne
 */
final class SystemUTCRules extends UTCRules implements Serializable {

    /**
     * Singleton.
     */
    static SystemUTCRules INSTANCE = new SystemUTCRules();
    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The table of leap second dates.
     */
    private AtomicReference<Data> dataRef = new AtomicReference<Data>(loadLeapSeconds());

    /** Data holder. */
    private static class Data implements Serializable {
        /** Serialization version. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private Data(long[] dates, int[] offsets, long[] taiSeconds) {
            super();
            this.dates = dates;
            this.offsets = offsets;
            this.taiSeconds = taiSeconds;
        }
        /** The table of leap second date when the leap second occurs. */
        private long[] dates;
        /** The table of TAI offset after the leap second. */
        private int[] offsets;
        /** The table of TAI second when the new offset starts. */
        private long[] taiSeconds;
    }

    //-----------------------------------------------------------------------
    /**
     * Restricted constructor.
     */
    private SystemUTCRules() {
    }

    /**
     * Resolves singleton.
     *
     * @return the resolved instance, never null
     */
    private Object readResolve() {
        return INSTANCE;
    }

    //-----------------------------------------------------------------------
    /**
     * Adds a new leap second to these rules.
     *
     * @param mjDay  the modified julian date that the leap second occurs at the end of
     * @param leapAdjustment  the leap seconds to add/remove at the end of the day, either -1 or 1
     * @throws IllegalArgumentException if the leap adjustment is invalid
     * @throws IllegalArgumentException if the day is before or equal the last known leap second day
     *  and the definition does not match a previously registered leap
     * @throws ConcurrentModificationException if another thread updates the rules at the same time
     */
    void registerLeapSecond(long mjDay, int leapAdjustment) {
        if (leapAdjustment != -1 && leapAdjustment != 1) {
            throw new IllegalArgumentException("Leap adjustment must be -1 or 1");
        }
        Data data = dataRef.get();
        int pos = Arrays.binarySearch(data.dates, mjDay);
        int currentAdj = pos > 0 ? data.offsets[pos] - data.offsets[pos - 1] : 0;
        if (currentAdj == leapAdjustment) {
            return;  // matches previous definition
        }
        if (mjDay <= data.dates[data.dates.length - 1]) {
            throw new IllegalArgumentException("Date must be after the last configured leap second date");
        }
        long[] dates = Arrays.copyOf(data.dates, data.dates.length + 1);
        int[] offsets = Arrays.copyOf(data.offsets, data.offsets.length + 1);
        long[] taiSeconds = Arrays.copyOf(data.taiSeconds, data.taiSeconds.length + 1);
        int offset = offsets[offsets.length - 2] + leapAdjustment;
        dates[dates.length - 1] = mjDay;
        offsets[offsets.length - 1] = offset;
        taiSeconds[taiSeconds.length - 1] = tai(mjDay, offset);
        Data newData = new Data(dates, offsets, taiSeconds);
        if (dataRef.compareAndSet(data, newData) == false) {
            throw new ConcurrentModificationException("Unable to update leap second rules as they have already been updated");
        }
    }

    //-----------------------------------------------------------------------
    @Override
    public String getName() {
        return "System";
    }

    @Override
    public int getLeapSecondAdjustment(long mjDay) {
        Data data = dataRef.get();
        int pos = Arrays.binarySearch(data.dates, mjDay);
        return pos > 0 ? data.offsets[pos] - data.offsets[pos - 1] : 0;
    }

    @Override
    public int getTAIOffset(long mjDay) {
        Data data = dataRef.get();
        int pos = Arrays.binarySearch(data.dates, mjDay);
        pos = (pos < 0 ? ~pos : pos);
        return pos > 0 ? data.offsets[pos - 1] : 10;
    }

    @Override
    public long[] getLeapSecondDates() {
        Data data = dataRef.get();
        return data.dates.clone();
    }

    //-------------------------------------------------------------------------
    @Override
    protected UTCInstant convertToUTC(TAIInstant taiInstant) {
        Data data = dataRef.get();
        long[] mjds = data.dates;
        long[] tais = data.taiSeconds;
        int pos = Arrays.binarySearch(tais, taiInstant.getTAISeconds());
        pos = (pos >= 0 ? pos : ~pos - 1);
        int taiOffset = (pos >= 0 ? data.offsets[pos] : 10);
        long adjustedTaiSecs = taiInstant.getTAISeconds() - taiOffset;
        long mjd = MathUtils.floorDiv(adjustedTaiSecs, SECS_PER_DAY) + OFFSET_MJD_TAI;
        long nod = MathUtils.floorMod(adjustedTaiSecs, SECS_PER_DAY) * NANOS_PER_SECOND + taiInstant.getNanoOfSecond();
        long mjdNextRegionStart = (pos + 1 < mjds.length ? mjds[pos + 1] + 1 : Long.MAX_VALUE);
        if (mjd == mjdNextRegionStart) {  // in leap second
            mjd--;
            nod = SECS_PER_DAY * NANOS_PER_SECOND + (nod / NANOS_PER_SECOND) * NANOS_PER_SECOND + nod % NANOS_PER_SECOND;
        }
        return UTCInstant.ofModifiedJulianDays(mjd, nod, this);
    }

    //-------------------------------------------------------------------------
    /**
     * Loads the leap seconds from file.
     * @return an array of two arrays - leap seconds dates and amounts
     */
    private static Data loadLeapSeconds() {
        InputStream in = SystemUTCRules.class.getResourceAsStream("/javax/time/LeapSeconds.txt");
        if (in == null) {
            throw new CalendricalException("LeapSeconds.txt resource missing");
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            Map<Long, Integer> leaps = new TreeMap<Long, Integer>();
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                line = line.trim();
                if (line.length() > 0 && line.charAt(0) != '#') {
                    String[] split = line.split(" ");
                    if (split.length != 2) {
                        throw new CalendricalException("LeapSeconds.txt has invalid line format");
                    }
                    LocalDate date = LocalDate.parse(split[0]);
                    int offset = Integer.parseInt(split[1]);
                    leaps.put(date.toModifiedJulianDays(), offset);
                }
            }
            long[] dates = new long[leaps.size()];
            int[] offsets = new int[leaps.size()];
            long[] taiSeconds = new long[leaps.size()];
            int i = 0;
            for (Entry<Long, Integer> entry : leaps.entrySet()) {
                long changeMjd = entry.getKey() - 1;  // subtract one to get date leap second is added
                int offset = entry.getValue();
                if (i > 0) {
                    int adjust = offset - offsets[i - 1];
                    if (adjust < -1 || adjust > 1) {
                        throw new CalendricalException("Leap adjustment must be -1 or 1");
                    }
                }
                dates[i] = changeMjd;
                offsets[i] = offset;
                taiSeconds[i++] = tai(changeMjd, offset);
            }
            return new Data(dates, offsets, taiSeconds);
        } catch (IOException ex) {
            try {
                in.close();
            } catch (IOException ignored) {
                // ignore
            }
            throw new CalendricalException("Exception reading LeapSeconds.txt", ex);
        }
    }

    /**
     * Gets the TAI seconds for the start of the day following the day passed in.
     * @param changeMjd  the MJD that the leap second is added to
     * @param offset  the new offset after the leap
     * @return the TAI seconds
     */
    private static long tai(long changeMjd, int offset) {
        return (changeMjd + 1 - OFFSET_MJD_TAI) * SECS_PER_DAY + offset;
    }

}
