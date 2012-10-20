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

import java.util.HashSet;
import java.util.Locale;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.time.Clock;
import javax.time.DateTimeException;
import javax.time.DateTimes;
import javax.time.LocalDate;
import javax.time.ZoneId;
import javax.time.calendrical.DateTimeAccessor;
import javax.time.calendrical.DateTimeValueRange;
import javax.time.calendrical.LocalDateTimeField;

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
 * <li> {@link #date(javax.time.calendrical.DateTimeAccessor) date(Calendrical)}
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
public abstract class Chronology {

    /**
     * Map of available calendars by name.
     */
    private static final ConcurrentHashMap<String, Chronology> CHRONOS_BY_NAME;
    /**
     * Map of available calendars by locale id.
     */
    private static final ConcurrentHashMap<String, Chronology> CHRONOS_BY_ID;
    static {
        // TODO: defer initialization?
        ConcurrentHashMap<String, Chronology> names = new ConcurrentHashMap<String, Chronology>();
        ConcurrentHashMap<String, Chronology> ids = new ConcurrentHashMap<String, Chronology>();
        ServiceLoader<Chronology> loader =  ServiceLoader.load(Chronology.class);
        for (Chronology chronology : loader) {
            names.putIfAbsent(chronology.getName(), chronology);
            String id = chronology.getLocaleId();
            if (id != null) {
                ids.putIfAbsent(id, chronology);
            }
        }
        CHRONOS_BY_NAME = names;
        CHRONOS_BY_ID = ids;
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code Chronology} from a calendrical.
     * <p>
     * A calendrical represents some form of date and time information.
     * This factory converts the arbitrary calendrical to an instance of {@code Chronology}.
     * If the specified calendrical does not have a chronology, {@link ISOChronology} is returned.
     * 
     * @param calendrical  the calendrical to convert, not null
     * @return the chronology, not null
     * @throws DateTimeException if unable to convert to an {@code Chronology}
     */
    public static Chronology from(DateTimeAccessor calendrical) {
        Chronology obj = calendrical.extract(Chronology.class);
        return (obj != null ? obj : ISOChronology.INSTANCE);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code Chrono} from a locale.
     * <p>
     * The locale can be used to identify a calendar.
     * This uses {@link Locale#getUnicodeLocaleType(String)} to obtain the "ca" key
     * to identify the calendar system.
     * <p>
     * If the locale does not contain calendar system information, the standard
     * ISO calendar system is used.
     * 
     * @param locale  the locale to use to obtain the calendar system, not null
     * @return the calendar system associated with the locale, not null
     * @throws DateTimeException if the locale-specified calendar cannot be found
     */
    public static Chronology ofLocale(Locale locale) {
        DateTimes.checkNotNull(locale, "Locale must not be null");
        String localeId = locale.getUnicodeLocaleType("ca");
        if (localeId == null) {
            return ISOChronology.INSTANCE;
        } else if ("iso".equals(localeId)) {
            return ISOChronology.INSTANCE;
        } else {
            Chronology chrono = CHRONOS_BY_ID.get(localeId);
            if (chrono == null) {
                throw new DateTimeException("Unknown Chrono calendar system: " + localeId);
            }
            return chrono;
        }
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
     * @throws DateTimeException if the named calendar cannot be found
     */
    public static Chronology ofName(String name) {
        Chronology chrono = CHRONOS_BY_NAME.get(name);
        if (chrono == null) {
            throw new DateTimeException("Unknown Chrono calendar system: " + name);
        }
        return chrono;
    }

    /**
     * Returns the names of the available calendar systems.
     * <p>
     * These names can be used with {@link #ofName(String)}.
     * 
     * @return the independent, modifiable set of the available calendar systems, not null
     */
    public static Set<String> getAvailableNames() {
        return new HashSet<String>(CHRONOS_BY_NAME.keySet());
    }

    //-----------------------------------------------------------------------
    /**
     * Creates an instance.
     */
    protected Chronology() {
        // register the subclass
        CHRONOS_BY_NAME.putIfAbsent(this.getName(), this);
        String localeId = this.getLocaleId();
        if (localeId != null) {
            CHRONOS_BY_ID.putIfAbsent(localeId, this);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the name of the calendar system.
     * 
     * @return the name, not null
     */
    public abstract String getName();

    /**
     * Gets the identifier of the calendar system for locale lookup.
     * <p>
     * The lookup by locale, {@link #ofLocale(Locale)}, uses this identifier
     * rather than the name. This is to support the pre-defined constants from CLDR.
     * 
     * @return the locale identifier, null if lookup by locale not supported
     */
    protected abstract String getLocaleId();

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
    public ChronoDate date(Era era, int yearOfEra, int month, int dayOfMonth) {
        return date(prolepticYear(era, yearOfEra), month, dayOfMonth);
    }

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
     * Creates a date in this calendar system from the era, year-of-era and day-of-year fields.
     * 
     * @param era  the calendar system era of the correct type, not null
     * @param yearOfEra  the calendar system year-of-era
     * @param dayOfYear  the calendar system day-of-year
     * @return the date in this calendar system, not null
     */
    public ChronoDate dateFromYearDay(Era era, int yearOfEra, int dayOfYear) {
        return dateFromYearDay(prolepticYear(era, yearOfEra), dayOfYear);
    }

    /**
     * Creates a date in this calendar system from the proleptic-year and day-of-year fields.
     * 
     * @param prolepticYear  the calendar system proleptic-year
     * @param dayOfYear  the calendar system day-of-year
     * @return the date in this calendar system, not null
     */
    public abstract ChronoDate dateFromYearDay(int prolepticYear, int dayOfYear);

    /**
     * Creates a date in this calendar system from another calendrical object.
     * <p>
     * This implementation uses {@link #dateFromEpochDay(long)}.
     * 
     * @param calendrical  the other calendrical, not null
     * @return the date in this calendar system, not null
     */
    public ChronoDate date(DateTimeAccessor calendrical) {
        long epochDay = calendrical.get(LocalDateTimeField.EPOCH_DAY);
        return dateFromEpochDay(epochDay);
    }

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
     * <p>
     * This implementation uses {@link #now(Clock)}.
     *
     * @return the current date using the system clock and default time-zone, not null
     */
    public ChronoDate now() {
        return now(Clock.systemDefaultZone());
    }

    /**
     * Creates the current date in this calendar system from the system clock in the specified time-zone.
     * <p>
     * This will query the {@link Clock#system(ZoneId) system clock} to obtain the current date.
     * Specifying the time-zone avoids dependence on the default time-zone.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @return the current date using the system clock, not null
     */
    public ChronoDate now(ZoneId zone) {
        return now(Clock.system(zone));
    }

    /**
     * Creates the current date in this calendar system from the specified clock.
     * <p>
     * This will query the specified clock to obtain the current date - today.
     * Using this method allows the use of an alternate clock for testing.
     * The alternate clock may be introduced using {@link Clock dependency injection}.
     * <p>
     * This implementation uses {@link #dateFromEpochDay(long)}.
     *
     * @param clock  the clock to use, not null
     * @return the current date, not null
     */
    public ChronoDate now(Clock clock) {
        DateTimes.checkNotNull(clock, "Clock must not be null");
        return dateFromEpochDay(LocalDate.now(clock).toEpochDay());
    }

    //-----------------------------------------------------------------------
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

    /**
     * Calculates the proleptic-year given the era and year-of-era.
     * <p>
     * This combines the era and year-of-era into the single proleptic-year field.
     *
     * @param era  the calendar system era of the correct type, not null
     * @param yearOfEra  the calendar system year-of-era
     * @return the proleptic-year
     * @throws DateTimeException if unable to convert
     */
    public abstract int prolepticYear(Era era, int yearOfEra);

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
     * Gets the range of valid values for the specified field.
     * <p>
     * All fields can be expressed as a {@code long} integer.
     * This method returns an object that describes the valid range for that value.
     * <p>
     * Note that the result only describes the minimum and maximum valid values
     * and it is important not to read too much into them. For example, there
     * could be values within the range that are invalid for the field.
     * <p>
     * This method will return a result whether or not the chronology supports the field.
     * 
     * @param field  the field to get the range for, not null
     * @return the range of valid values for the field, not null
     */
    public abstract DateTimeValueRange range(LocalDateTimeField field);

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
            Chronology other = (Chronology) obj;
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
