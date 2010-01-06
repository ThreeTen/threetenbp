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
import javax.time.calendar.ISOChronology;
import javax.time.calendar.PeriodRule;
import javax.time.calendar.field.DayOfWeek;
import javax.time.calendar.field.MonthOfYear;
import javax.time.calendar.field.Year;
import javax.time.calendar.format.DateTimeFormatterBuilder.TextStyle;

/**
 * The Minguo calendar system.
 * <p>
 * MinguoChronology defines the rules of the Minguo calendar system.
 * <p>
 * MinguoChronology is thread-safe and immutable.
 *
 * @author Ryoji Suzuki
 * @author Stephen Colebourne
 */
public final class MinguoChronology extends Chronology implements Serializable {

    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = 5856454970865881985L;

    /**
     * The singleton instance of <code>MinguoChronology</code>.
     */
    public static final MinguoChronology INSTANCE = new MinguoChronology();

    /**
     * Containing the offset from the ISO year.
     */
    static final int YEAR_OFFSET = 1911;

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
    private static final String TARGET_LANGUAGE = "zh";

    /**
     * Name data.
     */
    static {
        ERA_NARROW_NAMES.put(FALLBACK_LANGUAGE, new String[]{"BM", "AM"});
        ERA_NARROW_NAMES.put(TARGET_LANGUAGE, new String[]{"\u6c11\u570b", "\u6c11\u524d"});
        ERA_SHORT_NAMES.put(FALLBACK_LANGUAGE, new String[]{"B.M.", "A.M."});
        ERA_SHORT_NAMES.put(TARGET_LANGUAGE, new String[]{"\u6c11\u570b", "\u6c11\u524d"});
        ERA_FULL_NAMES.put(FALLBACK_LANGUAGE, new String[]{"Before Minguo", "Minguo"});
        ERA_FULL_NAMES.put(TARGET_LANGUAGE, new String[]{"\u4e2d\u83ef\u6c11\u570b", "\u6c11\u570b\u524d"});
    }

    //-----------------------------------------------------------------------
    /**
     * Restrictive constructor.
     */
    private MinguoChronology() {
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
        return "Minguo";
    }

    //-----------------------------------------------------------------------
//    /**
//     * Gets the rule for the year field in the Minguo chronology.
//     * <p>
//     * The values for the year field match those for year of era in the current era.
//     * For the previous era, the values run backwards and include zero, thus
//     * BEFORE_MINGUO 1 has the value 0, and BEFORE_MINGUO 2 has the value -1.
//     *
//     * @return the rule for the year field, never null
//     */
//    public static DateTimeFieldRule<Integer> yearRule() {
//        return YearRule.INSTANCE;
//    }

    /**
     * Gets the rule for the era field in the Minguo chronology.
     *
     * @return the rule for the year field, never null
     */
    public static DateTimeFieldRule<MinguoEra> eraRule() {
        return EraRule.INSTANCE;
    }

    /**
     * Gets the rule for the year of era field in the Minguo chronology.
     *
     * @return the rule for the year of era field, never null
     */
    public static DateTimeFieldRule<Integer> yearOfEraRule() {
        return YearOfEraRule.INSTANCE;
    }

    /**
     * Gets the rule for the month of year field in the Minguo chronology.
     *
     * @return the rule for the month of year field, never null
     */
    public static DateTimeFieldRule<Integer> monthOfYearRule() {
        return MonthOfYearRule.INSTANCE;
    }

    /**
     * Gets the rule for the day of month field in the Minguo chronology.
     *
     * @return the rule for the day of month field, never null
     */
    public static DateTimeFieldRule<Integer> dayOfMonthRule() {
        return DayOfMonthRule.INSTANCE;
    }

    /**
     * Gets the rule for the day of year field in the Minguo chronology.
     *
     * @return the rule for the day of year field, never null
     */
    public static DateTimeFieldRule<Integer> dayOfYearRule() {
        return DayOfYearRule.INSTANCE;
    }

    /**
     * Gets the rule for the day of week field in the Minguo chronology.
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
     * This is equivalent to the ISO years period rule.
     * <p>
     * See {@link #yearOfEraRule()} for the main date-time field.
     *
     * @return the period rule for years, never null
     */
    public static PeriodRule periodYears() {
        return ISOChronology.periodYears();
    }

