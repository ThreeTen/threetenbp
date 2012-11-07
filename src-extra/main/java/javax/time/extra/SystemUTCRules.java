/*
 * Copyright (c) 2010-2012, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.extra;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.net.URL;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicReference;

import javax.time.jdk8.Jdk8Methods;

/**
 * System default UTC rules.
 * 
 * <h4>Implementation notes</h4>
 * This class is immutable and thread-safe.
 */
final class SystemUTCRules extends UTCRules implements Serializable {

    /**
     * Singleton.
     */
    static final SystemUTCRules INSTANCE = new SystemUTCRules();
    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 7594178360693417218L;

    /**
     * The table of leap second dates.
     */
    private AtomicReference<Data> dataRef = new AtomicReference<Data>(loadLeapSeconds());

    /** Data holder. */
    private static final class Data implements Serializable {
        /** Serialization version. */
       private static final long serialVersionUID = -3655687912882817265L;
        /** Constructor. */
        private Data(long[] dates, int[] offsets, long[] taiSeconds) {
            super();
            this.dates = dates;
            this.offsets = offsets;
            this.taiSeconds = taiSeconds;
        }
        /** The table of leap second date when the leap second occurs. */
        final long[] dates;
        /** The table of TAI offset after the leap second. */
        final int[] offsets;
        /** The table of TAI second when the new offset starts. */
        final long[] taiSeconds;
        
        /**
         * @return The modified Julian Date of the newest leap second 
         */
        public long getNewestDate() {
            return dates[dates.length - 1];
        }
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
     * @return the resolved instance, not null
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

    //-----------------------------------------------------------------------
    @Override
    protected UTCInstant convertToUTC(TAIInstant taiInstant) {
        Data data = dataRef.get();
        long[] mjds = data.dates;
        long[] tais = data.taiSeconds;
        int pos = Arrays.binarySearch(tais, taiInstant.getTAISeconds());
        pos = (pos >= 0 ? pos : ~pos - 1);
        int taiOffset = (pos >= 0 ? data.offsets[pos] : 10);
        long adjustedTaiSecs = taiInstant.getTAISeconds() - taiOffset;
        long mjd = Jdk8Methods.floorDiv(adjustedTaiSecs, SECS_PER_DAY) + OFFSET_MJD_TAI;
        long nod = Jdk8Methods.floorMod(adjustedTaiSecs, SECS_PER_DAY) * NANOS_PER_SECOND + taiInstant.getNano();
        long mjdNextRegionStart = (pos + 1 < mjds.length ? mjds[pos + 1] + 1 : Long.MAX_VALUE);
        if (mjd == mjdNextRegionStart) {  // in leap second
            mjd--;
            nod = SECS_PER_DAY * NANOS_PER_SECOND + (nod / NANOS_PER_SECOND) * NANOS_PER_SECOND + nod % NANOS_PER_SECOND;
        }
        return UTCInstant.ofModifiedJulianDay(mjd, nod, this);
    }

    //-----------------------------------------------------------------------
    /**
     * Loads the rules from files in the class loader, often jar files.
     *
     * @return the list of loaded rules, not null
     * @throws Exception if an error occurs
     */
    private static Data loadLeapSeconds() {
        Data bestData = null;
        URL url = null;
        try {
            Enumeration<URL> en = Thread.currentThread().getContextClassLoader().getResources("javax/time/LeapSecondRules.dat");
            while (en.hasMoreElements()) {
                url = en.nextElement();
                Data candidate = loadLeapSeconds(url);
                if (bestData == null || candidate.getNewestDate() > bestData.getNewestDate()) {
                    bestData = candidate;
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException("Unable to load time-zone rule data: " + url, ex);
        }
        if (bestData == null) {
            // no data on classpath, but we allow manual registration of leap seconds
            // setup basic known data - MJD 1972-01-01 is 41317L, where offset was 10
            bestData = new Data(new long[] {41317L}, new int[] {10}, new long[] {tai(41317L, 10)});
        }
        return bestData;
    }

    /**
     * Loads the leap second rules from a URL, often in a jar file.
     *
     * @param url  the jar file to load, not null 
     * @throws Exception if an error occurs
     */
    private static Data loadLeapSeconds(URL url) throws ClassNotFoundException, IOException {
        boolean throwing = false;
        InputStream in = null;
        try {
            in = url.openStream();
            DataInputStream dis = new DataInputStream(in);
            if (dis.readByte() != 1) {
                throw new StreamCorruptedException("File format not recognised");
            }
            int leaps = dis.readInt();
            long[] dates = new long[leaps];
            int[] offsets = new int[leaps];
            long[] taiSeconds = new long[leaps];
            for (int i = 0 ; i < leaps; ++i) {
                long changeMjd = dis.readLong();  // date leap second is added
                int offset = dis.readInt();
                dates[i] = changeMjd;
                offsets[i] = offset;
                taiSeconds[i] = tai(changeMjd, offset);
            }
            return new Data(dates, offsets, taiSeconds);
        } catch (IOException ex) {
            throwing = true;
            throw ex;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    if (throwing == false) {
                        throw ex;
                    }
                }
            }
        }
    }

    /**
     * Gets the TAI seconds for the start of the day following the day passed in.
     * 
     * @param changeMjd  the MJD that the leap second is added to
     * @param offset  the new offset after the leap
     * @return the TAI seconds
     */
    private static long tai(long changeMjd, int offset) {
        return (changeMjd + 1 - OFFSET_MJD_TAI) * SECS_PER_DAY + offset;
    }

}
