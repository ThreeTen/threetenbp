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
package javax.time.chrono;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.time.CalendricalException;
import javax.time.Clock;
import javax.time.DateTimes;
import javax.time.LocalDate;
import javax.time.calendrical.DateTime;

/**
 * A standard year-month-day calendar system.
 * <p>
 * This class is used by applications seeking to handle dates in non-ISO calendar systems.
 * For example, the Gregorian, Japanese, Minguo, Thai Buddhist and others.
 * It is built on the generic concepts of year, month and day - subclasses define the
 * meaning of those concepts in the calendar system that they represent.
 * <p>
 * In practical terms, the {@code Chrono} instance also acts as a factory.
 * The {@link #ofName(String)} method allows an instance to be looked up by name.
 * Note that the result will be an instance configured using the default values for that calendar.
 * <p>
 * The {@code Chrono} class provides a set of methods to create {@code ChronoDate} instances.
 * The date classes are used to manipulate specific dates.
 * <ul>
 * <li> {@link #now() now()}
 * <li> {@link #now(Clock) now(clock)}
 * <li> {@link #date(int, int, int) date(year, month, day)}
 * <li> {@link #date(javax.time.chrono.Era, int, int, int) date(era, year, month, day)}
 * <li> {@link #date(javax.time.calendrical.DateTime) date(Calendrical)}
 * <li> {@link #dateFromEpochDay(long) dateFromEpochDay(epochDay)}
 * </ul>
 *
 * <h4 id="addcalendars">Adding New Calendars</h4>
 * <p>
 * A new calendar system may be defined and registered with this factory.
 * Implementors must provide a subclass of this class and the matching {@code ChronoDate}.
 * The {@link java.util.ServiceLoader} mechanism is then used to register the calendar.
 * 
 * <h4>Implementation notes</h4>
 * This interface must be implemented with care to ensure other classes operate correctly.
 * All implementations that can be instantiated must be final, immutable and thread-safe.
 * Subclasses should be Serializable wherever possible.
 */
public abstract class Chrono {

    /**
     * Map of available calendars by name.
     */
    private static final ConcurrentHashMap<String, Chrono> CHRONOS;
    static {
        // TODO: defer initialization?
        // hard code strings to avoid initialization loops
        ConcurrentHashMap<String, Chrono> map = new ConcurrentHashMap<String, Chrono>();
        
        ServiceLoader<Chrono> loader =  ServiceLoader.load(Chrono.class);
        for (Chrono chrono : loader) {
            map.putIfAbsent(chrono.getName(), chrono);
        }
        CHRONOS = map; //Collections.unmodifiableMap(map);
    }

    /**
     * Protected constructor.
     * Registers this Chrono with the map of available Chronos.
     */
    protected Chrono() {
        CHRONOS.putIfAbsent(this.getName(), this);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code Chrono} from a name.
     * <p>
     * The name is a standard way of identifying a calendar.
     * Since some calendars can be customized, the name typically refers to the
     * default customization. For example, the Gregorian calendar can have
     * multiple cutover dates from the Julian, but the lookup by name only
     * provides the default cutover date.
     * 
     * @param name  the calendar system name, not null
     * @return the calendar system with the name requested, not null
     * @throws CalendricalException if the named calendar cannot be found
     */
    public static Chrono ofName(String name) {
        Chrono chrono = CHRONOS.get(name);
        if (chrono == null) {
            throw new CalendricalException("Unknown Chrono calendar system: " + name);
        }
        return chrono;
    }

    /**
     * Returns the names of the available calendar systems.
     * 
     * @return the set of the available calendar systems, not null
     */
    public static Set<String> getAvailableNames() {
        return CHRONOS.keySet();
    }

    /**
     * Gets the name of the calendar system.
     * 
     * @return the name, not null
     */
    public abstract String getName();

    //-----------------------------------------------------------------------
    /**
     * Creates a date in this calendar system from the era, year-of-era, month-of-year and day-of-month fields.
     * 
     * @param era  the calendar system era of the correct type, not null
     * @param yearOfEra  the calendar system year-of-era
     * @param month  the calendar system month-of-year
     * @param dayOfMonth  the calendar system day-of-month
     * @return the date in this calendar system, not null
     */
    public abstract ChronoDate date(Era era, int yearOfEra, int month, int dayOfMonth);

