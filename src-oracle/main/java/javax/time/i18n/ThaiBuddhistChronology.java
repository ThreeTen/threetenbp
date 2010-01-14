/*
 * Copyright (c) 2009 Oracle All Rights Reserved.
 */
package javax.time.i18n;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;

import javax.time.calendar.Calendrical;
import javax.time.calendar.CalendricalMerger;
import javax.time.calendar.Chronology;
import javax.time.calendar.DateTimeFieldRule;
import javax.time.calendar.DayOfWeek;
import javax.time.calendar.ISOChronology;
import javax.time.calendar.MonthOfYear;
import javax.time.calendar.PeriodUnit;
import javax.time.calendar.Year;
import javax.time.calendar.format.DateTimeFormatterBuilder.TextStyle;

/**
 * The Thai Buddhist calendar system.
 * <p>
 * ThaiBuddhistChronology defines the rules of the Thai Buddhist calendar
 * system.
 * <p>
 * ThaiBuddhistChronology is thread-safe and immutable.
 *
 * @author Ryoji Suzuki
 * @author Stephen Colebourne
 */
public final class ThaiBuddhistChronology extends Chronology implements Serializable {

    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = 5856454970865881985L;

    /**
     * The singleton instance of <code>ThaiBuddhistChronology</code>.
     */
    public static final ThaiBuddhistChronology INSTANCE = new ThaiBuddhistChronology();

    /**
     * Containing the offset from the ISO year.
     */
    static final int YEAR_OFFSET = -543;

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
    private static final String TARGET_LANGUAGE = "th";

    /**
     * Name data.
     */
    static {
        ERA_NARROW_NAMES.put(FALLBACK_LANGUAGE, new String[]{"BB", "BE"});
        ERA_NARROW_NAMES.put(TARGET_LANGUAGE, new String[]{"BB", "BE"});
        ERA_SHORT_NAMES.put(FALLBACK_LANGUAGE, new String[]{"B.B.", "B.E."});
        ERA_SHORT_NAMES.put(TARGET_LANGUAGE,
                new String[]{"\u0e1e.\u0e28.",
                "\u0e1b\u0e35\u0e01\u0e48\u0e2d\u0e19\u0e04\u0e23\u0e34\u0e2a\u0e15\u0e4c\u0e01\u0e32\u0e25\u0e17\u0e35\u0e48"});
        ERA_FULL_NAMES.put(FALLBACK_LANGUAGE, new String[]{"Before Buddhist", "Budhhist Era"});
        ERA_FULL_NAMES.put(TARGET_LANGUAGE,
                new String[]{"\u0e1e\u0e38\u0e17\u0e18\u0e28\u0e31\u0e01\u0e23\u0e32\u0e0a",
                "\u0e1b\u0e35\u0e01\u0e48\u0e2d\u0e19\u0e04\u0e23\u0e34\u0e2a\u0e15\u0e4c\u0e01\u0e32\u0e25\u0e17\u0e35\u0e48"});
    }

    //-----------------------------------------------------------------------
    /**
     * Restrictive constructor.
     */
    private ThaiBuddhistChronology() {
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
        return "ThaiBuddhist";
    }

    //-----------------------------------------------------------------------
//    /**
//     * Gets the rule for the year field in the Thai Buddhist chronology.
//     * <p>
//     * The values for the year field match those for year of era in the current era.
//     * For the previous era, the values run backwards and include zero, thus
//     * BEFORE_BUDDHIST 1 has the value 0, and BEFORE_BUDDHIST 2 has the value -1.
//     *
//     * @return the rule for the year field, never null
//     */
//    public static DateTimeFieldRule<Integer> year() {
//        return YearRule.INSTANCE;
//    }

    /**
     * Gets the rule for the era field in the Thai Buddhist chronology.
     *
     * @return the rule for the year field, never null
     */
    public static DateTimeFieldRule<ThaiBuddhistEra> eraRule() {
        return EraRule.INSTANCE;
    }

    /**
     * Gets the rule for the year of era field in the Thai Buddhist chronology.
     *
     * @return the rule for the year of era field, never null
     */
    public static DateTimeFieldRule<Integer> yearOfEraRule() {
        return YearOfEraRule.INSTANCE;
    }

    /**
     * Gets the rule for the month-of-year field in the Thai Buddhist chronology.
     *
     * @return the rule for the month-of-year field, never null
     */
    public static DateTimeFieldRule<MonthOfYear> monthOfYearRule() {
        return MonthOfYearRule.INSTANCE;
    }

    /**
     * Gets the rule for the day-of-month field in the Thai Buddhist chronology.
     *
     * @return the rule for the day-of-month field, never null
     */
    public static DateTimeFieldRule<Integer> dayOfMonthRule() {
        return DayOfMonthRule.INSTANCE;
    }

    /**
     * Gets the rule for the day-of-year field in the Thai Buddhist chronology.
     *
     * @return the rule for the day-of-year field, never null
     */
    public static DateTimeFieldRule<Integer> dayOfYearRule() {
        return DayOfYearRule.INSTANCE;
    }

