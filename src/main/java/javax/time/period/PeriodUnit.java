/*
 * Copyright (c) 2007,2008, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.period;

import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The unit defining how a measurable period of time operates.
 * <p>
 * Period unit implementations define how a field like 'days' operates.
 * This includes the period name and relationship to other periods like hour.
 * <p>
 * PeriodUnit is an abstract class and must be implemented with care to
 * ensure other classes in the framework operate correctly.
 * All instantiable subclasses must be final, immutable and thread-safe.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public abstract class PeriodUnit implements Comparable<PeriodUnit> {

//    /**
//     * Standard period unit for centuries.
//     * This is a derived unit equal to 100 years.
//     */
//    public static final PeriodUnit CENTURIES = Standard.CENTURIES;
//    /**
//     * Standard period unit for decades.
//     * This is a derived unit equal to 10 years.
//     */
//    public static final PeriodUnit DECADES = Standard.DECADES;
//    /**
//     * Standard period unit for years.
//     * This is a standard unit equal to 12 months.
//     */
//    public static final PeriodUnit YEARS = Standard.YEARS;
//    /**
//     * Standard period unit for quarters.
//     * This is a derived unit equal to 3 months.
//     */
//    public static final PeriodUnit QUARTERS = Standard.QUARTERS;
//    /**
//     * Standard period unit for months.
//     * This is a basic unit.
//     */
//    public static final PeriodUnit MONTHS = Standard.MONTHS;
//    /**
//     * Standard period unit for weeks.
//     * This is a derived unit equal to 7 days.
//     */
//    public static final PeriodUnit WEEKS = Standard.WEEKS;
//    /**
//     * Standard period unit for days.
//     * This is a basic unit.
//     */
//    public static final PeriodUnit DAYS = Standard.DAYS;
//    /**
//     * Standard period unit for twelve hours, useful for AM/PM.
//     * This is a derived unit equal to 12 hours.
//     */
//    public static final PeriodUnit TWELVE_HOURS = Standard.TWELVE_HOURS;
//    /**
//     * Standard period unit for hours.
//     * This is a standard unit equal to 60 minutes.
//     */
//    public static final PeriodUnit HOURS = Standard.HOURS;
//    /**
//     * Standard period unit for minutes.
//     * This is a standard unit equal to 60 seconds.
//     */
//    public static final PeriodUnit MINUTES = Standard.MINUTES;
//    /**
//     * Standard period unit for seconds.
//     * This is a standard unit equal to 1000000000 nanos.
//     */
//    public static final PeriodUnit SECONDS = Standard.SECONDS;
//    /**
//     * Standard period unit for milliseconds.
//     * This is a standard unit equal to 1000000 nanos.
//     */
//    public static final PeriodUnit MILLIS = Standard.MILLIS;
//    /**
//     * Standard period unit for microseconds.
//     * This is a standard unit equal to 1000 nanos.
//     */
//    public static final PeriodUnit MICROS = Standard.MICROS;
//    /**
//     * Standard period unit for nanoseconds.
//     * This is a basic unit.
//     */
//    public static final PeriodUnit NANOS = Standard.NANOS;

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     */
    protected PeriodUnit() {
        super();
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the name of the unit.
     *
     * @return the name of the unit, never null
     */
    public abstract String getName();

    /**
     * Gets the alternate period that this field can be expressed as.
     * For example, an hour might be represented as 60 minutes.
     *
     * @return the alternate period, null if none
     */
    public abstract Period getAlternatePeriod();

    /**
     * Gets an estimate of the duration of the unit in seconds.
     * This is used for comparing period units.
     *
     * @return the estimate of the duration in seconds, never null
     */
    protected abstract BigDecimal getEstimatedDurationForComparison();

    /**
     * Checks whether this unit is a standard unit.
     *
     * @return true if this is a standard unit
     */
    public final boolean isStandard() {
        return getClass() == Standard.class;
    }

    /**
     * Checks whether this unit is derived from another more fundamental standard unit.
     * <p>
     * If this method returns true, then {@link #getAlternatePeriod()} must be non-null.
     *
     * @return true if this unit is a standard unit that is derived from another standard unit
     */
    public abstract boolean isStandardDerived();

    //-----------------------------------------------------------------------
    /**
     * Compares this PeriodUnit to another based on a typical duration.
     *
     * @param other  the other type to compare to, not null
     * @return the comparator result, negative if less, postive if greater, zero if equal
     * @throws NullPointerException if other is null
     */
    public int compareTo(PeriodUnit other) {
        return getEstimatedDurationForComparison().compareTo(other.getEstimatedDurationForComparison());
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a string representation of the unit, which is the unit name.
     *
     * @return the unit name, never null
     */
    @Override
    public String toString() {
        return getName();
    }

    //-----------------------------------------------------------------------
    /**
     * Implementation of standard period units.
     */
    static final class Standard extends PeriodUnit implements Serializable {
        /**
         * Map for resolving singletons.
         */
        private static final Map<String, Standard> RESOLVE_MAP = new ConcurrentHashMap<String, Standard>();
        /**
         * Standard period unit for centuries.
         */
        static final PeriodUnit CENTURIES = new Standard("Centuries", Period.years(100), 100L * 31557600L, true);
        /**
         * Standard period unit for decades.
         */
        static final PeriodUnit DECADES = new Standard("Decades", Period.years(10), 10L * 31557600L, true);
        /**
         * Standard period unit for years.
         */
        static final PeriodUnit YEARS = new Standard("Years", Period.months(12), 31557600L, false);  // 365.25 days
        /**
         * Standard period unit for weekyears.
         */
        static final PeriodUnit WEEKYEARS = new Standard("Weekyears", null, 364L * 86400L + 43200L , false);  // 364.5 days
        /**
         * Standard period unit for quarters.
         */
        static final PeriodUnit QUARTERS = new Standard("Quarters", Period.months(3), 7889400L, true);
        /**
         * Standard period unit for months.
         */
        static final PeriodUnit MONTHS = new Standard("Months", null, 2629800L, false);
        /**
         * Standard period unit for weeks.
         */
        static final PeriodUnit WEEKS = new Standard("Weeks", Period.days(7), 7L * 86400L, true);
        /**
         * Standard period unit for days.
         */
        static final PeriodUnit DAYS = new Standard("Days", null, 86400L, false);
        /**
         * Standard period unit for half days.
         */
        static final PeriodUnit TWELVE_HOURS = new Standard("TwelveHours", Period.hours(12), 12L * 3600L, true);
        /**
         * Standard period unit for hours.
         */
        static final PeriodUnit HOURS = new Standard("Hours", Period.minutes(60), 60L * 60L, false);
        /**
         * Standard period unit for minutes.
         */
        static final PeriodUnit MINUTES = new Standard("Minutes", Period.seconds(60), 60L, false);
        /**
         * Standard period unit for seconds.
         */
        static final PeriodUnit SECONDS = new Standard("Seconds", Period.nanos(1000000000), -1000000000L, false);
        /**
         * Standard period unit for milliseconds.
         */
        static final PeriodUnit MILLIS = new Standard("Millis", Period.nanos(1000000), -1000000L, false);
        /**
         * Standard period unit for microseconds.
         */
        static final PeriodUnit MICROS = new Standard("Micros", Period.nanos(1000), -1000L, false);
        /**
         * Standard period unit for nanoseconds.
         */
        static final PeriodUnit NANOS = new Standard("Nanos", null, -1L, false);

        /**
         * A serialization identifier for this class.
         */
        private static final long serialVersionUID = 136537637L;

        /**
         * The name of the rule, not null.
         */
        private final String name;
        /**
         * The alternate period, expressing this field in terms of another.
         */
        private final transient Period alternatePeriod;
        /**
         * An estimate of the duration of the unit, for comparison.
         */
        private final transient BigDecimal estimatedDuration;
        /**
         * An estimate of the duration of the unit, for comparison.
         */
        private final transient boolean derived;

        /**
         * Constructor.
         *
         * @param name  the name of the rule, not null
         * @param alternatePeriod  alternate period that this field can be expressed in, null if none
         * @param estimatedNanoDuration  an estimate of the duration, used for comparison
         * @param derived  true if this unit is derived
         */
        private Standard(String name, Period alternatePeriod, long estimatedNanoDuration, boolean derived) {
            super();
            this.name = name;
            this.alternatePeriod = alternatePeriod;
            if (estimatedNanoDuration >= 0) {
                this.estimatedDuration = new BigDecimal(estimatedNanoDuration);
            } else {
                this.estimatedDuration = new BigDecimal(-estimatedNanoDuration).movePointLeft(9);
            }
            this.derived = derived;
            RESOLVE_MAP.put(name, this);
        }

        /**
         * Resolves singletons.
         *
         * @return the resolved instance
         */
        private Object readResolve() throws ObjectStreamException {
            Standard resolved = RESOLVE_MAP.get(name);
            if (resolved == null) {
                throw new InvalidObjectException("Period unit is not recognised: " + name);
            }
            return resolved;
        }

        //-----------------------------------------------------------------------
        /** {@inheritDoc} */
        @Override
        public String getName() {
            return name;
        }

        /** {@inheritDoc} */
        @Override
        public Period getAlternatePeriod() {
            return alternatePeriod;
        }

        /** {@inheritDoc} */
        @Override
        protected BigDecimal getEstimatedDurationForComparison() {
            return estimatedDuration;
        }

        /** {@inheritDoc} */
        @Override
        public boolean isStandardDerived() {
            return derived;
        }

        /** {@inheritDoc} */
        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof Standard) {
                return name.equals(((Standard) obj).name);
            }
            return false;
        }

        /** {@inheritDoc} */
        @Override
        public int hashCode() {
            return name.hashCode();
        }
    }

}
