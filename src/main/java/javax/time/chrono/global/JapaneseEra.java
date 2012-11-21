/*
 * Copyright (c) 2012, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.chrono.global;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Arrays;

import javax.time.DateTimeException;
import javax.time.LocalDate;
import javax.time.jdk8.DefaultInterfaceEra;

import sun.util.calendar.CalendarDate;

/**
 * An era in the Japanese Imperial calendar system.
 * <p>
 * This class defines the valid eras for the Japanese chronology.
 * Only Meiji (1868-09-08 - 1912-07-29) and later eras are supported.
 * Japan introduced the Gregorian calendar since Meiji 6. The dates
 * between Meiji 1 - 5 are not historically correct.
 * The older eras are recognized as Seireki (Western calendar) era,
 * and the year of era of Seireki is proleptic Gregorian year.
 * (The Julian to Gregorian transition is not supported.)
 *
 * <h4>Implementation notes</h4>
 * This class is immutable and thread-safe.
 */
final class JapaneseEra
        extends DefaultInterfaceEra<JapaneseChrono>
        implements Serializable {

    // The offset value to 0-based index from the era value.
    // i.e., getValue() + ERA_OFFSET == 0-based index
    static final int ERA_OFFSET = 2;

    static final sun.util.calendar.Era[] ERA_CONFIG;

    /**
     * The singleton instance for the before Meiji era ( - 1868-09-07)
     * which has the value -2.
     */
    public static final JapaneseEra SEIREKI = new JapaneseEra(-2, LocalDate.MIN_DATE);
    /**
     * The singleton instance for the 'Meiji' era (1868-09-08 - 1912-07-29)
     * which has the value -1.
     */
    public static final JapaneseEra MEIJI = new JapaneseEra(-1, LocalDate.of(1868, 9, 8));
    /**
     * The singleton instance for the 'Taisho' era (1912-07-30 - 1926-12-24)
     * which has the value 0.
     */
    public static final JapaneseEra TAISHO = new JapaneseEra(0, LocalDate.of(1912, 7, 30));
    /**
     * The singleton instance for the 'Showa' era (1926-12-25 - 1989-01-07)
     * which has the value 1.
     */
    public static final JapaneseEra SHOWA = new JapaneseEra(1, LocalDate.of(1926, 12, 25));
    /**
     * The singleton instance for the 'Heisei' era (1989-01-08 - current)
     * which has the value 2.
     */
    public static final JapaneseEra HEISEI = new JapaneseEra(2, LocalDate.of(1989, 1, 8));

    // the number of defined JapaneseEra constants.
    // There could be an extra era defined in its configuration.
    private static final int N_ERA_CONSTANTS = HEISEI.getValue() + ERA_OFFSET + 1;

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 1466499369062886794L;

    // array for the singleton JapaneseEra instances
    private static final JapaneseEra[] KNOWN_ERAS;

    static {
        sun.util.calendar.Era[] sunEras = JapaneseChrono.JCAL.getEras();
        ERA_CONFIG = new sun.util.calendar.Era[sunEras.length + 1];
        for (int i = 1; i < ERA_CONFIG.length; i++) {
            ERA_CONFIG[i] = sunEras[i - 1];
        }
        KNOWN_ERAS = new JapaneseEra[ERA_CONFIG.length];
        KNOWN_ERAS[0] = SEIREKI;
        KNOWN_ERAS[1] = MEIJI;
        KNOWN_ERAS[2] = TAISHO;
        KNOWN_ERAS[3] = SHOWA;
        KNOWN_ERAS[4] = HEISEI;
        for (int i = N_ERA_CONSTANTS; i < ERA_CONFIG.length; i++) {
            CalendarDate date = ERA_CONFIG[i].getSinceDate();
            LocalDate isoDate = LocalDate.of(date.getYear(), date.getMonth(), date.getDayOfMonth());
            KNOWN_ERAS[i] = new JapaneseEra(i - ERA_OFFSET, isoDate);
        }
    };

    /**
     * The era value.
     * @serial
     */
    private final int eraValue;

    // the first day of the era
    private final transient LocalDate since;

    /**
     * Creates an instance.
     *
     * @param eraValue  the era value, validated
     * @param since  the date representing the first date of the era, validated not null
     */
    private JapaneseEra(int eraValue, LocalDate since) {
        this.eraValue = eraValue;
        this.since = since;
    }

    /**
     * Returns the singleton {@code JapaneseEra} corresponding to this object.
     * It's possible that this version of {@code JapaneseEra} doesn't support the latest era value.
     * In that case, this method throws an {@code ObjectStreamException}.
     *
     * @return the singleton {@code JapaneseEra} for this object
     * @throws ObjectStreamException if the deserialized object has any unknown numeric era value.
     */
    private Object readResolve() throws ObjectStreamException {
        try {
            return of(eraValue);
        } catch (DateTimeException e) {
            InvalidObjectException ex = new InvalidObjectException("Invalid era");
            ex.initCause(e);
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the Sun private Era instance corresponding to this {@code JapaneseEra}.
     * SEIREKI doesn't have its corresponding one.
     *
     * @return the Sun private Era instance for this {@code JapaneseEra},
     *         or null for SEIREKI.
     */
    sun.util.calendar.Era getPrivateEra() {
        return ERA_CONFIG[eraValue + ERA_OFFSET];
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code JapaneseEra} from a value.
     * <p>
     * The {@link #SHOWA} era that contains 1970-01-01 (ISO calendar system) has the value 1
     * Later era is numbered 2 ({@link #HEISEI}). Earlier eras are numbered 0 ({@link #TAISHO}),
     * -1 ({@link #MEIJI}), only Meiji and later eras are supported. The prior to Meiji,
     * {@link #SEIREKI} is used.
     *
     * @param japaneseEra  the era to represent
     * @return the {@code JapaneseEra} singleton, never null
     * @throws DateTimeException if {@code japaneseEra} is invalid
     */
    public static JapaneseEra of(int japaneseEra) {
        int index = japaneseEra + ERA_OFFSET;
        if (index < 0 || index >= KNOWN_ERAS.length) {
            throw new DateTimeException("japaneseEra is invalid");
        }
        return KNOWN_ERAS[index];
    }

    /**
     * Returns an array of JapaneseEras.
     * @return an array of JapaneseEras
     */
    static JapaneseEra[] values() {
        return Arrays.copyOf(KNOWN_ERAS, KNOWN_ERAS.length);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code JapaneseEra} from a date.
     *
     * @param date  the date, not null
     * @return the Era singleton, never null
     */
    static JapaneseEra from(LocalDate date) {
        for (int i = KNOWN_ERAS.length - 1; i > 0; i--) {
            JapaneseEra era = KNOWN_ERAS[i];
            if (date.compareTo(era.since) >= 0) {
                return era;
            }
        }
        return SEIREKI;
    }

    static JapaneseEra toJapaneseEra(sun.util.calendar.Era privateEra) {
        for (int i = ERA_CONFIG.length - 1; i > 0; i--) {
            if (ERA_CONFIG[i].equals(privateEra)) {
                return KNOWN_ERAS[i];
            }
        }
        return SEIREKI;
    }

    static sun.util.calendar.Era privateEraFrom(LocalDate isoDate) {
        for (int i = KNOWN_ERAS.length - 1; i > 0; i--) {
            JapaneseEra era = KNOWN_ERAS[i];
            if (isoDate.compareTo(era.since) >= 0) {
                return ERA_CONFIG[i];
            }
        }
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the numeric value of this {@code JapaneseEra}.
     * <p>
     * The {@link #SHOWA} era that contains 1970-01-01 (ISO calendar system) has the value 1.
     * Later eras are numbered from 2 ({@link #HEISEI}).
     * Earlier eras are numbered 0 ({@link #TAISHO}), -1 ({@link #MEIJI}), and -2 ({@link #SEIREKI}).
     *
     * @return the era value
     */
    @Override
    public int getValue() {
        return eraValue;
    }

    @Override
    public JapaneseChrono getChrono() {
        return JapaneseChrono.INSTANCE;
    }

    //-----------------------------------------------------------------------
    String getAbbreviation() {
        int index = getValue() + ERA_OFFSET;
        if (index == 0) {
            return "";
        }
        return ERA_CONFIG[index].getAbbreviation();
    }

    @Override
    public String toString() {
        return getAbbreviation();
    }

    //-----------------------------------------------------------------------
    private Object writeReplace() {
        return new Ser(Ser.JAPANESE_ERA_TYPE, this);
    }

    void writeExternal(DataOutput out) throws IOException {
        out.writeByte(this.getValue());
    }

    static JapaneseEra readExternal(DataInput in) throws IOException {
        byte eraValue = in.readByte();
        return JapaneseEra.of(eraValue);
    }

}
