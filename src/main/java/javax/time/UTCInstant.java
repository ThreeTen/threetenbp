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

import java.io.Serializable;

import javax.time.calendar.LocalDate;

/**
 * An instantaneous point on the time-line measured in the UTC time-scale,
 * handling leap seconds.
 * <p>
 * Most of the Time Framework for Java works on the assumption that the time-line is
 * simple, there are no leap-seconds and there are always 24 * 60 * 60 seconds in a day.
 * However, the Earth's rotation is not straightforward, and a solar day does not match
 * this definition.
 * <p>
 * This class is an alternative representation based on the UTC time-scale which
 * includes leap-seconds. Leap-seconds are additional seconds that are inserted into the
 * year-month-day-hour-minute-second time-line in order to keep UTC in line with the solar day.
 * When a leap second occurs, an accurate clock will show the time {@code 23:59:60} just before midnight.
 * <p>
 * Leap-seconds are announced in advance, typically at least six months.
 * The {@link UTCRules} class models which dates have leap-seconds.
 * Alternative implementations of the rules may be supplied.
 * <p>
 * The default rules implementation fixes the start point of UTC as 1972.
 * This date was chosen as UTC was more complex before 1972.
 * <p>
 * The duration between two points on the UTC time-scale is calculated solely using this class.
 * Do not use the {@code between} method on {@code Duration} as that will lose information.
 * Instead use {@link #durationUntil(UTCInstant)} on this class.
 * <p>
 * It is intended that most applications will use the {@code Instant} class
 * which uses the UTC-SLS mapping from UTC to guarantee 86400 seconds per day.
 * Specialist applications with access to an accurate time-source may find this class useful.
 * 
 * <h4>Time-scale</h4>
 * <p>
 * The length of the solar day is the standard way that humans measure time.
 * As the Earth's rotation changes, the length of the day varies.
 * In general, a solar day is slightly longer than 86400 seconds.
 * The actual length is not predictable and can only be determined by measurement.
 * The UT1 time-scale captures these measurements.
 * <p>
 * The UTC time-scale is a standard approach to bundle up all the additional fractions
 * of a second from UT1 into whole seconds, known as <i>leap-seconds</i>.
 * A leap-second may be added or removed depending on the Earth's rotational changes.
 * If it is removed, then the relevant date will have no time of 23:59:59.
 * If it is added, then the relevant date will have an extra second of 23:59:60.
 * <p>
 * The modern UTC time-scale was introduced in 1972, introducing the concept of whole leap-seconds.
 * Between 1958 and 1972, the definition of UTC was complex, with minor sub-second leaps and
 * alterations to the length of seconds. The default rules only implement UTC from 1972.
 * Prior to that date, the default rules fix the UTC-TAI offset at 10 seconds.
 * While not historically accurate, it is a simple, easy definition, suitable for this library.
 * <p>
 * The standard Java epoch of 1970-01-01 is prior to the introduction of whole leap-seconds into UTC in 1972.
 * As such, the Time Framework for Java needs to define what the 1970 epoch actually means.
 * The chosen definition follows the UTC definition given above, such that 1970-01-01 is 10 seconds
 * offset from TAI.
 * <p>
 * UTCInstant is immutable and thread-safe.
 *
 * @author Stephen Colebourne
 */
