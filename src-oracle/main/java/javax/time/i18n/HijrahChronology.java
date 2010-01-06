/*
 * Copyright (c) 2009 Oracle All Rights Reserved.
 */
package javax.time.i18n;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;

import javax.time.Duration;
import javax.time.calendar.Calendrical;
import javax.time.calendar.CalendricalMerger;
import javax.time.calendar.Chronology;
import javax.time.calendar.DateTimeFieldRule;
import javax.time.calendar.ISOChronology;
import javax.time.calendar.PeriodRule;
import javax.time.calendar.field.DayOfWeek;
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
 * The table shows the features described above.
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
 * @author Stephen Colebourne
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
//    /**
//     * Gets the rule for the year field in the Hijrah chronology.
//     * <p>
//     * The values for the year field match those for year of era in the current era.
//     * For the previous era, the values run backwards and include zero, thus
//     * BEFORE_HIJRAH 1 has the value 0, and BEFORE_HIJRAH 2 has the value -1.
//     *
//     * @return the rule for the year field, never null
//     */
//    public static DateTimeFieldRule<Integer> yearRule() {
//        return YearRule.INSTANCE;
//    }

    /**
     * Gets the rule for the era field in the Hijrah chronology.
     *
     * @return the rule for the year field, never null
     */
    public static DateTimeFieldRule<HijrahEra> eraRule() {
        return EraRule.INSTANCE;
    }

    /**
     * Gets the rule for the year of era field in the Hijrah chronology.
     *
     * @return the rule for the year of era field, never null
     */
    public static DateTimeFieldRule<Integer> yearOfEraRule() {
        return YearOfEraRule.INSTANCE;
    }

    /**
     * Gets the rule for the month of year field in the Hijrah chronology.
     *
     * @return the rule for the month of year field, never null
     */
    public static DateTimeFieldRule<Integer> monthOfYearRule() {
        return MonthOfYearRule.INSTANCE;
    }

    /**
     * Gets the rule for the day of month field in the Hijrah chronology.
     *
     * @return the rule for the day of month field, never null
     */
    public static DateTimeFieldRule<Integer> dayOfMonthRule() {
        return DayOfMonthRule.INSTANCE;
    }

    /**
     * Gets the rule for the day of year field in the Hijrah chronology.
     *
     * @return the rule for the day of year field, never null
     */
    public static DateTimeFieldRule<Integer> dayOfYearRule() {
        return DayOfYearRule.INSTANCE;
    }

    /**
     * Gets the rule for the day of week field in the Hijrah chronology.
     *
     * @return the rule for the day of week field, never null
     */
    public static DateTimeFieldRule<DayOfWeek> dayOfWeekRule() {
        return DayOfWeekRule.INSTANCE;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the period rule for eras.
     * <p>
     * The period rule defines the concept of a period of an era.
     * This is equivalent to the ISO eras period rule.
     * <p>
     * See {@link #eraRule()} for the main date-time field.
     *
     * @return the period rule for eras, never null
     */
    public static PeriodRule periodEras() {
        return ISOChronology.periodEras();
    }

    /**
     * Gets the period rule for years.
     * <p>
     * The period rule defines the concept of a period of a year.
     * The Hijrah year varies from 354 to 355 days.
     * The estimated duration of the year is 354.36... days (30617280 seconds).
     * <p>
     * See {@link #yearOfEraRule()} for the main date-time field.
     *
     * @return the period rule for years, never null
     */
    public static PeriodRule periodYears() {
        return YEARS;
    }

    /**
     * Gets the period rule for months.
     * <p>
     * The period rule defines the concept of a period of a month.
     * The Hijrah month varies from 29 to 30 days.
     * The estimated duration of the month is 29.5305... days (2551440 seconds).
     * <p>
     * See {@link #monthOfYearRule()} for the main date-time field.
     *
     * @return the period rule for months, never null
     */
    public static PeriodRule periodMonths() {
        return MONTHS;
    }

    /**
     * Gets the period rule for weeks.
     * <p>
     * The period rule defines the concept of a period of a week.
     * This is equivalent to the ISO weeks period rule.
     * <p>
     * See {@link #weekOfWeekBasedYearRule()} and {@link #weekOfYearRule()} for
     * the main date-time fields.
     *
     * @return the period rule for weeks, never null
     */
    public static PeriodRule periodWeeks() {
        return ISOChronology.periodWeeks();
    }

    /**
     * Gets the period rule for days.
     * <p>
     * The period rule defines the concept of a period of a day.
     * This is equivalent to the ISO days period rule.
     * <p>
     * See {@link #dayOfMonthRule()} for the main date-time field.
     *
     * @return the period rule for days, never null
     */
    public static PeriodRule periodDays() {
        return ISOChronology.periodDays();
    }

    //-----------------------------------------------------------------------
//    /**
//     * Rule implementation.
//     */
//    private static final class YearRule extends DateTimeFieldRule<Integer> implements Serializable {
//        /** Singleton instance. */
//        private static final DateTimeFieldRule<Integer> INSTANCE = new YearRule();
//        /** A serialization identifier for this class. */
//        private static final long serialVersionUID = 1L;
//        /** Constructor. */
//        private YearRule() {
//            super(Integer.class, HijrahChronology.INSTANCE, "Year", YEARS, null,
//                    -HijrahDate.MAX_YEAR_OF_ERA + 1, HijrahDate.MAX_YEAR_OF_ERA);
//        }
//        private Object readResolve() {
//            return INSTANCE;
//        }
//        @Override
//        protected Integer deriveValue(Calendrical calendrical) {
//            HijrahDate hd = calendrical.get(HijrahDate.rule());
//            if (hd == null) {
//                return null;
//            }
//            HijrahEra era = hd.getEra();
//            int yoe = hd.getYearOfEra();
//            return era == HijrahEra.BEFORE_HIJRAH ? 1 - yoe : yoe;
//        }
//        @Override
//        protected void merge(CalendricalMerger merger) {
//            HijrahChronology.INSTANCE.merge(merger);
//        }
//    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    private static final class EraRule extends DateTimeFieldRule<HijrahEra> implements Serializable {
        /** Singleton instance. */
        private static final DateTimeFieldRule<HijrahEra> INSTANCE = new EraRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private EraRule() {
            super(HijrahEra.class, HijrahChronology.INSTANCE, "Era", periodEras(), null, 0, 1);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        protected HijrahEra derive(Calendrical calendrical) {
            HijrahDate date = calendrical.get(HijrahDate.rule());
            return date != null ? date.getEra() : null;
        }
        @Override
        public String getText(int value, Locale locale, TextStyle textStyle) {
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

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    private static final class YearOfEraRule extends DateTimeFieldRule<Integer> implements Serializable {
        /** Singleton instance. */
        private static final DateTimeFieldRule<Integer> INSTANCE = new YearOfEraRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private YearOfEraRule() {
            super(Integer.class, HijrahChronology.INSTANCE, "YearOfEra", periodYears(), periodEras(),
                    HijrahDate.MIN_YEAR_OF_ERA, HijrahDate.MAX_YEAR_OF_ERA);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        // TODO: min/max years based on era
        @Override
        protected Integer derive(Calendrical calendrical) {
            HijrahDate date = calendrical.get(HijrahDate.rule());
            return date != null ? date.getYearOfEra() : null;
        }
        @Override
        protected void merge(CalendricalMerger merger) {
            HijrahEra era = merger.getValue(HijrahChronology.eraRule());
            era = (era != null ? era : HijrahEra.HIJRAH);
            Integer yoeVal = merger.getValue(this);
            // era, year, month, day-of-month
            Integer moyVal = merger.getValue(HijrahChronology.monthOfYearRule());
            Integer domVal = merger.getValue(HijrahChronology.dayOfMonthRule());
            if (moyVal != null && domVal != null) {
                HijrahDate date = HijrahDate.hijrahDate(era, yoeVal, moyVal, domVal);
                merger.storeMerged(HijrahDate.rule(), date);
                merger.removeProcessed(HijrahChronology.eraRule());
                merger.removeProcessed(this);
                merger.removeProcessed(HijrahChronology.monthOfYearRule());
                merger.removeProcessed(HijrahChronology.dayOfMonthRule());
            }
            // era, year, day-of-year
            Integer doyVal = merger.getValue(HijrahChronology.dayOfYearRule());
            if (doyVal != null) {
                HijrahDate date = HijrahDate.hijrahDate(era, yoeVal, 1, 1).plusDays(doyVal);
                merger.storeMerged(HijrahDate.rule(), date);
                merger.removeProcessed(HijrahChronology.eraRule());
                merger.removeProcessed(this);
                merger.removeProcessed(HijrahChronology.yearOfEraRule());
                merger.removeProcessed(HijrahChronology.dayOfYearRule());
            }
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    private static final class MonthOfYearRule extends DateTimeFieldRule<Integer> implements Serializable {
        /** Singleton instance. */
        private static final DateTimeFieldRule<Integer> INSTANCE = new MonthOfYearRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private MonthOfYearRule() {
            super(Integer.class, HijrahChronology.INSTANCE, "MonthOfYear", periodMonths(), periodYears(), 1, 12);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        protected Integer derive(Calendrical calendrical) {
            HijrahDate date = calendrical.get(HijrahDate.rule());
            return date != null ? date.getMonthOfYear() : null;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    private static final class DayOfMonthRule extends DateTimeFieldRule<Integer> implements Serializable {
        /** Singleton instance. */
        private static final DateTimeFieldRule<Integer> INSTANCE = new DayOfMonthRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private DayOfMonthRule() {
            super(Integer.class, HijrahChronology.INSTANCE, "DayOfMonth", periodDays(), periodMonths(), 1, HijrahDate.getMaximumDayOfMonth());
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
            HijrahEra era = calendrical.get(HijrahEra.rule());
            Integer yoeVal = calendrical.get(HijrahChronology.yearOfEraRule());
            Integer moyVal = calendrical.get(HijrahChronology.monthOfYearRule());
            if (era != null && yoeVal != null && moyVal != null) {
                int hijrahYear = (era == HijrahEra.BEFORE_HIJRAH ? 1 - yoeVal : yoeVal);
                return HijrahDate.getMonthLength(moyVal - 1, hijrahYear);
            }
            return getMaximumValue();
        }
        @Override
        protected Integer derive(Calendrical calendrical) {
            HijrahDate date = calendrical.get(HijrahDate.rule());
            return date != null ? date.getDayOfMonth() : null;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    private static final class DayOfYearRule extends DateTimeFieldRule<Integer> implements Serializable {
        /** Singleton instance. */
        private static final DateTimeFieldRule<Integer> INSTANCE = new DayOfYearRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private DayOfYearRule() {
            super(Integer.class, HijrahChronology.INSTANCE, "DayOfYear", periodDays(), periodYears(), 1, HijrahDate.getMaximumDayOfYear());
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
            HijrahEra era = calendrical.get(HijrahEra.rule());
            Integer yoeVal = calendrical.get(HijrahChronology.yearOfEraRule());
            if (era != null && yoeVal != null) {
                int hijrahYear = (era == HijrahEra.BEFORE_HIJRAH ? 1 - yoeVal : yoeVal);
                return HijrahDate.getYearLength(hijrahYear);
            }
            return getMaximumValue();
        }
        @Override
        protected Integer derive(Calendrical calendrical) {
            HijrahDate date = calendrical.get(HijrahDate.rule());
            return date != null ? date.getDayOfYear() : null;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    private static final class DayOfWeekRule extends DateTimeFieldRule<DayOfWeek> implements Serializable {
        /** Singleton instance. */
        private static final DateTimeFieldRule<DayOfWeek> INSTANCE = new DayOfWeekRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private DayOfWeekRule() {
            super(DayOfWeek.class, HijrahChronology.INSTANCE, "DayOfWeek", periodDays(), periodWeeks(), 1, 7);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        protected DayOfWeek derive(Calendrical calendrical) {
            HijrahDate date = calendrical.get(HijrahDate.rule());
            return date != null ? date.getDayOfWeek() : null;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Period rule for eras.
     */
    private static final PeriodRule YEARS = new PeriodRule(HijrahChronology.INSTANCE, "HijrahYears", Duration.seconds(30617280L));  // 354.36.... days
    /**
     * Period rule for eras.
     */
    private static final PeriodRule MONTHS = new PeriodRule(HijrahChronology.INSTANCE, "HijrahMonths", Duration.seconds(2551440L));  // 29.5305... days

}
