/*
 * Copyright (c) 2009 Oracle All Rights Reserved.
 */
package javax.time.i18n;

import static javax.time.period.PeriodUnits.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;

import javax.time.calendar.Calendrical;
import javax.time.calendar.CalendricalProvider;
import javax.time.calendar.Chronology;
import javax.time.calendar.DateTimeFieldRule;
import javax.time.calendar.LocalDate;
import javax.time.calendar.LocalTime;
import javax.time.calendar.Calendrical.FieldMap;
import javax.time.calendar.format.DateTimeFormatterBuilder.TextStyle;

/**
 * The Hijrah calendar system.
 * <p>
 * HijrahChronology defines the rules of the Hijrah calendar
 * system. The Hijrah calendar follows the Freeman-Grenville
 * algorithm (*1) and has following features.
 * <ul>
 * <li>A year has 12 months.</li>
 * <li>Over a cycle of 30 years there are 11 leap years.</li>
 * <li>There are 30 days in month number 1, 3, 5, 7, 9, and 11,
 * and 29 days in month number 2, 4, 6, 8, 10, and 12.</li>
 * <li>In a leap year month 12 has 30 days.</li>
 * <li>In a 30 year cycle, year 2, 5, 7, 10, 13, 16, 18, 21, 24,
 * 26, and 29 are leap years.</li>
 * <li>Total of 10631 days in a 30 years cycle.</li>
 * </ul>
 * <P>
 * The table shows the features descrived above.
 * <blockquote>
 * <table border="1">
 *   <tbody>
 *     <tr>
 *       <th># of month</th>
 *       <th>Name of month</th>
 *       <th>Number of days</th>
 *     </tr>
 *     <tr>
 *       <td>1</td>
 *       <td>Muharram</td>
 *       <td>30</td>
 *     </tr>
 *     <tr>
 *       <td>2</td>
 *       <td>Safar</td>
 *       <td>29</td>
 *     </tr>
 *     <tr>
 *       <td>3</td>
 *       <td>Rabi'al-Awwal</td>
 *       <td>30</td>
 *     </tr>
 *     <tr>
 *       <td>4</td>
 *       <td>Rabi'ath-Thani</td>
 *       <td>29</td>
 *     </tr>
 *     <tr>
 *       <td>5</td>
 *       <td>Jumada l-Ula</td>
 *       <td>30</td>
 *     </tr>
 *     <tr>
 *       <td>6</td>
 *       <td>Jumada t-Tania</td>
 *       <td>29</td>
 *     </tr>
 *     <tr>
 *       <td>7</td>
 *       <td>Rajab</td>
 *       <td>30</td>
 *     </tr>
 *     <tr>
 *       <td>8</td>
 *       <td>Sha`ban</td>
 *       <td>29</td>
 *     </tr>
 *     <tr>
 *       <td>9</td>
 *       <td>Ramadan</td>
 *       <td>30</td>
 *     </tr>
 *     <tr>
 *       <td>10</td>
 *       <td>Shawwal</td>
 *       <td>29</td>
 *     </tr>
 *     <tr>
 *       <td>11</td>
 *       <td>Dhu 'l-Qa`da</td>
 *       <td>30</td>
 *     </tr>
 *     <tr>
 *       <td>12</td>
 *       <td>Dhu 'l-Hijja</td>
 *       <td>29, but 30 days in years 2, 5, 7, 10,<br>
 * 13, 16, 18, 21, 24, 26, and 29</td>
 *     </tr>
 *   </tbody>
 * </table>
 * </blockquote>
 * <p>
 * (*1) The algorithm is taken from the book, 
 * The Muslim and Christian Calendars by G.S.P. Freeman-Grenville.
 * <p>
 * HijrahChronology is thread-safe and immutable.
 *
 * @author Ryoji Suzuki
 */
public final class HijrahChronology extends Chronology implements Serializable {

    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = 5856454970865881985L;

    /**
     * The singleton instance of <code>HijrahChronology</code>.
     */
    public static final HijrahChronology INSTANCE = new HijrahChronology();

    /**
     * Narrow names for eras.
     */
    private static final HashMap<String, String[]> ERA_NARROW_NAMES = new HashMap<String, String[]>();