    /**
     * Gets the period rule for months.
     * <p>
     * The period rule defines the concept of a period of a month.
     * This is equivalent to the ISO months period rule.
     * <p>
     * See {@link #monthOfYearRule()} for the main date-time field.
     *
     * @return the period rule for months, never null
     */
    public static PeriodRule periodMonths() {
        return ISOChronology.periodMonths();
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
//            super(Integer.class, MinguoChronology.INSTANCE, "Year", YEARS, null,
//                    -MinguoDate.MAX_YEAR_OF_ERA + 1, MinguoDate.MAX_YEAR_OF_ERA);
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
//            Integer moyVal = merger.getValueQuiet(MinguoChronology.INSTANCE.monthOfYear());
//            Integer domVal = merger.getValueQuiet(MinguoChronology.INSTANCE.dayOfMonth());
//            if (moyVal != null && domVal != null) {
//                int year = merger.getParsed(this);
//                int yearOfEra = Math.abs(year);
//                MinguoEra era = (year < 1 ? MinguoEra.BEFORE_MINGUO : MinguoEra.MINGUO);
//                MinguoDate date;
//                if (merger.isStrict()) {
//                    date = MinguoDate.minguoDate(era, yearOfEra, moyVal, domVal);
//                } else {
//                    date = MinguoDate.minguoDate(era, yearOfEra, 1, 1).plusMonths(moyVal)
//                            .plusMonths(-1).plusDays(domVal).plusDays(-1);
//                }
//                merger.storeMergedDate(date.toLocalDate());
//                merger.markFieldAsProcessed(this);
//                merger.markFieldAsProcessed(MinguoChronology.INSTANCE.monthOfYear());
//                merger.markFieldAsProcessed(MinguoChronology.INSTANCE.dayOfMonth());
//            }
//        }
//    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    private static final class EraRule extends DateTimeFieldRule<MinguoEra> implements Serializable {
        /** Singleton instance. */
        private static final DateTimeFieldRule<MinguoEra> INSTANCE = new EraRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private EraRule() {
            super(MinguoEra.class, MinguoChronology.INSTANCE, "Era", periodEras(), null, 0, 1);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        protected MinguoEra derive(Calendrical calendrical) {
            MinguoDate date = calendrical.get(MinguoDate.rule());
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
            super(Integer.class, MinguoChronology.INSTANCE, "YearOfEra", periodYears(), periodEras(),
                    MinguoDate.MIN_YEAR_OF_ERA, MinguoDate.MAX_YEAR_OF_ERA);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        // TODO: min/max years based on era
        @Override
        protected Integer derive(Calendrical calendrical) {
            MinguoDate date = calendrical.get(MinguoDate.rule());
            return date != null ? date.getYearOfEra() : null;
        }
        @Override
        protected void merge(CalendricalMerger merger) {
            MinguoEra era = merger.getValue(MinguoChronology.eraRule());
            era = (era != null ? era : MinguoEra.MINGUO);
            Integer yoeVal = merger.getValue(this);
            // era, year, month, day-of-month
            Integer moyVal = merger.getValue(MinguoChronology.monthOfYearRule());
            Integer domVal = merger.getValue(MinguoChronology.dayOfMonthRule());
            if (moyVal != null && domVal != null) {
                MinguoDate date = MinguoDate.minguoDate(era, yoeVal, moyVal, domVal);
                merger.storeMerged(MinguoDate.rule(), date);
                merger.removeProcessed(MinguoChronology.eraRule());
                merger.removeProcessed(this);
                merger.removeProcessed(MinguoChronology.monthOfYearRule());
                merger.removeProcessed(MinguoChronology.dayOfMonthRule());
            }
            // era, year, day-of-year
            Integer doyVal = merger.getValue(MinguoChronology.dayOfYearRule());
            if (doyVal != null) {
                MinguoDate date = MinguoDate.minguoDate(era, yoeVal, 1, 1).plusDays(doyVal);
                merger.storeMerged(MinguoDate.rule(), date);
                merger.removeProcessed(MinguoChronology.eraRule());
                merger.removeProcessed(this);
                merger.removeProcessed(MinguoChronology.yearOfEraRule());
                merger.removeProcessed(MinguoChronology.dayOfYearRule());
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
            super(Integer.class, MinguoChronology.INSTANCE, "MonthOfYear", periodMonths(), periodYears(), 1, 12);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        protected Integer derive(Calendrical calendrical) {
            MinguoDate date = calendrical.get(MinguoDate.rule());
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
            super(Integer.class, MinguoChronology.INSTANCE, "DayOfMonth", periodDays(), periodMonths(), 1, 31);
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
            MinguoEra era = calendrical.get(MinguoEra.rule());
            Integer yoeVal = calendrical.get(MinguoChronology.yearOfEraRule());
            Integer moyval = calendrical.get(MinguoChronology.monthOfYearRule());
            if (era != null && yoeVal != null && moyval != null) {
                int isoYear = (era == MinguoEra.BEFORE_MINGUO ? 1 - yoeVal : yoeVal) + YEAR_OFFSET;
                MonthOfYear month = MonthOfYear.monthOfYear(moyval);
                return month.lengthInDays(ISOChronology.isLeapYear(isoYear));
            }
            return getMaximumValue();
        }
        @Override
        protected Integer derive(Calendrical calendrical) {
            MinguoDate date = calendrical.get(MinguoDate.rule());
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
            super(Integer.class, MinguoChronology.INSTANCE, "DayOfYear", periodDays(), periodYears(), 1, 366);
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
            MinguoEra era = calendrical.get(MinguoEra.rule());
            Integer yoeVal = calendrical.get(MinguoChronology.yearOfEraRule());
            if (era != null && yoeVal != null) {
                int isoYear = (era == MinguoEra.BEFORE_MINGUO ? 1 - yoeVal : yoeVal) + YEAR_OFFSET;
                Year year = Year.isoYear(isoYear);
                return year.lengthInDays();
            }
            return getMaximumValue();
        }
        @Override
        protected Integer derive(Calendrical calendrical) {
            MinguoDate date = calendrical.get(MinguoDate.rule());
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
            super(DayOfWeek.class, MinguoChronology.INSTANCE, "DayOfWeek", periodDays(), periodWeeks(), 1, 7);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        protected DayOfWeek derive(Calendrical calendrical) {
            MinguoDate date = calendrical.get(MinguoDate.rule());
            return date != null ? date.getDayOfWeek() : null;
        }
    }

}
