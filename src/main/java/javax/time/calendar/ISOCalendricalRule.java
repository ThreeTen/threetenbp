/*
 * Copyright (c) 2011 Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.calendar;

import static javax.time.calendar.ISODateTimeRule.MILLI_OF_DAY;
import static javax.time.calendar.ISODateTimeRule.MINUTE_OF_DAY;
import static javax.time.calendar.ISODateTimeRule.NANO_OF_DAY;
import static javax.time.calendar.ISODateTimeRule.SECOND_OF_DAY;

import java.io.Serializable;

import javax.time.MathUtils;

/**
 * Internal class supplying the rules for the principal date and time objects.
 * <p>
 * {@code ISOCalendricalRule} provides the rules for classes like {@code LocalDate}
 * and {@code ZoneOffset}. This class is package private. Rules should be accessed
 * using the {@code rule()} method on each type, such as {@code LocalDate.rule()}.
 * <p>
 * Normally, a rule would be written as a small static nested class within the main
 * class, such as {@code LocalDate}. This class exists to avoid writing those
 * separate classes, centralizing the singleton pattern and enhancing performance
 * via an {@code int} ordinal and package scope.
 * Thus, this design is an optimization and should not necessarily be considered best practice.
 * <p>
 * This class is final, immutable and thread-safe.
 *
 * @param <T> the rule type
 * @author Stephen Colebourne
 */
final class ISOCalendricalRule<T> extends CalendricalRule<T> implements Serializable {

    /**
     * The serialization version.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Ordinal for performance and serialization.
     */
    final int ordinal;

    /**
     * Constructor used to create a rule.
     *
     * @param type  the type, not null
     * @param ordinal  the ordinal, not null
     */
    protected ISOCalendricalRule(Class<T> type, int ordinal) {
        super(type, type.getSimpleName());
        this.ordinal = ordinal;
    }

    /**
     * Deserialize singletons.
     * 
     * @return the resolved value, not null
     */
    private Object readResolve() {
        return RULE_CACHE[ordinal];
    }

    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    @Override
    protected T deriveFrom(CalendricalNormalizer merger) {
        switch (ordinal) {
            case LOCAL_DATE_ORDINAL: return (T) merger.getDate(true);
            case LOCAL_TIME_ORDINAL: return (T) merger.getTime(false);
            case LOCAL_DATE_TIME_ORDINAL: return (T) LocalDateTime.deriveFrom(merger);
            case OFFSET_DATE_ORDINAL: return (T) OffsetDate.deriveFrom(merger);
            case OFFSET_TIME_ORDINAL: return (T) OffsetTime.deriveFrom(merger);
            case OFFSET_DATE_TIME_ORDINAL: return (T) OffsetDateTime.deriveFrom(merger);
            case ZONED_DATE_TIME_ORDINAL: return (T) ZonedDateTime.deriveFrom(merger);
            case ZONE_OFFSET_ORDINAL: return (T) merger.getOffset(true);
            case ZONE_ID_ORDINAL: return (T) merger.getZone(true);
            case CHRONOLOGY_ORDINAL: return (T) merger.getChronology(true);
        }
        return null;
    }

    //-----------------------------------------------------------------------
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ISOCalendricalRule<?>) {
            return ordinal == ((ISOCalendricalRule<?>) obj).ordinal;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return ISOCalendricalRule.class.hashCode() + ordinal;
    }

    //-----------------------------------------------------------------------
    static final int LOCAL_DATE_ORDINAL = 0;
    static final int LOCAL_TIME_ORDINAL = 1;
    static final int LOCAL_DATE_TIME_ORDINAL = 2;
    static final int OFFSET_DATE_ORDINAL = 3;
    static final int OFFSET_TIME_ORDINAL = 4;
    static final int OFFSET_DATE_TIME_ORDINAL = 5;
    static final int ZONED_DATE_TIME_ORDINAL = 6;
    static final int ZONE_OFFSET_ORDINAL = 7;
    static final int ZONE_ID_ORDINAL = 8;
    static final int CHRONOLOGY_ORDINAL = 9;

    //-----------------------------------------------------------------------
    /**
     * The rule for {@code LocalDate}.
     */
    static final CalendricalRule<LocalDate> LOCAL_DATE = new ISOCalendricalRule<LocalDate>(LocalDate.class, LOCAL_DATE_ORDINAL);
    /**
     * The rule for {@code LocalTime}.
     */
    static final CalendricalRule<LocalTime> LOCAL_TIME = new ISOCalendricalRule<LocalTime>(LocalTime.class, LOCAL_TIME_ORDINAL);
    /**
     * The rule for {@code LocalDateTime}.
     */
    static final CalendricalRule<LocalDateTime> LOCAL_DATE_TIME = new ISOCalendricalRule<LocalDateTime>(LocalDateTime.class, LOCAL_DATE_TIME_ORDINAL);
    /**
     * The rule for {@code OffsetDate}.
     */
    static final CalendricalRule<OffsetDate> OFFSET_DATE = new ISOCalendricalRule<OffsetDate>(OffsetDate.class, OFFSET_DATE_ORDINAL);
    /**
     * The rule for {@code OffsetTime}.
     */
    static final CalendricalRule<OffsetTime> OFFSET_TIME = new ISOCalendricalRule<OffsetTime>(OffsetTime.class, OFFSET_TIME_ORDINAL);
    /**
     * The rule for {@code OffsetDateTime}.
     */
    static final CalendricalRule<OffsetDateTime> OFFSET_DATE_TIME = new ISOCalendricalRule<OffsetDateTime>(OffsetDateTime.class, OFFSET_DATE_TIME_ORDINAL);
    /**
     * The rule for {@code ZonedDateTime}.
     */
    static final CalendricalRule<ZonedDateTime> ZONED_DATE_TIME = new ISOCalendricalRule<ZonedDateTime>(ZonedDateTime.class, ZONED_DATE_TIME_ORDINAL);
    /**
     * The rule for {@code ZoneOffset}.
     */
    static final CalendricalRule<ZoneOffset> ZONE_OFFSET = new ISOCalendricalRule<ZoneOffset>(ZoneOffset.class, ZONE_OFFSET_ORDINAL);
    /**
     * The rule for {@code ZoneId}.
     */
    static final CalendricalRule<ZoneId> ZONE_ID = new ISOCalendricalRule<ZoneId>(ZoneId.class, ZONE_ID_ORDINAL);
    /**
     * The rule for {@code Chronology}.
     */
    static final CalendricalRule<Chronology> CHRONOLOGY = new ISOCalendricalRule<Chronology>(Chronology.class, CHRONOLOGY_ORDINAL);

    /**
     * Cache of rules for deserialization.
     * Indices must match ordinal passed to rule constructor.
     */
    private static final CalendricalRule<?>[] RULE_CACHE = new CalendricalRule<?>[] {
        LOCAL_DATE, LOCAL_TIME, LOCAL_DATE_TIME,
        OFFSET_DATE, OFFSET_TIME, OFFSET_DATE_TIME,
        ZONED_DATE_TIME, ZONE_OFFSET, ZONE_ID, CHRONOLOGY,
    };

}