    /**
     * Creates a date in this calendar system from the proleptic-year, month-of-year and day-of-month fields.
     * 
     * @param prolepticYear  the calendar system proleptic-year
     * @param month  the calendar system month-of-year
     * @param dayOfMonth  the calendar system day-of-month
     * @return the date in this calendar system, not null
     */
    public abstract ChronoDate date(int prolepticYear, int month, int dayOfMonth);

    /**
     * Creates a date in this calendar system from another calendrical object.
     * 
     * @param calendrical  the other calendrical, not null
     * @return the date in this calendar system, not null
     */
    public abstract ChronoDate date(DateTime calendrical);

    /**
     * Creates a date in this calendar system from the epoch day from 1970-01-01 (ISO).
     * 
     * @param epochDay  the epoch day measured from 1970-01-01 (ISO), not null
     * @return the date in this calendar system, not null
     */
    public abstract ChronoDate dateFromEpochDay(long epochDay);

    /**
     * Creates the current date in this calendar system from the system clock in the default time-zone.
     * <p>
     * This will query the {@link Clock#systemDefaultZone() system clock} in the default
     * time-zone to obtain the current date.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @return the current date using the system clock, not null
     */
    public ChronoDate now() {
        return now(Clock.systemDefaultZone());
    }

    /**
     * Creates the current date in this calendar system from the specified clock.
     * <p>
     * This will query the specified clock to obtain the current date - today.
     * Using this method allows the use of an alternate clock for testing.
     * The alternate clock may be introduced using {@link Clock dependency injection}.
     *
     * @param clock  the clock to use, not null
     * @return the current date, not null
     */
    public ChronoDate now(Clock clock) {
        DateTimes.checkNotNull(clock, "Clock must not be null");
        return dateFromEpochDay(LocalDate.now(clock).toEpochDay());
    }

    /**
     * Checks if the specified year is a leap year.
     * <p>
     * A leap-year is a year of a longer length than normal.
     * The exact meaning is determined by the chronology according to the following constraints.
     * <p>
     * A leap-year must imply a year-length longer than a non leap-year.
     *
     * @param prolepticYear  the proleptic-year to check, not validated for range
     * @return true if the year is a leap year
     */
    public abstract boolean isLeapYear(long prolepticYear);

    //-----------------------------------------------------------------------
    /**
     * Creates the calendar system era object from the numeric value.
     * <p>
     * The era is, conceptually, the largest division of the time-line.
     * Most calendar systems have a single epoch dividing the time-line into two eras.
     * However, some have multiple eras, such as one for the reign of each leader.
     * The exact meaning is determined by the chronology according to the following constraints.
     * <p>
     * The era in use at 1970-01-01 must have the value 1.
     * Later eras must have sequentially higher values.
     * Earlier eras must have sequentially lower values.
     * Each chronology must refer to an enum or similar singleton to provide the era values.
     * <p>
     * This method returns the singleton era of the correct type for the specified era value.
     *
     * @param eraValue  the era value
     * @return the calendar system era, not null
     */
    public abstract Era createEra(int eraValue);

    //-----------------------------------------------------------------------
    /**
     * Checks if this calendar system is equal to another calendar system.
     * <p>
     * The comparison is based on the entire state of the object.
     * <p>
     * The default implementation compares the name and class.
     * Subclasses must compare any additional state that they store.
     *
     * @param obj  the object to check, null returns false
     * @return true if this is equal to the other time-zone ID
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
           return true;
        }
        if (obj != null && getClass() == obj.getClass()) {
            Chrono other = (Chrono) obj;
            return getName().equals(other.getName());
        }
        return false;
    }

    /**
     * A hash code for this calendar system.
     * <p>
     * The default implementation is based on the name and class.
     * Subclasses should add any additional state that they store.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return getClass().hashCode() ^ getName().hashCode();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a string representation of the object.
     * <p>
     * The string representation will include the {@link #getName() name} of
     * the calendar system.
     *
     * @return a string representation of this calendar system, not null
     */
    @Override
    public String toString() {
        return getName() + "Chrono";
    }

}