    /**
     * Short names for eras.
     */
    private static final HashMap<String, String[]> ERA_SHORT_NAMES = new HashMap<String, String[]>();

    /**
     * Full names for eras.
     */
    private static final HashMap<String, String[]> ERA_FULL_NAMES = new HashMap<String, String[]>();

    /**
     * Fallback language for the era names.
     */
    private static final String FALLBACK_LANGUAGE = "en";

    /**
     * Language that has the era names.
     */
    //private static final String TARGET_LANGUAGE = "ar";

    /**
     * Name data.
     */
    static {
        ERA_NARROW_NAMES.put(FALLBACK_LANGUAGE, new String[]{"BH", "HE"});
        ERA_SHORT_NAMES.put(FALLBACK_LANGUAGE, new String[]{"B.H.", "H.E."});
        ERA_FULL_NAMES.put(FALLBACK_LANGUAGE, new String[]{"Before Hijrah", "Hijrah Era"});
    }

    //-----------------------------------------------------------------------
    /**
     * Restrictive constructor.
     */
    private HijrahChronology() {
    }

    /**
     * Resolves singleton.
     *
     * @return the singleton instance
     */
    private Object readResolve() {
        return INSTANCE;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the name of the chronology.
     *
     * @return the name of the chronology, never null
     */
    @Override
    public String getName() {
        return "Hijrah";
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the rule for the era field in the Hijrah chronology.
     *
     * @return the rule for the year field, never null
     */
    public DateTimeFieldRule era() {
        return EraRule.INSTANCE;
    }

    /**
     * Gets the rule for the year of era field in the Hijrah chronology.
     *
     * @return the rule for the year of era field, never null
     */
    public DateTimeFieldRule yearOfEra() {
        return YearOfEraRule.INSTANCE;
    }

    /**
     * Gets the rule for the year field in the Hijrah chronology.
     * <p>
     * The values for the year field match those for year of era in the current era.
     * For the previous era, the values run backwards and include zero, thus
     * BEFORE_HIJRAH 1 has the value 0, and BEFORE_HIJRAH 2 has the value -1.
     *
     * @return the rule for the year field, never null
     */
    @Override
    public DateTimeFieldRule year() {
        return YearRule.INSTANCE;
    }

    /**
     * Gets the rule for the month of year field in the Hijrah chronology.
     *
     * @return the rule for the month of year field, never null
     */
    @Override
    public DateTimeFieldRule monthOfYear() {
        return MonthOfYearRule.INSTANCE;
    }

    /**
     * Gets the rule for the day of month field in the Hijrah chronology.
     *
     * @return the rule for the day of month field, never null
     */
    @Override
    public DateTimeFieldRule dayOfMonth() {
        return DayOfMonthRule.INSTANCE;
    }

    /**
     * Gets the rule for the day of year field in the Hijrah chronology.
     *
     * @return the rule for the day of year field, never null
     */
    @Override
    public DateTimeFieldRule dayOfYear() {
        return DayOfYearRule.INSTANCE;
    }

    /**
     * Gets the rule for the day of week field in the Hijrah chronology.
     *
     * @return the rule for the day of week field, never null
     */
    @Override
    public DateTimeFieldRule dayOfWeek() {
        return DayOfWeekRule.INSTANCE;
    }

    //-----------------------------------------------------------------------
    /**
     * The hour of day field is not supported by the Hijrah chronology.
     *
     * @return never
     * @throws UnsupportedOperationException always
     */
    @Override
    public DateTimeFieldRule hourOfDay() {
        throw new UnsupportedOperationException("HijrahChronology does not support HourOfDay");
    }

    /**
     * The minute of hour field is not supported by the Hijrah chronology.
     *
     * @return never
     * @throws UnsupportedOperationException always
     */
    @Override
    public DateTimeFieldRule minuteOfHour() {
        throw new UnsupportedOperationException("HijrahChronology does not support MinuteOfHour");
    }

    /**
     * The second of minute field is not supported by the Hijrah chronology.
     *
     * @return never
     * @throws UnsupportedOperationException always
     */
    @Override
    public DateTimeFieldRule secondOfMinute() {
        throw new UnsupportedOperationException("HijrahChronology does not support SecondOfMinute");
   }

    /**
     * The nano of second field is not supported by the Hijrah chronology.
     *
     * @return never
     * @throws UnsupportedOperationException always
     */
    @Override
    public DateTimeFieldRule nanoOfSecond() {
        throw new UnsupportedOperationException("HijrahChronology does not support NanoOfSecond");
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    private static final class YearRule extends DateTimeFieldRule implements Serializable {
        /** Singleton instance. */
        private static final DateTimeFieldRule INSTANCE = new YearRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private YearRule() {
            super(HijrahChronology.INSTANCE, "Year", YEARS, null,
                    -HijrahDate.MAX_YEAR_OF_ERA + 1, HijrahDate.MAX_YEAR_OF_ERA);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        public Integer getValueQuiet(LocalDate date, LocalTime time) {
            if (date == null) {
                return null;
            } else {
                HijrahDate hijrahDate = HijrahDate.hijrahDate(date);
                HijrahEra era = hijrahDate.getEra();
                int year = hijrahDate.getYearOfEra();
                if (era == HijrahEra.BEFORE_HIJRAH) {
                    year = 1 - year;
                }
                return year;
            }
        }
        @Override
        protected void mergeDateTime(Calendrical.Merger merger) {
            Integer moyVal = merger.getValueQuiet(HijrahChronology.INSTANCE.monthOfYear());
            Integer domVal = merger.getValueQuiet(HijrahChronology.INSTANCE.dayOfMonth());
            if (moyVal != null && domVal != null) {
                int year = merger.getValue(this);
                int yearOfEra = Math.abs(year);
                HijrahEra era = (year < 1 ? HijrahEra.BEFORE_HIJRAH : HijrahEra.HIJRAH);
                HijrahDate date;
                if (merger.isStrict()) {
                    date = HijrahDate.hijrahDate(era, yearOfEra, moyVal, domVal);
                } else {
                    date = HijrahDate.hijrahDate(era, yearOfEra, 1, 1).plusMonths(moyVal)
                            .plusMonths(-1).plusDays(domVal).plusDays(-1);
                }
                merger.storeMergedDate(date.toLocalDate());
                merger.markFieldAsProcessed(this);
                merger.markFieldAsProcessed(HijrahChronology.INSTANCE.monthOfYear());
                merger.markFieldAsProcessed(HijrahChronology.INSTANCE.dayOfMonth());
            }
        }
    }

    /**
     * Rule implementation.
     */
    private static final class EraRule extends DateTimeFieldRule implements Serializable {
        /** Singleton instance. */
        private static final DateTimeFieldRule INSTANCE = new EraRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private EraRule() {
            // Use DECADES for now as there is no ERAS defined.
            super(HijrahChronology.INSTANCE, "Era", DECADES, null, 0, 1);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        public Integer getValueQuiet(LocalDate date, LocalTime time) {
            return (date == null ? null : HijrahDate.hijrahDate(date).getEra().getValue());
        }
        @Override
        protected Integer deriveValue(FieldMap fieldMap) {
            Integer yearVal = HijrahChronology.INSTANCE.year().getValueQuiet(fieldMap);
            if (yearVal == null) {
                return null;
            }
            return (yearVal < 1 ? 0 : 1);
        }
        /**
         * Gets the text for this field.
         * <p>
         * The value is queried using {@link #getValue(CalendricalProvider)}. The text
         * is then obtained for that value. If there is no textual mapping, then
         * the value is returned as per {@link Integer#toString()}.
         *
         * @param calendricalProvider  the calendrical provider, not null
         * @param locale  the locale to use, not null
         * @param textStyle  the text style, not null
         * @return the text of the field, never null
         * @throws UnsupportedCalendarFieldException if the value cannot be extracted
         */
        public String getText(CalendricalProvider calendricalProvider, Locale locale, TextStyle textStyle) {
            int value = getValue(calendricalProvider);
            String[] names = null;
            String language = locale.getLanguage();
            
            if (textStyle == TextStyle.NARROW) {
                names = ERA_NARROW_NAMES.get(language);
                if (names == null) {
                    names = ERA_NARROW_NAMES.get(FALLBACK_LANGUAGE);
                }
            }
            if (textStyle == TextStyle.SHORT) {
                names = ERA_SHORT_NAMES.get(language);
                if (names == null) {
                    names = ERA_SHORT_NAMES.get(FALLBACK_LANGUAGE);
                }
            }
            if (textStyle == TextStyle.FULL) {
                names = ERA_FULL_NAMES.get(language);
                if (names == null) {
                    names = ERA_FULL_NAMES.get(FALLBACK_LANGUAGE);
                }
            }
            return names == null ? Integer.toString(value) : names[value];
        }
    }

    /**
     * Rule implementation.
     */
    private static final class YearOfEraRule extends DateTimeFieldRule implements Serializable {
        /** Singleton instance. */
        private static final DateTimeFieldRule INSTANCE = new YearOfEraRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private YearOfEraRule() {
            super(HijrahChronology.INSTANCE, "YearOfEra", YEARS, null,
                    HijrahDate.MIN_YEAR_OF_ERA, HijrahDate.MAX_YEAR_OF_ERA);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        public Integer getValueQuiet(LocalDate date, LocalTime time) {
            return (date == null ? null : HijrahDate.hijrahDate(date).getYearOfEra());
        }
        @Override
        protected Integer deriveValue(FieldMap fieldMap) {
            Integer yearVal = HijrahChronology.INSTANCE.year().getValueQuiet(fieldMap);
            if (yearVal == null) {
                return null;
            }
            int year = yearVal;
            return (year < 1 ? -(year - 1) : year);
        }
        @Override
        protected void mergeFields(Calendrical.Merger merger) {
            Integer eraVal = merger.getValueQuiet(HijrahChronology.INSTANCE.era());
            HijrahEra era = (eraVal != null ? HijrahEra.hijrahEra(eraVal) : HijrahEra.HIJRAH);
            int yearOfEra = merger.getValue(this);
            int year = (era == HijrahEra.BEFORE_HIJRAH ? -yearOfEra + 1 : yearOfEra);
            merger.storeMergedField(HijrahChronology.INSTANCE.year(), year);
            if (eraVal != null) {
                merger.markFieldAsProcessed(HijrahChronology.INSTANCE.era());
            }
            merger.markFieldAsProcessed(HijrahChronology.INSTANCE.yearOfEra());
        }
        @Override
        protected void mergeDateTime(Calendrical.Merger merger) {
            Integer moyVal = merger.getValueQuiet(HijrahChronology.INSTANCE.monthOfYear());
            Integer domVal = merger.getValueQuiet(HijrahChronology.INSTANCE.dayOfMonth());
            if (moyVal != null && domVal != null) {
                Integer eraVal = merger.getValueQuiet(HijrahChronology.INSTANCE.era());
                HijrahEra era = (eraVal != null ? HijrahEra.hijrahEra(eraVal) : HijrahEra.HIJRAH);
                int yearOfEra = merger.getValue(this);
                HijrahDate date;
                if (merger.isStrict()) {
                    date = HijrahDate.hijrahDate(era, yearOfEra, moyVal, domVal);
                } else {
                    date = HijrahDate.hijrahDate(era, yearOfEra, 1, 1).plusMonths(moyVal)
                            .plusMonths(-1).plusDays(domVal).plusDays(-1);
                }
                merger.storeMergedDate(date.toLocalDate());
                if (eraVal != null) {
                    merger.markFieldAsProcessed(HijrahChronology.INSTANCE.era());
                }
                merger.markFieldAsProcessed(this);
                merger.markFieldAsProcessed(HijrahChronology.INSTANCE.monthOfYear());
                merger.markFieldAsProcessed(HijrahChronology.INSTANCE.dayOfMonth());
            }
        }
    }

    /**
     * Rule implementation.
     */
    private static final class MonthOfYearRule extends DateTimeFieldRule implements Serializable {
        /** Singleton instance. */
        private static final DateTimeFieldRule INSTANCE = new MonthOfYearRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private MonthOfYearRule() {
            super(HijrahChronology.INSTANCE, "MonthOfYear", MONTHS, YEARS, 1, 12);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        public Integer getValueQuiet(LocalDate date, LocalTime time) {
            return (date == null ? null : HijrahDate.hijrahDate(date).getMonthOfYear());
        }
    }

    /**
     * Rule implementation.
     */
    private static final class DayOfMonthRule extends DateTimeFieldRule
            implements Serializable {
        /** Singleton instance. */
        private static final DateTimeFieldRule INSTANCE = new DayOfMonthRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private DayOfMonthRule() {
            super(HijrahChronology.INSTANCE, "DayOfMonth", DAYS, MONTHS, 1, HijrahDate.getMaximumDayOfMonth());
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        public int getSmallestMaximumValue() {
            return HijrahDate.getSmallestMaximumDayOfMonth();
        }

        @Override
        public int getMaximumValue(Calendrical calendrical) {
            Integer yearVal = calendrical.deriveValueQuiet(HijrahChronology.INSTANCE.year());
            Integer moyVal = calendrical.deriveValueQuiet(HijrahChronology.INSTANCE.monthOfYear());
            if (yearVal != null && moyVal != null) {
                return HijrahDate.getMonthLength(moyVal - 1, yearVal);
            }
            return getMaximumValue();
        }
        @Override
        public Integer getValueQuiet(LocalDate date, LocalTime time) {
            return (date == null ? null : HijrahDate.hijrahDate(date).getDayOfMonth());
        }
    }

    /**
     * Rule implementation.
     */
    private static final class DayOfYearRule extends DateTimeFieldRule
            implements Serializable {
        /** Singleton instance. */
        private static final DateTimeFieldRule INSTANCE = new DayOfYearRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private DayOfYearRule() {
            super(HijrahChronology.INSTANCE, "DayOfYear", DAYS, YEARS, 1, HijrahDate.getMaximumDayOfYear());
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        public int getSmallestMaximumValue() {
            return HijrahDate.getSmallestMaximumDayOfYear();
        }
        @Override
        public int getMaximumValue(Calendrical calendrical) {
            Integer yearVal = calendrical.deriveValueQuiet(HijrahChronology.INSTANCE.year());
            Integer moyVal = calendrical.deriveValueQuiet(HijrahChronology.INSTANCE.monthOfYear());
            if (yearVal != null && moyVal != null) {
                return HijrahDate.getYearLength(yearVal);
            }
            return getMaximumValue();
        }
        @Override
        public Integer getValueQuiet(LocalDate date, LocalTime time) {
            return (date == null ? null : HijrahDate.hijrahDate(date).getDayOfYear());
        }
        @Override
        protected void mergeDateTime(Calendrical.Merger merger) {
            Integer yearVal = merger.getValueQuiet(HijrahChronology.INSTANCE.year());
            if (yearVal != null) {
                int doyVal = merger.getValue(this);
                int yearOfEra = Math.abs(yearVal);
                HijrahEra era = (yearVal < 1 ? HijrahEra.BEFORE_HIJRAH : HijrahEra.HIJRAH);
                HijrahDate date;
                if (merger.isStrict()) {
                    date = HijrahDate.hijrahDate(era, yearOfEra, 1, 1).withDayOfYear(doyVal);
                } else {
                    date = HijrahDate.hijrahDate(era, yearOfEra, 1, 1).plusDays(doyVal).plusDays(-1);
                }
                merger.storeMergedDate(date.toLocalDate());
                merger.markFieldAsProcessed(HijrahChronology.INSTANCE.year());
                merger.markFieldAsProcessed(this);
            }
        }
    }

    /**
     * Rule implementation.
     */
    private static final class DayOfWeekRule extends DateTimeFieldRule implements Serializable {
        /** Singleton instance. */
        private static final DateTimeFieldRule INSTANCE = new DayOfWeekRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private DayOfWeekRule() {
            super(HijrahChronology.INSTANCE, "DayOfWeek", DAYS, WEEKS, 1, 7);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        public Integer getValueQuiet(LocalDate date, LocalTime time) {
            return (date == null ? null : date.getDayOfWeek().getValue());
        }
    }
}
