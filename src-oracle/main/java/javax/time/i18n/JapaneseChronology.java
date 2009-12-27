/*
 * Copyright (c) 2009 Oracle All Rights Reserved.
 */
package javax.time.i18n;

import static javax.time.period.PeriodUnits.DAYS;
import static javax.time.period.PeriodUnits.DECADES;
import static javax.time.period.PeriodUnits.MONTHS;
import static javax.time.period.PeriodUnits.WEEKS;
import static javax.time.period.PeriodUnits.YEARS;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;

import javax.time.calendar.Calendrical;
import javax.time.calendar.CalendricalMerger;
import javax.time.calendar.Chronology;
import javax.time.calendar.DateTimeFieldRule;
import javax.time.calendar.field.MonthOfYear;
import javax.time.calendar.field.Year;
import javax.time.calendar.format.DateTimeFormatterBuilder.TextStyle;

/**
 * The Japanese Imperial calendar system.
 * <p>
 * JapaneseChronology defines the rules of the Japanese Imperial calendar system.
 * Only Keio (1865-04-07 - 1868-09-07) and later eras are supported.
 * Older eras are recognized as unknown era, and the year of era of
 * unknown era is Gregorian year.
 * <p>
 * JapaneseChronology is thread-safe and immutable.
 *
 * @author Ryoji Suzuki
 * @author Stephen Colebourne
 */
public final class JapaneseChronology extends Chronology implements Serializable {
    // TODO: Base of GregJulian? Or is ISO sufficient?

    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = -4760300484384995747L;

    /**
     * The singleton instance of <code>JapaneseChronology</code>.
     */
    public static final JapaneseChronology INSTANCE = new JapaneseChronology();

    /**
     * Containing the offset from the ISO year.
     */
    static final int YEAR_OFFSET = JapaneseEra.SHOWA.getYearOffset();

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
    private static final String TARGET_LANGUAGE = "ja";

    /**
     * Name data.
     */
    static {
        ERA_NARROW_NAMES.put(FALLBACK_LANGUAGE, new String[]{"Unknown", "K", "M", "T", "S", "H"});
        ERA_NARROW_NAMES.put(TARGET_LANGUAGE, new String[]{"Unknown", "K", "M", "T", "S", "H"});
        ERA_SHORT_NAMES.put(FALLBACK_LANGUAGE, new String[]{"Unknown", "K", "M", "T", "S", "H"});
        ERA_SHORT_NAMES.put(TARGET_LANGUAGE, new String[]{"Unknown", "\u6176", "\u660e", "\u5927", "\u662d", "\u5e73"});
        ERA_FULL_NAMES.put(FALLBACK_LANGUAGE, new String[]{"Unknown", "Keio", "Meiji", "Taisho", "Showa", "Heisei"});
        ERA_FULL_NAMES.put(TARGET_LANGUAGE,
                new String[]{"Unknown", "\u6176\u5fdc", "\u660e\u6cbb", "\u5927\u6b63", "\u662d\u548c", "\u5e73\u6210"});
    }

    //-----------------------------------------------------------------------
    /**
     * Restrictive constructor.
     */
    private JapaneseChronology() {
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
        return "Japanese";
    }

    //-----------------------------------------------------------------------
//    /**
//     * Gets the rule for the year field in the Japanese chronology.
//     * <p>
//     * The values for the year field match those for year of SHOWA era that contains 1970-01-01.
//     * For the previous era, TAISHO, the values run backwards and include zero, thus
//     * TAISHO 14 has the value 0, and TAISHO 13 has the value -1.
//     *
//     * @return the rule for the year field, never null
//     */
//    public static DateTimeFieldRule<Integer> yearRule() {
//        return YearRule.INSTANCE;
//    }

    /**
     * Gets the rule for the era field in the Japanese chronology.
     *
     * @return the rule for the year field, never null
     */
    public static DateTimeFieldRule<JapaneseEra> eraRule() {
        return EraRule.INSTANCE;
    }

    /**
     * Gets the rule for the year of era field in the Japanese chronology.
     *
     * @return the rule for the year of era field, never null
     */
    public static DateTimeFieldRule<Integer> yearOfEraRule() {
        return YearOfEraRule.INSTANCE;
    }

