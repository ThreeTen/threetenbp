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
import javax.time.calendar.DateTimeField;
import javax.time.calendar.DateTimeRule;
import javax.time.calendar.DateTimeRuleRange;
import javax.time.calendar.ISOChronology;
import javax.time.calendar.ISOPeriodUnit;
import javax.time.calendar.MonthOfYear;
import javax.time.calendar.PeriodUnit;
import javax.time.calendar.format.DateTimeFormatterBuilder.TextStyle;

/**
 * The Japanese Imperial calendar system.
 * <p>
 * {@code JapaneseChronology} defines the rules of the Japanese Imperial calendar system.
 * Only Keio (1865-04-07 - 1868-09-07) and later eras are supported.
 * Older eras are recognized as unknown era, and the year of era of
 * unknown era is Gregorian year.
 * <p>
 * JapaneseChronology is immutable and thread-safe.
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
     * The singleton instance of {@code JapaneseChronology}.
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
//    public static DateTimeFieldRule yearRule() {
//        return YearRule.INSTANCE;
//    }

    /**
     * Gets the rule for the era field in the Japanese chronology.
     *
     * @return the rule for the year field, never null
     */
    public static DateTimeRule eraRule() {
        return EraRule.INSTANCE;
    }

    /**
     * Gets the rule for the year of era field in the Japanese chronology.
     *
     * @return the rule for the year of era field, never null
     */
    public static DateTimeRule yearOfEraRule() {
        return YearOfEraRule.INSTANCE;
    }

    /**
     * Gets the rule for the month-of-year field in the Japanese chronology.
     *
     * @return the rule for the month-of-year field, never null
     */
    public static DateTimeRule monthOfYearRule() {
        return MonthOfYearRule.INSTANCE;
    }

    /**
     * Gets the rule for the day-of-month field in the Japanese chronology.
     *
     * @return the rule for the day-of-month field, never null
     */
    public static DateTimeRule dayOfMonthRule() {
        return DayOfMonthRule.INSTANCE;
    }

    /**
     * Gets the rule for the day-of-year field in the Japanese chronology.
     *
     * @return the rule for the day-of-year field, never null
     */
    public static DateTimeRule dayOfYearRule() {
        return DayOfYearRule.INSTANCE;
    }

    /**
     * Gets the rule for the day-of-week field in the Japanese chronology.
     *
     * @return the rule for the day-of-week field, never null
     */
    public static DateTimeRule dayOfWeekRule() {
        return DayOfWeekRule.INSTANCE;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the period rule for eras.
     * <p>
     * The period rule defines the concept of a period of an era.
     * The Japanese era is of variable length, so for the purpose of an estimated
     * duration this rule is equal to 40 years.
     * <p>
     * See {@link #eraRule()} for the main date-time field.
     *
     * @return the period rule for eras, never null
     */
    public static PeriodUnit periodEras() {
        return ERAS;
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
        return ISOPeriodUnit.YEARS;
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
        return ISOPeriodUnit.MONTHS;
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
        return ISOPeriodUnit.WEEKS;
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
        return ISOPeriodUnit.DAYS;
    }

    //-----------------------------------------------------------------------
//    /**
//     * Rule implementation.
//     */
//    private static final class YearRule extends DateTimeFieldRule implements
//            Serializable {
//        /** Singleton instance. */
//        private static final DateTimeFieldRule INSTANCE = new YearRule();
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
    private static final class EraRule extends DateTimeRule implements Serializable {
        /** Singleton instance. */
        private static final DateTimeRule INSTANCE = new EraRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private EraRule() {
            super(JapaneseChronology.INSTANCE, "Era", periodEras(), null, -3, 2);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        protected DateTimeField derive(Calendrical calendrical) {
            JapaneseDate date = calendrical.get(JapaneseDate.rule());
            return date != null ? field(date.getEra().getValue()) : null;
        }
        @Override
        protected void merge(CalendricalMerger merger) {
            DateTimeField yoeVal = merger.getValue(JapaneseChronology.yearOfEraRule());
            if (yoeVal != null) {
                // era, year, month, day-of-month
                JapaneseEra era = JapaneseEra.of(merger.getValue(this).getValidIntValue());
                DateTimeField moyVal = merger.getValue(JapaneseChronology.monthOfYearRule());
                DateTimeField domVal = merger.getValue(JapaneseChronology.dayOfMonthRule());
                if (moyVal != null && domVal != null) {
                    JapaneseDate date = JapaneseDate.of(era, yoeVal.getValidIntValue(), MonthOfYear.of(moyVal.getValidIntValue()), domVal.getValidIntValue());
                    merger.storeMerged(JapaneseDate.rule(), date);
                    merger.removeProcessed(this);
                    merger.removeProcessed(JapaneseChronology.yearOfEraRule());
                    merger.removeProcessed(JapaneseChronology.monthOfYearRule());
                    merger.removeProcessed(JapaneseChronology.dayOfMonthRule());
                }
                // era, year, day-of-year
                DateTimeField doyVal = merger.getValue(JapaneseChronology.dayOfYearRule());
                if (doyVal != null) {
                    JapaneseDate date = JapaneseDate.of(era, yoeVal.getValidIntValue(), MonthOfYear.JANUARY, 1).plusDays(doyVal.getValidIntValue() - 1);
                    merger.storeMerged(JapaneseDate.rule(), date);
                    merger.removeProcessed(this);
                    merger.removeProcessed(JapaneseChronology.yearOfEraRule());
                    merger.removeProcessed(JapaneseChronology.dayOfYearRule());
                }
            }
        }
        // TODO: never worked properly, needs to use proper provider
        @Override
        public String getText(long value, TextStyle textStyle, Locale locale) {
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
            return names == null ? Long.toString(value) : names[(int) value];
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    private static final class YearOfEraRule extends DateTimeRule implements Serializable {
        /** Singleton instance. */
        private static final DateTimeRule INSTANCE = new YearOfEraRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private YearOfEraRule() {
            super(JapaneseChronology.INSTANCE, "YearOfEra", periodYears(), periodEras(),
                    JapaneseDate.MIN_YEAR_OF_ERA, JapaneseDate.MAX_YEAR_OF_ERA);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        // TODO: min/max years based on era
        @Override
        protected DateTimeField derive(Calendrical calendrical) {
            JapaneseDate date = calendrical.get(JapaneseDate.rule());
            return date != null ? field(date.getYearOfEra()) : null;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    private static final class MonthOfYearRule extends DateTimeRule implements Serializable {
        /** Singleton instance. */
        private static final DateTimeRule INSTANCE = new MonthOfYearRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private MonthOfYearRule() {
            super(JapaneseChronology.INSTANCE, "MonthOfYear", periodMonths(), periodYears(), 1, 12);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        protected DateTimeField derive(Calendrical calendrical) {
            JapaneseDate date = calendrical.get(JapaneseDate.rule());
            return date != null ? field(date.getMonthOfYear().getValue()) : null;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    private static final class DayOfMonthRule extends DateTimeRule implements Serializable {
        /** Singleton instance. */
        private static final DateTimeRule INSTANCE = new DayOfMonthRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private DayOfMonthRule() {
            super(JapaneseChronology.INSTANCE, "DayOfMonth", periodDays(), periodMonths(), DateTimeRuleRange.of(1, 28, 31));
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        public DateTimeRuleRange getRange(Calendrical calendrical) {
            DateTimeField moyVal = calendrical.get(JapaneseChronology.monthOfYearRule());
            if (moyVal != null) {
                MonthOfYear moy = MonthOfYear.of(moyVal.getValidIntValue());
                if (moy == MonthOfYear.FEBRUARY) {
                    DateTimeField eraVal = calendrical.get(JapaneseEra.rule());
                    DateTimeField yoeVal = calendrical.get(JapaneseChronology.yearOfEraRule());
                    if (eraVal != null && yoeVal != null) {
                        JapaneseEra era = JapaneseEra.of(eraVal.getValidIntValue());
                        int isoYear = era.getYearOffset() + yoeVal.getValidIntValue();
                        return DateTimeRuleRange.of(1, moy.lengthInDays(ISOChronology.isLeapYear(isoYear)));
                    }
                    return DateTimeRuleRange.of(1, 28, 29);
                } else {
                    return DateTimeRuleRange.of(1, moy.maxLengthInDays());
                }
            }
            return getRange();
        }
        @Override
        protected DateTimeField derive(Calendrical calendrical) {
            JapaneseDate date = calendrical.get(JapaneseDate.rule());
            return date != null ? field(date.getDayOfMonth()) : null;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    private static final class DayOfYearRule extends DateTimeRule implements Serializable {
        /** Singleton instance. */
        private static final DateTimeRule INSTANCE = new DayOfYearRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private DayOfYearRule() {
            super(JapaneseChronology.INSTANCE, "DayOfYear", periodDays(), periodYears(), DateTimeRuleRange.of(1, 365, 366));
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        public DateTimeRuleRange getRange(Calendrical calendrical) {
            DateTimeField eraVal = calendrical.get(JapaneseEra.rule());
            DateTimeField yoeVal = calendrical.get(JapaneseChronology.yearOfEraRule());
            if (eraVal != null && yoeVal != null) {
                JapaneseEra era = JapaneseEra.of(eraVal.getValidIntValue());
                int isoYear = era.getYearOffset() + yoeVal.getValidIntValue();
                return DateTimeRuleRange.of(1, ISOChronology.isLeapYear(isoYear) ? 366 : 365);
            }
            return getRange();
        }
        @Override
        protected DateTimeField derive(Calendrical calendrical) {
            JapaneseDate date = calendrical.get(JapaneseDate.rule());
            return date != null ? field(date.getDayOfYear()) : null;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    private static final class DayOfWeekRule extends DateTimeRule implements Serializable {
        /** Singleton instance. */
        private static final DateTimeRule INSTANCE = new DayOfWeekRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private DayOfWeekRule() {
            super(JapaneseChronology.INSTANCE, "DayOfWeek", periodDays(), periodWeeks(), 1, 7);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        protected DateTimeField derive(Calendrical calendrical) {
            JapaneseDate date = calendrical.get(JapaneseDate.rule());
            return date != null ? field(date.getDayOfWeek().getValue()) : null;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Period unit for eras.
     */
    private static final PeriodUnit ERAS = new Eras();
    /**
     * Unit class for eras.
     */
    private static final class Eras extends PeriodUnit {
        private static final long serialVersionUID = 1L;
        private Eras() {
            super("JapaneseEras", Duration.ofSeconds(31556952L * 40L));
        }
        private Object readResolve() {
            return ERAS;
        }
    }

}