public final class UTCInstant
        implements Comparable<UTCInstant>, Serializable {
    // does not implement InstantProvider as that would enable methods like
    // Duration.between which gives the wrong answer due to lossy conversion

    /**
     * Constant for seconds per day.
     */
    private static final long SECS_PER_DAY = 24 * 60 * 60;
    /**
     * Constant for nanos per second.
     */
    private static final long NANOS_PER_SECOND = 1000000000;
    /**
     * Serialization version id.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The Modified Julian Day, from the epoch of 1858-11-17.
     */
    private final long mjDay;
    /**
     * The number of nanoseconds, later along the time-line, from the MJD field.
     * This is always positive and includes leap seconds.
     */
    private final long nanos;
    /**
     * The leap second rules.
     */
    private final UTCRules rules;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code UTCInstant} from a Modified Julian Day with
     * a nanosecond fraction of second using the system default leap second rules.
     * <p>
     * This factory creates an instance of a UTC instant.
     * The nanosecond of day value includes any leap second and has a valid range from
     * {@code 0} to {@code 86,400,000,000,000 - 1} on days other than leap-second-days
     * and other lengths on leap-second-days.
     * <p>
     * The nanosecond value must be positive even for negative values of Modified
     * Julian Days. One nanosecond before Modified Julian Day zero will be
     * {@code -1} days and the maximum nanosecond value.
     *
     * @param mjDay  the date as a Modified Julian Day (number of days from the epoch of 1858-11-17)
     * @param nanoOfDay  the nanoseconds within the day, including leap seconds
     * @return the UTC instant, never null
     * @throws IllegalArgumentException if nanoOfDay is out of range
     */
    public static UTCInstant ofModifiedJulianDays(long mjDay, long nanoOfDay) {
        return ofModifiedJulianDays(mjDay, nanoOfDay, UTCRules.system());
    }

    /**
     * Obtains an instance of {@code UTCInstant} from a Modified Julian Day with
     * a nanosecond fraction of second using the specified leap second rules.
     * <p>
     * This factory creates an instance of a UTC instant.
     * The nanosecond of day value includes any leap second and has a valid range from
     * {@code 0} to {@code 86,400,000,000,000 - 1} on days other than leap-second-days
     * and other lengths on leap-second-days.
     * <p>
     * The nanosecond value must be positive even for negative values of Modified
     * Julian Days. One nanosecond before Modified Julian Day zero will be
     * {@code -1} days and the maximum nanosecond value.
     *
     * @param mjDay  the date as a Modified Julian Day (number of days from the epoch of 1858-11-17)
     * @param nanoOfDay  the nanoseconds within the day, including leap seconds
     * @return the UTC instant, never null
     * @throws IllegalArgumentException if nanoOfDay is out of range
     */
    public static UTCInstant ofModifiedJulianDays(long mjDay, long nanoOfDay, UTCRules rules) {
        Instant.checkNotNull(rules, "LeapSecondRules must not be null");
        long leapSecs = rules.getLeapSecondAdjustment(mjDay);
        long maxNanos = (SECS_PER_DAY + leapSecs) * NANOS_PER_SECOND;
        if (nanoOfDay < 0 || nanoOfDay >= maxNanos) {
            throw new IllegalArgumentException("Nanosecond-of-day must be between 0 and " + maxNanos + " on date " + mjDay);
        }
        return new UTCInstant(mjDay, nanoOfDay, rules);
    }

    /**
     * Obtains an instance of {@code UTCInstant} from a provider of instants
     * using the system default leap second rules.
     * <p>
     * This method converts from the UTC-SLS to the UTC time-scale using the
     * system default leap-second rules. This conversion will lose information
     * around a leap second in accordance with UTC-SLS.
     * Converting back to an {@code Instant} may result in a slightly different instant.
     *
     * @param instant  the instant to convert, not null
     * @return the UTC instant, never null
     */
    public static UTCInstant of(Instant instant) {
        return of(instant, UTCRules.system());
    }

    /**
     * Obtains an instance of {@code UTCInstant} from a provider of instants
     * using the specified leap second rules.
     * <p>
     * This method converts from the UTC-SLS to the UTC time-scale using the
     * specified leap-second rules. This conversion will lose information
     * around a leap second in accordance with UTC-SLS.
     * Converting back to an {@code Instant} may result in a slightly different instant.
     *
     * @param instant  the instant to convert, not null
     * @param rules  the leap second rules, not null
     * @return the UTC instant, never null
     */
    public static UTCInstant of(Instant instant, UTCRules rules) {
        return rules.convertToUTC(instant);
    }

    /**
     * Obtains an instance of {@code UTCInstant} from a TAI instant
     * using the system default leap second rules.
     * <p>
     * This method converts from the TAI to the UTC time-scale using the
     * system default leap-second rules. This conversion does not lose information
     * and the UTC instant may safely be converted back to a {@code TAIInstant}.
     *
     * @param taiInstant  the TAI instant to convert, not null
     * @return the UTC instant, never null
     */
    public static UTCInstant of(TAIInstant taiInstant) {
        return of(taiInstant, UTCRules.system());
    }

    /**
     * Obtains an instance of {@code UTCInstant} from a TAI instant
     * using the specified leap second rules.
     * <p>
     * This method converts from the TAI to the UTC time-scale using the
     * specified leap-second rules. This conversion does not lose information
     * and the UTC instant may safely be converted back to a {@code TAIInstant}.
     *
     * @param taiInstant  the TAI instant to convert, not null
     * @param rules  the leap second rules, not null
     * @return the UTC instant, never null
     */
    public static UTCInstant of(TAIInstant taiInstant, UTCRules rules) {
        return rules.convertToUTC(taiInstant);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance.
     *
     * @param mjDay  the date as a Modified Julian Day (number of days from the epoch of 1858-11-17)
     * @param nanoOfDay  the nanoseconds within the day, including leap seconds
     * @param rules  the leap second rules, not null
     */
    private UTCInstant(long myDay, long nanoOfDay, UTCRules rules) {
        super();
        this.mjDay = myDay;
        this.nanos = nanoOfDay;
        this.rules = rules;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the leap second rules defining when leap seconds occur.
     *
     * @return the leap seconds rules
     */
    public UTCRules getRules() {
        return rules;
    }

    /**
     * Gets the Modified Julian Day (MJD).
     * <p>
     * The Modified Julian Day count is a simple incrementing count of days
     * where day 0 is 1858-11-17.
     * The nanosecond part of the day is returned by {@code getNanosOfDay}.
     * <p>
     * A Modified Julian Day varies in length, being one second longer on a leap day.
     *
     * @return the Modified Julian Day based on the epoch 1858-11-17
     */
    public long getModifiedJulianDays() {
        return mjDay;
    }

    /**
     * Gets the number of nanoseconds, later along the time-line, from the start
     * of the Modified Julian Day.
     * <p>
     * The nanosecond-of-day value measures the total number of nanoseconds from
     * the Modified Julian Day returned by {@code getModifiedJulianDay}.
     * This value will include any additional leap seconds.
     *
     * @return the nanoseconds within the day, including leap seconds
     */
    public long getNanoOfDay() {
        return nanos;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the instant is within a leap second.
     * <p>
     * This method returns true when an accurate clock would return a seconds
     * field of 60.
     *
     * @return true if this instant is within a leap second
     */
    public boolean isLeapSecond() {
        return nanos > SECS_PER_DAY * NANOS_PER_SECOND;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this instant with the specified duration added.
     * <p>
     * The duration is added using simple addition of the seconds and nanoseconds
     * in the duration to the seconds and nanoseconds of this instant.
     * As a result, the duration is treated as being measured in TAI compatible seconds
     * for the purpose of this method.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param duration  the duration to add, not null
     * @return a {@code UTCInstant} with the duration added, never null
     * @throws ArithmeticException if the calculation exceeds the supported range
     */
    public UTCInstant plus(Duration duration) {
        return UTCInstant.of(toTAIInstant().plus(duration), rules);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this instant with the specified duration subtracted.
     * <p>
     * The duration is subtracted using simple subtraction of the seconds and nanoseconds
     * in the duration from the seconds and nanoseconds of this instant.
     * As a result, the duration is treated as being measured in TAI compatible seconds
     * for the purpose of this method.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param duration  the duration to subtract, not null
     * @return a {@code UTCInstant} with the duration subtracted, never null
     * @throws ArithmeticException if the calculation exceeds the supported range
     */
    public UTCInstant minus(Duration duration) {
        return UTCInstant.of(toTAIInstant().minus(duration), rules);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the duration between this instant and the specified instant.
     * <p>
     * This calculates the duration between this instant and another based on
     * the UTC time-scale. Any leap seconds that occur will be included in the duration.
     * Adding the duration to this instant using {@link #plus} will always result
     * in an instant equal to the specified instant.
     *
     * @param utcInstant  the instant to calculate the duration until, not null
     * @return the duration until the specified instant, may be negative, never null
     * @throws ArithmeticException if the calculation exceeds the supported range
     */
    public Duration durationUntil(UTCInstant utcInstant) {
        TAIInstant thisTAI = toTAIInstant();
        TAIInstant otherTAI = utcInstant.toTAIInstant();
        return thisTAI.durationUntil(otherTAI);
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this instant to a {@code TAIInstant} using the stored
     * leap second rules.
     * <p>
     * This method converts from the UTC to the TAI time-scale using the stored leap-second rules.
     * Conversion to a {@code TAIInstant} retains the same point on the time-line
     * but loses the stored rules. If the TAI instant is converted back to a UTC instant
     * with different or updated rules then the calculated UTC instant may be different.
     *
     * @return a {@code TAIInstant} representing the same instant, never null
     * @throws ArithmeticException if the calculation exceeds the supported range
     */
    public TAIInstant toTAIInstant() {
        return rules.convertToTAI(this);
    }

    /**
     * Converts this instant to an {@code Instant} using the system default
     * leap second rules.
     * <p>
     * This method converts this instant from the UTC to the UTC-SLS time-scale using the
     * stored leap-second rules.
     * This conversion will lose information around a leap second in accordance with UTC-SLS.
     * Converting back to a {@code UTCInstant} may result in a slightly different instant.
     *
     * @return an {@code Instant} representing the best approximation of this instant, never null
     * @throws ArithmeticException if the calculation exceeds the supported range
     */
    public Instant toInstant() {
        return rules.convertToInstant(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this instant to another based on the time-line, then the name
     * of the rules.
     * <p>
     * The comparison is based on the positions on the time-line and the rules.
     * This definition means that two instants representing the same instant on
     * the time-line will differ if the rules differ. To compare the time-line
     * instant, convert both instants to a {@code TAIInstant}.
     *
     * @param otherInstant  the other instant to compare to, not null
     * @return the comparator value, negative if less, positive if greater
     */
    public int compareTo(UTCInstant otherInstant) {
        int cmp = MathUtils.safeCompare(mjDay, otherInstant.mjDay);
        if (cmp != 0) {
            return cmp;
        }
        return MathUtils.safeCompare(nanos, otherInstant.nanos);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this instant is equal to the specified {@code UTCInstant}.
     * <p>
     * The comparison is based on the positions on the time-line and the rules.
     * This definition means that two instants representing the same instant on
     * the time-line will differ if the rules differ. To compare the time-line
     * instant, convert both instants to a {@code TAIInstant}.
     *
     * @param otherInstant  the other instant, null returns false
     * @return true if the other instant is equal to this one
     */
    @Override
    public boolean equals(Object otherInstant) {
        if (this == otherInstant) {
            return true;
        }
        if (otherInstant instanceof UTCInstant) {
            UTCInstant other = (UTCInstant) otherInstant;
            return this.mjDay == other.mjDay &&
                   this.nanos == other.nanos &&
                   this.rules.equals(other.rules);
        }
        return false;
    }

    /**
     * Returns a hash code for this instant.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        // TODO: Evaluate hash code
        return ((int) (mjDay ^ (mjDay >>> 32))) + 51 * ((int) (nanos ^ (nanos >>> 32))) +
            rules.hashCode();
    }

    //-----------------------------------------------------------------------
    /**
     * A string representation of this instant.
     * <p>
     * The string is formatted using ISO-8601.
     *
     * @return a representation of this instant, never null
     */
    @Override
    public String toString() {
        LocalDate date = LocalDate.ofModifiedJulianDays(mjDay);  // TODO: capacity/import issues
        StringBuilder buf = new StringBuilder(18);
        int sod = (int) (nanos / NANOS_PER_SECOND);
        int hourValue = sod / (60 * 60);
        int minuteValue = (sod / 60) % 60;
        int secondValue = sod % 60;
        if (hourValue == 24) {
            hourValue = 23;
            minuteValue = 59;
            secondValue += 60;
        }
        int nanoValue = (int) (nanos % NANOS_PER_SECOND);
        buf.append(date).append('T')
            .append(hourValue < 10 ? "0" : "").append(hourValue)
            .append(minuteValue < 10 ? ":0" : ":").append(minuteValue)
            .append(secondValue < 10 ? ":0" : ":").append(secondValue);
        int pos = buf.length();
        buf.append(nanoValue + NANOS_PER_SECOND);
        buf.setCharAt(pos, '.');
        buf.append("(UTC)");
        return buf.toString();
    }

}
