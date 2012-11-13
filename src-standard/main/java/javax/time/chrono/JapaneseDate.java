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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Objects;

import javax.time.DateTimeException;
import javax.time.DayOfWeek;
import javax.time.LocalDate;
import javax.time.calendrical.DateTimeField;
import javax.time.calendrical.DateTimeValueRange;
import javax.time.calendrical.LocalDateTimeField;
import javax.time.jdk8.Jdk8Methods;

import sun.util.calendar.LocalGregorianCalendar;

/**
 * A date in the Japanese Imperial calendar system.
 * <p>
 * This implements {@code ChronoLocalDate} for the
 * {@linkplain JapaneseChrono Japanese Imperial calendar}.
 *
 * <h4>Implementation notes</h4>
 * This class is immutable and thread-safe.
 */
final class JapaneseDate
        extends ChronoDateImpl<JapaneseChrono>
        implements Serializable {
    // this class is package-scoped so that future conversion to public
    // would not change serialization

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = -305327627230580483L;

    /**
     * The underlying ISO local date.
     * @serial
     */
    private final LocalDate isoDate;
    /**
     * The JapaneseEra of this date.
     */
    private transient JapaneseEra era;
    /**
     * The Japanese imperial calendar year of this date.
     */
    private transient int yearOfEra;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code JapaneseDate} from the proleptic ISO year,
     * month-of-year and day-of-month.
     *
     * @param prolepticYear  the year to represent in the proleptic year
     * @param month  the month-of-year to represent, not null
     * @param dayOfMonth  the day-of-month to represent, from 1 to 31
     * @return the Japanese date, never null
     * @throws DateTimeException if the value of any field is out of range, or
     *                              if the day-of-month is invalid for the month-year
     */
    public static JapaneseDate of(int prolepticYear, int month, int dayOfMonth) {
        return new JapaneseDate(LocalDate.of(prolepticYear, month, dayOfMonth));
    }

    /**
     * Obtains an instance of {@code JapaneseDate} from the era, year-of-era,
     * month-of-year and day-of-month.
     *
     * @param era  the era to represent, not null
     * @param year  the year-of-era to represent
     * @param month  the month-of-year to represent
     * @param dayOfMonth  the day-of-month to represent, from 1 to 31
     * @return the Japanese date, never null
     * @throws DateTimeException if the value of any field is out of range, or
     *                           if the day-of-month is invalid for the month-year
     */
    static JapaneseDate of(JapaneseEra era, int yearOfEra, int month, int dayOfMonth) {
        Objects.requireNonNull(era, "era");
        LocalGregorianCalendar.Date jdate = JapaneseChrono.JCAL.newCalendarDate(null);
        jdate.setEra(era.getPrivateEra()).setDate(yearOfEra, month, dayOfMonth);
        if (!JapaneseChrono.JCAL.validate(jdate)) {
            throw new IllegalArgumentException();
        }
        LocalDate date = LocalDate.of(jdate.getNormalizedYear(), month, dayOfMonth);
        return new JapaneseDate(era, yearOfEra, date);
    }

    //-----------------------------------------------------------------------
    /**
     * Creates an instance from the given date.
     *
     * @param isoDate  the standard local date, validated not null
     */
    JapaneseDate(LocalDate isoDate) {
        LocalGregorianCalendar.Date jdate = toPrivateJapaneseDate(isoDate);
        this.era = JapaneseEra.toJapaneseEra(jdate.getEra());
        this.yearOfEra = jdate.getYear();
        this.isoDate = isoDate;
    }

    /**
     * Constructs a {@code JapaneseDate}. This constructor does NOT validate the given parameters,
     * and {@code era} and {@code year} must agree with {@code isoDate}.
     *
     * @param era  the era, validated not null
     * @param year  the year-of-era, validated
     * @param isoDate  the standard local date, validated not null
     */
    private JapaneseDate(JapaneseEra era, int year, LocalDate isoDate) {
        this.era = era;
        this.yearOfEra = year;
        this.isoDate = isoDate;
    }

    /**
     * Reconstitutes this object from a stream.
     *
     * @param stream object input stream
     */
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        LocalGregorianCalendar.Date jdate = toPrivateJapaneseDate(isoDate);
        this.era = JapaneseEra.toJapaneseEra(jdate.getEra());
        this.yearOfEra = jdate.getYear();
    }

    //-----------------------------------------------------------------------
    @Override
    public JapaneseChrono getChrono() {
        return JapaneseChrono.INSTANCE;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this date is equal to another {@code JapaneseDate}.
     * The comparison is based on the time-line position of the dates.
     * <p>
     * Only objects of type {@code JapaneseDate} are compared, other types return {@code false}.
     *
     * @param obj  the object to check, null returns {@code false}
     * @return {@code true} if this is equal to the other date
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof JapaneseDate == false) {
            return false;
        }
        JapaneseDate otherDate = (JapaneseDate) obj;
        return this.isoDate.equals(otherDate.isoDate);
    }

    /**
     * A hash code for this {@code JapaneseDate}.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return isoDate.hashCode();
    }

    /**
     * Returns a {@code String} representation of this {@code JapaneseDate}, such as {@code "H24-10-20"}.
     * <p>
     * The output will be in the format {@code {era-abbr}{year}-{month}-{day}} for dates in Meiji and the later eras,
     * or {@code {gregorian_year}-{month}-{day}} for dates before Meiji.
     *
     * @return the formatted date, not null
     */
    @Override
    public String toString() {
        if (era == JapaneseEra.SEIREKI) {
            return getChrono().getId() + " " + isoDate.toString();
        }
        return super.toString();
    }

    //-----------------------------------------------------------------------
    @Override
    public int lengthOfMonth() {
        return isoDate.lengthOfMonth();
    }

    @Override
    public DateTimeValueRange range(DateTimeField field) {
        if (field instanceof LocalDateTimeField) {
            if (isSupported(field)) {
                LocalDateTimeField f = (LocalDateTimeField) field;
                switch (f) {
                    case DAY_OF_YEAR:
                        return actualRange(Calendar.DAY_OF_YEAR);
                    case YEAR_OF_ERA:
                        return actualRange(Calendar.YEAR);
                }
                return getChrono().range(f);
            }
            throw new DateTimeException("Unsupported field: " + field.getName());
        }
        return field.doRange(this);
    }

    private DateTimeValueRange actualRange(int calendarField) {
        Calendar jcal = Calendar.getInstance(JapaneseChrono.LOCALE);
        jcal.set(Calendar.ERA, era.getValue() + JapaneseEra.ERA_OFFSET);
        jcal.set(yearOfEra, isoDate.getMonthValue() - 1, isoDate.getDayOfMonth());
        return DateTimeValueRange.of(jcal.getActualMinimum(calendarField),
                                     jcal.getActualMaximum(calendarField));
    }

    @Override
    public long getLong(DateTimeField field) {
        if (field instanceof LocalDateTimeField) {
            switch ((LocalDateTimeField) field) {
                case YEAR_OF_ERA:
                    return yearOfEra;
                case ERA:
                    return era.getValue();
                case DAY_OF_YEAR: {
                    LocalGregorianCalendar.Date jdate = toPrivateJapaneseDate(isoDate);
                    return JapaneseChrono.JCAL.getDayOfYear(jdate);
                }
                case WEEK_OF_YEAR:
                    // TODO: need to resolve week of year issues with Japanese calendar.
                    break;
            }
            // TODO: review other fields
            return isoDate.getLong(field);
        }
        return field.doGet(this);
    }

    /**
     * Returns a {@code LocalGregorianCalendar.Date} converted from the given {@code isoDate}.
     *
     * @param isoDate  the local date, not null
     * @return a {@code LocalGregorianCalendar.Date}, not null
     */
    private static LocalGregorianCalendar.Date toPrivateJapaneseDate(LocalDate isoDate) {
        LocalGregorianCalendar.Date jdate = JapaneseChrono.JCAL.newCalendarDate(null);
        sun.util.calendar.Era sunEra = JapaneseEra.privateEraFrom(isoDate);
        int year = isoDate.getYear();
        if (sunEra != null) {
            year -= sunEra.getSinceDate().getYear() - 1;
        }
        jdate.setEra(sunEra).setYear(year).setMonth(isoDate.getMonthValue()).setDayOfMonth(isoDate.getDayOfMonth());
        JapaneseChrono.JCAL.normalize(jdate);
        return jdate;
    }

    //-----------------------------------------------------------------------
    @Override
    public JapaneseDate with(DateTimeField field, long newValue) {
        if (field instanceof LocalDateTimeField) {
            LocalDateTimeField f = (LocalDateTimeField) field;
            switch (f) {
                case YEAR_OF_ERA:
                case YEAR:
                case ERA: {
                    f.checkValidValue(newValue);
                    int nvalue = (int) newValue;
                    switch (f) {
                        case YEAR_OF_ERA:
                            return this.withYear(nvalue);
                        case YEAR:
                            return with(isoDate.withYear(nvalue));
                        case ERA: {
                            return this.withYear(JapaneseEra.of(nvalue), yearOfEra);
                        }
                    }
                }
            }
            // TODO: review other fields, such as WEEK_OF_YEAR
            return with(isoDate.with(field, newValue));
        }
        return field.doSet(this, newValue);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this date with the year altered.
     * <p>
     * This method changes the year of the date.
     * If the month-day is invalid for the year, then the previous valid day
     * will be selected instead.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param era  the era to set in the returned date, not null
     * @param year  the year-of-era to set in the returned date
     * @return a {@code JapaneseDate} based on this date with the requested year, never null
     * @throws DateTimeException if {@code year} is invalid
     */
    private JapaneseDate withYear(JapaneseEra era, int yearOfEra) {
        int year = JapaneseChrono.INSTANCE.prolepticYear(era, yearOfEra);
        return with(isoDate.withYear(year));
    }

    /**
     * Returns a copy of this date with the year-of-era altered.
     * <p>
     * This method changes the year-of-era of the date.
     * If the month-day is invalid for the year, then the previous valid day
     * will be selected instead.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to set in the returned date
     * @return a {@code JapaneseDate} based on this date with the requested year-of-era, never null
     * @throws DateTimeException if {@code year} is invalid
     */
    @Override
    public JapaneseDate withYear(int year) {
        return withYear((JapaneseEra) getEra(), year);
    }

    //-----------------------------------------------------------------------
    @Override
    public JapaneseDate plusYears(long years) {
        return with(isoDate.plusYears(years));
    }

    @Override
    public JapaneseDate plusMonths(long months) {
        return with(isoDate.plusMonths(months));
    }

    @Override
    public JapaneseDate plusWeeks(long weeksToAdd) {
        return plusDays(Jdk8Methods.safeMultiply(weeksToAdd, 7));
    }

    @Override
    public JapaneseDate plusDays(long days) {
        return with(isoDate.plusDays(days));
    }

    private JapaneseDate with(LocalDate newDate) {
        return (newDate.equals(isoDate) ? this : new JapaneseDate(newDate));
    }

    //-----------------------------------------------------------------------
    @Override
    public Era<JapaneseChrono> getEra() {
        return super.getEra();
    }

    @Override
    public int getYear() {
        return super.getYear();
    }

    @Override
    public int getMonthValue() {
        return super.getMonthValue();
    }

    @Override
    public int getDayOfMonth() {
        return super.getDayOfMonth();
    }

    @Override
    public int getDayOfYear() {
        return super.getDayOfYear();
    }

    @Override
    public DayOfWeek getDayOfWeek() {
        return super.getDayOfWeek();
    }

    @Override
    public boolean isLeapYear() {
        return super.isLeapYear();
    }

    @Override
    public int lengthOfYear() {
        return super.lengthOfYear();
    }

    @Override
    public JapaneseDate withEra(Era<JapaneseChrono> era) {
        return (JapaneseDate)super.withEra(era);
    }

    @Override
    public JapaneseDate withMonth(int month) {
        return (JapaneseDate)super.withMonth(month);
    }

    @Override
    public JapaneseDate withDayOfMonth(int dayOfMonth) {
        return (JapaneseDate)super.withDayOfMonth(dayOfMonth);
    }

    @Override
    public JapaneseDate withDayOfYear(int dayOfYear) {
        return (JapaneseDate)super.withDayOfYear(dayOfYear);
    }

    @Override
    public JapaneseDate minusYears(long yearsToSubtract) {
        return (JapaneseDate)super.minusYears(yearsToSubtract);
    }

    @Override
    public JapaneseDate minusMonths(long monthsToSubtract) {
        return (JapaneseDate)super.minusMonths(monthsToSubtract);
    }

    @Override
    public JapaneseDate minusWeeks(long weeksToSubtract) {
        return (JapaneseDate)super.minusWeeks(weeksToSubtract);
    }

    @Override
    public JapaneseDate minusDays(long daysToSubtract) {
        return (JapaneseDate)super.minusDays(daysToSubtract);
    }

}