    /**
     * Gets the rule for the month of year field in the Japanese chronology.
     *
     * @return the rule for the month of year field, never null
     */
    public static DateTimeFieldRule<Integer> monthOfYearRule() {
        return MonthOfYearRule.INSTANCE;
    }

    /**
     * Gets the rule for the day of month field in the Japanese chronology.
     *
     * @return the rule for the day of month field, never null
     */
    public static DateTimeFieldRule<Integer> dayOfMonthRule() {
        return DayOfMonthRule.INSTANCE;
    }

    /**
     * Gets the rule for the day of year field in the Japanese chronology.
     *
     * @return the rule for the day of year field, never null
     */
    public static DateTimeFieldRule<Integer> dayOfYearRule() {
        return DayOfYearRule.INSTANCE;
    }

    /**
     * Gets the rule for the day of week field in the Japanese chronology.
     *
     * @return the rule for the day of week field, never null
     */
    public static DateTimeFieldRule<Integer> dayOfWeekRule() {
        return DayOfWeekRule.INSTANCE;
    }

    //-----------------------------------------------------------------------
//    /**
//     * Rule implementation.
//     */
//    private static final class YearRule extends DateTimeFieldRule<Integer> implements
//            Serializable {
//        /** Singleton instance. */
//        private static final DateTimeFieldRule<Integer> INSTANCE = new YearRule();
//        /** A serialization identifier for this class. */
//        private static final long serialVersionUID = 1L;
//        /** Constructor. */
//        private YearRule() {
//            super(Integer.class, JapaneseChronology.INSTANCE, "Year", YEARS,
//                    null, -JapaneseDate.MAX_YEAR_OF_ERA + 1, JapaneseDate.MAX_YEAR_OF_ERA);
//        }
//        private Object readResolve() {
//            return INSTANCE;
//        }
//        @Override
//        protected Integer deriveValue(Calendrical calendrical) {
//            JapaneseDate jd = calendrical.get(JapaneseDate.rule());
//            return jd != null ? jd.getYear() - YEAR_OFFSET : null;
//        }
//        @Override
//        protected void merge(CalendricalMerger merger) {
//            Integer moyVal = merger.getValue(JapaneseChronology.monthOfYearRule());
//            Integer domVal = merger.getValue(JapaneseChronology.dayOfMonthRule());
//            if (moyVal != null && domVal != null) {
//                int yearVal = merger.getValue(this);
//                int year = yearVal + YEAR_OFFSET;
//                JapaneseEra era = JapaneseEra.japaneseEra(LocalDate.date(year, moyVal, domVal));
//                int yearOfEra = year - era.getYearOffset();
//                JapaneseDate date;
//                if (merger.isStrict()) {
//                    date = JapaneseDate.japaneseDate(era, yearOfEra, moyVal, domVal);
//                } else {
//                    date = JapaneseDate.japaneseDate(era, yearOfEra, 1, 1)
//                            .plusMonths(moyVal).plusMonths(-1).plusDays(domVal)
//                            .plusDays(-1);
//                }
//                merger.storeMerged(date.toLocalDate());
//                merger.removeProcessed(this);
//                merger.removeProcessed(JapaneseChronology.monthOfYearRule());
//                merger.removeProcessed(JapaneseChronology.dayOfMonthRule());
//            }
//        }
//    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    private static final class EraRule extends DateTimeFieldRule<JapaneseEra> implements Serializable {
        /** Singleton instance. */
        private static final DateTimeFieldRule<JapaneseEra> INSTANCE = new EraRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private EraRule() {
            // Use DECADES for now as there is no ERAS defined.
            super(JapaneseEra.class, JapaneseChronology.INSTANCE, "Era", DECADES, null, -3, 2);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        protected JapaneseEra deriveValue(Calendrical calendrical) {
            JapaneseDate date = calendrical.get(JapaneseDate.rule());
            return date != null ? date.getEra() : null;
        }
        @Override
        protected void merge(CalendricalMerger merger) {
            Integer yoeVal = merger.getValue(JapaneseChronology.yearOfEraRule());
            if (yoeVal != null) {
                // era, year, month, day-of-month
                JapaneseEra era = merger.getValue(this);
                Integer moyVal = merger.getValue(JapaneseChronology.monthOfYearRule());
                Integer domVal = merger.getValue(JapaneseChronology.dayOfMonthRule());
                if (moyVal != null && domVal != null) {
                    JapaneseDate date = JapaneseDate.japaneseDate(era, yoeVal, moyVal, domVal);
                    merger.storeMerged(JapaneseDate.rule(), date);
                    merger.removeProcessed(this);
                    merger.removeProcessed(JapaneseChronology.yearOfEraRule());
                    merger.removeProcessed(JapaneseChronology.monthOfYearRule());
                    merger.removeProcessed(JapaneseChronology.dayOfMonthRule());
                }
                // era, year, day-of-year
                Integer doyVal = merger.getValue(JapaneseChronology.dayOfYearRule());
                if (doyVal != null) {
                    JapaneseDate date = JapaneseDate.japaneseDate(era, yoeVal, 1, 1).plusDays(doyVal);
                    merger.storeMerged(JapaneseDate.rule(), date);
                    merger.removeProcessed(this);
                    merger.removeProcessed(JapaneseChronology.yearOfEraRule());
                    merger.removeProcessed(JapaneseChronology.dayOfYearRule());
                }
            }
        }
        @Override
        public String getText(int value, Locale locale, TextStyle textStyle) {
            value = value + 3; // -3 is the min value.
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
            super(Integer.class, JapaneseChronology.INSTANCE, "YearOfEra", YEARS, null,
                    JapaneseDate.MIN_YEAR_OF_ERA, JapaneseDate.MAX_YEAR_OF_ERA);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        // TODO: min/max years based on era
        @Override
        protected Integer deriveValue(Calendrical calendrical) {
            JapaneseDate date = calendrical.get(JapaneseDate.rule());
            return date != null ? date.getYearOfEra() : null;
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
            super(Integer.class, JapaneseChronology.INSTANCE, "MonthOfYear", MONTHS, YEARS, 1, 12);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        protected Integer deriveValue(Calendrical calendrical) {
            JapaneseDate date = calendrical.get(JapaneseDate.rule());
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
            super(Integer.class, JapaneseChronology.INSTANCE, "DayOfMonth", DAYS, MONTHS, 1, 31);
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
            JapaneseEra era = calendrical.get(JapaneseEra.rule());
            Integer yoeVal = calendrical.get(JapaneseChronology.yearOfEraRule());
            Integer moyval = calendrical.get(JapaneseChronology.monthOfYearRule());
            if (era != null && yoeVal != null && moyval != null) {
                int isoYear = era.getYearOffset() + yoeVal;
                MonthOfYear month = MonthOfYear.monthOfYear(moyval);
                return month.lengthInDays(isoYear);
            }
            return getMaximumValue();
        }
        @Override
        protected Integer deriveValue(Calendrical calendrical) {
            JapaneseDate date = calendrical.get(JapaneseDate.rule());
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
            super(Integer.class, JapaneseChronology.INSTANCE, "DayOfYear", DAYS, YEARS, 1, 366);
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
            JapaneseEra era = calendrical.get(JapaneseEra.rule());
            Integer yoeVal = calendrical.get(JapaneseChronology.yearOfEraRule());
            if (era != null && yoeVal != null) {
                int isoYear = era.getYearOffset() + yoeVal;
                Year year = Year.isoYear(isoYear);
                return year.lengthInDays();
            }
            return getMaximumValue();
        }
        @Override
        protected Integer deriveValue(Calendrical calendrical) {
            JapaneseDate date = calendrical.get(JapaneseDate.rule());
            return date != null ? date.getDayOfYear() : null;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    private static final class DayOfWeekRule extends DateTimeFieldRule<Integer> implements Serializable {
        /** Singleton instance. */
        private static final DateTimeFieldRule<Integer> INSTANCE = new DayOfWeekRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private DayOfWeekRule() {
            super(Integer.class, JapaneseChronology.INSTANCE, "DayOfWeek", DAYS, WEEKS, 1, 7);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        protected Integer deriveValue(Calendrical calendrical) {
            JapaneseDate date = calendrical.get(JapaneseDate.rule());
            return date != null ? date.getDayOfWeek() : null;
        }
    }

}