    /**
     * Gets the rule for the day-of-week field in the Thai Buddhist chronology.
     *
     * @return the rule for the day-of-week field, never null
     */
    public static DateTimeFieldRule<DayOfWeek> dayOfWeekRule() {
        return DayOfWeekRule.INSTANCE;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the period unit for eras.
     * <p>
     * The period unit defines the concept of a period of an era.
     * This is equivalent to the ISO eras period unit.
     * <p>
     * See {@link #eraRule()} for the main date-time field.
     *
     * @return the period unit for eras, never null
     */
    public static PeriodUnit periodEras() {
        return ISOChronology.periodEras();
    }

    /**
     * Gets the period unit for years.
     * <p>
     * The period unit defines the concept of a period of a year.
     * This is equivalent to the ISO years period unit.
     * <p>
     * See {@link #yearOfEraRule()} for the main date-time field.
     *
     * @return the period unit for years, never null
     */
    public static PeriodUnit periodYears() {
        return ISOChronology.periodYears();
    }

    /**
     * Gets the period unit for months.
     * <p>
     * The period unit defines the concept of a period of a month.
     * This is equivalent to the ISO months period unit.
     * <p>
     * See {@link #monthOfYearRule()} for the main date-time field.
     *
     * @return the period unit for months, never null
     */
    public static PeriodUnit periodMonths() {
        return ISOChronology.periodMonths();
    }

    /**
     * Gets the period unit for weeks.
     * <p>
     * The period unit defines the concept of a period of a week.
     * This is equivalent to the ISO weeks period unit.
     *
     * @return the period unit for weeks, never null
     */
    public static PeriodUnit periodWeeks() {
        return ISOChronology.periodWeeks();
    }

    /**
     * Gets the period unit for days.
     * <p>
     * The period unit defines the concept of a period of a day.
     * This is equivalent to the ISO days period unit.
     * <p>
     * See {@link #dayOfMonthRule()} for the main date-time field.
     *
     * @return the period unit for days, never null
     */
    public static PeriodUnit periodDays() {
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
//            super(Integer.class, ThaiBuddhistChronology.INSTANCE, "Year", YEARS, null,
//                    -ThaiBuddhistDate.MAX_YEAR_OF_ERA + 1, ThaiBuddhistDate.MAX_YEAR_OF_ERA);
//        }
//        private Object readResolve() {
//            return INSTANCE;
//        }
//        @Override
//        public Integer getValueQuiet(LocalDate date, LocalTime time) {
//            return (date == null ? null : date.getYear() - YEAR_OFFSET);
//        }
//        @Override
//        protected void mergeDateTime(Calendrical.Merger merger) {
//            Integer moyVal = merger.getValueQuiet(ThaiBuddhistChronology.INSTANCE.monthOfYear());
//            Integer domVal = merger.getValueQuiet(ThaiBuddhistChronology.INSTANCE.dayOfMonth());
//            if (moyVal != null && domVal != null) {
//                int year = merger.getParsed(this);
//                int yearOfEra = Math.abs(year);
//                ThaiBuddhistEra era = (year < 1 ? ThaiBuddhistEra.BEFORE_BUDDHIST : ThaiBuddhistEra.BUDDHIST);
//                ThaiBuddhistDate date;
//                if (merger.isStrict()) {
//                    date = ThaiBuddhistDate.thaiBuddhistDate(era, yearOfEra, moyVal, domVal);
//                } else {
//                    date = ThaiBuddhistDate.thaiBuddhistDate(era, yearOfEra, 1, 1).plusMonths(moyVal)
//                            .plusMonths(-1).plusDays(domVal).plusDays(-1);
//                }
//                merger.storeMergedDate(date.toLocalDate());
//                merger.markFieldAsProcessed(this);
//                merger.markFieldAsProcessed(ThaiBuddhistChronology.INSTANCE.monthOfYear());
//                merger.markFieldAsProcessed(ThaiBuddhistChronology.INSTANCE.dayOfMonth());
//            }
//        }
//    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    private static final class EraRule extends DateTimeFieldRule<ThaiBuddhistEra> implements Serializable {
        /** Singleton instance. */
        private static final DateTimeFieldRule<ThaiBuddhistEra> INSTANCE = new EraRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private EraRule() {
            super(ThaiBuddhistEra.class, ThaiBuddhistChronology.INSTANCE, "Era", periodEras(), null, 0, 1);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        protected ThaiBuddhistEra derive(Calendrical calendrical) {
            ThaiBuddhistDate date = calendrical.get(ThaiBuddhistDate.rule());
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
            super(Integer.class, ThaiBuddhistChronology.INSTANCE, "YearOfEra", periodYears(), periodEras(),
                    ThaiBuddhistDate.MIN_YEAR_OF_ERA, ThaiBuddhistDate.MAX_YEAR_OF_ERA);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        // TODO: min/max years based on era
        @Override
        protected Integer derive(Calendrical calendrical) {
            ThaiBuddhistDate date = calendrical.get(ThaiBuddhistDate.rule());
            return date != null ? date.getYearOfEra() : null;
        }
        @Override
        protected void merge(CalendricalMerger merger) {
            ThaiBuddhistEra era = merger.getValue(ThaiBuddhistChronology.eraRule());
            era = (era != null ? era : ThaiBuddhistEra.BUDDHIST);
            Integer yoeVal = merger.getValue(this);
            // era, year, month, day-of-month
            MonthOfYear moy = merger.getValue(ThaiBuddhistChronology.monthOfYearRule());
            Integer domVal = merger.getValue(ThaiBuddhistChronology.dayOfMonthRule());
            if (moy != null && domVal != null) {
                ThaiBuddhistDate date = ThaiBuddhistDate.of(era, yoeVal, moy, domVal);
                merger.storeMerged(ThaiBuddhistDate.rule(), date);
                merger.removeProcessed(ThaiBuddhistChronology.eraRule());
                merger.removeProcessed(this);
                merger.removeProcessed(ThaiBuddhistChronology.monthOfYearRule());
                merger.removeProcessed(ThaiBuddhistChronology.dayOfMonthRule());
            }
            // era, year, day-of-year
            Integer doyVal = merger.getValue(ThaiBuddhistChronology.dayOfYearRule());
            if (doyVal != null) {
                ThaiBuddhistDate date = ThaiBuddhistDate.of(era, yoeVal, MonthOfYear.JANUARY, 1).plusDays(doyVal);
                merger.storeMerged(ThaiBuddhistDate.rule(), date);
                merger.removeProcessed(ThaiBuddhistChronology.eraRule());
                merger.removeProcessed(this);
                merger.removeProcessed(ThaiBuddhistChronology.yearOfEraRule());
                merger.removeProcessed(ThaiBuddhistChronology.dayOfYearRule());
            }
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    private static final class MonthOfYearRule extends DateTimeFieldRule<MonthOfYear> implements Serializable {
        /** Singleton instance. */
        private static final DateTimeFieldRule<MonthOfYear> INSTANCE = new MonthOfYearRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private MonthOfYearRule() {
            super(MonthOfYear.class, ThaiBuddhistChronology.INSTANCE, "MonthOfYear", periodMonths(), periodYears(), 1, 12);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        protected MonthOfYear derive(Calendrical calendrical) {
            ThaiBuddhistDate date = calendrical.get(ThaiBuddhistDate.rule());
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
            super(Integer.class, ThaiBuddhistChronology.INSTANCE, "DayOfMonth", periodDays(), periodMonths(), 1, 31);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        public int getSmallestMaximumValue() {
            return 28;
        }
        @Override
        public int getMaximumValue(Calendrical calendrical) {
            MonthOfYear moy = calendrical.get(ThaiBuddhistChronology.monthOfYearRule());
            if (moy != null) {
                ThaiBuddhistEra era = calendrical.get(ThaiBuddhistEra.rule());
                Integer yoeVal = calendrical.get(ThaiBuddhistChronology.yearOfEraRule());
                if (era != null && yoeVal != null) {
                    int isoYear = (era == ThaiBuddhistEra.BEFORE_BUDDHIST ? 1 - yoeVal : yoeVal) + YEAR_OFFSET;
                    return moy.lengthInDays(ISOChronology.isLeapYear(isoYear));
                } else {
                    return moy.maxLengthInDays();
                }
            }
            return getMaximumValue();
        }
        @Override
        protected Integer derive(Calendrical calendrical) {
            ThaiBuddhistDate date = calendrical.get(ThaiBuddhistDate.rule());
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
            super(Integer.class, ThaiBuddhistChronology.INSTANCE, "DayOfYear", periodDays(), periodYears(), 1, 366);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        public int getSmallestMaximumValue() {
            return 365;
        }
        @Override
        public int getMaximumValue(Calendrical calendrical) {
            ThaiBuddhistEra era = calendrical.get(ThaiBuddhistEra.rule());
            Integer yoeVal = calendrical.get(ThaiBuddhistChronology.yearOfEraRule());
            if (era != null && yoeVal != null) {
                int isoYear = (era == ThaiBuddhistEra.BEFORE_BUDDHIST ? 1 - yoeVal : yoeVal) + YEAR_OFFSET;
                Year year = Year.of(isoYear);
                return year.lengthInDays();
            }
            return getMaximumValue();
        }
        @Override
        protected Integer derive(Calendrical calendrical) {
            ThaiBuddhistDate date = calendrical.get(ThaiBuddhistDate.rule());
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
            super(DayOfWeek.class, ThaiBuddhistChronology.INSTANCE, "DayOfWeek", periodDays(), periodWeeks(), 1, 7);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        protected DayOfWeek derive(Calendrical calendrical) {
            ThaiBuddhistDate date = calendrical.get(ThaiBuddhistDate.rule());
            return date != null ? date.getDayOfWeek() : null;
        }
    }

}
